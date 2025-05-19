package com.bidmaster.service;

import java.sql.SQLException;
import java.util.List;
import com.bidmaster.model.Watchlist;

public interface WatchlistService {
    boolean addToWatchlist(int userId, int itemId) throws SQLException;
    boolean removeFromWatchlist(int userId, int itemId) throws SQLException;
    List<Watchlist> getWatchlistByUser(int userId) throws SQLException;
    boolean isItemInWatchlist(int userId, int itemId) throws SQLException;
}
