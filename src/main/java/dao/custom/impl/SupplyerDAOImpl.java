package dao.custom.impl;

import dao.custom.SupplyerDAO;
import dto.SupplierDTO;
import util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SupplyerDAOImpl implements SupplyerDAO {
    @Override
    public ArrayList<SupplierDTO> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<SupplierDTO> suppliers = new ArrayList<>();
        String sql = "SELECT sup_id, name, position, phoneNumber, address FROM supplier";
        try (ResultSet resultSet = CrudUtil.execute(sql)) {
            while (resultSet.next()) {
                String supId = resultSet.getString("sup_id");
                String name = resultSet.getString("name");
                String position = resultSet.getString("position");
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");
                SupplierDTO supplier = new SupplierDTO(supId, name, position, phoneNumber, address);
                suppliers.add(supplier);
            }
        }
        return suppliers;
    }

    @Override
    public boolean add(SupplierDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO supplier (sup_id, name, position, phoneNumber, address) VALUES (?, ?, ?, ?, ?)";
        return CrudUtil.execute(sql, dto.getSupId(), dto.getName(), dto.getPosition(), dto.getPhoneNumber(), dto.getAddress());
    }

    @Override
    public boolean update(SupplierDTO dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE supplier SET name = ?, position = ?, phoneNumber = ?, address = ? WHERE sup_id = ?";
        return CrudUtil.execute(sql, dto.getName(), dto.getPosition(), dto.getPhoneNumber(), dto.getAddress(), dto.getSupId());
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT sup_id FROM supplier WHERE sup_id = ?";
        ResultSet resultSet = CrudUtil.execute(sql, id);
        return resultSet.next();
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        String newId = null;
        String sql = "SELECT MAX(sup_id) FROM supplier";
        try (ResultSet resultSet = CrudUtil.execute(sql)) {
            if (resultSet.next()) {
                String maxId = resultSet.getString(1);
                if (maxId != null) {
                    int maxIdNumber = Integer.parseInt(maxId.substring(1));
                    newId = "S" + String.format("%04d", maxIdNumber + 1);
                } else {
                    newId = "S0001";
                }
            }
        }
        return newId;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM supplier WHERE sup_id = ?";
        return CrudUtil.execute(sql, id);
    }

    @Override
    public ArrayList<String> getSupPosition() throws SQLException, ClassNotFoundException {
        ArrayList<String> positions = new ArrayList<>();
        String sql = "SELECT name FROM sup_position";
        ResultSet resultSet = CrudUtil.execute(sql);
        while (resultSet.next()) {
            String position = resultSet.getString("name");
            positions.add(position);
        }
        return positions;
    }

    @Override
    public ArrayList<SupplierDTO> yetPositionGetSup(String position ) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM supplier WHERE position = ?";
        ResultSet resultSet = CrudUtil.execute(sql, position);
        ArrayList<SupplierDTO> supplierDTOS=new ArrayList<>();
        while (resultSet.next()) {
            SupplierDTO supplierDTO = new SupplierDTO();
            supplierDTO.setSupId(resultSet.getString("sup_id"));
            supplierDTO.setName(resultSet.getString("name"));
            supplierDTO.setPosition(resultSet.getString("position"));
            supplierDTO.setPhoneNumber(resultSet.getString("phoneNumber"));
            supplierDTO.setAddress(resultSet.getString("address"));
            supplierDTOS.add(supplierDTO);
        }
        return supplierDTOS;
    }
}
