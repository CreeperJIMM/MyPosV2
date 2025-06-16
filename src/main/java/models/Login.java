package models;


import app.MainMenuApp;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import models.Employee;

public class Login {
    private Employee user;
    private boolean IsError;
    public Employee show() {
        // 顯示登入對話框
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.setStyle("-fx-background-color: #e0f7fa;");

        // 建立使用者名稱與密碼輸入欄位
        Label userLabel = new Label("使用者:");
        TextField userField = new TextField(); // 使用者名稱輸入框
        userField.setPromptText("Username");

        Label pwdLabel = new Label("密碼:");
        PasswordField pwdField = new PasswordField(); // 密碼輸入框
        pwdField.setPromptText("Password");

        // 將元件加入 GridPane
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(pwdLabel, 0, 1);
        grid.add(pwdField, 1, 1);

        // 建立自訂 Dialog 視窗
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("登入");
        dialog.setHeaderText("請輸入使用者名稱與密碼");
        dialog.getDialogPane().setContent(grid); // 設定對話框內容為前面定義的 GridPane
        dialog.getDialogPane().getStylesheets().add("/css/bootstrap3.css");
        Node header = dialog.getDialogPane().lookup(".header-panel");
        if (header != null) header.setStyle("-fx-background-color: #c8f1fa;");
        dialog.getDialogPane().setStyle("-fx-background-color: #c8f1fa;");
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(
                ButtonType.CANCEL,
                ButtonType.OK
        );

        // 讓游標自動聚焦在使用者名稱欄位
        Platform.runLater(userField::requestFocus);

        // 設定 Dialog 結果轉換器，取得使用者輸入的帳號密碼
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new String[]{userField.getText(), pwdField.getText()}; // 返回使用者輸入的帳號與密碼
            }
            return null;
        });

        user = null;
        // 顯示對話框並處理登入驗證
        IsError = false;
        dialog.showAndWait().ifPresent(result -> {
            String usernameValue = result[0];
            String passwordValue = result[1];
            // 呼叫 DAO 進行帳號密碼驗證
            user = MainMenuApp.employeeDAO.authenticate(usernameValue, passwordValue);
            if (user == null) {
                // 驗證失敗，顯示錯誤訊息
                Alert alert = new Alert(Alert.AlertType.ERROR, "帳號或密碼錯誤！");
                alert.getDialogPane().getStylesheets().add("/css/bootstrap3.css");
                Node aleartHeader = alert.getDialogPane().lookup(".header-panel");
                if (aleartHeader != null) aleartHeader.setStyle("-fx-background-color: #e0f7fa;");
                alert.getDialogPane().setStyle("-fx-background-color: #c8f1fa;");
                alert.showAndWait();
                IsError = true;
            }
        });
        if(!IsError && user == null) {//取消登入介面
            user = new Employee();
            user.setId(-1);
        }
        return user;
    }
}
