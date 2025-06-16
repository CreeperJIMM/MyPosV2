package models;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MemberDialog extends Dialog<MemberDialog.MemberResult> {

    public static class MemberResult {
        public final String phoneNumber;
        public final boolean noMembership;

        public MemberResult(String phoneNumber, boolean noMembership) {
            this.phoneNumber = phoneNumber;
            this.noMembership = noMembership;
        }
    }

    public MemberDialog() {
        setTitle("會員查詢");
        setHeaderText("請輸入會員電話，或選擇無會員");
        getDialogPane().getStylesheets().add("/css/bootstrap3.css");
        Node header = getDialogPane().lookup(".header-panel");
        if (header != null) header.setStyle("-fx-background-color: #c8f1fa;");
        getDialogPane().setStyle("-fx-background-color: #e0f7fa;");
        // 建立 UI 元件
        Label phoneLabel = new Label("電話號碼：");
        TextField phoneField = new TextField();
        phoneField.setPromptText("例如 0912345678");
        Button noMemberButton = new Button("顧客沒有會員");

        // 佈局
        VBox upperPane = new VBox(10, phoneLabel, phoneField);
        upperPane.setPadding(new Insets(10));
        upperPane.setStyle("-fx-background-color: #e0f7fa;");

        HBox lowerPane = new HBox(noMemberButton);
        lowerPane.setPadding(new Insets(10));
        lowerPane.setSpacing(10);

        VBox content = new VBox(10, upperPane, lowerPane);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 預設 OK 按鈕行為
        Node okButton = getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        okButton.getStyleClass().setAll("button", "success");

        // 僅允許輸入數字、長度最多 10
        phoneField.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,10}")) {
                return change;
            }
            return null;
        }));
        // 啟用 OK 按鈕條件
        phoneField.textProperty().addListener((obs, oldText, newText) -> {
            boolean valid = newText.matches("09\\d{8}");
            okButton.setDisable(!valid);
        });

        // 無會員按鈕邏輯：直接關閉 Dialog 並回傳特殊結果
        noMemberButton.setOnAction(e -> {
            setResult(new MemberResult(null, true));
            close();
        });
        noMemberButton.getStyleClass().setAll("button", "info");

        Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.getStyleClass().add("danger");
        
        // OK 按鈕邏輯：回傳輸入電話號碼
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new MemberResult(phoneField.getText().trim(), false);
            }
            return null;
        });
    }
}
