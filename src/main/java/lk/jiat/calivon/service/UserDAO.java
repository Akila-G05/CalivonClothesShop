package lk.jiat.calivon.service;

import jakarta.servlet.http.HttpSession;
import lk.jiat.calivon.entity.Orders;
import lk.jiat.calivon.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAO {
    private Session session;

    public UserDAO(Session session) {
        this.session = session;
    }

    public List<User> getFilteredUsers(String search, int order, int limit, int page) {

        StringBuilder hql = new StringBuilder("SELECT DISTINCT u FROM User u WHERE 1=1");

        //HANDLE SORTING PART AND BUILD HQL QUERY
        if (search != null && !search.isBlank()) {
            hql.append(" AND ( LOWER(u.email) LIKE :search OR LOWER(u.firstName) LIKE :search OR LOWER(u.lastName) LIKE :search)");
        }

        // --- SORT ORDER ---
        hql.append(getOrderBy(order));

        Query<User> query = session.createQuery(hql.toString(), User.class);

        if (search != null && !search.isBlank()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }

        // Pagination
        int offset = (page - 1) * limit;
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }
    public long getFilteredOrdersCount(String search, int order) {

        StringBuilder hql = new StringBuilder(
                "SELECT COUNT(DISTINCT u.id) FROM User u WHERE 1=1"
        );

        //HANDLE SORTING PART AND BUILD HQL QUERY
        if (search != null && !search.isBlank()) {
            hql.append(" AND ( LOWER(u.email) LIKE :search OR LOWER(u.firstName) LIKE :search OR LOWER(u.lastName) LIKE :search)");
        }

        // --- SORT ORDER ---
        hql.append(getOrderBy(order));

        Query<Long> query = session.createQuery(hql.toString(),Long.class);

        if (search != null && !search.isBlank()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }

        return query.getSingleResult();
    }

    private String getOrderBy(int order) {
        switch (order) {
            case 2: // Name ASC
                return " ORDER BY u.firstName ASC ";

            case 3: // Name DESC
                return " ORDER BY u.firstName DESC ";

            case 4: // Date DESC (default)
                return " ORDER BY u.createdAt DESC ";

            case 5: // Date ASC
                return " ORDER BY u.createdAt ASC ";
        }

        return " ORDER BY u.createdAt DESC "; // fallback
    }
}
