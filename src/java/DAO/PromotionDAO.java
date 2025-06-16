package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Promotion;
import model.PromotionProduct;

public class PromotionDAO implements IPromotionDAO {
    private static final String INSERT_PROMOTION = "INSERT INTO Promotions (Name, Description, DiscountPercentage, StartDate, EndDate) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_PROMOTION_PRODUCT = "INSERT INTO Promotion_Products (PromotionID, ProductID) VALUES (?, ?)";
    private static final String SELECT_PROMOTION = "SELECT * FROM Promotions WHERE PromotionID = ?";
    private static final String SELECT_PROMOTION_PRODUCTS = "SELECT * FROM Promotion_Products WHERE PromotionID = ?";
    private static final String SELECT_ALL_PROMOTIONS = "SELECT * FROM Promotions";
    private static final String DELETE_PROMOTION = "DELETE FROM Promotions WHERE PromotionID = ?";
    private static final String DELETE_PROMOTION_PRODUCTS = "DELETE FROM Promotion_Products WHERE PromotionID = ?";
    private static final String UPDATE_PROMOTION = "UPDATE Promotions SET Name = ?, Description = ?, DiscountPercentage = ?, StartDate = ?, EndDate = ? WHERE PromotionID = ?";

    @Override
    public void insertPromotion(Promotion promotion, List<PromotionProduct> promotionProducts) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert into Promotions
            stmt = conn.prepareStatement(INSERT_PROMOTION, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, promotion.getName());
            stmt.setString(2, promotion.getDescription());
            stmt.setDouble(3, promotion.getDiscount());
            stmt.setDate(4, new java.sql.Date(promotion.getStartDate().getTime()));
            stmt.setDate(5, new java.sql.Date(promotion.getEndDate().getTime()));
            stmt.executeUpdate();

            // Get generated PromotionID
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                promotion.setPromotionId(rs.getInt(1));
            }

            // Insert into Promotion_Products
            stmt = conn.prepareStatement(INSERT_PROMOTION_PRODUCT);
            for (PromotionProduct promotionProduct : promotionProducts) {
                stmt.setInt(1, promotion.getPromotionId());
                stmt.setInt(2, promotionProduct.getProductId());
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
    public Promotion selectPromotion(int promotionId) {
        Promotion promotion = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_PROMOTION)) {
            stmt.setInt(1, promotionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("PromotionID"));
                promotion.setName(rs.getString("Name"));
                promotion.setDescription(rs.getString("Description"));
                promotion.setDiscount(rs.getDouble("DiscountPercentage"));
                promotion.setStartDate(rs.getTimestamp("StartDate"));
                promotion.setEndDate(rs.getTimestamp("EndDate"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return promotion;
    }

    @Override
    public List<Promotion> selectAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PROMOTIONS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("PromotionID"));
                promotion.setName(rs.getString("Name"));
                promotion.setDescription(rs.getString("Description"));
                promotion.setDiscount(rs.getDouble("DiscountPercentage"));
                promotion.setStartDate(rs.getTimestamp("StartDate"));
                promotion.setEndDate(rs.getTimestamp("EndDate"));
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return promotions;
    }

    @Override
    public boolean deletePromotion(int promotionId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete from Promotion_Products first
            stmt = conn.prepareStatement(DELETE_PROMOTION_PRODUCTS);
            stmt.setInt(1, promotionId);
            stmt.executeUpdate();

            // Delete from Promotions
            stmt = conn.prepareStatement(DELETE_PROMOTION);
            stmt.setInt(1, promotionId);
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
    public boolean updatePromotion(Promotion promotion) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_PROMOTION)) {
            stmt.setString(1, promotion.getName());
            stmt.setString(2, promotion.getDescription());
            stmt.setDouble(3, promotion.getDiscount());
            stmt.setDate(4, new java.sql.Date(promotion.getStartDate().getTime()));
            stmt.setDate(5, new java.sql.Date(promotion.getEndDate().getTime()));
            stmt.setInt(6, promotion.getPromotionId());
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