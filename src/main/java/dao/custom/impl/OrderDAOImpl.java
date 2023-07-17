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

    @Override
    public int yetDateGetCount(String s) throws SQLException, ClassNotFoundException {
        String getCountQuery = "SELECT COUNT(*) AS orderCount FROM orders WHERE order_date = ?";
        ResultSet resultSet = CrudUtil.execute(getCountQuery, s);
        int orderCount = 0;
        if (resultSet.next()) {
            orderCount = resultSet.getInt("orderCount");
        }
        resultSet.close();
        return orderCount;
    }

    @Override
    public double getProfit(String date) {
        double totalProfit = 0.0;
        try {
            String sql = "SELECT SUM(profit) AS totalProfit FROM orders WHERE order_date = ?";
            ResultSet resultSet = CrudUtil.execute(sql, date);
            if (resultSet.next()) {
                totalProfit = resultSet.getDouble("totalProfit");
            }
            resultSet.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return totalProfit;
    }

    @Override
    public double getCost(String date) {
        double cost = 0.0;
        try {
            String sql = "SELECT SUM(item_cost) AS totalProfit FROM orders WHERE order_date = ?";
            ResultSet resultSet = CrudUtil.execute(sql, date);
            if (resultSet.next()) {
                cost = resultSet.getDouble("totalProfit");
            }
            resultSet.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cost;
    }

    @Override
    public ArrayList<OrdersDTO> yetDateGetAll(String date) throws SQLException, ClassNotFoundException {
        ArrayList<OrdersDTO> orders = new ArrayList<>();
        String sql = "SELECT order_Id, tot_qty, order_date, item_cost, profit FROM orders where order_date='"+date+"'";
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
}
