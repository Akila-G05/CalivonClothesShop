let cartData;
let session;

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

async function deleteCartItem2(id){
    // reportInit();
    // Notiflix.Loading.pulse("Processing...");

    try {
        const response = await fetch("api/products/delete-cart?id=" + id, {
            method: "DELETE",
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                // notifySuccess(data.message);

                if (window.headerContentInstance) {
                    console.log("ok")
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
function renderHeaderCartDesign(data) {
    const container = document.getElementById("headerCartContainer");
    container.innerHTML = ""; // clear existing items

    if (Array.isArray(data.cartItems) && data.cartItems.length > 0) {
        data.cartItems.slice(0, 4).forEach(items => {
            let product = items.product;
            let stock = items.stock;

            const cartItem = document.createElement("div");
            cartItem.classList.add("cart-single-wraper");
            cartItem.innerHTML = `
            <div class="cart-img">
                <a href="single-product.html?id=${stock.stockId}">
                    <img src="${product.imgPath1 || 'assets/images/product/01.jpg'}" alt="${product.title}" style="object-fit: cover;">
                </a>
            </div>
            <div class="cart-content">
                <div class="cart-name">
                    <a href="single-product.html?id=${stock.stockId}">${product.title}</a>
                </div>
                <div class="cart-price">Rs. ${items.total}</div>
                <div class="cart-qty">Qty: <span>${items.cartQty}</span></div>
            </div>
            <div class="remove">
                <a href="javascript:void(0)" onclick="deleteCartItem2(${stock.stockId})"><i class="zmdi zmdi-close"></i></a>
            </div>
        `;
            container.appendChild(cartItem);
        });


        const subtotalDiv = document.createElement("div");
        subtotalDiv.classList.add("cart-subtotal");
        subtotalDiv.innerHTML += `Subtotal: <span>Rs. ${data.cartTotal.toFixed(2)}</span>`;
        container.appendChild(subtotalDiv);

        const checkBtnDiv = document.createElement("div");
        checkBtnDiv.classList.add("cart-check-btn");
        checkBtnDiv.innerHTML = ` 
            <div class="view-cart"> 
                <a class="btn-def" href="cart.html">View Cart</a> 
            </div> 
            <div class="check-btn"> 
                <a class="btn-def" href="checkout.html">Checkout</a> 
            </div> 
        `;

        container.appendChild(checkBtnDiv);

        // Update cart item count in header icon
        const cartCountSpan = document.querySelector(".header-cart .cart-icon span");
        if (cartCountSpan) {
            cartCountSpan.textContent = data.cartItems.length;
        }
    }
}

class HeaderContent extends HTMLElement {

    async connectedCallback() {
        window.headerContentInstance = this;

        session = await this.getSessionData();
        this.render(session);

        if(session.userType !== "admin"){
            cartData = await this.getHeaderCartProducts();
        }

        this.setActiveMenu();

        if (typeof $.fn.meanmenu !== 'undefined') {
            $('#dropdown').meanmenu({
                meanScreenWidth: "767",
                meanRevealPosition: "right"
            });
        }
    }

    async getSessionData() {
        try {
            const response = await fetch("api/sessions/sessions-data");
            if(response.ok){
                const data = await response.json();
                console.log(data);
                return data;
            }else{
                console.log("Session Loading Failed!");
                return null;
            }
        }catch (e) {
            console.log(e.message);
            return null;
        }
    }
    async getHeaderCartProducts(){
        try {
            const response = await fetch(`api/products/load-cart-data`);
            if(response.ok){
                const data = await response.json();
                // console.log(data)
                if(data.status){
                    renderHeaderCartDesign(data)
                    if (typeof loadCartTable === "function"){
                        loadCartTable(data);
                    }
                    return data;
                }else{
                    const container = document.getElementById("headerCartContainer");
                    container.innerHTML = "";
                    const cartCountSpan = document.querySelector(".header-cart .cart-icon span");
                    cartCountSpan.textContent = 0;

                    if(typeof loadCartEmptyDesign === "function"){
                        loadCartEmptyDesign();
                    }
                    return null;
                }
            }else{
                notify("Product Data Loading Failed!");
                return null;
            }
        }catch (e){
            notify(e.message);
            return null;
        }
    }

    setActiveMenu() {
        const currentPage = window.location.pathname.split("/").pop();
        const menuItems = document.querySelectorAll(".main-menu li");

        menuItems.forEach(li => {
            const link = li.querySelector("a");
            if (!link) return;

            const linkPage = link.getAttribute("href");

            if (linkPage === currentPage) {
                li.classList.add("current");
                link.classList.add("active");
            } else {
                li.classList.remove("current");
                link.classList.remove("active");
            }
        });
    }


    render(session){
        this.innerHTML =`
            <div class="header-top-bar black-bg clearfix">
                <div class="container">
                    <div class="row">
                        <div class="col-md-3 col-sm-6 col-6">
                            <div class="login-register-area">
                                ${session.message === "Session found" && session.userType !== "cart"? 
                                    `<ul>
                                        ${session.userType === "user" ?
                                        `<li><a href="my-account.html"><i class="fa fa-user"></i>  ${session.name}</a></li>`
                                        : `<li><a href="admin-account.html"><i class="fa fa-user"></i>  ${session.name}</a></li>`}
                                        <li><a href="javascript:void(0)" onclick="signOut()"><i class="fa fa-sign-out"></i> Logout</a></li>
                                    </ul>` 
                                    :
                                    `<ul>
                                        <li><a href="login.html">Login</a></li>
                                        <li><a href="signup.html">Register</a></li>
                                    </ul>` 
                                }; 
                            </div>
                        </div>
                        <div class="col-md-6 d-none d-md-block">

                        </div>
                        <div class="col-md-3 col-sm-6 col-6">
                            <div class="cart-currency-area login-register-area text-end">
                                <ul>
                                    ${session.userType === "user" ?
                                    `<li>
                                        <div class="header-wishlist">
                                            <div class="cart-icon"> <a href="wishlist.html">Wishlist<i class="fa fa-heart"></i></a></div>
                                        </div>
                                    </li>` : ``}
                                    ${session.userType !== "admin" ?
                                    `<li>
                                        <div class="header-cart">
                                            <div class="cart-icon"> 
                                                <a href="cart.html">
                                                    Cart<i class="zmdi zmdi-shopping-cart"></i>
                                                </a> <span id="cartItemsCount">0</span> 
                                            </div>
                                            <div class="cart-content-wraper" id="headerCartContainer">
                                                
                                                <div class="cart-subtotal"> Subtotal: <span>Rs. 0.00</span> </div>
                                                <div class="cart-check-btn">
                                                    <div class="view-cart"> <a class="btn-def" href="cart.html">View
                                                            Cart</a> </div>
                                                    <div class="check-btn"> <a class="btn-def"
                                                            href="checkout.html">Checkout</a> </div>
                                                </div>
                                            </div>
                                        </div>
                                    </li>`: ``}
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="sticky-header" class="header-middle-area">
                <div class="container">
                    <div class="full-width-mega-dropdown">
                        <div class="row">
                            <div class="col-md-2">
                                <div class="logo ptb-20"><a href="index.html">
                                        <img src="assets/images/logo/logo.png" alt="main logo"></a>
                                </div>
                            </div>
                            <div class="col-lg-7 col-md-10 d-none d-md-block">
                                <nav id="primary-menu">
                                    <ul class="main-menu">
                                        <li><a href="index.html">Home</a></li>
                                        <li><a href="shop.html">SHOP</a></li>

                                        ${session.userType === "user" ? 
                                            `<li><a href="my-account.html">PROFILE</a></li>` 
                                        : session.userType === "admin" ? `<li><a href="admin-account.html">PROFILE</a></li>` : ``}
                                        
                                        ${session.userType !== "admin" ? `<li><a href="cart.html">CART</a></li>` : ``}
                                        ${session.userType === "user" ? `<li><a href="wishlist.html">WISHLIST</a></li>` : ``}
                                            
                                        <li><a href="#">ABOUT</a></li>
                                        <li><a href="#">CONATCT</a></li>
                                    </ul>
                                </nav>
                            </div>
                            <div class="col-lg-3 d-none d-lg-block">
                                <div class="search-box global-table">
                                    <div class="global-row">
                                        <div class="global-cell">
                                            <form action="#">
                                                <div class="input-box">
                                                    <input class="single-input" placeholder="Search anything"
                                                        type="text">
                                                    <button class="src-btn"><i class="fa fa-search"></i></button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- mobile-menu-area start -->
                            <div class="mobile-menu-area">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-lg-12">
                                            <nav id="dropdown">
                                                <ul>
                                                    <li><a href="index.html">Home</a></li>
                                                    <li><a href="shop.html">SHOP</a></li>
                                                    
                                                    ${session.userType === "user" ?
                                                    `<li><a href="my-account.html">PROFILE</a></li>`
                                                    : session.userType === "admin" ? `<li><a href="admin-account.html">PROFILE</a></li>` : ``}
                                        
                                                    ${session.userType !== "admin" ? `<li><a href="cart.html">CART</a></li>` : ``}
                                                    ${session.userType === "user" ? `<li><a href="wishlist.html">WISHLIST</a></li>` : ``}
                                                        
                                                    <li><a href="#">ABOUT</a></li>
                                                    <li><a href="#">CONATCT</a></li>
                                                </ul>
                                            </nav>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!--mobile menu area end-->
                        </div>
                    </div>
                </div>
            </div>`;
    }
}

customElements.define("header-content", HeaderContent);