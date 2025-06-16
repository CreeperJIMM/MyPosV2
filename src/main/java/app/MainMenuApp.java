package app;

// ====== 匯入相關套件 ======
// JavaFX UI 元件與佈局
import dao.EmployeeDAO;
import db.MemberDbManager;
import db.OrderDbManger;
import db.ProductDbManager;
import java.util.HashMap;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Employee;
import models.Login;
import models.ProductCategoryMenu;
import mypos.AppMenu;
import mypos.AppHistory;
import mypos.AppDailyReport;
import mypos.AppEmployeeEdit;
import mypos.AppMemberEdit;
import mypos.AppProductEdit;

// ====== 主程式類別 ======
public class MainMenuApp extends Application {
    // ====== 欄位宣告區（全域變數） ======
    private BorderPane root; // 主畫面容器
    private VBox menuBox; // 左側功能選單
    private StackPane contentPane; // 主內容顯示區
    private ToggleButton toggleMenuBtn; // 左上角 menu 摺疊按鈕

    // 登入狀態與功能按鈕陣列
    private boolean loggedIn = false; // 是否已登入
    private Button[] functionButtons; // 所有功能按鈕
    private Button loginLogoutBtn; // 登入/登出按鈕

    // 登入/登出圖示
    private Image loginImage;   // 需要設為全域變數（類別欄位），因為登入/登出圖示會在多個方法中使用
    private Image logoutImage;  // 例如：initLoginLogoutBtn() 和 updateLoginState() 都會用到

    // 資料存取物件與目前登入使用者
    public static final EmployeeDAO employeeDAO = new EmployeeDAO();
    public static Employee currentUser = null;

    // 顯示登入者姓名的標籤
    private final Label userInfoLabel = new Label("");

    //資料庫內容
    static public ProductDbManager productManager = new ProductDbManager();
    static public OrderDbManger orderManger = new OrderDbManger();
    static public MemberDbManager memberManger = new MemberDbManager();
    static public ProductCategoryMenu pcm = new ProductCategoryMenu(productManager);
    static public HashMap<String, Image> ImgCache = new HashMap<>();
    
    // ====== JavaFX 應用程式進入點 ======
    @Override
    public void start(Stage primaryStage) {
        // 1. 初始化主容器與佈局
        root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #c8f1fa;");

        // 2. 初始化各個 UI 元件
        initMenuBox();         // 左側功能選單
        initContentPane();     // 主內容顯示區
        initToggleMenuBtn();   // 左上角 menu 摺疊按鈕
        initLoginLogoutBtn();  // 登入/登出按鈕
        initTitleBar();        // 標題列（含系統名稱與登入資訊）

        // 3. 預設顯示首頁
        setContent(createWelcomePane());

        // 4. 預設所有功能按鈕禁用（未登入時）
        updateLoginState();

        // 5. 設定 Scene 與顯示視窗
        Scene scene = new Scene(root, 1170, 650);
        scene.getStylesheets().add(getClass().getResource("/css/bootstrap3.css").toExternalForm());
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.setTitle("電腦零件訂購系統");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ====== UI 元件初始化方法 ======

    // 初始化左側功能選單
    private void initMenuBox() {
        menuBox = new VBox(15);
        menuBox.setPadding(new Insets(10));
        menuBox.setAlignment(Pos.TOP_CENTER);
        menuBox.setStyle("-fx-background-color: #c8f1fa;");

        // 建立每個功能按鈕，並設定圖示
        Button orderBtn = createMenuButton("訂購系統", "/images/order.png");
        Button memberBtn = createMenuButton("會員管理", "/images/member.png");
        Button historyBtn = createMenuButton("訂單管理", "/images/history.png");
        Button productBtn = createMenuButton("產品管理", "/images/product.png");
        Button userBtn = createMenuButton("用戶管理", "/images/user.png");
        Button reportBtn = createMenuButton("銷售報表", "/images/bar_chart.png");
        //Button analysisBtn = createMenuButton("數據分析", "/images/analysis.png");

        // 將所有功能按鈕放入陣列，方便統一管理啟用/禁用狀態
        functionButtons = new Button[] { orderBtn, memberBtn, historyBtn, productBtn, userBtn, reportBtn };

        // 設定按鈕樣式
        orderBtn.getStyleClass().setAll("button", "info");
        memberBtn.getStyleClass().setAll("button", "info");
        historyBtn.getStyleClass().setAll("button", "info");
        productBtn.getStyleClass().setAll("button", "info");
        userBtn.getStyleClass().setAll("button", "info");
        reportBtn.getStyleClass().setAll("button", "info");

        // 設定每個按鈕的點擊事件
        orderBtn.setOnAction(e -> {
            HBox loadPane = new HBox(10);
            Label loding = new Label("點餐系統載入中...");
            loding.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            Image loadingImage = new Image(getClass().getResourceAsStream("/images/loading.gif"));
            ImageView loadingIcon = new ImageView(loadingImage);
            loadPane.setAlignment(Pos.CENTER);
            loadPane.getChildren().addAll(loding, loadingIcon);
            setContent(loadPane);
            
            // 開始背景載入
            Task<Pane> loadOrderPaneTask = new Task<>() {
                @Override
                protected Pane call() throws Exception {
                    // 在背景執行 createOrderPane，避免 UI 卡住
                    return createOrderPane();
                }
            };
            loadOrderPaneTask.setOnSucceeded(ev -> {
                Pane orderPane = loadOrderPaneTask.getValue();
                setContent(orderPane); // 替換為真正內容
            });
            new Thread(loadOrderPaneTask).start();
        });
        historyBtn.setOnAction(e -> setContent(OrderDetailPane()));
        productBtn.setOnAction(e -> setContent(createProductPane()));
        memberBtn.setOnAction(e -> setContent(memberPane()));
        userBtn.setOnAction(e -> setContent(createUserPane()));
        reportBtn.setOnAction(e -> setContent(createReportPane()));

        // 將所有功能按鈕加入 menuBox
        menuBox.getChildren().addAll(orderBtn, memberBtn, historyBtn, productBtn, userBtn, reportBtn);
        root.setLeft(menuBox);
    }

    // 初始化主內容顯示區
    private void initContentPane() {
        contentPane = new StackPane();
        contentPane.setPadding(new Insets(20));
        contentPane.setStyle("-fx-background-color: #e0f7fa; -fx-border-color: #ddd;");
        contentPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.setCenter(contentPane);
    }

    // 初始化左上角 menu 摺疊按鈕
    private void initToggleMenuBtn() {
        toggleMenuBtn = new ToggleButton("≡");
        toggleMenuBtn.setStyle("-fx-font-size: 18px; -fx-background-radius: 5;");
        toggleMenuBtn.setFocusTraversable(false);
        toggleMenuBtn.setSelected(true);
        toggleMenuBtn.getStyleClass().addAll("toggle-button", "info"); // 使用 Bootstrap3 風格
        // 監聽 toggleMenuBtn 的選取狀態，切換 menuBox 顯示/隱藏
        // 監聽 toggleMenuBtn（≡ 按鈕）的選取狀態
        // 當 toggleMenuBtn 被點擊（選取/取消選取）時，會觸發此監聽器
        // isSelected 為 true 時，顯示左側 menuBox；為 false 時，隱藏 menuBox
        toggleMenuBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                root.setLeft(menuBox); // 顯示左側功能選單
            } else {
                root.setLeft(null); // 隱藏左側功能選單
            }
        });
    }

    // 初始化登入/登出按鈕
    private void initLoginLogoutBtn() {
        loginImage = new Image(getClass().getResourceAsStream("/images/login.png"), 20, 20, true, true);
        logoutImage = new Image(getClass().getResourceAsStream("/images/logout.png"), 20, 20, true, true);

        loginLogoutBtn = new Button("登入", new ImageView(loginImage));
        loginLogoutBtn.setStyle("-fx-font-size: 14px;");
        loginLogoutBtn.getStyleClass().setAll("button", "warning");

        userInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #337ab7; -fx-padding: 0 10 0 10;");

        // 設定登入/登出按鈕的事件處理
        // 可以直接呼叫 handleLoginLogout() 方法來統一處理登入與登出流程
        loginLogoutBtn.setOnAction(e -> handleLoginLogout());
    }

    // 初始化標題列（包含系統名稱與登入資訊）
    private void initTitleBar() {
        Label titleLabel = new Label("電腦零件訂購系統");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        HBox titleBox = new HBox(10, toggleMenuBtn, titleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(5, 0, 5, 15));
        HBox rightBox = new HBox(userInfoLabel, loginLogoutBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setSpacing(5);
        HBox titleBar = new HBox(titleBox, rightBox);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        titleBar.setSpacing(20);

        root.setTop(titleBar);
    }

    // ====== 狀態更新與輔助方法 ======
    // 處理登入與登出邏輯的方法
    // 如果目前尚未登入，則顯示登入對話框，讓使用者輸入帳號密碼
    // 如果已經登入，則執行登出，並回到歡迎頁面
    private void handleLoginLogout() {
        if (!loggedIn) {
            Login loginFunc = new Login();
           
            Employee user = null;
            while (user == null) {                
                user = loginFunc.show();
            }
            if(user != null && user.getId() != -1) {
                loggedIn = true;
                currentUser = user;
                updateLoginState();
            }
        } else {
            // 登出流程：清除登入狀態與目前使用者，並回到歡迎頁面
            loggedIn = false;
            currentUser = null;
            updateLoginState();
            setContent(createWelcomePane());
        }
    }
    
    // 根據登入狀態啟用/禁用功能按鈕與切換登入/登出按鈕文字與圖示
    private void updateLoginState() {
        for (Button btn : functionButtons) {
            btn.setDisable(true);
        }

        if (loggedIn && currentUser != null) {
            String role = currentUser.getRole();
            for (int i = 0; i < functionButtons.length; i++) {
                Button btn = functionButtons[i];
                switch (role) {
                    case "data":
                        if(i == 5) btn.setDisable(false);
                        break;
                    case "order":
                        if(i == 0) btn.setDisable(false);
                        break;
                    case "inventory":
                        if(i == 3) btn.setDisable(false);
                        break;
                    case "support":
                        if(i == 1) btn.setDisable(false);
                        else if(i == 2) btn.setDisable(false);
                        break;
                    default:
                        btn.setDisable(false);
                        break;
                }
            }
            loginLogoutBtn.setText("登出");
            loginLogoutBtn.setGraphic(new ImageView(logoutImage));
            // 顯示登入者姓名
            String txt = currentUser.getUsername();
            switch (currentUser.getRole()) {
                case "admin":
                    txt += "    管理員";
                    break;
                case "data":
                    txt += " 資料分析師";
                    break;
                case "order":
                    txt += "    收銀員";
                    break;
                case "inventory":
                    txt += " 庫存管理員";
                    break;
                case "support":
                    txt += "  客服人員";
                    break;
            }
            userInfoLabel.setText(txt);
        } else {
            loginLogoutBtn.setText("登入");
            loginLogoutBtn.setGraphic(new ImageView(loginImage));
            userInfoLabel.setText("");
        }
    }

    // ====== 建立各功能頁面的方法 ======
    // 建立帶有圖示的功能按鈕
    private Button createMenuButton(String text, String iconPath) {
        Button btn = new Button(text);
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        btn.setGraphic(icon);
        btn.setPrefWidth(140);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setStyle("-fx-font-size: 12px;");
        btn.setAlignment(Pos.CENTER_LEFT); // 讓內容靠左
        btn.setGraphicTextGap(5); // 圖示與文字間距
        return btn;
    }

    // 將傳入的 pane 設為主內容區的唯一內容
    private void setContent(Pane pane) {
        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // 讓內容自動撐滿
        contentPane.getChildren().setAll(pane);
    }

    // ====== 各功能頁面建立方法 ======

    // 歡迎頁面
    private Pane createWelcomePane() {
        Label label = new Label("歡迎使用電腦訂購系統");
        label.setStyle("-fx-font-size: 18px;");
        VBox box = new VBox(label);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // 訂單輸入頁面
    private Pane createOrderPane() {
        System.out.println("建立訂單輸入頁面");
        AppMenu entryApp = new AppMenu();
        return entryApp.getRootPane();
    }
    
    // 會員管理頁面
    private Pane memberPane() {
        System.out.println("建立會員管理頁面");
        AppMemberEdit memberApp = new AppMemberEdit();
        return memberApp.getRootPane();
    }

    // 訂單管理頁面
    private Pane OrderDetailPane() {
        System.out.println("建立訂單管理頁面");
        AppHistory historyApp = new AppHistory();
        return historyApp.getRootPane();
    }
    
    // 產品維護管理頁面
    private Pane createProductPane() {
        System.out.println("建立產品維護管理頁面");
        AppProductEdit maintenanceApp = new AppProductEdit();
        return maintenanceApp.getRootPane();
    }

    // 銷售報表頁面
    private Pane createReportPane() {
        System.out.println("建立銷售報表頁面");
        AppDailyReport reportApp = new AppDailyReport();
        return reportApp.getRootPane();
    }

    // 使用者管理頁面
    private Pane createUserPane() {
        System.err.println("建立用戶管理頁面");
        AppEmployeeEdit empolyeeApp = new AppEmployeeEdit();
        return empolyeeApp.getRootPane();
    }

    // ====== 主程式進入點 ======
    public static void main(String[] args) {
        launch(args);
    }
}
