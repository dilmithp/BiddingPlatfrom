package com.bidmaster.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.bidmaster.model.User;

public interface UserDAO {
    void insertUser(User user) throws SQLException;
    
    User getUserById(int userId) throws SQLException;
    
    User getUserByUsername(String username) throws SQLException;
    
    User getUserByEmail(String email) throws SQLException;
    
    List<User> getAllUsers() throws SQLException;
    
    List<User> searchUsers(String searchTerm) throws SQLException;
    
    boolean updateUser(User user) throws SQLException;
    
    boolean deleteUser(int userId) throws SQLException;
    
    boolean validateUser(String username, String password) throws SQLException;
    
    /**
     * Gets the total count of users in the system
     * 
     * @return The total number of users
     * @throws SQLException if a database error occurs
     */
    int getUserCount() throws SQLException;
    
    /**
     * Gets the count of new users registered in the last specified number of days
     * 
     * @param days The number of days to look back
     * @return The count of new users
     * @throws SQLException if a database error occurs
     */
    int getNewUserCount(int days) throws SQLException;
    
    /**
     * Gets a list of the most recently registered users
     * 
     * @param limit The maximum number of users to return
     * @return List of recent users
     * @throws SQLException if a database error occurs
     */
    List<User> getRecentUsers(int limit) throws SQLException;
    
    /**
     * Gets users registered within a date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of users registered within the date range
     * @throws SQLException if a database error occurs
     */
    List<User> getUsersRegisteredInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException;
    
    /**
     * Gets monthly user registration counts
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return Map with months as keys and registration counts as values
     * @throws SQLException if a database error occurs
     */
    Map<String, Integer> getMonthlyRegistrations(LocalDate startDate, LocalDate endDate) throws SQLException;
    
    /**
     * Gets active users (users who placed bids or listed items)
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of active users
     * @throws SQLException if a database error occurs
     */
    List<User> getActiveUsers(LocalDate startDate, LocalDate endDate) throws SQLException;
    
    /**
     * Gets top bidders
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param limit The maximum number of users to return
     * @return List of top bidders
     * @throws SQLException if a database error occurs
     */
    List<User> getTopBidders(LocalDate startDate, LocalDate endDate, int limit) throws SQLException;
}
