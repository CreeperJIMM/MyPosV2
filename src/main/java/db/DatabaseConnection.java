package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 資料庫連線管理類別
 * 負責提供資料庫連線
 * 
 * 繼承實現方式:
 * 更好的物件導向設計：符合SOLID原則
 * 靈活性高：可以有不同的子類實現不同的數據庫連接策略
 * 便於測試：可以注入模擬對象進行測試
 * 生命周期管理明確：可以控制連接的創建和關閉
 * 擴展性強：容易擴展到連接池、多種數據庫等場景
 * 
 * 在DAO (Data Access Object)模式中的角色:
 * 此類別屬於DAO模式中的「資料來源」(Data Source)部分，負責處理與實際資料庫的連接。
 * DAO模式將此低層級的資料庫連接邏輯與高層級的業務邏輯分離，增加系統靈活性。
 */
public class DatabaseConnection {
    protected static final String DB_URL = "jdbc:sqlite:pos.db";
    protected Connection connection;
    
    // 其他資料庫連線方式
    //private static final String URL = "jdbc:mariadb://localhost:3306/db_pos";
    //private static final String USER = "mis";
    //private static final String PWD = "mis123";

    public DatabaseConnection() {
        try {
            // 建立資料庫連線
            connection = DriverManager.getConnection(DB_URL);
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
            System.out.println("資料庫連線已建立。");
            
            // 移除初始化資料表的方法呼叫，因為資料表已經手動建立
        } catch (SQLException e) {
            System.out.println("資料庫連線錯誤: " + e.getMessage());
        }
    }
    
    /**
     * 取得資料庫連線
     * @return 資料庫連線
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * 關閉資料庫連線
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("關閉資料庫連線錯誤: " + e.getMessage());
        }
    }
}
