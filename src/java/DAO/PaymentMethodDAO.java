package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.PaymentMethod;

public class PaymentMethodDAO implements IPaymentMethodDAO {
    private static final String INSERT_PAYMENT_METHOD = "INSERT INTO PaymentMethods (Name, Description) VALUES (?, ?)";
    private static final String SELECT_PAYMENT_METHOD = "SELECT * FROM PaymentMethods WHERE PaymentMethodID = ?";
    private static final String SELECT_ALL_PAYMENT_METHODS = "SELECT * FROM PaymentMethods";
    private static final String DELETE_PAYMENT_METHOD = "DELETE FROM PaymentMethods WHERE PaymentMethodID = ?";
    private static final String UPDATE_PAYMENT_METHOD = "UPDATE PaymentMethods SET Name = ?, Description = ? WHERE PaymentMethodID = ?";

    @Override
    public void insertPaymentMethod(PaymentMethod paymentMethod) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_PAYMENT_METHOD)) {
            stmt.setString(1, paymentMethod.getName());
            stmt.setString(2, paymentMethod.getDescription());
            stmt.executeUpdate();
        }
    }

    @Override
    public PaymentMethod selectPaymentMethod(int paymentMethodId) {
        PaymentMethod paymentMethod = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_PAYMENT_METHOD)) {
            stmt.setInt(1, paymentMethodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                paymentMethod = new PaymentMethod();
                paymentMethod.setPaymentMethodId(rs.getInt("PaymentMethodID"));
                paymentMethod.setName(rs.getString("Name"));
                paymentMethod.setDescription(rs.getString("Description"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return paymentMethod;
    }

    @Override
    public List<PaymentMethod> selectAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PAYMENT_METHODS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setPaymentMethodId(rs.getInt("PaymentMethodID"));
                paymentMethod.setName(rs.getString("Name"));
                paymentMethod.setDescription(rs.getString("Description"));
                paymentMethods.add(paymentMethod);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return paymentMethods;
    }

    @Override
    public boolean deletePaymentMethod(int paymentMethodId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_PAYMENT_METHOD)) {
            stmt.setInt(1, paymentMethodId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updatePaymentMethod(PaymentMethod paymentMethod) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_PAYMENT_METHOD)) {
            stmt.setString(1, paymentMethod.getName());
            stmt.setString(2, paymentMethod.getDescription());
            stmt.setInt(3, paymentMethod.getPaymentMethodId());
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