let params = new URLSearchParams(window.location.search);
console.log(params.get("email"));
console.log(params.get("verificationCode"));

const verificationCode = document.getElementById("verificationCode");
verificationCode.value = params.get("verificationCode");
const userEmail = params.get("email")

async function verifyAccount(){
    reportInit();
    Notiflix.Loading.pulse("Verifying...", {
        clickToClose: false,
        svgColor: "#cc3333"
    });

    const verifyObj={
        email: userEmail,
        verificationCode:verificationCode.value
    }

    try {
        const response = await fetch("api/users/verify-accounts", {
            method:"POST",
            headers:{
                "Content-Type":"application/json",
            },
            body:JSON.stringify(verifyObj)
        });

        if(response.ok){
            const data =  await response.json();
            if(data.status){
                if(data.userStatus === "GUEST"){
                    openUserVerifyModel();
                }else {
                    Notiflix.Report.success(
                        'Calivon',
                        data.message,
                        'Okay', //Button title
                        () => {
                            window.location = "login.html"
                        },
                    );
                }
            }else{
                notify(data.message);
            }
        }else{
            notify("Verification Failed.");
        }
    }catch (e) {
        notify(e);
    }finally {
        Notiflix.Loading.remove();
    }
}

function openUserVerifyModel(){
    const verifyModalEl = document.getElementById("verifyModal");
    const verifyModal = new bootstrap.Modal(verifyModalEl);

    verifyModal.show();
}
async function verifyGuestAccount(){
    reportInit();
    Notiflix.Loading.pulse("Processing...");

    let newPassword = document.getElementById("newPassword")
    let confirmPassword = document.getElementById("confirmPassword")

    const userObj= {
        email: userEmail,
        verificationCode: verificationCode.value,
        newPassword: newPassword.value,
        confirmPassword: confirmPassword.value
    };

    try {
        const response = await fetch("api/users/verify-guest-accounts", {
            method: "POST",
            headers:{
                "Content-Type":"application/json"
            },
            body:JSON.stringify(userObj)
        });
        if(response.ok){
            const data = await response.json();
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay',
                    () => {
                        window.location = "login.html";
                    }
                );
            }else{
                notify(data.message);
            }
        }else {
            notify("Guest account verifying failed! ");
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}
