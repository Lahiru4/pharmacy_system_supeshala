package dao.custom.impl;

import dao.custom.OrderDrtailsDAO;
import dto.ItemsDTO;
import dto.OrderDetailsDTO;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailsDAOImpl implements OrderDrtailsDAO {
    @Override
    public ArrayList<OrderDetailsDTO> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<OrderDetailsDTO> orderDetailsList = new ArrayList<>();
        String sql = "SELECT * FROM orderdetails";
        ResultSet resultSet = CrudUtil.execute(sql);

        while (resultSet.next()) {
            String orderId = resultSet.getString("order_Id");
            String itemCode = resultSet.getString("item_code");
            String name = resultSet.getString("name");
            int qty = resultSet.getInt("qty");
            double sellingPrice = resultSet.getDouble("selling_price");
            double netTotal = resultSet.getDouble("net_tot");

            OrderDetailsDTO orderDetails = new OrderDetailsDTO(orderId, itemCode, name, qty, sellingPrice, netTotal);
            orderDetailsList.add(orderDetails);
        }

        return orderDetailsList;
    }

    @Override
    public boolean add(OrderDetailsDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO orderdetails (order_Id, item_code, name, qty, selling_price, net_tot) VALUES (?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getOrderId(), dto.getItemCode(), dto.getName(), dto.getQuantity(), dto.getSellingPrice(), dto.getNetTotal());
    }

    @Override
    public boolean update(OrderDetailsDTO dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        System.out.println(id);
        String sql = "DELETE FROM orderdetails WHERE order_Id = ?";
        return CrudUtil.execute(sql, id);
    }
    @Override
    public String getItemsCount(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(order_Id) FROM orderdetails WHERE order_Id=? GROUP BY order_Id;";
        ResultSet execute = CrudUtil.execute(sql, id);
        if (execute.next()) {
            return execute.getString(1);
        }
        return null;
    }

    @Override
    public ArrayList<ItemsDTO> yetOrderIdGetItems(String orderId) throws SQLException, ClassNotFoundException {
        ArrayList<ItemsDTO> items = new ArrayList<>();
        String sql = "SELECT * FROM orderdetails WHERE order_Id = ?";
        try (ResultSet resultSet = CrudUtil.execute(sql, orderId)) {
            while (resultSet.next()) {
                String itemCode = resultSet.getString("item_code");
                String name = resultSet.getString("name");
                int qty = resultSet.getInt("qty");
                double selling_price = resultSet.getDouble("selling_price");
                items.add(new ItemsDTO(itemCode,name,qty,selling_price,00,"",""));
            }
        }
        return items;
    }
}
