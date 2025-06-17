package mypos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Product;
import models.textArea;
import models.OrderTableView;
import models.ProductCategoryMenu;
import db.ProductDbManager;
import db.OrderDbManger;
import models.OrderDetail;
import models.messageBox;
import app.MainMenuApp;
import dao.MemberDAO;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import models.Member;
import models.MemberDialog;

public class AppMenu {
    static public Product SelectProduct = null;
    static public OrderDetail SelectTableProduct = null;
    static public Label showSelect;
    public String category = "CPU";
    static public ProductDbManager productManager = MainMenuApp.productManager;
    static public OrderDbManger orderManger = MainMenuApp.orderManger;
    static public CheckBox[] checkboxs = {};
    static public CheckBox allcheckbox;
    static public OrderTableView otv = new OrderTableView();
    static public int memberId = -1;
    static public textArea ta = new textArea(otv);
    public messageBox msg = new messageBox();
    public String[] CategoryText = productManager.getCategories();
    private final Map<String, ScrollPane> menus = new HashMap<>();
    ProductCategoryMenu pcm = MainMenuApp.pcm;
    //2.宣告一個容器(全域變數) menuContainerPane，裝不同種類的菜單，菜單類別選擇按鈕被按下，立即置放該種類的菜單。
    VBox menuContainerPane = new VBox();
    private final MemberDAO memberDao = new MemberDAO();
    private Member SelectMember = null;
    private Label lbmember = new Label();
    //3.多一個窗格(可以用磁磚窗格最方便)置放菜單類別選擇按鈕，置放於主視窗的最上方區域。
    public TilePane getMenuSelectionContainer() {
        
        //使用磁磚窗格，放置前面三個按鈕
        TilePane conntainerCategoryMenuBtn = new TilePane();
        //conntainerCategoryMenuBtn.setAlignment(Pos.CENTER_LEFT);
        //conntainerCategoryMenuBtn.setPrefColumns(6); //
        conntainerCategoryMenuBtn.setVgap(20);
        conntainerCategoryMenuBtn.setHgap(20);
        for(String c : CategoryText) {
            conntainerCategoryMenuBtn.getChildren().add(createButton(c));
        }
        return conntainerCategoryMenuBtn;
    } // getMenuSelectionContainer()方法
    public Button createButton(String c) {
        Button btn = new Button(c);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                allcheckbox.setSelected(true);
                for (CheckBox chk : checkboxs) {
                    chk.setSelected(true);
                }
                select_category_menu(event);
                addBtn.setText("新增一筆" + category);
                SelectProduct = null;
                showSelect.setText("");
            }
        });
        return btn;
    }
    // 前述三個類別按鈕可以呼叫以下事件，好處:當類別按鈕有很多可寫程式自動布置
    public void select_category_menu(ActionEvent event) {
        category = (((Button) event.getSource()).getText());
        updateMenuDisplay();
    }// select_category_menu()
    public Button addBtn;
    
    public void ReloadDatabase() {
        CategoryText = productManager.getCategories();
        pcm = new ProductCategoryMenu(productManager);
        for (String c : CategoryText) {
            ScrollPane menu = pcm.get(c, null);
            menus.put(c, menu);
        }
    }
    public VBox getTableContainer() {
        VBox tableContainer = new VBox();
        tableContainer.setSpacing(10);
        HBox btnPane = new HBox(10);
        btnPane.setPrefWidth(400);
        addBtn = new Button("新增一筆CPU");
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(SelectProduct != null) otv.addProduct(SelectProduct);
                ta.updateTextArea(CategoryText, memberId);
            }
        });
        Button minusBtn = new Button("刪除一筆");
        minusBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(SelectTableProduct != null) otv.minusProduct(SelectTableProduct);
                ta.updateTextArea(CategoryText, memberId);
                showSelect.setText("");
                SelectProduct = null;
            }
        });
        //show label
        showSelect = new Label("");
        showSelect.setMaxWidth(160);
        showSelect.setWrapText(false);
        //tableview
        TableView<OrderDetail> table = otv.table;
        table.setEditable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) SelectTableProduct = newSelection;
        });
        btnPane.getChildren().addAll(addBtn, minusBtn, showSelect);
        btnPane.setAlignment(Pos.CENTER_LEFT);
        btnPane.setHgrow(showSelect, Priority.ALWAYS);
        
        //Member name
        lbmember = new Label();
        lbmember.setText("一般顧客");
        //Member button
        Button memberBtn = new Button("報會員");
        memberBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("顯示會員視窗");
                MemberDialog dialog = new MemberDialog();
                Optional<MemberDialog.MemberResult> result = dialog.showAndWait();

                result.ifPresent(res -> {
                    if (res.noMembership) {
                        System.out.println("使用者選擇無會員");
                        SelectMember = null;
                        memberId = -1;
                        lbmember.setText("一般顧客");
                    } else {
                        System.out.println("輸入電話號碼：" + res.phoneNumber);
                        Member member = memberDao.getMemberByPhone(res.phoneNumber);
                        if(member == null) {
                            messageBox box = new messageBox();
                            box.showError("資料庫錯誤", "找不到會員！");
                            return;
                        }
                        memberId = member.getId();
                        SelectMember = member;
                        lbmember.setText(SelectMember.getName());
                    }
                });
                ta.updateTextArea(CategoryText, memberId);
            }
        });
        //Clear button
        Button clearBtn = new Button("清空購物車");
        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                otv.clearOrder();
                ta.updateTextArea(CategoryText, memberId);
            }
        });
        //Submit button
        Button submitBtn = new Button("送出訂單");
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(otv.getOrderDetails().size() <= 0) {
                    msg.show("送出訂單失敗", "購物車不可為空！\n請加入商品後再送出！");
                    return;
                }
                int memberId = -1;
                if(SelectMember != null) memberId = SelectMember.getId();
                Integer id = orderManger.writeOrders(otv.getOrderDetails(), otv.calculateTotalAmount(), memberId);
                otv.clearOrder();
                ta.updateTextArea(CategoryText, memberId);
                if(id == -1) {
                    msg.show("送出訂單失敗", "遇到不明錯誤，請重新送出一遍！");
                }else{
                    msg.show("送出訂單成功", "你的訂單號碼為 "+id+"\n詳細請至歷史訂單查詢");
                }
            }
        });
        HBox btnPane2 = new HBox(30);
        btnPane2.setPrefWidth(400);
        btnPane2.getChildren().addAll(lbmember, memberBtn, clearBtn, submitBtn);
        btnPane2.setAlignment(Pos.CENTER);
        btnPane2.setHgrow(showSelect, Priority.ALWAYS);
        
        tableContainer.getChildren().addAll(btnPane, table, ta.textArea, btnPane2);
        return tableContainer;
    } // getMenuSelectionContainer()方法
    public void updateMenuDisplay() {
        menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
        ScrollPane menu = pcm.get(category, null);
        menuContainerPane.getChildren().add(menu);
        updateCheckbox();
    };
    HashMap<String,List<Integer>> ShowMap = new HashMap<>() {{
        put("CPU", Arrays.asList(0,2));
        put("GPU", Arrays.asList(1,2));
        put("硬碟", Arrays.asList());
        put("電源", Arrays.asList());
    }};
    public void updateCheckbox() {
        if(ShowMap.containsKey(category)) {
            List<Integer> chks = ShowMap.get(category);
            for(int i = 0; i < checkboxs.length; i++) {
                if(chks.contains(i)) {
                    checkboxs[i].setVisible(true);
                    checkboxs[i].setManaged(true);
                }else{
                    checkboxs[i].setVisible(false);
                    checkboxs[i].setManaged(false);
                }
            }
        }
    }
    public String[] Brands = {"Intel","NVIDIA","AMD"};
    public VBox getBrandSelectionContainer() {
        // 品牌 CheckBox
        ArrayList<CheckBox> list = new ArrayList<>();
        for(String s : Brands) {
            CheckBox chk = new CheckBox(s);
            chk.setSelected(true);
            list.add(chk);
        }
        checkboxs = list.toArray(new CheckBox[0]);
        // 全選 → 控制底下品牌
        CheckBox chkAll = new CheckBox("全選");
        chkAll.setSelected(true);
        chkAll.setOnAction(e -> {
            boolean selected = chkAll.isSelected();
            for(CheckBox ch : checkboxs)
                ch.setSelected(selected);
            updateMenuDisplay();
        });
        allcheckbox = chkAll;
        // 單一品牌勾選變動 → 檢查是否要取消全選
        EventHandler<ActionEvent> brandHandler = e -> {
            boolean allselect = true;
            for(CheckBox c : checkboxs)
                allselect = allselect && c.isSelected();
            chkAll.setSelected(allselect);
            updateMenuDisplay();
        };
        HBox brandBox = new HBox(10, chkAll);
        for(CheckBox c : checkboxs) {
            c.setOnAction(brandHandler);
            brandBox.getChildren().add(c);
        }
        VBox fullContainer = new VBox(10, brandBox);
        return fullContainer;
    }
    
    public VBox getRootPane() {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10)); //外框內墊片
        root.getStylesheets().add("/css/bootstrap3.css");
        root.setStyle("-fx-background-color: #e0f7fa;");
        VBox selectMenuPane = new VBox();
        selectMenuPane.setSpacing(10);
        selectMenuPane.setPrefWidth(570);
        //搜尋框+按鈕
        TextField tfName = new TextField();
        tfName.setMaxWidth(120);
        tfName.setPrefColumnCount(30);
        Button btnSearchName = new Button();
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/search.png")));
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        btnSearchName.setGraphic(icon);
        btnSearchName.setOnAction(e -> {
            ScrollPane menu = pcm.get(category, tfName.getText());
            menuContainerPane.getChildren().clear();
            menuContainerPane.getChildren().add(menu);
        });
        HBox searchBar = new HBox(2);
        searchBar.getChildren().addAll(tfName, btnSearchName);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox();
        topBar.getChildren().addAll(getMenuSelectionContainer(), spacer, searchBar);
        //塞入菜單選擇區塊
        selectMenuPane.getChildren().add(topBar);
        selectMenuPane.getChildren().add(getBrandSelectionContainer());
       // 載入資料庫
       ReloadDatabase();
       // 塞入菜單區塊 預設為CPU
        menuContainerPane.getChildren().add(menus.get(CategoryText[0]));
        selectMenuPane.getChildren().add(menuContainerPane);
        HBox mainContainer = new HBox();//主要容器
        mainContainer.setSpacing(10);
        mainContainer.setPrefHeight(650);
        mainContainer.getChildren().addAll(getTableContainer(), selectMenuPane);
        root.getChildren().add(mainContainer);
        updateMenuDisplay();
        ta.updateTextArea(CategoryText, memberId);
        return root;
    }
}