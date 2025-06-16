package DAO;

import model.NewsletterSubscription;
import java.util.List;
import java.sql.SQLException;

public interface INewsletterSubscriptionDAO {
    void insertNewsletterSubscription(NewsletterSubscription subscription) throws SQLException;
    NewsletterSubscription selectNewsletterSubscription(int subscriptionId);
    List<NewsletterSubscription> selectAllNewsletterSubscriptions();
}