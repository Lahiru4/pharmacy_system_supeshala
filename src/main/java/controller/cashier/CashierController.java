package controller.cashier;

import bo.PlaceorderBO;
import bo.PlaceorderBOImpl;
import com.jfoenix.controls.JFXButton;
import dao.custom.ItemsDAO;
import dao.custom.OrderDAO;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.OrderDAOImpl;
import db.DbConnection;
import dto.ItemsDTO;
import dto.OrdersDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.controlsfx.control.Notifications;
import tdm.BillItemTM;
import tdm.BillTable2;
import tdm.ItemsTM;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CashierController {
    public TextField grossTot;
    public TextField discount;
    public TableColumn amount;
    public Label order_id_lbl;
    public TextField customer_name;
    @FXML
    private AnchorPane itemsAndBill;

    @FXML
    private TableView<ItemsTM> showTable;

    @FXML
    private TableColumn<?, ?> img;

    @FXML
    private TableColumn<?, ?> description;

    @FXML
    private TableColumn<?, ?> qty;

    @FXML
    private TableColumn<?, ?> price;

    @FXML
    private TableColumn<?, ?> expirationDate;

    @FXML
    public TableView<BillTable2> billTable;

    @FXML
    private TableColumn<?, ?> billTableItems;

    @FXML
    private TableColumn<?, ?> billTableQty;

    @FXML
    private TableColumn<?, ?> billTablePrice;

    @FXML
    public TextField searchTexfeld;

    @FXML
    private TextField cashPaid;

    @FXML
    private TextField balance;

    @FXML
    private TextField tot;

    @FXML
    private JFXButton pay;
    public ItemsDAO itemsDAO = new ItemsDAOImpl();

    @FXML
    void addCartOnAction(MouseEvent event) throws IOException {
        int selectedIndex = showTable.getSelectionModel().getSelectedIndex();
        ItemsTM items = showTable.getItems().get(selectedIndex);
        AddCartController.setItems(items);

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddCartController controller = fxmlLoader.getController();
        controller.cashierController = this;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(showTable.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
    }

    public void setBillTableItems(ItemsTM items, int qty) {
        billTableItems.setCellValueFactory(new PropertyValueFactory<>("description"));
        billTableQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        billTablePrice.setCellValueFactory(new PropertyValueFactory<>("selling_price"));
        amount.setCellValueFactory(new PropertyValueFactory<>("net_tot"));

        for (int i = 0; i < billTable.getItems().size(); i++) {
            if (billTable.getItems().get(i).getItemCode().equals(items.getItemCode())) {
                billTable.getItems().get(i).setQty(billTable.getItems().get(i).getQty() + qty);
                billTable.getItems().get(i).setNet_tot(billTable.getItems().get(i).getNet_tot()+(billTable.getItems().get(i).getSelling_price()*qty));
                billTable.refresh();
                setTot(items.getSelling_price(), qty);
                return;
            }
        }
        billTable.getItems().add(new BillTable2(items.getDescription(), qty, items.getSelling_price(), items.getItemCode(), items.getPurchase_price(),items.getSelling_price()*qty));
        setTot(items.getSelling_price(), qty);
    }
    private void setTot(double sellingPrice, int qty) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double cost = sellingPrice * qty;
        double oldTot = Double.parseDouble(tot.getText() + "00");
        double newTot = oldTot + cost;
        String format = decimalFormat.format(newTot);
        tot.setText(format);
        grossTot.setText(format + "");
    }
    @FXML
    void billDelectOnAction(MouseEvent event) throws IOException {
        BillTable2 selectedItem = billTable.getSelectionModel().getSelectedItem();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddCartController controller = fxmlLoader.getController();
        controller.cashierController = this;
        controller.setDataAndDeleteOnSetAction(selectedItem);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(showTable.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
    }
    @FXML
    void onKeyPressed(KeyEvent event) {
        System.out.println(event.getText());
    }
    @FXML
    void payOnAction(ActionEvent event) {
        // save Orders
        boolean velid = velid();
        if (velid) {
            OrdersDTO ordersDTO = collectdata();
            PlaceorderBO placeorderBO = new PlaceorderBOImpl();

            ObservableList<BillTable2> items = billTable.getItems();


            try {
                boolean b = placeorderBO.saverOrder(ordersDTO, items);
                if (b) {
                    Notifications.create()
                            .graphic(new ImageView(new Image("/view/assests/images/icons8-ok-48.png")))
                            .text("order success ")
                            .title("success")
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.TOP_RIGHT)
                            .show();
                    printBill(billTable.getItems().size() + "", ordersDTO.getOrderId(),
                            Double.parseDouble(cashPaid.getText()), Double.parseDouble(balance.getText()),
                            this.getClass().getResourceAsStream("/report/Bill1.jrxml"));
                    textClear();
                    billTable.getItems().clear();
                    billTable.refresh();
                    order_id_lbl.setText(generateNewOrderId());
                    lodeTableData();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void initialize() {
        lodeTableData();
        setSearchFilter();
        order_id_lbl.setText(generateNewOrderId());
    }

    private void lodeTableData() {
        img.setCellValueFactory(new PropertyValueFactory<>("img"));
        qty.setCellValueFactory(new PropertyValueFactory<>("QTY"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        price.setCellValueFactory(new PropertyValueFactory<>("selling_price"));
        expirationDate.setCellValueFactory(new PropertyValueFactory<>("ex_date"));

        // Code to be executed on the JavaFX Application Thread
        // Update UI components or perform UI-related tasks
        //showTable.getItems().removeAll(showTable.getItems());


        ArrayList<ItemsTM> ob = new ArrayList<>();
        try {
            ArrayList<ItemsDTO> all = itemsDAO.getAll();
            for (ItemsDTO item : all) {
                Image img = new Image("view/assests/images/icons8-pill-48.png");
                ImageView imageView = new ImageView(img);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
                ItemsTM itemTM = new ItemsTM(
                        item.getItemCode(),
                        item.getItemDescription(),
                        item.getQty(),
                        item.getSellingPrice(),
                        item.getPurchasePrice(),
                        imageView,
                        item.getStockID(),
                        item.getExpirationDate()
                );
                ob.add(itemTM);
                //
            }
            showTable.setItems(FXCollections.observableArrayList(ob));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void setSearchFilter() {
        FilteredList<ItemsTM> filteredData = new FilteredList<>(showTable.getItems(), b -> true);

        searchTexfeld.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("UP")) {
                if (showTable.getSelectionModel().getSelectedIndex() == -1) {
                    showTable.getSelectionModel().select(0);
                    return;
                }
                showTable.getSelectionModel().select(showTable.getSelectionModel().getSelectedIndex() - 1);
            }
            if (keyEvent.getCode().toString().equals("DOWN")) {
                if (showTable.getSelectionModel().getSelectedIndex() == -1) {
                    showTable.getSelectionModel().select(0);
                    return;
                }
                showTable.getSelectionModel().select(showTable.getSelectionModel().getSelectedIndex() + 1);

            }
            searchTexfeld.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                filteredData.setPredicate(ItemsTM -> {
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }
                    String searchKeyWord = newValue.toLowerCase();
                    if (ItemsTM.getItemCode().toLowerCase().indexOf(searchKeyWord) > -1) {
                        return true;
                    } else if (ItemsTM.getDescription().toLowerCase().indexOf(searchKeyWord) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                });
            }));

            SortedList<ItemsTM> sortedList = new SortedList<>(filteredData);
            sortedList.comparatorProperty().bind(showTable.comparatorProperty());
            showTable.setItems(sortedList);
        });
    }

    public void discountOnAction(KeyEvent keyEvent) {
        if (discount.getText().length() < 1) {
            return;
        }
        if (Double.parseDouble(discount.getText()) > 0) {
            double d_count = Double.parseDouble(discount.getText());
            double tot_value = Double.parseDouble(tot.getText());
            double g_tot = tot_value - ((tot_value / 100) * d_count);
            grossTot.setText(g_tot + "");
        }

    }

    public void clearFields() {
        cashPaid.clear();
        balance.clear();
        tot.clear();
        grossTot.clear();
        discount.clear();
    }

    public void totOnAction(ActionEvent actionEvent) {
        discount.requestFocus();
    }

    public void disOnAction(ActionEvent actionEvent) {
        grossTot.requestFocus();
    }

    public void grossTotOnAction(ActionEvent actionEvent) {
        cashPaid.requestFocus();
    }

    @FXML
    void cashPaidOnAction(ActionEvent event) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double g_tot = Double.parseDouble(grossTot.getText());
        double cash = Double.parseDouble(cashPaid.getText());
        if (cash >= g_tot) {
            balance.setText(decimalFormat.format((cash - g_tot)));
            balance.requestFocus();
        } else new Alert(Alert.AlertType.ERROR, "check cash Amount").show();
    }
    public void balanceOnAction(ActionEvent actionEvent) {
        // save Orders
        boolean velid = velid();
        if (velid) {
            OrdersDTO ordersDTO = collectdata();
            PlaceorderBO placeorderBO = new PlaceorderBOImpl();

            ObservableList<BillTable2> items = billTable.getItems();


            try {
                boolean b = placeorderBO.saverOrder(ordersDTO, items);
                if (b) {
                    Notifications.create()
                            .graphic(new ImageView(new Image("/view/assests/images/icons8-ok-48.png")))
                            .text("order success ")
                            .title("success")
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.TOP_RIGHT)
                            .show();
                    printBill(billTable.getItems().size() + "", ordersDTO.getOrderId(),
                            Double.parseDouble(cashPaid.getText()), Double.parseDouble(balance.getText()),
                            this.getClass().getResourceAsStream("/report/Bill1.jrxml"));
                    textClear();
                    billTable.getItems().clear();
                    billTable.refresh();
                    order_id_lbl.setText(generateNewOrderId());
                    lodeTableData();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void textClear() {
        tot.clear();
        discount.clear();
        grossTot.clear();
        cashPaid.clear();
        balance.clear();
        customer_name.clear();

    }

    private void printBill(String count, String orderId, Double cashPaid, Double balance, InputStream resourceAsStream) {
        try {
            Connection connection = connection = DbConnection.getInstance().getConnection();
            String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date from orderdetails od inner join orders o ON od.order_id=o.order_id where o.order_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);
            JasperDesign jasperDesign = JRXmlLoader.load(resourceAsStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);


            Map<String, Object> parameters = new HashMap<>();

            parameters.put("order_Id", orderId);
            parameters.put("cash_paid", cashPaid);
            parameters.put("balance", balance);
            parameters.put("count", count);
            parameters.put("customer_name", customer_name.getText());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, resultSetDataSource);

            net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);
            JasperPrintManager.printReport(jasperPrint, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OrdersDTO collectdata() {
        if (generateNewOrderId().equals("")) {
            new Alert(Alert.AlertType.ERROR, "ID IS NOT 'available'").show();
            return null;
        }
        String itemCost = grossTot.getText();
        ObservableList<BillTable2> items = billTable.getItems();
        int totQty = 0;
        double profit = 0;
        for (BillTable2 temp : items) {
            // totQty
            int qty = temp.getQty();
            totQty += qty;

            //totProfit
            profit = temp.getSelling_price() - temp.getPurchase_price();
        }
        if (discount.getText() == "") {
            if (Double.parseDouble(discount.getText()) > 0) {
                double dis = Double.parseDouble(discount.getText());
                double v = (profit / 100) * dis;
                profit = profit - v;
            }
        }
        return new OrdersDTO(generateNewOrderId(), totQty, LocalDate.now() + "", Double.parseDouble(itemCost), profit);
    }

    public String generateNewOrderId() {
        OrderDAO orderDAO = new OrderDAOImpl();
        String id = "";
        try {
            id = orderDAO.generateNewID();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void printDiscountBill(String orderId, double cashPaid, double balance, double discount) {


    }

    public void searchOnAction(ActionEvent actionEvent) throws IOException {
        int selectedIndex = showTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            ItemsTM items = showTable.getItems().get(selectedIndex);
            AddCartController.setItems(items);

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
            Parent parent = fxmlLoader.load();
            stage.setScene(new Scene(parent));
            AddCartController controller = fxmlLoader.getController();
            controller.cashierController = this;
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(showTable.getScene().getWindow());
            stage.centerOnScreen();
            stage.show();
            return;

        }


        ItemsTM items = showTable.getItems().get(0);
        AddCartController.setItems(items);

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddCartController controller = fxmlLoader.getController();
        controller.cashierController = this;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(showTable.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
    }

    public boolean velid() {
        if (tot.getText().length() < 1) {
            new Alert(Alert.AlertType.ERROR, "Select Items").show();
            searchTexfeld.requestFocus();
            return false;
        }
        if (discount.getText().length() < 1) {
            discount.setText("0");
            return false;
        }
        double g_tot = Double.parseDouble(grossTot.getText());
        double cash = Double.parseDouble(cashPaid.getText());
        if (cash >= g_tot) {
        } else {
            new Alert(Alert.AlertType.ERROR, "check cash Amount").show();
            cashPaid.requestFocus();
            return false;
        }
        return true;
    }

    public void billTableDelete(BillTable2 selectedItem) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();
        if (buttonType.orElse(no) == yes) {
            billTable.getItems().remove(selectedItem);
            billTable.refresh();
            clearFields();
            for (int i = 0; i < billTable.getItems().size(); i++) {
                BillTable2 billTable2 = billTable.getItems().get(i);
                setTot(billTable2.getSelling_price(), billTable2.getQty());
            }
        }
    }
}
