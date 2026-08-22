

window.addEventListener('DOMContentLoaded', async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        await getWishlistProducts();
    }finally {
        Notiflix.Loading.remove(500);
    }

});

async function getWishlistProducts(){
    try {
        const response = await fetch(`api/products/load-wishlist-data`);
        if(response.ok){
            const data = await response.json();
            if(data.status){
            // console.log(data);
                loadWishlistTable(data)
            }else{
                loadEmptyDesign();
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
function loadEmptyDesign(){
    const emptyDesign = document.getElementById("emptyDesignContainer");
    emptyDesign.innerHTML = `
        <div class="col-12 border border-3 text-center p-5 border-top-0" >
            <div class="row">
                <h1>Your Wishlist Is Empty!</h1>
            </div>
        </div>
    `;

    const tbody = document.getElementById("wishlistProductTableBody");
    tbody.innerHTML = ""; //
}
function loadWishlistTable(data) {
    const emptyDesign = document.getElementById("emptyDesignContainer");
    emptyDesign.innerHTML = ""; // remove emptyTableDesign

    const tbody = document.getElementById("wishlistProductTableBody");
    tbody.innerHTML = ""; // clear table

    if (Array.isArray(data.wishlistItems) && data.wishlistItems.length > 0) {
        data.wishlistItems.forEach(async (items, index) => {
            let product = items.product;
            let stock = items.stock;
            // Add row
            let tr = document.createElement("tr");
            tr.classList.add("cart_item")
            tr.innerHTML = `
            <td class="item-img">
                <a href="single-product.html?id=${stock.stockId}" style="object-fit: cover;"><img src="${product.imgPath1}" alt=""> </a>
            </td>
            <td class="item-title"> <a href="single-product.html?id=${stock.stockId}">${product.title}</a></td>
            <td class="">
                <div style="display: flex; align-items: center; justify-content: center">
                    <div style="width: 20px; height: 20px; background-color: ${stock.colorCode}; border: 1px solid #ccc;"></div>
                </div>
            </td>
            <td class="item-price">${stock.sizeName}</td>
            <td class="item-price">
              Rs. ${stock.discountId === 1 ? Number(stock.price).toFixed(2) : (stock.newPrice ? Number(stock.newPrice).toFixed(2) : Number(stock.price).toFixed(2))}
            </td>
            <td class="item-qty">
                ${stock.qty > 0 ? `In Stock` : `Sold out`}
            </td>
            <td class="total-price"><a class="btn-def btn2" href="javascript:void(0)" onclick="addProductToCart(${stock.stockId}, 1)">Add To Cart</a></td>
            <td class="remove-item"><a href="javascript:void(0)" onclick="deleteWishlist(${stock.wishlistId})"><i class="fa fa-trash-o"></i></a></td>
        `;

            tbody.appendChild(tr);
        });
    }
}

async function deleteWishlist(id){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    try {
        const response = await fetch("api/products/delete-wishlist?id=" + id, {
            method: "DELETE",
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                notifySuccess(data.message);

                getWishlistProducts();
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
