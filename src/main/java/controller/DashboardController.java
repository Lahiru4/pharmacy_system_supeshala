package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controller.cashier.CashierController;
import controller.home.HomeController;
import controller.stock.StockController;
import dao.custom.EmployeeDAO;
import dao.custom.impl.EmployeeDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private AnchorPane loadFormContext;

    @FXML
    private JFXButton sales;

    @FXML
    private AnchorPane home;

    @FXML
    private JFXButton logOutBtn;

    @FXML
    private JFXButton stock;

    @FXML
    private JFXButton setting;

    @FXML
    private JFXButton employ;

    @FXML
    public JFXTextField username;
    public String cashier_name;

    @FXML
    void employOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Order.fxml"));
        Node resource = fxmlLoader.load();
        home.getChildren().clear();
        home.getChildren().add(resource);

    }

    @FXML
    void salesOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/Cashier.fxml"));
        Node resource = fxmlLoader.load();
        CashierController controller = fxmlLoader.getController();
        home.getChildren().clear();
        home.getChildren().add(resource);
        if (username.getText().length()>0) {
            controller.cashier_name = this.username.getText();
        }
        controller.searchTexfeld.requestFocus();

    }

    @FXML
    void settingsOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/emloyee/employ.fxml"));
        Node resource = fxmlLoader.load();
        home.getChildren().clear();
        home.getChildren().add(resource);

    }

    @FXML
    void stockOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/stock/Stock.fxml"));
        Node resource = fxmlLoader.load();
        home.getChildren().clear();
        home.getChildren().add(resource);
        StockController controller = fxmlLoader.getController();
    }

    @SneakyThrows
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lodeMethode();
    }

    private void lodeMethode() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/home/home.fxml"));
        Node resource = fxmlLoader.load();
        HomeController controller = fxmlLoader.getController();
        home.getChildren().clear();
        home.getChildren().add(resource);
    }

    public void homePageOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/home/home.fxml"));
        Node resource = fxmlLoader.load();
        HomeController controller = fxmlLoader.getController();
        home.getChildren().clear();
        home.getChildren().add(resource);
    }

    public void setNameOnAction(ActionEvent actionEvent) {
        String text = username.getText();
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();
        try {
            boolean exist = employeeDAO.exist(username.getText());
            if (exist) {
                String employeeName = employeeDAO.getEmployeeName(username.getText());
                username.clear();
                username.setText(employeeName);
            }else {
                new Alert(Alert.AlertType.ERROR,"not user").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
