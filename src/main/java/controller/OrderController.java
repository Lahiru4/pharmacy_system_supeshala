package controller;

import bo.PlaceorderBO;
import bo.PlaceorderBOImpl;
import com.jfoenix.controls.JFXButton;
import dao.custom.DayOrderCountDAO;
import dao.custom.ItemsDAO;
import dao.custom.OrderDAO;
import dao.custom.OrderDrtailsDAO;
import dao.custom.impl.DayOrderCountDAOImpl;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.OrderDAOImpl;
import dao.custom.impl.OrderDetailsDAOImpl;
import db.DbConnection;
import dto.ItemsDTO;
import dto.OrdersDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import tdm.OrderTM;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class OrderController implements Initializable {
    public TableView<OrderTM> order;
    public DatePicker select_date;
    public Label order_count;
    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TableColumn<?, ?> totQty;

    @FXML
    private TableColumn<?, ?> totAmount;

    @FXML
    private TableColumn<?, ?> profit;

    @FXML
    private TableColumn<?, ?> date;

    @FXML
    private TableColumn<?, ?> action;
    private OrderDAO orderDAO = new OrderDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        select_date.setValue(LocalDate.now());
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        totQty.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        totAmount.setCellValueFactory(new PropertyValueFactory<>("itemCost"));
        profit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        action.setCellValueFactory(new PropertyValueFactory<>("button"));
        setTableDataSub();
        setCount();
    }

    private void setCount() {
        order_count.setText(order.getItems().size()+"");
    }

    /*private void lodeTableData() {
        try {
            order.getItems().removeAll(order.getItems());
            for (OrdersDTO ordersDTO : orderDAO.getAll()) {
                Image img = new Image("view/assests/images/icons8-print-48.png");
                ImageView imageView = new ImageView(img);
                imageView.setFitHeight(30);
                imageView.setPreserveRatio(true);
                JFXButton button = new JFXButton();
                button.setGraphic(imageView);
                setRemoveBtnOnAction(button, ordersDTO);

                //

                order.getItems().add(new OrderTM(ordersDTO.getOrderId(), ordersDTO.getTotalQuantity(), ordersDTO.getOrderDate()
                        , ordersDTO.getItemCost(), ordersDTO.getProfit(), button));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
*/
    private void setRemoveBtnOnAction(JFXButton button, OrdersDTO ordersDTO) {
        //
        button.setOnAction((actionEvent -> {

            OrderDrtailsDAO orderDetailsDAO = new OrderDetailsDAOImpl();
            try {
                String itemsCount = orderDetailsDAO.getItemsCount(ordersDTO.getOrderId());
                printBill(ordersDTO.getOrderId(), Integer.parseInt(itemsCount), this.getClass().getResourceAsStream("/report2/re_printBill.jrxml"));
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }));


        //
    }

    private void printBill(String orderId, int count, InputStream resourceAsStream) {

        try {
            Connection connection = connection = DbConnection.getInstance().getConnection();
            /*String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date from orderdetails od inner join orders o ON od.order_id=o.order_id where o.order_id = ?";*/
            String query = "select od.order_Id,od.name,od.qty, od.selling_price,od.net_tot,o.item_cost,o.order_date,c.number from orderdetails od inner join orders o ON od.order_id=o.order_id inner join day_ordercount c ON c.or_id=o.order_Id where o.order_id =  ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultSet);
            JasperDesign jasperDesign = JRXmlLoader.load(resourceAsStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);


            Map<String, Object> parameters = new HashMap<>();

            parameters.put("order_Id", orderId);
            parameters.put("item_count", count);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, resultSetDataSource);

            net.sf.jasperreports.view.JasperViewer.viewReport(jasperPrint, false);
            JasperPrintManager.printReport(jasperPrint, true);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void returnOnAction(MouseEvent mouseEvent) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();
        if (buttonType.orElse(no) == yes) {
            PlaceorderBO placeorderBO = new PlaceorderBOImpl();
            //OrdersDTO selectedItem = order.getSelectionModel().getSelectedItem();
            OrderTM ordersDTO = order.getSelectionModel().getSelectedItem();
            OrdersDTO selectedItem = new OrdersDTO(ordersDTO.getOrderId(), ordersDTO.getTotalQuantity(), ordersDTO.getOrderDate()
                    , ordersDTO.getItemCost(), ordersDTO.getProfit());
            OrderDrtailsDAO orderDetailsDAO = new OrderDetailsDAOImpl();
            ItemsDAO itemsDAO = new ItemsDAOImpl();
            try {
                //ArrayList<ItemsDTO> all =new ArrayList<>();

                ArrayList<ItemsDTO> itemsDTOS = orderDetailsDAO.yetOrderIdGetItems(selectedItem.getOrderId());
                /*for (String yetOrderIdGetItem : orderDetailsDAO.yetOrderIdGetItems(selectedItem.getOrderId())) {


                    all.add(itemsDAO.yetCodeGetAll(yetOrderIdGetItem));
                }*/
                boolean b = placeorderBO.returnOrder(selectedItem, itemsDTOS);
                if (b) {
                    new Alert(Alert.AlertType.INFORMATION, "okay").show();
                    setTableDataSub();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void setTableDataSub(){
        try {
            order.getItems().removeAll(order.getItems());
            for (OrdersDTO ordersDTO : orderDAO.yetDateGetAll(select_date.getValue()+"")) {
                Image img = new Image("view/assests/images/icons8-print-48.png");
                ImageView imageView = new ImageView(img);
                imageView.setFitHeight(30);
                imageView.setPreserveRatio(true);
                JFXButton button = new JFXButton();
                button.setGraphic(imageView);
                setRemoveBtnOnAction(button, ordersDTO);


                DayOrderCountDAO dayOrderCountDAO = new DayOrderCountDAOImpl();
                String s = dayOrderCountDAO.geAll(ordersDTO.getOrderId());
                String Id=ordersDTO.getOrderId();
                if (!s.equals("0")) {
                    Id=Id+"( "+s+" )";
                }


                order.getItems().add(new OrderTM(ordersDTO.getOrderId(), ordersDTO.getTotalQuantity(), ordersDTO.getOrderDate()
                        , ordersDTO.getItemCost(), ordersDTO.getProfit(), button,Id));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            setCount();
        }
    }

    public void selectDataOnAction(ActionEvent actionEvent) {
        setTableDataSub();
    }
}
