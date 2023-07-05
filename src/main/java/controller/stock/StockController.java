package controller.stock;

import com.jfoenix.controls.JFXButton;
import dao.custom.StockDAO;
import dao.custom.impl.StockDAOImpl;
import db.DbConnection;
import dto.StockDTO;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.Notifications;
import tdm.StockTM;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class StockController {
    public TableView<StockTM> stockTable;

    @FXML
    private TableColumn<?, ?> stockId;

    @FXML
    private TableColumn<?, ?> importer;

    @FXML
    private TableColumn<?, ?> distributor;

    @FXML
    private TableColumn<?, ?> represent;

    @FXML
    private TableColumn<?, ?> agency;

    @FXML
    private TableColumn<?, ?> totQty;

    @FXML
    private TableColumn<?, ?> stockDate;

    @FXML
    private TableColumn<?, ?> action;
    @FXML
    private TextField searchTexfeld;
    public StockDAO stockDAO = new StockDAOImpl();

    public void initialize() {
        lodeTableData();

    }

    public void lodeTableData() {
        stockId.setCellValueFactory(new PropertyValueFactory<>("id"));
        importer.setCellValueFactory(new PropertyValueFactory<>("importer"));
        distributor.setCellValueFactory(new PropertyValueFactory<>("areaDistributor"));
        represent.setCellValueFactory(new PropertyValueFactory<>("salesRepresent"));
        agency.setCellValueFactory(new PropertyValueFactory<>("agency"));
        totQty.setCellValueFactory(new PropertyValueFactory<>("totalQty"));
        stockDate.setCellValueFactory(new PropertyValueFactory<>("stockDate"));
        action.setCellValueFactory(new PropertyValueFactory<>("btn"));

        stockTable.getItems().removeAll(stockTable.getItems());
        try {
            ArrayList<StockDTO> all = stockDAO.getAll();
            for (StockDTO items : all) {

                Image img = new Image("view/assests/images/icons8-delete-100.png");
                ImageView imageView = new ImageView(img);
                imageView.setFitHeight(30);
                imageView.setPreserveRatio(true);
                JFXButton button = new JFXButton();
                button.setGraphic(imageView);
                setRemoveBtnOnAction(button, items);

                stockTable.getItems().add(new StockTM(items.getId(), items.getImporter(), items.getAreaDistributor()
                        , items.getSalesRepresent(), items.getAgency(), items.getTotalQty(), items.getTotalPurchasePrice()
                        , items.getStockDate(), button));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setRemoveBtnOnAction(JFXButton button, StockDTO stockDTO) {
        button.setOnAction((actionEvent -> {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

            if (buttonType.orElse(no) == yes) {
                String itemCode1 = stockDTO.getId();
                try {
                    boolean delete = stockDAO.delete(itemCode1);
                    if (delete) {
                        ImageView imageView = new ImageView(new Image("/view/assests/images/icons8-ok-48.png"));
                        Notifications.create()
                                .graphic(imageView)
                                .text(" Deleted Successful ")
                                .title("Successful")
                                .hideAfter(Duration.seconds(5))
                                .position(Pos.TOP_RIGHT)
                                .darkStyle()
                                .show();
                        lodeTableData();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }));

    }

    public void addItemOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/stock/AddInventory.fxml"));
        Parent load = fxmlLoader.load();
        AddInventoryController controller = fxmlLoader.getController();
        controller.stockController = this;
        controller.pack.setVisible(true);
        controller.packLbl.setVisible(true);
        stage.setScene(new Scene(load));
        stage.centerOnScreen();
        stage.show();
        controller.description.requestFocus();
    }

    public void stockReportOnAction(ActionEvent actionEvent) {
        Thread thread = new Thread() {
            @SneakyThrows
            public void run() {
                String sql = "select * from items";
                JasperDesign jasdi = JRXmlLoader.load(this.getClass().getResourceAsStream("/report/thirakasuwasewastock.jrxml"));
                JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sql);
                jasdi.setQuery(newQuery);
                JasperReport js = JasperCompileManager.compileReport(jasdi);
                JasperPrint jp = JasperFillManager.fillReport(js, null, DbConnection.getInstance().getConnection());
                JasperViewer viewer = new JasperViewer(jp, false);
                viewer.show();
            }
        };
        thread.start();
    }

    void setSearchFilter() {
        FilteredList<StockTM> filteredData = new FilteredList<>(stockTable.getItems(), b -> true);

        searchTexfeld.setOnKeyPressed(keyEvent -> {
            searchTexfeld.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                filteredData.setPredicate(ItemsTM -> {
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }
                    String searchKeyWord = newValue.toLowerCase();
                    if (ItemsTM.getAgency().toLowerCase().indexOf(searchKeyWord) > -1) {
                        return true;
                    } else if (ItemsTM.getStockDate().toLowerCase().indexOf(searchKeyWord) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                });
            }));
            SortedList<StockTM> sortedList = new SortedList<>(filteredData);
            sortedList.comparatorProperty().bind(stockTable.comparatorProperty());
            stockTable.setItems(sortedList);
        });
    }

    @FXML
    void searchTexfeldOnAction(KeyEvent event) {
        setSearchFilter();
    }

    public void reportMailOnAction(ActionEvent actionEvent) {
    }

    public void mouseClickOnAction(MouseEvent mouseEvent) throws IOException {
        int selectedIndex = stockTable.getSelectionModel().getSelectedIndex();
        StockTM stockTM = stockTable.getItems().get(selectedIndex);
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/stock/AddInventory.fxml"));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        AddInventoryController controller = fxmlLoader.getController();
        controller.stockController = this;
        controller.setAllData(stockTM);
        controller.setStockInfo(stockTM);
        stage.show();
        stage.centerOnScreen();
    }
    public void SupplierOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/sup/Supplier.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(stockTable.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
    }
    public void searchItemOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/stock/items.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(stockTable.getScene().getWindow());
        stage.centerOnScreen();
        stage.show();
    }
}
