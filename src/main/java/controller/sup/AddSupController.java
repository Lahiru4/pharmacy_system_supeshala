package controller.sup;

import com.jfoenix.controls.JFXButton;
import dao.custom.SupplyerDAO;
import dao.custom.impl.SupplyerDAOImpl;
import dto.SupplierDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddSupController implements Initializable {
    public SupplyerDAO supplyerDAO = new SupplyerDAOImpl();
    public ComboBox<String> position;
    @FXML
    private TextField name;

    @FXML
    private TextField phoneNumber;


    @FXML
    public JFXButton save;

    @FXML
    private TextField address;
    public SupplierController supplierController;
    public SupplierDTO supplierDTO;
    @FXML
    public JFXButton delete;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lodePosition();
    }

    private void lodePosition() {
        try {
            ArrayList<String> supPosition = supplyerDAO.getSupPosition();
            for (String temp : supPosition) {
                position.getItems().add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveOnAction(ActionEvent actionEvent) {
        try {
            String newID = supplyerDAO.generateNewID();
            if (save.getText().equals("update")) {
                SupplierDTO supplierDTO = new SupplierDTO(this.supplierDTO.getSupId(), name.getText(), position.getSelectionModel().getSelectedItem(), phoneNumber.getText(), address.getText());
                boolean update = supplyerDAO.update(supplierDTO);
                if (update) {
                    new Alert(Alert.AlertType.INFORMATION,"Okay").showAndWait();
                    address.getScene().getWindow().hide();
                    supplierController.loadSupplierData();
                }

            }else {
                SupplierDTO supplierDTO = new SupplierDTO(newID, name.getText(), position.getSelectionModel().getSelectedItem(), phoneNumber.getText(), address.getText());
                boolean add = supplyerDAO.add(supplierDTO);
                if (add) {
                    new Alert(Alert.AlertType.INFORMATION,"Okay").showAndWait();
                    address.getScene().getWindow().hide();
                    supplierController.loadSupplierData();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setOldData(SupplierDTO supplierDTO) {
        if (supplierDTO != null) {
            name.setText(supplierDTO.getName());
            phoneNumber.setText(supplierDTO.getPhoneNumber());
            address.setText(supplierDTO.getAddress());
            position.setValue(supplierDTO.getPosition());
            this.supplierDTO=supplierDTO;
        } else {
            // Clear the fields if the supplied SupplierDTO object is null
            name.setText("");
            phoneNumber.setText("");
            address.setText("");
            position.setValue(null);
        }
    }
    @FXML
    void deleteOnAction(ActionEvent event) {
        try {
            boolean delete1 = supplyerDAO.delete(this.supplierDTO.getSupId());
            if (delete1) {
                new Alert(Alert.AlertType.INFORMATION,"Okay").showAndWait();
                address.getScene().getWindow().hide();
                supplierController.loadSupplierData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
