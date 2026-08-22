package lk.jiat.calivon.controller.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.persistence.Id;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.calivon.annotation.IsAdmin;
import lk.jiat.calivon.annotation.IsUser;
import lk.jiat.calivon.dto.ProductDTO;
import lk.jiat.calivon.dto.StockDTO;
import lk.jiat.calivon.entity.Product;
import lk.jiat.calivon.service.FileUploadService;
import lk.jiat.calivon.service.ProductService;
import lk.jiat.calivon.service.ProfileService;
import lk.jiat.calivon.util.AppUtil;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/products")
public class ProductController {
/// CART PART START
    @Path("/update-cart")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCart(String jsonData, @Context HttpServletRequest request) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray cartData = jsonObject.getAsJsonArray("cartData");
        String responseJson = new ProductService().updateCartItem(cartData, request);

        return Response.ok().entity(responseJson).build();
    }

    @Path("/delete-cart")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartItem(@QueryParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new ProductService().deleteCartItem(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-cart-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadCartData(@Context HttpServletRequest request) {
        String reponseJson = new ProductService().loadCartData(request);
        return Response.ok().entity(reponseJson).build();
    }

    @Path("/add-product-cart")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(String jsonData, @Context HttpServletRequest request) {
        StockDTO stockDTO = AppUtil.gson.fromJson(jsonData, StockDTO.class);
        String responseJson = new ProductService().addProductToCart(stockDTO ,request);
        return Response.ok().entity(responseJson).build();
    }
/// CART PART END

/// WISHLIST PART START
    @IsUser
    @Path("/delete-wishlist")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWishlist(@QueryParam("id") int id, @Context HttpServletRequest request) {
        String responseJson = new ProductService().deleteWishlistItem(id, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-wishlist-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadWishlistData(@Context HttpServletRequest request) {
        String reponseJson = new ProductService().loadWishlistData(request);
        return Response.ok().entity(reponseJson).build();
    }

    @Path("/add-product-wishlist")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToWishlist(String jsonData, @Context HttpServletRequest request) {
        StockDTO stockDTO = AppUtil.gson.fromJson(jsonData, StockDTO.class);
        String responseJson = new ProductService().addProductToWishlist(stockDTO ,request);
        return Response.ok().entity(responseJson).build();
    }
/// WISHLIST PART END

/// SINGLE VIEW DATA LOADING PART START
    @Path("/change-single-view-stock")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeSingleViewStock(
            @QueryParam("pId") int productId, @QueryParam("cId") int colorId, @QueryParam("sId") int sizeId, @QueryParam("checkId") int checkId) {
        String reponseJson = new ProductService().getStockByColorAndSize(productId, colorId, sizeId, checkId);
        return Response.ok().entity(reponseJson).build();
    }

    @Path("/load-single-view-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadSingleViewData(
            @QueryParam("id") int stockId){
        String reponseJson = new ProductService().loadSingleProductViewData(stockId);
        return Response.ok().entity(reponseJson).build();
    }
/// SINGLE VIEW DATA LOADING PART START

    @Path("/sort") //Shop Sort,Load Related Products in Single Product View
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sortProducts(
            @QueryParam("minPrice") @DefaultValue("0") double minPrice,
            @QueryParam("maxPrice") @DefaultValue("0") double maxPrice,
            @QueryParam("colorId") @DefaultValue("0") int colorId,
            @QueryParam("sizeId") @DefaultValue("0") int sizeId,
            @QueryParam("categoryId") @DefaultValue("0") int categoryId,
            @QueryParam("subCategoryId") @DefaultValue("0") int subCategoryId,
            @QueryParam("brandId") @DefaultValue("0") int brandId,
            @QueryParam("search") String search,
            @QueryParam("order") @DefaultValue("5") int order,      // uses your mapping
            @QueryParam("limit") @DefaultValue("15") int limit,
            @QueryParam("page") @DefaultValue("1") int page
    ){
        String json = new ProductService().sortProducts(minPrice, maxPrice, colorId, sizeId, categoryId, subCategoryId, brandId, search, order, limit, page);
        return Response.ok().entity(json).build();
    }


    @Path("/load-home")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllProducts(){
        Object latest = AppUtil.gson.fromJson(new ProductService().getAllProducts("createdAt", "DESC", 8), Object.class);
        Object oldest = AppUtil.gson.fromJson(new ProductService().getAllProducts("createdAt", "ASC", 8), Object.class);

        Map<String, Object> map = new HashMap<>();
        map.put("latest", latest);
        map.put("oldest", oldest);

        String finalJson = AppUtil.gson.toJson(map);

        return Response.ok().entity(finalJson).build();
    }

/// ADMIN SIDE PART START
    @IsAdmin
    @PUT
    @Path("/change-stock-status")
    public Response changeStockStatus(String jsonData, @Context HttpServletRequest request) {
        StockDTO stockDTO = AppUtil.gson.fromJson(jsonData, StockDTO.class);
        String responseJson = new ProductService().changeStockStatus(stockDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsAdmin
    @PUT
    @Path("/add-stock-discount")
    public Response addStockDiscount(String jsonData, @Context HttpServletRequest request) {
        StockDTO stockDTO = AppUtil.gson.fromJson(jsonData, StockDTO.class);
        String responseJson = new ProductService().addStockDiscount(stockDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/{productId}/all-stocks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadStocksByProductId(@PathParam("productId") int productId) {
        String responseObject = new ProductService().getStockByProductId(productId);
        return Response.ok().entity(responseObject).build();
    }

    @Path("/sort-admin-products")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sorOrders(
            @QueryParam("search") String search,
            @QueryParam("orderSelect") @DefaultValue("2") int order,      // uses your mapping
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("page") @DefaultValue("1") int page,
            @Context HttpServletRequest request
    ){
        String json = new ProductService().sortAdminProducts(search, order, limit, page, request);
        return Response.ok().entity(json).build();
    }

    @IsAdmin
    @Path("/save-product")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveProduct(@FormDataParam("product") String productJson, @Context HttpServletRequest request) {
        ProductDTO productDTO = AppUtil.gson.fromJson(productJson, ProductDTO.class);
        String responseJson = new ProductService().addNewProductDetails(productDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsAdmin
    @Path("/save-stocks")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveStocks(@FormDataParam("stockData") String stockJson, @FormDataParam("productId") int productId, @Context HttpServletRequest request) {
        Type stockListType = new TypeToken<List<StockDTO>>() {}.getType();
        List<StockDTO> stockList = AppUtil.gson.fromJson(stockJson, stockListType);

        String responseJson = new ProductService().addStockDetails(stockList, productId, request);
        return Response.ok().entity(responseJson).build();
    }


    @IsAdmin
    @Path("/{productId}/upload-images")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProductImages(
            @PathParam("productId") int productId,
            @FormDataParam("images[]") FormDataBodyPart formDataBodyPart,
            @Context ServletContext context) {

        List<FileUploadService.FileItem> fileItems = new ArrayList<>();
        FileUploadService fileUploadService = new FileUploadService(context);

        ProductService productService = new ProductService();
        Product product = productService.getProductById(productId);

        formDataBodyPart.getParent().getBodyParts().forEach(bodyPart -> {
            InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
            ContentDisposition contentDisposition = bodyPart.getContentDisposition();
            System.out.println(contentDisposition.getFileName());
            FileUploadService.FileItem fileItem = fileUploadService.uploadFile("product/" + productId, inputStream, contentDisposition);
            fileItems.add(fileItem);
            product.getImages().add(fileItem.getFilePath());
        });

        String responseJson = productService.addProductImages(product);
        return Response.ok().entity(responseJson).build();
    }
/// ADMIN SIDE PART END
}
