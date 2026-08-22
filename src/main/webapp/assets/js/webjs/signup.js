async function signUp(){
    reportInit();
    Notiflix.Loading.pulse("Processing...", {
        clickToClose: false,
        svgColor: "#cc3333"
    });

    let firstName = document.getElementById("fname");
    let lastName = document.getElementById("lname");
    let email = document.getElementById("email");
    let password = document.getElementById("password");

    const user = {
        firstName: firstName.value,
        lastName: lastName.value,
        email: email.value,
        password: password.value
    }

    try{
        const response = await fetch("api/users", {
            method:"POST",
            headers:{
                "Content-Type": "application/json"
            },
            body: JSON.stringify(user)
        });

        if(response.ok){
            const data = await response.json();
            console.log(data)
            if(data.status){
                Notiflix.Report.success(
                    'Calivon',
                    data.message,
                    'Okay', //Button title
                    ()=>{
                        window.location.reload();
                    }
                );
            }else{
                notify(data.message);
            }
        }else{
            notify('Something went wrong. Please check your details.');
        }
    }catch (e){
        notify(e.message);
    }finally {
        Notiflix.Loading.remove();
    }
}