async function addProductToWishlist(id){
    reportInit()
    Notiflix.Loading.pulse("Loading Data...");

    const obj= {
        stockId:id
    }

    try{
        const response = await fetch("api/products/add-product-wishlist", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(obj)
        });

        if(response.ok){
            const data = await response.json();
            console.log(data)
            if(data.status){
                notifySuccess(data.message);
            }else{
                notify(data.message)
            }
        }else{
            notify("Product Adding Failed!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function addProductToCart(id, qty){
    reportInit()
    Notiflix.Loading.pulse("Loading Data...");

    const obj= {
        stockId:id,
        qty: qty
    }

    try{
        const response = await fetch("api/products/add-product-cart", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(obj)
        });

        if(response.ok){
            const data = await response.json();
            if(data.status){
                notifySuccess(data.message);
                let qtyInput = document.getElementById("qtyInput");
                qtyInput.value = 1;
                if (window.headerContentInstance) {
                    await window.headerContentInstance.getHeaderCartProducts();
                }
            }else{
                notify(data.message)
            }
        }else{
            notify("Product Adding Failed!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}

window.addEventListener('DOMContentLoaded', async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        const params = new URLSearchParams(window.location.search);
        const stockId = params.get('id');

        await getSingleProductViewData(stockId);
    }finally {
        Notiflix.Loading.remove(500);
    }

});

function renderDropdowns(selector, list, suffix, defaultId){
    list.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[suffix] ;
        if(item.id === defaultId){
            option.selected = true;
        }
        selector.appendChild(option);
    })
}

async function getSingleProductViewData(stockId){
    try {
        const response = await fetch(`api/products/load-single-view-data?id=${stockId}`);
        if(response.ok){
            const data = await response.json();
            console.log(data);

            renderSingleProductView(data);
            getRelatedProduct(data.product.brandId);
        }else{
            notify("Product Data Loading Failed!");
            // return null;
        }
    }catch (e){
        notify(e.message);
        // return null;
    }
}
function renderSingleProductView(data){
    let product = data.product;
    let stock = data.stock;


    let imageContainer = document.getElementById("imagesContainer");
    imageContainer.innerHTML = `
        <div class="product-more-views ">
            <div class="tab_thumbnail" data-tabs="tabs">
                <div class="thumbnail-carousel ">
                    <ul class="nav ">
                        <li>
                            <div style="font-size: 40px; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #333333;" onclick="selectActiveImage('up')">
                                <i class="fa fa-chevron-up"></i>
                            </div>
                        </li>
                        
                        <li>
                            <a class="active" href="#view11" class="shadow-box" aria-controls="view11" data-bs-toggle="tab"><img src="${product.imgPath1}" alt=""></a>
                        </li>
                        ${product.imgPath2  !== undefined && product.imgPath2 !== null && product.imgPath2 !== "" ?
                            `<li>
                                <a href="#view22" class="shadow-box" aria-controls="view22" data-bs-toggle="tab"><img src="${product.imgPath2}" alt="" /></a>
                            </li>`: ``
                        }
                        ${product.imgPath3  !== undefined && product.imgPath3 !== null && product.imgPath3 !== "" ?
                            `<li>
                                <a href="#view33" class="shadow-box" aria-controls="view33" data-bs-toggle="tab"><img src="${product.imgPath3}" alt="" /></a>
                            </li>`: ``
                        }
                        
                        <li>
                            <div style="font-size: 40px; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #333333;" onclick="selectActiveImage('down')">
                                <i class="fa fa-chevron-down"></i>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="tab-content active-portfolio-area pos-rltv">
            <div class="social-tag">
<!--                <a href="#"><i class="zmdi zmdi-share"></i></a>-->
                <div class="product-label red" id="discountLabel">
                ${stock.validDiscount ? `<div class="new">Sale <span>${stock.discountValue}%</span></div>` : ``}
                </div>
            </div>
           
            <div role="tabpanel" class="tab-pane active" id="view11">
                <div class="product-img">
                    <a class="fancybox" data-fancybox-group="group" href="${product.imgPath1}"><img src="${product.imgPath1}" alt="Single portfolio" style="object-fit: cover"/></a>
                </div>
            </div>
            ${product.imgPath2 !== undefined && product.imgPath2 !== null && product.imgPath2 !== "" ?
                `<div role="tabpanel" class="tab-pane" id="view22">
                    <div class="product-img">
                        <a class="fancybox" data-fancybox-group="group" href="${product.imgPath2}"><img src="${product.imgPath2}" alt="Single portfolio" style="object-fit: cover"/></a>
                    </div>
                </div>` : ``
            }
            ${product.imgPath3 !== undefined && product.imgPath3 !== null && product.imgPath3 !== "" ?
                `<div role="tabpanel" class="tab-pane" id="view33">
                    <div class="product-img">
                        <a class="fancybox" data-fancybox-group="group" href="${product.imgPath3}"><img src="${product.imgPath3}" alt="Single portfolio" style="object-fit: cover"/></a>
                    </div>
                </div>`:``
            }
        </div>
    `;

    let productDataContainer = document.getElementById("productDataContainer");
    productDataContainer.innerHTML = `
        <div class="sp-top-des">
            <h3>${product.title} <span>(${product.brandName})</span></h3>
            <div class="prodcut-ratting-price">
                <div class="prodcut-ratting">
                    <a href="#" tabindex="0"><i class="fa fa-star-o"></i></a>
                    <a href="#" tabindex="0"><i class="fa fa-star-o"></i></a>
                    <a href="#" tabindex="0"><i class="fa fa-star-o"></i></a>
                    <a href="#" tabindex="0"><i class="fa fa-star-o"></i></a>
                    <a href="#" tabindex="0"><i class="fa fa-star-o"></i></a>
                </div>
                <div class="prodcut-price">
                    ${stock.validDiscount ? `
                        <div class="new-price" id="newPrice">Rs.${stock.newPrice.toFixed(2)}</div>
                        <div class="old-price"> <del id="oldPrice">Rs.${stock.price.toFixed(2)}</del> </div>
                        ` :
                        `<div class="new-price" id="newPrice">Rs.${stock.price.toFixed(2)}</div>`
                    }
                </div>
            </div>
        </div>
        <div class="sp-des">
            <p>
                This product blends comfort, style, and practicality to suit any moment of your day. Crafted with attention to detail and made to last, it’s a wardrobe essential that keeps you looking good.
            </p>
        </div>
        <div class="sp-bottom-des">
            <div class="single-product-option">
                <div class="sort product-type">
                    <label>Color: </label>
                    <select id="colorSelect" onchange="changeStock(1, ${product.productId})">
                    </select>
                </div>
                <div class="sort product-type">
                    <label>Size: </label>
                    <select id="sizeSelect" onchange="changeStock(2, ${product.productId})">
                    </select>
                </div>
            </div>
            <div class="quantity-area">
                <label>Qty :</label>
                <div class="cart-quantity">
                    <div class="product-qty">
                        <div class="cart-quantity">
                            <div class="cart-plus-minus">
                                <div class="dec qtybutton">-</div>
                                <input value="1" name="qtybutton" class="cart-plus-minus-box" type="number" min="1" max="${stock.qty}" 
                                    oninput="if(this.value>this.max) this.value=this.max; if(this.value<this.min) this.value=this.min;" id="qtyInput">
                                <div class="inc qtybutton">+</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="social-icon socile-icon-style-1">
                <ul>
                    <li id="cartBtn"><a href="javascript:void(0)" data-tooltip="Add To Cart" class="add-cart add-cart-text" data-placement="left" tabindex="0" onclick="addProductToCart(${stock.stockId}, parseInt(document.getElementById('qtyInput').value))">Add To Cart<i class="fa fa-cart-plus"></i></a></li>
                    <li id="wishlistBtn"><a href="javascript:void(0)" data-tooltip="Wishlist" class="w-list" tabindex="0" onclick="addProductToWishlist(${stock.stockId})"><i class="fa fa-heart-o"></i></a></li>
                </ul>
            </div>
        </div>
    `;

    let productDescriptionContainer = document.getElementById("descriptionContainer");
    productDescriptionContainer.innerHTML = product.description;

    const sizeSelect = document.getElementById("sizeSelect");
    const colorSelect = document.getElementById("colorSelect");

    renderDropdowns(colorSelect, data.colors, 'name', stock.colorId)
    renderDropdowns(sizeSelect, data.sizes, 'name', stock.sizeId)
    $(document).on('click', '.cart-plus-minus .inc', function() {
        var $input = $(this).siblings('input');
        var currentVal = parseInt($input.val()) || 0;
        var maxVal = parseInt($input.attr('max')) || Infinity; // get max value from attribute
        if (currentVal < maxVal) { // only increment if below max
            $input.val(currentVal + 1);
            $input.trigger('change');
        }
    });
    $(document).on('click', '.cart-plus-minus .dec', function() {
        var $input = $(this).siblings('input');
        var currentVal = parseInt($input.val()) || 0;
        if (currentVal > 1) { // do not go below 1
            $input.val(currentVal - 1);
            $input.trigger('change');
        }
    });
}
function selectActiveImage(direction) {
    // select only thumbnail <a> elements (ignore arrow divs)
    const anchors = Array.from(document.querySelectorAll('.thumbnail-carousel ul li a[href]'));

    if (anchors.length === 0) return;

    // find current active thumbnail
    let activeIndex = anchors.findIndex(a => a.classList.contains('active'));
    if (activeIndex === -1) activeIndex = 0; // fallback to first

    // calculate next index with wrap-around
    if (direction === 'up') {
        activeIndex = (activeIndex - 1 + anchors.length) % anchors.length;
    } else { // down
        activeIndex = (activeIndex + 1) % anchors.length;
    }

    // remove active class from all thumbnails
    anchors.forEach(a => a.classList.remove('active'));

    // set new active
    const newActive = anchors[activeIndex];
    newActive.classList.add('active');

    // scroll thumbnail into view
    newActive.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'nearest' });

    // update main tab content
    const panes = document.querySelectorAll('.tab-content .tab-pane');
    panes.forEach(p => p.classList.remove('active'));

    const targetId = newActive.getAttribute('href');
    if (targetId) {
        const targetPane = document.querySelector(targetId);
        if (targetPane) targetPane.classList.add('active');
    }
}

async function changeStock(checkId, pId){
    //checkId = 1 -> Color Select
    //checkId = 2 -> Size Select
    let colorId = document.getElementById("colorSelect").value;
    let sizeId = document.getElementById("sizeSelect").value;

    try {
        const response = await fetch(`api/products/change-single-view-stock?pId=${pId}&cId=${colorId}&sId=${sizeId}&checkId=${checkId}`);
        if(response.ok){
            const data = await response.json();
            console.log(data);
            updateSingleProductUI(data.stock)

            if(checkId === 1 && data.sizes !== undefined && data.sizes.length > 0){
                let sizeSelect = document.getElementById("sizeSelect");
                sizeSelect.innerHTML = ``;
                renderDropdowns(sizeSelect, data.sizes, 'name', data.stock.sizeId);
            }
        }else{
            notify("Product Data Loading Failed!");
            // return null;
        }
    }catch (e){
        notify(e.message);
        // return null;
    }
}
function updateSingleProductUI(stock) {
    // Update price + discount
    const discountLabel = document.getElementById("discountLabel");
    let newPriceElem = document.getElementById("newPrice");
    let oldPriceElem = document.getElementById("oldPrice");

    let wishlistBtn = document.getElementById("wishlistBtn");
    if (wishlistBtn) {
        wishlistBtn.innerHTML = `
            <a href="#" data-tooltip="Wishlist" class="w-list" tabindex="0"
               onclick="addProductToWishlist(${stock.stockId})">
                <i class="fa fa-heart-o"></i>
            </a>
        `;
    }

    let cartBtn = document.getElementById("cartBtn");
    if (cartBtn) {
        cartBtn.innerHTML = `
            <a href="javascript:void(0)" data-tooltip="Add To Cart" class="add-cart add-cart-text" data-placement="left" tabindex="0" 
                onclick="addProductToCart(${stock.stockId}, parseInt(document.getElementById('qtyInput').value))">
                Add To Cart <i class="fa fa-cart-plus"></i>
            </a>
        `;
    }

    let saleDiv = document.createElement("div");
    saleDiv.className = "new";
    saleDiv.innerHTML = `Sale <span>${stock.discountValue}%</span>`;

    if (stock.discountId !== 1) {
        // Discount Active
        newPriceElem.innerHTML = "Rs." + stock.newPrice.toFixed(2);

        if (!oldPriceElem) {
            // If old price div not exists → add it
            let priceDiv = newPriceElem.parentElement;
            priceDiv.innerHTML += `<div class="old-price"><del id="oldPrice"></del></div>`;
            oldPriceElem = document.getElementById("oldPrice");
        }
        oldPriceElem.innerHTML = "Rs." + stock.price.toFixed(2);

        discountLabel.appendChild(saleDiv);
    } else {
        // No Discount
        newPriceElem.innerHTML = "Rs." + stock.price.toFixed(2);
        if (oldPriceElem) oldPriceElem.parentElement.remove(); // remove old price

        discountLabel.innerHTML = ``;
    }

    // Update Qty max
    let qtyInput = document.getElementById("qtyInput");
    qtyInput.max = stock.qty;

    // Auto reduce qty if more than new max
    if (parseInt(qtyInput.value) > stock.qty) {
        qtyInput.value = stock.qty;
    }
}

async function getRelatedProduct(brandId){
    //Related Product For Category URL : api/products/sort?categoryId=1&minPrice=800&maxPrice=4850&order=5&limit=15&page=1
    //Related Product For Sub-Category URL : api/products/sort?subCategoryId=1&minPrice=800&maxPrice=4850&order=5&limit=15&page=1
    try {
        const response = await fetch(`api/products/sort?brandId=${brandId}&minPrice=0&maxPrice=50000&order=5&limit=8&page=1`);
        if(response.ok){
            const data = await response.json();
            console.log(data);

            renderRelatedProductCards(data);
        }else{
            notify("Related Product Data Loading Failed!");
        }
    }catch (e){
        notify(e.message);
    }
}
function renderRelatedProductCards(data){
    const container = document.getElementById("relatedProductsContainer");
    const mobileViewContainer = document.getElementById("shopProductsContainer");
    container.innerHTML = ``;
    mobileViewContainer.innerHTML = ``;

    // Loop through products
    data.products.forEach(product => {
        const card = document.createElement("product-card");
        card.data = product; // send product object to your ProductCard element
        container.appendChild(card);

        const card2 = document.createElement("product-card");
        card2.data = product; // send product object to your ProductCard element
        const wrapper = document.createElement("div");
        wrapper.className = "col-lg-3 col-md-6 item";
        wrapper.appendChild(card2);
        mobileViewContainer.appendChild(wrapper);
    });

    if ($.fn.slick) {
        $('.new-arrival-slider-active').slick('unslick'); // remove old initialization
        $('.new-arrival-slider-active').slick({
            slidesToShow: 4,
            slidesToScroll: 1,
            arrows: true,
            dots: false,
            autoplay: true,
            autoplaySpeed: 3000,
            responsive: [
                { breakpoint: 1200, settings: { slidesToShow: 3 } },
                { breakpoint: 992, settings: { slidesToShow: 2 } },
                { breakpoint: 576, settings: { slidesToShow: 1 } },
            ]
        });
    }
}







