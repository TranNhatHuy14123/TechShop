package DAO;

import model.Product;
import java.util.List;
import java.sql.SQLException;

public interface IProductDAO {
    void insertProduct(Product product) throws SQLException;
    Product selectProduct(int productId);
    List<Product> selectAllProducts();
    boolean deleteProduct(int productId) throws SQLException;
    boolean updateProduct(Product product) throws SQLException;
    List<Product> selectProductsByPage(int page, int size);
    List<Product> searchProducts(String keyword, Integer categoryId);
    List<Product> getFeaturedProducts();
}