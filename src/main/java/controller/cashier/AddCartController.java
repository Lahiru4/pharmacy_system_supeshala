package controller.cashier;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import tdm.BillTable2;
import tdm.ItemsTM;

public class AddCartController {
    public Label itemsName;
    public ItemsTM items;
    public CashierController cashierController;

    public TextField price;
    public ImageView detete;

    public void setItems(ItemsTM items) {
        this.items = items;
        if (items == null) {
            return;
        }
        itemsName.setText(items.getDescription());
        qty.setText(items.getQTY() + "");
        price.setText(items.getSelling_price() + "");
    }

    public TextField entQty;
    public Label qty;


    public void initialize() {
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
        if (items != null) {
            ObservableList<BillTable2> items1 = cashierController.billTable.getItems();
            for (BillTable2 temp : items1) {
                if (temp.getItemCode().equals(items.getItemCode())) {
                    int entQTY = Integer.parseInt(entQty.getText());
                    if (items.getQTY() >= entQTY) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            for (int i = 0; i < cashierController.showTable.getItems().size(); i++) {
                if (cashierController.showTable.getItems().get(i).getItemCode().equals(selectedItem.getItemCode())) {
                    int entQTY = Integer.parseInt(entQty.getText());
                    if (cashierController.showTable.getItems().get(i).getQTY() >= entQTY) {
                        return true;
                    } else {
                        return false;
                    }
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
        this.selectedItem = selectedItem;
        detete.setVisible(true);
        for (int i = 0; i < cashierController.showTable.getItems().size(); i++) {
            if (cashierController.showTable.getItems().get(i).getItemCode().equals(selectedItem.getItemCode())) {
                items=cashierController.showTable.getItems().get(i);
                itemsName.setText(items.getDescription() + "");
                qty.setText(items.getQTY() + "");
                price.setText(items.getSelling_price() + "");
                return;
            }
        }
    }

    private BillTable2 selectedItem;

    public void deleteOnAction(MouseEvent mouseEvent) {
        cashierController.billTableDelete(selectedItem);
        detete.getScene().getWindow().hide();
    }
}
