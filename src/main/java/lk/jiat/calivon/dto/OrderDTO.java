package lk.jiat.calivon.dto;

import java.io.Serializable;
import java.util.List;

public class OrderDTO implements Serializable {
    private String idAsString;
    private long idAsLong;
    private int deliveryTypeId;
    private String deliveryTypeName;
    private int statusId;
    private String statusValue;
    private double totalPrice;

    private String createdAt;
    private List<OrderItemDTO> items;

    private OrderDetailsDTO orderDetails;

    public OrderDetailsDTO getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetailsDTO orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getIdAsString() {
        return idAsString;
    }

    public void setIdAsString(String idAsString) {
        this.idAsString = idAsString;
    }

    public long getIdAsLong() {
        return idAsLong;
    }

    public void setIdAsLong(long idAsLong) {
        this.idAsLong = idAsLong;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public int getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(int deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public String getDeliveryTypeName() {
        return deliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
