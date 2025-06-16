package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import models.Product;

/**
 * 產品資料表管理類別
 * 專門負責產品資料的 CRUD 操作
 */
public class ProductDbManager extends DatabaseConnection {
    
    /**
     * 插入新產品
     */
    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO products(product_id, category, name, brand, price, image_url, description) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getProduct_id());
            pstmt.setString(2, product.getCategory());
            pstmt.setString(3, product.getName());
            pstmt.setString(4, product.getBrand());
            pstmt.setInt(5, product.getPrice());
            pstmt.setString(6, product.getImage());
            pstmt.setString(7, product.getDescription());
            pstmt.executeUpdate();
            System.out.println("插入產品成功: " + product.getName());
            return true;
        } catch (SQLException e) {
            System.out.println("插入產品錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新產品資訊
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET category = ?, name = ?, price = ?, image_url = ?, description = ? " +
                "WHERE product_id = ?";
                
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getCategory());
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getPrice());
            pstmt.setString(4, product.getImage());
            pstmt.setString(5, product.getDescription());
            pstmt.setString(6, product.getProduct_id());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("產品不存在: " + product.getProduct_id());
            } else {
                System.out.println("更新產品成功: " + product.getName());
            }   
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("更新產品錯誤: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 刪除產品
     */
    public boolean deleteProduct(String productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("產品不存在: " + productId);
            } else {
                System.out.println("刪除產品成功: " + productId);
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("刪除產品錯誤: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * 根據ID獲取產品
     */
    public Product getProductById(String productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Product product = new Product();
                product.setProduct_id(rs.getString("product_id"));
                product.setCategory(rs.getString("category"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setImage(rs.getString("image_url"));
                product.setDescription(rs.getString("description"));
                return product;
            }
        } catch (SQLException e) {
            System.out.println("獲取產品錯誤: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 獲取所有產品
     */
    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProduct_id(rs.getString("product_id"));
                product.setCategory(rs.getString("category"));
                product.setBrand(rs.getString("brand"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setImage(rs.getString("image_url"));
                product.setDescription(rs.getString("description"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("獲取所有產品錯誤: " + e.getMessage());
        }
        return products;
    }
    
    /**
     * 根據類別獲取產品
     */
    public List<Product> getProductsByCategory(String category) {
        String sql = "SELECT * FROM products WHERE category = ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setProduct_id(rs.getString("product_id"));
                product.setCategory(rs.getString("category"));
                product.setBrand(rs.getString("brand"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setImage(rs.getString("image_url"));
                product.setDescription(rs.getString("description"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("根據類別獲取產品錯誤: " + e.getMessage());
        }
        return products;
    }

    /**
     * 根據名稱模糊搜尋產品
     */
    public List<Product> getProductsByName(String name) {
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProduct_id(rs.getString("product_id"));
                product.setCategory(rs.getString("category"));
                product.setBrand(rs.getString("brand"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
                product.setImage(rs.getString("image_url"));
                product.setDescription(rs.getString("description"));
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("根據名稱模糊搜尋產品錯誤: " + e.getMessage());
        }
        return products;
    }
    
        /**
     * 從資料庫獲取所有產品類別
     * @return 產品類別陣列
     */
    public String[] getCategories() {
        List<Product> products = this.getAllProducts();
        List<String> categories = new ArrayList<>();
        
        // 使用 stream 收集不重複的類別
        categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
        
        return categories.toArray(new String[0]);
    }
    
    /**
     * 從資料庫獲取所有產品，並轉換為 TreeMap 格式
     * @return 以產品ID為鍵的產品 TreeMap
     */
    public TreeMap<String, Product> getProducts() {
        List<Product> productList = this.getAllProducts();
        TreeMap<String, Product> productMap = new TreeMap<>();
        
        // 將列表轉換為 TreeMap
        for (Product product : productList) {
            productMap.put(product.getProduct_id(), product);
        }
        
        return productMap;
    }
}
