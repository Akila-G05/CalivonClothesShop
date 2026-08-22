package lk.jiat.calivon.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.calivon.entity.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderDAO {
    private Session session;

    public OrderDAO(Session session) {
        this.session = session;
    }

    public List<Orders> getFilteredOrders(String search, int orderStatusId, int limit, int page, HttpSession httpSession) {
        User dbUser = null;

        if(httpSession != null && httpSession.getAttribute("user") != null) {
            User sessionUser = (User) httpSession.getAttribute("user");
            dbUser = session.find(User.class, sessionUser.getId());
        }

        StringBuilder hql = new StringBuilder("SELECT DISTINCT o FROM Orders o JOIN o.orderItems oi WHERE 1=1");

        Integer orderIdFromSearch = null;
        if (search != null && !search.isBlank()) {
            if (search.startsWith("ORD")) {
                try {
                    orderIdFromSearch = Integer.parseInt(search.substring(3));
                } catch (NumberFormatException ignored) {}
            }
        }

        //HANDLE SORTING PART AND BUILD HQL QUERY
        if (orderIdFromSearch != null) {
            hql.append(" AND o.id = :searchId");
        }
        if (orderStatusId > 0){
            hql.append(" AND o.status.id = :orderId ");
        }

        //HANDLE USER PART AND BUILD HQL QUERY
        if(dbUser != null){
            hql.append(" AND o.user.id = :userId");
        }

        // --- SORT ORDER ---
        hql.append(" ORDER BY o.createdAt DESC");

        Query<Orders> query = session.createQuery(hql.toString(), Orders.class);

        if (orderIdFromSearch != null) {
            query.setParameter("searchId", orderIdFromSearch);
        }
        if (orderStatusId > 0) {
            query.setParameter("orderId", orderStatusId);
        }
        if(dbUser != null){
            query.setParameter("userId", dbUser.getId());
        }


        // Pagination
        int offset = (page - 1) * limit;
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }
    public long getFilteredOrdersCount(String search, int orderStatusId, HttpSession httpSession) {
        User dbUser = null;

        if(httpSession != null && httpSession.getAttribute("user") != null) {
            User sessionUser = (User) httpSession.getAttribute("user");
            dbUser = session.find(User.class, sessionUser.getId());
        }

        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(DISTINCT o.id) FROM Orders o JOIN o.orderItems oi WHERE 1=1"
        );

        Integer orderIdFromSearch = null;
        if (search != null && !search.isBlank()) {
            if (search.startsWith("ORD")) {
                try {
                    orderIdFromSearch = Integer.parseInt(search.substring(3));
                } catch (NumberFormatException ignored) {}
            }
        }

        if (orderIdFromSearch != null) hql.append(" AND o.id = :searchId");
        if (orderStatusId > 0) hql.append(" AND o.status.id = :orderId");

        if(dbUser != null){
            hql.append(" AND o.user.id = :userId");
        }

        Query<Long> query = session.createQuery(hql.toString(), Long.class);

        if (orderIdFromSearch != null) {
            query.setParameter("searchId", orderIdFromSearch);
        }
        if (orderStatusId > 0) {
            query.setParameter("orderId", orderStatusId);
        }
        if(dbUser != null){
                query.setParameter("userId", dbUser.getId());
        }

        return query.getSingleResult();
    }
}
