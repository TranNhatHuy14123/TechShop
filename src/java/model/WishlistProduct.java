package model;

public class WishlistProduct {
    private int wishlistProductId;
    private int wishlistId;
    private int productId;

    public WishlistProduct() {}

    public int getWishlistProductId() {
        return wishlistProductId;
    }

    public void setWishlistProductId(int wishlistProductId) {
        this.wishlistProductId = wishlistProductId;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}