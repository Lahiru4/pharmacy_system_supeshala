package dao.custom;

import dao.CrudDAO;
import dto.ItemsDTO;
import tdm.BillItemTM;
import tdm.BillTable2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ItemsDAO extends CrudDAO<ItemsDTO> {

    public boolean add(List<ItemsDTO> items) throws SQLException, ClassNotFoundException;
    public String getOldItemQty(BillTable2 items) throws SQLException, ClassNotFoundException;
    public boolean updateQtyOnly(String itemCode, int newQty) throws SQLException, ClassNotFoundException;
    public String generateNewID(int newNum) throws SQLException, ClassNotFoundException;
    public ArrayList<ItemsDTO> yetStockId_getAll(String stock_id) throws SQLException, ClassNotFoundException;
    public ItemsDTO yetCodeGetAll(String code) throws SQLException, ClassNotFoundException;

    int getStockCount();
}
