package dao.custom;

import java.sql.SQLException;

public interface DayOrderCountDAO {
    public String generateNewID(String date) throws SQLException, ClassNotFoundException;
    public boolean add(String id,String date,String or_id) throws SQLException, ClassNotFoundException;
    public boolean delete(String or_id) throws SQLException, ClassNotFoundException;
    public String geAll(String or_id) throws SQLException, ClassNotFoundException;
}
