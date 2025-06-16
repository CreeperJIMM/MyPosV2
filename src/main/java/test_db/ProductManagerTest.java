package test_db;

import java.util.List;

import db.ProductDbManager;
import models.Product;

/**
 * 產品資料庫管理器測試程式
 * 測試產品資料庫的查詢功能
 */
public class ProductManagerTest {
    
    public static void main(String[] args) {
        System.out.println("開始測試產品資料庫管理器...");
        
        ProductDbManager productManager = null;
        
        try {
            // 創建產品資料庫管理器
            productManager = new ProductDbManager();
            
            // 測試獲取所有產品
            System.out.println("\n===== 測試獲取所有產品 =====");
            List<Product> allProducts = productManager.getAllProducts();
            if (allProducts.isEmpty()) {
                System.out.println("沒有找到任何產品記錄。");
            } else {
                System.out.println("成功找到 " + allProducts.size() + " 個產品記錄:");
                displayProducts(allProducts);
            }
            
            // 測試獲取特定產品ID
            System.out.println("\n===== 測試獲取特定產品 =====");
            // 假設我們有一個產品ID是"p-j-101"，如果沒有該ID的產品請修改為實際存在的ID
            String testProductId = "101";
            Product product = productManager.getProductById(testProductId);
            if (product != null) {
                System.out.println("成功找到產品 " + testProductId + ":");
                displayProduct(product);
            } else {
                System.out.println("找不到產品ID為 " + testProductId + " 的產品。");
            }
            
            // 測試獲取特定類別的產品
            System.out.println("\n===== 測試獲取特定類別產品 =====");
            // 假設我們有一個類別是"果汁"，如果沒有該類別請修改為實際存在的類別
            String testCategory = "CPU";
            List<Product> categoryProducts = productManager.getProductsByCategory(testCategory);
            if (categoryProducts.isEmpty()) {
                System.out.println("找不到類別為 " + testCategory + " 的產品。");
            } else {
                System.out.println("成功找到 " + categoryProducts.size() + " 個類別為 " + testCategory + " 的產品:");
                displayProducts(categoryProducts);
            }
            
        } finally {
            // 確保連線關閉
            if (productManager != null) {
                productManager.close();
                System.out.println("\n資料庫連線已關閉。");
            }
        }
        
        System.out.println("\n產品資料庫管理器測試完成。");
    }
    
    /**
     * 顯示單一產品詳細資訊
     */
    private static void displayProduct(Product product) {
        System.out.println("-------------------------------");
        System.out.println("產品ID: " + product.getProduct_id());
        System.out.println("類別: " + product.getCategory());
        System.out.println("名稱: " + product.getName());
        System.out.println("價格: " + product.getPrice());
        System.out.println("圖片: " + product.getImage());
        System.out.println("描述: " + product.getDescription());
        System.out.println("-------------------------------");
    }
    
    /**
     * 顯示多個產品的資訊
     */
    private static void displayProducts(List<Product> products) {
        for (Product product : products) {
            displayProduct(product);
        }
    }
}
