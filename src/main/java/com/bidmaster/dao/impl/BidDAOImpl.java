package com.bidmaster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bidmaster.dao.BidDAO;
import com.bidmaster.model.Bid;
import com.bidmaster.util.DBConnectionUtil;

public class BidDAOImpl implements BidDAO {
    private static final Logger LOGGER = Logger.getLogger(BidDAOImpl.class.getName());
    
    private static final String INSERT_BID = 
        "INSERT INTO Bids (itemId, bidderId, bidAmount, status) VALUES (?, ?, ?, ?)";
    
    private static final String GET_BID_BY_ID = 
        "SELECT * FROM Bids WHERE bidId = ?";
    
    private static final String GET_BIDS_BY_ITEM = 
        "SELECT b.*, u.username AS bidderUsername FROM Bids b JOIN Users u ON b.bidderId = u.userId WHERE b.itemId = ? ORDER BY b.bidAmount DESC";
    
    private static final String GET_BIDS_BY_BIDDER = 
        "SELECT b.*, i.title AS itemTitle FROM Bids b JOIN Items i ON b.itemId = i.itemId WHERE b.bidderId = ? ORDER BY b.bidTime DESC";
    
    private static final String GET_HIGHEST_BID_FOR_ITEM = 
        "SELECT b.*, u.username AS bidderUsername FROM Bids b JOIN Users u ON b.bidderId = u.userId WHERE b.itemId = ? ORDER BY b.bidAmount DESC LIMIT 1";
    
    private static final String UPDATE_BID_STATUS = 
        "UPDATE Bids SET status = ? WHERE bidId = ?";
    
    private static final String UPDATE_ALL_BIDS_STATUS = 
        "UPDATE Bids SET status = ? WHERE itemId = ?";
    
    private static final String DELETE_BID = 
        "DELETE FROM Bids WHERE bidId = ?";
    
    private static final String COUNT_BIDS_FOR_ITEM = 
        "SELECT COUNT(*) FROM Bids WHERE itemId = ?";
    
    private static final String GET_TOTAL_BID_COUNT = 
        "SELECT COUNT(*) FROM Bids";
    
    private static final String GET_NEW_BID_COUNT = 
        "SELECT COUNT(*) FROM Bids WHERE bidTime >= DATE_SUB(NOW(), INTERVAL ? DAY)";
    
    private static final String GET_BIDS_IN_DATE_RANGE = 
        "SELECT b.*, u.username as bidderUsername, i.title as itemTitle " +
        "FROM Bids b " +
        "JOIN Users u ON b.bidderId = u.userId " +
        "JOIN Items i ON b.itemId = i.itemId " +
        "WHERE b.bidTime BETWEEN ? AND ? " +
        "ORDER BY b.bidTime DESC";
    
    private static final String GET_DAILY_BID_COUNTS = 
        "SELECT DATE_FORMAT(bidTime, '%Y-%m-%d') as day, COUNT(*) as count " +
        "FROM Bids " +
        "WHERE bidTime BETWEEN ? AND ? " +
        "GROUP BY DATE_FORMAT(bidTime, '%Y-%m-%d') " +
        "ORDER BY day";
    
    private static final String GET_RECENT_BIDS_FOR_SELLER = 
        "SELECT b.*, u.username as bidderUsername, i.title as itemTitle " +
        "FROM Bids b " +
        "JOIN Users u ON b.bidderId = u.userId " +
        "JOIN Items i ON b.itemId = i.itemId " +
        "WHERE i.sellerId = ? " +
        "ORDER BY b.bidTime DESC " +
        "LIMIT ?";
    
    private static final String GET_TOTAL_BIDS_COUNT_FOR_SELLER = 
        "SELECT COUNT(*) FROM Bids b JOIN Items i ON b.itemId = i.itemId WHERE i.sellerId = ?";

    @Override
    public int insertBid(Bid bid) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BID, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setInt(1, bid.getItemId());
            preparedStatement.setInt(2, bid.getBidderId());
            preparedStatement.setBigDecimal(3, bid.getBidAmount());
            preparedStatement.setString(4, bid.getStatus() != null ? bid.getStatus() : "active");
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating bid failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bidId = generatedKeys.getInt(1);
                    bid.setBidId(bidId);
                    LOGGER.log(Level.INFO, "Bid created successfully: ID {0}, Amount: {1}", 
                            new Object[]{bidId, bid.getBidAmount()});
                    return bidId;
                } else {
                    throw new SQLException("Creating bid failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting bid", e);
            throw e;
        }
    }

    @Override
    public Bid getBidById(int bidId) throws SQLException {
        Bid bid = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BID_BY_ID)) {
            
            preparedStatement.setInt(1, bidId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bid = extractBidFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bid by ID: " + bidId, e);
            throw e;
        }
        
        return bid;
    }

    @Override
    public List<Bid> getBidsByItem(int itemId) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BIDS_BY_ITEM)) {
            
            preparedStatement.setInt(1, itemId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Bid bid = extractBidFromResultSet(resultSet);
                    bid.setBidderUsername(resultSet.getString("bidderUsername"));
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bids by item: " + itemId, e);
            throw e;
        }
        
        return bids;
    }

    @Override
    public List<Bid> getBidsByBidder(int bidderId) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BIDS_BY_BIDDER)) {
            
            preparedStatement.setInt(1, bidderId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Bid bid = extractBidFromResultSet(resultSet);
                    bid.setItemTitle(resultSet.getString("itemTitle"));
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bids by bidder: " + bidderId, e);
            throw e;
        }
        
        return bids;
    }

    @Override
    public Bid getHighestBidForItem(int itemId) throws SQLException {
        Bid bid = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_HIGHEST_BID_FOR_ITEM)) {
            
            preparedStatement.setInt(1, itemId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bid = extractBidFromResultSet(resultSet);
                    bid.setBidderUsername(resultSet.getString("bidderUsername"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting highest bid for item: " + itemId, e);
            throw e;
        }
        
        return bid;
    }

    @Override
    public boolean updateBidStatus(int bidId, String status) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BID_STATUS)) {
            
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, bidId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Bid status updated: ID {0}, Status: {1}, Rows affected: {2}", 
                    new Object[]{bidId, status, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bid status: " + bidId, e);
            throw e;
        }
    }

    @Override
    public boolean updateAllBidsStatus(int itemId, String status) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ALL_BIDS_STATUS)) {
            
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, itemId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "All bids status updated for item: ID {0}, Status: {1}, Rows affected: {2}", 
                    new Object[]{itemId, status, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating all bids status for item: " + itemId, e);
            throw e;
        }
    }

    @Override
    public boolean deleteBid(int bidId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BID)) {
            
            preparedStatement.setInt(1, bidId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Bid deleted: ID {0}, Rows affected: {1}", new Object[]{bidId, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting bid: " + bidId, e);
            throw e;
        }
    }

    @Override
    public int countBidsForItem(int itemId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BIDS_FOR_ITEM)) {
            
            preparedStatement.setInt(1, itemId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting bids for item: " + itemId, e);
            throw e;
        }
        
        return 0;
    }

    @Override
    public int getTotalBidCount() throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TOTAL_BID_COUNT);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total bid count", e);
            throw e;
        }
    }

    @Override
    public int getNewBidCount(int days) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NEW_BID_COUNT)) {
            
            preparedStatement.setInt(1, days);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting new bid count", e);
            throw e;
        }
    }

    @Override
    public List<Bid> getBidsInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BIDS_IN_DATE_RANGE)) {
            
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Bid bid = extractBidFromResultSet(resultSet);
                    bid.setBidderUsername(resultSet.getString("bidderUsername"));
                    bid.setItemTitle(resultSet.getString("itemTitle"));
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bids in date range", e);
            throw e;
        }
        return bids;
    }

    @Override
    public Map<String, Integer> getDailyBidCounts(LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, Integer> dailyBidCounts = new LinkedHashMap<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_DAILY_BID_COUNTS)) {
            
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String day = resultSet.getString("day");
                    int count = resultSet.getInt("count");
                    dailyBidCounts.put(day, count);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting daily bid counts", e);
            throw e;
        }
        return dailyBidCounts;
    }
    
    @Override
    public List<Bid> getRecentBidsForSeller(int sellerId, int limit) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_RECENT_BIDS_FOR_SELLER)) {
            
            preparedStatement.setInt(1, sellerId);
            preparedStatement.setInt(2, limit);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Bid bid = extractBidFromResultSet(resultSet);
                    bid.setBidderUsername(resultSet.getString("bidderUsername"));
                    bid.setItemTitle(resultSet.getString("itemTitle"));
                    bids.add(bid);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting recent bids for seller: " + sellerId, e);
            throw e;
        }
        return bids;
    }
    
    @Override
    public int getTotalBidsCountForSeller(int sellerId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TOTAL_BIDS_COUNT_FOR_SELLER)) {
            
            preparedStatement.setInt(1, sellerId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total bids count for seller: " + sellerId, e);
            throw e;
        }
    }
    
    /**
     * Extracts a Bid object from a ResultSet
     * 
     * @param resultSet The ResultSet containing bid data
     * @return The extracted Bid object
     * @throws SQLException if a database error occurs
     */
    private Bid extractBidFromResultSet(ResultSet resultSet) throws SQLException {
        Bid bid = new Bid();
        bid.setBidId(resultSet.getInt("bidId"));
        bid.setItemId(resultSet.getInt("itemId"));
        bid.setBidderId(resultSet.getInt("bidderId"));
        bid.setBidAmount(resultSet.getBigDecimal("bidAmount"));
        bid.setBidTime(resultSet.getTimestamp("bidTime"));
        bid.setStatus(resultSet.getString("status"));
        return bid;
    }
}
