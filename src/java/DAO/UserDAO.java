package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.User;

public class UserDAO implements IUserDAO {
    private static final String INSERT_USER = "INSERT INTO Users (FirstName, LastName, Email, Password, Address, City, Country, ZipCode, Phone, Role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER = "SELECT * FROM Users WHERE UserID = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM Users";
    private static final String DELETE_USER = "DELETE FROM Users WHERE UserID = ?";
    private static final String UPDATE_USER = "UPDATE Users SET FirstName = ?, LastName = ?, Email = ?, Password = ?, Address = ?, City = ?, Country = ?, ZipCode = ?, Phone = ?, Role = ? WHERE UserID = ?";

    @Override
    public void insertUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_USER)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getCountry());
            stmt.setString(8, user.getZipCode());
            stmt.setString(9, user.getPhone());
            stmt.setString(10, user.getRole());
            stmt.executeUpdate();
        }
    }

    @Override
    public User selectUser(int userId) {
        User user = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_USER)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFirstName(rs.getString("FirstName"));
                user.setLastName(rs.getString("LastName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setAddress(rs.getString("Address"));
                user.setCity(rs.getString("City"));
                user.setCountry(rs.getString("Country"));
                user.setZipCode(rs.getString("ZipCode"));
                user.setPhone(rs.getString("Phone"));
                user.setRole(rs.getString("Role"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return user;
    }

    @Override
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFirstName(rs.getString("FirstName"));
                user.setLastName(rs.getString("LastName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setAddress(rs.getString("Address"));
                user.setCity(rs.getString("City"));
                user.setCountry(rs.getString("Country"));
                user.setZipCode(rs.getString("ZipCode"));
                user.setPhone(rs.getString("Phone"));
                user.setRole(rs.getString("Role"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                users.add(user);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return users;
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getCountry());
            stmt.setString(8, user.getZipCode());
            stmt.setString(9, user.getPhone());
            stmt.setString(10, user.getRole());
            stmt.setInt(11, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public User checkLogin(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setFirstName(rs.getString("FirstName"));
                user.setLastName(rs.getString("LastName"));
                user.setEmail(rs.getString("Email"));
                user.setPassword(rs.getString("Password"));
                user.setAddress(rs.getString("Address"));
                user.setCity(rs.getString("City"));
                user.setCountry(rs.getString("Country"));
                user.setZipCode(rs.getString("ZipCode"));
                user.setPhone(rs.getString("Phone"));
                user.setRole(rs.getString("Role"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                return user;
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
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