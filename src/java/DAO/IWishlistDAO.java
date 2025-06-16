package DAO;

import model.Wishlist;
import model.WishlistProduct;
import java.util.List;
import java.sql.SQLException;

public interface IWishlistDAO {
    void insertWishlist(Wishlist wishlist, List<WishlistProduct> wishlistProducts) throws SQLException;
    Wishlist selectWishlist(int wishlistId);
    List<Wishlist> selectAllWishlists();
    boolean deleteWishlist(int wishlistId) throws SQLException;
    boolean updateWishlist(Wishlist wishlist) throws SQLException;
}