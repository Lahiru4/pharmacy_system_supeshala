package bo;

import dao.custom.DayOrderCountDAO;
import dao.custom.ItemsDAO;
import dao.custom.OrderDAO;
import dao.custom.OrderDrtailsDAO;
import dao.custom.impl.DayOrderCountDAOImpl;
import dao.custom.impl.ItemsDAOImpl;
import dao.custom.impl.OrderDAOImpl;
import dao.custom.impl.OrderDetailsDAOImpl;
import db.DbConnection;
import dto.ItemsDTO;
import dto.OrderDetailsDTO;
import dto.OrdersDTO;
import javafx.collections.ObservableList;
import tdm.BillItemTM;
import tdm.BillTable2;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class PlaceorderBOImpl implements PlaceorderBO {
    public OrderDrtailsDAO orderDrtailsDAO = new OrderDetailsDAOImpl();
    public OrderDAO orderDAO = new OrderDAOImpl();
    public ItemsDAO itemsDAO = new ItemsDAOImpl();

    public boolean saverOrder(OrdersDTO ordersDTO, ObservableList<BillTable2> items) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();

        boolean check_opt = orderDAO.exist(ordersDTO.getOrderId());
        if (check_opt) {
            return false;
        }
        connection.setAutoCommit(false);
        //save order
        boolean add = orderDAO.add(ordersDTO);
        if (!add) {
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }
        //save orderDetales
        for (BillTable2 temp : items) {
            double netTot = temp.getQty() * temp.getSelling_price();
            boolean saveOrderDetalesOpt = orderDrtailsDAO.add(new OrderDetailsDTO(ordersDTO.getOrderId(), temp.getItemCode(), temp.getDescription(), temp.getQty(), temp.getSelling_price(), netTot));
            if (!saveOrderDetalesOpt) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            int OldItemQty = Integer.parseInt(itemsDAO.getOldItemQty(temp));
            int newItemQty = OldItemQty - temp.getQty();
            boolean updateQtyOnlyOPT = itemsDAO.updateQtyOnly(temp.getItemCode(), newItemQty);
            if (!updateQtyOnlyOPT) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            DayOrderCountDAO dayOrderCountDAO = new DayOrderCountDAOImpl();
            String newID = dayOrderCountDAO.generateNewID(LocalDate.now().toString());
            boolean add1 = dayOrderCountDAO.add(newID, LocalDate.now().toString(), ordersDTO.getOrderId());
            if (!add1) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }


        }
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }

    @Override
    public boolean returnOrder(OrdersDTO ordersDTO, ArrayList<ItemsDTO> items) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        boolean check_opt = orderDAO.exist(ordersDTO.getOrderId());
        connection.setAutoCommit(false);
        if (check_opt) {
            DayOrderCountDAO dayOrderCountDAO = new DayOrderCountDAOImpl();
            boolean delete1 = dayOrderCountDAO.delete(ordersDTO.getOrderId());
            if (!delete1) {
                System.out.println("fuck new id");
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            for (ItemsDTO temp : items) {
                BillTable2 billItemTM = new BillTable2(temp.getItemDescription(), temp.getQty(), temp.getSellingPrice(),
                        temp.getItemCode(), temp.getPurchasePrice(),temp.getSellingPrice()* temp.getQty());

                String oldItemQty = itemsDAO.getOldItemQty(billItemTM);
                int newQty = (Integer.parseInt(oldItemQty) + temp.getQty());
                boolean b = itemsDAO.updateQtyOnly(temp.getItemCode(), newQty);
                if (!b) {
                    System.out.println("1fuck");
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }
            boolean delete2 = orderDrtailsDAO.delete(ordersDTO.getOrderId());
            if (!delete2) {
                System.out.println("2fuck");
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            if (delete2) {
                boolean delete = orderDAO.delete(ordersDTO.getOrderId());
                if (!delete) {
                    System.out.println("3fuck");
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }
}
