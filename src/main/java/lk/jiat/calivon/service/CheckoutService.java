package lk.jiat.calivon.service;

import com.google.gson.JsonObject;
import jakarta.persistence.criteria.Order;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.dto.CheckoutResponseDTO;
import lk.jiat.calivon.dto.PayHereDTO;
import lk.jiat.calivon.dto.UserDTO;
import lk.jiat.calivon.entity.*;
import lk.jiat.calivon.mail.VerificationMail;
import lk.jiat.calivon.provider.MailServiceProvider;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.Env;
import lk.jiat.calivon.util.HibernateUtil;
import lk.jiat.calivon.util.PayHereUtil;
import lk.jiat.calivon.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CheckoutService {
    public String createGuestAccount(UserDTO userDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        String firstName = userDTO.getFirstName();
        String lastName  = userDTO.getLastName();
        String email     = userDTO.getEmail();

        if(firstName == null || firstName.isBlank()) {
            message = "First Name is required!";
        }else if(lastName == null || lastName.isBlank()) {
            message = "Last Name is required!";
        }else if(email == null || email.isBlank()) {
            message = "Email is required!";
        } else if(!email.matches(Validator.EMAIL_VALIDATION)) {
            message = "Please enter a valid email address!";
        }else if(userDTO.getLineOne() == null) {
            message = "Address Line One is required!";
        }else if(userDTO.getLineOne().isBlank()) {
            message = "Address Line One can not be empty or blank!";
        }else if(userDTO.getProvinceId() == 0){
            message = "Please select a province!";
        }else if(userDTO.getDistrictId() == 0){
            message = "Please select a district!";
        }else if(userDTO.getCityId() == 0){
            message = "Please select a city!";
        }else if(userDTO.getPostalCode() != null && !userDTO.getPostalCode().isBlank() && !userDTO.getPostalCode().matches(Validator.POSTAL_CODE_VALIDATION)){
            message = "Enter a valid postal code!";
        } else if (userDTO.getMobile() == null || userDTO.getMobile().isBlank()) {
            message = "Mobile is required!";
        } else if (!userDTO.getMobile().matches(Validator.MOBILE_VALIDATION)) {
            message = "Please enter a valid mobile number!";
        }else{
            HttpSession session = request.getSession();

            if(session.getAttribute("cart") != null) {
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = null;

                try {
                    transaction = hibernateSession.beginTransaction();

                    User singleUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                            .setParameter("email", userDTO.getEmail())
                            .getSingleResultOrNull();

                    String verificationCode = AppUtil.generateCode();

                    if (singleUser != null) {
                        message = "The email already exists! Please use another email.";
                    } else {
                        User u = new User();
                        u.setFirstName(userDTO.getFirstName());
                        u.setLastName(userDTO.getLastName());
                        u.setEmail(userDTO.getEmail());

                        u.setPassword("Guest@" + verificationCode);
                        u.setVerificationCode(verificationCode);

                        Status pendingStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class) //Return Status Object
                                .setParameter("value", String.valueOf(Status.Type.GUEST))
                                .getSingleResult();
                        u.setStatus(pendingStatus);

                        Address address = new Address();
                        address.setLineOne(userDTO.getLineOne());
                        address.setLineTwo(userDTO.getLineTwo());
                        address.setPostalCode(userDTO.getPostalCode());
                        address.setMobile(userDTO.getMobile());
                        address.setPrimary(true);
                        address.setUser(u);

                        City city = hibernateSession.find(City.class, userDTO.getCityId());
                        address.setCity(city);

                        hibernateSession.persist(u);
                        hibernateSession.persist(address);

                        Map<Integer, Integer> cartSession = (Map<Integer, Integer>) session.getAttribute("cart");
                        if(!cartSession.isEmpty()){
                            for (Map.Entry<Integer, Integer> entry : cartSession.entrySet()) {
                                int stockId = entry.getKey();
                                int qty = entry.getValue();

                                Stock stock = hibernateSession.find(Stock.class, stockId);

                                Cart cartItem = new Cart();
                                cartItem.setQty(qty);
                                cartItem.setUser(u);
                                cartItem.setStock(stock);

                                hibernateSession.persist(cartItem);
                            }
                        }
                        session.removeAttribute("cart");
                        session.setAttribute("user", u);

                        responseObject.addProperty("userId", u.getId());
                        status = true;
                        message = "Guest Account successfully created.";
                    }
                }catch (HibernateException e) {
                    if (transaction != null) transaction.rollback();
                    message = "Account creation failed. Please try again!.";
                } finally {
                    hibernateSession.close();
                }
            }else{
                message = "Your cart session has expired! Please add items again.";
            }
        }
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String codCheckout(int deliveryTypeId, double cartTotal, @Context HttpServletRequest request){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction(); // ✅ START TX HERE

            CheckoutResponseDTO responseDTO = createOrder(deliveryTypeId, cartTotal, session, request);

            if (!responseDTO.isStatus()) {
                tx.rollback(); // ✅ rollback once
                return buildResponse(false, responseDTO.getMessage());
            }

            tx.commit();
            return buildResponse(responseDTO.isStatus(), responseDTO.getMessage());

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return buildResponse(false, "Order failed to create!");
        } finally {
            session.close();
        }
    }
    public String payHereCheckout(int deliveryTypeId, double cartTotal, HttpServletRequest request) {
        JsonObject response = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction(); // ✅ START TX HERE

            CheckoutResponseDTO responseDTO = createOrder(deliveryTypeId, cartTotal, session, request);

            if (!responseDTO.isStatus()) {
                tx.rollback(); // ✅ rollback once
                return buildResponse(false, responseDTO.getMessage());
            }

            tx.commit();

            response.add("PayHere", AppUtil.gson.toJsonTree(responseDTO.getPayHereDTO()));
            response.addProperty("status", true);
            response.addProperty("message", "Proceed to payment");

            return AppUtil.gson.toJson(response);

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return buildResponse(false, "Payment initialization failed!");
        } finally {
            session.close();
        }
    }
    private String buildResponse(boolean status, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("message", message);
        return AppUtil.gson.toJson(json);
    }

    public CheckoutResponseDTO createOrder(int deliveryTypeId, double cartTotal, Session session, @Context HttpServletRequest request){

        // 1. Delivery type validation
        DeliveryTypes deliveryType = session.createQuery(
                        "FROM DeliveryTypes dt WHERE dt.id = :id", DeliveryTypes.class)
                .setParameter("id", deliveryTypeId)
                .getSingleResultOrNull();

        if (deliveryType == null) {
            return errorOrderResponse("Invalid delivery type!");
        }

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) {
            return errorOrderResponse("Please login first!");
        }

        if (httpSession.getAttribute("user") == null) {
            return errorOrderResponse("Please login first!");
        }

        User sessionUser = (User) httpSession.getAttribute("user");

        User user = session.find(User.class, sessionUser.getId());

        if (user == null) {
            return errorOrderResponse("User not found!");
        }

        if (user.getStatus().getValue().equals(String.valueOf(Status.Type.GUEST))) {

            Orders singleOrder = session.createQuery("FROM Orders o WHERE o.user.id = :id ORDER BY o.createdAt DESC", Orders.class)
                    .setParameter("id", user.getId())
                    .setMaxResults(1)
                    .getSingleResultOrNull();

            if (singleOrder != null) {
                String orderStatus = singleOrder.getStatus().getValue();

                boolean isFinalState = orderStatus.equals(String.valueOf(Status.Type.REJECTED)) ||
                        orderStatus.equals(String.valueOf(Status.Type.RECEIVED)) ||
                        orderStatus.equals(String.valueOf(Status.Type.COMPLETED));

                if (!isFinalState) {
                    return errorOrderResponse(
                            "You already have an active order. Please wait or verify your account to place another."
                    );
                }
            }
        }

        if (user.getAddresses().isEmpty()) {
            return errorOrderResponse("Please add a delivery address to continue.");
        }

        Address primaryAddress = session.createQuery("FROM Address a WHERE a.user.id = :uid AND a.isPrimary = true", Address.class)
                .setParameter("uid", user.getId())
                .getSingleResultOrNull();

        if (primaryAddress == null) {
            return errorOrderResponse("Please select a primary delivery address to continue.");
        }

        List<Cart> cartList = session.createQuery(
                        "FROM Cart c WHERE c.user.id = :uid", Cart.class)
                .setParameter("uid", user.getId())
                .getResultList();

        if (cartList.isEmpty()) {
            return errorOrderResponse("Your cart is empty!");
        }

        Status pendingStatus = session.createNamedQuery("Status.findByValue", Status.class)
                .setParameter("value", String.valueOf(Status.Type.PENDING))
                .getSingleResult();

        double shippingPrice = primaryAddress.getCity().getDistrict().getShipping();

        double finalPrice = cartTotal + deliveryType.getPrice() + shippingPrice;

        Orders order = new Orders();
        order.setUser(user);
        order.setPrice(finalPrice);
        order.setDeliveryTypes(deliveryType);
        order.setStatus(pendingStatus);

        session.persist(order);

        StringBuilder items = new StringBuilder();

        for (Cart c : cartList) {
            if(deliveryType.getId() == 1) { //Card
                items.append(c.getStock().getProduct().getTitle())
                        .append(" x ")
                        .append(c.getQty())
                        .append(", ");
            }

            if (!c.getStock().getStatus().getValue().equals(String.valueOf(Status.Type.ACTIVE))
                    || c.getStock().getQty() <= 0) {

                throw new RuntimeException(
                        c.getStock().getProduct().getTitle() + " is not available!"
                );
            }

            Stock stock = c.getStock();
            double buyingPrice = stock.getPrice();
            if (stock.getDiscount().getId() != 1 && new Date().before(stock.getDiscount().getExpiredAt())) {
                buyingPrice = stock.getPrice() - ((stock.getPrice() / 100) * stock.getDiscount().getValue());
            }

            int availableQty = stock.getQty();
            int cartQty = c.getQty();
            int buyQty = Math.min(c.getQty(), c.getStock().getQty());
            if(cartQty > availableQty) {
                buyQty = availableQty;
            }

            OrderItems item = new OrderItems();
            item.setOrders(order);
            item.setStock(c.getStock());
            item.setQty(buyQty);
            item.setRating(0);
            item.setBuyingPrice(buyingPrice);

            Status defaultStatus = session.createNamedQuery("Status.findByValue", Status.class) //Return Status Object
                    .setParameter("value", String.valueOf(Status.Type.ACTIVE))
                    .getSingleResult();
            item.setStatus(defaultStatus);

            if(deliveryType.getId() == 2) { //COD Payment
                c.getStock().setQty(c.getStock().getQty() - buyQty);
            }

            session.persist(item);
        }

        // 3. Snapshot order details (VERY GOOD DESIGN)
        OrderDetails details = new OrderDetails();
        details.setOrders(order);
        details.setName(user.getFirstName() + " " + user.getLastName());
        details.setLineOne(primaryAddress.getLineOne());
        details.setLineTwo(primaryAddress.getLineTwo());
        details.setPostalCode(primaryAddress.getPostalCode());
        details.setMobile(primaryAddress.getMobile());
        details.setCity(primaryAddress.getCity());

        session.persist(details);

        // 4. Clear cart
        session.createQuery("DELETE FROM Cart WHERE user.id = :uid")
                .setParameter("uid", user.getId())
                .executeUpdate();

        PayHereDTO payHereDTO = null;

        if(deliveryType.getId() == 1) { //Card
            String orderId = order.getIdAsString();
            String hash = PayHereUtil.generateHash(
                    orderId,
                    finalPrice,
                    AppUtil.MAIN_CURRENCY
            );

            payHereDTO = new PayHereDTO();
            payHereDTO.setSandbox(true);
            payHereDTO.setMerchantId(PayHereUtil.getMerchantId());
            payHereDTO.setOrderId(orderId);
            payHereDTO.setItems(items.toString());
            payHereDTO.setAmount(String.format("%.2f", finalPrice));
            payHereDTO.setCurrency(AppUtil.MAIN_CURRENCY);
            payHereDTO.setHash(hash);

            payHereDTO.setFirstName(user.getFirstName());
            payHereDTO.setLastName(user.getLastName());
            payHereDTO.setEmail(user.getEmail());
            payHereDTO.setPhone(primaryAddress.getMobile());
            payHereDTO.setAddress(primaryAddress.getLineOne());
            payHereDTO.setCity(primaryAddress.getCity().getName());
            payHereDTO.setCountry(AppUtil.APP_COUNTRY);

            payHereDTO.setReturnURL(Env.get("app.public.url") + "/payment-success.html");
            payHereDTO.setCancelURL(Env.get("app.public.url") + "/payment-cancel.html");
            payHereDTO.setNotifyURL(Env.get("app.public.url") + "/api/payments/notify");
        }

        return successOrderResponse("Order successfully created!", payHereDTO);
    }
    public static CheckoutResponseDTO errorOrderResponse(String message) {
        return new CheckoutResponseDTO(false, message);
    }
    public static CheckoutResponseDTO successOrderResponse(String message, PayHereDTO dto) {
        return new CheckoutResponseDTO(true, message, dto);
    }

    public void finalizePayHereOrder(int orderId){
        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            try {
                Orders order = hibernateSession.find(Orders.class, orderId);
                if (order == null) {
                    throw new RuntimeException("Order not found for ID: " + orderId);
                }

                // Update stock quantities
                Set<OrderItems> orderItems = order.getOrderItems();
                if (orderItems != null) {
                    for (OrderItems orderItem : orderItems) {
                        Stock stock = orderItem.getStock();
                        if (stock != null) {
                            int updatedQty = stock.getQty() - orderItem.getQty();
                            if (updatedQty < 0) {
                                throw new RuntimeException("Insufficient stock for product: "
                                        + stock.getProduct().getTitle());
                            }
                            stock.setQty(updatedQty);
                            hibernateSession.merge(stock);
                        }
                    }
                }

                // Update order status
                Status packingStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.PACKING))
                        .getSingleResult();
                order.setStatus(packingStatus);
                hibernateSession.merge(order);

                // Remove cart items
//                List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user=:user", Cart.class)
//                        .setParameter("user", order.getUser())
//                        .getResultList();
//                for (Cart cart : cartList) {
//                    hibernateSession.remove(cart);
//                }

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Failed to complete order: " + e.getMessage(), e);
            }
        }
    }
    public void failOrder(int orderId) {

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            try {
                Orders order = hibernateSession.find(Orders.class, orderId);
                if (order == null) {
                    throw new RuntimeException("Order not found for ID: " + orderId);
                }

                Status rejectedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.REJECTED))
                        .getSingleResult();
                order.setStatus(rejectedStatus);
                hibernateSession.merge(order);

                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Failed to reject order: " + e.getMessage(), e);
            }
        }
    }
}
