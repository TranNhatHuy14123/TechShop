package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Category;

public class CategoryDAO implements ICategoryDAO {
    private static final String INSERT_CATEGORY = "INSERT INTO Categories (Name, Description) VALUES (?, ?)";
    private static final String SELECT_CATEGORY = "SELECT * FROM Categories WHERE CategoryID = ?";
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM Categories";
    private static final String DELETE_CATEGORY = "DELETE FROM Categories WHERE CategoryID = ?";
    private static final String UPDATE_CATEGORY = "UPDATE Categories SET Name = ?, Description = ? WHERE CategoryID = ?";

    @Override
    public void insertCategory(Category category) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_CATEGORY)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.executeUpdate();
        }
    }

    @Override
    public Category selectCategory(int categoryId) {
        Category category = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_CATEGORY)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.setCategoryId(rs.getInt("CategoryID"));
                category.setName(rs.getString("Name"));
                category.setDescription(rs.getString("Description"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return category;
    }

    @Override
    public List<Category> selectAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_CATEGORIES)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("CategoryID"));
                category.setName(rs.getString("Name"));
                category.setDescription(rs.getString("Description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return categories;
    }

    @Override
    public boolean deleteCategory(int categoryId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateCategory(Category category) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getCategoryId());
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