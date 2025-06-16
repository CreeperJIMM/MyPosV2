package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale order from the database
 * Maps to the sale_orders table
 */
public class SaleOrder {
    private String orderId;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String customerId;
    
    // Optional relationship with order details (not in the database schema directly)
    //private List<OrderDetailEntry> orderDetails;

    /**
     * Default constructor
     */
    public SaleOrder() {
        
    }

    /**
     * Parameterized constructor
     * 
     * @param orderId     The order ID
     * @param orderDate   The timestamp when order was placed
     * @param totalAmount The total amount for the order
     * @param customerId  The customer ID who placed the order
     */
    public SaleOrder(String orderId, LocalDateTime orderDate, double totalAmount, String customerId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        //this.orderDetails = new ArrayList<>();
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "SaleOrder{" +
                "orderId='" + orderId + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
