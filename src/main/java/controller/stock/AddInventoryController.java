package controller.stock;

import bo.SaveStockDO;
import bo.SaveStockDOImpl;
import com.jfoenix.controls.JFXButton;
import controller.cashier.CashierController;
import dao.custom.ItemsDAO;
import dao.custom.StockDAO;
import dao.custom.SupplyerDAO;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.StockDAOImpl;
import dao.custom.impl.SupplyerDAOImpl;
import dto.ItemsDTO;
import dto.StockDTO;
import dto.SupplierDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import tdm.ItemsTM;
import tdm.StockTM;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class AddInventoryController {
    public TextField itemCode;
    public TextField description;
    public TextField qty;


    public TextField sellingprice;
    public TextField purchaseprice;
    public StockController stockController;
    public JFXButton save;
    public DatePicker expiration;
    public TableView<ItemsDTO> lists;

    public TableColumn name;

    public TableColumn qtyC;
    public TableColumn sellP;
    public TableColumn purchP;
    public TableColumn exDate;
    public TableColumn action;
    public SupplyerDAO supplyerDAO = new SupplyerDAOImpl();
    public Label stock_idLbl;
    public TableColumn stock_id;
    public JFXButton addBtn;
    public ItemsController item_con;
    public Label packLbl;
    public TextField pack;
    public CashierController cashierController;
    @FXML
    private ComboBox<String> importer;

    @FXML
    private ComboBox<String> areaDistributor;

    @FXML
    private ComboBox<String> sales_Represent;
    @FXML
    private TableColumn<?, ?> item_code;

    @FXML
    private ComboBox<String> agency;
    private StockDAO stockDAO = new StockDAOImpl();
    public ItemsDAO itemsDAO = new ItemsDAOImpl();


    public void initialize() {
        //set Stock id
        setStockId();
        name.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        stock_id.setCellValueFactory(new PropertyValueFactory<>("stockID"));
        qtyC.setCellValueFactory(new PropertyValueFactory<>("qty"));
        sellP.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        purchP.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        exDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        item_code.setCellValueFactory(new PropertyValueFactory<>("itemCode"));


        setItemCode();
        setSupComData();
    }

    private void setStockId() {
        try {
            stock_idLbl.setText(stockDAO.generateNewID());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setItemCode() {
        try {
            String newID = "";
            if (lists.getItems().size() > 0) {
                newID = itemsDAO.generateNewID(lists.getItems().size());
            } else {
                newID = itemsDAO.generateNewID();
            }
            itemCode.setText(newID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setSupComData() {
        try {
            for (SupplierDTO importer : supplyerDAO.yetPositionGetSup("Importer")) {
                this.importer.getItems().add(importer.getName());
            }

            for (SupplierDTO area_distributor : supplyerDAO.yetPositionGetSup("Area Distributor")) {
                this.areaDistributor.getItems().add(area_distributor.getName());

            }

            for (SupplierDTO salesRepresent : supplyerDAO.yetPositionGetSup("Sales Represent")) {
                this.sales_Represent.getItems().add(salesRepresent.getName());

            }

            for (SupplierDTO agency : supplyerDAO.yetPositionGetSup("Agency")) {
                this.agency.getItems().add(agency.getName());

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean checkSave;

    public void saveOnAction(ActionEvent actionEvent) {
        if (!checkSave) {
            if (save.getText().equals("Save")) {
                SaveStockDO saveStockDO = new SaveStockDOImpl();
                ObservableList<ItemsDTO> items = lists.getItems();
                try {
                    boolean save1 = saveStockDO.save(getStock(), items);
                    if (save1) {
                        ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                        Notifications.create()
                                .graphic(imageView)
                                .text(" Save Successful ")
                                .title("Successful")
                                .hideAfter(Duration.seconds(5))
                                .position(Pos.TOP_RIGHT)
                                .darkStyle()
                                .show();
                        checkSave = false;
                        if (stockController!=null) {
                            stockController.lodeTableData();
                        }
                        if (cashierController!=null) {
                            cashierController.showTable.getItems().remove(cashierController.showTable.getItems());
                            cashierController.showTable.refresh();
                            cashierController.lodeTableData();
                            cashierController.searchTexfeld.requestFocus();
                            addBtn.getScene().getWindow().hide();
                            cashierController.setSearchFilter();
                        }
                        lists.getItems().removeAll(lists.getItems());
                        setStockId();
                        setItemCode();

                        //lists.getScene().getWindow().hide();
                    } else {
                        Notifications.create()
                                .graphic(new ImageView(new Image("/view/assests/images/icons8-fail-48.png")))
                                .text("Save Fail ")
                                .title("Fail")
                                .hideAfter(Duration.seconds(5))
                                .position(Pos.TOP_RIGHT)
                                .show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private StockDTO getStock() {
        int itemsQty = lists.getItems().size();
        int tot = 0;
        double p_tot = 0;
        for (ItemsDTO item : lists.getItems()) {
            tot += item.getQty();
            p_tot += item.getPurchasePrice();
        }
        return new StockDTO(stock_idLbl.getText(), importer.getSelectionModel().getSelectedItem()
                , areaDistributor.getSelectionModel().getSelectedItem(), sales_Represent
                .getSelectionModel().getSelectedItem(), agency.getSelectionModel().getSelectedItem(),
                itemsQty, p_tot, LocalDate.now() + "");
    }

    public void addOnAction(ActionEvent actionEvent) {
        if (validate()) {
            ItemsDTO itemsDTO = collectData();
            if (addBtn.getText().equals("Add")) {
                ItemsDTO itemsDTO2 = collectData2();
                lists.getItems().add(itemsDTO2);
                clearFields();
                setItemCode();
            } else {
                try {
                    boolean update = itemsDAO.update(itemsDTO);
                    if (update) {
                        ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                        Notifications.create()
                                .graphic(imageView)
                                .text(" Update Successful ")
                                .title("Successful")
                                .hideAfter(Duration.seconds(5))
                                .position(Pos.TOP_RIGHT)
                                .darkStyle()
                                .show();
                        itemCode.getScene().getWindow().hide();
                        item_con.lodeShodTableData();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ItemsDTO collectData2() {
        String packText = pack.getText();
        String qtyText = qty.getText();
        String sellingpriceText = sellingprice.getText();
        String purchasepriceText = purchaseprice.getText();

        int tot_qty = Integer.parseInt(qtyText) * Integer.parseInt(packText);
        double tot_sell_price = Double.parseDouble(sellingpriceText) * Integer.parseInt(packText);
        double tot_pu_price = Double.parseDouble(purchasepriceText) * Integer.parseInt(packText);

        double sel_price_1_item = tot_sell_price / tot_qty;
        double pu_price_1_item = tot_pu_price / tot_qty;


        return new ItemsDTO(itemCode.getText(), description.getText(), Integer.parseInt(qty.getText())*Integer.parseInt(pack.getText()), sel_price_1_item
                , pu_price_1_item, stock_idLbl.getText(), expiration.getValue() + "");
    }

    public void clearFields() {
        itemCode.clear();
        description.clear();
        expiration.setValue(null);
        qty.clear();
        sellingprice.clear();
        purchaseprice.clear();
        pack.clear();
        //tot
    }

    public ItemsDTO collectData() {
        return new ItemsDTO(itemCode.getText(), description.getText(), Integer.parseInt(qty.getText()), Double.parseDouble(sellingprice.getText()
        ), Double.parseDouble(purchaseprice.getText()), stock_idLbl.getText(), expiration.getValue() + "");
    }
    private boolean validate() {
        if (description.getText().length() < 1) {
            new Alert(Alert.AlertType.ERROR, "Invalid description").show();
            description.requestFocus();
            return false;
        } else if (!qty.getText().matches("^\\d+$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty on hand").show();
            qty.requestFocus();
            return false;
        } else if (!sellingprice.getText().matches("^[0-9]+[.]?[0-9]*$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid unit price").show();
            sellingprice.requestFocus();
            return false;
        } else if (!purchaseprice.getText().matches("^[0-9]+[.]?[0-9]*$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid unit price").show();
            purchaseprice.requestFocus();
            return false;
        } else if (expiration.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Select Date").show();
            purchaseprice.requestFocus();
            return false;
        } else return true;
    }

    public void onGetToQty(ActionEvent actionEvent) {
        qty.requestFocus();
    }

    public void onGetToSellingPrice(ActionEvent actionEvent) {
        sellingprice.requestFocus();
    }

    public void onGetToPurchasePrice(ActionEvent actionEvent) {
        purchaseprice.requestFocus();

    }

    public void setAllData(StockTM stockTM) {
        try {
            for (ItemsDTO itemsDTO : itemsDAO.yetStockId_getAll(stockTM.getId())) {
                lists.getItems().add(itemsDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setStockInfo(StockTM stockTM) {
        stock_idLbl.setText(stockTM.getId());
        importer.getSelectionModel().select(stockTM.getImporter());
        areaDistributor.getSelectionModel().select(stockTM.getAreaDistributor());
        sales_Represent.getSelectionModel().select(stockTM.getSalesRepresent());
        agency.getSelectionModel().select(stockTM.getAgency());
    }

    @FXML
    void delectdeleteOnAction(MouseEvent event) {
        ItemsDTO selectedItem = lists.getSelectionModel().getSelectedItem();
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();
        if (buttonType.orElse(no) == yes) {
            try {
                boolean delete = itemsDAO.delete(selectedItem.getItemCode());
                if (delete) {
                    ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                    Notifications.create()
                            .graphic(imageView)
                            .text(" Deleted Successful ")
                            .title("Successful")
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.TOP_RIGHT)
                            .darkStyle()
                            .show();
                    lists.getItems().remove(selectedItem);
                    item_con.lodeTableData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            clearFields();
        }
    }

    public void setItem(ItemsTM item) {
        itemCode.setText(item.getItemCode());
        description.setText(item.getDescription());
        qty.setText(item.getQTY() + "");
        sellingprice.setText(item.getSelling_price() + "");
        purchaseprice.setText(item.getPurchase_price() + "");
        stock_idLbl.setText(item.getSuppler_Id());
        LocalDate parse = LocalDate.parse(item.getEx_date() + "");
        expiration.setValue(parse);
        addBtn.setText("Update");
    }

    public void deleteOnActoon(MouseEvent mouseEvent) {
        try {
            boolean delete = itemsDAO.delete(itemCode.getText());
            if (delete) {
                ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                Notifications.create()
                        .graphic(imageView)
                        .text(" Deleted Successful ")
                        .title("Successful")
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.TOP_RIGHT)
                        .darkStyle()
                        .show();
                clearFields();
                lists.getItems().remove(lists.getItems());
                //


                //
            } else {
                Notifications.create()
                        .graphic(new ImageView(new Image("/view/assests/images/icons8-fail-48.png")))
                        .text("Deleted Fail ")
                        .title("Fail")
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.TOP_RIGHT)
                        .show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void goToPacjkOnAction(ActionEvent actionEvent) {
        qty.requestFocus();
    }

    public void goToPack(ActionEvent actionEvent) {
        pack.requestFocus();
    }
}
