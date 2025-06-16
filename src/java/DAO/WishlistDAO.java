package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Wishlist;
import model.WishlistProduct;

public class WishlistDAO implements IWishlistDAO {
    private static final String INSERT_WISHLIST = "INSERT INTO Wishlists (UserID) VALUES (?)";
    private static final String INSERT_WISHLIST_PRODUCT = "INSERT INTO Wishlist_Products (WishlistID, ProductID) VALUES (?, ?)";
    private static final String SELECT_WISHLIST = "SELECT * FROM Wishlists WHERE WishlistID = ?";
    private static final String SELECT_WISHLIST_PRODUCTS = "SELECT * FROM Wishlist_Products WHERE WishlistID = ?";
    private static final String SELECT_ALL_WISHLISTS = "SELECT * FROM Wishlists";
    private static final String DELETE_WISHLIST = "DELETE FROM Wishlists WHERE WishlistID = ?";
    private static final String DELETE_WISHLIST_PRODUCTS = "DELETE FROM Wishlist_Products WHERE WishlistID = ?";
    private static final String UPDATE_WISHLIST = "UPDATE Wishlists SET UserID = ? WHERE WishlistID = ?";

    @Override
    public void insertWishlist(Wishlist wishlist, List<WishlistProduct> wishlistProducts) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into Wishlists
            stmt = conn.prepareStatement(INSERT_WISHLIST, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, wishlist.getUserId());
            stmt.executeUpdate();

            // Get generated WishlistID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                wishlist.setWishlistId(rs.getInt(1));
            }

            // Insert into Wishlist_Products
            stmt = conn.prepareStatement(INSERT_WISHLIST_PRODUCT);
            for (WishlistProduct wishlistProduct : wishlistProducts) {
                stmt.setInt(1, wishlist.getWishlistId());
                stmt.setInt(2, wishlistProduct.getProductId());
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
    public Wishlist selectWishlist(int wishlistId) {
        Wishlist wishlist = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_WISHLIST)) {
            stmt.setInt(1, wishlistId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                wishlist = new Wishlist();
                wishlist.setWishlistId(rs.getInt("WishlistID"));
                wishlist.setUserId(rs.getInt("UserID"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return wishlist;
    }

    @Override
    public List<Wishlist> selectAllWishlists() {
        List<Wishlist> wishlists = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_WISHLISTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Wishlist wishlist = new Wishlist();
                wishlist.setWishlistId(rs.getInt("WishlistID"));
                wishlist.setUserId(rs.getInt("UserID"));
                wishlists.add(wishlist);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return wishlists;
    }

    @Override
    public boolean deleteWishlist(int wishlistId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete from Wishlist_Products first
            stmt = conn.prepareStatement(DELETE_WISHLIST_PRODUCTS);
            stmt.setInt(1, wishlistId);
            stmt.executeUpdate();

            // Delete from Wishlists
            stmt = conn.prepareStatement(DELETE_WISHLIST);
            stmt.setInt(1, wishlistId);
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
    public boolean updateWishlist(Wishlist wishlist) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_WISHLIST)) {
            stmt.setInt(1, wishlist.getUserId());
            stmt.setInt(2, wishlist.getWishlistId());
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