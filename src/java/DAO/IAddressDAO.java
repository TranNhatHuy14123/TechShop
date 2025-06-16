package DAO;

import model.Address;
import java.util.List;
import java.sql.SQLException;

public interface IAddressDAO {
    void insertAddress(Address address) throws SQLException;
    Address selectAddress(int addressId);
    List<Address> selectAllAddresses();
    boolean deleteAddress(int addressId) throws SQLException;
    boolean updateAddress(Address address) throws SQLException;
}