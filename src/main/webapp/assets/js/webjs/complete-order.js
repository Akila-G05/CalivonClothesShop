window.addEventListener('load', async () => {
    reportInit();
    Notiflix.Loading.pulse("Loading Data...");

    try {
        if(session.message !== "Session found" || session.userType !== "cart"){
            Notiflix.Report.warning(
                'Calivon',
                'Your cart session has expired! Please add items again.',
                'Okay', //Button title
                ()=>{
                    window.location = "cart.html";
                }
            );
        }
        let data = null;
        if (window.headerContentInstance) {
            data = await window.headerContentInstance.getHeaderCartProducts();
        }
        await renderOrderTables(data);
        await loadDeliveryTypes();
        await loadProvinces();
    }finally {
        Notiflix.Loading.remove(500);
    }
});
window.districtData = null;

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
            console.log(data.districts)
            districtData = data.districts;
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

async function createGuestAccount(dTypeId){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    let firstName = document.getElementById("fname");
    let lastName = document.getElementById("lname");
    let email = document.getElementById("email");

    let lineOne = document.getElementById("lineOne")
    let lineTwo = document.getElementById("lineTwo")
    let provinceSelect = document.getElementById("provinceSelect")
    let districtSelect = document.getElementById("districtSelect")
    let citySelect = document.getElementById("citySelect")
    let postalCode = document.getElementById("postalCode")
    let mobile = document.getElementById("mobile")

    const dataObj={
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,

        mobile: mobile.value,
        lineOne: lineOne.value,
        lineTwo: lineTwo.value,
        provinceId: provinceSelect.value,
        districtId: districtSelect.value,
        cityId: citySelect.value,
        postalCode: postalCode.value,
    };

    try {
        const response = await fetch("api/checkout/create-guest-account", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dataObj)
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay',
                    ()=>{
                        proceedCODPayment(dTypeId);
                    }
                );
            }else{
                notify(data.message);
            }
        }else {
            notify("Guest account creation failed! ");
        }
    }catch (e){
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}