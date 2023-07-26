package controller.emloyee;

import controller.cashier.CashierController;
import dao.custom.EmployeeDAO;
import dao.custom.impl.EmployeeDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserlogController implements Initializable {

    public TextField user;
    private CashierController cashierController;

    public void userLogOnAction(ActionEvent actionEvent) {
        String text = user.getText();
        EmployeeDAO employeeDAO = new EmployeeDAOImpl();
        try {
            boolean exist = employeeDAO.exist(user.getText());
            if (exist) {
                String employeeName = employeeDAO.getEmployeeName(user.getText());
                Notifications.create()
                        .graphic(new ImageView(new Image("/view/assests/images/icons8-ok-48.png")))
                        .text("Login success ")
                        .title("success")
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.TOP_RIGHT)
                        .show();
                cashierController.setUser(employeeName);
                user.getScene().getWindow().hide();
                cashierController.searchTexfeld.requestFocus();

            }else {
                Notifications.create()
                        .graphic(new ImageView(new Image("/view/assests/images/icons8-cancel-50.png")))
                        .text("Not User ")
                        .title("success")
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user.requestFocus();
    }

    public void setCashierController(CashierController cashierController) {
        this.cashierController=cashierController;
    }
}
