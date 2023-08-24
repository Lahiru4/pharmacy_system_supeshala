package controller.stock;

import controller.cashier.CashierController;
import dao.custom.ItemsDAO;
import dao.custom.StockDAO;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.StockDAOImpl;
import dto.ItemsDTO;
import dto.StockDTO;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tdm.ItemsTM;
import tdm.StockTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemsController implements Initializable {
    public TableColumn item_code;
    @FXML
    public TableView<ItemsDTO> lists;

    @FXML
    private TableColumn<?, ?> name;

    @FXML
    private TableColumn<?, ?> stock_id;

    @FXML
    private TableColumn<?, ?> qtyC;

    @FXML
    private TableColumn<?, ?> sellP;

    @FXML
    private TableColumn<?, ?> purchP;

    @FXML
    private TableColumn<?, ?> exDate;

    @FXML
    private TableColumn<?, ?> action;

    @FXML
    public TextField searchTexfeld;
    public ItemsDAO itemsDAO = new ItemsDAOImpl();
    public StockDAO stockDAO = new StockDAOImpl();
    private CashierController cashierController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        name.setCellValueFactory(new PropertyValueFactory<>("itemDescription"));
        stock_id.setCellValueFactory(new PropertyValueFactory<>("stockID"));
        qtyC.setCellValueFactory(new PropertyValueFactory<>("qty"));
        sellP.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        purchP.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        exDate.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        item_code.setCellValueFactory(new PropertyValueFactory<>("itemCode"));

        lodeTableData();
    }

    public void lodeTableData() {
        try {
            for (ItemsDTO itemsDTO : itemsDAO.getAll()) {
                lists.getItems().add(itemsDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    void setSearchFilter() {
        FilteredList<ItemsDTO> filteredData = new FilteredList<>(lists.getItems(), b -> true);

        searchTexfeld.setOnKeyPressed(keyEvent -> {
            searchTexfeld.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                filteredData.setPredicate(ItemsDTO -> {
                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                        return true;
                    }
                    String searchKeyWord = newValue.toLowerCase();
                    if (ItemsDTO.getItemCode().toLowerCase().indexOf(searchKeyWord) > -1) {
                        return true;
                    } else if (ItemsDTO.getItemDescription().toLowerCase().indexOf(searchKeyWord) > -1) {
                        return true;
                    } else {
                        return false;
                    }
                });
            }));

            SortedList<ItemsDTO> sortedList = new SortedList<>(filteredData);
            sortedList.comparatorProperty().bind(lists.comparatorProperty());
            lists.setItems(sortedList);
        });
    }

    public void searchTexfeldOnAction(KeyEvent keyEvent) {
        setSearchFilter();
    }

    public void editOnAction(MouseEvent mouseEvent) throws IOException {
        searchTexfeld.clear();
        //
        int selectedIndex = lists.getSelectionModel().getSelectedIndex();
        ItemsDTO itemsDTO = lists.getItems().get(selectedIndex);
        try {
            StockDTO stockDTO = stockDAO.yetStockIdGetStock(itemsDTO.getStockID());
            ItemsDTO selectedItem = lists.getSelectionModel().getSelectedItem();
            StockTM stockTM = new StockTM(stockDTO.getId(), stockDTO.getImporter(), stockDTO.getAreaDistributor(), stockDTO.getSalesRepresent()
                    , stockDTO.getAgency(), stockDTO.getTotalQty(), stockDTO.getTotalPurchasePrice(), stockDTO.getStockDate(), null);
            ItemsTM itemsTM = new ItemsTM(selectedItem.getItemCode(), selectedItem.getItemDescription(), selectedItem.getQty(), selectedItem.getSellingPrice()
                    , selectedItem.getPurchasePrice(), null, selectedItem.getStockID(), selectedItem.getExpirationDate());

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/stock/AddInventory.fxml"));
            Parent parent = fxmlLoader.load();
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(lists.getScene().getWindow());
            AddInventoryController controller = fxmlLoader.getController();
            controller.setAllData(stockTM);
            controller.setStockInfo(stockTM);
            controller.setItem(itemsTM);
            controller.item_con = this;
            stage.centerOnScreen();
            stage.show();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setCashiCon(CashierController cashierController) {
        this.cashierController = cashierController;
    }

    public void lodeShodTableData() {
        if (cashierController!=null) {
            cashierController.searchTexfeld.clear();
            cashierController.showTable.getItems().remove(cashierController.showTable.getItems());
            cashierController.showTable.refresh();
            cashierController.lodeTableData();
            cashierController.setSearchFilter();
        }
    }

}
