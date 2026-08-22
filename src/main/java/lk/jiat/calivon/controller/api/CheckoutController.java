package lk.jiat.calivon.controller.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.dto.UserDTO;
import lk.jiat.calivon.service.CheckoutService;
import lk.jiat.calivon.service.ProfileService;
import lk.jiat.calivon.util.AppUtil;

@Path("/checkout")
public class CheckoutController {
    @Path("/cod")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(String jsonData,@Context HttpServletRequest request) {
        JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

        double cartTotal = json.get("cartTotal").getAsDouble();
        int dTypeId = json.get("dTypeId").getAsInt();

        System.out.println("dTypeId: " + dTypeId + " & " + "cartTotal: " + cartTotal);

        String responseJson = new CheckoutService().codCheckout(dTypeId, cartTotal, request);

        return Response.ok().entity(responseJson).build();
    }

    @Path("/payhere")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payHereCheckout(String jsonData, @Context HttpServletRequest request) {
        JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

        double cartTotal = json.get("cartTotal").getAsDouble();
        int dTypeId = json.get("dTypeId").getAsInt();

        String responseJson = new CheckoutService().payHereCheckout(dTypeId, cartTotal, request);
        return Response.ok().entity(responseJson).build();
    }


    @Path("/create-guest-account")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGuestAccount(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.gson.fromJson(jsonData, UserDTO.class);
        System.out.println("userDTO: " + userDTO.getEmail());
        String responseData = new CheckoutService().createGuestAccount(userDTO, request);
        return Response.ok().entity(responseData).build();
    }
}
