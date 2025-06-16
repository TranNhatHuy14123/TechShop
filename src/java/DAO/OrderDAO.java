package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Order;
import model.OrderProduct;

public class OrderDAO implements IOrderDAO {
    private static final String INSERT_ORDER = "INSERT INTO Orders (UserID, Total, BillingAddressID, ShippingAddressID, OrderNotes, PaymentMethodID, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_ORDER_PRODUCT = "INSERT INTO Order_Products (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
    private static final String SELECT_ORDER = "SELECT * FROM Orders WHERE OrderID = ?";
    private static final String SELECT_ORDER_PRODUCTS = "SELECT * FROM Order_Products WHERE OrderID = ?";
    private static final String SELECT_ALL_ORDERS = "SELECT * FROM Orders";
    private static final String DELETE_ORDER = "DELETE FROM Orders WHERE OrderID = ?";
    private static final String DELETE_ORDER_PRODUCTS = "DELETE FROM Order_Products WHERE OrderID = ?";
    private static final String UPDATE_ORDER = "UPDATE Orders SET UserID = ?, Total = ?, BillingAddressID = ?, ShippingAddressID = ?, OrderNotes = ?, PaymentMethodID = ?, Status = ? WHERE OrderID = ?";

    @Override
    public void insertOrder(Order order, List<OrderProduct> orderProducts) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into Orders
            stmt = conn.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getUserId());
            stmt.setDouble(2, order.getTotal());
            stmt.setInt(3, order.getBillingAddressId());
            if (order.getShippingAddressId() != null) {
                stmt.setInt(4, order.getShippingAddressId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, order.getOrderNotes());
            stmt.setInt(6, order.getPaymentMethodId());
            stmt.setString(7, order.getStatus());
            stmt.executeUpdate();

            // Get generated OrderID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
            }

            // Insert into Order_Products
            stmt = conn.prepareStatement(INSERT_ORDER_PRODUCT);
            for (OrderProduct orderProduct : orderProducts) {
                stmt.setInt(1, order.getOrderId());
                stmt.setInt(2, orderProduct.getProductId());
                stmt.setInt(3, orderProduct.getQuantity());
                stmt.addBatch();
            }
            stmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    printSQLException(ex);
                }
            }
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public Order selectOrder(int orderId) {
        Order order = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ORDER)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setOrderId(rs.getInt("OrderID"));
                order.setUserId(rs.getInt("UserID"));
                order.setTotal(rs.getDouble("Total"));
                order.setBillingAddressId(rs.getInt("BillingAddressID"));
                int shippingAddressId = rs.getInt("ShippingAddressID");
                if (!rs.wasNull()) {
                    order.setShippingAddressId(shippingAddressId);
                }
                order.setOrderNotes(rs.getString("OrderNotes"));
                order.setPaymentMethodId(rs.getInt("PaymentMethodID"));
                order.setStatus(rs.getString("Status"));
                order.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return order;
    }

    @Override
    public List<Order> selectAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ORDERS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderID"));
                order.setUserId(rs.getInt("UserID"));
                order.setTotal(rs.getDouble("Total"));
                order.setBillingAddressId(rs.getInt("BillingAddressID"));
                int shippingAddressId = rs.getInt("ShippingAddressID");
                if (!rs.wasNull()) {
                    order.setShippingAddressId(shippingAddressId);
                }
                order.setOrderNotes(rs.getString("OrderNotes"));
                order.setPaymentMethodId(rs.getInt("PaymentMethodID"));
                order.setStatus(rs.getString("Status"));
                order.setCreatedAt(rs.getTimestamp("CreatedAt"));
                orders.add(order);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return orders;
    }

    @Override
    public boolean deleteOrder(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete from Order_Products first
            stmt = conn.prepareStatement(DELETE_ORDER_PRODUCTS);
            stmt.setInt(1, orderId);
            stmt.executeUpdate();

            // Delete from Orders
            stmt = conn.prepareStatement(DELETE_ORDER);
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();

            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    printSQLException(ex);
                }
            }
            throw e;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public boolean updateOrder(Order order) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_ORDER)) {
            stmt.setInt(1, order.getUserId());
            stmt.setDouble(2, order.getTotal());
            stmt.setInt(3, order.getBillingAddressId());
            if (order.getShippingAddressId() != null) {
                stmt.setInt(4, order.getShippingAddressId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, order.getOrderNotes());
            stmt.setInt(6, order.getPaymentMethodId());
            stmt.setString(7, order.getStatus());
            stmt.setInt(8, order.getOrderId());
            return stmt.executeUpdate() > 0;
        }
    }

    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}