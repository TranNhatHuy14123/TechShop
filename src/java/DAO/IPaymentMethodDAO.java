package DAO;
import model.PaymentMethod;
import java.util.List;
import java.sql.SQLException;

public interface IPaymentMethodDAO {
    void insertPaymentMethod(PaymentMethod paymentMethod) throws SQLException;
    PaymentMethod selectPaymentMethod(int paymentMethodId);
    List<PaymentMethod> selectAllPaymentMethods();
    boolean deletePaymentMethod(int paymentMethodId) throws SQLException;
    boolean updatePaymentMethod(PaymentMethod paymentMethod) throws SQLException;
}