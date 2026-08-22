window.addEventListener("load", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        await loadCategories()
        await loadProductSpecification();
        await loadAdminPanelData();

        const statusFlow = [
            //"PENDING",
            //"REJECTED",
            //"APPROVED",
            "PACKING",
            "DELIVERED",
            "RECEIVED",
            "COMPLETED"
        ];
        $(document).on('click', '.status-stepper .inc', async function () {
            const $stepper = $(this).closest('.status-stepper');
            const $input = $stepper.find('.status-box');

            const orderId = $stepper.data('order-id');
            const currentStatus = $input.val();
            const index = statusFlow.indexOf(currentStatus);

            if (index !== -1 && index < statusFlow.length - 1) {
                const newStatus = statusFlow[index + 1];

                $input.val(newStatus);
                await changeOrdersStatus(orderId, newStatus);
            }
        });
        $(document).on('click', '.status-stepper .dec', async function () {
            const $stepper = $(this).closest('.status-stepper');
            const $input = $stepper.find('.status-box');

            const orderId = $stepper.data('order-id');
            const currentStatus = $input.val();
            const index = statusFlow.indexOf(currentStatus);

            if (index > 0) {
                const newStatus = statusFlow[index - 1];

                $input.val(newStatus);
                await changeOrdersStatus(orderId, newStatus);
            }
        });
    }finally {
        Notiflix.Loading.remove(500);
    }
})

document.getElementById("admin_panel_anchor").addEventListener("click", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");
    try{
        await loadAdminPanelData();
    }finally {
        Notiflix.Loading.remove();
    }
})
document.getElementById("manage_users_anchor").addEventListener("click", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");
    try{
        await sortUsers();
    }finally {
        Notiflix.Loading.remove();
    }
})
document.getElementById("add_product_anchor").addEventListener("click", () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    Notiflix.Loading.remove(250);
})
document.getElementById("manage_product_anchor").addEventListener("click", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");
    try {
        // await loadAllAdminProducts();
        await sortAdminProducts();
    }finally {
        Notiflix.Loading.remove();
    }
})
document.getElementById("manage_orders_anchor").addEventListener("click", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");
    try{
        await sortOrders();
    }finally {
        Notiflix.Loading.remove();
    }
})

const description1 = new RichTextEditor('#description');
function validatePriceInput(input) {
    input.value = input.value.replace(/[^0-9]/g, ''); // remove non-digits
}

let currentPage = 1;

const stockModalEl = document.getElementById("stockModal");
const stockModal = new bootstrap.Modal(stockModalEl);

// LOADING DATA START
function renderDropdowns(selector, list, suffix){
    selector.innerHTML = `<option value="0">Select</option>`
    list.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[suffix];
        selector.appendChild(option);
    })
}

async function loadCategories(){
    try{
        const response = await fetch("api/data/load-categories");
        if(response.ok){
            const data = await response.json();
            const categorySelect = document.getElementById("categorySelect");
            renderDropdowns(categorySelect, data.categories, 'name')
        }else{
            notify("Category Loading Failed!");
        }
    }catch (e) {
        notify(e.message);
    }
}
async function loadSubCategories(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    const categorySelect = document.getElementById("categorySelect");

    try{
        const response = await fetch(`api/data/${categorySelect.value}/load-subcategories`);
        if(response.ok){
            const data = await response.json();

            const subCategorySelect = document.getElementById("subCategorySelect");
            if(data.status){
                renderDropdowns(subCategorySelect, data.subCategories, 'name');
            }else{
                notify(data.message);
            }
        }else{
            notify("Model Loading Failed!");
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function loadProductSpecification(){
    try {
        const response = await fetch("api/data/specifications");
        if(response.ok){
            const data = await response.json();

            const brandSelect = document.getElementById("brandSelect");
            const colorSelect = document.getElementById("colorSelect");
            const sizeSelect = document.getElementById("sizeSelect");

            renderDropdowns(brandSelect, data.brands, 'name')
            renderDropdowns(colorSelect, data.colors, 'value')
            renderDropdowns(sizeSelect, data.sizes, 'value')
        }else{
            notify(e.message);
        }
    }catch (e){
        notify(e.message);
    }
}
async function loadDiscounts(){ //called in openStockModal();
    try {
        const response = await fetch("api/data/load-discounts");
        if(response.ok){
            const data = await response.json();
            return data.discounts;
        }else{
            notify(e.message);
            return [];
        }
    }catch (e){
        notify(e.message);
        return [];
    }
}

async function loadAllAdminProducts(){
    try {
        const response = await fetch("api/products/admin-all");
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            if (data.status) {
                loadAdminProductTable(data.product);  // 👈 pass product array
            } else {
                notify(data.message);
            }
        }else{
            notify("Products Loading Failed!");
        }
    }catch (e){
        notify(e.message);
    }
}


// LOADING DATA END

// ADMIN PANEL PART
let salesChart;
document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("salesChart").getContext("2d");

    salesChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: [],
            datasets: [{
                label: "Sales (Rs.)",
                data: [],
                borderColor: "#0d6efd",
                backgroundColor: "rgba(13,110,253,0.15)",
                fill: true,
                tension: 0.4,
                pointRadius: 4,
                pointBackgroundColor: "#0d6efd"
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return " Rs. " + context.parsed.y.toLocaleString();
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: value => "Rs. " + value / 1000 + "k"
                    }
                }
            }
        }
    });
});

async function loadAdminPanelData(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    try {
        const response = await fetch("api/profiles/admin-data");
        if(response.ok){
            const data = await response.json();
            // console.log(data);

            if(data.status){
                renderAdminPanelData(data.data);
            }else{
                notify(data.message)
            }
        }else{
            notify("Something went wrong!")
        }
    }catch (e){
        notify(e.message)
    }finally {
        Notiflix.Loading.remove();
    }
}
function renderAdminPanelData(data){

    //LOAD ADMIN PANEL FIRST ROW
    animateCounter({
        element: document.getElementById("totalRevenue"),
        start: data.totalRevenue - 1000,
        end: data.totalRevenue,
        duration: 900,
        formatter: v =>
            "Rs. " + Number(v).toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            })
    });

    animateCounter({
        element: document.getElementById("usersCount"),
        start: 0,
        end: data.totalUsers,
        formatter: v => String(Math.floor(v)).padStart(2, "0")
    });

    animateCounter({
        element: document.getElementById("productsCount"),
        start: 0,
        end: data.totalProducts,
        formatter: v => String(Math.floor(v)).padStart(2, "0")
    });

    animateCounter({
        element: document.getElementById("ordersCount"),
        start: 0,
        end: data.totalOrders,
        formatter: v => String(Math.floor(v)).padStart(2, "0")
    });
    //LOAD ADMIN PANEL FIRST ROW

    //LOAD ADMIN PANEL SECOND ROW CHART
    if (data.salesChart && salesChart) {
        salesChart.data.labels = data.salesChart.labels;
        salesChart.data.datasets[0].data = data.salesChart.values;
        salesChart.update();
    }
    //LOAD ADMIN PANEL SECOND ROW CHART

    //LOAD ADMIN PANEL MOST SELLING PRODUCT TABLE
    const mostSellingProductContainer = document.getElementById("mostSellingProductTableBody");

    if (Array.isArray(data.topProducts) || data.topProducts.length !== 0) {
        mostSellingProductContainer.innerText = ``;
        data.topProducts.forEach((item, index) => {
            let tr = document.createElement("tr");

            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${truncatePannelData(item.title)}</td>
            <td>${item.totalSold}</td>
            <td class="text-success">
                ${"Rs. " + Number(item.revenue).toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            })}
            </td>
        `;

            mostSellingProductContainer.appendChild(tr);
        });
    }
    //LOAD ADMIN PANEL MOST SELLING PRODUCT TABLE

    //LOAD ADMIN PANEL LOW STOCK PRODUCT TABLE
    const lowStockContainer = document.getElementById("lowStockTableBody");

    if (Array.isArray(data.lowStocks) || data.lowStocks.length !== 0) {
        lowStockContainer.innerText = ``;
        data.lowStocks.forEach((item, index) => {
            let tr = document.createElement("tr");

            tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${truncatePannelData(item.title)}</td>
            <td>
                <div style="width:20px;height:20px; background-color:${item.colorCode};border:1px solid #ccc;"></div>
            </td>
            <td>${item.size}</td>
            <td>${item.available}</td>
        `;

            lowStockContainer.appendChild(tr);
        });
    }
    //LOAD ADMIN PANEL LOW STOCK PRODUCT TABLE

    //LOAD BEST SELLER PRODUCT CARD DATA
    const bestSellerProductContainer = document.getElementById("bestSellerProductContainer");
    bestSellerProductContainer.innerHTML = ``;

    if(data.bestSeller !== undefined){
        const item = data.bestSeller;

        bestSellerProductContainer.innerHTML = `
        <div class="card-body text-center">
            <h5 class="fw-bold mb-3">Best Seller</h5>
            
            <!-- Product Info -->
            <div class="d-flex align-items-center justify-content-center mb-3">
                <img src="${item.image}"
                     style="width: 190px; height: 190px; object-fit: cover;">
            </div>
            
            <h6 class="fw-bold mb-1">${truncatePannelData(item.title)}</h6>
            <span class="text-muted">${item.brand}</span>
            
            <div class="mt-2 fw-semibold text-success">
                ${"Rs. " + Number(item.revenue).toLocaleString(undefined, {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                })}
            </div>
            <!-- Stats -->
            <div class="row text-center mb-3 mx-auto">
                <div class="col-3 mx-auto">
                    <div class="text-muted small">Sold</div>
                    <div class="fw-bold fs-5 text-primary">${item.totalSold}</div>
                </div>
                <div class="col-3 mx-auto">
                    <div class="text-muted small">In Stock</div>
                    <div class="fw-bold fs-5 text-success">${item.stockQty}</div>
                </div>
            </div>
        </div>
        `;
    }
    //LOAD BEST SELLER PRODUCT CARD DATA

    //LOAD LOW STOCKS AND PENDING ORDER COUNT
    animateCounter({
        element: document.getElementById("pendingOrdersCount"),
        start: 0,
        end: data.pendingOrders,
        formatter: v => String(Math.floor(v)).padStart(2, "0")
    });

    animateCounter({
        element: document.getElementById("lowStocksCount"),
        start: 0,
        end: data.lowStockCount,
        formatter: v => String(Math.floor(v)).padStart(2, "0")
    });
    //LOAD LOW STOCKS AND PENDING ORDER COUNT

    //LOAD RECENT ORDERS TABLE
    const recentOrdersContainer = document.getElementById("recentOrdersContainer");

    if (Array.isArray(data.recentOrders) || data.recentOrders.length !== 0) {
        recentOrdersContainer.innerText = ``;
        data.recentOrders.forEach((item, index) => {
            recentOrdersContainer.innerHTML += ` 
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    <div class="fw-semibold">#${item.id}</div>
                    <small class="text-muted">${item.customerName}</small>
                </div>
                <div class="text-end">
                    <div class="fw-semibold text-success">
                        ${"Rs. " + Number(item.price).toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            })}
                    </div>
                    ${item.status === "PENDING" ? `
                        <span class="badge bg-warning">${item.status}</span>
                    ` : item.status === "CANCELED" ? `
                        <span class="badge bg-danger text-white">${item.status}</span>
                    ` : item.status === "REJECTED" ? `
                        <span class="badge bg-danger text-white">${item.status}</span>
                    ` : item.status === "COMPLETED" ? `
                        <span class="badge bg-success text-white">${item.status}</span>
                    ` : item.status === "RECEIVED" ? `
                        <span class="badge bg-success text-white">${item.status}</span>
                    ` : `<span class="badge bg-primary text-white">${item.status}</span>`}
                </div>
            </li>
        `;
        });
    }
    //LOAD RECENT ORDERS TABLE

    //LOAD TOP CUSTOMERS TABLE
    const topCustomersContainer = document.getElementById("topCustomersContainer");
    if (Array.isArray(data.topCustomers) || data.topCustomers.length !== 0) {
        topCustomersContainer.innerText = ``;
        data.topCustomers.forEach((item, index) => {
            let tr = document.createElement("tr");

            tr.innerHTML = `
            <td>
                ${index === 0 ? `
                <span class="badge bg-warning text-dark">${index + 1}</span>
                ` : `<span class="badge bg-secondary">${index + 1}</span>`}
            </td>
            <td>
                <div class="fw-semibold">${item.name}</div>
                <small class="text-muted">${item.email}</small>
            </td>
            <td class="text-center">${item.totalOrders}</td>
            <td class="text-end fw-semibold text-success">
                ${"Rs. " + Number(item.totalSpent).toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            })}
            </td>
        `;

            topCustomersContainer.appendChild(tr);
        });
    }
    //LOAD TOP CUSTOMERS TABLE
}
function animateCounter({element, start = 0, end = 0, duration = 800, formatter = v => v}) {

    let startTime = null;

    function step(timestamp) {
        if (!startTime) startTime = timestamp;

        const progress = Math.min((timestamp - startTime) / duration, 1);
        const current = progress * (end - start) + start;

        element.innerText = formatter(current);

        if (progress < 1) requestAnimationFrame(step);
    }

    requestAnimationFrame(step);
}
function truncatePannelData(text, maxLength = 25) {
    if (!text) return "";
    return text.length > maxLength
        ? text.slice(0, maxLength) + "…"
        : text;
}

// ADMIN PANEL PART

//// DB NEW PRODUCT ADDING PART START
let index = 0;
function addStockToTable(){
    reportInit();
    let categorySelect = document.getElementById("categorySelect")
    let subCategorySelect = document.getElementById("subCategorySelect");
    let brandSelect = document.getElementById("brandSelect");
    let description = description1.getHTMLCode();
    let title = document.getElementById("title").value;

    let qty = document.getElementById("qty").value;
    let price = document.getElementById("price").value;
    let colorSelect = document.getElementById("colorSelect");
    let sizeSelect = document.getElementById("sizeSelect");

    if(categorySelect.value <= 0){
        notify("Please select a category!");
    }else if(subCategorySelect.value <= 0){
        notify("Please select a sub-category!");
    }else if(brandSelect.value <= 0){
        notify("Please select a brand!");
    }else if(title === ""){
        notify("Product title cannot be empty!")
    }else if(description === ""){
        notify("Product description cannot be empty!");
    }else if(colorSelect.value <= 0){
        notify("Please select a color!")
    }else if(sizeSelect.value <= 0){
        notify("Please select a size!")
    }else if(price <= 0){
        notify("Product price cannot be less than or equal to 0!")
    }else if(qty <= 0){
        notify("Product quantity cannot be less than or equal to 0!")
    }else{
        let colorId = colorSelect.value;
        let colorName = colorSelect.options[colorSelect.selectedIndex].text;
        let sizeId = sizeSelect.value;
        let sizeName = sizeSelect.options[sizeSelect.selectedIndex].text;

        index += 1;
        let tbody = document.getElementById("stockTableBody");

        // Create row
        let tr = document.createElement("tr");
        tr.classList.add("global-row");

        tr.setAttribute("data-color-id", colorId);
        tr.setAttribute("data-size-id", sizeId);

        tr.innerHTML = `
            <td class="global-cell">${index}</td>
            <td class="global-cell">${colorName}</td>
            <td class="global-cell">${sizeName}</td>
            <td class="global-cell">${qty}</td>
            <td class="global-cell">Rs. ${price}.00</td>
            <td class="global-cell remove-item">
                <a href="javascript:void(0)" onclick="removeStockTableRow(this)">
                    <i class="fa fa-trash-o"></i>
                </a>
            </td>
        `;

        tbody.appendChild(tr);

        document.getElementById("qty").value = "1";
        document.getElementById("price").value = "0.00";
        colorSelect.selectedIndex = 0;
        sizeSelect.selectedIndex = 0;
    }

}
function removeStockTableRow(btn) {
    let tr = btn.closest("tr");
    tr.remove();
    // updateRowNumbers();
}
function getStockTableAsJson(){
    let rows = document.querySelectorAll("#stockTableBody tr");
    let stockList = [];

    rows.forEach((row) => {
        let cells = row.querySelectorAll("td");

        stockList.push({
            colorId: row.getAttribute("data-color-id"),
            sizeId: row.getAttribute("data-size-id"),
            qty: cells[3].textContent,
            price: parseFloat(cells[4].textContent.replace("Rs.", "").replace(",", "").trim())
        });
    });

    return stockList;
}

function validateField(condition, message) {
    if (condition) {
        notify(message);
        return false;
    }
    return true;
}
async function resetFields(){
    document.getElementById("categorySelect").selectedIndex = 0;
    document.getElementById("subCategorySelect").selectedIndex = 0;
    document.getElementById("brandSelect").selectedIndex = 0;
    document.getElementById("title").value = "";
    description1.setHTMLCode("");

    document.getElementById("qty").value = "1";
    document.getElementById("price").value = "0.00";
    document.getElementById("colorSelect").selectedIndex = 0;
    document.getElementById("sizeSelect").selectedIndex = 0;

    document.getElementById("img1").value = "";
    document.getElementById("img2").value = "";
    document.getElementById("img3").value = "";

    // Clear table rows (correct way)
    document.getElementById("stockTableBody").innerHTML = "";
}

async function saveProductToDB(){
    let categorySelect = document.getElementById("categorySelect")
    let subCategorySelect = document.getElementById("subCategorySelect");
    let brandSelect = document.getElementById("brandSelect");
    let title = document.getElementById("title");
    let description = description1.getHTMLCode();

    const stockList = getStockTableAsJson(); // get array of stock objects

    let img1 = document.getElementById("img1");
    let img2 = document.getElementById("img2");
    let img3 = document.getElementById("img3");

    let hasFile = false;

    if (!validateField(categorySelect.value <= 0, "Please select a category!")) return;
    if (!validateField(subCategorySelect.value <= 0, "Please select a sub-category!")) return;
    if (!validateField(brandSelect.value <= 0, "Please select a brand!")) return;
    if (!validateField(title.value.trim() === "", "Product title cannot be empty!")) return;
    if (!validateField(description.trim() === "" || description === "<p><br></p>",
        "Product description cannot be empty!")) return;

    if (stockList.length === 0) {
        notify("Please add at least one stock item.");
        return;
    }

    // Append files only if selected
    if (img1.files[0]) {
        hasFile = true;
    }
    if (img2.files[0]) {
        hasFile = true;
    }
    if (img3.files[0]) {
        hasFile = true;
    }

    // If no files selected, notify user and exit
    if (!hasFile) {
        notify("Please select at least one image to upload!");
        return;
    }

    reportInit();
    Notiflix.Loading.pulse("Processing...");

    const productDataObj = {
        categoryId: categorySelect.value,
        subCategoryId: subCategorySelect.value,
        brandId: brandSelect.value,
        title: title.value,
        description: description
    }

    const formData = new FormData();
    formData.append("product", JSON.stringify(productDataObj));

    try {
        const response = await fetch("api/products/save-product", {
            method: "POST",
            body: formData
        });
        if (response.ok) {
            const data = await response.json();
            console.log(data);

            if (data.status) {
                notifySuccess("Product details uploaded successfully.")
                await uploadProductImages(data.productId);
                await saveStockDetails(data.productId);

                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    () => {
                        resetFields();
                    }
                );
            } else {
                notify(data.message);
            }
        } else {
            notify("Product details adding failed!");
        }
    } catch (e) {
        notify(e.message);
    } finally {
        Notiflix.Loading.remove();
    }

}
async function saveStockDetails(productId){
    const formData = new FormData();
    formData.append("stockData", JSON.stringify(getStockTableAsJson()));
    formData.append("productId", productId);

    try {
        const response = await fetch(`api/products/save-stocks`, {
            method:"POST",
            body: formData
        });
        if(response.ok){
            const data = await response.json();
            // console.log(data);

            if(data.status){
                notifySuccess(data.message);
            }else{
                notify(data.message);
            }
        }else{
            notify("Stock details adding failed!");
        }
    }catch (e){
        notify(e.message);
    }
}
async function uploadProductImages(productId){
    let img1 = document.getElementById("img1");
    let img2 = document.getElementById("img2");
    let img3 = document.getElementById("img3");

    const formData = new FormData();

    // Append files only if selected
    if (img1.files[0]) {
        formData.append("images[]", img1.files[0]);
    }
    if (img2.files[0]) {
        formData.append("images[]", img2.files[0]);
    }
    if (img3.files[0]) {
        formData.append("images[]", img3.files[0]);
    }

    try {
        const response = await fetch(`api/products/${productId}/upload-images`, {
            method:"PUT", //to update a data that already has
            body: formData
        });

        if(response.ok){
            const data = await response.json();
            // console.log(data);
            notifySuccess(data.message);
        }else{
            notify("Product images uploading failed!");
        }
    }catch (e) {
        notify(e.message);
    }
}
//// DB NEW PRODUCT ADDING PART END

//// ADMIN PRODUCT MANAGE PART START
async function sortAdminProducts(page = 1) {
    currentPage = page || 1;

    // read UI safely (elements may be missing on some pages)
    const searchEl = document.getElementById("adminSearchField");
    const orderEl = document.getElementById("adminProductsOrderSelect");

    const searchField = searchEl ? searchEl.value.trim() : "";
    const productsPerPage = 10;
    const orderSelect = orderEl ? parseInt(orderEl.value, 10) || 0 : 0;

    // Build query params safely
    const params = new URLSearchParams();
    if (searchField) params.append("search", searchField);
    params.append("orderSelect", orderSelect);
    params.append("limit", productsPerPage);
    params.append("page", currentPage);

    const url = `api/products/sort-admin-products?${params.toString()}`;
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
            notify(data.message || "No Products found");

            generatePagination(0, productsPerPage, 1, "products");
            return;
        }

        loadAdminProductTable(data.product);

        // build pagination
        const totalPages = data.totalPages || 1;
        generatePagination(totalPages, productsPerPage, data.currentPage || currentPage, "products");

    } catch (err) {
        console.error(err);
        notify(err.message || "Data loading failed!");
    } finally {
        Notiflix.Loading.remove();
    }
}
function loadAdminProductTable(productData) {
    const tbody = document.getElementById("adminProductTableBody");
    tbody.innerHTML = ""; // clear table

    productData.forEach(async (p, index) => {
        // Add row
        let tr = document.createElement("tr");
        tr.innerHTML = `
            <tr class="cart_item">
                <td class="item-img">
                    <a href="#"><img src="${p.imgPath1}" alt=""> </a>
                </td>
                <td class="item-title"> <a href="#">${p.title}</a></td>
                <td class="item-title"><a href="javascript:void(0)" style="cursor: default">${p.categoryName} - ${p.subCategoryName} - ${p.brandName}</a>  </td>
                <td class="">
                    ${p.inStock ? "In Stock" : "Out of Stock"} &nbsp;<a href="javascript:void(0)" class="fs-5 text-dark" onclick="openStockModal(${p.productId})"><i class="fa fa-edit"></i></a>
                </td>
                <td class="remove-item"><a href="#"><i class="fa fa-trash-o"></i></a></td>
            </tr>
        `;

        tbody.appendChild(tr);
    });
}

async function loadStocksByProductId(productId){
    try {
        const response = await fetch(`api/products/${productId}/all-stocks`);
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            return data.stocks;
            // notify(data.message);
        }else{
            notify("Stocks Loading Failed!");
            return null;
        }
    }catch (e){
        notify(e.message);
        return null;
    }
}
async function openStockModal(productId) {
    // productId Passing From loadAdminProductTable();
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");
    try{
        let stockData = await loadStocksByProductId(productId);
        let discountData = await loadDiscounts();
        loadAdminStockModalTable(stockData, discountData);
    }finally {
        Notiflix.Loading.remove(500);
        await new Promise(res => setTimeout(res, 500));
        stockModal.show();
    }
}
function loadAdminStockModalTable(stockData, discounts) {
    // console.log(discounts);
    // console.log(stockData);
    const tbody = document.getElementById("AdminStockTableBody");
    tbody.innerHTML = ""; // clear old rows

    stockData.forEach((stock, index) => {

        let discountOptions = "";
        let nowDate = new Date();

        // 1. Find the currently selected discount
        let currentDiscount = discounts.find(d => d.id === stock.discountId);
        // 2. Add CURRENT discount as the first option
        if (currentDiscount) {
            discountOptions += `
            <option selected value="${currentDiscount.id}">
                ${currentDiscount.discountName}${currentDiscount.value > 0 ? " - " + currentDiscount.value + "% OFF" : ""}
            </option>`;
        }

        discounts.forEach(d => {
            // skip the current discount
            if (d.id === stock.discountId) return;

            // Always include DEFAULT (id = 1 or value = 0 — adjust as needed)
            if (d.discountName === "DEFAULT" || d.value === 0) {
                discountOptions += `<option value="${d.id}">${d.discountName}</option>`;
                return;
            }

            // Convert expiredAt string to a Date object
            let expiredDate = new Date(d.expiredAt);

            // Only show if NOT EXPIRED
            if (expiredDate > nowDate) {
                discountOptions += `<option value="${d.id}">${d.discountName} - ${d.value}% OFF</option>`;
            }
        });

        // Calculate display price
        let priceHTML = "";

        if (currentDiscount && (currentDiscount.discountName !== "DEFAULT" && currentDiscount.value > 0) && new Date(currentDiscount.expiredAt) > nowDate) {
            // discount EXISTS and is NOT DEFAULT
            let newPrice = stock.price - ((stock.price / 100) * currentDiscount.value);

            priceHTML = `
                <span style="text-decoration: line-through; color:#999;">
                    Rs. ${stock.price}
                </span>
                <span style="margin-left:6px;">
                    Rs. ${newPrice}
                </span>
            `;
        } else {
            // DEFAULT
            priceHTML = `Rs. ${stock.price}`;
        }

        const row = `
            <tr class="global-row">
                <td class="global-cell">${index + 1}</td>
                <td class="global-cell">${stock.colorName}</td>
                <td class="global-cell">${stock.sizeName}</td>
                <td class="global-cell">${stock.qty}</td>
                <td class="global-cell" id="stockPrice${stock.stockId}">${priceHTML}</td>

                <td class="global-cell" >
                    <select class="selectpicker select-custom" data-live-search="true" id="discountSelect${stock.stockId}" onchange="changeStockDiscount(${stock.stockId})">
                        ${discountOptions}
                    </select>
                </td>

                <td class="global-cell">
                    <select class="selectpicker select-custom" data-live-search="true" id="statusSelect${stock.stockId}" onchange="changeStockStatus(${stock.stockId})">
                        ${stock.statusId === 1 ? `
                            <option value="1">ACTIVE</option>
                            <option value="3">INACTIVE</option>
                        ` : ` 
                            <option value="3">INACTIVE</option>
                            <option value="1">ACTIVE</option>
                        `}
                    </select>
                </td>

                <td class="global-cell remove-item">
                    <a href="javascript:void(0)" onclick="deleteStock(${stock.stockId})">
                        <i class="fa fa-trash-o"></i>
                    </a>
                </td>
            </tr>
        `;

        tbody.insertAdjacentHTML("beforeend", row);
    });

    // Reinitialize bootstrap-select after adding rows
    // $('.selectpicker').selectpicker('refresh');
}

async function changeStockDiscount(stockId){
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    let discount = document.getElementById("discountSelect" + stockId);

    const dataObj = {
        stockId: stockId,
        discountId: discount.value
    }

    try{
        const response = await fetch("api/products/add-stock-discount", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        })
        if(response.ok){
            const data = await response.json();
            if(data.status){
                notifySuccess(data.message)

                const priceCell = document.getElementById("stockPrice" + stockId);
                const currentDiscount = data.discount; // StockDTO returned from backend

                const nowDate = new Date();
                let priceHTML = "";

                if (currentDiscount && (currentDiscount.discountName !== "DEFAULT" && currentDiscount.value > 0) && new Date(currentDiscount.expiredAt) > nowDate) {
                    // Active discount
                    let newPrice = data.price - ((data.price / 100) * currentDiscount.value);

                    priceHTML = `
                        <span style="text-decoration: line-through; color:#999;">
                            Rs. ${data.price}
                        </span>
                        <span style="margin-left:6px;">
                            Rs. ${newPrice}
                        </span>
                    `;
                } else {
                    // Default discount or expired
                    priceHTML = `Rs. ${data.price}`;
                }


                priceCell.innerHTML = priceHTML;
            }else{
                notify(data.message)
            }
        }else{
            notify("Discount Adding Failed!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function changeStockStatus(stockId){
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    let status = document.getElementById("statusSelect" + stockId);

    const dataObj = {
        stockId: stockId,
        statusId: status.value
    }

    try{
        const response = await fetch("api/products/change-stock-status", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        })
        if(response.ok){
            const data = await response.json();
            if(data.status2){
                notifySuccess(data.message)
            }else{
                notify(data.message)
            }
        }else{
            notify("Status Changing Failed!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
//// ADMIN PRODUCT MANAGE PART END

//// ADMIN ORDERS MANAGE PART START
async function sortOrders(page = 1) {
    currentPage = page || 1;

    // read UI safely (elements may be missing on some pages)
    const searchEl = document.getElementById("ordersSearchField");
    const orderByStatusEl = document.getElementById("statusSelect");

    const searchField = searchEl ? searchEl.value.trim() : "";
    const productPerPage = 10;
    const orderSelect = orderByStatusEl ? parseInt(orderByStatusEl.value, 10) || 0 : 0;
    // console.log(orderSelect)

    // Build query params safely
    const params = new URLSearchParams();
    if (searchField) params.append("search", searchField);
    params.append("orderByStatus", orderSelect);
    params.append("limit", productPerPage);
    params.append("page", currentPage);

    const url = `api/orders/sort?${params.toString()}`;
    console.log("SORT URL:", url);

    try {
        Notiflix.Loading.pulse("Loading Orders...");
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("Orders Loading Failed!");
        }

        const data = await response.json();
        console.log(data)
        if (!data.status) {
            notify(data.message || "No Orders found");
            generatePagination(0, productPerPage, 1, "orders");

            return;
        }

        renderOrderData(data);

        // build pagination
        const totalPages = data.totalPages || 1;
        generatePagination(totalPages, productPerPage, data.currentPage || currentPage, "orders");


    } catch (err) {
        console.error(err);
        notify(err.message || "Sorting failed!");
    } finally {
        Notiflix.Loading.remove();
    }
}

function renderOrderData(data)  {
    const tableBodyContainer = document.getElementById("adminOrderTableBody");
    tableBodyContainer.innerHTML = "";

    if (data.orders && data.orders.length > 0) {
        data.orders.forEach((order, index) => {
            let tr = document.createElement("tr");
            tr.classList.add("cart_item");

            tr.innerHTML = `
            <td class="item-price">
                <a class="text-danger fw-bold text-decoration-underline"
                   href="javascript:void(0)"
                   data-items='${encodeURIComponent(JSON.stringify(order.items))}'
                   data-order-details='${encodeURIComponent(JSON.stringify(order.orderDetails))}'
                   onclick="openOrderItemModal(this)">
                   ${order.idAsString}
                </a>
            </td>
            <td class="item-qty">Rs ${order.totalPrice.toFixed(2)}</td>
            <td class="item-qty">${order.createdAt}</td>
            <td class="remove-item fw-bold">${order.deliveryTypeName}</td>
            <td class="remove-item fw-bold">${order.statusValue}</td>
            
            <td class="item-qty">
                ${order.statusValue === "PENDING" ? `
                    <a class="btn-def" href="javascript:void(0)" style="background-color:#b50311; color:white" onclick="changeOrdersStatus(${order.idAsLong}, 'REJECTED')">
                        REJECTED
                    </a>
                    <a class="btn-def" href="javascript:void(0)" style="background-color:#0f8703; color:white" onclick="changeOrdersStatus(${order.idAsLong}, 'PACKING')"> 
                        APPROVED
                    </a>
                ` : order.statusValue === "CANCELED" || order.statusValue === "REJECTED" ? `
                    <a class="btn-def" href="javascript:void(0)" style="background-color:white; color:#b50311; border-color:#b50311">${order.statusValue}</a>
                ` : order.statusValue === "COMPLETED" ? `
                    <a class="btn-def" href="javascript:void(0)" style="background-color:white; color:#0f8703; border-color:#0f8703">
                       ${order.statusValue}
                    </a>
                ` : `
                    <div class="cart-quantity">
                        <div class="product-qty">
                            <div class="cart-quantity">
                                <div class="cart-plus-minus status-stepper" data-order-id="${order.idAsLong}">
                                    <div class="dec qtybutton">&lt;</div>
                                    <input type="text" class="status-box cart-plus-minus-box text-dark" value="${order.statusValue}" style="width: 90px" readonly>
                                    <div class="inc qtybutton">&gt;</div>
                                </div>
                            </div>
                        </div>
                    </div>
                `}
            </td>
        `;

            tableBodyContainer.appendChild(tr);
        });
    }else{
        tableBodyContainer.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-4 fs-1 fw-bold text-muted">
                    No Orders Found!
                </td>
            </tr>
        `;
    }
}
function openOrderItemModal(el) {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    const items = JSON.parse(
        decodeURIComponent(el.dataset.items)
    );

    const orderDetails = JSON.parse(
        decodeURIComponent(el.dataset.orderDetails)
    );

    const modalEl = document.getElementById('orderItemsModal');
    const modal = new bootstrap.Modal(modalEl);

    try{
        renderModalOrderItemsData(items, orderDetails);
    }finally {
        Notiflix.Loading.remove();
        modal.show();
    }
}
function renderModalOrderItemsData(items, orderDetails){
    const tableBodyContainer = document.getElementById("adminOrderItemTableBody");
    tableBodyContainer.innerHTML = "";

    const orderDetailsContainer = document.getElementById("orderDetailsModal");
    orderDetailsContainer.innerHTML = `
        <div class="row">
            <div class="col-md-6 col-12" style="height: 120px">
                <div class="row">
                    <span class="fw-bold fs-6">Addess : </span>
                    <span>${orderDetails.lineOne}, ${orderDetails.lineTwo}</span>
                </div>
                <div class="col-12 d-flex flex-column">
                    <span class="fw-bold fs-6">
                        ${orderDetails.provinceName}, ${orderDetails.districtName}, ${orderDetails.cityName} (${orderDetails.postalCode})
                    </span>
                </div>
            </div>
            <div class="col-md-1  d-lg-flex d-none justify-content-center" style="height: 100px">
                <div class="vr"></div>
            </div>
            <div class="col-md-4 col-12 d-flex flex-column" style="height: 120px">
                <span class="fw-bold fs-6">Name : <span class="fw-normal">${orderDetails.name}</span></span>
                <span class="fw-bold fs-6">Email : <span class="fw-normal">${orderDetails.email}</span></span>
                <span class="fw-bold fs-6">Mobile : <span class="fw-normal">${orderDetails.mobile}</span></span>
                <span class="fw-bold fs-6">Delivery Cost : <span class="fw-normal">Rs. ${orderDetails.shipping.toFixed(2)}</span></span>
            </div>
        </div>
    `;
    items.forEach(item => {
        const tr = document.createElement("tr");
        tr.classList.add("cart_item");

        tr.innerHTML = `
            <td class="item-img">
                <img src="${item.imgPath1}" alt="">
            </td>
            <td>
                <a class="text-dark fw-bold">
                    ${truncate(item.title, 30)}
                </a>
            </td>
            <td class="remove-item fw-bold">
                <div style="display:flex;align-items:center;justify-content:center">
                    <div style="width:20px;height:20px;
                        background-color:${item.colorCode};
                        border:1px solid #ccc;"></div>
                    &nbsp;&nbsp;${item.size}
                </div>
            </td>
            <td class="item-qty">Rs. ${(+item.buyingPrice).toFixed(2)}</td>
            <td class="item-qty">${item.qty}</td>
            <td class="total-price">
                ${item.statusName === "ACTIVE"
                    ? `<a class="btn-def reject-btn" data-item-id="${item.id}" style="background:#b50311;color:#fff; cursor: pointer;" onclick="changeOrderItemStatus(${item.id})">REJECT</a>`
                    : `<span class="fw-bold statusLabel" data-item-id="${item.id}">${item.statusName}</span>`
                }
            </td>
        `;

        tableBodyContainer.appendChild(tr);
    });
}
function truncate(text, maxLength = 30) {
    if (!text) return "";
    return text.length > maxLength
        ? text.slice(0, maxLength) + "…"
        : text;
}

async function changeOrderItemStatus(itemId){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    try {
        const response = await fetch("api/orders/items-status?id=" + itemId, {
            method: "PUT",
        });
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    () => {
                        sortOrders(currentPage)
                    }
                );

                // find the clicked button
                const btn = document.querySelector(
                    `.reject-btn[data-item-id='${itemId}']`
                );

                if(btn){
                    const td = btn.closest("td");

                    // replace button with label
                    td.innerHTML = `
                        <span class="fw-bold statusLabel"
                              data-item-id="${itemId}">
                            REJECTED
                        </span>
                    `;
                }
            }else{
                notify(data.message);
            }
        }else{
            notify("Something went wrong!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function changeOrdersStatus(orderId, statusValue){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    const dataObj = {
        orderId: orderId,
        statusValue: statusValue
    }

    try {
        const response = await fetch("api/orders/status", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        });
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    () => {
                        sortOrders(currentPage)
                    }
                );
            }else{
                notify(data.message);
            }
        }else{
            notify("Something went wrong!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
//// ADMIN ORDERS MANAGE PART END

//USER MANEGEMENT HANDELING FUNCTIONS
async function sortUsers(page = 1) {
    currentPage = page || 1;

    // read UI safely (elements may be missing on some pages)
    const searchEl = document.getElementById("usersSearchField");
    const orderEl = document.getElementById("orderSelect");

    const searchField = searchEl ? searchEl.value.trim() : "";
    const usersPerPage = 10;
    const orderSelect = orderEl ? parseInt(orderEl.value, 10) || 0 : 0;

    // Build query params safely
    const params = new URLSearchParams();
    if (searchField) params.append("search", searchField);
    params.append("orderSelect", orderSelect);
    params.append("limit", usersPerPage);
    params.append("page", currentPage);

    const url = `api/profiles/sort-users?${params.toString()}`;
    console.log("SORT URL:", url);

    try {
        Notiflix.Loading.pulse("Loading Users...");
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("User Loading Failed!");
        }

        const data = await response.json();
        // console.log(data)
        if (!data.status) {
            notify(data.message || "No Users found");

            generatePagination(0, usersPerPage, 1, "users");
            return;
        }

        renderUserData(data);

        // build pagination
        const totalPages = data.totalPages || 1;
        generatePagination(totalPages, usersPerPage, data.currentPage || currentPage, "users");

    } catch (err) {
        console.error(err);
        notify(err.message || "Sorting failed!");
    } finally {
        Notiflix.Loading.remove();
    }
}

function renderUserData(data) {
    const tableBodyContainer = document.getElementById("adminUserManagementTableBody");
    tableBodyContainer.innerHTML = "";

    const USER_STATUSES = ["BLOCKED", "GUEST", "VERIFIED"];

    data.users.forEach((user, index) => {

        const tr = document.createElement("tr");
        tr.classList.add("cart_item");

        let optionsHtml = "";
        USER_STATUSES.forEach(status => {
            if (status !== user.statusValue) {
                optionsHtml += `<option value="${status}">${status}</option>`;
            }
        });

        tr.innerHTML = `
            <td class="item-qty">${index + 1}</td>
            <td class="item-qty text-danger fw-bold" style="cursor: pointer;" >
                <a class="text-danger fw-bold text-decoration-underline"
                   href="javascript:void(0)"
                   data-addresses ='${encodeURIComponent(JSON.stringify(user.addresses))}'
                   onclick="openUserItemModal(this)">
                   ${user.firstName} ${user.lastName}
                </a>
            </td>
            <td class="item-price">${user.email}</td>
            <td class="item-price">${user.mobile ?? "...."}</td>
            <td class="item-price">${user.sinceAt}</td>
            <td class="remove-item fw-bold">${user.statusValue}</td>
            <td class="item-qty">
                <select class="select-custom user-status-select" data-user-id="${user.id}" onchange="changeUserStatus(this)">
                    <option value="${user.statusValue}">${user.statusValue}</option>
                    ${optionsHtml}
                </select>
            </td>
        `;

        tableBodyContainer.appendChild(tr);
    });
}
function openUserItemModal(el) {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    const addressDetails = JSON.parse(
        decodeURIComponent(el.dataset.addresses)
    );

    const modalEl = document.getElementById('userAddressesModal');
    const modal = new bootstrap.Modal(modalEl);

    try{
        console.log(addressDetails)
        renderModalUserAddressesData(addressDetails);
    }finally {
        Notiflix.Loading.remove();
        modal.show();
    }
}
function renderModalUserAddressesData(addresses){

    const addressDetailsContainer = document.getElementById("addressDetailsModal");
    addressDetailsContainer.innerHTML = ``;

    if (!Array.isArray(addresses) || addresses.length === 0) {
        addressDetailsContainer.innerHTML = `
            <div class="col-12 text-center" >
                <div class="row">
                    <h2>No address added yet</h2>
                </div>
            </div>
        `;
    } else {
        addresses.sort((a, b) => b.isPrimary - a.isPrimary);

        addresses.forEach(item => {
            addressDetailsContainer.innerHTML += `
            <div class="col-12" >
                <div class="row">
                    <div class="col-md-6 col-12" style="height: 120px">
                        <div class="row">
                            <span class="fw-bold fs-6">Address : </span>
                            <span>${item.lineOne}, ${item.lineTwo}</span>
                        </div>
                        <div class="col-12 d-flex flex-column">
                            <span class="fw-bold fs-6">
                                ${item.provinceName}, ${item.districtName}, ${item.cityName}
                            </span>
                        </div>
                    </div>
                    <div class="col-md-1  d-lg-flex d-none justify-content-center" style="height: 100px">
                        <div class="vr"></div>
                    </div>
                    <div class="col-md-4 col-12 d-flex flex-column" style="height: 120px">
                        <span class="fw-bold fs-6">Mobile : <span class="fw-normal">${item.mobile}</span></span>
                        <span class="fw-bold fs-6">Postal Code : <span class="fw-normal">${item.postalCode}</span></span>
                        <span class="fw-bold fs-6">Delivery Cost : <span class="fw-normal">${item.shipping}</span></span>
                        <span class="fw-bold fs-6">
                            Status :
                            ${item.isPrimary ?
                `<span class="text-danger">Primary Address</span>`
                : `<span class="fw-normal">Secondary</span>`
            }
                        </span>
                    </div>
                </div>
            </div>
            <div class="col-12" style="height: 20px;">
                <div class="row  d-flex flex-column align-items-center">
                    <hr class="border border-1 border-dark">
                </div>
            </div>
        `;
        });
    }
}

async function changeUserStatus(selectElement){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    const selectedValue = selectElement.value;
    const userId = selectElement.dataset.userId;

    const dataObj = {
        userId: userId,
        statusValue: selectedValue
    }

    try {
        const response = await fetch("api/profiles/user-status", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        });
        if(response.ok){
            const data = await response.json();
            console.log(data)
            if(data.status){
                // Notiflix.Report.success(
                //     'Calivon',
                //     data.message,
                //     'Okay', //Button title
                //     () => {
                //         sortUsers(currentPage)
                //     }
                // );
                notifySuccess(data.message);
                sortUsers(currentPage);
            }else{
                notify(data.message);
            }
        }else{
            notify("Something went wrong!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
//USER MANEGEMENT HANDELING FUNCTIONS

function generatePagination(totalPages = 1, perPage = 15, current = 1, type = "orders") {
    const wrapper = document.querySelector(`.pagination-btn .page-numbers.${type}`);
    if (!wrapper) return;

    let html = "";

    // Prev
    const prev = Math.max(1, current - 1);
    html += `
        <li>
            <a href="#" class="page-numbers" data-page="${prev}">
                <i class="zmdi zmdi-long-arrow-left"></i>
            </a>
        </li>
    `;

    // Pages (windowed)
    const maxShow = 7;
    let start = Math.max(1, current - Math.floor(maxShow / 2));
    let end = Math.min(totalPages, start + maxShow - 1);
    if (end - start + 1 < maxShow) {
        start = Math.max(1, end - maxShow + 1);
    }

    for (let i = start; i <= end; i++) {
        html += i === current
            ? `<li><span class="page-numbers current">${i}</span></li>`
            : `<li><a href="#" class="page-numbers" data-page="${i}">${i}</a></li>`;
    }

    // Next
    const next = Math.min(totalPages, current + 1);
    html += `
        <li>
            <a href="#" class="page-numbers" data-page="${next}">
                <i class="zmdi zmdi-long-arrow-right"></i>
            </a>
        </li>
    `;

    wrapper.innerHTML = html;
}
document.addEventListener("click", function (e) {
    const link = e.target.closest("a.page-numbers");
    if (!link) return;

    const wrapper = link.closest("ul.page-numbers");
    if (!wrapper) return;

    e.preventDefault();

    const page = parseInt(link.dataset.page, 10);
    if (isNaN(page)) return;

    const type = wrapper.dataset.type;

    if (type === "orders") {
        sortOrders(page);
    } else if (type === "users") {
        sortUsers(page);
    }
});



