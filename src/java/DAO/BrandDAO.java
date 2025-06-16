package DAO;

import java.sql.*;
import java.util.*;
import connect.DBConnection;
import model.Brand;

public class BrandDAO implements IBrandDAO {
    private static final String INSERT_BRAND = "INSERT INTO Brands (Name, Description) VALUES (?, ?)";
    private static final String SELECT_BRAND = "SELECT * FROM Brands WHERE BrandID = ?";
    private static final String SELECT_ALL_BRANDS = "SELECT * FROM Brands";
    private static final String DELETE_BRAND = "DELETE FROM Brands WHERE BrandID = ?";
    private static final String UPDATE_BRAND = "UPDATE Brands SET Name = ?, Description = ? WHERE BrandID = ?";

    @Override
    public void insertBrand(Brand brand) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(INSERT_BRAND)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            stmt.executeUpdate();
        }
    }

    @Override
    public Brand selectBrand(int brandId) {
        Brand brand = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_BRAND)) {
            stmt.setInt(1, brandId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                brand = new Brand();
                brand.setBrandId(rs.getInt("BrandID"));
                brand.setName(rs.getString("Name"));
                brand.setDescription(rs.getString("Description"));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return brand;
    }

    @Override
    public List<Brand> selectAllBrands() {
        List<Brand> brands = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_BRANDS)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Brand brand = new Brand();
                brand.setBrandId(rs.getInt("BrandID"));
                brand.setName(rs.getString("Name"));
                brand.setDescription(rs.getString("Description"));
                brands.add(brand);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return brands;
    }

    @Override
    public boolean deleteBrand(int brandId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(DELETE_BRAND)) {
            stmt.setInt(1, brandId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateBrand(Brand brand) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_BRAND)) {
            stmt.setString(1, brand.getName());
            stmt.setString(2, brand.getDescription());
            stmt.setInt(3, brand.getBrandId());
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