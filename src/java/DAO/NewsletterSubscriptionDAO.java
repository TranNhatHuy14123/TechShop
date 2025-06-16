package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.NewsletterSubscription;

public class NewsletterSubscriptionDAO implements INewsletterSubscriptionDAO {
    private static final String INSERT_NEWSLETTER_SUBSCRIPTION = "INSERT INTO NewsletterSubscriptions (Email, SubscriptionDate) VALUES (?, ?)";
    private static final String SELECT_NEWSLETTER_SUBSCRIPTION = "SELECT * FROM NewsletterSubscriptions WHERE SubscriptionID = ?";
    private static final String SELECT_ALL_NEWSLETTER_SUBSCRIPTIONS = "SELECT * FROM NewsletterSubscriptions";

    @Override
    public void insertNewsletterSubscription(NewsletterSubscription subscription) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_NEWSLETTER_SUBSCRIPTION)) {
            stmt.setString(1, subscription.getEmail());
            stmt.setTimestamp(2, new Timestamp(subscription.getSubscribedAt().getTime()));
            stmt.executeUpdate();
        }
    }

    @Override
    public NewsletterSubscription selectNewsletterSubscription(int subscriptionId) {
        NewsletterSubscription subscription = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_NEWSLETTER_SUBSCRIPTION)) {
            stmt.setInt(1, subscriptionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                subscription = new NewsletterSubscription();
                subscription.setSubscriptionId(rs.getInt("SubscriptionID"));
                subscription.setEmail(rs.getString("Email"));
                subscription.setSubscribedAt(rs.getTimestamp("SubscriptionDate"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return subscription;
    }

    @Override
    public List<NewsletterSubscription> selectAllNewsletterSubscriptions() {
        List<NewsletterSubscription> subscriptions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_NEWSLETTER_SUBSCRIPTIONS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NewsletterSubscription subscription = new NewsletterSubscription();
                subscription.setSubscriptionId(rs.getInt("SubscriptionID"));
                subscription.setEmail(rs.getString("Email"));
                subscription.setSubscribedAt(rs.getTimestamp("SubscriptionDate"));
                subscriptions.add(subscription);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return subscriptions;
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