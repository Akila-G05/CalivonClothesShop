window.addEventListener('load', async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        $(document).on('click', '.cart-plus-minus .inc', function() {
            var $input = $(this).closest('.cart-plus-minus').find('input.qtyInput');
            var currentVal = parseInt($input.val()) || 0;
            var maxVal = parseInt($input.attr('max')) || Infinity;
            if (currentVal < maxVal) {
                $input.val(currentVal + 1).trigger('change');
            }
        });
        $(document).on('click', '.cart-plus-minus .dec', function() {
            var $input = $(this).closest('.cart-plus-minus').find('input.qtyInput');
            var currentVal = parseInt($input.val()) || 0;
            if (currentVal > 1) {
                $input.val(currentVal - 1).trigger('change');
            }
        });
    }finally {
        Notiflix.Loading.remove(500);
    }

});

async function getCartProducts(){
    try {
        const response = await fetch(`api/products/load-cart-data`);
        if(response.ok){
            const data = await response.json();
            if(data.status){
                loadCartTable(data);
            }else{
                loadCartEmptyDesign()
            }
        }else{
            notify("Product Data Loading Failed!");
            // return null;
        }
    }catch (e){
        notify(e.message);
        // return null;
    }
} // Using Header getHeaderCartProducts() Function For Now

function loadCartEmptyDesign(){
    const emptyDesign = document.getElementById("emptyDesignContainer");
    emptyDesign.innerHTML = `
        <div class="col-12 border border-3 text-center p-5 border-top-0" >
            <div class="row">
                <h1>Your Cart Is Empty!</h1>
            </div>
        </div>
    `;

    const tbody = document.getElementById("cartProductTableBody");
    tbody.innerHTML = ""; // clear table

    document.getElementById("subTotal").textContent = "Rs. 00.0";
    document.getElementById("shipping").textContent = "Rs. 0.00";
    document.getElementById("total").textContent = "Rs. 0.00";
}
function loadCartTable(data){
    const emptyDesign = document.getElementById("emptyDesignContainer");
    emptyDesign.innerHTML = "";

    const tbody = document.getElementById("cartProductTableBody");
    tbody.innerHTML = ""; // clear table

    data.cartItems.forEach(async (items, index) => {
        let product = items.product;
        let stock = items.stock;
        // Add row
        let tr = document.createElement("tr");
        tr.classList.add("cart_item")
        if(stock.statusId === 3){
            tr.style.cssText = "background: rgba(255,51,51,0.42);";
        }
        tr.innerHTML = `
            <td class="item-img">
                <div class="img-wrapper" style="position: relative; display: inline-block;">
                    ${stock.statusId === 3 ? `
                        <div class="product-label" style="position: absolute; right: 130px; top: -24px; z-index: 3000">
                            <div class="new">Unavailable</div>
                        </div>
                    ` : ""}
                    <a href="single-product.html?id=${stock.stockId}">
                        <img src="${product.imgPath1}" >
                    </a>
                </div>
            </td>
            <td class="item-title"> <a href="single-product.html?id=${stock.stockId}">${product.title}</a></td>
            <td class="">
                <div style="display: flex; align-items: center; justify-content: center">
                    <div style="width: 20px; height: 20px; background-color:${stock.colorCode}; border: 1px solid #ccc;"></div>&nbsp; &nbsp;${stock.sizeName}
                </div>
            </td>
            <td class="item-price">
              Rs. ${stock.discountId === 1 ? Number(stock.price).toFixed(2) : (stock.newPrice ? Number(stock.newPrice).toFixed(2) : Number(stock.price).toFixed(2))}
            </td>
            <td class="item-qty text-danger fw-bold">
                ${stock.qty > 0 ? `
                    <div class="cart-quantity">
                        <div class="product-qty">
                            <div class="cart-quantity">
                                <div class="cart-plus-minus">
                                    <div class="dec qtybutton">-</div>
                                    <input value="${items.cartQty}" name="qtybutton" class="cart-plus-minus-box qtyInput" type="number" min="1" max="${stock.qty}" 
                                        oninput="if(this.value>this.max) this.value=this.max; if(this.value<this.min) this.value=this.min;">
                                    <div class="inc qtybutton">+</div>
                                </div>
                            </div>
                        </div>
                    </div>
                ` : `Out of Stock`}
            </td>
            <td class="total-price"><strong> Rs. ${Number(items.total).toFixed(2)}</strong></td>
            <td class="remove-item"><a href="javascript:void(0)" onclick="deleteCartItem(${stock.stockId})"><i class="fa fa-trash-o"></i></a></td>
        `;

        tbody.appendChild(tr);
    });

    const cartBtnContainer = document.getElementById("cardBtnContainer");
    cartBtnContainer.innerHTML = `
        <a href="javascript:void(0)" class="btn-def btn2" id="updateCartBtn">Update Cart</a>
        <a href="shop.html" class="btn-def btn2">Continue Shopping</a>
    `;
    document.getElementById("updateCartBtn").addEventListener("click", async () => {
        // regenerate cartArray from table rows
        const rows = document.querySelectorAll("#cartProductTableBody tr.cart_item");
        const cartArray = [];

        rows.forEach(row => {
            const stockId = Number(row.querySelector(".remove-item a").getAttribute("onclick").match(/\d+/)[0]);row.querySelector(".qtyInput");
            const qtyInput = row.querySelector(".qtyInput"); // ← FIXED
            const qty = qtyInput ? Number(qtyInput.value) : 0;
            cartArray.push({ stockId, qty });
        });

        await updateCart(cartArray);
        // console.log(cartArray)

        if (window.headerContentInstance) {
            await window.headerContentInstance.getHeaderCartProducts();
        }
    });

    document.getElementById("subTotal").textContent = "Rs. " + data.cartTotal.toFixed(2);
    document.getElementById("shipping").textContent = "Rs. " + data.shippingPrice.toFixed(2);
    document.getElementById("total").textContent = "Rs. " + data.grandTotal.toFixed(2);
}
async function deleteCartItem(id){
    // reportInit();
    // Notiflix.Loading.pulse("Processing...");

    try {
        const response = await fetch("api/products/delete-cart?id=" + id, {
            method: "DELETE",
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                notifySuccess(data.message);

                if (window.headerContentInstance) {
                    await window.headerContentInstance.getHeaderCartProducts();
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

async function updateCart(cartArray){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    const dataObj = {
        cartData: cartArray
    };

    try{
        const response = await fetch("api/products/update-cart", {
           method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        });

        if(response.ok){
            const data = await response.json();

            if(data.status){
                notifySuccess(data.message)
                if (window.headerContentInstance) {
                    await window.headerContentInstance.getHeaderCartProducts();
                }
            }else{
                notify(data.message)
            }
        }else{
            notify("Cart update failed!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}



