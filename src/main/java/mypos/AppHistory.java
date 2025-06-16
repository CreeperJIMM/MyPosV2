package mypos;

import app.MainMenuApp;
import dao.MemberDAO;
import db.OrderDbManger;
import db.ProductDbManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.textArea;
import models.OrderDetail;
import models.OrderInfo;
import models.OrderTableView;
import models.messageBox;

public class AppHistory {

    private OrderDbManger dbManager = MainMenuApp.orderManger;
    private ProductDbManager pm = MainMenuApp.productManager;
    public String[] CategoryText = pm.getCategories();
    OrderTableView otv = new OrderTableView();
    private messageBox msg = new messageBox();
    private ComboBox<OrderInfo> orderSelector = new ComboBox<>();
    private MemberDAO memberDao = new MemberDAO();
    public String SelectId = "";
    public int memberId = -1;
    private textArea ta = new textArea(otv);
    
    private void updateSelect() {
        orderSelector.getItems().clear();
        orderSelector.getItems().addAll(dbManager.getAllOrder());
    }

    private void updateTable() {
        ObservableList<OrderDetail> details = dbManager.getOrderDetailsByOrderId(SelectId);
        otv.getOrderDetails().setAll(details);
        ta.updateTextArea(CategoryText, memberId);
    }

    public VBox getRootPane() {
        SelectId = "";
        updateTable();
        //stage.setX(mainStage.getX() + 300);
        //stage.setY(mainStage.getY() + 50);
        VBox root = new VBox(10);
        root.getStylesheets().add("/css/bootstrap3.css");
        root.setStyle("-fx-background-color: #e0f7fa;");
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        
        Label memberName = new Label("");
        
        orderSelector.setPromptText("請選擇訂單編號");
        orderSelector.setPrefWidth(250);
        orderSelector.setOnAction(e -> {
            OrderInfo selected = orderSelector.getValue();
            if (selected != null) {
                SelectId = selected.getOrderId();
                memberId = selected.getMemberId();
                if(memberId != -1) {
                    memberName.setText(memberDao.getMemberById(memberId).getName());
                }else{
                    memberName.setText("一般顧客");
                }
                updateTable();
            }
        });
        updateSelect();
       
        //History button
        Button clearBtn = new Button("清除全部");
        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!msg.showYesNo("刪除全部訂單確認", "你確定要刪除全部訂單嗎？\n你的訂單會全部消失！")) 
                    return;
                dbManager.resetOrders();
                otv.getOrderDetails().clear();
                ta.updateTextArea(CategoryText, memberId);
                updateSelect();
                SelectId = "";
                memberName.setText("");
            }
        });
        //Clear button
        Button deleteBtn = new Button("刪除一筆");
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(SelectId.isEmpty()) return;
                if(!msg.showYesNo("刪除訂單確認", "你確定要刪除這筆訂單嗎？\n該筆訂單就會消失！\n新訂單不會補上刪除的訂單編號！")) 
                    return;
                dbManager.deleteOrderById(SelectId);
                otv.getOrderDetails().clear();
                ta.updateTextArea(CategoryText, memberId);
                updateSelect();
                SelectId = "";
                memberName.setText("");
            }
        });
        HBox SelectPane = new HBox(5);
        SelectPane.getChildren().addAll(orderSelector, memberName);
        SelectPane.setAlignment(Pos.CENTER);
        HBox btnPane = new HBox(50);
        btnPane.setPrefWidth(300);
        btnPane.getChildren().addAll(clearBtn, deleteBtn);
        btnPane.setAlignment(Pos.CENTER);
        ta.updateTextArea(CategoryText, memberId);
        root.getChildren().addAll(SelectPane, otv.table, ta.textArea, btnPane);
        return root;
    }
}
