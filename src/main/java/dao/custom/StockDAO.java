package dao.custom;

import dao.CrudDAO;
import dto.StockDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface StockDAO extends CrudDAO<StockDTO> {
    public StockDTO yetStockIdGetStock(String stockID) throws SQLException, ClassNotFoundException;

}
