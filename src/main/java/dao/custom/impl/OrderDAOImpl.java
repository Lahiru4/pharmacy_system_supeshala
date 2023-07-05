package dao.custom.impl;

import dao.custom.OrderDAO;
import dto.OrdersDTO;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public ArrayList<OrdersDTO> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<OrdersDTO> orders = new ArrayList<>();
        String sql = "SELECT order_Id, tot_qty, order_date, item_cost, profit FROM orders";
        try (ResultSet resultSet = CrudUtil.execute(sql)) {
            while (resultSet.next()) {
                String orderId = resultSet.getString("order_Id");
                int totalQuantity = resultSet.getInt("tot_qty");
                String orderDate = resultSet.getString("order_date");
                double itemCost = resultSet.getDouble("item_cost");
                double profit = resultSet.getDouble("profit");
                OrdersDTO order = new OrdersDTO(orderId, totalQuantity, orderDate, itemCost, profit);
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public boolean add(OrdersDTO order) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO orders (order_Id, tot_qty, order_date, item_cost, profit) " +
                "VALUES (?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, order.getOrderId(), order.getTotalQuantity(),
                order.getOrderDate(), order.getItemCost(), order.getProfit());
    }

    @Override
    public boolean update(OrdersDTO dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT order_Id FROM orders WHERE order_Id = ?";
        ResultSet resultSet = CrudUtil.execute(sql, id);
        return resultSet.next();
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        String sql = "SELECT MAX(CAST(SUBSTRING(order_Id, 4) AS UNSIGNED)) AS max_id_number FROM orders";
        ResultSet resultSet = CrudUtil.execute(sql);

        if (resultSet.next()) {
            String currentMaxID = resultSet.getString(1);
            if (currentMaxID != null) {
                //int currentIDNumber = Integer.parseInt(currentMaxID.substring(3));
                int newIDNumber = Integer.parseInt(currentMaxID) + 1;
                return String.format("ORD%03d", newIDNumber);
            }
        }
        return "ORD001"; // If no existing order IDs found, start with ORD001
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM orders WHERE order_Id = ?";
        return CrudUtil.execute(sql, id);
    }
}
