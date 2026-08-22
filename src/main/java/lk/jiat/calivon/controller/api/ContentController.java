package lk.jiat.calivon.controller.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.annotation.IsUser;
import lk.jiat.calivon.service.ContentService;
import lk.jiat.calivon.service.ProfileService;

@Path("/data")
public class ContentController {
    @Path("/load-delivery-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadDeliveryTypes() {
        String responseJson = new ContentService().loadDeliveryTypes();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-cities")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadCities(@QueryParam("id") int id) {
        String loadAllCities = new ContentService().loadCityDetails(id);
        return Response.ok().entity(loadAllCities).build();
    }

    @Path("/load-districts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadDistricts(@QueryParam("id") int id) {
        String responseJson = new ContentService().loadDistrictsDetails(id);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-provinces")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadProvinces(){
        String responseJson = new ContentService().loadProvinceDetails();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/shop-load-categories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadShopCategories(){
        String responseJson = new ContentService().loadCategoryDetails();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-categories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadCategories(){
        String responseJson = new ContentService().loadCategoryDetails();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/{categoryId}/load-subcategories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadModels(@PathParam("categoryId") int id){
        String responseJson = new ContentService().loadSubCategoryDetails(id);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-discounts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadDiscounts(){
        String responseJson = new ContentService().loadDiscountDetails();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/specifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadSpecifications(){
        String responseJson = new ContentService().loadProductSpecifications();
        return Response.ok().entity(responseJson).build();
    }
}
