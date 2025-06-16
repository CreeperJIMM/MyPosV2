package models;

import java.util.ArrayList;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextArea;

public class textArea {

    public TextArea textArea;
    private OrderTableView otv;
    
    public textArea(OrderTableView otv) {
        this.otv = otv;
        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefWidth(300);
        textArea.setPrefHeight(140);//110
        textArea.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        textArea.setText("");
    }

    public void updateTextArea(String[] CategoryText, int memberId) {
        ArrayList<String> txt = new ArrayList<>();
        for (String ct : CategoryText) {
            String t = String.format("%d", otv.calculateTotalAmount(ct));
            txt.add(String.format("%s總計：%" + (25 - t.length() - 1) + "s$", ct, t));
        }
        txt.add("------------------------------");
        int total_amount = otv.calculateTotalAmount();
        int discount = (int)Math.round(otv.calculateTotalAmount() * 0.9);
        if(memberId != -1) {
            String t = String.format("%d", (total_amount - discount) * -1);
            txt.add(String.format( "會員折扣：%" + (25 - t.length() - 1) + "s$", t));
        }else{
            txt.add("");
        }
        String total = String.format("%d", total_amount);
        if(memberId != -1) total = String.format("%d", discount);
        txt.add(String.format("總金額：%" + (25 - total.length() - 1) + "s$", total));
        textArea.setText(String.join("\n", txt));
    }
}
