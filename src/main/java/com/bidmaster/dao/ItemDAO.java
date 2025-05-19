package com.bidmaster.dao;

import java.sql.SQLException;
import java.util.List;
import com.bidmaster.model.Item;

public interface ItemDAO {
    int insertItem(Item item) throws SQLException;
    Item getItemById(int itemId) throws SQLException;
    List<Item> getAllItems() throws SQLException;
    List<Item> getItemsByCategory(int categoryId) throws SQLException;
    List<Item> getItemsBySeller(int sellerId) throws SQLException;
    List<Item> searchItems(String searchTerm) throws SQLException;
    List<Item> getActiveItems() throws SQLException;
    List<Item> getEndingSoonItems(int hours) throws SQLException;
    boolean updateItem(Item item) throws SQLException;
    boolean updateItemStatus(int itemId, String status) throws SQLException;
    boolean updateCurrentPrice(int itemId, double newPrice) throws SQLException;
    boolean deleteItem(int itemId) throws SQLException;
    /**
     * Gets the count of active items in the system
     * 
     * @return The count of active items
     * @throws SQLException if a database error occurs
     */
    int getActiveItemCount() throws SQLException;
    
    /**
     * Gets the count of new items created in the last specified number of days
     * 
     * @param days The number of days to look back
     * @return The count of new items
     * @throws SQLException if a database error occurs
     */
    int getNewItemCount(int days) throws SQLException;
    
    /**
     * Gets a list of the most recently created items
     * 
     * @param limit The maximum number of items to return
     * @return List of recent items
     * @throws SQLException if a database error occurs
     */
    List<Item> getRecentItems(int limit) throws SQLException;
}
