package DAO;

import model.CompareList;
import model.CompareListProduct;
import java.util.List;
import java.sql.SQLException;

public interface ICompareListDAO {
    void insertCompareList(CompareList compareList, List<CompareListProduct> compareListProducts) throws SQLException;
    CompareList selectCompareList(int compareListId);
    List<CompareList> selectAllCompareLists();
    boolean deleteCompareList(int compareListId) throws SQLException;
    boolean updateCompareList(CompareList compareList) throws SQLException;
}