package dao.custom.impl;


import dao.custom.EmployeeDAO;
import dto.EmployeeDTO;
import util.CrudUtil;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmployeeDAOImpl implements EmployeeDAO {
    @Override
    public String getEmployeeID(String employeeName) throws SQLException, ClassNotFoundException {
        String sql = "SELECT employee_id FROM employee WHERE name = ?";

        try {
            ResultSet resultSet = CrudUtil.execute(sql, employeeName);
            if (resultSet.next()) {
                return resultSet.getString("employee_id");
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getEmployeeCount() {
        int count = 0;
        try {
            String sql = "SELECT COUNT(*) AS employeeCount FROM employee";
            ResultSet resultSet = CrudUtil.execute(sql);
            if (resultSet.next()) {
                count = resultSet.getInt("employeeCount");
            }
            resultSet.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public ArrayList<EmployeeDTO> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT employee_id, name, phone_number, register_date, position FROM employee";
        ResultSet resultSet = CrudUtil.execute(sql);
        while (resultSet.next()) {
            String employeeId = resultSet.getString("employee_id");
            String name = resultSet.getString("name");
            String phoneNumber = resultSet.getString("phone_number");
            Date registerDate = resultSet.getDate("register_date");
            String position = resultSet.getString("position");

            EmployeeDTO employee = new EmployeeDTO(employeeId, name, phoneNumber, registerDate, position);
            employees.add(employee);
        }
        return employees;
    }

    @Override
    public boolean add(EmployeeDTO employee) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO employee (employee_id, name, phone_number, register_date, position) " +
                "VALUES (?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, employee.getId(), employee.getName(),
                employee.getPhone(), employee.getRegisterDate(), employee.getPosition());

    }

    @Override
    public boolean update(EmployeeDTO employee) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE employee SET name = ?, phone_number = ?, register_date = ?, position = ? WHERE employee_id = ?";
        return CrudUtil.execute(sql, employee.getName(), employee.getPhone(),
                employee.getRegisterDate(), employee.getPosition(), employee.getId());
    }

    @Override
    public boolean exist(String name) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM employee WHERE name = ?";
        ResultSet resultSet = CrudUtil.execute(sql, name);
        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        String sql = "SELECT MAX(employee_id) FROM employee";
        ResultSet resultSet = CrudUtil.execute(sql);
        if (resultSet.next()) {
            String maxID = resultSet.getString(1);
            if (maxID != null) {
                int idNumber = Integer.parseInt(maxID.substring(1)) + 1;
                String newID = "E" + String.format("%04d", idNumber);
                return newID;
            } else {
                return "E0001"; // If no existing IDs, start from E0001
            }
        }
        return null;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        return CrudUtil.execute(sql, id);
    }
}
