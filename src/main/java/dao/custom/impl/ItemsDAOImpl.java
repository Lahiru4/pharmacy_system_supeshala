package dao.custom.impl;

import dao.custom.ItemsDAO;
import dto.ItemsDTO;
import tdm.BillItemTM;
import tdm.BillTable2;
import util.CrudUtil;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemsDAOImpl implements ItemsDAO {
    @Override
    public ArrayList<ItemsDTO> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<ItemsDTO> itemList = new ArrayList<>();
        String sql = "SELECT * FROM items";
        ResultSet resultSet = CrudUtil.execute(sql);
        while (resultSet.next()) {
            String itemCode = resultSet.getString("item_code");
            String itemDescription = resultSet.getString("item_description");
            int qty = resultSet.getInt("qty");
            double sellingPrice = resultSet.getDouble("selling_price");
            double purchasePrice = resultSet.getDouble("purchase_price");
            String supplierID = resultSet.getString("stock_id");
            String expirationDate = resultSet.getString("expiration_date");
            ItemsDTO item = new ItemsDTO(itemCode, itemDescription, qty, sellingPrice, purchasePrice,
                    supplierID, expirationDate);
            itemList.add(item);
        }
        return itemList;
    }

    public String getOldItemQty(BillTable2 items) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtil.execute("SELECT qty FROM items WHERE item_code=?", items.getItemCode());
        if (rst.next()) {
            return rst.getString(1);
        }
        return null;
    }

    @Override
    public boolean add(ItemsDTO item) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO items (item_code, item_description, qty, selling_price, " +
                "purchase_price, stock_id, expiration_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, item.getItemCode(), item.getItemDescription(), item.getQty(),
                item.getSellingPrice(), item.getPurchasePrice(), item.getStockID(),
                item.getExpirationDate());
    }

    @Override
    public boolean add(List<ItemsDTO> items) throws SQLException, ClassNotFoundException {
        for (ItemsDTO ob : items) {
            boolean exist = exist(ob.getItemCode());
            if (!add(ob)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(ItemsDTO item) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE items SET item_description = ?, qty = ?, selling_price = ?, purchase_price = ?, " +
                "stock_id = ?, expiration_date = ?  WHERE item_code = ?";
        return CrudUtil.execute(sql, item.getItemDescription(), item.getQty(), item.getSellingPrice(),
                item.getPurchasePrice(), item.getStockID(),
                item.getExpirationDate(), item.getItemCode());
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM items WHERE item_code = ?";
        ResultSet resultSet = CrudUtil.execute(sql, id);
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        String prefix = "ITM";
        String sql = "SELECT MAX(CAST(SUBSTRING(item_code, 4) AS UNSIGNED)) AS max_id_number " +
                "FROM items;";
        ResultSet resultSet = CrudUtil.execute(sql);
        if (resultSet.next()) {
            String maxId = resultSet.getString(1);
            if (maxId != null) {
                int numericPart = Integer.parseInt(maxId);
                int newNumericPart = numericPart + 1;
                return prefix + String.format("%03d", newNumericPart);
            }
        }

        return prefix + "001";
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM items WHERE item_code = ?";
        return CrudUtil.execute(sql, id);
    }

    public boolean updateQtyOnly(String itemCode, int newQty) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE items SET qty = ? WHERE item_code = ?";
        return CrudUtil.execute(sql, newQty, itemCode);
    }
    @Override
    public String generateNewID(int newNum) throws SQLException, ClassNotFoundException {
        String prefix = "ITM";
        String sql = "SELECT MAX(CAST(SUBSTRING(item_code, 4) AS UNSIGNED)) AS max_id_number " +
                "FROM items;";
        ResultSet resultSet = CrudUtil.execute(sql);
        if (resultSet.next()) {
            String maxID = resultSet.getString(1);
            if (maxID != null) {
                int numericPart = Integer.parseInt(maxID);
                int newNumericPart = ((numericPart + 1) + newNum);
                return prefix + String.format("%03d", newNumericPart);
            }
        }
        return prefix + "00" + (newNum + 1);
    }
    @Override
    public ArrayList<ItemsDTO> yetStockId_getAll(String stock_id) throws SQLException, ClassNotFoundException {
        ArrayList<ItemsDTO> itemList = new ArrayList<>();
        String sql = "SELECT * FROM items where stock_id=?";
        ResultSet resultSet = CrudUtil.execute(sql, stock_id);
        while (resultSet.next()) {
            String itemCode = resultSet.getString("item_code");
            String itemDescription = resultSet.getString("item_description");
            int qty = resultSet.getInt("qty");
            double sellingPrice = resultSet.getDouble("selling_price");
            double purchasePrice = resultSet.getDouble("purchase_price");
            String supplierID = resultSet.getString("stock_id");
            String expirationDate = resultSet.getString("expiration_date");

            ItemsDTO item = new ItemsDTO(itemCode, itemDescription, qty, sellingPrice, purchasePrice,
                    supplierID, expirationDate);
            itemList.add(item);
        }
        return itemList;
    }
    @Override
    public ItemsDTO yetCodeGetAll(String code) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM items WHERE item_code = ?";
        try (ResultSet resultSet = CrudUtil.execute(sql, code)) {
            while (resultSet.next()) {
                String itemCode = resultSet.getString("item_code");
                String itemDescription = resultSet.getString("item_description");
                int quantity = resultSet.getInt("qty");
                double sellingPrice = resultSet.getDouble("selling_price");
                double purchasePrice = resultSet.getDouble("Purchase_price");
                String stockId = resultSet.getString("stock_id");
                Date expirationDate = resultSet.getDate("expiration_date");
                return new ItemsDTO(itemCode, itemDescription, quantity, sellingPrice, purchasePrice
                        , stockId, expirationDate + "");
            }
        }
        return null;
    }

    @Override
    public int getStockCount() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) AS stockCount FROM items";
            ResultSet resultSet = CrudUtil.execute(sql);
            if (resultSet.next()) {
                count = resultSet.getInt("stockCount");
            }
            resultSet.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }
}