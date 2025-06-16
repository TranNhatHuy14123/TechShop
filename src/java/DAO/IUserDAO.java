package DAO;

import model.User;
import java.util.List;
import java.sql.SQLException;

public interface IUserDAO {
    void insertUser(User user) throws SQLException;
    User selectUser(int userId);
    List<User> selectAllUsers();
    boolean deleteUser(int userId) throws SQLException;
    boolean updateUser(User user) throws SQLException;
    User checkLogin(String email, String password);
}