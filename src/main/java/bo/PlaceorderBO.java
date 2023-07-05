package bo;

import dto.ItemsDTO;
import dto.OrdersDTO;
import javafx.collections.ObservableList;
import tdm.BillItemTM;
import tdm.BillTable2;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PlaceorderBO {
    public boolean saverOrder(OrdersDTO ordersDTO, ObservableList<BillTable2> items) throws SQLException, ClassNotFoundException;
    public boolean returnOrder(OrdersDTO ordersDTO, ArrayList<ItemsDTO> items) throws SQLException, ClassNotFoundException;
}
