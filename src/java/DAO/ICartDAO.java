package DAO;

import model.Cart;
import model.CartProduct;
import java.util.List;
import java.sql.SQLException;

public interface ICartDAO {
    void insertCart(Cart cart, List<CartProduct> cartProducts) throws SQLException;
    Cart selectCart(int cartId);
    List<Cart> selectAllCarts();
    boolean deleteCart(int cartId) throws SQLException;
    boolean updateCart(Cart cart) throws SQLException;
}