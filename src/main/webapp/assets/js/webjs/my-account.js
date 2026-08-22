window.addEventListener("load", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    try {
        await loadProvinces();
        await loadModalProvinces();
        await loadUserData();
        await loadAddress();
    }finally {
        Notiflix.Loading.remove(500);
    }
});

document.getElementById("address_anchor").addEventListener("click", () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    Notiflix.Loading.remove(500);
})
document.getElementById("personal_info_anchor").addEventListener("click", () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    Notiflix.Loading.remove(500);
})
document.getElementById("orders_anchor").addEventListener("click", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    try{
        await sortOrders();
    }finally {
        Notiflix.Loading.remove(500);
    }
})
document.getElementById("signout_anchor").addEventListener("click", async () => {
    await signOut();
})

const addressModalEl = document.getElementById("productModal");
const addressModal = new bootstrap.Modal(addressModalEl);
function openAddressModal() {
    // let addressModal = new bootstrap.Modal(document.getElementById("productModal"));
    addressModal.show();
}

function renderDropdowns(selector, list, suffix){
    selector.innerHTML = `<option value="0">Select</option>`
    list.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[suffix];
        selector.appendChild(option);
    })
}

async function loadProvinces(){
    try{
        const response = await fetch("api/data/load-provinces");
        if(response.ok){
            const data = await response.json();

            const province = document.getElementById("provinceSelect");
            renderDropdowns(province, data.provinces, 'name')
        }else{
            notify("Province Loading Failed!");
        }
    }catch (e) {
        notify(e.message);
    }
}
async function loadDistricts(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    const id = document.getElementById("provinceSelect").value;

    try{
        const response = await fetch("api/data/load-districts?id="+id);
        if(response.ok){
            const data = await response.json();

            const district = document.getElementById("districtSelect");
            renderDropdowns(district, data.districts, 'name')
        }else{
            notify("District Loading Failed!");
        }
    }catch (e) {
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function loadCities(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    const id = document.getElementById("districtSelect").value;

    try {
        const response = await fetch("api/data/load-cities?id="+id);
        if(response.ok){
            const data =  await response.json();
            const citySelect = document.getElementById("citySelect");
            renderDropdowns(citySelect, data.cities, 'name')
        }else{
            notify("City Loading Failed!");
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}

async function loadModalProvinces(){
    try{
        const response = await fetch("api/data/load-provinces");
        if(response.ok){
            const data = await response.json();

            const provinceSelect = document.getElementById("provinceSelectModal");
            renderDropdowns(provinceSelect, data.provinces, 'name')
        }else{
            notify("Province Loading Failed!");
        }
    }catch (e) {
        notify(e.message);
    }
}
async function loadModalDistricts(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    const id = document.getElementById("provinceSelectModal").value;

    try{
        const response = await fetch("api/data/load-districts?id="+id);
        if(response.ok){
            const data = await response.json();

            const districtSelect = document.getElementById("districtSelectModal");
            renderDropdowns(districtSelect, data.districts, 'name')
        }else{
            notify("District Loading Failed!");
        }
    }catch (e) {
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function loadModalCities(){
    reportInit();
    Notiflix.Loading.pulse("Loading Data ...");

    const id = document.getElementById("districtSelectModal").value;

    try {
        const response = await fetch("api/data/load-cities?id="+id);
        if(response.ok){
            const data =  await response.json();
            const citySelectModal = document.getElementById("citySelectModal");
            renderDropdowns(citySelectModal, data.cities, 'name')
        }else{
            notify("City Loading Failed!");
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}

async function loadUserData(){
    try {
        const response = await fetch("api/profiles/profile-data");
        if(response.ok){
            const data = await response.json();
            // console.log(data)

            if (session.userStatus !== "VERIFIED") {
                document.getElementById("headingThree").classList.remove("d-none");
            } else {
                document.getElementById("headingThree").classList.add("d-none");
            }

            document.getElementById("fname").value = data.user.firstName;
            document.getElementById("lname").value = data.user.lastName;
            document.getElementById("email").value = data.user.email;
            document.getElementById("currentPassword").value = data.user.password;

            document.getElementById("lineOne").value = data.user.lineOne ? data.user.lineOne : "";
            document.getElementById("lineTwo").value = data.user.lineTwo ? data.user.lineTwo : "";
            document.getElementById("postalCode").value = data.user.postalCode ? data.user.postalCode : "";
            document.getElementById("provinceSelect").value = data.user.provinceId ? data.user.provinceId : 0;
            await loadDistricts();
            document.getElementById("districtSelect").value = data.user.districtId ? data.user.districtId : 0;
            await loadCities();
            document.getElementById("citySelect").value = data.user.cityId ? data.user.cityId : 0;
            document.getElementById("mobile").value = data.user.mobile ? data.user.mobile : "";
        }else{
            notify("Profile Loading Data Failed!");
        }
    }catch (e){
        notify(e.message);
    }
}
async function updateUserData(){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    let firstName = document.getElementById("fname")
    let lastName = document.getElementById("lname")
    let currentPassword = document.getElementById("currentPassword")
    let newPassword = document.getElementById("newPassword")
    let confirmPassword = document.getElementById("confirmPassword")

    const userObj={
        firstName: firstName.value,
        lastName: lastName.value,
        password: currentPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value
    };

    try {
        const response = await fetch("api/profiles/update-profile", {
            method: "PUT",
            headers:{
                "Content-Type":"application/json"
            },
            body:JSON.stringify(userObj)
        });
        if(response.ok){
            const data = await response.json();
            // console.log(data);
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                );
                await loadUserData();
            }else{
                notify(data.message);
            }
        }else {
            notify("Personal info update failed! ");
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}

async function changePrimaryAddress(id){
    reportInit();
    Notiflix.Loading.pulse("Changing Address...");

    try {
        const response = await fetch("api/profiles/change-primary?id=" + id, {
            method: "PUT"
        });
        if(response.ok){
            const data = await response.json();

            Notiflix.Report.success(
                'Calivon',
                data.message,
                'Okay', //Button title
            );

            loadAddress();
            loadUserData();
        }else{
            notify("Something went wrong!")
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }

}

async function addNewAddress(id){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    let lineOne = document.getElementById("lineOneModal")
    let lineTwo = document.getElementById("lineTwoModal")
    let postalCode = document.getElementById("postalCodeModal")
    let provinceSelect = document.getElementById("provinceSelectModal")
    let districtSelect = document.getElementById("districtSelectModal")
    let citySelect = document.getElementById("citySelectModal")
    let mobile = document.getElementById("mobileModal")

    const addressObj={
        addressId: id,
        mobile: mobile.value,
        lineOne: lineOne.value,
        lineTwo: lineTwo.value,
        postalCode: postalCode.value,
        provinceId: provinceSelect.value,
        districtId: districtSelect.value,
        cityId: citySelect.value,
    };

    try {
        const response = await fetch("api/profiles/add-address", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(addressObj)
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay',
                    ()=>{
                        addressModal.hide();
                    }
                );

                if(id != 0){
                    changePrimaryAddress(id);
                }else{
                    loadAddress();
                    loadUserData();
                }
            }else{
                notify(data.message);
            }
        }else {
            notify("Adding new address failed! ");
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function updateAddress(id){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    let lineOne = document.getElementById("lineOne")
    let lineTwo = document.getElementById("lineTwo")
    let provinceSelect = document.getElementById("provinceSelect")
    let districtSelect = document.getElementById("districtSelect")
    let postalCode = document.getElementById("postalCode")
    let citySelect = document.getElementById("citySelect")
    let mobile = document.getElementById("mobile")

    const addressObj={
        addressId: id,
        mobile: mobile.value,
        lineOne: lineOne.value,
        lineTwo: lineTwo.value,
        postalCode: postalCode.value,
        cityId: citySelect.value,
        provinceId: provinceSelect.value,
        districtId: districtSelect.value,
    };

    try {
        const response = await fetch("api/profiles/update-address", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(addressObj)
        });
        if(response.ok){
            const data = await response.json();
            console.log(data);
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay',
                );
                if(id != 0){
                    changePrimaryAddress(id);
                }else{
                    loadAddress();
                    loadUserData();
                }
            }else{
                notify(data.message);
            }
        }else {
            notify("Address updating failed! ");
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}
async function deleteAddress(id){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    try {
        const response = await fetch("api/profiles/delete-address?id=" + id, {
            method: "DELETE",
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                );

                loadAddress();
                loadUserData();
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
async function loadAddress(){
    let container = document.getElementById("addressContainer");
    let updateBtnContainer = document.getElementById("updateBtnContainer");
    container.innerHTML = ``;
    updateBtnContainer.innerHTML = ``;

    try {
        const response = await fetch("api/profiles/addresses");
        if(response.ok){
            const data = await response.json();
            // console.log(data);

            let addressesCount = 0;
            data.addresses.forEach((address, index) => {
                let addressCardDesignHtml = ``;

                addressesCount += index;
                if(address.isPrimary) {
                    addressCardDesignHtml = `
                    <!-- Active Address Design -->
                    <div class="col-lg-3 col-md-6 mt-2 mt-lg-0">
                        <div class="shadow-box border border-1 pt-0 p-3" style="background-color: #fafafa;  height: 210px">
                            <div class="row">
                                <h5 style="background-color: #333333; padding: 8px; color: white; font-size: 17px;">Address ${index + 1}</h5>
                                <label style="height: 50px;">${address.lineOne}${address.lineTwo ? ", " + address.lineTwo : ""}</label>
                                <label style="font-weight: bold">${address.cityName}</label>
                                <label>${address.mobile}</label><br>
                                <div class="d-flex justify-content-end gap-3 mt-2">  
                                    <a class="link-primary text-end" href="#">Default Address</a>
                                </div>
                            </div>
                        </div>
                    </div>
                    `;

                    updateBtnContainer.innerHTML = `
                    <a class="btn-def btn2" href="javascript:void(0)" onclick="updateAddress(${address.id})">Update</a>
                    `;
                }
                if(!address.isPrimary){
                    addressCardDesignHtml = `
                    <!-- Deactive Address Design -->
                    <div class="col-lg-3 col-md-6 mt-2 mt-lg-0">
                        <div class="shadow-box border border-1 pt-0 p-3" style="background-color: #fafafa; height: 210px">
                            <div class="row">
                                <h5 style="background-color: #333333; padding: 8px; color: white; font-size: 17px;">Address ${index + 1}</h5>
                                <label style="height: 50px;">
                                    ${address.lineOne}${address.lineTwo ? ", " + address.lineTwo : ""}
                                </label>     
                                <label style="font-weight: bold">${address.cityName}</label>
                                <label>${address.mobile}</label>
                                
                                <!-- ROW WITH BOTH BUTTONS -->
                                <div class="d-flex justify-content-end gap-3 mt-2">
                                    <!-- Delete -->
                                    <a class="link-danger" style="cursor:pointer" onclick="deleteAddress(${address.id})">
                                        <i class="fa fa-trash"></i> Delete
                                    </a>
                                    <!-- Activate -->
                                    <a class="link-primary" style="cursor:pointer" onclick="changePrimaryAddress(${address.id})">
                                        Activate
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                }

                container.innerHTML += addressCardDesignHtml;
            });

            // console.log(addressesCount)
            if(addressesCount < 4 && session.userStatus === "VERIFIED"){
                let addNewAddressCardDesignHtml = `
                <!-- Add New Address Design -->
                <div class="col-lg-3 col-md-6 mt-2 mt-lg-0" onclick="openAddressModal()">
                    <div class="shadow-box border border-3 p-3 d-flex align-items-center justify-content-center"
                         style="background-color: #fafafa; height: 210px; cursor: pointer; ">

                        <i class="fa fa-plus" style="font-size: 60px; color: #333333;"></i>

                    </div>
                </div>
                `;

                container.innerHTML += addNewAddressCardDesignHtml;
            }
        }else{
            notify(data.message);
        }
    }catch (e){
        notify(e.message);
    }
}

async function sendVerificationCode(){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    try {
        const response = await fetch("api/profiles/send_vcode", {
            method: "PUT",
            headers:{
                "Content-Type":"application/json"
            },
        });
        if(response.ok){
            const data = await response.json();
            console.log(data);
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                );
            }else{
                notify(data.message);
            }
        }else {
            notify("Personal info update failed! ");
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}

//ORDER HISTORY HANDELING FUNCTIONS
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

let currentPage = 1;
async function sortOrders(page = 1) {
    currentPage = page || 1;

    // read UI safely (elements may be missing on some pages)
    const searchEl = document.getElementById("ordersSearchField");
    const orderByStatusEl = document.getElementById("statusSelect");

    const searchField = searchEl ? searchEl.value.trim() : "";
    const productPerPage = 10;
    const orderSelect = orderByStatusEl ? parseInt(orderByStatusEl.value, 10) || 0 : 0;
    console.log(orderSelect)

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
        if (!data.status) {
            notify(data.message || "No Orders found");
            // clear container and pagination
            // const c = document.getElementById("shopProductsContainer");
            // if (c) c.innerHTML = "";
            generatePagination(0, productPerPage, 1);
            return;
        }

        // document.getElementById("resultCount").innerHTML = data.totalProducts;
        // showFilteredItems(categories, pData);
        // // render product cards (you already have product-card custom element)
        renderOrderData(data);

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
function renderOrderData(data){
    const accordionContainer = document.getElementById("ordersAccordionContainer");
    accordionContainer.innerHTML = "";

    data.orders.forEach((order, index) => {
        const collapseId = `orderCollapse_${index}`;

        // ---- Render order items ----
        let itemsHtml = "";
        order.items.forEach(item => {
            let totalItemPrice = item.buyingPrice * item.qty;

            itemsHtml += `
                <div class="col-lg-12 item" style="border-color: #303030">
                    <!-- single product start-->
                    <div class="single-product single-product-list d-lg-flex d-block align-items-center"  style="border: 1px solid #ccc">
                        <div class="product-img">
                            ${item.validDiscount ? `
                                <div class="product-label red">
                                    <div class="new">SALE ${item.discountValue}%</div>
                                </div>
                            ` : ``}
                            
                            <div class="single-prodcut-img  product-overlay pos-rltv">
                                <a href="single-product.html?id=${item.stockId}">
                                    <img alt="" src="${item.imgPath1}" class="primary-image" >
                                    <img alt="" src="" class="secondary-image">
                                </a>
                            </div>
                        </div>
                        <div class="product-text prodcut-text-list fix d-flex flex-column justify-content-center">
                            <div class="prodcut-name list-name montserrat"> <a
                                   href="single-product.html?id=${item.stockId}">${item.title}</a>
                            </div>
                            <div class="prodcut-ratting-price">
                                <div class="prodcut-ratting list-ratting">
                                    <a href="#"><i class="fa fa-star-o"></i></a>
                                    <a href="#"><i class="fa fa-star-o"></i></a>
                                    <a href="#"><i class="fa fa-star-o"></i></a>
                                    <a href="#"><i class="fa fa-star-o"></i></a>
                                    <a href="#"><i class="fa fa-star-o"></i></a>
                                </div>
                                <div class="prodcut-price list-price">
                                    <div class="new-price"> Rs ${totalItemPrice.toFixed(2)}</div>
                                    <div class="old-price">(Rs. ${item.buyingPrice} x ${item.qty})</div>
                                </div>
                            </div>
                            <div class="list-product-content">
                                <div style="display: flex; align-items: start; justify-content: start" class="fs-6">
                                    <span class="fw-bold">Brand &nbsp;:&nbsp; </span> ${item.brandName} &nbsp;
                                </div>
                                <div style="display: flex; align-items: start; justify-content: start" class="fs-6">
                                    <span class="fw-bold">Color &nbsp;:&nbsp; </span> &nbsp;
                                    <div style="width: 20px; height: 20px; background-color: ${item.colorCode}; border: 1px solid #ccc;"></div> &nbsp; | &nbsp;
                                    <span class="fw-bold">Size &nbsp;:&nbsp; </span> ${item.size} &nbsp;
                                </div>
                                <div style="display: flex; align-items: start; justify-content: start" class="fs-6">

                                </div>
                                <div style="display: flex; align-items: start; justify-content: start" class="fs-6">
                                    <span class="fw-bold">Qty &nbsp;:&nbsp; </span> ${item.qty} &nbsp;
                                </div>
                            </div>
                            <div class="social-icon-wraper mt-25">
                                <div class="social-icon socile-icon-style-1">
                                    <ul>
                                        ${order.statusValue === "PENDING" ? `
                                            ${item.statusName === "ACTIVE" ? `
                                                <li>
                                                    <a href="javascript:void(0)" onclick="changeOrderItemStatus(${item.id})" data-tooltip="Add To Cart" class="add-cart add-cart-text bg-danger text-white border-danger cancelBtn" data-placement="left" tabindex="0">
                                                        Cancel
                                                        <i class="fa fa-times"></i>
                                                    </a>
                                                </li>
                                            ` : ``}
                                        ` : ``}
                                        
                                        ${order.statusValue === "RECEIVED" ? `
                                            <li>
                                                <a href="javascript:void(0)" data-tooltip="Add To Cart" class="add-cart add-cart-text" data-placement="left" tabindex="0">
                                                    Review
                                                    <i class="fa fa-comment"></i>
                                                </a>
                                            </li>
                                        ` : ``}
                                        
                                        <li>
                                            <a href="javascript:void(0)" onclick="addProductToCart(${item.stockId}, 1)"><i class="zmdi zmdi-shopping-cart"></i></a>
                                        </li>
                                        <li>
                                            <a href="javascript:void(0)" onclick="addProductToWishlist(${item.stockId})"><i class="zmdi zmdi-favorite-outline"></i></a>
                                        </li>
                                    </ul>
                                </div>
                            </div>

                            <div class="mt-4" style="background-color:#303030">
                                <div class="d-flex justify-content-end">
                                    <div class="bg-white pt-1 pb-1 text-center mr-20" style="width:120px;">
                                        ${item.statusName === "ACTIVE" ? `
                                            <span style="color:#303030; font-weight:bold">${order.statusValue}</span>
                                        ` : `
                                            <span style="color:rgba(218,3,15,0.7); font-weight:bold">${item.statusName}</span
                                        `}
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                    <!-- single product end-->
                </div>
            `;
        })

        let statusId = order.statusId;

        // ---- Render accordion ----
        accordionContainer.innerHTML += `
            <div class="accordion-item border mb-2">

                <h2 class="accordion-header">
                    <button class="accordion-button ${statusId === 12 || statusId === 8 || statusId === 9 ? 'collapsed' : ''} d-flex flex-column flex-md-row
                        align-items-start align-items-md-center text-start"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#${collapseId}"
                        aria-expanded="${statusId === 12 || statusId === 8 || statusId === 9 ? 'false' : 'true'}">

                        <span class="fw-bold">
                            ${order.idAsString}
                            <span class="fw-normal fw-bold" style="font-size: 12px">
                                (${order.statusValue})
                            </span>
                        </span>
                        <span class="ms-md-auto fw-bold mt-1 mt-md-0">
                            Placed On : 
                            <span class="fw-normal fw-bold" style="font-size: 12px">
                                (${order.createdAt})
                            </span>
                        </span>

                        <span class="ms-md-auto fw-bold mt-1 mt-md-0">
                            Rs ${order.totalPrice.toFixed(2)}
                            <span class="fw-normal fw-bold" style="font-size: 12px">
                                (${order.deliveryTypeName})
                            </span>
                        </span>
                    </button>
                </h2>

                <div id="${collapseId}" class="accordion-collapse collapse ${statusId === 12 || statusId === 8 || statusId === 9 ? '' : 'show'}">
                    <div class="accordion-body row total-shop-product-list">
                        ${itemsHtml}
                    </div>
                </div>

            </div>
        `;
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
                sortOrders(p); // keep active filters, only change page
            }
        };
    });
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
            console.log(data)
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    async () => {
                        await sortOrders(currentPage)
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
//ORDER HISTORY HANDELING FUNCTIONS

async function signOut(){
    reportInit();
    Notiflix.Loading.pulse("Processing...", {
        clickToClose: false,
        svgColor: "#cc3333"
    });

    try {
        const response = await fetch("api/users/logout", {
            method:"GET",
            credentials:"include"
        });
        if(response.status === 200){
            Notiflix.Report.success(
                'Calivon',
                'Logout Successful',
                'Okay', //Button title
                () => {
                    window.location = "login.html";
                },
            );
        }else{
            notify('Something went wrong. Log out process failed.');
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove(1000);
    }
}


