package lk.jiat.calivon.service;

import com.google.gson.JsonObject;
import jakarta.persistence.criteria.Order;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.dto.OrderDTO;
import lk.jiat.calivon.dto.OrderDetailsDTO;
import lk.jiat.calivon.dto.OrderItemDTO;
import lk.jiat.calivon.entity.*;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrdersService {
    public String sortOrders(String search, int order, int limit, int page, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);

        if(httpSession != null && (httpSession.getAttribute("user") != null || httpSession.getAttribute("admin") != null)) {
            Session session = HibernateUtil.getSessionFactory().openSession();

            try {
                // --- 1. FETCH FILTERED PRODUCTS ---
                OrderDAO dao = new OrderDAO(session);

                List<Orders> orders = dao.getFilteredOrders(
                        search, order, limit, page, httpSession
                );
                long totalOrders = dao.getFilteredOrdersCount(
                        search, order, httpSession
                );

                int totalPages = (int) Math.ceil((double) totalOrders / limit);

                // --- 2. BUILD PRODUCT LIST (same format as getBestStockForProductCard()) ---
                List<OrderDTO> orderDTOList = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (Orders o : orders) {
                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setIdAsLong(o.getIdAsLong());
                    orderDTO.setIdAsString(o.getIdAsString());
                    orderDTO.setDeliveryTypeId(o.getDeliveryTypes().getId());
                    orderDTO.setDeliveryTypeName(o.getDeliveryTypes().getName());
                    orderDTO.setStatusId(o.getStatus().getId());
                    orderDTO.setStatusValue(o.getStatus().getValue());
                    orderDTO.setCreatedAt(o.getCreatedAt().format(formatter));
                    orderDTO.setTotalPrice(o.getPrice());

                    OrderDetails od = session.createQuery("FROM OrderDetails od WHERE od.orders.id = :orderId", OrderDetails.class)
                            .setParameter("orderId", o.getIdAsLong())
                            .getSingleResultOrNull();

                    OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
                    orderDetailsDTO.setLineOne(od.getLineOne());
                    orderDetailsDTO.setLineTwo(od.getLineTwo());
                    orderDetailsDTO.setCityName(od.getCity().getName());
                    orderDetailsDTO.setDistrictName(od.getCity().getDistrict().getName());
                    orderDetailsDTO.setShipping(od.getCity().getDistrict().getShipping());
                    orderDetailsDTO.setProvinceName(od.getCity().getDistrict().getProvince().getName());
                    orderDetailsDTO.setPostalCode(od.getPostalCode());
                    orderDetailsDTO.setEmail(o.getUser().getEmail());
                    orderDetailsDTO.setName(od.getName());
                    orderDetailsDTO.setMobile(od.getMobile());

                    List<OrderItemDTO> itemDTOList = new ArrayList<>();

                    for (OrderItems oi : o.getOrderItems()) {
                        Stock stock = oi.getStock();
                        Product product = stock.getProduct();

                        OrderItemDTO itemDTO = new OrderItemDTO();
                        itemDTO.setId(oi.getId());
                        itemDTO.setQty(oi.getQty());
                        itemDTO.setRating(oi.getRating());

                        itemDTO.setStockId(stock.getId());
                        itemDTO.setSize(stock.getSize().getValue());
                        itemDTO.setColorName(stock.getColor().getValue());
                        itemDTO.setColorCode(stock.getColor().getCode());
                        itemDTO.setDiscountId(stock.getDiscount().getId());
                        itemDTO.setDiscountValue(stock.getDiscount().getValue());
                        itemDTO.setBuyingPrice(oi.getBuyingPrice());
                        itemDTO.setStatusId(oi.getStatus().getId());
                        itemDTO.setStatusName(oi.getStatus().getValue());

                        if (stock.getDiscount().getId() != 1 && new Date().before(stock.getDiscount().getExpiredAt())) {
                            itemDTO.setValidDiscount(true);
                        } else {
                            itemDTO.setValidDiscount(false);
                        }

                        itemDTO.setTitle(product.getTitle());
                        itemDTO.setBrandName(product.getBrand().getName());

                        List<String> images = product.getImages();
                        if (images != null && !images.isEmpty()) {
                            itemDTO.setImgPath1(images.get(0));
                        }

                        itemDTOList.add(itemDTO);
                    }

                    orderDTO.setOrderDetails(orderDetailsDTO);
                    orderDTO.setItems(itemDTOList);
                    orderDTOList.add(orderDTO);
                }


                // --- 3. ADD JSON OUTPUT ---
                responseObject.add("orders", AppUtil.gson.toJsonTree(orderDTOList));
                responseObject.addProperty("totalPages", totalPages);
                responseObject.addProperty("currentPage", page);

                status = true;
                message = "Orders sorted successfully";

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

    public String changeOrdersStatus(int orderId, String statusValue, @Context HttpServletRequest request){
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) {
            return buildResponse(false, "Please login first");
        }

        boolean isAdmin = httpSession.getAttribute("admin") != null;
        if (!isAdmin) {
            return buildResponse(false, "Unauthorized access");
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = hibernateSession.beginTransaction();

            Orders orders = hibernateSession.get(Orders.class, orderId);
            if (orders == null) {
                return buildResponse(false, "Order not found");
            }

            List<OrderItems> orderItemsList = hibernateSession.createQuery("FROM OrderItems od WHERE od.orders.id = :orderId AND od.status.value = :status", OrderItems.class)
                    .setParameter("orderId", orders.getIdAsLong())
                    .setParameter("status", "ACTIVE")
                    .getResultList();

            Status dbStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                    .setParameter("value", statusValue)
                    .getSingleResult();

            if(dbStatus.getValue().equals("REJECTED") || dbStatus.getValue().equals("COMPLETED")){
                if(!orderItemsList.isEmpty()){
                    for (OrderItems oi : orderItemsList) {
                        oi.setStatus(dbStatus);

                        if(dbStatus.getValue().equals("REJECTED")){
                            Stock stock = oi.getStock();
                            stock.setQty(stock.getQty() + oi.getQty());
                        }
                    }
                }
            }

            if(dbStatus.getValue().equals("REJECTED")){
                if(orders.getDeliveryTypes().getId() != 2) {
                    return buildResponse(false, "This part under development.");
                }

                orders.setPrice(0.00);
            }

            orders.setStatus(dbStatus);
            tx.commit();
            status = true;
            message = "Order status successfully changed";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            message = "Something went wrong";
        } finally {
            hibernateSession.close();
        }

        return buildResponse(status, message);
    }
    public String changeOrderItemStatus(int orderItemId, @Context HttpServletRequest request) {
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) {
            return buildResponse(false, "Please login first");
        }

        // 🔐 ROLE CHECK
        boolean isAdmin = httpSession.getAttribute("admin") != null;
        boolean isUser  = httpSession.getAttribute("user") != null;

        if (!isAdmin && !isUser) {
            return buildResponse(false, "Unauthorized access");
        }

        // ✅ Resolve session user/admin safely
        User sessionUser = null; // for ownership check
        Admin sessionAdmin = null;
        if (isAdmin) {
            sessionAdmin = (Admin) httpSession.getAttribute("admin");
        } else {
            sessionUser = (User) httpSession.getAttribute("user");
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = hibernateSession.beginTransaction();

            // 🔎 Fetch order item
            OrderItems orderItem = hibernateSession.find(OrderItems.class, orderItemId);
            if (orderItem == null) {
                return buildResponse(false, "Order item not found");
            }

            Orders order = orderItem.getOrders();

            // 👤 USER ownership check (ADMIN can bypass)
            if (isUser && order.getUser().getId() != sessionUser.getId()) {
                return buildResponse(false, "Unauthorized action");
            }

            // ⛔ Only PENDING orders can be modified
            if (!"PENDING".equals(order.getStatus().getValue())) {
                return buildResponse(false, "Order cannot be modified at this stage");
            }

            // 🎯 Resolve final statuses
            Status itemFinalStatus;
            Status orderFinalStatus;

            if (isAdmin) {
                itemFinalStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.REJECTED))
                        .getSingleResult();
                orderFinalStatus = itemFinalStatus;
            } else { // USER
                itemFinalStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.CANCELED))
                        .getSingleResult();
                orderFinalStatus = itemFinalStatus;
            }

            // 🧾 Update order item status
            orderItem.setStatus(itemFinalStatus);

            // ♻ Restore stock
            Stock stock = orderItem.getStock();
            stock.setQty(stock.getQty() + orderItem.getQty());

            // 🔍 Check remaining ACTIVE items
            Status activeStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                    .setParameter("value", String.valueOf(Status.Type.ACTIVE))
                    .getSingleResult();

            Long activeItems = hibernateSession.createQuery(
                            "SELECT COUNT(oi.id) FROM OrderItems oi WHERE oi.orders.id = :orderId AND oi.status = :status", Long.class)
                    .setParameter("orderId", order.getIdAsLong())
                    .setParameter("status", activeStatus)
                    .getSingleResult();

            // 💰 Calculate balance price
            double totalItemsCost = orderItem.getBuyingPrice() * orderItem.getQty();
            double balancePrice = order.getPrice() - totalItemsCost;

            // 📦 If no active items → adjust delivery/payment and update order status
            if (activeItems == 0) {
                OrderDetails orderDetails = hibernateSession.createQuery(
                                "FROM OrderDetails od WHERE od.orders.id = :orderId",
                                OrderDetails.class)
                        .setParameter("orderId", order.getIdAsLong())
                        .getSingleResultOrNull();

                double deliveryPrice = 0;
                if (orderDetails != null && orderDetails.getCity() != null && orderDetails.getCity().getDistrict() != null) {
                    deliveryPrice = orderDetails.getCity().getDistrict().getShipping();
                }
                double paymentPrice = order.getDeliveryTypes().getPrice(); // COD, etc.

                balancePrice -= (deliveryPrice + paymentPrice);

                if (balancePrice < 0) balancePrice = 0; // safety check

                order.setStatus(orderFinalStatus);
            }

            order.setPrice(balancePrice);

            tx.commit();
            status = true;
            message = isAdmin
                    ? "Order item rejected successfully"
                    : "Order item cancelled successfully";

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            message = "Something went wrong";
        } finally {
            hibernateSession.close();
        }

        return buildResponse(status, message);
    }
    private String buildResponse(boolean status, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("message", message);
        return AppUtil.gson.toJson(json);
    }
}
