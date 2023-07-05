package controller;

import dao.custom.EmployeeDAO;
import dao.custom.impl.EmployeeDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {
    @FXML
    public TextField userName;
    @FXML
    public PasswordField password;
    EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void logInOnAction(ActionEvent actionEvent) {
        try {
            boolean exist = employeeDAO.exist(userName.getText());
            if (!exist) {
                new Alert(Alert.AlertType.ERROR, "NOT USER !").show();
                userName.setStyle("-fx-border-color: red");
                userName.requestFocus();
                return;
            }
            String employeeID = employeeDAO.getEmployeeID(userName.getText());
            if (!employeeID.equals(password.getText())) {
                new Alert(Alert.AlertType.ERROR, "invalid password !").show();
                password.setStyle("-fx-border-color: red");
                password.requestFocus();
                return;
            }

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            Parent parent = fxmlLoader.load();
            DashboardController controller = fxmlLoader.getController();
            controller.username.setText(userName.getText());
            controller.cashier_name=userName.getText();
            stage.setScene(new Scene(parent));
            stage.centerOnScreen();
            stage.show();

            password.getScene().getWindow().hide();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void userNameOnAction(ActionEvent actionEvent) {
        password.requestFocus();
    }

    public void passwordOnAction(ActionEvent actionEvent) {
        try {
            boolean exist = employeeDAO.exist(userName.getText());
            if (!exist) {
                new Alert(Alert.AlertType.ERROR, "NOT USER !").show();
                userName.setStyle("-fx-border-color: red");
                userName.requestFocus();
                return;
            }
            String employeeID = employeeDAO.getEmployeeID(userName.getText());
            if (!employeeID.equals(password.getText())) {
                new Alert(Alert.AlertType.ERROR, "invalid password !").show();
                password.setStyle("-fx-border-color: red");
                password.requestFocus();
                return;
            }
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            Parent parent = fxmlLoader.load();
            DashboardController controller = fxmlLoader.getController();
            controller.username.setText(userName.getText());
            controller.cashier_name=userName.getText();
            stage.setScene(new Scene(parent));
            stage.centerOnScreen();
            stage.show();

            password.getScene().getWindow().hide();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
