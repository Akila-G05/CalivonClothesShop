function notify(data) {
    Notiflix.Notify.failure(data, {
        fontFamily: "Lato, sans-serif",
        fontSize: "15px",
        zindex: 9999,

        failure: {
            background: "#333333",
            textColor: "#fff",
            notiflixIconColor: "#fff"
        },
    });
}

function notifySuccess(data) {
    Notiflix.Notify.success(data, {
        fontFamily: "Lato, sans-serif",
        fontSize: "15px",
        zindex: 9999,

        success: {
            background: "#333333",
            textColor: "#fff",
            notiflixIconColor: "#fff"
        },
    });
}

function reportInit(){
    Notiflix.Report.init({
        fontFamily: "Lato, sans-serif",
        fontSize: "16px",
        backgroundColor: '#fcfcfc',
        svgSize: '80px',
        zindex: 9998,

        cssAnimation: true,
        cssAnimationDuration: 360,
        cssAnimationStyle: 'fade',

        success: {
            svgColor: '#333333',
            titleColor: '#333333',
            messageColor: '#242424',
            buttonBackground: '#333333',
            buttonColor: '#fff',
            backOverlayColor: 'rgba(98, 93, 112, 0.21)',
        },

        warning: {
            svgColor: '#333333',
            titleColor: '#333333',
            messageColor: '#242424',
            buttonBackground: '#333333',
            buttonColor: '#fff',
            backOverlayColor: 'rgba(98, 93, 112, 0.21)',
        }
    });

    Notiflix.Loading.init({
        zindex: 100,
        clickToClose: false,
        svgColor: "#cc3333"
    });
}