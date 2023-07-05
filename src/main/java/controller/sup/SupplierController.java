package controller.sup;

import dao.custom.SupplyerDAO;
import dao.custom.impl.SupplyerDAOImpl;
import dto.SupplierDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {
    public AnchorPane supBoard;

    @FXML
    private TableView<SupplierDTO> sup_table;

    @FXML
    private TableColumn<SupplierDTO, String> name;

    @FXML
    private TableColumn<SupplierDTO, String> position;

    @FXML
    private TableColumn<SupplierDTO, String> number;

    @FXML
    private TableColumn<SupplierDTO, String> address;

    private SupplyerDAO supplyerDAO=new SupplyerDAOImpl();
    public void SupplierOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/sup/AddSup.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(supBoard.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
        AddSupController controller = fxmlLoader.getController();
        controller.supplierController=this;
        controller.delete.setVisible(false);
    }
    public void loadSupplierData()  {
        ArrayList<SupplierDTO> suppliers = null; // Call the getAll() method to retrieve the supplier data
        try {
            suppliers = supplyerDAO.getAll();
            // Clear existing data in the table
            sup_table.getItems().clear();

            // Bind the cell values to the corresponding properties in SupplierDTO
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            position.setCellValueFactory(new PropertyValueFactory<>("position"));
            number.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            address.setCellValueFactory(new PropertyValueFactory<>("address"));

            // Populate the table with the retrieved data
            sup_table.getItems().addAll(suppliers);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSupplierData();
    }

    public void upDate(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/sup/AddSup.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(supBoard.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
        AddSupController controller = fxmlLoader.getController();
        controller.supplierController=this;
        controller.save.setText("update");
        controller.delete.setVisible(true);
        controller.setOldData(sup_table.getSelectionModel().getSelectedItem());
    }
}
