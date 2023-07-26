package controller.emloyee;

import com.jfoenix.controls.JFXButton;
import dao.custom.EmployeeDAO;
import dao.custom.impl.EmployeeDAOImpl;
import dto.EmployeeDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class AddEmplyeeController {


    public ImageView delete;
    private EmployController employController;
    public JFXButton saveBtn;
    @FXML
    public TextField id;

    @FXML
    private TextField number;

    @FXML
    private TextField name;
    public EmployeeDAO employeeDAO = new EmployeeDAOImpl();

    public void setEmployController(EmployController employController) {
        this.employController = employController;
    }

    public void save(ActionEvent actionEvent) {
        EmployeeDTO employeeDTO = getData();
        if (saveBtn.getText().equals("Update")) {
            try {
                boolean update = employeeDAO.update(employeeDTO);
                if (update) {
                    employController.table.getItems().clear();
                    employController.table.refresh();
                    employController.setTableData();
                    id.getScene().getWindow().hide();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                boolean add = employeeDAO.add(employeeDTO);
                if (add) {
                    id.getScene().getWindow().hide();
                    employController.table.getItems().clear();
                    employController.table.refresh();
                    employController.setTableData();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private EmployeeDTO getData() {
        return new EmployeeDTO(id.getText(), name.getText(), number.getText(), null, null);
    }

    public void setOldData(EmployeeDTO selectedItem) {
        id.setText(selectedItem.getId());
        name.setText(selectedItem.getName());
        number.setText(selectedItem.getPhone());
    }

    public void deleteOnAction(MouseEvent mouseEvent) {
        try {
            boolean delete = employeeDAO.delete(id.getText());
            if (delete) {
                employController.table.getItems().clear();
                employController.table.refresh();
                employController.setTableData();
                id.getScene().getWindow().hide();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
