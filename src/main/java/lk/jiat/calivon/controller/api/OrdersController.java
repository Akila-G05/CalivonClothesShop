package lk.jiat.calivon.controller.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.service.OrdersService;
import lk.jiat.calivon.service.ProductService;
import lk.jiat.calivon.service.ProfileService;

@Path("/orders")
public class OrdersController {
    @Path("/status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOrdersStatus(String jsonData, @Context HttpServletRequest request) {
        JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();

        int id = json.get("orderId").getAsInt();
        String statusValue = json.get("statusValue").getAsString();

        System.out.println(id + " " + statusValue);
        String responseJson = new OrdersService().changeOrdersStatus(id, statusValue, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/items-status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelOrders(@QueryParam("id") int id, @Context HttpServletRequest request) {
        System.out.println(id);
        String responseJson = new OrdersService().changeOrderItemStatus(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/sort")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sorOrders(
            @QueryParam("search") String search,
            @QueryParam("orderByStatus") @DefaultValue("2") int order,      // uses your mapping
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("page") @DefaultValue("1") int page,
            @Context HttpServletRequest request
    ){
        String json = new OrdersService().sortOrders(search, order, limit, page, request);
        return Response.ok().entity(json).build();
    }
}
