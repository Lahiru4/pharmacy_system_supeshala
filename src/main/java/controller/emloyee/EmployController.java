package controller.emloyee;

import dao.custom.EmployeeDAO;
import dao.custom.impl.EmployeeDAOImpl;
import dto.EmployeeDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class EmployController implements Initializable {
    @FXML
    public TableView<EmployeeDTO> table;
    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TableColumn<?, ?> name;

    @FXML
    private TableColumn<?, ?> number;

    @FXML
    private Label emp_count;
    EmployeeDAO employeeDAO=new EmployeeDAOImpl();


    public void addemployeeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/emloyee/add_emplyee.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        AddEmplyeeController controller = fxmlLoader.getController();
        controller.setEmployController(this);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(table.getScene().getWindow());
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        number.setCellValueFactory(new PropertyValueFactory<>("phone"));
        setTableData();
    }

    public void setTableData() {
        try {
            for (EmployeeDTO employeeDTO : employeeDAO.getAll()) {
                table.getItems().add(employeeDTO);
            }
            emp_count.setText(table.getItems().size()+"");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void tableClickOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/emloyee/add_emplyee.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        AddEmplyeeController controller = fxmlLoader.getController();
        controller.setEmployController(this);
        controller.saveBtn.setText("Update");
        controller.id.setEditable(false);
        controller.delete.setVisible(true);
        controller.setOldData(table.getSelectionModel().getSelectedItem());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(table.getScene().getWindow());
        stage.show();
    }
}
