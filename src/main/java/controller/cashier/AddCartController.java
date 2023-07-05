package controller.cashier;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import tdm.BillItemTM;
import tdm.BillTable2;
import tdm.ItemsTM;

public class AddCartController {
    public Label itemsName;
    private static ItemsTM items;
    public CashierController cashierController;
    public TextField price;
    public ImageView detete;

    public static void setItems(ItemsTM items) {
        AddCartController.items = items;
    }

    public TextField entQty;
    public Label qty;


    public void initialize() {
        if (items == null) {
            return;
        }
        itemsName.setText(items.getDescription());
        qty.setText(items.getQTY() + "");
        price.setText(items.getSelling_price()+"");

    }

    public void addtobtnOnAction(ActionEvent actionEvent) {
        if (qtyCheck() && entQty.getText() != "" && qty.getText().matches("^\\d+$") && Integer.parseInt(entQty.getText()) > 0 && Integer.parseInt(entQty.getText()) <= Integer.parseInt(qty.getText())) {
            items.setSelling_price(Double.parseDouble(price.getText()));
            cashierController.setBillTableItems(items, Integer.parseInt(entQty.getText()));
            entQty.getScene().getWindow().hide();
            cashierController.searchTexfeld.requestFocus();
            cashierController.searchTexfeld.clear();
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid QTY Input").showAndWait();
            entQty.requestFocus();
        }
    }

    private boolean qtyCheck() {
        ObservableList<BillTable2> items1 = cashierController.billTable.getItems();
        for (BillTable2 temp : items1) {
            if (temp.getItemCode().equals(items.getItemCode())) {
                int qty1 = temp.getQty();
                int entQTY = Integer.parseInt(entQty.getText());
                int tot = qty1 + entQTY;
                if (items.getQTY() > tot) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public void addToCartOnAction(ActionEvent actionEvent) {
        if (qtyCheck() && entQty.getText() != "" && qty.getText().matches("^\\d+$") && Integer.parseInt(entQty.getText()) > 0 && Integer.parseInt(entQty.getText()) <= Integer.parseInt(qty.getText())) {
            items.setSelling_price(Double.parseDouble(price.getText()));
            cashierController.setBillTableItems(items, Integer.parseInt(entQty.getText()));
            entQty.getScene().getWindow().hide();
            cashierController.searchTexfeld.requestFocus();
            cashierController.searchTexfeld.clear();
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid QTY Input").showAndWait();
            entQty.requestFocus();
        }
    }

    public void goToQty(ActionEvent actionEvent) {
        entQty.requestFocus();
    }

    public void setDataAndDeleteOnSetAction(BillTable2 selectedItem) {
        this.selectedItem=selectedItem;
        entQty.setText(selectedItem.getQty()+"");
        price.setText(selectedItem.getSelling_price()+"");
        detete.setVisible(true);
    }
    private BillTable2 selectedItem;

    public void deleteOnAction(MouseEvent mouseEvent) {
        cashierController.billTableDelete(selectedItem);
        detete.getScene().getWindow().hide();
    }
}
