import lk.jiat.calivon.mail.VerificationMail;
import lk.jiat.calivon.provider.MailServiceProvider;

public class TestMailProcess {
    public static void main(String[] args) {
        MailServiceProvider.getInstance().start();
        VerificationMail verificationMail = new VerificationMail("akilagimhana2005@gmail.com", "12345");
        MailServiceProvider.getInstance().sendMail(verificationMail);
    }
}
