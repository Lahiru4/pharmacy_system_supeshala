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
import javafx.scene.input.MouseEvent;
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
        password.requestFocus();
    }

    public void logInOnAction(ActionEvent actionEvent) throws IOException {
            if (password.getText().equals("123")) {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
                Parent parent = fxmlLoader.load();
                DashboardController controller = fxmlLoader.getController();
                stage.setScene(new Scene(parent));
                stage.centerOnScreen();
                stage.show();
                password.getScene().getWindow().hide();

            }else {
                new Alert(Alert.AlertType.ERROR, "NOT USER !").show();
                password.setStyle("-fx-border-color: red");
                password.requestFocus();
                return;
            }



    }

    public void passwordOnAction(ActionEvent actionEvent) throws IOException {
        if (password.getText().equals("123")) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            Parent parent = fxmlLoader.load();
            DashboardController controller = fxmlLoader.getController();
            stage.setScene(new Scene(parent));
            stage.centerOnScreen();
            stage.show();
            password.getScene().getWindow().hide();

        }else {
            new Alert(Alert.AlertType.ERROR, "NOT USER !").show();
            password.setStyle("-fx-border-color: red");
            password.requestFocus();
            return;
        }
    }

    public void regeshtarOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/emloyee/add_emplyee.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        stage.centerOnScreen();
        stage.show();
    }
}
