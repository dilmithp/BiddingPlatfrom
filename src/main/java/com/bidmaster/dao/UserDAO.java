package com.bidmaster.dao;

import java.sql.SQLException;
import java.util.List;
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
}
