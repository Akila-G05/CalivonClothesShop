class FooterContent extends HTMLElement{
    connectedCallback(){
        this.innerHTML= `<!-- footer area start-->
            <div class="footer-area ptb-50">
                <div class="container mx-auto">
                    <div class="row">
                        <div class="col-xl-3 col-lg-3 col-md-4">
                            <div class="single-footer contact-us">
                                <div class="footer-title uppercase">
                                    <h5>Contact US</h5>
                                </div>
                                <ul>
                                    <li>
                                        <div class="contact-icon"> <i class="zmdi zmdi-pin-drop"></i> </div>
                                        <div class="contact-text">
                                            <p>Address: Your address goes here</p>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="contact-icon"> <i class="zmdi zmdi-email-open"></i> </div>
                                        <div class="contact-text">
                                            <p><span><a href="mailto://demo@example.com">demo@example.com</a></span>
                                                <span><a href="mailto://info@example.com">info@example.com</a></span></p>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="contact-icon"> <i class="zmdi zmdi-phone-paused"></i> </div>
                                        <div class="contact-text">
                                            <p><a href="tel://01234567890">01234567890</a> <a
                                                    href="tel://01234567890">01234567890</a></p>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="col-xl-2 col-lg-1 col-md-4"></div>
                        <div class="col-xl-2 col-lg-2 col-md-4">
                            <div class="single-footer informaton-area">
                                <div class="footer-title uppercase">
                                    <h5>Information</h5>
                                </div>
                                <div class="informatoin">
                                    <ul>
                                        <li><a href="#">My Account</a></li>
                                        <li><a href="#">Order History</a></li>
                                        <li><a href="#">Wishlist</a></li>
                                        <li><a href="#">Returnes</a></li>
                                        <li><a href="#">Private Policy</a></li>
                                        <li><a href="#">Site Map</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="col-xl-1 col-lg-1 col-md-4"></div>
                        <div class="col-xl-3 col-lg-3 col-md-4 offset-xl-1">
                            <div class="single-footer newslatter-area">
                                <div class="footer-title uppercase">
                                    <h5>Get Newsletters</h5>
                                </div>
                                <div class="newslatter">
                                    <form action="#" method="post">
                                        <div class="input-box pos-rltv">
                                            <input placeholder="Type Your Email hear" type="text">
                                            <a href="#">
                                                <i class="zmdi zmdi-arrow-right"></i>
                                            </a>
                                        </div>
                                    </form>
                                    <div class="social-icon socile-icon-style-3 mt-40">
                                        <div class="footer-title uppercase">
                                            <h5>Social Network</h5>
                                        </div>
                                        <ul>
                                            <li><a href="#"><i class="zmdi zmdi-facebook"></i></a></li>
                                            <li><a href="#"><i class="zmdi zmdi-linkedin"></i></a></li>
                                            <li><a href="#"><i class="zmdi zmdi-pinterest"></i></a></li>
                                            <li><a href="#"><i class="zmdi zmdi-google"></i></a></li>
                                            <li><a href="#"><i class="zmdi zmdi-twitter"></i></a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--footer area start-->

            <!--footer bottom area start-->
            <div class="footer-bottom global-table">
                <div class="global-row">
                    <div class="global-cell">
                        <div class="container">
                            <div class="row">
                                <div class="col-md-12 text-center">
                                    <p class="copyrigth text-center">
                                        © 2026 <span class="text-capitalize">Calivon</span>. Made
                                        with 
                                        by
                                        <a href="https://themeforest.net/user/codecarnival/portfolio">BinaryBrains</a>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--footer bottom area end-->`;
    }
}

customElements.define("footer-content", FooterContent);