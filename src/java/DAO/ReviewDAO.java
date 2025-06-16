package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Review;

public class ReviewDAO implements IReviewDAO {
    private static final String INSERT_REVIEW = "INSERT INTO Reviews (ProductID, UserID, Rating, Comment) VALUES (?, ?, ?, ?)";
    private static final String SELECT_REVIEW = "SELECT * FROM Reviews WHERE ReviewID = ?";
    private static final String SELECT_ALL_REVIEWS = "SELECT * FROM Reviews";
    private static final String DELETE_REVIEW = "DELETE FROM Reviews WHERE ReviewID = ?";
    private static final String UPDATE_REVIEW = "UPDATE Reviews SET ProductID = ?, UserID = ?, Rating = ?, Comment = ? WHERE ReviewID = ?";

    @Override
    public void insertReview(Review review) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_REVIEW)) {
            stmt.setInt(1, review.getProductId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.executeUpdate();
        }
    }

    @Override
    public Review selectReview(int reviewId) {
        Review review = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_REVIEW)) {
            stmt.setInt(1, reviewId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                review = new Review();
                review.setReviewId(rs.getInt("ReviewID"));
                review.setProductId(rs.getInt("ProductID"));
                review.setUserId(rs.getInt("UserID"));
                review.setRating(rs.getInt("Rating"));
                review.setComment(rs.getString("Comment"));
                review.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return review;
    }

    @Override
    public List<Review> selectAllReviews() {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_REVIEWS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("ReviewID"));
                review.setProductId(rs.getInt("ProductID"));
                review.setUserId(rs.getInt("UserID"));
                review.setRating(rs.getInt("Rating"));
                review.setComment(rs.getString("Comment"));
                review.setCreatedAt(rs.getTimestamp("CreatedAt"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return reviews;
    }

    @Override
    public boolean deleteReview(int reviewId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_REVIEW)) {
            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateReview(Review review) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_REVIEW)) {
            stmt.setInt(1, review.getProductId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.setInt(5, review.getReviewId());
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