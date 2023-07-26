package dao.custom.impl;

import dao.custom.DayAmountDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DayAmountDAOImpl implements DayAmountDAO {

    @Override
    public boolean save(double amount) throws SQLException, ClassNotFoundException {
        String  sql="INSERT INTO dayCashAmount (dte,amount) VALUES(?,?)";
        String s = String.valueOf(amount);
        String s1 = LocalDate.now().toString();
        return CrudUtil.execute(sql,s1,s);
    }

    @Override
    public String getAmount(String day) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("select * from daycashamount where dte = ?", day);
        if (resultSet.next()) {
            return resultSet.getString("amount");
        }
        return "00";
    }

    @Override
    public boolean exist(String day) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM daycashamount WHERE dte = ?";
        ResultSet resultSet = CrudUtil.execute(sql, day);
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    @Override
    public boolean upDate(String text) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE daycashamount SET amount= ? WHERE dte = ?";
        return CrudUtil.execute(sql, text,LocalDate.now().toString());
    }
}
