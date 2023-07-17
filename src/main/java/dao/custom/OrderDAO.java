package dao.custom;

import dao.CrudDAO;
import dto.OrdersDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface OrderDAO extends CrudDAO<OrdersDTO> {
    int yetDateGetCount(String s) throws SQLException, ClassNotFoundException;

    double getProfit(String date);
    public double getCost(String date);

    public ArrayList<OrdersDTO> yetDateGetAll(String date)throws SQLException, ClassNotFoundException;
}
