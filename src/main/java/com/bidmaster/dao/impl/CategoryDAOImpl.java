package com.bidmaster.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bidmaster.dao.CategoryDAO;
import com.bidmaster.model.Category;
import com.bidmaster.util.DBConnectionUtil;

/**
 * Implementation of CategoryDAO interface
 */
public class CategoryDAOImpl implements CategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAOImpl.class.getName());
    
    private static final String INSERT_CATEGORY = "INSERT INTO Categories (categoryName, description, parentCategoryId) VALUES (?, ?, ?)";
    private static final String GET_CATEGORY_BY_ID = "SELECT c.*, p.categoryName as parentCategoryName FROM Categories c LEFT JOIN Categories p ON c.parentCategoryId = p.categoryId WHERE c.categoryId = ?";
    private static final String GET_ALL_CATEGORIES = "SELECT c.*, p.categoryName as parentCategoryName FROM Categories c LEFT JOIN Categories p ON c.parentCategoryId = p.categoryId ORDER BY c.categoryName";
    private static final String GET_SUBCATEGORIES = "SELECT * FROM Categories WHERE parentCategoryId = ? ORDER BY categoryName";
    private static final String UPDATE_CATEGORY = "UPDATE Categories SET categoryName = ?, description = ?, parentCategoryId = ? WHERE categoryId = ?";
    private static final String DELETE_CATEGORY = "DELETE FROM Categories WHERE categoryId = ?";
    private static final String CHECK_CATEGORY_USAGE = "SELECT COUNT(*) FROM Items WHERE categoryId = ?";

    @Override
    public int insertCategory(Category category) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());
            
            if (category.getParentCategoryId() != null) {
                preparedStatement.setInt(3, category.getParentCategoryId());
            } else {
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
            }
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int categoryId = generatedKeys.getInt(1);
                    category.setCategoryId(categoryId);
                    LOGGER.log(Level.INFO, "Category created successfully: {0}, ID: {1}", 
                            new Object[]{category.getCategoryName(), categoryId});
                    return categoryId;
                } else {
                    throw new SQLException("Creating category failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting category", e);
            throw e;
        }
    }

    @Override
    public Category getCategoryById(int categoryId) throws SQLException {
        Category category = null;
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CATEGORY_BY_ID)) {
            
            preparedStatement.setInt(1, categoryId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    category = extractCategoryFromResultSet(resultSet);
                    category.setParentCategoryName(resultSet.getString("parentCategoryName"));
                    
                    // Get subcategories
                    List<Category> subcategories = getSubcategories(categoryId);
                    category.setSubcategories(subcategories);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting category by ID: " + categoryId, e);
            throw e;
        }
        return category;
    }

    @Override
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_CATEGORIES);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            
            while (resultSet.next()) {
                Category category = extractCategoryFromResultSet(resultSet);
                category.setParentCategoryName(resultSet.getString("parentCategoryName"));
                categories.add(category);
            }
            
            // Build category hierarchy
            buildCategoryHierarchy(categories);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all categories", e);
            throw e;
        }
        return categories;
    }

    @Override
    public List<Category> getSubcategories(int parentCategoryId) throws SQLException {
        List<Category> subcategories = new ArrayList<>();
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SUBCATEGORIES)) {
            
            preparedStatement.setInt(1, parentCategoryId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Category category = extractCategoryFromResultSet(resultSet);
                    subcategories.add(category);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting subcategories for parent: " + parentCategoryId, e);
            throw e;
        }
        return subcategories;
    }

    @Override
    public boolean updateCategory(Category category) throws SQLException {
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CATEGORY)) {
            
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());
            
            if (category.getParentCategoryId() != null) {
                preparedStatement.setInt(3, category.getParentCategoryId());
            } else {
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
            }
            
            preparedStatement.setInt(4, category.getCategoryId());
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            LOGGER.log(Level.INFO, "Category updated: {0}, Rows affected: {1}", 
                    new Object[]{category.getCategoryName(), rowsAffected});
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category: " + category.getCategoryId(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteCategory(int categoryId) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnectionUtil.getConnection();
            connection.setAutoCommit(false);
            
            // Check if category is in use
            try (PreparedStatement checkStatement = connection.prepareStatement(CHECK_CATEGORY_USAGE)) {
                checkStatement.setInt(1, categoryId);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // Category is in use, cannot delete
                        connection.rollback();
                        LOGGER.log(Level.WARNING, "Cannot delete category {0} because it is in use", categoryId);
                        return false;
                    }
                }
            }
            
            // Check if category has subcategories
            List<Category> subcategories = getSubcategories(categoryId);
            if (!subcategories.isEmpty()) {
                // Category has subcategories, cannot delete
                connection.rollback();
                LOGGER.log(Level.WARNING, "Cannot delete category {0} because it has subcategories", categoryId);
                return false;
            }
            
            // Delete the category
            try (PreparedStatement deleteStatement = connection.prepareStatement(DELETE_CATEGORY)) {
                deleteStatement.setInt(1, categoryId);
                int rowsAffected = deleteStatement.executeUpdate();
                
                if (rowsAffected > 0) {
                    connection.commit();
                    LOGGER.log(Level.INFO, "Category deleted: ID {0}, Rows affected: {1}", 
                            new Object[]{categoryId, rowsAffected});
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Error deleting category: " + categoryId, e);
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }
    
    /**
     * Extracts a Category object from a ResultSet
     * 
     * @param resultSet The ResultSet containing category data
     * @return The extracted Category object
     * @throws SQLException if a database error occurs
     */
    private Category extractCategoryFromResultSet(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setCategoryId(resultSet.getInt("categoryId"));
        category.setCategoryName(resultSet.getString("categoryName"));
        category.setDescription(resultSet.getString("description"));
        
        int parentCategoryId = resultSet.getInt("parentCategoryId");
        if (!resultSet.wasNull()) {
            category.setParentCategoryId(parentCategoryId);
        }
        
        return category;
    }
    
    /**
     * Builds the category hierarchy by setting subcategories
     * 
     * @param categories The list of all categories
     */
    private void buildCategoryHierarchy(List<Category> categories) {
        // Create a map of parent categories
        for (Category category : categories) {
            if (category.getParentCategoryId() != null) {
                for (Category parentCategory : categories) {
                    if (parentCategory.getCategoryId() == category.getParentCategoryId()) {
                        parentCategory.addSubcategory(category);
                        break;
                    }
                }
            }
        }
    }
}
