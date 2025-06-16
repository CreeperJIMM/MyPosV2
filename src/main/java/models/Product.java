package models;

public class Product {

    private String product_id;
    private String category;
    private String brand;
    private String name;
    private int price;
    private String image;
    //private String imgSrc; //之前的教材用的名稱

    private String description;

    /**
     *
     * @param id
     * @param category
     * @param brand
     * @param name
     * @param price
     * @param brand
     * @param image
     */
    public Product(String id, String category, String brand, String name, int price, String image, String description1) {
        this.product_id = id;
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
    }

    public Product() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCategory() {
        return category;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Product [id=" + product_id + ", name=" + name + ", price=" + price + "]";
    }

}
