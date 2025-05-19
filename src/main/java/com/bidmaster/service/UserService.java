package com.bidmaster.service;

import java.sql.SQLException;
import java.util.List;
import com.bidmaster.model.User;

public interface UserService {
    void registerUser(User user) throws SQLException;
    User getUserById(int userId) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    User getUserByEmail(String email) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    List<User> searchUsers(String searchTerm) throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int userId) throws SQLException;
    boolean authenticateUser(String username, String password) throws SQLException;
    boolean isAdmin(User user);
    int getUserCount() throws SQLException;
    int getNewUserCount(int days) throws SQLException;
    List<User> getRecentUsers(int limit) throws SQLException;
}
