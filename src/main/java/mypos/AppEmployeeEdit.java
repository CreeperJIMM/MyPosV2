package mypos;

import app.MainMenuApp;
import dao.EmployeeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.Employee;

//--------------------
// 1. 主程式類別宣告
//--------------------
public class AppEmployeeEdit {

    //--------------------
    // 2. 欄位宣告：資料存取、資料清單、狀態顯示
    //--------------------
    private final EmployeeDAO employeeDao = new EmployeeDAO();
    private final ObservableList<Employee> employee_list = FXCollections.observableList(employeeDao.getAllEmployees());
    private final Label statusLabel = new Label("狀態顯示區");

    public void show(Stage mainStage) {
        employee_list.clear();
        employee_list.addAll(FXCollections.observableList(employeeDao.getAllEmployees()));
        Stage stage = new Stage();
        stage.setTitle("用戶編輯系統");
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
        HBox searchPane = createSearchPane();
        // 產品表格
        TableView<Employee> table = initializeProductTable();
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
    private TableColumn<Employee, String> createColumn(String title, String propertyName, String... options) {
        TableColumn<Employee, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));

        if (options != null && options.length > 0) {
            // 如果有指定選項，使用 ComboBoxTableCell
            ObservableList<String> optionList = FXCollections.observableArrayList(options);
            column.setCellFactory(ComboBoxTableCell.forTableColumn(optionList));
        } else {
            // 否則使用預設文字輸入
            column.setCellFactory(TextFieldTableCell.forTableColumn());
        }

        return column;
    }

    //--------------------
    // 7. 建立TableView欄位（整數型別）
    //--------------------
    private TableColumn<Employee, Integer> createColumn(String title, String propertyName, IntegerStringConverter converter) {
        TableColumn<Employee, Integer> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        return column;
    }

    private <T> void setEditCommitHandler(TableColumn<Employee, T> column, String propertyName) {
        column.setOnEditCommit(event -> {
            Employee employee = event.getRowValue();
            switch (propertyName) {
                case "id":
                    employee.setId((Integer) event.getNewValue());
                    break;
                case "department":
                    employee.setDepartment(event.getNewValue().toString());
                    break;
                case "username":
                    employee.setUsername(event.getNewValue().toString());
                    break;
                case "password":
                    employee.setPassword(event.getNewValue().toString());
                    break;
                case "role":
                    employee.setRole(event.getNewValue().toString());
                    break;
            }
            System.out.println(propertyName + " updated: " + employee);
        });
    }

    private HBox createSearchPane() {
        // 新增查詢區塊元件
        HBox searchPane = new HBox();
        searchPane.setSpacing(20);
        searchPane.setAlignment(Pos.CENTER_LEFT);
        // 查詢產品ID
        Label lblId = new Label("ID：  ");
        TextField tfId = new TextField();
        tfId.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        Button btnSearchId = new Button("查詢ID");
        btnSearchId.setOnAction(e -> {
            statusLabel.setText("查詢用戶ID: " + tfId.getText());
            employee_list.clear();
            employee_list.add(employeeDao.getEmployeeById(Integer.parseInt(tfId.getText())));
        });


        // 查詢用戶名稱
        Label lblName = new Label("名稱：  ");
        TextField tfName = new TextField();
        Button btnSearchName = new Button("查詢名稱");
        btnSearchName.setOnAction(e -> {
            statusLabel.setText("查詢用戶名稱: " + tfName.getText());
            employee_list.clear();
            employee_list.add(employeeDao.getEmployeeByUsername(tfName.getText()));

        });

        // 查詢全部產品
        Button btnAll = new Button("全部用戶");
        btnAll.setOnAction(e -> {
            statusLabel.setText("顯示全部用戶");
            employee_list.clear();
            employee_list.addAll(FXCollections.observableList(employeeDao.getAllEmployees()));
        });
        searchPane.getChildren().addAll(lblId,tfId,btnSearchId, lblName, tfName, btnSearchName, btnAll);
        return searchPane;
    }
    
    // 模組化：操作按鈕區塊
    private HBox createToolbar(TableView<Employee> table) {
        Button btnUpdate = new Button("更新&儲存");
        Button btnDuplicate = new Button("重複");
        //Button btnSave = new Button("儲存");
        Button btnDelete = new Button("刪除");

        btnUpdate.getStyleClass().setAll("button", "success");
        btnDuplicate.getStyleClass().setAll("button", "warning");
        //btnSave.getStyleClass().setAll("button", "success");
        btnDelete.getStyleClass().setAll("button", "danger");

        btnUpdate.setOnAction(event -> {
            Employee employee = table.getSelectionModel().getSelectedItem();
            if (employee != null) {
                boolean find = employeeDao.getEmployeeById(employee.getId()) != null;
                if(find) {
                    boolean updateSuccess = employeeDao.updateEmployee(employee);
                    if (updateSuccess) {
                        statusLabel.setText("已更新: " + employee.getUsername());
                        System.out.println("已更新: " + employee.getUsername());
                    } else {
                        statusLabel.setText("更新失敗: " + employee.getUsername());
                        System.out.println("更新失敗: " + employee.getUsername());
                    }
                } else {
                    employeeDao.addEmployee(employee);
                    statusLabel.setText("儲存: " + employee.getUsername());
                    System.out.println("儲存: " + employee.getUsername());
                }
            }
        });

        btnDuplicate.setOnAction(event -> {
            Employee employee = table.getSelectionModel().getSelectedItem();
            if (employee != null) {
                int newEmployeeId = employee_list.size()+1;
                Employee duplicatedProduct = new Employee(
                        newEmployeeId,
                        employee.getUsername() + "1",
                        employee.getPassword(),
                        employee.getRole(),
                        employee.getDepartment()
                );
                employee_list.add(duplicatedProduct);
                statusLabel.setText("重複: " + employee.getUsername() + " (尚未存檔)");
                System.out.println("重複: " + employee.getUsername() + " (尚未存檔)");
            }
        });

        btnDelete.setOnAction(event -> {
            Employee employee = table.getSelectionModel().getSelectedItem();
            if (employee != null) {
                if (employee.getId() == MainMenuApp.currentUser.getId()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("無法刪除");
                    alert.setHeaderText("不可刪除當前登入用戶");
                    //alert.setContentText("");
                    alert.show();
                    return;
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("確認刪除");
                alert.setHeaderText("您即將刪除用戶: " + employee.getUsername());
                alert.setContentText("確定要刪除這個用戶嗎?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean deleteSuccess = employeeDao.deleteEmployee(employee.getUsername());
                        if (deleteSuccess) {
                            employee_list.remove(employee);
                            statusLabel.setText("刪除: " + employee.getUsername());
                            System.out.println("刪除: " + employee.getUsername());
                        } else {
                            statusLabel.setText("刪除失敗: " + employee.getUsername());
                            System.out.println("刪除失敗: " + employee.getUsername());
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
    private TableView<Employee> initializeProductTable() {
        TableView<Employee> table = new TableView<>();
        table.setEditable(true);
        // 表格最後一欄是空白，不要顯示!
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //自動均分欄位大小
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // 定義表格欄位
        TableColumn<Employee, Integer> idColumn = createColumn("ID", "id", new IntegerStringConverter());
        idColumn.setPrefWidth(100);
        TableColumn<Employee, String> departColumn = createColumn("部門", "department", "IT", "Sales", "Finance", "Warehouse");
        departColumn.setPrefWidth(200);
        TableColumn<Employee, String> nameColumn = createColumn("名稱", "username");
        nameColumn.setPrefWidth(200);
        TableColumn<Employee, String> pwdColumn = createColumn("密碼", "password");
        pwdColumn.setPrefWidth(200);
        TableColumn<Employee, String> roleColumn = createColumn("角色", "role", "admin", "order", "inventory", "data", "support");
        roleColumn.setPrefWidth(190);
        // 設定欄位允許編輯後的事件處理
        //setEditCommitHandler(idColumn, "id");
        idColumn.setEditable(false);
        setEditCommitHandler(departColumn, "department");
        //setEditCommitHandler(nameColumn, "username");
        setEditCommitHandler(pwdColumn, "password");
        setEditCommitHandler(roleColumn, "role");

        //名稱檢查是否重複
        nameColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            Employee currentEmployee = event.getRowValue();
            // 檢查是否有重複 username（排除自己）
            boolean isDuplicate = table.getItems().stream()
                    .filter(emp -> emp != currentEmployee)
                    .anyMatch(emp -> emp.getUsername().equals(newValue));
            if (isDuplicate) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("重複名稱");
                alert.setHeaderText(null);
                alert.setContentText("使用者名稱 '" + newValue + "' 已存在，請輸入其他名稱。");
                alert.showAndWait();
                table.refresh();
            } else {
                currentEmployee.setUsername(newValue);
            }
        });
        
        //--------------------
        // 11. 將所有欄位加入TableView
        //--------------------
        table.getColumns().add(idColumn);
        table.getColumns().add(departColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(pwdColumn);
        table.getColumns().add(roleColumn);

        table.setItems(employee_list);

        return table;
    }
    //--------------------
    // End of class
    //--------------------
}
