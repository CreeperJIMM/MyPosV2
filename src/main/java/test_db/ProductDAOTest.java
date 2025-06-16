package test_db;

import java.util.List;

import db.ProductDAO;
import models.Product;

/**
 * 產品資料存取物件（DAO）測試程式
 * 測試ProductDAO的查詢功能
 * 
 * DAO測試的重要性:
 * 
 * 1. 驗證資料存取層的功能:
 *    - 確認DAO能正確地執行CRUD操作
 *    - 驗證查詢結果是否符合預期
 * 
 * 2. 系統集成測試:
 *    - 驗證應用能與實際資料庫正常通信
 *    - 確認資料模型與資料庫結構的一致性
 * 
 * 3. 展示DAO模式的優勢:
 *    - 測試代碼只需知道DAO接口，不需了解資料庫實現細節
 *    - 測試代碼展示了如何在應用中正確使用DAO
 *    - 當底層資料庫技術變化時，測試代碼不需修改
 * 
 * 4. 資料庫連接管理測試:
 *    - 確認連接能正確建立和關閉
 *    - 驗證連接資源的適當釋放
 * 
 * 與直接使用DatabaseManager的區別:
 *    - ProductDAO隱藏了SQL細節和連接管理
 *    - 提供了更簡潔、業務導向的接口
 *    - 增加了一層抽象，便於未來替換資料存儲技術
 */
public class ProductDAOTest {
    
    public static void main(String[] args) {
        System.out.println("開始測試產品資料存取物件（DAO）...");
        
        ProductDAO productDAO = null;
        
        try {
            // 創建產品DAO
            productDAO = new ProductDAO();
            
            // 測試獲取所有產品
            System.out.println("\n===== 測試獲取所有產品 =====");
            List<Product> allProducts = productDAO.getAllProducts();
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
            Product product = productDAO.getProductById(testProductId);
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
            List<Product> categoryProducts = productDAO.getProductsByCategory(testCategory);
            if (categoryProducts.isEmpty()) {
                System.out.println("找不到類別為 " + testCategory + " 的產品。");
            } else {
                System.out.println("成功找到 " + categoryProducts.size() + " 個類別為 " + testCategory + " 的產品:");
                displayProducts(categoryProducts);
            }
            
        } finally {
            // 確保連線關閉
            if (productDAO != null) {
                productDAO.closeConnection();
                System.out.println("\n資料庫連線已關閉。");
            }
        }
        
        System.out.println("\n產品資料存取物件（DAO）測試完成。");
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
