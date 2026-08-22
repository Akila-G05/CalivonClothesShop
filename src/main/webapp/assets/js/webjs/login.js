
async function signin(){
    reportInit();
    Notiflix.Loading.pulse("Processing...", {
        clickToClose: false,
        svgColor: "#cc3333"
    });

    let email = document.getElementById("email");
    let password = document.getElementById("password");
    let guestToggle = document.getElementById("login-toggle")

    const userLoginObj={
        email:email.value,
        password:password.value,
        guestLogin: guestToggle.checked
    }

    try {
        const response = await fetch("api/users/login", {
            method: "POST",
            headers: {
                "Content-Type":"application/json"
            },
            body: JSON.stringify(userLoginObj)
        });

        if(response.ok){
            const data = await response.json();
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    () => {
                        window.location = "index.html";
                    },
                );
            }else{
                notify(data.message);
            }
        }else{
            notify("Login failed! Please try again");
        }
    }catch (e) {
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }

}

const pupils = document.querySelectorAll('.eye span');

document.addEventListener('mousemove', e => {
    pupils.forEach(pupil => {
        const eye = pupil.parentElement.getBoundingClientRect();

        const x = (e.clientX - (eye.left + eye.width / 2)) / 12;
        const y = (e.clientY - (eye.top + eye.height / 2)) / 12;

        pupil.style.transform = `translate(${x}px, ${y}px)`;
    });
});
