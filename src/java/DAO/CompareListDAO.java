package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.CompareList;
import model.CompareListProduct;

public class CompareListDAO implements ICompareListDAO {
    private static final String INSERT_COMPARE_LIST = "INSERT INTO CompareLists (UserID) VALUES (?)";
    private static final String INSERT_COMPARE_LIST_PRODUCT = "INSERT INTO CompareList_Products (CompareListID, ProductID) VALUES (?, ?)";
    private static final String SELECT_COMPARE_LIST = "SELECT * FROM CompareLists WHERE CompareListID = ?";
    private static final String SELECT_COMPARE_LIST_PRODUCTS = "SELECT * FROM CompareList_Products WHERE CompareListID = ?";
    private static final String SELECT_ALL_COMPARE_LISTS = "SELECT * FROM CompareLists";
    private static final String DELETE_COMPARE_LIST = "DELETE FROM CompareLists WHERE CompareListID = ?";
    private static final String DELETE_COMPARE_LIST_PRODUCTS = "DELETE FROM CompareList_Products WHERE CompareListID = ?";
    private static final String UPDATE_COMPARE_LIST = "UPDATE CompareLists SET UserID = ? WHERE CompareListID = ?";

    @Override
    public void insertCompareList(CompareList compareList, List<CompareListProduct> compareListProducts) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into CompareLists
            stmt = conn.prepareStatement(INSERT_COMPARE_LIST, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, compareList.getUserId());
            stmt.executeUpdate();

            // Get generated CompareListID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                compareList.setCompareListId(rs.getInt(1));
            }

            // Insert into CompareList_Products
            stmt = conn.prepareStatement(INSERT_COMPARE_LIST_PRODUCT);
            for (CompareListProduct compareListProduct : compareListProducts) {
                stmt.setInt(1, compareList.getCompareListId());
                stmt.setInt(2, compareListProduct.getProductId());
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
    public CompareList selectCompareList(int compareListId) {
        CompareList compareList = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_COMPARE_LIST)) {
            stmt.setInt(1, compareListId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                compareList = new CompareList();
                compareList.setCompareListId(rs.getInt("CompareListID"));
                compareList.setUserId(rs.getInt("UserID"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return compareList;
    }

    @Override
    public List<CompareList> selectAllCompareLists() {
        List<CompareList> compareLists = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_COMPARE_LISTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CompareList compareList = new CompareList();
                compareList.setCompareListId(rs.getInt("CompareListID"));
                compareList.setUserId(rs.getInt("UserID"));
                compareLists.add(compareList);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return compareLists;
    }

    @Override
    public boolean deleteCompareList(int compareListId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete from CompareList_Products first
            stmt = conn.prepareStatement(DELETE_COMPARE_LIST_PRODUCTS);
            stmt.setInt(1, compareListId);
            stmt.executeUpdate();

            // Delete from CompareLists
            stmt = conn.prepareStatement(DELETE_COMPARE_LIST);
            stmt.setInt(1, compareListId);
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
    public boolean updateCompareList(CompareList compareList) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_COMPARE_LIST)) {
            stmt.setInt(1, compareList.getUserId());
            stmt.setInt(2, compareList.getCompareListId());
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