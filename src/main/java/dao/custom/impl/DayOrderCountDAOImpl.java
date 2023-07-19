package dao.custom.impl;

import dao.custom.DayOrderCountDAO;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DayOrderCountDAOImpl implements DayOrderCountDAO {
    @Override
    public String generateNewID(String date) throws SQLException, ClassNotFoundException {
        String sql = "SELECT MAX(CAST(SUBSTRING(number,1) AS UNSIGNED)) AS max_id_number FROM day_orderCount where date=?";
        ResultSet resultSet = CrudUtil.execute(sql,date);

        if (resultSet.next()) {
            String currentMaxID = resultSet.getString(1);
            if (currentMaxID != null) {
                //int currentIDNumber = Integer.parseInt(currentMaxID.substring(3));
                int newIDNumber = Integer.parseInt(currentMaxID) + 1;
                return String.format(newIDNumber+"");
            }
        }
        return "01"; // If no existing order IDs found, start with ORD001
    }

    @Override
    public boolean add(String id, String date,String or_id) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO day_orderCount (number, date,or_id) " +
                "VALUES (?, ?,?)";
        return CrudUtil.execute(sql,id,date,or_id);
    }

    @Override
    public boolean delete(String or_id) throws SQLException, ClassNotFoundException {
        String sql="DELETE FROM day_orderCount WHERE or_id = ?";
        return  CrudUtil.execute(sql,or_id);
    }

    @Override
    public String geAll(String or_id) throws SQLException, ClassNotFoundException {
        String s = "select * from day_ordercount where or_id=?";
        ResultSet resultSet = CrudUtil.execute(s,or_id);
        if (resultSet.next()) {
            String number = resultSet.getString(1);
            return number;
        }
        return "0";
    }
}
