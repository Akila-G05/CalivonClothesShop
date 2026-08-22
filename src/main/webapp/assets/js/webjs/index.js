window.addEventListener("DOMContentLoaded", async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        await loadAllProductsForSection1();
        // await sortProducts();
    }finally {
        Notiflix.Loading.remove(500);
    }
});

async function loadAllProductsForSection1(){
    try {
        const response = await fetch("api/products/load-home");
        if(response.ok){
            const data = await response.json();
            // console.log(data)
            if (data.latest.status) {
                // console.log("ok")
                renderLatestProducts(data.latest);
            }
            if(data.oldest.status){
                renderOldestProducts(data.oldest);
            }
        }else{
            notify("Products Loading Failed!");
        }
    }catch (e){
        notify(e.message);
    }
}

async function sortProducts() {
    let currentPage = 1;

    const productPerPage = 8;
    const orderSelect = 5;

    // parse price range robustly - extract numbers
    let minPrice = 0;
    let maxPrice = 10000000000;

    // Build query params safely
    const params = new URLSearchParams();
    params.append("minPrice", minPrice);
    params.append("maxPrice", maxPrice);
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
            return;
        }

        renderLatestProducts(data);

    } catch (err) {
        console.error(err);
        notify(err.message || "Sorting failed!");
    } finally {
        Notiflix.Loading.remove();
    }
}
function renderLatestProducts(data){
    const container = document.getElementById("newArrivalProductContainer");
    container.innerHTML = "";

    // Loop through products
    data.products.forEach(product => {
        const card = document.createElement("product-card");
        card.data = product; // send product object to your ProductCard element
        container.appendChild(card);
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
function renderOldestProducts(data){
    const container = document.getElementById("ProductOrderASCContainer");
    container.innerHTML = "";

    // Loop through products
    data.products.forEach(product => {
        const card = document.createElement("product-card");
        card.data = product; // send product object to your ProductCard element
        container.appendChild(card);
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

