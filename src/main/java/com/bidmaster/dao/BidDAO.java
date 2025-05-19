package com.bidmaster.dao;

import java.sql.SQLException;
import java.util.List;
import com.bidmaster.model.Bid;

public interface BidDAO {
    int insertBid(Bid bid) throws SQLException;
    Bid getBidById(int bidId) throws SQLException;
    List<Bid> getBidsByItem(int itemId) throws SQLException;
    List<Bid> getBidsByBidder(int bidderId) throws SQLException;
    Bid getHighestBidForItem(int itemId) throws SQLException;
    boolean updateBidStatus(int bidId, String status) throws SQLException;
    boolean updateAllBidsStatus(int itemId, String status) throws SQLException;
    boolean deleteBid(int bidId) throws SQLException;
    int countBidsForItem(int itemId) throws SQLException;
    /**
     * Gets the total count of bids in the system
     * 
     * @return The total number of bids
     * @throws SQLException if a database error occurs
     */
    int getTotalBidCount() throws SQLException;
    
    /**
     * Gets the count of new bids placed in the last specified number of days
     * 
     * @param days The number of days to look back
     * @return The count of new bids
     * @throws SQLException if a database error occurs
     */
    int getNewBidCount(int days) throws SQLException;
}
