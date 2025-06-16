package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Cart;
import model.CartProduct;

public class CartDAO implements ICartDAO {
    private static final String INSERT_CART = "INSERT INTO Carts (UserID, Subtotal) VALUES (?, ?)";
    private static final String INSERT_CART_PRODUCT = "INSERT INTO Cart_Products (CartID, ProductID, Quantity) VALUES (?, ?, ?)";
    private static final String SELECT_CART = "SELECT * FROM Carts WHERE CartID = ?";
    private static final String SELECT_CART_PRODUCTS = "SELECT * FROM Cart_Products WHERE CartID = ?";
    private static final String SELECT_ALL_CARTS = "SELECT * FROM Carts";
    private static final String DELETE_CART = "DELETE FROM Carts WHERE CartID = ?";
    private static final String DELETE_CART_PRODUCTS = "DELETE FROM Cart_Products WHERE CartID = ?";
    private static final String UPDATE_CART = "UPDATE Carts SET UserID = ?, Subtotal = ? WHERE CartID = ?";

    @Override
    public void insertCart(Cart cart, List<CartProduct> cartProducts) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into Carts
            stmt = conn.prepareStatement(INSERT_CART, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, cart.getUserId());
            stmt.setDouble(2, cart.getSubtotal());
            stmt.executeUpdate();

            // Get generated CartID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cart.setCartId(rs.getInt(1));
            }

            // Insert into Cart_Products
            stmt = conn.prepareStatement(INSERT_CART_PRODUCT);
            for (CartProduct cartProduct : cartProducts) {
                stmt.setInt(1, cart.getCartId());
                stmt.setInt(2, cartProduct.getProductId());
                stmt.setInt(3, cartProduct.getQuantity());
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
    public Cart selectCart(int cartId) {
        Cart cart = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_CART)) {
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                cart = new Cart();
                cart.setCartId(rs.getInt("CartID"));
                cart.setUserId(rs.getInt("UserID"));
                cart.setSubtotal(rs.getDouble("Subtotal"));
                cart.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return cart;
    }

    @Override
    public List<Cart> selectAllCarts() {
        List<Cart> carts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_CARTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Cart cart = new Cart();
                cart.setCartId(rs.getInt("CartID"));
                cart.setUserId(rs.getInt("UserID"));
                cart.setSubtotal(rs.getDouble("Subtotal"));
                cart.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                carts.add(cart);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return carts;
    }

    @Override
    public boolean deleteCart(int cartId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete from Cart_Products first
            stmt = conn.prepareStatement(DELETE_CART_PRODUCTS);
            stmt.setInt(1, cartId);
            stmt.executeUpdate();

            // Delete from Carts
            stmt = conn.prepareStatement(DELETE_CART);
            stmt.setInt(1, cartId);
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
    public boolean updateCart(Cart cart) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_CART)) {
            stmt.setInt(1, cart.getUserId());
            stmt.setDouble(2, cart.getSubtotal());
            stmt.setInt(3, cart.getCartId());
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