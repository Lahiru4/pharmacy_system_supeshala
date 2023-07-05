package dao.custom.impl;

import dao.custom.StockDAO;
import dto.StockDTO;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockDAOImpl implements StockDAO {
    @Override
    public ArrayList<StockDTO> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<StockDTO> stocks = new ArrayList<>();
        String sql = "SELECT id, importer, area_distributor, sales_represent, agency, total_qty, total_Purchase_Price, stock_date FROM stock";
        try (ResultSet resultSet = CrudUtil.execute(sql)) {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String importer = resultSet.getString("importer");
                String areaDistributor = resultSet.getString("area_distributor");
                String salesRepresent = resultSet.getString("sales_represent");
                String agency = resultSet.getString("agency");
                int totalQty = resultSet.getInt("total_qty");
                int totalPurchasePrice = resultSet.getInt("total_Purchase_Price");
                String stockDate = resultSet.getString("stock_date");
                StockDTO stock = new StockDTO(id, importer, areaDistributor, salesRepresent, agency, totalQty, totalPurchasePrice, stockDate);
                stocks.add(stock);
            }
        }
        return stocks;
    }
    @Override
    public boolean add(StockDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO stock (id, importer, area_distributor, sales_represent, agency, total_qty, total_Purchase_Price, stock_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        boolean execute = CrudUtil.execute(sql, dto.getId(), dto.getImporter(), dto.getAreaDistributor(), dto.getSalesRepresent(), dto.getAgency(), dto.getTotalQty(), dto.getTotalPurchasePrice(), dto.getStockDate());
        return execute;
    }
    @Override
    public boolean update(StockDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE stock SET importer = ?, area_distributor = ?, sales_represent = ?, agency = ?, `total _qty` = ?, total_Purchase_Price = ?, stock_date = ? WHERE id = ?";
        int affectedRows = CrudUtil.execute(sql, dto.getImporter(), dto.getAreaDistributor(), dto.getSalesRepresent(), dto.getAgency(), dto.getTotalQty(), dto.getTotalPurchasePrice(), dto.getStockDate(), dto.getId());
        return affectedRows > 0;
    }
    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT id FROM stock WHERE id = ?";
        ResultSet resultSet = CrudUtil.execute(sql, id);
        return resultSet.next();
    }
    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        String prefix="S";
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) AS max_id_number " +
                "FROM stock;";
        ResultSet resultSet = CrudUtil.execute(sql);

        if (resultSet.next()) {
            String maxId = resultSet.getString(1);
            if (maxId != null) {
                int numericPart = Integer.parseInt(maxId);
                int newNumericPart = numericPart + 1;
                return prefix + String.format("%04d", newNumericPart);
            }
        }
        return prefix + "0001";
    }
    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM stock WHERE id = ?";
        return CrudUtil.execute(sql, id);
    }
    @Override
    public StockDTO yetStockIdGetStock(String stockID) throws SQLException, ClassNotFoundException {

        StockDTO stockDTO = new StockDTO();
        // Prepare the SQL query
        String query = "SELECT * FROM stock where id=?";

        // Execute the query using CrudUtil
        ResultSet resultSet = CrudUtil.execute(query,stockID);

        // Iterate over the result set and populate the stockList
        if (resultSet.next()) {
            String id = resultSet.getString("id");
            String importer = resultSet.getString("importer");
            String areaDistributor = resultSet.getString("area_distributor");
            String salesRepresent = resultSet.getString("sales_represent");
            String agency = resultSet.getString("agency");
            int totalQty = resultSet.getInt("total_qty");
            double totalPurchasePrice = resultSet.getDouble("total_Purchase_Price");
            String stockDate = resultSet.getString("stock_date");

            // Create a new StockDTO object and add it to the stockList
            stockDTO= new StockDTO(id, importer, areaDistributor, salesRepresent, agency, totalQty, totalPurchasePrice, stockDate);
        }
        // Close the result set
        resultSet.close();
        // Return the populated stockList
        return stockDTO;
    }
}
