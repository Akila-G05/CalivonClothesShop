package lk.jiat.calivon.controller.api;

import com.google.gson.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.service.CheckoutService;
import lk.jiat.calivon.util.PayHereUtil;

@Path("/payments")
public class PaymentGatewayController {
    @POST
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response notify(MultivaluedMap<String, String> form) {

//        if (!PayHereUtil.validateNotify(form)) {
//            return Response.status(400).entity("INVALID SIGNATURE").build();
//        }

        String orderIdStr = form.getFirst("order_id"); // ORD00000034
        int orderId = Integer.parseInt(orderIdStr.replaceAll("\\D+", ""));

        String statusCode = form.getFirst("status_code");

        System.out.println(orderId);

        CheckoutService service = new CheckoutService();

        if ("2".equals(statusCode)) {
            service.finalizePayHereOrder(orderId);
        }else {
            service.failOrder(orderId);
        }

        return Response.ok("OK").build();
    }

    @PUT
    @Path("/dev-notify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response devPaymentSuccess(@QueryParam("orderId") String orderStrId) {
        CheckoutService service = new CheckoutService();
        // ORD00000034
        int orderId = Integer.parseInt(orderStrId.replaceAll("\\D+", ""));
        System.out.println(orderId);
//        try {
//            service.finalizePayHereOrder(orderId);
//
//            JsonObject json = new JsonObject();
//            json.addProperty("status", true);
//            json.addProperty("message", "DEV payment successful");
//
//            return Response.ok(json.toString()).build();
//
//        } catch (Exception e) {
//            JsonObject json = new JsonObject();
//            json.addProperty("status", false);
//            json.addProperty("message", "DEV payment failed");
//
//            return Response.status(500).entity(json.toString()).build();
//        }
        return Response.ok("OK").build();
    }
}

