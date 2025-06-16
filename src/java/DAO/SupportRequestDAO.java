package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.SupportRequest;

public class SupportRequestDAO implements ISupportRequestDAO {
    private static final String INSERT_SUPPORT_REQUEST = "INSERT INTO SupportRequests (UserID, Subject, Description, Status) VALUES (?, ?, ?, ?)";
    private static final String SELECT_SUPPORT_REQUEST = "SELECT * FROM SupportRequests WHERE SupportRequestID = ?";
    private static final String SELECT_ALL_SUPPORT_REQUESTS = "SELECT * FROM SupportRequests";
    private static final String DELETE_SUPPORT_REQUEST = "DELETE FROM SupportRequests WHERE SupportRequestID = ?";
    private static final String UPDATE_SUPPORT_REQUEST = "UPDATE SupportRequests SET UserID = ?, Subject = ?, Description = ?, Status = ? WHERE SupportRequestID = ?";

    @Override
    public void insertSupportRequest(SupportRequest supportRequest) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_SUPPORT_REQUEST)) {
            stmt.setInt(1, supportRequest.getUserId());
            stmt.setString(2, supportRequest.getSubject());
            stmt.setString(3, supportRequest.getMessage());
            stmt.setString(4, supportRequest.getStatus());
            stmt.executeUpdate();
        }
    }

    @Override
    public SupportRequest selectSupportRequest(int supportRequestId) {
        SupportRequest supportRequest = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_SUPPORT_REQUEST)) {
            stmt.setInt(1, supportRequestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                supportRequest = new SupportRequest();
                supportRequest.setRequestId(rs.getInt("SupportRequestID"));
                supportRequest.setUserId(rs.getInt("UserID"));
                supportRequest.setSubject(rs.getString("Subject"));
                supportRequest.setMessage(rs.getString("Description"));
                supportRequest.setStatus(rs.getString("Status"));
                supportRequest.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return supportRequest;
    }

    @Override
    public List<SupportRequest> selectAllSupportRequests() {
        List<SupportRequest> supportRequests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SUPPORT_REQUESTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SupportRequest supportRequest = new SupportRequest();
                supportRequest.setRequestId(rs.getInt("SupportRequestID"));
                supportRequest.setUserId(rs.getInt("UserID"));
                supportRequest.setSubject(rs.getString("Subject"));
                supportRequest.setMessage(rs.getString("Description"));
                supportRequest.setStatus(rs.getString("Status"));
                supportRequest.setCreatedAt(rs.getTimestamp("CreatedAt"));
                supportRequests.add(supportRequest);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return supportRequests;
    }

    @Override
    public boolean deleteSupportRequest(int supportRequestId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_SUPPORT_REQUEST)) {
            stmt.setInt(1, supportRequestId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateSupportRequest(SupportRequest supportRequest) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_SUPPORT_REQUEST)) {
            stmt.setInt(1, supportRequest.getUserId());
            stmt.setString(2, supportRequest.getSubject());
            stmt.setString(3, supportRequest.getMessage());
            stmt.setString(4, supportRequest.getStatus());
            stmt.setInt(5, supportRequest.getRequestId());
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