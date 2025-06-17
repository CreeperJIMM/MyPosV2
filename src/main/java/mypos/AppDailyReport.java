package mypos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import db.DatabaseConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppDailyReport extends Application {

    public static class DailySales {
        private final LocalDate date;
        private final double totalAmount;

        public DailySales(LocalDate date, double totalAmount) {
            this.date = date;
            this.totalAmount = totalAmount;
        }

        public LocalDate getDate() { return date; }
        public double getTotalAmount() { return totalAmount; }
    }

    // 以訂單為單位的每日營業額，回傳文字
    private String fetchDailySalesText() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== 每日營業額統計 ======\n");
        sb.append("---------------------------\n");
        String sql = "SELECT date(order_date) as order_date, SUM(total_amount) as total " +
                     "FROM orders GROUP BY date(order_date) ORDER BY date(order_date) DESC";
        DatabaseConnection dbConn = new DatabaseConnection();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            sb.append("日期\t\t營業額\n");
            while (rs.next()) {
                String dateStr = rs.getString("order_date");
                if (dateStr == null) continue;
                double total = rs.getDouble("total");
                sb.append(String.format("%s\t%.0f\n", dateStr, total));
            }
        } catch (SQLException e) {
            sb.append("查詢失敗: ").append(e.getMessage());
        } finally {
            dbConn.close();
        }
        return sb.toString();
    }

    // 新增：查詢三種category產品的銷售金額統計
    private String fetchCategorySalesSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== 各類別產品銷售金額統計 ======\n");
        sb.append("---------------------------\n");
        String sql = "SELECT p.category, SUM(od.quantity * p.price) AS sales " +
                     "FROM order_items od " +
                     "JOIN products p ON od.product_id = p.product_id " +
                     "GROUP BY p.category " +
                     "ORDER BY p.category";
        double total = 0.0;
        DatabaseConnection dbConn = new DatabaseConnection();
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("category");
                double sales = rs.getDouble("sales");
                sb.append(String.format("%s：%.0f 元\n", category, sales));
                total += sales;
            }
            sb.append("---------------------------\n");
            sb.append(String.format("總共：%.0f 元\n", total));
        } catch (SQLException e) {
            sb.append("查詢失敗: ").append(e.getMessage());
        } finally {
            dbConn.close();
        }
        return sb.toString();
    }
    /*
    // 新增：查詢每筆訂單的明細 (品名 數量 價格 總價)
    private String fetchOrderDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== 訂單明細 ======\n");
        sb.append("---------------------------\n");
        String sql = "SELECT so.order_id, so.order_date, so.total_amount, p.name, p.category, od.quantity, p.price, (od.quantity * p.price) AS total " +
                     "FROM orders so " +
                     "JOIN order_items od ON so.order_id = od.order_id " +
                     "JOIN products p ON od.product_id = p.product_id " +
                     "ORDER BY so.order_date DESC, so.order_id, od.order_id";
        DatabaseConnection dbConn = new DatabaseConnection();
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            String lastOrderId = "-1";
            boolean firstOrder = true;
            double orderAmount = 0.0;
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                String orderDate = rs.getString("order_date");
                // 只取日期部分
                if (orderDate != null && orderDate.length() >= 10) {
                    orderDate = orderDate.substring(0, 10);
                }
                double totalAmount = rs.getDouble("total_amount");
                String productCategory = rs.getString("category");
                String productName = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double total = rs.getDouble("total");
                if (!orderId.equals(lastOrderId)) {
                    if (!firstOrder) {
                        sb.append("--------------------------------------------------\n");
                    }
                    firstOrder = false;
                    sb.append(String.format("訂單編號: %s  日期: %s  總金額: %.0f\n", orderId, orderDate, totalAmount));
                    // 使用固定欄寬對齊
                    sb.append(String.format("%-12s%-8s%-8s%-8s%-8s\n", "品名", "類別", "數量", "價格", "總價"));
                    lastOrderId = orderId;
                }
                sb.append(String.format("%-12s%-8s%-8d%-8.0f%-8.0f\n", productName, productCategory, quantity, price, total));
            }
        } catch (SQLException e) {
            sb.append("查詢失敗: ").append(e.getMessage());
        } finally {
            dbConn.close();
        }
        return sb.toString();
    }*/

    // 新增：產生 category 銷售金額長條圖
    private BarChart<String, Number> createCategorySalesBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("產品類別");
        yAxis.setLabel("銷售金額");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("各類別產品銷售金額");
        barChart.setPrefSize(500, 650); // 可自訂長寬 (寬, 高)

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("銷售金額");

        String sql = "SELECT p.category, SUM(od.quantity * p.price) AS sales " +
                     "FROM order_items od " +
                     "JOIN products p ON od.product_id = p.product_id " +
                     "GROUP BY p.category " +
                     "ORDER BY p.category";
        DatabaseConnection dbConn = new DatabaseConnection();
        try (Connection conn = dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("category");
                double sales = rs.getDouble("sales");
                series.getData().add(new XYChart.Data<>(category, sales));
            }
        } catch (SQLException e) {
            // 可選：顯示錯誤訊息
        } finally {
            dbConn.close();
        }
        barChart.getData().add(series);
        return barChart;
    }
    
    // 新增：產生每日金額長條圖
    private LineChart<String, Number> createDailySalesLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("日期");
        yAxis.setLabel("銷售金額");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("每日銷售總額");
        lineChart.setPrefSize(500, 650); // 可自訂長寬 (寬, 高)

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("銷售金額");

        String sql = "SELECT date(order_date) AS order_date, SUM(total_amount) AS total "
                + "FROM orders GROUP BY date(order_date) ORDER BY date(order_date) DESC";
        
        
        DatabaseConnection dbConn = new DatabaseConnection();
        try (Connection conn = dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            List<XYChart.Data<String, Number>> dataList = new ArrayList<>();
            while (rs.next()) {
                String date = rs.getString("order_date"); // 修正為日期欄位
                double total = rs.getDouble("total");     // 修正為 total 欄位
                dataList.add(new XYChart.Data<>(date, total));
            }
            Collections.reverse(dataList);
            series.getData().addAll(dataList);
        } catch (SQLException e) {
            e.printStackTrace(); // 或自行處理錯誤顯示
        } finally {
            dbConn.close();
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    // 產生前五產品熱銷排行榜
    private BarChart<Number, String> createTopProductsChart() {
        CategoryAxis yAxis = new CategoryAxis();
        NumberAxis xAxis = new NumberAxis();
        yAxis.setLabel("產品名稱");
        xAxis.setLabel("銷售數量");

        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top 5 熱銷產品");
        barChart.setPrefSize(500, 650);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("熱銷商品");

        String sql = "SELECT p.name, SUM(oi.quantity) AS total_sold "
                + "FROM order_items oi "
                + "JOIN products p ON oi.product_id = p.product_id "
                + "GROUP BY p.name "
                + "ORDER BY total_sold DESC "
                + "LIMIT 5";

        DatabaseConnection dbConn = new DatabaseConnection();
        try (Connection conn = dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            List<XYChart.Data<Number, String>> dataList = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("total_sold");
                dataList.add(new XYChart.Data<>(quantity, name));
            }
            Collections.reverse(dataList);
            series.getData().addAll(dataList);
        } catch (SQLException e) {
            e.printStackTrace(); // 建議實務上顯示錯誤提示
        } finally {
            dbConn.close();
        }

        barChart.getData().add(series);
        return barChart;
    }

    public VBox getRootPane() {
        // 報表標題
        Label categorySummaryLabel = new Label("各類別產品銷售金額統計");
        categorySummaryLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Label dailySalesLabel = new Label("每日營業額統計");
        dailySalesLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        Label barChartLabel = new Label("各類別產品銷售金額長條圖");
        barChartLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        // 新增 TextArea 顯示 category 銷售金額統計
        TextArea categorySummaryArea = new TextArea();
        categorySummaryArea.setEditable(false);
        categorySummaryArea.setPrefRowCount(16);
        categorySummaryArea.setPrefColumnCount(32);
        categorySummaryArea.setText(fetchCategorySalesSummary());
        
        // 新增 BarChart 顯示 category 銷售金額統計
        BarChart<String, Number> categoryBarChart = createCategorySalesBarChart();

        // 新增 TextArea 顯示每日營業額統計
        TextArea dailySalesArea = new TextArea();
        dailySalesArea.setEditable(false);
        dailySalesArea.setPrefRowCount(16);
        dailySalesArea.setPrefColumnCount(32);
        dailySalesArea.setText(fetchDailySalesText());

        // 左半邊 VBox (放類別銷售統計、長條圖、每日營業額統計)
        VBox leftVBox = new VBox(10,
            categorySummaryLabel,
            categorySummaryArea,
            dailySalesLabel,
            dailySalesArea
        );

        // 右半邊 VBox (只放訂單明細)
        VBox rightVBox = new VBox(10, barChartLabel, categoryBarChart);

        // 使用 HBox 左右排列
        HBox mainHBox = new HBox(30, leftVBox, rightVBox);
        mainHBox.setAlignment(Pos.TOP_CENTER);

        // 新增圖表選擇
        ComboBox<String> chartSelector = new ComboBox<>();
        chartSelector.getItems().addAll(
            "各類別產品銷售金額長條圖",
            "每日營業額折線圖",
            "前5熱銷產品長條圖"
        );
        chartSelector.setValue("各類別產品銷售金額長條圖");
        
        chartSelector.setOnAction(e -> {
            String selected = chartSelector.getValue();
            barChartLabel.setText(selected);
            switch (selected) {
                case "每日營業額折線圖":
                    LineChart<String, Number> newChart1 = createDailySalesLineChart();
                    rightVBox.getChildren().clear();
                    rightVBox.getChildren().addAll(barChartLabel, newChart1);
                    break;
                case "各類別產品銷售金額長條圖":
                    BarChart<String, Number> newChart2 = createCategorySalesBarChart();
                    rightVBox.getChildren().clear();
                    rightVBox.getChildren().addAll(barChartLabel, newChart2);
                    break;
                case "前5熱銷產品長條圖":
                    BarChart<Number, String> newChart3 = createTopProductsChart();
                    rightVBox.getChildren().clear();
                    rightVBox.getChildren().addAll(barChartLabel, newChart3);
                    break;
            }
        });
        
        // 新增「重新產生報表」按鈕
        Button refreshButton = new Button("重新產生報表");
        refreshButton.setOnAction(e -> {
            categorySummaryArea.setText(fetchCategorySalesSummary());
            dailySalesArea.setText(fetchDailySalesText());
            chartSelector.getOnAction().handle(new ActionEvent());
        });
        
        HBox topBar = new HBox(200);
        topBar.getChildren().addAll(refreshButton, chartSelector);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        
        VBox root = new VBox(15);
        root.getChildren().addAll(
            topBar,
            mainHBox
        );
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(900, 700);
        root.setStyle("-fx-padding: 20;");
        root.getStylesheets().add(getClass().getResource("/css/bootstrap3.css").toExternalForm());

        return root;
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("每日營業額報表");
        primaryStage.setScene(new Scene(getRootPane()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
