package controller.cashier;

import bo.PlaceorderBO;
import bo.PlaceorderBOImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import controller.DashboardController;
import controller.emloyee.UserlogController;
import controller.stock.AddInventoryController;
import controller.stock.ItemsController;
import dao.custom.DayOrderCountDAO;
import dao.custom.ItemsDAO;
import dao.custom.OrderDAO;
import dao.custom.impl.DayOrderCountDAOImpl;
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
    public String cashier_name;
    public TextField discount;

    public TableColumn amount;
    public Label order_id_lbl;
    public TextField customer_name;
    public JFXRadioButton transfer;
    public DashboardController dashboard;
    @FXML
    private AnchorPane itemsAndBill;

    @FXML
    public TableView<ItemsTM> showTable;

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

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddCartController controller = fxmlLoader.getController();
        controller.cashierController = this;
        controller.setItems(items);
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
                /*billTable.getItems().get(i).getQty() +*/
                billTable.getItems().get(i).setQty(qty);
                billTable.getItems().get(i).setNet_tot(billTable.getItems().get(i).getSelling_price() * qty);
                billTable.refresh();
                setTot(items.getSelling_price(), qty);
                return;
            }
        }
        billTable.getItems().add(new BillTable2(items.getDescription(), qty, items.getSelling_price(), items.getItemCode(), items.getPurchase_price(), items.getSelling_price() * qty));
        setTot(items.getSelling_price(), qty);
    }

    private void setTot(double sellingPrice, int qty) {
        /*DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double cost = sellingPrice * qty;
        double oldTot = Double.parseDouble(tot.getText() + "00");
        double newTot = oldTot + cost;
        String format = decimalFormat.format(newTot);
        tot.setText(format);
        grossTot.setText(format + "");*/

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double temp_tot = 0;
        if (billTable.getItems() != null) {
            for (BillTable2 item : billTable.getItems()) {
                temp_tot += item.getNet_tot();
            }
        }
        tot.setText(decimalFormat.format(temp_tot));
        grossTot.setText(decimalFormat.format(temp_tot));

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
    @FXML
    void billDelectOnAction(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddCartController controller = fxmlLoader.getController();
        controller.cashierController = this;
        controller.setDataAndDeleteOnSetAction(billTable.getSelectionModel().getSelectedItem());
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
        saveOrder();

    }

    public void initialize() {
        lodeTableData();
        setSearchFilter();
        DayOrderCountDAO dayOrderCountDAO = new DayOrderCountDAOImpl();
        String newID = null;
        try {
            userLogMethode();
            newID = dayOrderCountDAO.generateNewID(LocalDate.now().toString());
            order_id_lbl.setText(newID);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void userLogMethode() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/emloyee/userlog.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setAlwaysOnTop(true);
        UserlogController controller = fxmlLoader.getController();
        controller.setCashierController(this);
        controller.user.requestFocus();
        stage.centerOnScreen();
        stage.show();
    }

    public void lodeTableData() {
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
                ItemsTM itemTM = new ItemsTM(item.getItemCode(), item.getItemDescription(), item.getQty(), item.getSellingPrice(), item.getPurchasePrice(), imageView, item.getStockID(), item.getExpirationDate());
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

    public void setSearchFilter() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
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
        };
        thread.start();

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
        boolean selected = transfer.isSelected();
        if (!selected) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            double g_tot = Double.parseDouble(grossTot.getText());
            double cash = Double.parseDouble(cashPaid.getText());
            if (cash >= g_tot) {
                balance.setText(decimalFormat.format((cash - g_tot)));
                saveOrder();
            } else new Alert(Alert.AlertType.ERROR, "check cash Amount").show();
        }
    }

    private void saveOrder() {
        if (!cashier_name.equals("user")) {
            boolean selected = transfer.isSelected();
            PlaceorderBO placeorderBO = new PlaceorderBOImpl();
            if (!selected) {
                boolean velid = velid();
                if (velid) {
                    OrdersDTO ordersDTO = collectdata();
                    ObservableList<BillTable2> items = billTable.getItems();
                    try {
                        boolean b = placeorderBO.saverOrder(ordersDTO, items);
                        if (b) {
                            Notifications.create().graphic(new ImageView(new Image("/view/assests/images/icons8-ok-48.png"))).text("order success ").title("success").hideAfter(Duration.seconds(5)).position(Pos.TOP_RIGHT).show();
                            if (customer_name.getText().length() > 0) {
                                printBillCustomerNameWith(billTable.getItems().size() + "", ordersDTO.getOrderId(), Double.parseDouble(cashPaid.getText()), Double.parseDouble(balance.getText()), this.getClass().getResourceAsStream("/report/cus_name_with_bill.jrxml"));//


                            } else {
                                printBill(billTable.getItems().size() + "", ordersDTO.getOrderId(), Double.parseDouble(cashPaid.getText()), Double.parseDouble(balance.getText()), this.getClass().getResourceAsStream("/report/Bill1.jrxml"));
                            }

                            textClear();
                            billTable.getItems().clear();
                            billTable.refresh();
                            //
                            DayOrderCountDAO dayOrderCountDAO = new DayOrderCountDAOImpl();
                            String newID = dayOrderCountDAO.generateNewID(LocalDate.now().toString());
                            order_id_lbl.setText(newID);
                            lodeTableData();
                            //
                            userLogMethode();
                            dashboard.username.setText("user");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                ObservableList<BillTable2> items = billTable.getItems();
                try {
                    boolean transferring = placeorderBO.transferring(items);
                    if (transferring) {
                        Notifications.create().graphic(new ImageView(new Image("/view/assests/images/icons8-ok-48.png"))).text("Transferring success").title("success").hideAfter(Duration.seconds(5)).position(Pos.TOP_RIGHT).show();
                        transfer.setSelected(false);
                        textClear();
                        billTable.getItems().clear();
                        billTable.refresh();
                        lodeTableData();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Notifications.create().graphic(new ImageView(new Image("/view/assests/images/icons8-cancel-96.png"))).text("Non Access User").title("fall").hideAfter(Duration.seconds(5)).position(Pos.TOP_RIGHT).show();
        }

    }

    private void printBillCustomerNameWith(String count, String orderId, Double cashPaid, Double balance, InputStream resourceAsStream) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Connection connection = connection = DbConnection.getInstance().getConnection();
                    String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date ,c.number from orderdetails od inner join  orders o ON od.order_id=o.order_id  inner join day_ordercount c ON c.or_id=o.order_Id where o.order_id = ?";
                    /*String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date from orderdetails od inner join orders o ON od.order_id=o.order_id where o.order_id = ?";*/
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
                    parameters.put("cashier_name", cashier_name);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, resultSetDataSource);

                    //net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);
                    JasperPrintManager.printReport(jasperPrint, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    customer_name.clear();
                }
            }
        };
        thread.start();

    }

    private void textClear() {
        tot.clear();
        discount.clear();
        grossTot.clear();
        cashPaid.clear();
        balance.clear();
    }

    private void printBill(String count, String orderId, Double cashPaid, Double balance, InputStream resourceAsStream) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Connection connection = connection = DbConnection.getInstance().getConnection();
                    String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date ,c.number from orderdetails od inner join  orders o ON od.order_id=o.order_id  inner join day_ordercount c ON c.or_id=o.order_Id where o.order_id = ?";
                    /*String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date from orderdetails od inner join orders o ON od.order_id=o.order_id where o.order_id = ?";*/
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
                    parameters.put("cashier_name", cashier_name);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, resultSetDataSource);

                    //net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);
                    JasperPrintManager.printReport(jasperPrint, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
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
            profit += (temp.getSelling_price() - temp.getPurchase_price()) * qty;
        }
        if (Double.parseDouble(discount.getText()) > 0) {
            double dis = Double.parseDouble(discount.getText());
            double v = (profit / 100) * dis;
            profit = profit - v;
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

    public void searchOnAction(ActionEvent actionEvent) throws IOException {
        int selectedIndex = showTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            ItemsTM items = showTable.getItems().get(selectedIndex);
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
            Parent parent = fxmlLoader.load();
            stage.setScene(new Scene(parent));
            AddCartController controller = fxmlLoader.getController();
            controller.cashierController = this;
            controller.setItems(items);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(showTable.getScene().getWindow());
            stage.centerOnScreen();
            stage.show();
            return;
        }
        ItemsTM items = showTable.getItems().get(0);

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/AddCart.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddCartController controller = fxmlLoader.getController();
        controller.cashierController = this;
        controller.setItems(items);
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

    public void addItemOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/stock/AddInventory.fxml"));
        Parent load = fxmlLoader.load();
        AddInventoryController controller = fxmlLoader.getController();
        controller.cashierController = this;
        controller.pack.setVisible(true);
        controller.packLbl.setVisible(true);
        stage.setScene(new Scene(load));
        stage.centerOnScreen();
        stage.show();
        controller.description.requestFocus();
    }

    public void setUser(String employeeName) {
        cashier_name = employeeName;
        dashboard.username.setText(employeeName);
    }

    public void setdashboardCon(DashboardController dashboardController) {
        this.dashboard = dashboardController;
        dashboard.username.setText("user");
    }

    public void updateOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/stock/items.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(discount.getScene().getWindow());
        ItemsController controller = fxmlLoader.getController();
        controller.setCashiCon(this);
        stage.centerOnScreen();
        stage.show();
    }
}
