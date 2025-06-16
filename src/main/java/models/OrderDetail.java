package models;
import models.Product;

public class OrderDetail {

    private int id;
    private String order_id;
    private String product_id;
    private int quantity;
    private int product_price;
    private String product_brand;
    private String product_category;
    private String product_name;
    
    public OrderDetail(Product pd) {
        this.product_id = pd.getProduct_id();
        this.product_name = pd.getName();
        this.product_price = pd.getPrice();
        this.product_brand = pd.getBrand();
        this.product_category = pd.getCategory();
        this.quantity = quantity;
    }
    
    public OrderDetail() {
    }

    public int get_id() {
        return id;
    }

    public void set_id(int id) {
        this.id = id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    
    public String getProduct_Brand() {
        return product_brand;
    }

    public void setProduct_brand(String product_brand) {
        this.product_name = product_brand;
    }
    
        public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }
}
