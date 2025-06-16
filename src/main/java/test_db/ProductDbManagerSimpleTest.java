package test_db;

import java.util.List;
import java.util.TreeMap;

import db.ProductDbManager;
import models.Product;

/**
 * ProductDbManagerSimple 類別的測試程式
 * 測試對產品資料表的基本操作功能
 */
public class ProductDbManagerSimpleTest {
    
    public static void main(String[] args) {
        // 創建資料庫管理類別實例
        ProductDbManager productManager = new ProductDbManager();
        
        // 測試獲取所有產品
        System.out.println("===== 測試獲取所有產品 =====");
        List<Product> allProducts = productManager.getAllProducts();
        System.out.println("總共取得 " + allProducts.size() + " 個產品");
        
        // 列印前5個產品作為範例
        int count = 0;
        for (Product product : allProducts) {
            if (count++ < 5) {
                System.out.println("產品ID: " + product.getProduct_id()+ 
                                  ", 名稱: " + product.getName() + 
                                  ", 類別: " + product.getCategory() + 
                                  ", 價格: " + product.getPrice());
            }
        }
        
        // 測試獲取所有產品類別
        System.out.println("\n===== 測試獲取所有產品類別 =====");
        String[] categories = productManager.getCategories();
        System.out.println("總共取得 " + categories.length + " 個產品類別");
        for (String category : categories) {
            System.out.println("類別: " + category);
        }
        
        // 測試以TreeMap獲取所有產品
        System.out.println("\n===== 測試以TreeMap獲取所有產品 =====");
        TreeMap<String, Product> productMap = productManager.getProducts();
        System.out.println("TreeMap中的產品數: " + productMap.size());
        
        // 列印TreeMap中的第一個和最後一個產品
        if (!productMap.isEmpty()) {
            String firstKey = productMap.firstKey();
            String lastKey = productMap.lastKey();
            
            System.out.println("第一個產品 (ID: " + firstKey + "): " + 
                              productMap.get(firstKey).getName());
            System.out.println("最後一個產品 (ID: " + lastKey + "): " + 
                              productMap.get(lastKey).getName());
        }
        
        System.out.println("\n測試完成!");
    }
}
