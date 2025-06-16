package DAO;

import model.Order;
import model.OrderProduct;
import java.util.List;
import java.sql.SQLException;

public interface IOrderDAO {
    void insertOrder(Order order, List<OrderProduct> orderProducts) throws SQLException;
    Order selectOrder(int orderId);
    List<Order> selectAllOrders();
    boolean deleteOrder(int orderId) throws SQLException;
    boolean updateOrder(Order order) throws SQLException;
}