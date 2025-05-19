package com.bidmaster.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bidmaster.dao.ItemDAO;
import com.bidmaster.dao.impl.ItemDAOImpl;
import com.bidmaster.model.Item;
import com.bidmaster.service.ItemService;

/**
 * Implementation of ItemService interface
 */
public class ItemServiceImpl implements ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemServiceImpl.class.getName());
    
    private ItemDAO itemDAO;
    
    public ItemServiceImpl() {
        this.itemDAO = new ItemDAOImpl();
    }

    @Override
    public int createItem(Item item) throws SQLException {
        try {
            return itemDAO.insertItem(item);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating item", e);
            throw e;
        }
    }

    @Override
    public Item getItemById(int itemId) throws SQLException {
        try {
            return itemDAO.getItemById(itemId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting item by ID", e);
            throw e;
        }
    }

    @Override
    public List<Item> getAllItems() throws SQLException {
        try {
            return itemDAO.getAllItems();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all items", e);
            throw e;
        }
    }

    @Override
    public List<Item> getItemsByCategory(int categoryId) throws SQLException {
        try {
            return itemDAO.getItemsByCategory(categoryId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting items by category", e);
            throw e;
        }
    }

    @Override
    public List<Item> getItemsBySeller(int sellerId) throws SQLException {
        try {
            return itemDAO.getItemsBySeller(sellerId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting items by seller", e);
            throw e;
        }
    }

    @Override
    public List<Item> searchItems(String searchTerm) throws SQLException {
        try {
            return itemDAO.searchItems(searchTerm);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching items", e);
            throw e;
        }
    }

    @Override
    public List<Item> getActiveItems() throws SQLException {
        try {
            return itemDAO.getActiveItems();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active items", e);
            throw e;
        }
    }

    @Override
    public List<Item> getEndingSoonItems(int hours) throws SQLException {
        try {
            return itemDAO.getEndingSoonItems(hours);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting ending soon items", e);
            throw e;
        }
    }

    @Override
    public boolean updateItem(Item item) throws SQLException {
        try {
            return itemDAO.updateItem(item);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item", e);
            throw e;
        }
    }

    @Override
    public boolean updateItemStatus(int itemId, String status) throws SQLException {
        try {
            return itemDAO.updateItemStatus(itemId, status);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item status", e);
            throw e;
        }
    }

    @Override
    public boolean updateCurrentPrice(int itemId, double newPrice) throws SQLException {
        try {
            return itemDAO.updateCurrentPrice(itemId, newPrice);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating item price", e);
            throw e;
        }
    }

    @Override
    public boolean deleteItem(int itemId) throws SQLException {
        try {
            return itemDAO.deleteItem(itemId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting item", e);
            throw e;
        }
    }

    @Override
    public boolean isItemOwner(int itemId, int userId) throws SQLException {
        try {
            Item item = itemDAO.getItemById(itemId);
            return item != null && item.getSellerId() == userId;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking item ownership", e);
            throw e;
        }
    }
    
    @Override
    public int getActiveItemCount() throws SQLException {
        try {
            return itemDAO.getActiveItemCount();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active item count", e);
            throw e;
        }
    }

    @Override
    public int getNewItemCount(int days) throws SQLException {
        try {
            return itemDAO.getNewItemCount(days);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting new item count for last " + days + " days", e);
            throw e;
        }
    }

    @Override
    public List<Item> getRecentItems(int limit) throws SQLException {
        try {
            return itemDAO.getRecentItems(limit);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting recent items with limit " + limit, e);
            throw e;
        }
    }
}
