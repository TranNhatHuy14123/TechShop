package connect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            if (con != null) {
                System.out.println("Kết nối đến SQL Server thành công!");
            } else {
                System.out.println("Kết nối thất bại.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TestConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
