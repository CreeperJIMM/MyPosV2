package models;

import app.MainMenuApp;
import java.util.TreeMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import mypos.AppMenu;
import models.Product;
import db.ProductDbManager;
import java.util.HashMap;
import javafx.scene.control.ScrollPane;

public class ProductCategoryMenu {
    public long lastClickTime = 0;
    HashMap<String, Image> ImgCache = MainMenuApp.ImgCache;
    TreeMap<String, Product> product_dict = null;
    ProductDbManager productManager;
    public String[] CategoryText;
    
    public ProductCategoryMenu(ProductDbManager productManager) {
        this.productManager = productManager;
        CategoryText = productManager.getCategories();
    }
    
    public javafx.scene.control.ScrollPane get(String category, String search) {
        //取得產品清單(呼叫靜態方法取得)
        if (product_dict == null) product_dict = productManager.getProducts();
        //磁磚窗格
        TilePane category_menu = new TilePane();
        category_menu.setStyle("-fx-background-color: #D2E9FF;");
        category_menu.setVgap(10);
        category_menu.setHgap(10);
        //設定一個 row有4個columns，放不下就放到下一個row
        category_menu.setPrefColumns(4);
        //將產品清單內容一一置放入產品菜單磁磚窗格
        for (String item_id : product_dict.keySet()) {
            //用if選擇產品類別
            if (product_dict.get(item_id).getCategory().equals(category)) {
                if(search != null && !product_dict.get(item_id).getName().toLowerCase().contains(search.toLowerCase())) {
                    continue;
                }
                String brand = product_dict.get(item_id).getBrand();
                if (!isShowItem(brand)) {
                    continue;
                }
                // 建立一個 VBox 作為容器，垂直排列圖片與文字
                VBox box = new VBox();
                box.setAlignment(Pos.CENTER); // 置中對齊
                box.setSpacing(5); // 圖片與文字的間距
                box.setPrefSize(120, 140); // 可自行調整整個磁磚的大小
                //定義新增一筆按鈕
                Button btn = new Button();
                btn.setPrefSize(120, 120);
                //按鈕元件顯示圖片Creating a graphic (image)
                //讀出圖片
                //System.out.println(product_dict.get(item_id).getPhoto());
                String imgurl = product_dict.get(item_id).getImage();
                Image img;
                if (ImgCache.containsKey(imgurl)) {
                    img = ImgCache.get(imgurl);
                } else {
                    img = new Image("/imgs/" + product_dict.get(item_id).getImage());
                    ImgCache.put(imgurl, img);
                }
                ImageView imgview = new ImageView(img);//圖片顯示物件
                imgview.setFitHeight(80); //設定圖片高度，你要自行調整，讓它美觀
                imgview.setPreserveRatio(true); //圖片的寬高比維持

                //Setting a graphic to the button
                btn.setGraphic(imgview); //按鈕元件顯示圖片
                //新建標籤
                Label label = new Label();
                String txt = String.format("%s %d＄", product_dict.get(item_id).getName(), product_dict.get(item_id).getPrice());
                label.setText(txt);
                label.setWrapText(true); // 文字過長可換行
                label.setTextAlignment(TextAlignment.CENTER);
                label.setMinHeight(32); // 限制兩行高度
                label.setPrefHeight(32);
                label.setMaxHeight(32);

                // 建立 Tooltip，內容為商品描述
                Tooltip tooltip = new Tooltip(product_dict.get(item_id).getDescription());
                tooltip.setWrapText(true);         // 讓長描述可自動換行
                tooltip.setMaxWidth(200);         // 限制最大寬度（避免過長）
                tooltip.setShowDelay(Duration.millis(100)); // 鼠標懸停多久後顯示
                // 套用到Label上
                Tooltip.install(label, tooltip);

                box.getChildren().addAll(btn, label);
                category_menu.getChildren().add(box);  //放入菜單磁磚窗格

                //定義按鈕事件-->點選一次，就加入購物車，再點選一次，數量要+1
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //新增一筆訂單到order_list (ArrayList)
                        //addToCart(item_id);
                        //order_list.add(new Order("p-109", "新增的果汁", 30, 1));
                        AppMenu.SelectProduct = product_dict.get(item_id);
                        String txt = String.format("已選擇 %s", AppMenu.SelectProduct.getName());
                        AppMenu.showSelect.setText(txt);
                        //雙擊自動加入購物車
                        long now = System.currentTimeMillis();
                        long mi = now - lastClickTime;
                        if (now - lastClickTime < 300) {
                            lastClickTime = 0;
                            if (AppMenu.SelectProduct != null) {
                                AppMenu.otv.addProduct(AppMenu.SelectProduct);
                            }
                            AppMenu.ta.updateTextArea(CategoryText, AppMenu.memberId);
                        } else {
                            lastClickTime = now;
                        }
                    }
                });
            }
        }
        category_menu.setMaxWidth(511);
        VBox wrapper = new VBox(category_menu);
        wrapper.setStyle("-fx-background-color: #D2E9FF;");
        wrapper.setPadding(new Insets(5, 5, 5, 10));
        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(430);
        return scrollPane;
    }//getProductCategoryMenu()
    
        public boolean isShowItem(String brand) {
        if(AppMenu.checkboxs.length <= 0) return true;
        boolean flag = true;
        switch(brand.strip()) {
            case "Intel":
                flag = AppMenu.checkboxs[0].isSelected();
                break;
            case "NVIDIA":
                flag = AppMenu.checkboxs[1].isSelected();
                break;
            case "AMD":
                flag = AppMenu.checkboxs[2].isSelected();
                break;
            default:
                flag = AppMenu.allcheckbox.isSelected();
                break;
        }
        return flag;
    }
}
