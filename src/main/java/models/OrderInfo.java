package models;

public class OrderInfo {
    private String orderId;
    private String date;
    private int total_amount;
    private int memberId;

    public OrderInfo(String orderId, String date, int total, int memberId) {
        this.orderId = orderId;
        this.date = date;
        this.total_amount = total;
        this.memberId = memberId;
    }

    public String getOrderId() {
        return orderId;
    }
    
    public int getTotal() {
        return total_amount;
    }
    
    public int getMemberId() {
        return memberId;
    }

    @Override
    public String toString() {
        return "訂單" + orderId + "（" + date + "）";
    }
}