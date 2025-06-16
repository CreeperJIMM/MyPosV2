package test_db;

import java.sql.Connection;
import java.sql.SQLException;

import db.DatabaseConnection;

/**
 * 資料庫連線測試程式
 * 用於驗證資料庫連線是否能正常建立
 */
public class ConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("開始測試資料庫連線...");
        
        DatabaseConnection dbConn = null;
        
        try {
            // 嘗試建立資料庫連線
            dbConn = new DatabaseConnection();
            Connection connection = dbConn.getConnection();
            
            // 檢查連線是否為null
            if (connection != null && !connection.isClosed()) {
                System.out.println("連線測試成功！成功連接到資料庫。");
                
                // 顯示資料庫基本資訊
                System.out.println("資料庫產品名稱: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("資料庫產品版本: " + connection.getMetaData().getDatabaseProductVersion());
                System.out.println("JDBC驅動名稱: " + connection.getMetaData().getDriverName());
                System.out.println("JDBC驅動版本: " + connection.getMetaData().getDriverVersion());
            } else {
                System.out.println("連線測試失敗：未能取得有效的資料庫連線。");
            }
            
        } catch (SQLException e) {
            System.out.println("連線測試失敗：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 確保連線關閉
            if (dbConn != null) {
                dbConn.close();
                System.out.println("資料庫連線已關閉。");
            }
        }
        
        System.out.println("資料庫連線測試完成。");
    }
}
