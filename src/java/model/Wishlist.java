package model;

import java.sql.Timestamp;

public class Wishlist {
    private int wishlistId;
    private int userId;

    public Wishlist() {}

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}