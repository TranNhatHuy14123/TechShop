package model;

import java.sql.Timestamp;

public class SocialShare {
    private int shareId;
    private int productId;
    private Integer userId;
    private String platform;
    private Timestamp sharedAt;

    public SocialShare() {}

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Timestamp getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(Timestamp sharedAt) {
        this.sharedAt = sharedAt;
    }
}