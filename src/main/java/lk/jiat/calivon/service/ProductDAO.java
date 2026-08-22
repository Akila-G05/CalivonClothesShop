package lk.jiat.calivon.service;

import lk.jiat.calivon.entity.Discount;
import lk.jiat.calivon.entity.Product;
import lk.jiat.calivon.entity.Stock;
import org.hibernate.Session;
import org.hibernate.query.Query;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {
    private Session session;

    public ProductDAO(Session session) {
        this.session = session;
    }

    public List<Product> getFilteredProducts(double minPrice, double maxPrice, int colorId, int sizeId,
                                             int categoryId, int subCategoryId, int brandId, String search, int order, int limit, int page) {

        StringBuilder hql = new StringBuilder("SELECT DISTINCT p FROM Product p LEFT JOIN p.stocks s WHERE 1=1 ");

        //HANDLE SORTING PART AND BUILD HQL QUERY
        createFilteredHqlQuery(minPrice, maxPrice, colorId, sizeId, categoryId, subCategoryId, brandId, search, hql);

        // --- SORT ORDER ---
        hql.append(getOrderBy(order));

        Query<Product> query = session.createQuery(hql.toString(), Product.class);

        // Set parameters
        if (categoryId > 0) query.setParameter("categoryId", categoryId);
        if (subCategoryId > 0) query.setParameter("subCategoryId", subCategoryId);
        if (brandId > 0) query.setParameter("brandId", brandId);
        if (colorId > 0) query.setParameter("colorId", colorId);
        if (sizeId > 0) query.setParameter("sizeId", sizeId);
        if (minPrice > 0) query.setParameter("minPrice", minPrice);
        if (maxPrice > 0) query.setParameter("maxPrice", maxPrice);
        if (search != null && !search.isEmpty()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }

        // Pagination
        int offset = (page - 1) * limit;
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public long countFilteredProducts(double minPrice, double maxPrice, int colorId, int sizeId, int categoryId, int subCategoryId, int brandId, String search) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(DISTINCT p.id) FROM Product p LEFT JOIN p.stocks s WHERE 1=1 ");

        //HANDLE SORTING PART AND BUILD HQL QUERY
        createFilteredHqlQuery(minPrice, maxPrice, colorId, sizeId, categoryId, subCategoryId, brandId, search, hql);

        Query<Long> query = session.createQuery(hql.toString(), Long.class);

        if (categoryId > 0) query.setParameter("categoryId", categoryId);
        if (subCategoryId > 0) query.setParameter("subCategoryId", subCategoryId);
        if (brandId > 0) query.setParameter("brandId", brandId);
        if (colorId > 0) query.setParameter("colorId", colorId);
        if (sizeId > 0) query.setParameter("sizeId", sizeId);
        if (minPrice > 0) query.setParameter("minPrice", minPrice);
        if (maxPrice > 0) query.setParameter("maxPrice", maxPrice);
        if (search != null && !search.isEmpty()) {
            query.setParameter("search", "%" + search.toLowerCase() + "%");
        }

        return query.uniqueResult();
    }

    private void createFilteredHqlQuery(double minPrice, double maxPrice, int colorId, int sizeId,
                                        int categoryId, int subCategoryId, int brandId, String search, StringBuilder hql) {
        if (categoryId > 0) hql.append("AND p.subCategory.category.id = :categoryId ");
        if (subCategoryId > 0) hql.append("AND p.subCategory.id = :subCategoryId ");
        if (brandId > 0) hql.append("AND p.brand.id = :brandId ");
        if (colorId > 0) hql.append("AND s.color.id = :colorId ");
        if (sizeId > 0) hql.append("AND s.size.id = :sizeId ");
        if (minPrice > 0) hql.append("AND s.price >= :minPrice ");
        if (maxPrice > 0) hql.append("AND s.price <= :maxPrice ");
        if (search != null && !search.isEmpty()) hql.append("AND LOWER(p.title) LIKE :search ");
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
        }

        return " ORDER BY p.createdAt DESC "; // fallback
    }

    public Map<String, Object> getBestStockForShopPage(Product product, Session session, int colorId, int sizeId, double minPrice, double maxPrice) {

        StringBuilder hql = new StringBuilder(
                "FROM Stock s WHERE s.product = :product " +
                        "AND s.status.id = 1 AND s.qty > 0 "
        );

        if (colorId > 0) hql.append("AND s.color.id = :colorId ");
        if (sizeId > 0) hql.append("AND s.size.id = :sizeId ");

        if (minPrice > 0) {
            hql.append("AND (s.price - (s.price * COALESCE(s.discount.value,0) / 100)) >= :minPrice ");
        }
        if (maxPrice > 0) {
            hql.append("AND (s.price - (s.price * COALESCE(s.discount.value,0) / 100)) <= :maxPrice ");
        }

        hql.append("ORDER BY s.id ASC");

        Query<Stock> query = session.createQuery(hql.toString(), Stock.class);
        query.setParameter("product", product);

        if (colorId > 0) query.setParameter("colorId", colorId);
        if (sizeId > 0) query.setParameter("sizeId", sizeId);
        if (minPrice > 0) query.setParameter("minPrice", minPrice);
        if (maxPrice > 0) query.setParameter("maxPrice", maxPrice);

        List<Stock> stocks = query.getResultList();

        if (stocks.isEmpty()) return null;

        return getBestStock(stocks);
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
            if (d != null && d.getId() != 1 && d.getExpiredAt().after(now)) {
                bestStockWithValidDiscount = s;
                bestDiscountWithValidDiscount = d;
                break;
            }

            // Default or no discount
            if (bestStockWithDefault == null) {
                bestStockWithDefault = s;
                bestDiscountWithDefault = d; // this discount never be null, also add expired dicount to this
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
}
