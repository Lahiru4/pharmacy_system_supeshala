package dao.custom;

import dao.CrudDAO;
import dto.SupplierDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface SupplyerDAO extends CrudDAO<SupplierDTO> {
    public ArrayList<String> getSupPosition() throws SQLException, ClassNotFoundException;
    public ArrayList<SupplierDTO> yetPositionGetSup(String position ) throws SQLException, ClassNotFoundException;
}
