package com.bidmaster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
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
    
    // SQL queries
    private static final String INSERT_ITEM = 
        "INSERT INTO Items (title, description, sellerId, categoryId, startingPrice, currentPrice, reservePrice, startTime, endTime, status, imageUrl) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String GET_ITEM_BY_ID = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.itemId = ?";
    
    private static final String GET_ALL_ITEMS = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "ORDER BY i.createdAt DESC";
    
    private static final String GET_ITEMS_BY_CATEGORY = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.categoryId = ? " +
        "ORDER BY i.createdAt DESC";
    
    private static final String GET_ITEMS_BY_SELLER = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.sellerId = ? " +
        "ORDER BY i.createdAt DESC";
    
    private static final String SEARCH_ITEMS = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.title LIKE ? OR i.description LIKE ? " +
        "ORDER BY i.createdAt DESC";
    
    private static final String GET_ACTIVE_ITEMS = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.status = 'active' AND i.startTime <= NOW() AND i.endTime > NOW() " +
        "ORDER BY i.endTime ASC";
    
    private static final String GET_ENDING_SOON_ITEMS = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.status = 'active' AND i.endTime > NOW() AND i.endTime <= DATE_ADD(NOW(), INTERVAL ? HOUR) " +
        "ORDER BY i.endTime ASC";
    
    private static final String UPDATE_ITEM = 
        "UPDATE Items SET title = ?, description = ?, categoryId = ?, startingPrice = ?, " +
        "reservePrice = ?, startTime = ?, endTime = ?, status = ?, imageUrl = ? " +
        "WHERE itemId = ?";
    
    private static final String UPDATE_ITEM_STATUS = 
        "UPDATE Items SET status = ? WHERE itemId = ?";
    
    private static final String UPDATE_CURRENT_PRICE = 
        "UPDATE Items SET currentPrice = ? WHERE itemId = ?";
    
    private static final String DELETE_ITEM = 
        "DELETE FROM Items WHERE itemId = ?";
    
    private static final String GET_ACTIVE_ITEM_COUNT = 
        "SELECT COUNT(*) FROM Items WHERE status = 'active'";
    
    private static final String GET_NEW_ITEM_COUNT = 
        "SELECT COUNT(*) FROM Items WHERE createdAt >= DATE_SUB(NOW(), INTERVAL ? DAY)";
    
    private static final String GET_RECENT_ITEMS = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "ORDER BY i.createdAt DESC LIMIT ?";
    
    private static final String GET_ITEMS_IN_DATE_RANGE = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.createdAt BETWEEN ? AND ? " +
        "ORDER BY i.createdAt DESC";
    
    private static final String GET_ITEMS_WITH_MOST_BIDS = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName, COUNT(b.bidId) as bidCount " +
        "FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "LEFT JOIN Bids b ON i.itemId = b.itemId AND b.bidTime BETWEEN ? AND ? " +
        "WHERE i.createdAt <= ? " +
        "GROUP BY i.itemId " +
        "ORDER BY bidCount DESC " +
        "LIMIT ?";
    
    private static final String GET_ACTIVE_ITEMS_BY_SELLER = 
        "SELECT i.*, u.username as sellerUsername, c.categoryName FROM Items i " +
        "JOIN Users u ON i.sellerId = u.userId " +
        "JOIN Categories c ON i.categoryId = c.categoryId " +
        "WHERE i.sellerId = ? AND i.status = 'active' AND i.startTime <= NOW() AND i.endTime > NOW() " +
        "ORDER BY i.endTime ASC";
    
    private static final String GET_ACTIVE_ITEM_COUNT_BY_SELLER = 
        "SELECT COUNT(*) FROM Items WHERE sellerId = ? AND status = 'active' AND startTime <= NOW() AND endTime > NOW()";

    @Override
    public int insertItem(Item item) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setInt(3, item.getSellerId());
            preparedStatement.setInt(4, item.getCategoryId());
            preparedStatement.setBigDecimal(5, item.getStartingPrice());
            preparedStatement.setBigDecimal(6, item.getCurrentPrice());
            
            if (item.getReservePrice() != null) {
                preparedStatement.setBigDecimal(7, item.getReservePrice());
            } else {
                preparedStatement.setNull(7, java.sql.Types.DECIMAL);
            }
            
            preparedStatement.setTimestamp(8, Timestamp.valueOf(item.getStartTime()));
            preparedStatement.setTimestamp(9, Timestamp.valueOf(item.getEndTime()));
            preparedStatement.setString(10, item.getStatus());
            preparedStatement.setString(11, item.getImageUrl());
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int itemId = generatedKeys.getInt(1);
                    item.setItemId(itemId);
                    LOGGER.log(Level.INFO, "Item created successfully: {0}, ID: {1}", 
                            new Object[]{item.getTitle(), itemId});
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
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
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
                item.setSellerUsername(resultSet.getString("sellerUsername"));
                item.setCategoryName(resultSet.getString("categoryName"));
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
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
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
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
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
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching items: " + searchTerm, e);
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
                item.setSellerUsername(resultSet.getString("sellerUsername"));
                item.setCategoryName(resultSet.getString("categoryName"));
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
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
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
            preparedStatement.setInt(3, item.getCategoryId());
            preparedStatement.setBigDecimal(4, item.getStartingPrice());
            
            if (item.getReservePrice() != null) {
                preparedStatement.setBigDecimal(5, item.getReservePrice());
            } else {
                preparedStatement.setNull(5, java.sql.Types.DECIMAL);
            }
            
            preparedStatement.setTimestamp(6, Timestamp.valueOf(item.getStartTime()));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(item.getEndTime()));
            preparedStatement.setString(8, item.getStatus());
            preparedStatement.setString(9, item.getImageUrl());
            preparedStatement.setInt(10, item.getItemId());
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Item updated: {0}, Rows affected: {1}", 
                    new Object[]{item.getTitle(), rowsAffected});
            
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
            
            LOGGER.log(Level.INFO, "Item price updated: ID {0}, Price: {1}, Rows affected: {2}", 
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
            
            LOGGER.log(Level.INFO, "Item deleted: ID {0}, Rows affected: {1}", 
                    new Object[]{itemId, rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting item: " + itemId, e);
            throw e;
        }
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
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting recent items", e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> getItemsCreatedInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ITEMS_IN_DATE_RANGE)) {
            
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting items in date range", e);
            throw e;
        }
        return items;
    }

    @Override
    public List<Item> getItemsWithMostBids(LocalDate startDate, LocalDate endDate, int limit) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ITEMS_WITH_MOST_BIDS)) {
            
            preparedStatement.setDate(1, Date.valueOf(startDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            preparedStatement.setInt(4, limit);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
                    item.setBidCount(resultSet.getInt("bidCount"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting items with most bids", e);
            throw e;
        }
        return items;
    }
    
    @Override
    public List<Item> getActiveItemsBySeller(int sellerId) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ACTIVE_ITEMS_BY_SELLER)) {
            
            preparedStatement.setInt(1, sellerId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Item item = extractItemFromResultSet(resultSet);
                    item.setSellerUsername(resultSet.getString("sellerUsername"));
                    item.setCategoryName(resultSet.getString("categoryName"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active items by seller: " + sellerId, e);
            throw e;
        }
        return items;
    }
    
    @Override
    public int getActiveItemCountBySeller(int sellerId) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ACTIVE_ITEM_COUNT_BY_SELLER)) {
            
            preparedStatement.setInt(1, sellerId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active item count by seller: " + sellerId, e);
            throw e;
        }
    }
    
    /**
     * Extracts an Item object from a ResultSet
     * 
     * @param resultSet The ResultSet containing item data
     * @return The extracted Item object
     * @throws SQLException if a database error occurs
     */
    private Item extractItemFromResultSet(ResultSet resultSet) throws SQLException {
        Item item = new Item();
        item.setItemId(resultSet.getInt("itemId"));
        item.setTitle(resultSet.getString("title"));
        item.setDescription(resultSet.getString("description"));
        item.setSellerId(resultSet.getInt("sellerId"));
        item.setCategoryId(resultSet.getInt("categoryId"));
        item.setStartingPrice(resultSet.getBigDecimal("startingPrice"));
        item.setCurrentPrice(resultSet.getBigDecimal("currentPrice"));
        item.setReservePrice(resultSet.getBigDecimal("reservePrice"));
        
        Timestamp startTime = resultSet.getTimestamp("startTime");
        if (startTime != null) {
            item.setStartTime(startTime.toLocalDateTime());
        }
        
        Timestamp endTime = resultSet.getTimestamp("endTime");
        if (endTime != null) {
            item.setEndTime(endTime.toLocalDateTime());
        }
        
        item.setStatus(resultSet.getString("status"));
        item.setImageUrl(resultSet.getString("imageUrl"));
        
        Timestamp createdAt = resultSet.getTimestamp("createdAt");
        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return item;
    }
}
