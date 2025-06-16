package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Product;

public class ProductDAO implements IProductDAO {
    private static final String INSERT_PRODUCT = "INSERT INTO Products (Name, CategoryID, BrandID, Price, OldPrice, Discount, Images, IsNew, Rating, StockStatus, Description, Size, Color) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PRODUCT = "SELECT * FROM Products WHERE ProductID = ?";
    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM Products";
    private static final String DELETE_PRODUCT = "DELETE FROM Products WHERE ProductID = ?";
    private static final String UPDATE_PRODUCT = "UPDATE Products SET Name = ?, CategoryID = ?, BrandID = ?, Price = ?, OldPrice = ?, Discount = ?, Images = ?, IsNew = ?, Rating = ?, StockStatus = ?, Description = ?, Size = ?, Color = ? WHERE ProductID = ?";
    private static final String SELECT_PRODUCTS_BY_PAGE = "SELECT * FROM Products LIMIT ? OFFSET ?";
    private static final String SEARCH_PRODUCTS = "SELECT * FROM Products WHERE Name LIKE ? AND (CategoryID = ? OR ? IS NULL)";
    private static final String GET_FEATURED_PRODUCTS = "SELECT * FROM Products WHERE Discount IS NOT NULL OR IsNew = 1 LIMIT 10";

    @Override
    public void insertProduct(Product product) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_PRODUCT)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setInt(3, product.getBrandId());
            stmt.setDouble(4, product.getPrice());
            if (product.getOldPrice() != null) {
                stmt.setDouble(5, product.getOldPrice());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }
            if (product.getDiscount() != null) {
                stmt.setInt(6, product.getDiscount());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, product.getImages());
            stmt.setBoolean(8, product.isNew());
            stmt.setDouble(9, product.getRating());
            stmt.setString(10, product.getStockStatus());
            stmt.setString(11, product.getDescription());
            stmt.setString(12, product.getSize());
            stmt.setString(13, product.getColor());
            stmt.executeUpdate();
        }
    }

    @Override
    public Product selectProduct(int productId) {
        Product product = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCT)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                product = new Product();
                product.setProductId(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setCategoryId(rs.getInt("CategoryID"));
                product.setBrandId(rs.getInt("BrandID"));
                product.setPrice(rs.getDouble("Price"));
                double oldPrice = rs.getDouble("OldPrice");
                if (!rs.wasNull()) {
                    product.setOldPrice(oldPrice);
                }
                int discount = rs.getInt("Discount");
                if (!rs.wasNull()) {
                    product.setDiscount(discount);
                }
                product.setImages(rs.getString("Images"));
                product.setNew(rs.getBoolean("IsNew"));
                product.setRating(rs.getDouble("Rating"));
                product.setStockStatus(rs.getString("StockStatus"));
                product.setDescription(rs.getString("Description"));
                product.setSize(rs.getString("Size"));
                product.setColor(rs.getString("Color"));
                product.setCreatedAt(rs.getTimestamp("CreatedAt"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return product;
    }

    @Override
    public List<Product> selectAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PRODUCTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setCategoryId(rs.getInt("CategoryID"));
                product.setBrandId(rs.getInt("BrandID"));
                product.setPrice(rs.getDouble("Price"));
                double oldPrice = rs.getDouble("OldPrice");
                if (!rs.wasNull()) {
                    product.setOldPrice(oldPrice);
                }
                int discount = rs.getInt("Discount");
                if (!rs.wasNull()) {
                    product.setDiscount(discount);
                }
                product.setImages(rs.getString("Images"));
                product.setNew(rs.getBoolean("IsNew"));
                product.setRating(rs.getDouble("Rating"));
                product.setStockStatus(rs.getString("StockStatus"));
                product.setDescription(rs.getString("Description"));
                product.setSize(rs.getString("Size"));
                product.setColor(rs.getString("Color"));
                product.setCreatedAt(rs.getTimestamp("CreatedAt"));
                products.add(product);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return products;
    }

    @Override
    public boolean deleteProduct(int productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_PRODUCT)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateProduct(Product product) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_PRODUCT)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setInt(3, product.getBrandId());
            stmt.setDouble(4, product.getPrice());
            if (product.getOldPrice() != null) {
                stmt.setDouble(5, product.getOldPrice());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }
            if (product.getDiscount() != null) {
                stmt.setInt(6, product.getDiscount());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, product.getImages());
            stmt.setBoolean(8, product.isNew());
            stmt.setDouble(9, product.getRating());
            stmt.setString(10, product.getStockStatus());
            stmt.setString(11, product.getDescription());
            stmt.setString(12, product.getSize());
            stmt.setString(13, product.getColor());
            stmt.setInt(14, product.getProductId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Product> selectProductsByPage(int page, int size) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_PRODUCTS_BY_PAGE)) {
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setCategoryId(rs.getInt("CategoryID"));
                product.setBrandId(rs.getInt("BrandID"));
                product.setPrice(rs.getDouble("Price"));
                double oldPrice = rs.getDouble("OldPrice");
                if (!rs.wasNull()) {
                    product.setOldPrice(oldPrice);
                }
                int discount = rs.getInt("Discount");
                if (!rs.wasNull()) {
                    product.setDiscount(discount);
                }
                product.setImages(rs.getString("Images"));
                product.setNew(rs.getBoolean("IsNew"));
                product.setRating(rs.getDouble("Rating"));
                product.setStockStatus(rs.getString("StockStatus"));
                product.setDescription(rs.getString("Description"));
                product.setSize(rs.getString("Size"));
                product.setColor(rs.getString("Color"));
                product.setCreatedAt(rs.getTimestamp("CreatedAt"));
                products.add(product);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return products;
    }

    @Override
    public List<Product> searchProducts(String keyword, Integer categoryId) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SEARCH_PRODUCTS)) {
            stmt.setString(1, "%" + keyword + "%");
            if (categoryId != null) {
                stmt.setInt(2, categoryId);
                stmt.setInt(3, categoryId);
            } else {
                stmt.setNull(2, Types.INTEGER);
                stmt.setNull(3, Types.INTEGER);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setCategoryId(rs.getInt("CategoryID"));
                product.setBrandId(rs.getInt("BrandID"));
                product.setPrice(rs.getDouble("Price"));
                double oldPrice = rs.getDouble("OldPrice");
                if (!rs.wasNull()) {
                    product.setOldPrice(oldPrice);
                }
                int discount = rs.getInt("Discount");
                if (!rs.wasNull()) {
                    product.setDiscount(discount);
                }
                product.setImages(rs.getString("Images"));
                product.setNew(rs.getBoolean("IsNew"));
                product.setRating(rs.getDouble("Rating"));
                product.setStockStatus(rs.getString("StockStatus"));
                product.setDescription(rs.getString("Description"));
                product.setSize(rs.getString("Size"));
                product.setColor(rs.getString("Color"));
                product.setCreatedAt(rs.getTimestamp("CreatedAt"));
                products.add(product);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return products;
    }

    @Override
    public List<Product> getFeaturedProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(GET_FEATURED_PRODUCTS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setCategoryId(rs.getInt("CategoryID"));
                product.setBrandId(rs.getInt("BrandID"));
                product.setPrice(rs.getDouble("Price"));
                double oldPrice = rs.getDouble("OldPrice");
                if (!rs.wasNull()) {
                    product.setOldPrice(oldPrice);
                }
                int discount = rs.getInt("Discount");
                if (!rs.wasNull()) {
                    product.setDiscount(discount);
                }
                product.setImages(rs.getString("Images"));
                product.setNew(rs.getBoolean("IsNew"));
                product.setRating(rs.getDouble("Rating"));
                product.setStockStatus(rs.getString("StockStatus"));
                product.setDescription(rs.getString("Description"));
                product.setSize(rs.getString("Size"));
                product.setColor(rs.getString("Color"));
                product.setCreatedAt(rs.getTimestamp("CreatedAt"));
                products.add(product);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return products;
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