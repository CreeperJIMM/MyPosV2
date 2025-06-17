package models;

import app.MainMenuApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.TextAlignment;
import javafx.util.converter.IntegerStringConverter;
import mypos.AppMenu;


public class OrderTableView {

    public TableView<OrderDetail> table;
    private ObservableList<OrderDetail> orderDetails;
    private TableColumn<OrderDetail, Integer> quantityCol;
    public String[] CategoryText = MainMenuApp.productManager.getCategories();

    public OrderTableView() {
        orderDetails = FXCollections.observableArrayList();
        table = new TableView<>(orderDetails);
        table.setPrefWidth(300);

        TableColumn<OrderDetail, String> nameCol = new TableColumn<>("商品");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(5.0 / 7.0)); // 5:1:1比例

        TableColumn<OrderDetail, Integer> priceCol = new TableColumn<>("價格");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("product_price"));
        priceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.95 / 7.0));
        quantityCol = new TableColumn<>("數量");
        quantityCol.setEditable(true);
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.prefWidthProperty().bind(table.widthProperty().multiply(0.955 / 7.0));

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // 定義數量欄位編輯完成後的處理邏輯
        quantityCol.setOnEditCommit(event -> {
            int row_num = event.getTablePosition().getRow();  // 取得被修改的行號
            int new_val = event.getNewValue();  // 取得用戶輸入的新數量
            OrderDetail target = event.getTableView().getItems().get(row_num);  // 取得對應的訂單項目
            if (new_val <= 0) {
                table.getItems().remove(target);
            }
            target.setQuantity(new_val);  // 更新數量
            calculateTotalAmount();  // 重新計算總金額
            AppMenu.ta.updateTextArea(CategoryText, AppMenu.memberId);
            //System.out.println("被修改數量的產品:" + orderDetails.get(row_num).getProduct_name());
            //System.out.println("數量被修改為:" + orderDetails.get(row_num).getQuantity());
        });
        // 價格、數量靠右
        //setRightAlignedCell(priceCol);
        //setRightAlignedCell(quantityCol);

        table.getColumns().addAll(nameCol, priceCol, quantityCol);

        // 關閉水平滾動條
        table.setFixedCellSize(25); // 每列固定高度，避免抖動
        table.setPrefHeight(325);   // 你可以自己調 PrefHeight
        table.setMaxHeight(410);
    }   

    private <T> void setRightAlignedCell(TableColumn<OrderDetail, T> column) {
        column.setCellFactory(col -> {
            return new TableCell<OrderDetail, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                        setAlignment(Pos.CENTER_RIGHT);
                        setTextAlignment(TextAlignment.RIGHT);
                    }
                }
            };
        });
    }

    public void addProduct(Product p) {
        for (OrderDetail detail : orderDetails) {
            if (detail.getProduct_id().equals(p.getProduct_id())) {
                detail.setQuantity(detail.getQuantity() + 1);
                table.refresh();
                updateMultiply(orderDetails.size());
                return;
            }
        }
        OrderDetail newDetail = new OrderDetail(p);
        newDetail.setQuantity(1);
        orderDetails.add(newDetail);
        table.refresh();
        updateMultiply(orderDetails.size());
        return;
    }

    public void minusProduct(OrderDetail od) {
        for (OrderDetail detail : orderDetails) {
            if (detail.getProduct_id().equals(od.getProduct_id())) {
                detail.setQuantity(detail.getQuantity() - 1);
                if (detail.getQuantity() <= 0) {
                    orderDetails.remove(detail);
                }
                break;
            }
        }
        table.refresh();
        updateMultiply(orderDetails.size() + 1);
    }
    
    public void updateMultiply(int n) {
        if (n >= 12) {
            quantityCol.prefWidthProperty().bind(table.widthProperty().multiply(0.75 / 7.0));
        }
        else {
            quantityCol.prefWidthProperty().bind(table.widthProperty().multiply(0.955 / 7.0));
        }
    }

    public int calculateTotalAmount() {
        int total = 0;
        for (OrderDetail od : orderDetails) {
            total += od.getProduct_price() * od.getQuantity();
        }
        return total;
    }

    public int calculateTotalAmount(String category) {
        int total = 0;
        for (OrderDetail od : orderDetails) {
            if (category.equals(od.getProduct_category())) {
                total += od.getProduct_price() * od.getQuantity();
            }
        }
        return total;
    }

    public ObservableList<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void clearOrder() {
        orderDetails.clear();
        updateMultiply(orderDetails.size());
    }
}
