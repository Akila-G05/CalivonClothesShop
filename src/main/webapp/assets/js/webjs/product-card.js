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

class ProductCard extends HTMLElement {

    constructor() {
        super();
    }

    set data(product) {
        this.render(product);
    }

    render(product) {
        this.innerHTML = `
            <div class="product-item">
                <!-- single product start-->
                <div class="single-product">
                    <div class="product-img">
                        ${product.stock.qty > 0 ?
                            `<div class="product-label red">
                            ${product.stock.discountId !== 1 ? `<div class="new">Sale <span>${product.stock.discountValue}%</span></div>` : ``}
                            </div>`
                        : 
                            `<div class="product-label">
                            <div class="new">Out Of Stock</div>
                            </div>`
                        }
                        <div class="single-prodcut-img  product-overlay pos-rltv">
                            <a href="single-product.html?id=${product.stock.stockId}"> 
                            <img alt="" src="${product.imgPath1}" class="primary-image" style="height: 300px; object-fit: cover">
                            <img alt="" src="" class="secondary-image"> </a>
                        </div>
                        <div class="product-icon socile-icon-tooltip text-center">
                            <ul>
                                <li>
                                    <a href="javascript:void(0)" data-tooltip="Add To Cart" class="add-cart" data-placement="left" onclick="addProductToCart(${product.stock.stockId}, 1)">
                                        <i class="fa fa-cart-plus"></i>
                                    </a>
                                </li>
                                <li>
                                    <a href="javascript:void(0)" data-tooltip="Wishlist" class="w-list" onclick="addProductToWishlist(${product.stock.stockId})">
                                        <i class="fa fa-heart-o"></i>   
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="product-text">
                        <div class="prodcut-name"> 
                            <a href="single-product.html?id=${product.stock.stockId}">${product.title}</a> 
                        </div>
                        <div class="prodcut-ratting-price">
                            <div class="prodcut-ratting">
                                <a href="#"><i class="fa fa-star"></i></a>
                                <a href="#"><i class="fa fa-star"></i></a>
                                <a href="#"><i class="fa fa-star"></i></a>
                                <a href="#"><i class="fa fa-star"></i></a>
                                <a href="#"><i class="fa fa-star-o"></i></a>
                            </div>
                            <div class="prodcut-price">
                                ${product.stock.discountId !== 1 ? `
                                <div class="new-price">Rs.${product.stock.newPrice.toFixed(2)}</div>
                                <div class="old-price"> <del>Rs.${product.stock.price.toFixed(2)}</del> </div>
                                ` : 
                                `<div class="new-price">Rs.${product.stock.price.toFixed(2)}</div>`}
                            </div>
                        </div>
                    </div>
                </div>
                <!-- single product end-->
            </div>
        `;
    }
}

customElements.define("product-card", ProductCard);
