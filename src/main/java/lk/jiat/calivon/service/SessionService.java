package lk.jiat.calivon.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.entity.Admin;
import lk.jiat.calivon.entity.User;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.HibernateUtil;
import org.hibernate.Session;

public class SessionService {
    public String getSessionData(@Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String userType = "Not Found";
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if(httpSession == null){
            message = "Sessions not found";
        }else{
            if(httpSession.getAttribute("user") != null){
                userType = "user";
                User sessionUser = (User) httpSession.getAttribute("user");

                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                User dbUser = hibernateSession.find(User.class, sessionUser.getId());

                responseObject.addProperty("name", sessionUser.getFirstName() +  " " + sessionUser.getLastName());
                responseObject.addProperty("email", sessionUser.getEmail());
                responseObject.addProperty("userStatus", dbUser.getStatus().getValue());
            }else if(httpSession.getAttribute("admin") != null) {
                userType = "admin";
                Admin sessionUser = (Admin) httpSession.getAttribute("admin");
                responseObject.addProperty("name", sessionUser.getFirstName() +  " " + sessionUser.getLastName());
                responseObject.addProperty("email", sessionUser.getEmail());
            }else if(httpSession.getAttribute("cart") != null) {
                userType = "cart";
                responseObject.addProperty("name","");
            }
            message = "Session found";
        }

        responseObject.addProperty("status",status);
        responseObject.addProperty("userType",userType);
        responseObject.addProperty("message",message);
        return AppUtil.gson.toJson(responseObject);
    }
}
