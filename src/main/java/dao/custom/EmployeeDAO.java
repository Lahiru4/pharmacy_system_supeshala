package dao.custom;

import dao.CrudDAO;
import dto.EmployeeDTO;


import java.sql.SQLException;

public interface EmployeeDAO extends CrudDAO<EmployeeDTO> {
    public String getEmployeeID(String employeeName) throws SQLException, ClassNotFoundException;

    int getEmployeeCount();
}
