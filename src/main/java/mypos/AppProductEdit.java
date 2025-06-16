package mypos;

import db.ProductDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.Product;

//--------------------
// 1. 主程式類別宣告
//--------------------
public class AppProductEdit {

    //--------------------
    // 2. 欄位宣告：資料存取、資料清單、狀態顯示
    //--------------------
    private final ProductDAO productDao = new ProductDAO();
    private final ObservableList<Product> product_list = FXCollections.observableList(productDao.getAllProducts());
    private final Label statusLabel = new Label("狀態顯示區");

    public void show(Stage mainStage) {
        product_list.clear();
        product_list.addAll(FXCollections.observableList(productDao.getAllProducts()));
        Stage stage = new Stage();
        stage.setTitle("產品編輯系統");
        stage.setX(mainStage.getX() - 150);
        stage.setY(mainStage.getY() + 50);
        VBox root = getRootPane();
        root.getStylesheets().add("/css/bootstrap3.css");
        root.setStyle("-fx-background-color: #e0f7fa;");
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(root, 1155, 500);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/icon.png"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
        stage.close();
    }

    /*
    //--------------------
    // 3. JavaFX應用程式進入點
    //--------------------
    @Override
    public void start(Stage primaryStage) {
        VBox root = getRootPane();
        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("高科資管POS系統");
        primaryStage.show();
    }*/
    //--------------------
    // 4. 建立主畫面元件
    //--------------------
    public VBox getRootPane() {
        VBox searchPane = createSearchPane();
        // 產品表格
        TableView<Product> table = initializeProductTable();
        HBox toolbar = createToolbar(table);
        VBox vbox = new VBox(searchPane, toolbar, table, statusLabel);
        vbox.getStylesheets().add(getClass().getResource("/css/bootstrap3.css").toExternalForm());
        // 添加CSS樣式
        table.getStyleClass().add("table");
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        // 設定上下左右各20像素的padding
        vbox.setPadding(new Insets(20, 20, 20, 20));

        return vbox;
    }

    //--------------------
    // 6. 建立TableView欄位（字串型別）
    //--------------------
    private TableColumn<Product, String> createColumn(String title, String propertyName) {
        TableColumn<Product, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        return column;
    }

    //--------------------
    // 7. 建立TableView欄位（整數型別）
    //--------------------
    private TableColumn<Product, Integer> createColumn(String title, String propertyName, IntegerStringConverter converter) {
        TableColumn<Product, Integer> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        return column;
    }

    private <T> void setEditCommitHandler(TableColumn<Product, T> column, String propertyName) {
        column.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            switch (propertyName) {
                // 如果propertyName是"productId"，將新的值轉換為字串並設置到product的productId屬性中
                case "product_id":
                    product.setProduct_id(event.getNewValue().toString());
                    break;
                // 如果propertyName是"category"，將新的值轉換為字串並設置到product的category屬性中
                case "category":
                    product.setCategory(event.getNewValue().toString());
                    break;
                case "name":
                    product.setName(event.getNewValue().toString());
                    break;
                case "brand":
                    product.setName(event.getNewValue().toString());
                    break;
                case "price":
                    product.setPrice((Integer) event.getNewValue());
                    break;
                case "image":
                    product.setImage(event.getNewValue().toString());
                    break;
                case "description":
                    product.setDescription(event.getNewValue().toString());
                    break;
            }
            System.out.println(propertyName + " updated: " + product);
        });
    }

    private VBox createSearchPane() {
        // 新增查詢區塊元件
        VBox searchPane = new VBox();
        searchPane.setSpacing(10);

        // 查詢產品ID
        Label lblId = new Label("ID：  ");
        TextField tfId = new TextField();
        Button btnSearchId = new Button("查詢ID");
        btnSearchId.setOnAction(e -> {
            statusLabel.setText("查詢產品ID: " + tfId.getText());
            product_list.clear();
            product_list.add(productDao.getProductById(tfId.getText()));
        });

        // 查詢產品類別
        Label lblCategory = new Label("類別：");
        TextField tfCategory = new TextField();
        Button btnSearchCategory = new Button("查詢類別");
        btnSearchCategory.setOnAction(e -> {
            statusLabel.setText("查詢產品類別: " + tfCategory.getText());
            product_list.clear();
            product_list.addAll(FXCollections.observableList(productDao.getProductsByCategory(tfCategory.getText())));
        });

        // 查詢全部產品
        Button btnAll = new Button("全部產品");
        btnAll.setOnAction(e -> {
            statusLabel.setText("顯示全部產品");
            product_list.clear();
            product_list.addAll(FXCollections.observableList(productDao.getAllProducts()));
        });

        // 搜尋產品名稱
        Label lblName = new Label("名稱：");
        TextField tfName = new TextField();
        tfName.setPrefColumnCount(30);
        Button btnSearchName = new Button("搜尋名稱");
        btnSearchName.setOnAction(e -> {
            statusLabel.setText("搜尋產品名稱: " + tfName.getText());
            product_list.clear();
            product_list.addAll(FXCollections.observableList(productDao.getProductsByName(tfName.getText())));
        });
        
        HBox search1 = new HBox(0);
        search1.setSpacing(20);
        search1.setAlignment(Pos.CENTER_LEFT);
        search1.getChildren().addAll(lblId, tfId, btnSearchId, lblCategory, tfCategory, btnSearchCategory);
        HBox search2 = new HBox(0);
        search2.setSpacing(20);
        search2.setAlignment(Pos.CENTER_LEFT);
        search2.getChildren().addAll(lblName, tfName, btnSearchName, btnAll);
        searchPane.getChildren().addAll(search1, search2);
        return searchPane;
    }
    
    // 模組化：操作按鈕區塊
    private HBox createToolbar(TableView<Product> table) {
        Button btnUpdate = new Button("更新&儲存");
        Button btnDuplicate = new Button("重複");
        //Button btnSave = new Button("儲存");
        Button btnDelete = new Button("刪除");

        btnUpdate.getStyleClass().setAll("button", "success");
        btnDuplicate.getStyleClass().setAll("button", "warning");
        //btnSave.getStyleClass().setAll("button", "success");
        btnDelete.getStyleClass().setAll("button", "danger");

        btnUpdate.setOnAction(event -> {
            Product product = table.getSelectionModel().getSelectedItem();
            if (product != null) {
                boolean find = productDao.getProductById(product.getProduct_id()) != null;
                if (find) {
                    boolean updateSuccess = productDao.update(product);
                    if (updateSuccess) {
                        statusLabel.setText("已更新: " + product.getName());
                        System.out.println("已更新: " + product.getName());
                    } else {
                        statusLabel.setText("更新失敗: " + product.getName());
                        System.out.println("更新失敗: " + product.getName());
                    }
                } else {
                    productDao.add(product);
                    statusLabel.setText("儲存: " + product.getName());
                    System.out.println("儲存: " + product.getName());
                }
            }
        });

        btnDuplicate.setOnAction(event -> {
            Product product = table.getSelectionModel().getSelectedItem();
            if (product != null) {
                String newProductId = product.getProduct_id() + "_copy";
                Product duplicatedProduct = new Product(
                        newProductId,
                        product.getCategory(),
                        product.getBrand(),
                        product.getName() + " (Copy)",
                        product.getPrice(),
                        product.getImage(),
                        product.getDescription()
                );
                product_list.add(duplicatedProduct);
                statusLabel.setText("重複: " + product.getName() + " (尚未存檔)");
                System.out.println("重複: " + product.getName() + " (尚未存檔)");
            }
        });

        btnDelete.setOnAction(event -> {
            Product product = table.getSelectionModel().getSelectedItem();
            if (product != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("確認刪除");
                alert.setHeaderText("您即將刪除產品: " + product.getName());
                alert.setContentText("確定要刪除這個產品嗎?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean deleteSuccess = productDao.delete(product.getProduct_id());
                        if (deleteSuccess) {
                            product_list.remove(product);
                            statusLabel.setText("刪除: " + product.getName());
                            System.out.println("刪除: " + product.getName());
                        } else {
                            statusLabel.setText("刪除失敗: " + product.getName());
                            System.out.println("刪除失敗: " + product.getName());
                        }
                    }
                });
            }
        });

        HBox toolbar = new HBox(btnUpdate, btnDuplicate, btnDelete);
        toolbar.setSpacing(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        return toolbar;
    }

    //--------------------
    // 9. 初始化TableView與所有欄位、按鈕
    //--------------------
    private TableView<Product> initializeProductTable() {
        TableView<Product> table = new TableView<>();
        table.setEditable(true);
        // 表格最後一欄是空白，不要顯示!
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //自動均分欄位大小
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // 定義表格欄位
        TableColumn<Product, String> idColumn = createColumn("產品ID", "product_id");
        idColumn.setPrefWidth(80);
        TableColumn<Product, String> categoryColumn = createColumn("類別", "category");
        categoryColumn.setPrefWidth(60);
        TableColumn<Product, String> nameColumn = createColumn("名稱", "name");
        nameColumn.setPrefWidth(180);
        TableColumn<Product, String> brandColumn = createColumn("品牌", "brand");
        brandColumn.setPrefWidth(80);
        TableColumn<Product, Integer> priceColumn = createColumn("價格", "price", new IntegerStringConverter());
        priceColumn.setPrefWidth(60);
        TableColumn<Product, String> imageColumn = createColumn("圖片網址", "image");
        imageColumn.setPrefWidth(90);
        TableColumn<Product, String> descriptionColumn = createColumn("簡介", "description");
        descriptionColumn.setPrefWidth(380);
        // 設定欄位允許編輯後的事件處理
        setEditCommitHandler(idColumn, "product_id");
        setEditCommitHandler(categoryColumn, "category");
        setEditCommitHandler(nameColumn, "name");
        setEditCommitHandler(brandColumn, "brand");
        setEditCommitHandler(priceColumn, "price");
        setEditCommitHandler(imageColumn, "image");
        setEditCommitHandler(descriptionColumn, "description");

        //--------------------
        // 11. 將所有欄位加入TableView
        //--------------------
        table.getColumns().add(idColumn);
        table.getColumns().add(categoryColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(brandColumn);
        table.getColumns().add(priceColumn);
        table.getColumns().add(imageColumn);
        table.getColumns().add(descriptionColumn);

        table.setItems(product_list);

        return table;
    }
    //--------------------
    // End of class
    //--------------------
}
