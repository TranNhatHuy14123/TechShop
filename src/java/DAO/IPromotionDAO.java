package DAO;

import model.Promotion;
import model.PromotionProduct;
import java.util.List;
import java.sql.SQLException;

public interface IPromotionDAO {
    void insertPromotion(Promotion promotion, List<PromotionProduct> promotionProducts) throws SQLException;
    Promotion selectPromotion(int promotionId);
    List<Promotion> selectAllPromotions();
    boolean deletePromotion(int promotionId) throws SQLException;
    boolean updatePromotion(Promotion promotion) throws SQLException;
}