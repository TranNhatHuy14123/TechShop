package DAO;

import model.Payment;
import java.util.List;
import java.sql.SQLException;

public interface IPaymentDAO {
    void insertPayment(Payment payment) throws SQLException;
    Payment selectPayment(int paymentId);
    List<Payment> selectAllPayments();
    boolean deletePayment(int paymentId) throws SQLException;
    boolean updatePayment(Payment payment) throws SQLException;
}