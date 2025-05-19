package com.bidmaster.dao;

import java.sql.SQLException;
import java.util.List;
import com.bidmaster.model.Watchlist;

public interface WatchlistDAO {
    boolean insertWatchlist(Watchlist watchlist) throws SQLException;
    boolean deleteWatchlist(int userId, int itemId) throws SQLException;
    List<Watchlist> getWatchlistByUser(int userId) throws SQLException;
    boolean isItemInWatchlist(int userId, int itemId) throws SQLException;
}
