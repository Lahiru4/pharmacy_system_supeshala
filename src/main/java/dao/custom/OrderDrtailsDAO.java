package dao.custom;

import dao.CrudDAO;
import dto.ItemsDTO;
import dto.OrderDetailsDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface  OrderDrtailsDAO extends CrudDAO<OrderDetailsDTO> {
    public String getItemsCount(String id) throws SQLException, ClassNotFoundException;
    public ArrayList<ItemsDTO> yetOrderIdGetItems(String orderId) throws SQLException, ClassNotFoundException;

}
