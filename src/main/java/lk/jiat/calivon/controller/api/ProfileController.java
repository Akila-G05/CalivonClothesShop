package lk.jiat.calivon.controller.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.annotation.IsUser;
import lk.jiat.calivon.dto.UserDTO;
import lk.jiat.calivon.entity.User;
import lk.jiat.calivon.service.OrdersService;
import lk.jiat.calivon.service.ProfileService;
import lk.jiat.calivon.util.AppUtil;

@Path("/profiles")
public class ProfileController {

    //ADMIN PROFILE LOGIC HANDELING
    @Path("/admin-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdminPanelData(@Context HttpServletRequest request) {
        String responseJson = new ProfileService().loadAdminPanelData(request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/user-status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOrdersStatus(String jsonData, @Context HttpServletRequest request) {
        JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

        int id = json.get("userId").getAsInt();
        String statusValue = json.get("statusValue").getAsString();

        String responseJson = new ProfileService().changeUserStatus(id, statusValue, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/sort-users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sorOrders(
            @QueryParam("search") String search,
            @QueryParam("orderSelect") @DefaultValue("2") int order,      // uses your mapping
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("page") @DefaultValue("1") int page,
            @Context HttpServletRequest request
    ){
        String json = new ProfileService().sortUsers(search, order, limit, page, request);
        return Response.ok().entity(json).build();
    }
    //ADMIN PROFILE LOGIC HANDELING

    //USER PROFILE LOGIC HANDELING
    @IsUser
    @Path("/send_vcode")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendVerificationCode(String jsonData, @Context  HttpServletRequest request){
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new ProfileService().sendVerificationCode(userDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/update-profile")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePersonalInfo(String jsonData, @Context  HttpServletRequest request){
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new ProfileService().updatePersonalInfo(userDTO, request);
//        System.out.println(userDTO.getFirstName());
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/delete-address")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAddress(@QueryParam("id") int id, @Context HttpServletRequest request) {
//        System.out.println(id);
        String responseJson = new ProfileService().deleteAddress(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/add-address")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewAddress(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
//        System.out.println(userDTO.getAddressId());
        String responseData = new ProfileService().addNewOrUpdateAddress(userDTO, request);
        return Response.ok().entity(responseData).build();
    }

    @IsUser
    @Path("/update-address")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAddress(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
//        System.out.println(userDTO.getAddressId());
        String responseData = new ProfileService().addNewOrUpdateAddress(userDTO, request);
        return Response.ok().entity(responseData).build();
    }

    @IsUser
    @Path("/change-primary")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePrimaryAddress(@QueryParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new ProfileService().changePrimaryAddress(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/profile-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserProfile(@Context HttpServletRequest request){
        String responseJson = new ProfileService().loadUserProfileData(request);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/addresses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddresses(@Context HttpServletRequest request) {
        String responseJson = new ProfileService().loadUserAddresses(request);
        return Response.ok().entity(responseJson).build();
    }
    //USER PROFILE LOGIC HANDELING
}
