package dao.custom;

import java.sql.SQLException;

public interface DayAmountDAO {
    public boolean save(double amount) throws SQLException, ClassNotFoundException;
    public String getAmount(String day) throws SQLException, ClassNotFoundException;
    public boolean exist(String day) throws SQLException, ClassNotFoundException;

    boolean upDate(String text) throws SQLException, ClassNotFoundException;
}
