window.baseGrandTotal = 0;
window.shippingPrice = 0;
window.deliveryPrice = 0;
let deliveryData;

async function loadDeliveryTypes(){
    try{
        const response = await fetch("api/data/load-delivery-types");
        if(response.ok){
            const data = await response.json()

            deliveryData = data;

            const parent = document.querySelector(".pay-type-total");
            parent.innerHTML = ""; // clear existing

            data.deliveryTypes.forEach((dtype, index) => {
                const div = document.createElement("div");
                div.classList.add("pay-type");

                const radioId = "pay-type-" + dtype.id;

                div.innerHTML = `
                <input type="radio" id="${radioId}" name="pay" value="${dtype.id}" ${index === 0 ? "checked" : ""} onclick="changeDeliveryType(JSON.parse(decodeURIComponent('${encodeURIComponent(JSON.stringify(dtype))}')))">
                <label for="${radioId}">${dtype.name} ${dtype.price > 0 ? "(Cash payment fee: Rs. " + dtype.price.toFixed(2) + ")" : ""}</label>
            `;

                parent.appendChild(div);
            });

            return data;
        }else{
            notify("DeliveryTypes Loading Failed!");
            return null;
        }
    }catch (e) {
        notify(e.message);
        return null;
    }
}

function updateTotals() {
    const total = window.baseGrandTotal + window.shippingPrice + window.deliveryPrice;

    document.getElementById("shipping").innerText = "Rs. " + window.shippingPrice.toFixed(2);
    document.getElementById("grandTotal").innerText = "Rs. " + total.toFixed(2);
}

function changeDeliveryType(type) {
    window.deliveryPrice = type.price || 0;
    updateTotals();
}

function changeDistrict() {
    const districtSelect = document.getElementById("districtSelect");
    const selectedId = Number(districtSelect.value);

    if (selectedId !== 0 && Array.isArray(districtData)) {
        const result = districtData.find(item => item.id === selectedId);
        window.shippingPrice = result ? result.price : 0;
    } else {
        window.shippingPrice = 0;
    }

    updateTotals();
}

async function handleCheckoutFlow(checkStatus){
    const stockModalEl = document.getElementById("cartUserCheckoutModal");
    const stockModal = new bootstrap.Modal(stockModalEl);

    try {
        if(checkStatus){
            await loadDeliveryTypes();
            if (session.message === "Session found" && session.userType === "user") {
                renderOrderTables(cartData);
            } else if (session.message === "Session found" && session.userType === "cart") {
                window.location = "complete-order.html";
            } else {
                notify("Please add at least one item to cart!")
            }
        }
    } finally {
        Notiflix.Loading.remove(500);
        await new Promise(res => setTimeout(res, 500));

        if(checkStatus) {
            if (session.message === "Session found" && session.userType === "user") {
                stockModal.show();
            }
        }
    }
}
async function openUserCheckoutModal(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    let data = cartData;
    if (window.headerContentInstance) {
        data = await window.headerContentInstance.getHeaderCartProducts();
    }
    console.log(data)
    if (!data || !Array.isArray(data.cartItems)) {
        Notiflix.Loading.remove();
        notify("Your cart is empty. Please add products to continue.");
        return;
    }

    let checkStatus = true
    data.cartItems.forEach((items) => {
        if(items.stock.statusId === 3){
            notify("Some items in your cart are not available right now. Please remove those items to proceed.");
            checkStatus = false;
        }
    });

    await handleCheckoutFlow(checkStatus);

} //Cart Page Checkout Button
function renderOrderTables(data){
    const body = document.getElementById("userOrderTableBody");
    body.innerHTML = "";

    window.baseGrandTotal = data.grandTotal;
    console.log(data)

    data.cartItems.forEach(async (items, index) => {
        let product = items.product;
        let stock = items.stock;

        let tr = document.createElement("tr");
        tr.classList.add("cart_item", "check-item", "prd-name");
        tr.innerHTML = `
            <td class="ctg-type print-tr"> ${product.title} (${stock.colorName} / ${stock.sizeName}) ×
                <span>${items.cartQty}</span></td>
            ${stock.discountId === 1 ?
            `<td class="cgt-des print-tr"> Rs. ${items.total.toFixed(2)} </td>`
            :
            `<td class="cgt-des print-tr"> Rs. ${items.total.toFixed(2)}&nbsp;
                     <del>${items.totalWithoutDiscount.toFixed(2)}</del>
                </td>`
        }
        `;
        body.appendChild(tr);
    });

    body.innerHTML += `
        <tr class="cart_item">
            <td class="ctg-type"> Subtotal</td>
            ${data.cartTotalWithoutDiscount >= 0 ?
        `<td class="cgt-des">Rs. ${data.cartTotal.toFixed(2)}&nbsp;
                    <del>${data.cartTotalWithoutDiscount.toFixed(2)}</del>
                </td>`
        :
        `<td class="cgt-des"> Rs. ${data.cartTotal.toFixed(2)}</td>`
    }
        </tr>
        <tr class="cart_item">
            <td class="ctg-type"> Shipping</td>
            <td class="cgt-des" id="shipping">Rs. ${data.shippingPrice.toFixed(2)}</td>
        </tr>
        <tr class="cart_item">
        <td class="ctg-type crt-total">Total</td>
        <td class="cgt-des prc-total" id="grandTotal">Rs. ${data.grandTotal.toFixed(2)}</td>
        </tr>
    `;
}

async function checkOutUserOrder() {
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    try{
        const selectedPay = document.querySelector('input[name="pay"]:checked');

        if (!selectedPay) {
            notify("Please select a payment method!");
            return;
        }
        // console.log(deliveryData)
        const selectedId = Number(selectedPay.value);

        // Find selected delivery type from array
        const selectedType = deliveryData.deliveryTypes.find(t => t.id === selectedId);

        if (!selectedType) {
            notify("Invalid Delivery Type!");
            return;
        }
        // Decision
        if (selectedType.id === 2) {
            // COD
            if (session.message === "Session found" && session.userType === "user") {
                await proceedCODPayment(2);
            }else if(session.message === "Session found" && session.userType === "guest"){
                await proceedCODPayment(2);
            }else if(session.message === "Session found" && session.userType === "cart"){
                await createGuestAccount(2);
            }
        } else if (selectedType.id === 1) {
            // Card payment
            notifySuccess("User selected Card Payment");
            // printOrder();
            proceedCardPayment(1);
        }
    }finally {
        Notiflix.Loading.remove();
    }
}

async function proceedCODPayment(dTypeId){ //Payment Finalizing Part
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    const dataObj={
        cartTotal: cartData.cartTotal,
        dTypeId: dTypeId
    };

    try {
        const response = await fetch("api/checkout/cod", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        });
        if(response.ok){
            const data = await response.json();
            console.log(data)
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    async () => {
                        printOrder();
                    }
                );
            }else{
                notify(data.message);
            }
        }else {
            notify("Checkout Process failed! ");
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
let payHereData;
async function proceedCardPayment(dTypeId){
    reportInit();
    Notiflix.Loading.pulse("Redirecting to payment...");

    const dataObj={
        cartTotal: cartData.cartTotal,
        dTypeId: dTypeId
    };

    await new Promise(resolve => setTimeout(resolve, 200));

    try{
        const response = await fetch("api/checkout/payhere", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                console.log(data.PayHere)
                payHereData = data.PayHere
                payhere.startPayment(data.PayHere);
            }else{
                notify(data.message);
            }
        }else{
            notify("Order Data Loading Failed!");
        }
    }catch (e) {
        Notiflix.Loading.remove();
        notify(e.message);
    }
}
async function devCardPayment() {
    reportInit();
    Notiflix.Loading.pulse("Finalizing payment...");

    console.log(payHereData.order_id)

    try {
        const response = await fetch("/api/payments/dev-notify?orderId=" + payHereData.order_id, {
            method: "PUT"
        });

        if(response.ok) {
            const data = await response.json();

            if (data.status) {
                notify("Payment successful!");
                printOrder();
            } else {
                notify(data.message);
            }
        }
    } catch (e) {
        notify("Something went wrong. Payment finalise process failed!");
    } finally {
        Notiflix.Loading.remove();
    }
}

payhere.onCompleted = async function(){
    try {
        console.log("completed : ", payHereData)
        await devCardPayment()
    }catch (e){
        notify("Payment finalise failed. Something went wrong!")
    }
};
payhere.onDismissed = function(){
    console.log("dismissed : ",  payHereData)
    notify("Payment cancelled!");
};
payhere.onError = function(error){
    console.log("error : ", payHereData)
    notify("Payment error: " + error);
};

function printOrder() {
    const printContents = document.getElementById("printableOrderArea").innerHTML;
    const originalContents = document.body.innerHTML;

    // Open a temporary print layout
    document.body.innerHTML = `
        <html>
        <head>
            <title>Order Summary</title>
            <style>
                .print-wrapper {
                    width: 800px;              /* or 100%, or 210mm for A4 */
                    margin: 0 auto;            /* ⬅ centers horizontally */
                }
            
                .print-tr{
                    font-size: 12px;
                }
                
                .print-title-div{
                    color: #303030;
                }
                
                .print-title{
                    color: #303030;
                    font-weight: bolder;
                    font-size: 30px;
                    text-align: center;
                }
                
                table{
                    width: 100%;
                }
                
                @media print {
                    body {
                        margin: 0;
                        padding: 0;
                    }
                
                    .print-wrapper {
                        margin: 0 auto !important;
                    }
                }
            </style>
        </head>
        <body>
            <div class="print-wrapper">
                ${printContents}
            </div>
        </body>
        </html>
    `;

    window.print();

    // Restore original page to avoid breaking site
    document.body.innerHTML = originalContents;

    location.reload(); // reattach JS events (important)
}