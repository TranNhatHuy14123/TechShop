package DAO;

import model.SupportRequest;
import java.util.List;
import java.sql.SQLException;

public interface ISupportRequestDAO {
    void insertSupportRequest(SupportRequest supportRequest) throws SQLException;
    SupportRequest selectSupportRequest(int supportRequestId);
    List<SupportRequest> selectAllSupportRequests();
    boolean deleteSupportRequest(int supportRequestId) throws SQLException;
    boolean updateSupportRequest(SupportRequest supportRequest) throws SQLException;
}