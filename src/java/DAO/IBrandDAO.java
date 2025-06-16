package DAO;

import model.Brand;
import java.util.List;
import java.sql.SQLException;

public interface IBrandDAO {
    void insertBrand(Brand brand) throws SQLException;
    Brand selectBrand(int brandId);
    List<Brand> selectAllBrands();
    boolean deleteBrand(int brandId) throws SQLException;
    boolean updateBrand(Brand brand) throws SQLException;
}