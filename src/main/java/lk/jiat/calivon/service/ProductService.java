package lk.jiat.calivon.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.dto.AddressDTO;
import lk.jiat.calivon.dto.ProductDTO;
import lk.jiat.calivon.dto.StockDTO;
import lk.jiat.calivon.dto.UserDTO;
import lk.jiat.calivon.entity.*;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductService {
    /// CART LOGIC HANDLING PART START
    public String updateCartItem(JsonArray cartData, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        if(session != null && session.getAttribute("user") != null) {
            User sessionUser = (User)session.getAttribute("user");
            Transaction transaction = hibernateSession.beginTransaction();

            User user = hibernateSession.find(User.class, sessionUser.getId());
            for (JsonElement el : cartData) {
                JsonObject obj = el.getAsJsonObject();
                int stockId = obj.get("stockId").getAsInt();
                int requestedQty = obj.get("qty").getAsInt();

                Cart cart = hibernateSession.createQuery("FROM Cart c WHERE c.stock.id = :stockId AND c.user.id = :userId", Cart.class)
                        .setParameter("stockId", stockId)
                        .setParameter("userId", user.getId())
                        .getSingleResultOrNull();

                int availableStock = cart.getStock().getQty();
                int finalQty = requestedQty;

                if (requestedQty > availableStock) {
                    finalQty = availableStock;

                    message = "Only " + availableStock + " items available for "
                            + cart.getStock().getProduct().getTitle() + " "
                            + cart.getStock().getColor().getValue() + " "
                            + cart.getStock().getSize().getValue()
                            + ". Cart quantity adjusted.";

                }

                cart.setQty(finalQty);
                hibernateSession.merge(cart);
            }

            try{
                transaction.commit();
                status = true;
                message = "Cart updated successfully";
            } catch (Exception e) {
                transaction.rollback();
                message = "Error updating cart: " + e.getMessage();
            }
        }else if (session != null && session.getAttribute("cart") != null) {
            Map<Integer, Integer> sessionCart = (Map<Integer, Integer>) session.getAttribute("cart");
            if (sessionCart != null) {
                for (JsonElement el : cartData) {
                    JsonObject obj = el.getAsJsonObject();
                    int stockId = obj.get("stockId").getAsInt();
                    int requestedQty = obj.get("qty").getAsInt();

                    Stock stock = hibernateSession.createQuery("FROM Stock s WHERE s.id = :stockId", Stock.class)
                            .setParameter("stockId", stockId)
                            .getSingleResultOrNull();
                    int availableStock = stock.getQty();
                    int finalQty = Math.min(requestedQty, availableStock);

                    if (sessionCart.containsKey(stockId)) {
                        sessionCart.put(stockId, finalQty);
                    } else {
                        message = "This cart item does not exist in session cart.";
                    }
                }

                session.setAttribute("cart", sessionCart);
                message = "Session Cart Updated Successfully!";
                status = true;
            }else{
                message = "Session cart is empty!";
            }
        }

        hibernateSession.close();

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    public String deleteCartItem(int id, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("user") != null) {
            User sessionUser = (User)session.getAttribute("user");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            User user = hibernateSession.find(User.class, sessionUser.getId());

            Cart cart = hibernateSession.createQuery("FROM Cart c WHERE c.stock.id = :stockId AND c.user.id = :userId", Cart.class)
                    .setParameter("stockId", id)
                    .setParameter("userId", user.getId())
                    .getSingleResultOrNull();

            if(cart != null){
                if(cart.getUser().getId() != sessionUser.getId()){
                    message = "You are not allowed to delete this item!";
                }else{
                    try{
                        hibernateSession.delete(cart);
                        transaction.commit();
                        status = true;
                        message = "Cart item removed!";
                    }catch (HibernateException e){
                        transaction.rollback();
                        message = "Cart item delete failed!";
                        e.printStackTrace();
                    }
                }
            }else{
                message = "Cart item does not exist!";
            }

            hibernateSession.close();
        }else if (session != null && session.getAttribute("cart") != null) {
            Map<Integer, Integer> sessionCart = (Map<Integer, Integer>) session.getAttribute("cart");
            if (sessionCart != null) {
                if(sessionCart.containsKey(id)){
                    sessionCart.remove(id);
                    session.setAttribute("cart", sessionCart);

                    status = true;
                    message = "Cart item removed!";
                } else {
                    message = "Cart item does not exist in session cart!";
                }
            }else{
                message = "Session cart is empty!";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String loadCartData(@Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = request.getSession(false);

        if(httpSession != null &&  httpSession.getAttribute("admin") != null){
            message = "You are not allowed to do this operation!";
        }

        if(httpSession != null &&  httpSession.getAttribute("user") != null) {
            User sessionUser = (User) httpSession.getAttribute("user");

            User user = hibernateSession.find(User.class, sessionUser.getId());

            Address primaryAddress = user.getAddresses().stream()
                    .filter(Address::isPrimary)
                    .findFirst()
                    .orElse(null);

            double shippingPrice = 0.0;
            if(primaryAddress != null && primaryAddress.getCity() != null && primaryAddress.getCity().getDistrict() != null) {
                shippingPrice = primaryAddress.getCity().getDistrict().getShipping();
            }

            List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user = :user ORDER BY c.id DESC", Cart.class)
                    .setParameter("user", user)
                    .getResultList();

            if(!cartList.isEmpty()){
                JsonObject cartResponse = buildCartResponse(hibernateSession, null, cartList, shippingPrice);

                responseObject.add("cartItems", cartResponse.get("cartItems"));
                responseObject.addProperty("cartTotal", cartResponse.get("cartTotal").getAsDouble());
                responseObject.addProperty("cartTotalWithoutDiscount", cartResponse.get("cartTotalWithoutDiscount").getAsDouble());
                responseObject.addProperty("shippingPrice", shippingPrice);
                responseObject.addProperty("grandTotal", cartResponse.get("grandTotal").getAsDouble());
                status = true;
            }
        }

        if (httpSession != null && httpSession.getAttribute("cart") != null) {
            Map<Integer, Integer> cartSession = (Map<Integer, Integer>) httpSession.getAttribute("cart");

            if (!cartSession.isEmpty()) {
                JsonObject cartResponse = buildCartResponse(hibernateSession, cartSession, null, 300.0);

                responseObject.add("cartItems", cartResponse.get("cartItems"));
                responseObject.addProperty("cartTotal", cartResponse.get("cartTotal").getAsDouble());
                responseObject.addProperty("cartTotalWithoutDiscount", cartResponse.get("cartTotalWithoutDiscount").getAsDouble());
                responseObject.addProperty("shippingPrice", 0);
                responseObject.addProperty("grandTotal", cartResponse.get("grandTotal").getAsDouble());
                status = true;

            }
        }

        responseObject.addProperty("status",status);
        responseObject.addProperty("message",message);
        return AppUtil.gson.toJson(responseObject);
    }
    private JsonObject buildCartResponse(Session hibernateSession, Map<Integer, Integer> sessionCart, List<Cart> userCart, double shippingPrice) {
        JsonArray cartArray = new JsonArray();
        double cartTotal = 0;
        double cartTotalWithoutDiscount = 0;

        // CASE 1: Logged-in user cart
        if (userCart != null) {
            for (Cart c : userCart) {
                int qty = c.getQty();
                Stock stock = c.getStock();

                JsonObject item = buildCartItem(stock, qty);
                double total = item.get("total").getAsDouble();
                double totalWithoutDiscount = item.get("totalWithoutDiscount").getAsDouble();

                cartTotal += total;
                cartTotalWithoutDiscount += totalWithoutDiscount;
                cartArray.add(item);
            }
        }

        // CASE 2: Session cart
        if (sessionCart != null) {
            for (Map.Entry<Integer, Integer> entry : sessionCart.entrySet()) {
                int stockId = entry.getKey();
                int qty = entry.getValue();

                Stock stock = hibernateSession.find(Stock.class, stockId);
                if (stock == null) continue;

                JsonObject item = buildCartItem(stock, qty);
                double total = item.get("total").getAsDouble();
                double totalWithoutDiscount = item.get("totalWithoutDiscount").getAsDouble();

                cartTotal += total;
                cartTotalWithoutDiscount += totalWithoutDiscount;
                cartArray.add(item);
            }
        }

        // Final JSON Response
        JsonObject response = new JsonObject();
        response.add("cartItems", cartArray);
        response.addProperty("cartTotal", cartTotal);
        response.addProperty("cartTotalWithoutDiscount", cartTotalWithoutDiscount);
        response.addProperty("shippingPrice", shippingPrice);
        response.addProperty("grandTotal", cartTotal + shippingPrice);

        return response;
    }
    private JsonObject buildCartItem(Stock stock, int qty) {
        StockDTO stockDTO = new StockDTO();
        stockDTO.setStockId(stock.getId());
        stockDTO.setPrice(stock.getPrice().toString());
        stockDTO.setQty(stock.getQty());
        stockDTO.setColorCode(stock.getColor().getCode());
        stockDTO.setColorName(stock.getColor().getValue());
        stockDTO.setSizeName(stock.getSize().getValue());
        stockDTO.setDiscountId(stock.getDiscount().getId());
        stockDTO.setDiscountValue(stock.getDiscount().getValue());
        stockDTO.setStatusId(stock.getStatus().getId());

        Date now = new Date();

        double total;
        double totalWithoutDiscount = 0;

        if (stock.getDiscount().getId() != 1 && stock.getDiscount().getExpiredAt().after(now)) {
            double newPrice = stock.getPrice() - ((stock.getPrice() / 100) * stock.getDiscount().getValue());
            stockDTO.setNewPrice(newPrice);
            total = newPrice * qty;
            totalWithoutDiscount = stock.getPrice() * qty;
        } else {
            total = stock.getPrice() * qty;
        }

        Product product = stock.getProduct();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getId());
        productDTO.setTitle(product.getTitle());
        productDTO.setBrandName(product.getBrand().getName());

        List<String> images = product.getImages();
        if (images != null && !images.isEmpty()) {
            productDTO.setImgPath1(images.get(0));
        }

        JsonObject item = new JsonObject();
        item.add("product", AppUtil.gson.toJsonTree(productDTO));
        item.add("stock", AppUtil.gson.toJsonTree(stockDTO));
        item.addProperty("cartQty", qty);
        item.addProperty("total", total);
        item.addProperty("totalWithoutDiscount", totalWithoutDiscount);

        return item;
    }

    public String addProductToCart(StockDTO stockDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        int stockId = stockDTO.getStockId();

        Session hibernateSession =  HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = hibernateSession.beginTransaction();

        Stock stock = hibernateSession.find(Stock.class, stockId);

        HttpSession httpSession = request.getSession(true);
        if(httpSession.getAttribute("admin") != null){
            message = "Admins can't do cart operations!";
        }else if(httpSession.getAttribute("guest") != null){
            message = "Guest accounts can't do cart operations!";
        }else if(httpSession.getAttribute("user") != null){
            User sessionUser = (User) httpSession.getAttribute("user");

            User user = hibernateSession.find(User.class, sessionUser.getId());

            if(user == null || stock == null){
                message = "User or Stock not found!";
            } else {
                if(stock.getQty() > 0) {
                    List<Cart> cartList = hibernateSession.createQuery(
                                    "FROM Cart c WHERE c.user = :user AND c.stock = :stock", Cart.class)
                            .setParameter("user", user)
                            .setParameter("stock", stock)
                            .getResultList();

                    if (!cartList.isEmpty()) {
                        Cart existingCart = cartList.get(0);
                        if (stock.getQty() > (existingCart.getQty() + stockDTO.getQty())) {
                            existingCart.setQty(existingCart.getQty() + stockDTO.getQty());
                            message = "Product quantity updated in cart!";
                            status = true;
                        } else {
                            existingCart.setQty(stock.getQty());
                            message = "Only " + stock.getQty() + " items available in stock. Cart quantity set to maximum available.";
                            status = true;
                        }
                    } else {
                        Cart cart = new Cart();
                        cart.setQty(stockDTO.getQty());
                        cart.setStock(stock);
                        cart.setUser(user);
                        hibernateSession.persist(cart);
                        status = true;
                        message = "Product Added to Cart!";
                    }
                }else{
                    message = "This item is out of stock!";
                }
            }
        }else{
            Map<Integer, Integer> cartSession = (Map<Integer, Integer>) httpSession.getAttribute("cart");

            if (cartSession == null) {
                cartSession = new HashMap<>();
                httpSession.setAttribute("cart", cartSession);
            }

            if(stock.getQty() > 0) {
                cartSession.put(stockId, cartSession.getOrDefault(stockId, 0) + stockDTO.getQty());
                message = "Product added to session cart!";
            }else{
                message = "This item is out of stock!";
            }

            status = true;
        }

        try {
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            message = "Product Failed Add To Cart!";
        }

        hibernateSession.close();
        responseObject.addProperty("status",status);
        responseObject.addProperty("message",message);
        return AppUtil.gson.toJson(responseObject);
    }
    /// CART LOGIC HANDLING PART START

    /// WISHLIST LOGIC HANDLING PART START
    public String deleteWishlistItem(int id, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("user") != null) {
            User sessionUser = (User)session.getAttribute("user");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            Wishlist wishlist = hibernateSession.get(Wishlist.class, id);

            if(wishlist != null){
                if(wishlist.getUser().getId() != sessionUser.getId()){
                    message = "You are not allowed to delete this item!";
                }else{
                    try{
                        hibernateSession.delete(wishlist);
                        transaction.commit();
                        status = true;
                        message = "Wishlist item removed!";
                    }catch (HibernateException e){
                        transaction.rollback();
                        message = "Wishlist item delete failed!";
                        e.printStackTrace();
                    }
                }
            }else{
                message = "Wishlist item does not exist!";
            }

            hibernateSession.close();
        }else{
            message = "Please login first!";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    public String loadWishlistData(@Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if(httpSession == null ||  httpSession.getAttribute("user") == null){
            message = "Session Expired! Please logged in again.";
        }else {
            User sessionUser = (User) httpSession.getAttribute("user");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User user = hibernateSession.find(User.class, sessionUser.getId());

            List<Wishlist> wishlist = hibernateSession.createQuery("FROM Wishlist w WHERE w.user = :user ORDER BY w.id DESC", Wishlist.class)
                    .setParameter("user", user)
                    .getResultList();

            if(!wishlist.isEmpty()){
                JsonArray wishlistArray = new JsonArray();
                for(Wishlist w : wishlist){
                    Stock stock = w.getStock();
                    StockDTO stockDTO = new StockDTO();
                    stockDTO.setStockId(stock.getId());
                    stockDTO.setPrice(stock.getPrice().toString());
                    stockDTO.setQty(stock.getQty());
                    stockDTO.setColorCode(stock.getColor().getCode());
                    stockDTO.setColorName(stock.getColor().getValue());
                    stockDTO.setSizeName(stock.getSize().getValue());
                    stockDTO.setDiscountId(stock.getDiscount().getId());
                    stockDTO.setDiscountValue(stock.getDiscount().getValue());
                    stockDTO.setWishlistId(w.getId());

                    Date now = new Date();

                    if(stock.getDiscount().getId() != 1 && stock.getDiscount().getExpiredAt().after(now)){
                        double newPrice = stock.getPrice() - ((stock.getPrice() / 100) * stock.getDiscount().getValue());
                        stockDTO.setNewPrice(newPrice);
                    }

                    Product product = stock.getProduct();
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setProductId(product.getId());
                    productDTO.setTitle(product.getTitle());
                    productDTO.setBrandName(product.getBrand().getName());

                    List<String> images = product.getImages();
                    if (images != null && !images.isEmpty()) {
                        productDTO.setImgPath1(images.get(0));
                    }

                    JsonObject item = new JsonObject();
                    item.add("product", AppUtil.gson.toJsonTree(productDTO));
                    item.add("stock", AppUtil.gson.toJsonTree(stockDTO));

                    wishlistArray.add(item);
                }

                responseObject.add("wishlistItems", AppUtil.gson.toJsonTree(wishlistArray));
                status = true;
            }
        }

        responseObject.addProperty("status",status);
        responseObject.addProperty("message",message);
        return AppUtil.gson.toJson(responseObject);
    }
    public String addProductToWishlist(StockDTO stockDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        int stockId = stockDTO.getStockId();

        HttpSession httpSession = request.getSession(false);
        if(httpSession == null ||  httpSession.getAttribute("user") == null){
            message = "Please logging first!.";
        }else{
            User sessionUser = (User) httpSession.getAttribute("user");

            Session hibernateSession =  HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            User user = hibernateSession.find(User.class, sessionUser.getId());
            Stock stock = hibernateSession.find(Stock.class, stockId);

            List<Wishlist> wishlist = hibernateSession.createQuery("FROM Wishlist w WHERE w.user = :user", Wishlist.class)
                    .setParameter("user", user)
                    .getResultList();

            boolean stockAlreadyExists = false;
            if(!wishlist.isEmpty()){
                for(Wishlist w : wishlist){
                    if(w.getStock().getId() == stockId){
                        stockAlreadyExists = true;
                        break;
                    }
                }
            }

            if(stockAlreadyExists){
                status = true;
                message = "Product already exists in wishlist!";
            }else {
                Wishlist wishlistObj = new Wishlist();
                wishlistObj.setQty(1);
                wishlistObj.setStock(stock);
                wishlistObj.setUser(user);

                try {
                    hibernateSession.persist(wishlistObj);
                    transaction.commit();
                    status = true;
                    message = "Product Added to Wishlist!";
                }catch (HibernateException e){
                    transaction.rollback();
                    message = "Product Failed Add To Wishlist!";
                }
            }

            hibernateSession.close();
        }

        responseObject.addProperty("status",status);
        responseObject.addProperty("message",message);
        return AppUtil.gson.toJson(responseObject);
    }
    /// WISHLIST LOGIC HANDLING PART END

    /// USER SIDE SINGLE PRODUCT VIEW PAGE DATA LOADING PART START
    public String getStockByColorAndSize(int productId, int colorId, int sizeId, int checkId) {
        JsonObject responseObject = new JsonObject();
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Stock> stockList = hibernateSession.createQuery(
                "FROM Stock s WHERE s.product.id = :productId AND s.color.id = :colorId AND s.size.id = :sizeId", Stock.class
        ).setParameter("productId", productId).setParameter("colorId", colorId).setParameter("sizeId", sizeId).getResultList();

        if (stockList != null && !stockList.isEmpty()) {
            Map<String, Object> stockMap = getBestStock(stockList);
            responseObject.add("stock", AppUtil.gson.toJsonTree(stockMap));
            message = "Stock data loaded successfully";
        } else {
            List<Stock> stockList2 = hibernateSession.createQuery(
                    "FROM Stock s WHERE s.product.id = :productId AND s.color.id = :colorId", Stock.class
            ).setParameter("productId", productId).setParameter("colorId", colorId).getResultList();
            Map<String, Object> stockMap = getBestStock(stockList2);
            responseObject.add("stock", AppUtil.gson.toJsonTree(stockMap));
            message = "Stock data loaded successfully";
        }

        if(checkId == 1){ //Color Select -> shoud get Sizes list
            List<Size> sizeList = hibernateSession.createQuery(
                    "SELECT DISTINCT s FROM Size s JOIN Stock st ON st.size.id = s.id WHERE st.product.id = :productId AND st.color.id = :colorId", Size.class
            ).setParameter("productId", productId).setParameter("colorId", colorId).getResultList();
            responseObject.add("sizes", AppUtil.gson.toJsonTree(ProductService.sizes(sizeList)));
        }

        hibernateSession.close();
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String loadSingleProductViewData(int stockId){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Stock stock = hibernateSession.createQuery("FROM Stock s WHERE s.id = :id AND s.status.id = 1", Stock.class)
                .setParameter("id", stockId)
                .getSingleResultOrNull();

        Product product = hibernateSession.createQuery("FROM Product p WHERE p.id = :id", Product.class)
                .setParameter("id", stock.getProduct().getId())
                .getSingleResultOrNull();

        if(stock == null){
            message = "Product not found";
        }else if(product == null){
            message = "Stock not found";
        }else{
            List<Color> colorList = hibernateSession.createQuery(
                    "SELECT DISTINCT c FROM Color c JOIN Stock s ON s.color.id = c.id WHERE s.product.id = :productId", Color.class
            ).setParameter("productId", product.getId()).getResultList();

            List<Size> sizeList = hibernateSession.createQuery(
                    "SELECT DISTINCT s FROM Size s JOIN Stock st ON st.size.id = s.id WHERE st.product.id = :productId AND st.color.id = :colorId", Size.class
            ).setParameter("productId", product.getId()).setParameter("colorId", stock.getColor().getId()).getResultList();

            if(stock.getProduct().getId() == product.getId()){
                ProductDTO productDTO = new ProductDTO();
                productDTO.setProductId(product.getId());
                productDTO.setTitle(product.getTitle());
                productDTO.setDescription(product.getDescription());
                productDTO.setBrandId(product.getBrand().getId());
                productDTO.setBrandName(product.getBrand().getName());
                productDTO.setCategoryId(product.getSubCategory().getCategory().getId());
                productDTO.setCategoryName(product.getSubCategory().getCategory().getName());
                productDTO.setSubCategoryId(product.getSubCategory().getId());
                productDTO.setSubCategoryName(product.getSubCategory().getName());

                List<String> images = product.getImages();
                if (images != null && !images.isEmpty()) {
                    productDTO.setImgPath1(images.get(0));
                    if (images.size() > 1) {
                        productDTO.setImgPath2(images.get(1));
                    }
                    if (images.size() > 2) {
                        productDTO.setImgPath3(images.get(2));
                    }
                }

                StockDTO stockDTO = new StockDTO();
                stockDTO.setStockId(stock.getId());
                stockDTO.setPrice(stock.getPrice().toString());
                stockDTO.setQty(stock.getQty());
                stockDTO.setColorId(stock.getColor().getId());
                stockDTO.setColorName(stock.getColor().getValue());
                stockDTO.setSizeId(stock.getSize().getId());
                stockDTO.setSizeName(stock.getSize().getValue());
                stockDTO.setDiscountId(stock.getDiscount().getId());
                stockDTO.setDiscountValue(stock.getDiscount().getValue());

                Date now = new Date();

                if(stock.getDiscount().getId() != 1 && stock.getDiscount().getExpiredAt().after(now)){
                    double newPrice = stock.getPrice() - ((stock.getPrice() / 100) * stock.getDiscount().getValue());
                    stockDTO.setNewPrice(newPrice);
                    stockDTO.setValidDiscount(true);
                }else{
                    stockDTO.setValidDiscount(false);
                }

                responseObject.add("product", AppUtil.gson.toJsonTree(productDTO));
                responseObject.add("stock", AppUtil.gson.toJsonTree(stockDTO));
                responseObject.add("colors", AppUtil.gson.toJsonTree(ProductService.colors(colorList)));
                responseObject.add("sizes", AppUtil.gson.toJsonTree(ProductService.sizes(sizeList)));

                status = true;
                message = "Data successfully loaded";
            }else{
                message = "Stock not found";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        hibernateSession.close();
        return AppUtil.gson.toJson(responseObject);
    }
    private static List<JsonObject> colors(List<Color> colorList){
        List<JsonObject> json = new ArrayList<>();
        for(Color c : colorList){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", c.getId());
            obj.addProperty("name", c.getValue());
            json.add(obj);
        }
        return json;
    }
    private static List<JsonObject> sizes(List<Size> sizeList){
        List<JsonObject> json = new ArrayList<>();
        for(Size s : sizeList){
            JsonObject obj = new JsonObject();
            obj.addProperty("id", s.getId());
            obj.addProperty("name", s.getValue());
            json.add(obj);
        }
        return json;
    }
    /// USER SIDE SINGLE PRODUCT VIEW PAGE DATA LOADING PART END

    /// USER SIDE PRODUCTS CARD DATA LOADING PART START
    public String sortProducts(double minPrice, double maxPrice, int colorId, int sizeId, int categoryId,
            int subCategoryId, int brandId, String search, int order, int limit, int page) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // --- 1. FETCH FILTERED PRODUCTS ---
            ProductDAO dao = new ProductDAO(session);

            List<Product> products = dao.getFilteredProducts(
                    minPrice, maxPrice, colorId, sizeId, categoryId, subCategoryId, brandId, search, order, limit, page
            );

            long totalProducts = dao.countFilteredProducts(
                    minPrice, maxPrice, colorId, sizeId, categoryId, subCategoryId, brandId, search
            );

            int totalPages = (int) Math.ceil((double) totalProducts / limit);

            // --- 2. BUILD PRODUCT LIST (same format as getBestStockForProductCard()) ---
            List<Map<String, Object>> productArrayList = new ArrayList<>();
            for (Product p : products) {

                Map<String, Object> stockMap = dao.getBestStockForShopPage(p, session, colorId, sizeId, minPrice, maxPrice);

                // Skip product if no valid stock
                if (stockMap == null) continue;

                Map<String, Object> productMap = new HashMap<>();
                productMap.put("productId", p.getId());
                productMap.put("title", p.getTitle());
                productMap.put("brandName", p.getBrand().getName());
                productMap.put("createdAt", p.getCreatedAt().toString());

                List<String> images = p.getImages();
                if (images != null && !images.isEmpty()) {
                    productMap.put("imgPath1", images.get(0));
                }

                productMap.put("stock", stockMap);

                productArrayList.add(productMap);
            }

            // --- 3. ADD JSON OUTPUT ---
            responseObject.add("products", AppUtil.gson.toJsonTree(productArrayList));
            responseObject.addProperty("totalProducts", totalProducts);
            responseObject.addProperty("totalPages", totalPages);
            responseObject.addProperty("currentPage", page);

            status = true;
            message = "Products sorted successfully";

        } catch (Exception e) {
            e.printStackTrace();
            message = "Sorting failed due to server error";
        } finally {
            session.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.gson.toJson(responseObject);
    }

    public String getAllProducts(String sortBy, String order, int limit){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        String hql = "FROM Product p ORDER BY p." + sortBy + " " + order;
        List<Product> productList = hibernateSession.createQuery(hql,Product.class).setMaxResults(limit).getResultList();
        Set<Product> productSet = new LinkedHashSet<>(productList);

        if(productSet.isEmpty()){
            message = "Products Not Found";
        }else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            List<Map<String, Object>> productArrayList = new ArrayList<>();

            for (Product p : productSet) {
                Map<String, Object> stockMap = getBestStockForProductCard(p, hibernateSession);
                if (stockMap == null) {
                    stockMap = new HashMap<>();
                    stockMap.put("stockId", 0);
                    stockMap.put("price", 0);
                    stockMap.put("qty", 0); // include qty!
                    stockMap.put("discountId", 1);
                }

                int stockQty = (int) stockMap.getOrDefault("qty", 0);
                if (stockQty <= 0) {
                    continue; // skip product
                }

                Map<String, Object> productMap = new HashMap<>();
                productMap.put("productId", p.getId());
                productMap.put("title", p.getTitle());
                productMap.put("brandName", p.getBrand().getName());
                productMap.put("createdAt", p.getCreatedAt().toString());

                List<String> images = p.getImages();
                if (images != null && !images.isEmpty()) {
                    productMap.put("imgPath1", images.get(0));
                }

                productMap.put("stock", stockMap);
                productArrayList.add(productMap);
            }

            responseObject.add("products", AppUtil.gson.toJsonTree(productArrayList));
            status = true;
            message = "Product loading Successful";
        }


        hibernateSession.close();

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.gson.toJson(responseObject);
    } //Home Product Loading Part

    public Map<String, Object> getBestStockForProductCard(Product product, Session hibernateSession) {
        List<Stock> stockList = hibernateSession.createQuery(
                        "FROM Stock s WHERE s.product = :product AND s.status.id = 1 AND s.qty > 0 ORDER BY s.id ASC", Stock.class
                )
                .setParameter("product", product)
                .getResultList();

        if (stockList.isEmpty()) {
            return null;
        }

        Map<String, Object> stockMap = getBestStock(stockList);

        return stockMap;
    }
    private Map<String, Object> getBestStock(List<Stock> stockList) {

        if (stockList == null || stockList.isEmpty()) {
            return null;
        }

        Stock bestStockWithValidDiscount = null;
        Discount bestDiscountWithValidDiscount = null;

        Stock bestStockWithDefault = null;
        Discount bestDiscountWithDefault = null;

        Date now = new Date();

        for (Stock s : stockList) {

            if (s == null || s.getQty() <= 0) {
                continue;
            }

            Discount d = s.getDiscount();

            // Valid non-default discount
            if (d != null && d.getId() != 1 && d.getExpiredAt() != null && d.getExpiredAt().after(now)) {
                bestStockWithValidDiscount = s;
                bestDiscountWithValidDiscount = d;
                break;
            }

            // Default or no discount
            if (bestStockWithDefault == null) {
                bestStockWithDefault = s;
                bestDiscountWithDefault = d; // may be null (allowed)
            }
        }

        Stock selectedStock;
        Discount selectedDiscount;

        if (bestStockWithValidDiscount != null) {
            selectedStock = bestStockWithValidDiscount;
            selectedDiscount = bestDiscountWithValidDiscount;
        } else {
            selectedStock = bestStockWithDefault;
            selectedDiscount = bestDiscountWithDefault;
        }

        // 🔒 FINAL SAFETY CHECK
        if (selectedStock == null) {
            return null;
        }

        Map<String, Object> stockMap = new HashMap<>();

        stockMap.put("stockId", selectedStock.getId());
        stockMap.put("price", selectedStock.getPrice());
        stockMap.put("qty", selectedStock.getQty());
        stockMap.put("sizeId", selectedStock.getSize().getId());
        stockMap.put("sizeName", selectedStock.getSize().getValue());

        if (selectedDiscount != null && selectedDiscount.getExpiredAt().after(now)) {
            stockMap.put("discountId", selectedDiscount.getId());
            stockMap.put("discountName", selectedDiscount.getDiscountName());
            stockMap.put("discountValue", selectedDiscount.getValue());
            stockMap.put("discountExpiredAt",
                    selectedDiscount.getExpiredAt() != null
                            ? selectedDiscount.getExpiredAt().toString()
                            : null
            );

            if (selectedDiscount.getId() != 1) {
                double newPrice = selectedStock.getPrice()
                        - ((selectedStock.getPrice() / 100) * selectedDiscount.getValue());
                stockMap.put("newPrice", newPrice);
            }
        } else {
            // No discount case
            stockMap.put("discountId", 1);
            stockMap.put("discountName", "DEFAULT");
            stockMap.put("discountValue", 0);
        }

        return stockMap;
    } /// Usage -> getBestStockForProductCard(), getStockByColorAndSize(),
       /// USER SIDE PRODUCTS CARD DATA LOADING PART END

    /// ADMIN SIDE PART START
    public String changeStockStatus(StockDTO stockDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status2 = false;
        String message = "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(stockDTO.getStockId()  <= 0){
            message = "Stock not found!";
        }else if(stockDTO.getStatusId() <= 0){
            message = "Please select valid status!";
        }else{
            HttpSession session = request.getSession(false);
            if(session != null && session.getAttribute("admin") != null){
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = hibernateSession.beginTransaction();

                Stock stock = hibernateSession.find(Stock.class, stockDTO.getStockId());
                Status status = hibernateSession.find(Status.class, stockDTO.getStatusId());

                if (stock == null) {
                    message = "Stock not found!";
                } else if (status == null) {
                    message = "Status not found!";
                } else {
                    try {
                        stock.setStatus(status);
                        hibernateSession.merge(stock); // IMPORTANT!
                        transaction.commit();
                        status2 = true;
                        message = "Status changed successfully!";
                    }catch (HibernateException e) {
                        transaction.rollback();
                        message = "Status Changed failed!";
                    }
                }
            }else{
                message = "Please login first!";
            }
        }

        responseObject.addProperty("status2", status2);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    public String addStockDiscount(StockDTO stockDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(stockDTO.getStockId()  <= 0){
            message = "Stock not found!";
        }else if(stockDTO.getDiscountId() <= 0){
            message = "Please select discount!";
        }else{
            HttpSession session = request.getSession(false);
            if(session != null && session.getAttribute("admin") != null){
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = hibernateSession.beginTransaction();

                Stock stock = hibernateSession.find(Stock.class, stockDTO.getStockId());
                Discount discount = hibernateSession.find(Discount.class, stockDTO.getDiscountId());

                if (stock == null) {
                    message = "Stock not found in database!";
                } else if (discount == null) {
                    message = "Discount not found!";
                } else {
                    try {
                        responseObject.add("discount", AppUtil.gson.toJsonTree(discount));
                        responseObject.addProperty("price",  stock.getPrice());

                        stock.setDiscount(discount);
                        hibernateSession.merge(stock); // IMPORTANT!
                        transaction.commit();
                        status = true;
                        message = "Discount added successfully!";
                    }catch (HibernateException e) {
                        transaction.rollback();
                        message = "Discount added failed!";
                    }
                }
            }else{
                message = "Please login first!";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String getStockByProductId(int productId) {
        JsonObject responseObject = new JsonObject();
        String message = "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Product product = hibernateSession.find(Product.class, productId);

        if(product == null) {
            message = "Product doesn't exist!";
        }else{
            List<Stock> stockList = hibernateSession.createQuery("FROM Stock s WHERE s.product = :product", Stock.class)
                    .setParameter("product", product)
                    .getResultList();

            int totalQty = 0;

            List<StockDTO> stockDTOList = new ArrayList<>();
            for(Stock stock : stockList) {
                StockDTO stockDTO = new StockDTO();
                stockDTO.setStockId(stock.getId());
                stockDTO.setQty(stock.getQty());
                stockDTO.setPrice(String.valueOf(stock.getPrice()));
                stockDTO.setColorId(stock.getColor().getId());
                stockDTO.setColorName(stock.getColor().getValue());
                stockDTO.setSizeId(stock.getSize().getId());
                stockDTO.setSizeName(stock.getSize().getValue());
                stockDTO.setCreatedAt(formatter.format(stock.getCreatedAt()));
                stockDTO.setStatusId(stock.getStatus().getId());
                stockDTO.setDiscountId(stock.getDiscount().getId());

                totalQty += stock.getQty();

                stockDTOList.add(stockDTO);
            }
            responseObject.add("stocks", AppUtil.gson.toJsonTree(stockDTOList));
            responseObject.add("totalQty", AppUtil.gson.toJsonTree(totalQty));
            message = "Stock data loaded successfully!";
        }

        hibernateSession.close();

        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    public String sortAdminProducts(String search, int order, int limit, int page, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);

        if(httpSession != null && httpSession.getAttribute("admin") != null) {
            Session session = HibernateUtil.getSessionFactory().openSession();

            try {
                // --- 1. FETCH FILTERED PRODUCTS ---
                AdminProductDAO dao = new AdminProductDAO(session);

                List<Product> productsList = dao.getFilteredProducts(
                        search, order, limit, page, httpSession
                );
                long totalProducts = dao.getFilteredProductsCount(
                        search, order, httpSession
                );

                int totalPages = (int) Math.ceil((double) totalProducts / limit);


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                List<ProductDTO> productDTOList = new ArrayList<>();
                for(Product p : productsList) {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setProductId(p.getId());
                    productDTO.setTitle(p.getTitle());
                    productDTO.setDescription(p.getDescription());
                    productDTO.setBrandName(p.getBrand().getName());
                    productDTO.setSubCategoryName(p.getSubCategory().getName());
                    productDTO.setCategoryName(p.getSubCategory().getCategory().getName());

                    // ✅ SAFE STOCK COUNT
                    int totalQty = 0;
                    Set<Stock> stocks = p.getStocks();
                    if (stocks != null) {
                        for (Stock stock : stocks) {
                            totalQty += stock.getQty();
                        }
                    }

                    productDTO.setInStock(totalQty > 0);

                    List<String> images = p.getImages();
                    if (images != null && !images.isEmpty()) {
                        productDTO.setImgPath1(images.get(0));

//                            if (images.size() > 1) {
//                                productDTO.setImgPath2(images.get(1));
//                            }
//                            if (images.size() > 2) {
//                                productDTO.setImgPath3(images.get(2));
//                            }
                    }

                    productDTOList.add(productDTO);
                }

                responseObject.add("product", AppUtil.gson.toJsonTree(productDTOList));
                responseObject.addProperty("totalPages", totalPages);
                responseObject.addProperty("currentPage", page);

                status = true;
                message = "Products sorted successfully";

            } catch (Exception e) {
                e.printStackTrace();
                message = "Sorting failed due to server error";
            } finally {
                session.close();
            }
        }else{
            message = "Please Login First";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.gson.toJson(responseObject);
    }

    public String addNewProductDetails(ProductDTO productDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        /// Product Inserting Part Start
        if(productDTO.getCategoryId() <= 0){
            message = "Please Select a Category!";
        }else if(productDTO.getSubCategoryId() <= 0){
            message = "Please Select a SubCategory!";
        }else if(productDTO.getBrandId() <= 0){
            message = "Please Select a Brand!";
        }else if (productDTO.getTitle() == null) {
            message = "Product title is required!";
        }else if (productDTO.getTitle().isBlank()){
            message = "Product title cannot be empty!";
        }else if (productDTO.getDescription() == null) {
            message = "Product description is required!";
        }else if (productDTO.getDescription().isBlank()) {
            message = "Product description cannot be empty!";
        }else{
            HttpSession httpSession = request.getSession(false);
            if(httpSession == null){
                message = "Session is expired! Please Logged in!";
            }else{
                if(httpSession.getAttribute("admin") == null){
                    message = "Session is expired! Please Logged in!";
                }else{
                    Admin adminUser = (Admin) httpSession.getAttribute("admin");

                    Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                    Admin admin = hibernateSession.createQuery("FROM Admin a WHERE a.email = :email", Admin.class)
                            .setParameter("email", adminUser.getEmail())
                            .getSingleResultOrNull();

                    if (admin == null) {
                        message = "This request can only be proceed by admin!";
                    }else{
                        SubCategory subCategory = hibernateSession.find(SubCategory.class, productDTO.getSubCategoryId());
                        if(subCategory == null){
                            message = "Sub-category does not exist!";
                        }else{
                            Brand brand = hibernateSession.find(Brand.class, productDTO.getBrandId());
                            if(brand == null){
                                message = "Brand does not exist!";
                            }else{
                                Product product = new Product();
                                product.setTitle(productDTO.getTitle());
                                product.setDescription(productDTO.getDescription());
                                product.setSubCategory(subCategory);
                                product.setBrand(brand);
                                product.setAdmin(admin);

                                Transaction transaction = hibernateSession.beginTransaction();

                                try {
                                    hibernateSession.persist(product);
                                    transaction.commit();
                                    status = true;
                                    message = "Product successfully added!";
                                    responseObject.addProperty("productId", product.getId());
                                }catch (HibernateException e){
                                    transaction.rollback();
                                    message = "Product failed to added!";
                                }
                            }
                        }
                    }
                    hibernateSession.close();
                }
            }
        }
        /// Product Inserting Part Start

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String addStockDetails(List<StockDTO> stockDTOList, int productId, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if(httpSession == null){
            message = "Session is expired! Please Logged in!";
        }else {
            if (httpSession.getAttribute("admin") == null) {
                message = "Session is expired! Please Logged in!";
            } else {
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                Product product = hibernateSession.find(Product.class, productId);

                if(product == null){
                    message = "Product does not exist!";
                }else{
                    for(StockDTO stockDTO : stockDTOList){
                        Color color = hibernateSession.find(Color.class, stockDTO.getColorId());
                        if(color == null){
                            message = "Color does not exist!";
                        }else{
                            Size size = hibernateSession.find(Size.class, stockDTO.getSizeId());
                            if(size == null){
                                message = "Color does not exist!";
                            }else{
                                Stock stock = new Stock();
                                stock.setProduct(product);
                                stock.setPrice(stockDTO.getPrice());
                                stock.setQty(stockDTO.getQty());
                                stock.setSize(size);
                                stock.setColor(color);

                                Status ApprovedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                                        .setParameter("value", String.valueOf(Status.Type.ACTIVE))
                                        .getSingleResult();
                                Discount defaultDiscount = hibernateSession.createNamedQuery("Discount.findByDefault", Discount.class)
                                        .getSingleResult();

                                stock.setStatus(ApprovedStatus);
                                stock.setDiscount(defaultDiscount);

                                Transaction transaction = hibernateSession.beginTransaction();
                                try {
                                    hibernateSession.persist(stock);
                                    transaction.commit();
                                    status = true;
                                    message = "Stock successfully added!";
                                }catch (HibernateException e){
                                    transaction.rollback();
                                    message = "Stock failed to added!";
                                }
                            }
                        }

                    }
                }
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String addProductImages(Product product) {
        JsonObject responseObject =  new JsonObject();
        boolean satus = false;
        String message = "";

        Session hibernateSession =  HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = hibernateSession.beginTransaction();
        try{
            hibernateSession.merge(product);
            transaction.commit();
            satus = true;
            message = "Product image uploaded successfully";
        }catch (HibernateException e){
            transaction.rollback();
            message = "Product image uploading failed";
        }finally {
            hibernateSession.close();
        }

        responseObject.addProperty("satus", satus);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    public Product getProductById(int id){
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Product product = hibernateSession.find(Product.class, id);
        hibernateSession.close();
        return product;
    }
    /// ADMIN SIDE PART END
}
