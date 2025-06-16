package DAO;

import model.Review;
import java.util.List;
import java.sql.SQLException;

public interface IReviewDAO {
    void insertReview(Review review) throws SQLException;
    Review selectReview(int reviewId);
    List<Review> selectAllReviews();
    boolean deleteReview(int reviewId) throws SQLException;
    boolean updateReview(Review review) throws SQLException;
}