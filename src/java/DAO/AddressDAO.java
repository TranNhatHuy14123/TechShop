package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Address;

public class AddressDAO implements IAddressDAO {
    private static final String INSERT_ADDRESS = "INSERT INTO Addresses (UserID, FirstName, LastName, Address, City, Country, ZipCode, Phone, IsDefault) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ADDRESS = "SELECT * FROM Addresses WHERE AddressID = ?";
    private static final String SELECT_ALL_ADDRESSES = "SELECT * FROM Addresses";
    private static final String DELETE_ADDRESS = "DELETE FROM Addresses WHERE AddressID = ?";
    private static final String UPDATE_ADDRESS = "UPDATE Addresses SET UserID = ?, FirstName = ?, LastName = ?, Address = ?, City = ?, Country = ?, ZipCode = ?, Phone = ?, IsDefault = ? WHERE AddressID = ?";

    @Override
    public void insertAddress(Address address) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_ADDRESS)) {
            stmt.setInt(1, address.getUserId());
            stmt.setString(2, address.getFirstName());
            stmt.setString(3, address.getLastName());
            stmt.setString(4, address.getAddress());
            stmt.setString(5, address.getCity());
            stmt.setString(6, address.getCountry());
            stmt.setString(7, address.getZipCode());
            stmt.setString(8, address.getPhone());
            stmt.setBoolean(9, address.isDefault());
            stmt.executeUpdate();
        }
    }

    @Override
    public Address selectAddress(int addressId) {
        Address address = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ADDRESS)) {
            stmt.setInt(1, addressId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                address = new Address();
                address.setAddressId(rs.getInt("AddressID"));
                address.setUserId(rs.getInt("UserID"));
                address.setFirstName(rs.getString("FirstName"));
                address.setLastName(rs.getString("LastName"));
                address.setAddress(rs.getString("Address"));
                address.setCity(rs.getString("City"));
                address.setCountry(rs.getString("Country"));
                address.setZipCode(rs.getString("ZipCode"));
                address.setPhone(rs.getString("Phone"));
                address.setDefault(rs.getBoolean("IsDefault"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return address;
    }

    @Override
    public List<Address> selectAllAddresses() {
        List<Address> addresses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ADDRESSES)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("AddressID"));
                address.setUserId(rs.getInt("UserID"));
                address.setFirstName(rs.getString("FirstName"));
                address.setLastName(rs.getString("LastName"));
                address.setAddress(rs.getString("Address"));
                address.setCity(rs.getString("City"));
                address.setCountry(rs.getString("Country"));
                address.setZipCode(rs.getString("ZipCode"));
                address.setPhone(rs.getString("Phone"));
                address.setDefault(rs.getBoolean("IsDefault"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return addresses;
    }

    @Override
    public boolean deleteAddress(int addressId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_ADDRESS)) {
            stmt.setInt(1, addressId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateAddress(Address address) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_ADDRESS)) {
            stmt.setInt(1, address.getUserId());
            stmt.setString(2, address.getFirstName());
            stmt.setString(3, address.getLastName());
            stmt.setString(4, address.getAddress());
            stmt.setString(5, address.getCity());
            stmt.setString(6, address.getCountry());
            stmt.setString(7, address.getZipCode());
            stmt.setString(8, address.getPhone());
            stmt.setBoolean(9, address.isDefault());
            stmt.setInt(10, address.getAddressId());
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
