package controller;

import com.jfoenix.controls.JFXButton;
import controller.cashier.CashierController;
import controller.stock.StockController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
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
    public Label username;

    @FXML
    void employOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Order.fxml"));
        Node resource = fxmlLoader.load();
        home.getChildren().clear();
        home.getChildren().add(resource);

    }

    @FXML
    void logOutBtnOn(ActionEvent event) throws IOException {

    }

    @FXML
    void salesOnAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/Cashier.fxml"));
        Node resource = fxmlLoader.load();
        home.getChildren().clear();
        home.getChildren().add(resource);
        CashierController controller = fxmlLoader.getController();
        controller.searchTexfeld.requestFocus();

    }

    @FXML
    void settingsOnAction(ActionEvent event) {

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/cashier/Cashier.fxml"));
        Node resource = fxmlLoader.load();
        home.getChildren().clear();
        home.getChildren().add(resource);
        CashierController controller = fxmlLoader.getController();
        controller.searchTexfeld.requestFocus();
    }
}
