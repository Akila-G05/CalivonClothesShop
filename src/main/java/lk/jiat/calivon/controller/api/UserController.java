package lk.jiat.calivon.controller.api;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.annotation.IsUser;
import lk.jiat.calivon.dto.UserDTO;
import lk.jiat.calivon.service.ProfileService;
import lk.jiat.calivon.service.UserService;
import lk.jiat.calivon.util.AppUtil;

@Path("/users")
public class UserController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAccount(String jsonData){
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().addNewUser(userDTO);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/verify-guest-accounts")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verifyGuestAccount(String jsonData, @Context  HttpServletRequest request){
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().verifyGuestAccounts(userDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/verify-accounts")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verifyUserAccount(String jsonData){
        Gson gson = new Gson();
        UserDTO userDTO = gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().verifyUserAccounts(userDTO);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().userLogin(userDTO,  request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/logout")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if(httpSession!=null) {
            httpSession.invalidate();
            return Response.status(Response.Status.OK).build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
