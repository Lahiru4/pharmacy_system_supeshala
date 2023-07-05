package bo;

import dto.ItemsDTO;
import dto.StockDTO;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;

public interface SaveStockDO {
    public boolean save(StockDTO stockDTO, ObservableList<ItemsDTO> itemsDTOS) throws SQLException, ClassNotFoundException;
}
