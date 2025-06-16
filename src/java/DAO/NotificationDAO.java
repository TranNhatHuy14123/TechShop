package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Notification;

public class NotificationDAO {
    private static final String SELECT_NOTIFICATION = "SELECT * FROM Notifications WHERE NotificationID = ?";
    private static final String SELECT_ALL_NOTIFICATIONS = "SELECT * FROM Notifications";

    public Notification selectNotification(int notificationId) {
        Notification notification = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_NOTIFICATION)) {
            stmt.setInt(1, notificationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                notification = new Notification();
                notification.setNotificationId(rs.getInt("NotificationID"));
                notification.setUserId(rs.getInt("UserID"));
                notification.setMessage(rs.getString("Message"));
                notification.setRead(rs.getBoolean("IsRead"));
                notification.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return notification;
    }

    public List<Notification> selectAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_NOTIFICATIONS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getInt("NotificationID"));
                notification.setUserId(rs.getInt("UserID"));
                notification.setMessage(rs.getString("Message"));
                notification.setRead(rs.getBoolean("IsRead"));
                notification.setCreatedAt(rs.getTimestamp("CreatedAt"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return notifications;
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