package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Payment;

public class PaymentDAO implements IPaymentDAO {
    private static final String INSERT_PAYMENT = "INSERT INTO Payments (OrderID, PaymentMethodID, Amount, Status, TransactionID) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_PAYMENT = "SELECT * FROM Payments WHERE PaymentID = ?";
    private static final String SELECT_ALL_PAYMENTS = "SELECT * FROM Payments";
    private static final String DELETE_PAYMENT = "DELETE FROM Payments WHERE PaymentID = ?";
    private static final String UPDATE_PAYMENT = "UPDATE Payments SET OrderID = ?, PaymentMethodID = ?, Amount = ?, Status = ?, TransactionID = ? WHERE PaymentID = ?";

    @Override
    public void insertPayment(Payment payment) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_PAYMENT)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setInt(2, payment.getPaymentMethodId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getStatus());
            stmt.setString(5, payment.getTransactionId());
            stmt.executeUpdate();
        }
    }

    @Override
    public Payment selectPayment(int paymentId) {
        Payment payment = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_PAYMENT)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                payment = new Payment();
                payment.setPaymentId(rs.getInt("PaymentID"));
                payment.setOrderId(rs.getInt("OrderID"));
                payment.setPaymentMethodId(rs.getInt("PaymentMethodID"));
                payment.setAmount(rs.getDouble("Amount"));
                payment.setStatus(rs.getString("Status"));
                payment.setTransactionId(rs.getString("TransactionID"));
                payment.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return payment;
    }

    @Override
    public List<Payment> selectAllPayments() {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PAYMENTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("PaymentID"));
                payment.setOrderId(rs.getInt("OrderID"));
                payment.setPaymentMethodId(rs.getInt("PaymentMethodID"));
                payment.setAmount(rs.getDouble("Amount"));
                payment.setStatus(rs.getString("Status"));
                payment.setTransactionId(rs.getString("TransactionID"));
                payment.setCreatedAt(rs.getTimestamp("CreatedAt"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return payments;
    }

    @Override
    public boolean deletePayment(int paymentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_PAYMENT)) {
            stmt.setInt(1, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updatePayment(Payment payment) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_PAYMENT)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setInt(2, payment.getPaymentMethodId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getStatus());
            stmt.setString(5, payment.getTransactionId());
            stmt.setInt(6, payment.getPaymentId());
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