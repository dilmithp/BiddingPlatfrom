package com.bidmaster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bidmaster.dao.ItemDAO;
import com.bidmaster.model.Item;
import com.bidmaster.util.DBConnectionUtil;

public class ItemDAOImpl implements ItemDAO {
    private static final Logger LOGGER = Logger.getLogger(ItemDAOImpl.class.getName());
    
    private static final String INSERT_ITEM = "INSERT INTO Items (title, description, startingPrice, reservePrice, currentPrice, imageUrl, categoryId, sellerId, startTime, endTime, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_ITEM_BY_ID = "SELECT * FROM Items WHERE itemId = ?";
    private static final String GET_ALL_ITEMS = "SELECT * FROM Items ORDER BY createdAt DESC";
    private static final String GET_ITEMS_BY_CATEGORY = "SELECT * FROM Items WHERE categoryId = ? ORDER BY createdAt DESC";
    private static final String GET_ITEMS_BY_SELLER = "SELECT * FROM Items WHERE sellerId = ? ORDER BY createdAt DESC";
    private static final String SEARCH_ITEMS = "SELECT * FROM Items WHERE title LIKE ? OR description LIKE ? ORDER BY createdAt DESC";
    private static final String GET_ACTIVE_ITEMS = "SELECT * FROM Items WHERE status = 'active' AND NOW() BETWEEN startTime AND endTime ORDER BY endTime ASC";
    private static final String GET_ENDING_SOON_ITEMS = "SELECT * FROM Items WHERE status = 'active' AND NOW() BETWEEN startTime AND endTime AND endTime <= DATE_ADD(NOW(), INTERVAL ? HOUR) ORDER BY endTime ASC";
    private static final String UPDATE_ITEM = "UPDATE Items SET title = ?, description = ?, startingPrice = ?, reservePrice = ?, imageUrl = ?, categoryId = ?, startTime = ?, endTime = ?, status = ? WHERE itemId = ?";
    private static final String UPDATE_ITEM_STATUS = "UPDATE Items SET status = ? WHERE itemId = ?";
    private static final String UPDATE_CURRENT_PRICE = "UPDATE Items SET currentPrice = ? WHERE itemId = ?";
    private static final String DELETE_ITEM = "DELETE FROM Items WHERE itemId = ?";
    private static final String GET_ACTIVE_ITEM_COUNT = "SELECT COUNT(*) FROM Items WHERE status = 'active'";
    private static final String GET_NEW_ITEM_COUNT = "SELECT COUNT(*) FROM Items WHERE createdAt >= DATE_SUB(NOW(), INTERVAL ? DAY)";
    private static final String GET_RECENT_ITEMS = "SELECT i.*, u.username as sellerUsername FROM Items i JOIN Users u ON i.sellerId = u.userId ORDER BY i.createdAt DESC LIMIT ?";
    @Override
    public int insertItem(Item item) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setBigDecimal(3, item.getStartingPrice());
            preparedStatement.setBigDecimal(4, item.getReservePrice());
            preparedStatement.setBigDecimal(5, item.getCurrentPrice());
            preparedStatement.setString(6, item.getImageUrl());
            preparedStatement.setInt(7, item.getCategoryId());
            preparedStatement.setInt(8, item.getSellerId());
            preparedStatement.setTimestamp(9, Timestamp.valueOf(item.getStartTime()));
            preparedStatement.setTimestamp(10, Timestamp.valueOf(item.getEndTime()));
            preparedStatement.setString(11, item.getStatus());
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int itemId = generatedKeys.getInt(1);
                    item.setItemId(itemId);
                    LOGGER.log(Level.INFO, "Item created successfully: {0}, ID: {1}", new Object[]{item.getTitle(), itemId});
                    return itemId;
                } else {
                    throw new SQLException("Creating item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting item", e);
            throw e;
        }
    }

    @Override
    public Item getItemById(int itemId) throws SQLException {
        Item item = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ITEM_BY_ID)) {
            
            preparedStatement.setInt(1, itemId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    item = extractItemFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting item by ID: " + itemId, e);
            throw e;
        }
        return item;
    }

    @Override
    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_ITEMS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                Item item = extractItemFromResultSet(resultSet);
                items.add(item);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all items", e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> getItemsByCategory(int categoryId) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ITEMS_BY_CATEGORY)) {
            
            preparedStatement.setInt(1, categoryId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting items by category: " + categoryId, e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> getItemsBySeller(int sellerId) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ITEMS_BY_SELLER)) {
            
            preparedStatement.setInt(1, sellerId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting items by seller: " + sellerId, e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> searchItems(String searchTerm) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_ITEMS)) {
            
            String searchPattern = "%" + searchTerm + "%";
            preparedStatement.setString(1, searchPattern);
            preparedStatement.setString(2, searchPattern);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching items with term: " + searchTerm, e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> getActiveItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ACTIVE_ITEMS);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                Item item = extractItemFromResultSet(resultSet);
                items.add(item);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active items", e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> getEndingSoonItems(int hours) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ENDING_SOON_ITEMS)) {
            
            preparedStatement.setInt(1, hours);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting ending soon items", e);
            throw e;
        }
        return items;
    }

    @Override
    public boolean updateItem(Item item) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ITEM)) {
            
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setBigDecimal(3, item.getStartingPrice());
            preparedStatement.setBigDecimal(4, item.getReservePrice());
            preparedStatement.setString(5, item.getImageUrl());
            preparedStatement.setInt(6, item.getCategoryId());
            preparedStatement.setTimestamp(7, Timestamp.valueOf(item.getStartTime()));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(item.getEndTime()));
            preparedStatement.setString(9, item.getStatus());
            preparedStatement.setInt(10, item.getItemId());
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item updated: {0}, Rows affected: {1}", new Object[]{item.getTitle(), rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item: " + item.getItemId(), e);
            throw e;
        }
    }

    @Override
    public boolean updateItemStatus(int itemId, String status) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ITEM_STATUS)) {
            
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, itemId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item status updated: ID {0}, Status: {1}, Rows affected: {2}", 
                    new Object[]{itemId, status, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item status: " + itemId, e);
            throw e;
        }
    }

    @Override
    public boolean updateCurrentPrice(int itemId, double newPrice) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CURRENT_PRICE)) {
            
            preparedStatement.setDouble(1, newPrice);
            preparedStatement.setInt(2, itemId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item price updated: ID {0}, New Price: {1}, Rows affected: {2}", 
                    new Object[]{itemId, newPrice, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item price: " + itemId, e);
            throw e;
        }
    }

    @Override
    public boolean deleteItem(int itemId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ITEM)) {
            
            preparedStatement.setInt(1, itemId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item deleted: ID {0}, Rows affected: {1}", new Object[]{itemId, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting item: " + itemId, e);
            throw e;
        }
    }
    
    private Item extractItemFromResultSet(ResultSet resultSet) throws SQLException {
        Item item = new Item();
        item.setItemId(resultSet.getInt("itemId"));
        item.setTitle(resultSet.getString("title"));
        item.setDescription(resultSet.getString("description"));
        item.setStartingPrice(resultSet.getBigDecimal("startingPrice"));
        item.setReservePrice(resultSet.getBigDecimal("reservePrice"));
        item.setCurrentPrice(resultSet.getBigDecimal("currentPrice"));
        item.setImageUrl(resultSet.getString("imageUrl"));
        item.setCategoryId(resultSet.getInt("categoryId"));
        item.setSellerId(resultSet.getInt("sellerId"));
        item.setStartTime(resultSet.getTimestamp("startTime").toLocalDateTime());
        item.setEndTime(resultSet.getTimestamp("endTime").toLocalDateTime());
        item.setStatus(resultSet.getString("status"));
        item.setCreatedAt(resultSet.getTimestamp("createdAt"));
        return item;
    }
    @Override
    public int getActiveItemCount() throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ACTIVE_ITEM_COUNT);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active item count", e);
            throw e;
        }
    }

    @Override
    public int getNewItemCount(int days) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NEW_ITEM_COUNT)) {
            
            preparedStatement.setInt(1, days);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting new item count", e);
            throw e;
        }
    }

    @Override
    public List<Item> getRecentItems(int limit) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_RECENT_ITEMS)) {
            
            preparedStatement.setInt(1, limit);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    // Add seller username for display purposes
                    if (resultSet.getString("sellerUsername") != null) {
                        item.setSellerUsername(resultSet.getString("sellerUsername"));
                    }
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting recent items", e);
            throw e;
        }
        return items;
    }
}
