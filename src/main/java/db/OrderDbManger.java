package db;

import app.MainMenuApp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Product;
import db.ProductDbManager;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import models.OrderDetail;
import models.OrderInfo;

/**
 * 訂單資料表管理類別 專門負責訂單資料的 CRUD 操作
 */
public class OrderDbManger extends DatabaseConnection {

    private ProductDbManager pm = MainMenuApp.productManager;
    TreeMap<String, Product> productMap = pm.getProducts();

    public Integer writeOrders(ObservableList<OrderDetail> orderDetails, int amount, int memberId) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return -1;
        }

        try {
            connection.setAutoCommit(false); // 開始交易
            if(memberId != -1) amount = (int)Math.round(amount * 0.9);
            int orderId = writeOrder(amount, memberId); // 插入主表，取得 order_id
            if (orderId == -1) {
                connection.rollback();
                return -1;
            }

            boolean itemsResult = writeItems(orderId, orderDetails);
            if (!itemsResult) {
                connection.rollback();
                return -1;
            }

            connection.commit(); // 所有步驟成功，提交交易
            return orderId;

        } catch (SQLException e) {
            System.out.println("寫入訂單交易失敗: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("回滾失敗: " + ex.getMessage());
            }
            return -1;

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("重設 AutoCommit 失敗: " + e.getMessage());
            }
        }
    }

// 寫入主表，回傳產生的 order_id
    private int writeOrder(int amount, int memberId) {
        String sql = "INSERT INTO orders (order_date, total_amount, member_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(1, date);
            pstmt.setDouble(2, amount);
            pstmt.setInt(3, memberId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("寫入訂單主表失敗: " + e.getMessage());
        }
        return -1;
    }

// 寫入明細表
    private boolean writeItems(int orderId, ObservableList<OrderDetail> items) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (OrderDetail od : items) {
                pstmt.setInt(1, orderId);
                pstmt.setString(2, od.getProduct_id());
                pstmt.setInt(3, od.getQuantity());
                pstmt.setInt(4, od.getProduct_price());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return true;

        } catch (SQLException e) {
            System.out.println("寫入訂單明細失敗: " + e.getMessage());
            return false;
        }
    }

    public List<OrderInfo> getAllOrder() {
        List<OrderInfo> list = new ArrayList<>();
        String sql = "SELECT DISTINCT order_id, order_date, total_amount, member_id FROM orders ORDER BY order_id DESC";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                String date = rs.getString("order_date");
                int total = rs.getInt("total_amount");
                int memberId = rs.getInt("member_id");
                list.add(new OrderInfo(orderId, date, total, memberId));
            }
        } catch (SQLException e) {
            System.out.println("讀取訂單編號失敗: " + e.getMessage());
        }
        return list;
    }

    public ObservableList<OrderDetail> getOrderDetailsByOrderId(String orderNum) {
        ObservableList<OrderDetail> details = FXCollections.observableArrayList();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, orderNum);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String pid = rs.getString("product_id");
                Product pd = productMap.get(pid);

                if (pd != null) {
                    OrderDetail od = new OrderDetail(pd);
                    od.setOrder_id(rs.getString("order_id"));
                    od.setQuantity(rs.getInt("quantity"));
                    details.add(od);
                }
            }
        } catch (SQLException e) {
            System.out.println("讀取訂單明細失敗: " + e.getMessage());
        }

        return details;
    }

    public void resetOrders() {
        String deleteOrderItems = "DELETE FROM order_items";
        String deleteOrders = "DELETE FROM orders";
        String resetOrderItemsSeq = "DELETE FROM sqlite_sequence WHERE name = 'order_items'";
        String resetOrdersSeq = "DELETE FROM sqlite_sequence WHERE name = 'orders'";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(deleteOrderItems);
            stmt.executeUpdate(deleteOrders);
            stmt.executeUpdate(resetOrderItemsSeq);
            stmt.executeUpdate(resetOrdersSeq);
            System.out.println("訂單資料已清空並重置自動編號。");
        } catch (SQLException e) {
            System.out.println("重置訂單資料失敗: " + e.getMessage());
        }
    }

    public void deleteOrderById(String orderId) {
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";

        try (PreparedStatement deleteItemsStmt = connection.prepareStatement(deleteItemsSql); PreparedStatement deleteOrderStmt = connection.prepareStatement(deleteOrderSql)) {

            // 先刪除 order_items 表中的對應資料
            deleteItemsStmt.setString(1, orderId);
            deleteItemsStmt.executeUpdate();

            // 再刪除 orders 表中的訂單資料
            deleteOrderStmt.setString(1, orderId);
            deleteOrderStmt.executeUpdate();

            System.out.println("已成功刪除訂單與相關明細，order_id = " + orderId);
        } catch (SQLException e) {
            System.out.println("刪除訂單失敗: " + e.getMessage());
        }
    }
}
