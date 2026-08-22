let categories = null;
let pData = null;

window.addEventListener("load", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        categories = await loadCategories();
        pData = await loadProductSpecification();
        await sortProducts();
        // showFilteredItems(categories, data);
    }finally {
        Notiflix.Loading.remove(500);
    }
})

///LOAD SORTING DETAILS START
async function loadCategories(){
    try{
        const response = await fetch("api/data/shop-load-categories");
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            renderCategoryTree(data.categories);
            return data.categories;
        }else{
            notify("Category Loading Failed!");
            return null;
        }
    }catch (e) {
        notify(e.message);
        return null;
    }
}
function renderCategoryTree(categories) {
    const treeContainer = $("#categoryTree");
    treeContainer.empty(); // clear existing tree

    categories.forEach(cat => {
        const li = $("<li>").addClass("closed");
        const a = $("<a>")
            .attr("href", "javascript:void(0)")
            .text(`${cat.name} (${cat.subCategories.length})`)
            .click(() => sortProducts(cat.id, 0, null, null, null)); // keep your sort function
        li.append(a);

        const ul = $("<ul>");
        cat.subCategories.forEach(sub => {
            const subLi = $("<li>");
            const subA = $("<a>")
                .attr("href", "javascript:void(0)")
                .text(sub.name)
                .click(() => sortProducts(0, sub.id, null, null, null));
            subLi.append(subA);
            ul.append(subLi);
        });

        li.append(ul);
        treeContainer.append(li);
    });

    // Re-initialize Treeview plugin on the updated container
    treeContainer.treeview({
        collapsed: true,   // collapse all branches initially
        animated: "fast",
        persist: "location" // optional: remembers open/closed state
    });
}

async function loadProductSpecification(){
    try {
        const response = await fetch("api/data/specifications");
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            initPriceSlider(data.minPrice, data.maxPrice);
            initColorSort(data.colors);
            initSizeSort(data.sizes);
            initBrandSort(data.brands);

            return data;
        }else{
            notify(e.message);
            return null;
        }
    }catch (e){
        notify(e.message);
        return null;
    }
}
function initPriceSlider(minPrice, maxPrice) {

    $( "#slider-range" ).slider({
        range: true,
        min: minPrice,
        max: maxPrice,
        values: [ minPrice, maxPrice ],  // default bounds
        slide: function( event, ui ) {
            $( "#amount" ).val("Rs. " + ui.values[0] + " - Rs. " + ui.values[1]);
        }
    });

    $( "#amount" ).val(
        "Rs. " + minPrice + " - Rs. " + maxPrice
    );
}
function initColorSort(colors) {
    const container = document.getElementById("colorContainer");
    container.innerHTML = "";

    colors.forEach(c => {
        const li = document.createElement("li");
        li.classList.add("border", "border-2", "border-dark");

        li.innerHTML = `
            <a href="javascript:void(0)" 
               style="background-color: ${c.code};" 
               onclick="sortProducts(null, null, null, ${c.id}, null)">
            </a>
        `;

        container.appendChild(li);
    });
}
function initSizeSort(sizes) {
    const container1 = document.getElementById("sizeContainer");
    const container2 = document.getElementById("sizeContainer2");

    container1.innerHTML = "";
    container2.innerHTML = "";

    sizes.forEach((s, index) => {
        const li = document.createElement("li");

        li.innerHTML = `
            <a href="javascript:void(0)" onclick="sortProducts(null, null, null, null, ${s.id})">${s.value}</a>
        `;

        if (index < 5) {
            container1.appendChild(li);   // First 5 → first row
        } else {
            container2.appendChild(li);   // Rest → second row
        }
    });
}
function initBrandSort(brands) {
    const container1 = document.getElementById("brandContainer");

    container1.innerHTML = "";

    brands.forEach((b, index) => {
        const li = document.createElement("li");

        li.innerHTML = `
            <a href="javascript:void(0)" onclick="sortProducts(null, null, ${b.id}, null, null)">${b.name}</a>
        `;

       container1.appendChild(li);
    });
}
///LOAD SORTING DETAILS START


let currentPage = 1;
let activeCategory = null;
let activeSubCategory = null;
let activeBrand = null;
let activeColor = null;
let activeSize = null;

async function sortProducts(catId = null, subCatId = null, brandId = null, colorId = null, sizeId = null, page = 1) {
    // update global active filters when a non-null value is passed
    if (catId !== null) activeCategory = catId;
    if (subCatId !== null) activeSubCategory = subCatId;
    if (brandId !== null) activeBrand = brandId;
    if (colorId !== null) activeColor = colorId;
    if (sizeId !== null) activeSize = sizeId;
    currentPage = page || 1;

    // read UI safely (elements may be missing on some pages)
    const searchEl = document.getElementById("shopSearchField");
    const amountEl = document.getElementById("amount");           // "Rs. 800 - Rs. 1400" or "800 - 1400"
    const perPageEl = document.getElementById("productPerPageSelect");
    const orderEl = document.getElementById("input-sort-select");

    const searchField = searchEl ? searchEl.value.trim() : "";
    const productPerPage = perPageEl ? parseInt(perPageEl.value, 10) || 15 : 15;
    const orderSelect = orderEl ? parseInt(orderEl.value, 10) || 5 : 5;

    // parse price range robustly - extract numbers
    let minPrice = null, maxPrice = null;
    if (amountEl && typeof amountEl.value === "string" && amountEl.value.trim() !== "") {
        // find all numbers (integers / floats)
        const nums = amountEl.value.replace(/,/g, "").match(/(\d+(\.\d+)?)/g);
        if (nums && nums.length >= 1) {
            minPrice = parseFloat(nums[0]);
            if (nums.length >= 2) maxPrice = parseFloat(nums[1]);
        }
    }

    // Build query params safely
    const params = new URLSearchParams();
    if (activeCategory) params.append("categoryId", activeCategory);
    if (activeSubCategory) params.append("subCategoryId", activeSubCategory);
    if (activeBrand) params.append("brandId", activeBrand);
    if (activeColor) params.append("colorId", activeColor);
    if (activeSize) params.append("sizeId", activeSize);
    if (searchField) params.append("search", searchField);
    if (minPrice !== null) params.append("minPrice", minPrice);
    if (maxPrice !== null) params.append("maxPrice", maxPrice);
    params.append("order", orderSelect);
    params.append("limit", productPerPage);
    params.append("page", currentPage);

    const url = `api/products/sort?${params.toString()}`;
    console.log("SORT URL:", url);

    try {
        Notiflix.Loading.pulse("Loading Products...");
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("Products Loading Failed!");
        }

        const data = await response.json();
        console.log(data)
        if (!data.status) {
            notify(data.message || "No products found");
            // clear container and pagination
            const c = document.getElementById("shopProductsContainer");
            if (c) c.innerHTML = "";
            generatePagination(0, productPerPage, 1);
            return;
        }

        document.getElementById("resultCount").innerHTML = data.totalProducts;
        showFilteredItems(categories, pData);
        // render product cards (you already have product-card custom element)
        renderProductCard(data);

        // build pagination
        const totalPages = data.totalPages || 1;
        generatePagination(totalPages, productPerPage, data.currentPage || currentPage);

    } catch (err) {
        console.error(err);
        notify(err.message || "Sorting failed!");
    } finally {
        Notiflix.Loading.remove();
    }
}

function showFilteredItems(categoryData, pData) {
    const container = document.getElementById("sortedFilterItemsContainers");
    if (!container) return;

    // Clear container first
    container.innerHTML = "";

    // Category Tag
    if (activeCategory != null && activeCategory > 0) {
        let cat = categoryData.find(x => x.id === activeCategory);
        if (cat) {
            container.innerHTML += `
                <li>
                    <a href="javascript:void(0)" style="display: flex; gap: 6px; align-items: center"
                       onclick="sortProducts(0, activeSubCategory, activeBrand, activeColor, activeSize)"><div style="height: 20px"></div>
                        ${cat.name} &nbsp; <i class="fa fa-times"></i>
                    </a>
                </li>
            `;
        }
    }

    // Subcategory Tag
    if (activeSubCategory != null && activeSubCategory > 0) {
        let catObj = categoryData.find(cat =>
            cat.subCategories?.some(sub => sub.id === activeSubCategory)
        );
        let subObj = catObj?.subCategories?.find(sub => sub.id === activeSubCategory);
        if (catObj && subObj) {
            container.innerHTML += `
                <li>
                    <a href="javascript:void(0)" style="display: flex; gap: 6px; align-items: center"
                       onclick="sortProducts(activeCategory, 0, activeBrand, activeColor, activeSize)"><div style="height: 20px"></div>
                        ${catObj.name} (${subObj.name}) &nbsp; <i class="fa fa-times"></i>
                    </a>
                </li>
            `;
        }
    }

    // Color Tag
    if (activeColor != null && activeColor > 0) {
        let colorObj = pData.colors.find(x => x.id === activeColor);
        if (colorObj) {
            container.innerHTML += `             
                <li>
                    <a href="javascript:void(0)" style="display: flex; gap: 6px; align-items: center"
                       onclick="sortProducts(activeCategory, activeSubCategory, activeBrand, 0, activeSize)">
                        <div style="width: 20px; height: 20px; background-color: ${colorObj.code}; border: 1px solid #ccc;"></div> &nbsp; <i class="fa fa-times"></i>
                    </a>
                </li>
            `;
        }
    }

    // Size Tag
    if (activeSize != null && activeSize > 0) {
        let sizeObj = pData.sizes.find(x => x.id === activeSize);
        if (sizeObj) {
            container.innerHTML += `
                <li>
                    <a href="javascript:void(0)" style="display: flex; gap: 6px; align-items: center"
                       onclick="sortProducts(activeCategory, activeSubCategory, activeBrand, activeColor, 0)"><div style="height: 20px"></div>
                        Size (${sizeObj.value}) &nbsp; <i class="fa fa-times"></i>
                    </a>
                </li>
            `;
        }
    }

    // Brand Tag
    if (activeBrand != null && activeBrand > 0) {
        let brandObj = pData.brands.find(x => x.id === activeBrand);
        if (brandObj) {
            container.innerHTML += `
            <li>
                <a href="javascript:void(0)" style="display: flex; gap: 6px; align-items: center"
                   onclick="sortProducts(activeCategory, activeSubCategory, 0, activeColor, activeSize)">
                    <div style="height: 20px"></div>
                    ${brandObj.name} &nbsp; <i class="fa fa-times"></i>
                </a>
            </li>
        `;
        }
    }

}
function renderProductCard(data) {
    const container = document.getElementById("shopProductsContainer");
    if (!container) {
        console.warn("shopProductsContainer not found.");
        return;
    }
    container.innerHTML = ""; // clear previous products

    (data.products || []).forEach(p => {
        // Create the wrapper div
        const wrapper = document.createElement("div");
        wrapper.className = "col-lg-4 col-md-6 item";

        // Create the product-card element
        const card = document.createElement("product-card");
        card.data = p;

        wrapper.appendChild(card);

        container.appendChild(wrapper);
    });
}
function generatePagination(totalPages = 1, perPage = 15, current = 1) {
    const wrapper = document.querySelector(".pagination-btn .page-numbers");
    if (!wrapper) return;

    current = current || 1;
    let html = '';

    // Prev
    const prev = Math.max(1, current - 1);
    html += `<li><a href="#" class="prev page-numbers" data-page="${prev}"><i class="zmdi zmdi-long-arrow-left"></i></a></li>`;

    // pages (windowed)
    const maxShow = 7;
    let start = Math.max(1, current - Math.floor(maxShow / 2));
    let end = Math.min(totalPages, start + maxShow - 1);
    if (end - start + 1 < maxShow) start = Math.max(1, end - maxShow + 1);

    for (let i = start; i <= end; i++) {
        if (i === current) {
            html += `<li><span class="page-numbers current">${i}</span></li>`;
        } else {
            html += `<li><a href="#" class="page-numbers" data-page="${i}">${i}</a></li>`;
        }
    }

    // Next
    const next = Math.min(totalPages, current + 1);
    html += `<li><a href="#" class="next page-numbers" data-page="${next}"><i class="zmdi zmdi-long-arrow-right"></i></a></li>`;

    wrapper.innerHTML = html;

    // attach handlers
    wrapper.querySelectorAll("a.page-numbers").forEach(a => {
        a.onclick = (ev) => {
            ev.preventDefault();
            const p = parseInt(a.getAttribute("data-page"), 10);
            if (!isNaN(p)) {
                sortProducts(null, null, null, null, null, p); // keep active filters, only change page
            }
        };
    });
}


