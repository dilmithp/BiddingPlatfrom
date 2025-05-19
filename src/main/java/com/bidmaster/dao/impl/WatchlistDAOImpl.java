package com.bidmaster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bidmaster.dao.WatchlistDAO;
import com.bidmaster.model.Watchlist;
import com.bidmaster.util.DBConnectionUtil;

public class WatchlistDAOImpl implements WatchlistDAO {
    private static final Logger LOGGER = Logger.getLogger(WatchlistDAOImpl.class.getName());
    
    private static final String INSERT_WATCHLIST = "INSERT INTO Watchlist (userId, itemId) VALUES (?, ?)";
    private static final String DELETE_WATCHLIST = "DELETE FROM Watchlist WHERE userId = ? AND itemId = ?";
    private static final String GET_WATCHLIST_BY_USER = "SELECT w.*, i.title as itemTitle, i.imageUrl as itemImageUrl FROM Watchlist w JOIN Items i ON w.itemId = i.itemId WHERE w.userId = ? ORDER BY w.addedDate DESC";
    private static final String IS_ITEM_IN_WATCHLIST = "SELECT COUNT(*) FROM Watchlist WHERE userId = ? AND itemId = ?";

    @Override
    public boolean insertWatchlist(Watchlist watchlist) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_WATCHLIST)) {
            
            preparedStatement.setInt(1, watchlist.getUserId());
            preparedStatement.setInt(2, watchlist.getItemId());
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item added to watchlist: User {0}, Item {1}, Rows affected: {2}", 
                    new Object[]{watchlist.getUserId(), watchlist.getItemId(), rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding item to watchlist", e);
            throw e;
        }
    }

    @Override
    public boolean deleteWatchlist(int userId, int itemId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_WATCHLIST)) {
            
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, itemId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item removed from watchlist: User {0}, Item {1}, Rows affected: {2}", 
                    new Object[]{userId, itemId, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing item from watchlist", e);
            throw e;
        }
    }

    @Override
    public List<Watchlist> getWatchlistByUser(int userId) throws SQLException {
        List<Watchlist> watchlistItems = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_WATCHLIST_BY_USER)) {
            
            preparedStatement.setInt(1, userId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Watchlist watchlist = extractWatchlistFromResultSet(resultSet);
                    watchlist.setItemTitle(resultSet.getString("itemTitle"));
                    watchlist.setItemImageUrl(resultSet.getString("itemImageUrl"));
                    watchlistItems.add(watchlist);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting watchlist for user: " + userId, e);
            throw e;
        }
        return watchlistItems;
    }

    @Override
    public boolean isItemInWatchlist(int userId, int itemId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(IS_ITEM_IN_WATCHLIST)) {
            
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, itemId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if item is in watchlist", e);
            throw e;
        }
        return false;
    }
    
    private Watchlist extractWatchlistFromResultSet(ResultSet resultSet) throws SQLException {
        Watchlist watchlist = new Watchlist();
        watchlist.setWatchlistId(resultSet.getInt("watchlistId"));
        watchlist.setUserId(resultSet.getInt("userId"));
        watchlist.setItemId(resultSet.getInt("itemId"));
        watchlist.setAddedDate(resultSet.getTimestamp("addedDate"));
        return watchlist;
    }
}
