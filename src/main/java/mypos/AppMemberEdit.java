package mypos;

import dao.MemberDAO;
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
import models.Member;

//--------------------
// 1. 主程式類別宣告
//--------------------
public class AppMemberEdit {

    //--------------------
    // 2. 欄位宣告：資料存取、資料清單、狀態顯示
    //--------------------
    private final MemberDAO memberDao = new MemberDAO();
    private final ObservableList<Member> member_list = FXCollections.observableList(memberDao.getAllMembers());
    private final Label statusLabel = new Label("狀態顯示區");

    public void show(Stage mainStage) {
        member_list.clear();
        member_list.addAll(FXCollections.observableList(memberDao.getAllMembers()));
        Stage stage = new Stage();
        stage.setTitle("會員編輯系統");
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
        TableView<Member> table = initializeProductTable();
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
    private TableColumn<Member, String> createColumn(String title, String propertyName, String... options) {
        TableColumn<Member, String> column = new TableColumn<>(title);
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
    private TableColumn<Member, Integer> createColumn(String title, String propertyName, IntegerStringConverter converter) {
        TableColumn<Member, Integer> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        return column;
    }

    private <T> void setEditCommitHandler(TableColumn<Member, T> column, String propertyName) {
        column.setOnEditCommit(event -> {
            Member member = event.getRowValue();
            switch (propertyName) {
                case "id":
                    member.setId((Integer) event.getNewValue());
                    break;
                case "sex":
                    member.setSex(event.getNewValue().toString());
                    break;
                case "name":
                    member.setName(event.getNewValue().toString());
                    break;
                case "address":
                    member.setAddress(event.getNewValue().toString());
                    break;
                case "phone":
                    member.setPhone(event.getNewValue().toString());
                    break;
                case "birthday":
                    member.setBirthday(event.getNewValue().toString());
                    break;
                case "create_date":
                    member.setCreateDate(event.getNewValue().toString());
                    break;
            }
            System.out.println(propertyName + " 更新: " + member);
        });
    }

    private HBox createSearchPane() {
        // 新增查詢區塊元件
        HBox searchPane = new HBox();
        searchPane.setSpacing(10);
        searchPane.setAlignment(Pos.CENTER_LEFT);
        // 查詢產品ID
        Label lblId = new Label("ID：");
        TextField tfId = new TextField();
        tfId.setMaxWidth(40);
        tfId.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        Button btnSearchId = new Button("查詢ID");
        btnSearchId.setOnAction(e -> {
            if(tfId.getText() == "") return;
            statusLabel.setText("查詢會員ID: " + tfId.getText());
            member_list.clear();
            member_list.add(memberDao.getMemberById(Integer.parseInt(tfId.getText())));
        });

        // 查詢用戶名稱
        Label lblName = new Label("名字：");
        TextField tfName = new TextField();
        Button btnSearchName = new Button("查詢名字");
        btnSearchName.setOnAction(e -> {
            statusLabel.setText("查詢會員名字: " + tfName.getText());
            member_list.clear();
            member_list.add(memberDao.getMemberByName(tfName.getText()));

        });
        
        // 查詢用戶手機號
        Label lblPhone = new Label("手機號碼：");
        TextField tfPhone = new TextField();
        Button btnSearchPhone = new Button("查詢號碼");
        btnSearchPhone.setOnAction(e -> {
            statusLabel.setText("查詢會員手機號碼: " + tfPhone.getText());
            member_list.clear();
            member_list.add(memberDao.getMemberByPhone(tfPhone.getText()));

        });

        // 查詢全部會員
        Button btnAll = new Button("全部會員");
        btnAll.setOnAction(e -> {
            statusLabel.setText("顯示全部會員");
            member_list.clear();
            member_list.addAll(FXCollections.observableList(memberDao.getAllMembers()));
        });
        searchPane.getChildren().addAll(lblId,tfId,btnSearchId, lblName, tfName, btnSearchName, lblPhone, tfPhone, btnSearchPhone, btnAll);
        return searchPane;
    }
    
    // 模組化：操作按鈕區塊
    private HBox createToolbar(TableView<Member> table) {
        Button btnUpdate = new Button("更新&儲存");
        Button btnDuplicate = new Button("重複");
        //Button btnSave = new Button("儲存");
        Button btnDelete = new Button("刪除");

        btnUpdate.getStyleClass().setAll("button", "success");
        btnDuplicate.getStyleClass().setAll("button", "warning");
        //btnSave.getStyleClass().setAll("button", "success");
        btnDelete.getStyleClass().setAll("button", "danger");

        btnUpdate.setOnAction(event -> {
            Member member = table.getSelectionModel().getSelectedItem();
            if (member != null) {
                boolean find = memberDao.getMemberById(member.getId()) != null;
                if(find) {
                    boolean updateSuccess = memberDao.updateMember(member);
                    if (updateSuccess) {
                        statusLabel.setText("已更新: " + member.getName());
                        System.out.println("已更新: " + member.getName());
                    } else {
                        statusLabel.setText("更新失敗: " + member.getName());
                        System.out.println("更新失敗: " + member.getName());
                    }
                } else {
                    memberDao.addMember(member);
                    statusLabel.setText("儲存: " + member.getName());
                    System.out.println("儲存: " + member.getName());
                }
            }
        });

        btnDuplicate.setOnAction(event -> {
            Member member = table.getSelectionModel().getSelectedItem();
            if (member != null) {
                int newEmployeeId = member_list.size()+1;
                Member duplicatedProduct = new Member(
                        newEmployeeId,
                        member.getName()+ "1",
                        member.getSex(),
                        member.getAddress(),
                        member.getPhone(),
                        member.getBirthday(),
                        member.getCreateDate()
                );
                member_list.add(duplicatedProduct);
                statusLabel.setText("重複: " + member.getName()+ " (尚未存檔)");
                System.out.println("重複: " + member.getName()+ " (尚未存檔)");
            }
        });

        btnDelete.setOnAction(event -> {
            Member member = table.getSelectionModel().getSelectedItem();
            if (member != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("確認刪除");
                alert.setHeaderText("您即將刪除用戶: " + member.getName());
                alert.setContentText("確定要刪除這個用戶嗎?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        boolean deleteSuccess = memberDao.deleteMember(member.getPhone());
                        if (deleteSuccess) {
                            member_list.remove(member);
                            statusLabel.setText("刪除: " + member.getName());
                            System.out.println("刪除: " + member.getName());
                        } else {
                            statusLabel.setText("刪除失敗: " + member.getName());
                            System.out.println("刪除失敗: " + member.getName());
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
    private TableView<Member> initializeProductTable() {
        TableView<Member> table = new TableView<>();
        table.setEditable(true);
        // 表格最後一欄是空白，不要顯示!
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //自動均分欄位大小
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // 定義表格欄位
        TableColumn<Member, Integer> idColumn = createColumn("ID", "id", new IntegerStringConverter());
        idColumn.setPrefWidth(60);
        TableColumn<Member, String> nameColumn = createColumn("名字", "name");
        nameColumn.setPrefWidth(100);
        TableColumn<Member, String> sexColumn = createColumn("性別", "sex", "male", "female");
        sexColumn.setPrefWidth(100);
        TableColumn<Member, String> addressColumn = createColumn("地址", "address");
        addressColumn.setPrefWidth(250);
        TableColumn<Member, String> phoneColumn = createColumn("手機號碼", "phone");
        phoneColumn.setPrefWidth(100);
        TableColumn<Member, String> birthColumn = createColumn("生日", "birthday");
        birthColumn.setPrefWidth(100);
        TableColumn<Member, String> createColumn = createColumn("創立時間", "createDate");
        createColumn.setPrefWidth(180);
        // 設定欄位允許編輯後的事件處理
        //setEditCommitHandler(idColumn, "id");
        idColumn.setEditable(false);
        createColumn.setEditable(false);
        setEditCommitHandler(nameColumn, "name");
        setEditCommitHandler(sexColumn, "sex");
        setEditCommitHandler(addressColumn, "address");
        setEditCommitHandler(phoneColumn, "phone");
        setEditCommitHandler(birthColumn, "birthday");

        //手機號檢查是否重複
        phoneColumn.setOnEditCommit(event -> {
            String newValue = event.getNewValue();
            Member currentMember = event.getRowValue();
            // 檢查是否有重複 username（排除自己）
            boolean isDuplicate = table.getItems().stream()
                    .filter(emp -> emp != currentMember)
                    .anyMatch(emp -> emp.getPhone().equals(newValue));
            if (isDuplicate) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("有重複手機號碼");
                alert.setHeaderText(null);
                alert.setContentText("會員手機號 '" + newValue + "' 已存在，請輸入其他手機號碼。");
                alert.showAndWait();
                table.refresh();
            } else {
                currentMember.setPhone(newValue);
            }
        });
        
        //--------------------
        // 11. 將所有欄位加入TableView
        //--------------------
        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(sexColumn);
        table.getColumns().add(addressColumn);
        table.getColumns().add(phoneColumn);
        table.getColumns().add(birthColumn);
        table.getColumns().add(createColumn);

        table.setItems(member_list);

        return table;
    }
    //--------------------
    // End of class
    //--------------------
}
