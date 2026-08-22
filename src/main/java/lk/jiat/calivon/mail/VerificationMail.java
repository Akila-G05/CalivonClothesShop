package lk.jiat.calivon.mail;

import io.rocketbase.mail.model.HtmlTextEmail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lk.jiat.calivon.util.Env;

public class VerificationMail extends Mailable{
    private final String to;
    private String verificationCode;

    public VerificationMail(String to, String verificationCode){
        this.to = to;
        this.verificationCode = verificationCode;
    }

    @Override
    public void build(Message message) throws MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Email Verification Code - "+ Env.get("app.name"));

        String appUrl = Env.get("app.url");
        String verifyURL = appUrl + "/verify-account.html?email="+ to + "&verificationCode=" + verificationCode;

        HtmlTextEmail htmlTextEmail = getEmailTemplateBuilder()
                .header()
                .logo("https://upload.wikimedia.org/wikipedia/commons/e/eb/SmartTradePI.png").logoHeight(40).and()
                .text("WELCOME : " + to).h1().center().and()
                .text("Thanks for register in our website").center().and()
                .text("To verify your email please click on the button below.").center().and()
                .text("Your Verification Code: " + verificationCode).center().and()
                .button("Verify you Email", verifyURL).blue().center().and()
                .text("If you have any trouble please paste this link in your browser.").center().and()
                .html("<div style='text-align:center;'><a href='" + verifyURL + "'>" + verifyURL + "</a></div>").and()

                .copyright(Env.get("app.name")).url(appUrl).suffix(". All Rights Reserved").and()
                .build();

        message.setContent(htmlTextEmail.getHtml(), "text/html; charset=utf-8");
    }
}
