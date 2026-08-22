package lk.jiat.calivon.service;

import jakarta.servlet.http.HttpSession;
import lk.jiat.calivon.entity.Admin;
import lk.jiat.calivon.entity.Product;
import lk.jiat.calivon.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class AdminProductDAO {
    private Session session;

    public AdminProductDAO(Session session) {
        this.session = session;
    }

    public List<Product> getFilteredProducts(String search, int order, int limit, int page, HttpSession httpSession) {
        StringBuilder hql = new StringBuilder("SELECT p FROM Product p LEFT JOIN p.stocks s WHERE 1=1 ");

        Admin admin = (Admin) httpSession.getAttribute("admin");

        if (search != null && !search.trim().isEmpty()) {
            hql.append("AND LOWER(p.title) LIKE :search ");
        }
        if (admin != null) {
            hql.append("AND p.admin = :admin ");
        }

        hql.append(" GROUP BY p.id ");

        // Sort
        hql.append(getOrderBy(order));

        Query<Product> query = session.createQuery(hql.toString(), Product.class);

        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", "%" + search.toLowerCase().trim() + "%");
        }
        if (admin != null) {
            query.setParameter("admin", admin);
        }

        // Pagination safety
        page = Math.max(page, 1);
        limit = Math.max(limit, 1);

        int offset = (page - 1) * limit;
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }
    public long getFilteredProductsCount(String search, int order, HttpSession httpSession) {

        StringBuilder hql = new StringBuilder("SELECT COUNT(p.id) FROM Product p WHERE 1=1 ");

        Admin admin = (Admin) httpSession.getAttribute("admin");

        if (search != null && !search.trim().isEmpty()) {
            hql.append("AND LOWER(p.title) LIKE :search ");
        }
        if (admin != null) {
            hql.append("AND p.admin = :admin ");
        }

        Query<Long> query = session.createQuery(hql.toString(),Long.class);

        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", "%" + search.toLowerCase().trim() + "%");
        }
        if (admin != null) {
            query.setParameter("admin", admin);
        }

        return query.getSingleResult();
    }

    private String getOrderBy(int order) {

        switch (order) {
            case 1: // products that have discounts
                return " ORDER BY s.discount.id DESC ";

            case 2: // Name ASC
                return " ORDER BY p.title ASC ";

            case 3: // Name DESC
                return " ORDER BY p.title DESC ";

            case 4: // Date ASC
                return " ORDER BY p.createdAt ASC ";

            case 5: // Date DESC (default)
                return " ORDER BY p.createdAt DESC ";

            case 6: // Price Low → High
                return " ORDER BY s.price ASC ";

            case 7: // Price High → Low
                return " ORDER BY s.price DESC ";

            case 8:
                return " ORDER BY SUM(s.qty) ASC";
        }

        return " ORDER BY p.createdAt DESC "; // fallback
    }
}
