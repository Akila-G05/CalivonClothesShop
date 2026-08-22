package lk.jiat.calivon.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.dto.UserDTO;
import lk.jiat.calivon.entity.Admin;
import lk.jiat.calivon.entity.Status;
import lk.jiat.calivon.entity.User;
import lk.jiat.calivon.mail.VerificationMail;
import lk.jiat.calivon.provider.MailServiceProvider;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.HibernateUtil;
import lk.jiat.calivon.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {
    public String addNewUser(UserDTO userDTO){
        JsonObject responseObject = new JsonObject();

        boolean status = false;
        String message;

        /// registration handeling code | user registration part start
        if(userDTO.getFirstName() == null){
            message = "First Name is required!";
        }else if(userDTO.getFirstName().isBlank()) {
            message = "First Name can not be empty or blank!";
        }else if (userDTO.getLastName() == null) {
            message = "Last Name is required!";
        }else if(userDTO.getLastName().isBlank()) {
            message = "Last Name can not be empty or blank!";
        }else if (userDTO.getEmail() == null) {
            message = "Email is required!";
        }else if(userDTO.getEmail().isBlank()) {
            message = "Email can not be empty or blank!";
        }else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "Please enter a valid email address!";
        }else if (userDTO.getPassword() == null) {
            message = "Password is required!";
        }else if(userDTO.getPassword().isBlank()) {
            message = "Password can not be empty or blank!";
        }else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please enter a valid password. \n " +
                    "Password must be at least 8 characters long and include at least one uppercase letter, " +
                    "One lowercase letter, one digit, and one special character!";
        }else{
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User singleUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getSingleResultOrNull();

            if(singleUser != null){
                message = "The email already exists! Please use another email.";
            }else{
                User u = new User();
                u.setFirstName(userDTO.getFirstName());
                u.setLastName(userDTO.getLastName());
                u.setEmail(userDTO.getEmail());
                u.setPassword(userDTO.getPassword());

                String verificationCode = AppUtil.generateCode();
                u.setVerificationCode(verificationCode);

                Status pendingStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class) //Return Status Object
                        .setParameter("value", String.valueOf(Status.Type.PENDING))
                        .getSingleResult();
                u.setStatus(pendingStatus);

                Transaction transaction = hibernateSession.beginTransaction();

                try {
                    hibernateSession.persist(u);
                    transaction.commit();

                    /// Verification Mail Send Process Start
                    VerificationMail verificationMail = new VerificationMail(u.getEmail(), u.getVerificationCode());
                    MailServiceProvider.getInstance().sendMail(verificationMail);
                    /// Verification Mail Send Process End

                    status = true;
                    responseObject.addProperty("uId", u.getId());
                    message = "Account created successfully. Verification code has been sent. Please verify your account.";
                }catch (HibernateException e){
                    transaction.rollback();
                    message = "Account creation failed. Please try again!.";
                }
            }

            hibernateSession.close();
        }
        /// registration handeling code | user registeration part end

        responseObject.addProperty("status", status);
        responseObject.addProperty("message",message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String userLogin(UserDTO userDTO, @Context HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            message = "Email is required!";
        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "Please enter a valid email address!";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            try {
                User user = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                        .setParameter("email", userDTO.getEmail())
                        .getSingleResultOrNull();

                Admin admin = hibernateSession.createNamedQuery("Admin.getByEmail", Admin.class)
                        .setParameter("email", userDTO.getEmail())
                        .getSingleResultOrNull();

                boolean guestLogin = userDTO.isGuestLogin();

                if (admin != null) {
                    if (guestLogin) {
                        message = "Admin login is not allowed as guest.";
                    } else if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
                        message = "Password is required!";
                    } else if (!admin.getPassword().equals(userDTO.getPassword())) {
                        message = "Login data not matched!";
                    } else {
                        resetSession(request);
                        request.getSession().setAttribute("admin", admin);
                        status = true;
                        message = "Admin Login Successful";
                    }
                }else if (user != null) {/* ---------- USER LOGIN ---------- */

                    String userStatus = user.getStatus().getValue();

                    if(String.valueOf(Status.Type.BLOCKED).equals(userStatus)){
                        message = "Account is blocked! Please contact admin.";
                    }else {
                        /* ===== GUEST LOGIN PATH ===== */
                        if (guestLogin) {
                            if (!Status.Type.GUEST.name().equals(userStatus)) {
                                message = "This account is not a guest account.";
                            } else {
                                resetSession(request);
                                request.getSession().setAttribute("user", user);
                                status = true;
                                message = "Guest Login Successful";
                            }
                        } else {/* ===== NORMAL LOGIN PATH ===== */
                            if (Status.Type.GUEST.name().equals(userStatus)) {
                                message = "Please login as guest using the guest option.";
                            } else if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
                                message = "Password is required!";
                            } else if (!user.getPassword().equals(userDTO.getPassword())) {
                                message = "Login data not matched!";
                            } else if (!Status.Type.VERIFIED.name().equals(userStatus)) {
                                message = "Your account is not verified.";
                            } else {
                                resetSession(request);
                                request.getSession().setAttribute("user", user);
                                status = true;
                                message = "User Login Successful";
                            }
                        }
                    }
                } else {
                    message = "Account not found! Please register first.";
                }

            } finally {
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    private void resetSession(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        request.getSession(true);
    }

    public String verifyUserAccounts(UserDTO userDTO){
        JsonObject responseObject = new JsonObject();

        boolean status = false;
        String message = "";

        /// accounts verification handeling code | accounts verification part start
        if(userDTO.getEmail() == null){
            message = "Email is required!";
        }else if (userDTO.getEmail().isBlank()) {
            message = "Email can not be empty or blank!";
        }else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "Please enter a valid email address!";
        }else if(userDTO.getVerificationCode() == null){
            message = "Verification code is required!";
        }else if(userDTO.getVerificationCode().isBlank()){
            message = "Verification code can not be empty or blank!";
        }else if(!userDTO.getVerificationCode().matches(Validator.VERIFICATION_CODE_VALIDATION)){
            message = "Please enter a valid verification code.";
        }else{
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            User user = hibernateSession.createQuery("FROM User u WHERE u.email=:email", User.class)
                    .setParameter("email", userDTO.getEmail())
//                    .setParameter("verificationCode", userDTO.getVerificationCode())
                    .getSingleResultOrNull();
            if(user == null){
                message = "Account not found! Please Register first!";
            }else{
                Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.VERIFIED))
                        .getSingleResult();

                responseObject.addProperty("userStatus", user.getStatus().getValue());

                if(user.getStatus().equals(verifiedStatus)){
                    message = "Account already verified!";
                }else{
                    if(!String.valueOf(Status.Type.GUEST).equals(user.getStatus().getValue())) {
                        user.setStatus(verifiedStatus);
                        user.setVerificationCode("");
                        Transaction transaction = hibernateSession.beginTransaction();
                        try {
                            hibernateSession.merge(user);
                            transaction.commit();
                            status = true;
                            message = "Account verification completed!";
                        } catch (HibernateException e) {
                            transaction.rollback();
                            message = "Something went wrong. Account verification Process Failed!";
                        }
                    }else{
                        status = true;
                        message = "Please set password to verify your account!";
                    }
                }
            }
            hibernateSession.close();
        }
        /// accounts verification handeling code | accounts verification part end

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    public String verifyGuestAccounts(UserDTO userDTO, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String newPassword = userDTO.getNewPassword();
        String confirmPassword = userDTO.getConfirmPassword();

        if (newPassword == null || newPassword.isBlank()) {
            message = "New password is required!";
        }else if (confirmPassword == null || confirmPassword.isBlank()) {
            message = "Confirm password is required!";
        }else if (!newPassword.matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please enter a valid new password.";
        }else if (!confirmPassword.matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please enter a valid confirm password.";
        }else if (!newPassword.equals(confirmPassword)) {
            message = "New and Confirm passwords do not match!";
        }else{
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            User dbUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getSingleResult();

            if (!String.valueOf(Status.Type.VERIFIED).equals(dbUser.getStatus().getValue())) {
                dbUser.setPassword(userDTO.getNewPassword());

                Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.VERIFIED))
                        .getSingleResult();

                dbUser.setStatus(verifiedStatus);
                dbUser.setVerificationCode("");
                Transaction transaction = hibernateSession.beginTransaction();

                try {
                    hibernateSession.merge(dbUser);
                    transaction.commit();
                    status = true;
                    message = "Account verification completed!";
                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Something went wrong. Account verification Process Failed!";
                }
            } else {
                message = "This account already verified!";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
}
