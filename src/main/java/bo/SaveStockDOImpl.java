package bo;

import dao.custom.ItemsDAO;
import dao.custom.StockDAO;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.StockDAOImpl;
import db.DbConnection;
import dto.ItemsDTO;
import dto.StockDTO;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;

public class SaveStockDOImpl implements SaveStockDO {
    public StockDAO stockDAO = new StockDAOImpl();
    public ItemsDAO itemsDAO = new ItemsDAOImpl();

    @Override
    public boolean save(StockDTO stockDTO, ObservableList<ItemsDTO> itemsDTOS) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        if (itemsDTOS.size() < 1) {
            return false;
        }
        connection.setAutoCommit(false);
        try {
            boolean check_opt = stockDAO.exist(stockDTO.getId());
            if (!check_opt) {
                boolean add_stock = stockDAO.add(stockDTO);
                if (add_stock) {
                    boolean isAllAdded = itemsDAO.add(itemsDTOS);
                    if(isAllAdded){
                        connection.commit();
                        return true;
                    }else {
                        connection.rollback();
                        return false;
                    }
                }
            }else {
                boolean isAllAdded = itemsDAO.add(itemsDTOS);
                if(isAllAdded){
                    connection.commit();
                    return true;
                }else {
                    connection.rollback();
                    return false;
                }
            }
            connection.rollback();
        } catch (Exception e) {
            connection.rollback();
        }finally {
            connection.setAutoCommit(true);
        }
        return false;
    }
}
