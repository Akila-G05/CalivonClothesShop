package lk.jiat.calivon.dto;

import lk.jiat.calivon.entity.Status;

public class StockDTO {
    private int stockId;
    private int productId;
    private int qty;
    private double price;
    private int colorId;
    private String colorName;
    private String colorCode;
    private int sizeId;
    private String sizeName;
    private String createdAt;

    private String status;
    private int statusId;

    private int discountId;
    private String discountName;
    private Double discountValue;
    private String discountExpiredAt;
    private Double newPrice;
    private boolean validDiscount;

    private int wishlistId;
    private int cartId;

    public boolean isValidDiscount() {
        return validDiscount;
    }

    public void setValidDiscount(boolean validDiscount) {
        this.validDiscount = validDiscount;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountExpiredAt() {
        return discountExpiredAt;
    }

    public void setDiscountExpiredAt(String discountExpiredAt) {
        this.discountExpiredAt = discountExpiredAt;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(String price) {
        // Remove "Rs." and parse as double
        this.price = Double.parseDouble(price.replaceAll("[^0-9.]", ""));
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public int getSizeId() {
        return sizeId;
    }

    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "StockDTO{" +
                "colorId=" + colorId +
                ", sizeId=" + sizeId +
                ", qty=" + qty +
                ", price='" + price + '\'' +
                '}';
    }
}
