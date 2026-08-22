package lk.jiat.calivon.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.dto.*;
import lk.jiat.calivon.entity.*;
import lk.jiat.calivon.mail.VerificationMail;
import lk.jiat.calivon.provider.MailServiceProvider;
import lk.jiat.calivon.util.AppUtil;
import lk.jiat.calivon.util.HibernateUtil;
import lk.jiat.calivon.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProfileService {
    //USER PROFILE LOGIC HANDELING
    public String sendVerificationCode(UserDTO userDTO, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if(httpSession == null){
            message = "Please login first!";
        }else if(httpSession.getAttribute("user") == null){
            message = "Please login first!";
        }else{
            User sessionUser = (User) httpSession.getAttribute("user");
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            User dbUser = hibernateSession.find(User.class, sessionUser.getId());

            if (!String.valueOf(Status.Type.VERIFIED).equals(dbUser.getStatus().getValue())) {
                String verificationCode = AppUtil.generateCode();
                dbUser.setVerificationCode(verificationCode);

                /// Verification Mail Send Process Start
                VerificationMail verificationMail = new VerificationMail(dbUser.getEmail(), dbUser.getVerificationCode());
                MailServiceProvider.getInstance().sendMail(verificationMail);
                /// Verification Mail Send Process End

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.merge(dbUser);
                    transaction.commit();
                    status = true;
                    message = "Verification code send to your email!";
                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Verification code sending failed!";
                }
            }else{
                message = "This account already verified!";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String loadUserProfileData(@Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        /// user profile data handeling code | user profile data part start
        HttpSession httpSession = request.getSession(false);
        User user =  (User)httpSession.getAttribute("user");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPassword(user.getPassword());

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Address> addressList = hibernateSession.createQuery("FROM Address a WHERE a.user=:user", Address.class)
                .setParameter("user", user)
                .getResultList();

        Address primaryAddress = null;
        for(Address address : addressList){
            if(address.isPrimary()){
                primaryAddress = address;
                break;
            }
        }
        if(primaryAddress != null){
            userDTO.setLineOne(primaryAddress.getLineOne());
            userDTO.setLineTwo(primaryAddress.getLineTwo());
            userDTO.setPostalCode(primaryAddress.getPostalCode());
            userDTO.setMobile(primaryAddress.getMobile());
            userDTO.setPrimary(primaryAddress.isPrimary());
            userDTO.setProvinceId(primaryAddress.getCity().getDistrict().getProvince().getId());
            userDTO.setProvinceName(primaryAddress.getCity().getDistrict().getProvince().getName());
            userDTO.setDistrictId(primaryAddress.getCity().getDistrict().getId());
            userDTO.setDistrictName(primaryAddress.getCity().getDistrict().getName());
            userDTO.setCityId(primaryAddress.getCity().getId());
            userDTO.setCityName(primaryAddress.getCity().getName());
        }

        LocalDateTime createdAt = user.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMMM");
        String sinceAt = createdAt.format(formatter);
        userDTO.setSinceAt(sinceAt);

        hibernateSession.close();

        responseObject.add("user", AppUtil.gson.toJsonTree(userDTO));
        /// user profile data handeling code | user profile data part end

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String updatePersonalInfo(UserDTO userDTO, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if(userDTO.getFirstName() == null){
            message = "First Name is required!";
        }else if(userDTO.getFirstName().isBlank()) {
            message = "First Name can not be empty or blank!";
        }else if (userDTO.getLastName() == null) {
            message = "Last Name is required!";
        }else if(userDTO.getLastName().isBlank()) {
            message = "Last Name can not be empty or blank!";
        }else if (userDTO.getPassword() == null) {
            message = "Password is required!";
        }else if(userDTO.getPassword().isBlank()) {
            message = "Password can not be empty or blank!";
        }else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please enter a valid password.";
        }else if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isBlank() && !userDTO.getNewPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please enter a valid password for new password.";
        }else if (userDTO.getConfirmPassword() != null && !userDTO.getConfirmPassword().isBlank() && !userDTO.getConfirmPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please enter a valid password for confirm password.";
        }else if(userDTO.getNewPassword() != null && userDTO.getConfirmPassword() != null && !userDTO.getNewPassword().equals(userDTO.getConfirmPassword())){
            message = "New & Confirm Passwords do not match!";
        }else{
            HttpSession httpSession = request.getSession(false);
            if(httpSession == null){
                message = "Please login first!";
            }else if(httpSession.getAttribute("user") == null){
                message = "Please login first!";
            }else{
                User sessionUser = (User) httpSession.getAttribute("user");
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                User dbUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                        .setParameter("email", sessionUser.getEmail())
                        .getSingleResult();

                if (!String.valueOf(Status.Type.VERIFIED).equals(dbUser.getStatus().getValue())) {
                    message = "Please verify your account";
                }else {

                    dbUser.setFirstName(userDTO.getFirstName());
                    dbUser.setLastName(userDTO.getLastName());
                    dbUser.setPassword(!userDTO.getConfirmPassword().isBlank() ? userDTO.getConfirmPassword() : userDTO.getPassword());

                    Transaction transaction = hibernateSession.beginTransaction();

                    try {
                        hibernateSession.merge(dbUser);
                        transaction.commit();
                        httpSession.setAttribute("user", dbUser);
                        status = true;
                        message = "Personal info updated successful!";
                    } catch (HibernateException e) {
                        transaction.rollback();
                        message = "Personal info update failed!";
                    }

                }

                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String loadUserAddresses(@Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();

        HttpSession httpSession = request.getSession();
        if(httpSession != null && httpSession.getAttribute("user") != null){
            User sessionUser = (User) httpSession.getAttribute("user");

//            responseObject.addProperty("name", sessionUser.getFirstName() +  " " + sessionUser.getLastName());
//            responseObject.addProperty("email", sessionUser.getEmail());

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            List<Address> addressList = hibernateSession.createQuery("FROM Address a WHERE a.user = :user", Address.class)
                    .setParameter("user", sessionUser)
                    .getResultList();

            List<JsonObject> addresses = new ArrayList<>();
            for (Address a:addressList){
                JsonObject jo = new JsonObject();
                jo.addProperty("id", a.getId());
                jo.addProperty("lineOne", a.getLineOne());
                jo.addProperty("lineTwo", a.getLineTwo());
                jo.addProperty("mobile",  a.getMobile());
                jo.addProperty("cityId", a.getCity().getId());
                jo.addProperty("cityName", a.getCity().getName());
                jo.addProperty("isPrimary", a.isPrimary());
                addresses.add(jo);
            }
            responseObject.add("addresses", AppUtil.gson.toJsonTree(addresses));

            hibernateSession.close();
        }

        return AppUtil.gson.toJson(responseObject);
    }

    public String changePrimaryAddress(int id, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("user") != null){
            User sessionUser = (User) session.getAttribute("user");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            List<Address> addressList = hibernateSession.createQuery("FROM Address a WHERE a.user = :user", Address.class)
                    .setParameter("user", sessionUser)
                    .getResultList();

            for(Address address : addressList){
                if(address.isPrimary()){
                    address.setPrimary(false);
                }

                if(address.getId() == id){
                    address.setPrimary(true);
                }
            }

            try {
                transaction.commit();
                message = "Primary address changed Successful!";
                status = true;
            }catch (HibernateException e){
                transaction.rollback();
                message = "Primary Address changed failed!";
            }
        }else{
            message = "Please login first!";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String addNewOrUpdateAddress(UserDTO userDTO, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if(userDTO.getLineOne() == null) {
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
            HttpSession httpSession = request.getSession(false);
            if(httpSession != null && httpSession.getAttribute("user") != null){
                User sessionUser = (User)httpSession.getAttribute("user");

                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

                User user = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                        .setParameter("email", sessionUser.getEmail())
                        .getSingleResultOrNull();

                if (!String.valueOf(Status.Type.VERIFIED).equals(user.getStatus().getValue())) {
                    message = "Please verify your account";
                }else {
                    Transaction transaction = hibernateSession.beginTransaction();

                    if (userDTO.getAddressId() <= 0) {
                        Address address = new Address();
                        address.setLineOne(userDTO.getLineOne());
                        address.setLineTwo(userDTO.getLineTwo());
                        address.setPostalCode(userDTO.getPostalCode());
                        address.setMobile(userDTO.getMobile());
                        address.setUser(user);

                        City city = hibernateSession.find(City.class, userDTO.getCityId());
                        address.setCity(city);

                        hibernateSession.persist(address);
                        message = "New Address added Successful!";
                    } else {
                        Address existing = hibernateSession.find(Address.class, userDTO.getAddressId());
                        if (existing != null && existing.getUser().getId() == sessionUser.getId()) {
                            existing.setLineOne(userDTO.getLineOne());
                            existing.setLineTwo(userDTO.getLineTwo());
                            existing.setPostalCode(userDTO.getPostalCode());
                            existing.setMobile(userDTO.getMobile());

                            City city = hibernateSession.find(City.class, userDTO.getCityId());
                            existing.setCity(city);

                            hibernateSession.merge(existing);
                            message = "Address updated successfully!";
                        } else {
                            message = "Invalid address!";
                        }
                    }

                    try {
                        transaction.commit();
                        status = true;
                    } catch (HibernateException e) {
                        transaction.rollback();
                        message = "Address added failed!";
                    }
                }

                hibernateSession.close();
            }else{
                message = "Please login first!";
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }

    public String deleteAddress(int id, @Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("user") != null) {
            User sessionUser = (User)session.getAttribute("user");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            Address address = hibernateSession.get(Address.class, id);

            if(address != null){
                if(address.getUser().getId() != sessionUser.getId()){
                    message = "You are not allowed to delete this address!";
                }else if(address.isPrimary()){
                    message = "Primary address can't be deleted!";
                }else{
                    try{
                        hibernateSession.delete(address);
                        transaction.commit();
                        status = true;
                        message = "Address Delete Successful!";
                    }catch (HibernateException e){
                        transaction.rollback();
                        message = "Address delete failed!";
                        e.printStackTrace();
                    }
                }
            }else{
                message = "Address does not exist!";
            }

            hibernateSession.close();
        }else{
            message = "Please login first!";
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    //USER PROFILE LOGIC HANDELING

    //ADMIN PROFILE LOGIC HANDELING
    public String sortUsers(String search, int order, int limit, int page, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);

        if(httpSession != null || httpSession.getAttribute("admin") != null) {
            Session session = HibernateUtil.getSessionFactory().openSession();

            try {
                // --- 1. FETCH FILTERED PRODUCTS ---
                UserDAO dao = new UserDAO(session);

                List<User> users = dao.getFilteredUsers(
                        search, order, limit, page
                );
                long totalUsers = dao.getFilteredOrdersCount(
                        search, order
                );

                int totalPages = (int) Math.ceil((double) totalUsers / limit);

                // --- 2. BUILD PRODUCT LIST (same format as getBestStockForProductCard()) ---
                List<UserDTO> userDTOList = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (User u : users) {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(u.getId());
                    userDTO.setSinceAt(u.getCreatedAt().format(formatter));
                    userDTO.setFirstName(u.getFirstName());
                    userDTO.setLastName(u.getLastName());
                    userDTO.setEmail(u.getEmail());
                    userDTO.setStatusValue(u.getStatus().getValue());

                    List<Address> addresses = session.createQuery("FROM Address a WHERE a.user.id = :uId", Address.class)
                            .setParameter("uId", u.getId())
                            .getResultList();

                    List<AddressDTO> addressDTOList = new ArrayList<>();
                    for(Address address : addresses){
                        if(address.isPrimary()){
                            userDTO.setMobile(address.getMobile());
                        }

                        AddressDTO addressDTO = new AddressDTO();
                        addressDTO.setLineOne(address.getLineOne());
                        addressDTO.setLineTwo(address.getLineTwo());
                        addressDTO.setCityName(address.getCity().getName());
                        addressDTO.setDistrictName(address.getCity().getDistrict().getName());
                        addressDTO.setProvinceName(address.getCity().getDistrict().getProvince().getName());
                        addressDTO.setShipping(address.getCity().getDistrict().getShipping());
                        addressDTO.setPostalCode(address.getPostalCode());
                        addressDTO.setMobile(address.getMobile());
                        addressDTO.setPrimary(address.isPrimary());

                        addressDTOList.add(addressDTO);
                    }

                    userDTO.setAddresses(addressDTOList);

                    userDTOList.add(userDTO);
                }


                // --- 3. ADD JSON OUTPUT ---
                responseObject.add("users", AppUtil.gson.toJsonTree(userDTOList));
                responseObject.addProperty("totalPages", totalPages);
                responseObject.addProperty("currentPage", page);

                status = true;
                message = "Users sorted successfully";

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

    public String changeUserStatus(int userId, String statusValue, @Context HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message;

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "This operation is only for admins!");
            return AppUtil.gson.toJson(responseObject);
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = hibernateSession.beginTransaction();

            User user = hibernateSession.find(User.class, userId);
            if (user == null) {
                message = "User does not exist!";
                tx.rollback();
            } else {

                Status dbStatus = hibernateSession
                        .createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", statusValue)
                        .getSingleResultOrNull();

                if (dbStatus == null) {
                    message = "Invalid status!";
                    tx.rollback();
                } else if (user.getStatus().getId() == dbStatus.getId()) {
                    message = "User already has this status!";
                    tx.rollback();
                } else {
                    user.setStatus(dbStatus);
                    status = true;
                    message = "User status changed successfully!";
                    tx.commit();
                }
            }

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            message = "User status change failed!";
            e.printStackTrace();
        } finally {
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.gson.toJson(responseObject);
    }
    private String buildResponse(boolean status, String message) {
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        json.addProperty("message", message);
        return AppUtil.gson.toJson(json);
    }

    public String loadAdminPanelData(@Context HttpServletRequest request){
        JsonObject responseObject = new JsonObject();
        boolean status = false;

        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute("admin") != null){
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            AdminDashboardDTO dashboardDTO = new AdminDashboardDTO();

            dashboardDTO.totalRevenue = hibernateSession.createQuery(
                            "SELECT COALESCE(SUM(o.price), 0) FROM Orders o WHERE o.status.value IN (:statuses)",
                            Double.class)
                    .setParameterList("statuses", List.of("COMPLETED", "RECEIVED"))
                    .getSingleResult();

            dashboardDTO.totalOrders = hibernateSession.createQuery(
                            "SELECT COUNT(o.id) FROM Orders o",
                            Long.class)
                    .getSingleResult();

            dashboardDTO.totalUsers = hibernateSession.createQuery(
                            "SELECT COUNT(u.id) FROM User u",
                            Long.class)
                    .getSingleResult();

            dashboardDTO.totalProducts = hibernateSession.createQuery(
                            "SELECT COUNT(p.id) FROM Product p",
                            Long.class)
                    .getSingleResult();

            dashboardDTO.pendingOrders = hibernateSession.createQuery(
                            "SELECT COUNT(o.id) FROM Orders o WHERE o.status.value IN (:statuses)",
                            Long.class)
                    .setParameterList("statuses", List.of("PENDING", "PACKING", "DELIVERED"))
                    .getSingleResult();

            dashboardDTO.lowStockCount = hibernateSession.createQuery(
                            "SELECT COUNT(s.id) FROM Stock s WHERE s.qty <= :threshold",
                            Long.class)
                    .setParameter("threshold", 5)
                    .getSingleResult();

            List<Object[]> topProductsList = hibernateSession.createQuery(
                            "SELECT p.id, p.title, SUM(oi.qty), SUM(oi.qty * oi.buyingPrice), " +
                                    "(SELECT COALESCE(SUM(st.qty), 0) FROM Stock st WHERE st.product.id = p.id), " +
                                    "p.brand.name " +
                                    "FROM OrderItems oi JOIN oi.stock s JOIN s.product p JOIN oi.orders o " +
                                    "WHERE o.status.value IN (:statuses) " +
                                    "GROUP BY p.id, p.title, p.brand.name ORDER BY SUM(oi.qty) DESC",
                            Object[].class)
                    .setParameterList("statuses", List.of("COMPLETED", "RECEIVED"))
                    .setMaxResults(5)
                    .getResultList();

            if(!topProductsList.isEmpty()){
                Object[] row = topProductsList.get(0);

                AdminDashboardDTO.BestSellerDTO bestSeller = new AdminDashboardDTO.BestSellerDTO();
                bestSeller.id = ((Number) row[0]).intValue();
                bestSeller.title = (String) row[1];
                bestSeller.totalSold = (Long) row[2];
                bestSeller.revenue = (Double) row[3];
                bestSeller.stockQty = (Long) row[4];
                bestSeller.brand = (String) row[5];

                Product p = hibernateSession.find(Product.class, bestSeller.id);
                if(p != null && p.getImages() != null && !p.getImages().isEmpty()){
                    bestSeller.image = p.getImages().get(0);
                }

                dashboardDTO.bestSeller = bestSeller;
            }

            List<AdminDashboardDTO.TopProductDTO> topProducts = new ArrayList<>();
            for(Object[] row : topProductsList){
                AdminDashboardDTO.TopProductDTO productDTO = new AdminDashboardDTO.TopProductDTO();
                productDTO.id = ((Number) row[0]).intValue();
                productDTO.title = (String) row[1];
                productDTO.totalSold = (Long) row[2];
                productDTO.revenue = (Double) row[3];
                topProducts.add(productDTO);
            }
            dashboardDTO.topProducts = topProducts;

            List<Object[]> lowStocksProductList = hibernateSession.createQuery(
                            "SELECT p.id, p.title, s.qty, s.color.value, s.color.code, s.size.value " +
                                    "FROM Product p JOIN p.stocks s ORDER BY s.qty ASC",
                            Object[].class)
                    .setMaxResults(5)
                    .getResultList();

            List<AdminDashboardDTO.LowStockDTO> lowStocks = new ArrayList<>();
            for(Object[] row : lowStocksProductList){
                AdminDashboardDTO.LowStockDTO stockDTO = new AdminDashboardDTO.LowStockDTO();
                stockDTO.id = ((Number) row[0]).intValue();
                stockDTO.title = (String) row[1];
                stockDTO.available = ((Number) row[2]).intValue();
                stockDTO.color = (String) row[3];
                stockDTO.colorCode = (String) row[4];
                stockDTO.size = (String) row[5];
                lowStocks.add(stockDTO);
            }
            dashboardDTO.lowStocks = lowStocks;

            List<Object[]> recentOrdersList = hibernateSession.createQuery(
                            "SELECT o.id, o.price, o.status.value, o.createdAt, u.firstName, u.lastName " +
                                    "FROM Orders o JOIN o.user u ORDER BY o.createdAt DESC",
                            Object[].class)
                    .setMaxResults(5)
                    .getResultList();

            List<AdminDashboardDTO.RecentOrderDTO> recentOrders = new ArrayList<>();
            for(Object[] row : recentOrdersList){
                AdminDashboardDTO.RecentOrderDTO orderDTO = new AdminDashboardDTO.RecentOrderDTO();
                orderDTO.id = ((Number) row[0]).intValue();;
                orderDTO.price = (Double) row[1];
                orderDTO.status = (String) row[2];
                orderDTO.createdAt = (java.time.LocalDateTime) row[3];
                orderDTO.customerName = row[4] + " " + row[5];
                recentOrders.add(orderDTO);
            }
            dashboardDTO.recentOrders = recentOrders;

            List<Object[]> topCustomersList = hibernateSession.createQuery(
                            "SELECT u.id, u.firstName, u.lastName, u.email, COUNT(o.id), COALESCE(SUM(o.price), 0) " +
                                    "FROM Orders o JOIN o.user u " +
                                    "WHERE o.status.value IN (:statuses) " +
                                    "GROUP BY u.id, u.firstName, u.lastName, u.email ORDER BY SUM(o.price) DESC",
                            Object[].class)
                    .setParameterList("statuses", List.of("COMPLETED", "RECEIVED"))
                    .setMaxResults(5)
                    .getResultList();

            List<AdminDashboardDTO.TopCustomerDTO> topCustomers = new ArrayList<>();
            for(Object[] row : topCustomersList){
                AdminDashboardDTO.TopCustomerDTO customerDTO = new AdminDashboardDTO.TopCustomerDTO();
                customerDTO.id = ((Number) row[0]).intValue();
                customerDTO.name = row[1] + " " + row[2];
                customerDTO.email = (String) row[3];
                customerDTO.totalOrders = (Long) row[4];
                customerDTO.totalSpent = (Double) row[5];
                topCustomers.add(customerDTO);
            }
            dashboardDTO.topCustomers = topCustomers;


            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);

            List<Object[]> salesChartRows = hibernateSession.createQuery(
                            "SELECT DATE(o.createdAt), COALESCE(SUM(o.price), 0) " +
                                    "FROM Orders o " +
                                    "WHERE o.status.value IN (:statuses) " +
                                    "AND DATE(o.createdAt) BETWEEN :startDate AND :endDate " +
                                    "GROUP BY DATE(o.createdAt) " +
                                    "ORDER BY DATE(o.createdAt)",
                            Object[].class
                    )
                    .setParameterList("statuses", List.of("COMPLETED", "RECEIVED"))
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

            Map<LocalDate, Double> salesMap = new LinkedHashMap<>();

            for (int i = 6; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                salesMap.put(date, 0.0);
            }

            for (Object[] row : salesChartRows) {
                LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                Double amount = ((Number) row[1]).doubleValue();
                salesMap.put(date, amount);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");

            AdminDashboardDTO.SalesChartDTO salesChartDTO = new AdminDashboardDTO.SalesChartDTO();
            salesChartDTO.labels = new ArrayList<>();
            salesChartDTO.values = new ArrayList<>();

            for (Map.Entry<LocalDate, Double> entry : salesMap.entrySet()) {
                salesChartDTO.labels.add(entry.getKey().format(formatter));
                salesChartDTO.values.add(entry.getValue());
            }

            dashboardDTO.salesChart = salesChartDTO;

            responseObject.add("data", AppUtil.gson.toJsonTree(dashboardDTO));
            status = true;
        }

        responseObject.addProperty("status", status);
        return AppUtil.gson.toJson(responseObject);
    }
}
