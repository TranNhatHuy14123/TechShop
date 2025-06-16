package DAO;

import model.Category;
import java.util.List;
import java.sql.SQLException;

public interface ICategoryDAO {
    void insertCategory(Category category) throws SQLException;
    Category selectCategory(int categoryId);
    List<Category> selectAllCategories();
    boolean deleteCategory(int categoryId) throws SQLException;
    boolean updateCategory(Category category) throws SQLException;
}