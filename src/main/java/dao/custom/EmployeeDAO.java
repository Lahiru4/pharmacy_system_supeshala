package dao.custom;

import dao.CrudDAO;
import dto.EmployeeDTO;


import java.sql.SQLException;

public interface EmployeeDAO extends CrudDAO<EmployeeDTO> {
    public String getEmployeeName(String employeeName) throws SQLException, ClassNotFoundException;

    int getEmployeeCount();
}
