package lk.jiat.calivon.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboardDTO {
    public double totalRevenue;
    public long totalOrders;
    public long totalUsers;
    public long totalProducts;
    public long pendingOrders;
    public long lowStockCount;

    public BestSellerDTO bestSeller;
    public List<TopProductDTO> topProducts;
    public List<LowStockDTO> lowStocks;
    public List<RecentOrderDTO> recentOrders;
    public List<TopCustomerDTO> topCustomers;

    public SalesChartDTO salesChart;

    /* ---------- INNER DTOs ---------- */

    public static class BestSellerDTO {
        public int id;
        public String title;
        public long totalSold;
        public double revenue;
        public double price;
        public long stockQty;
        public String brand;
        public String image;
    }

    public static class TopProductDTO {
        public int id;
        public String title;
        public long totalSold;
        public double revenue;
    }

    public static class LowStockDTO {
        public int id;
        public String title;
        public int available;
        public String color;
        public String colorCode;
        public String size;
    }

    public static class RecentOrderDTO {
        public int id;
        public double price;
        public String status;
        public LocalDateTime createdAt;
        public String customerName;
    }

    public static class TopCustomerDTO {
        public int id;
        public String name;
        public String email;
        public long totalOrders;
        public double totalSpent;
    }

    public static class SalesChartDTO {
        public List<String> labels;
        public List<Double> values;
    }

}
