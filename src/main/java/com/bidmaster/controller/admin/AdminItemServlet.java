package com.bidmaster.controller.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.bidmaster.model.Item;
import com.bidmaster.service.ItemService;
import com.bidmaster.service.impl.ItemServiceImpl;
import com.bidmaster.util.ImageUploadUtil;

/**
 * Servlet implementation class AdminItemServlet
 * Handles item management in the admin panel
 */
@WebServlet("/admin/AdminItemServlet")
public class AdminItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminItemServlet.class.getName());
    
    private ItemService itemService;
    
    public void init() {
        itemService = new ItemServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in and is an admin
        if (session == null || session.getAttribute("userId") == null || 
                !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        try {
            // Check if search is requested
            String searchTerm = request.getParameter("searchTerm");
            List<Item> itemList;
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Search for items
                itemList = itemService.searchItems(searchTerm);
                request.setAttribute("searchTerm", searchTerm);
            } else {
                // Get all items
                itemList = itemService.getAllItems();
            }
            
            request.setAttribute("itemList", itemList);
            request.getRequestDispatcher("/admin/items.jsp").forward(request, response);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in AdminItemServlet", e);
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in and is an admin
        if (session == null || session.getAttribute("userId") == null || 
                !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Get action parameter
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect("AdminItemServlet");
            return;
        }
        
        try {
            switch (action) {
                case "delete":
                    deleteItem(request, response);
                    break;
                case "updateStatus":
                    updateItemStatus(request, response);
                    break;
                default:
                    response.sendRedirect("AdminItemServlet");
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in AdminItemServlet", e);
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles item deletion
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws SQLException if a database error occurs
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    private void deleteItem(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            
            // Get item to delete its image if exists
            Item item = itemService.getItemById(itemId);
            if (item != null && item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                ImageUploadUtil.deleteImage(item.getImageUrl(), getServletContext());
            }
            
            boolean deleted = itemService.deleteItem(itemId);
            
            if (deleted) {
                LOGGER.log(Level.INFO, "Item deleted: ID {0}", itemId);
                request.setAttribute("successMessage", "Item deleted successfully");
            } else {
                LOGGER.log(Level.WARNING, "Failed to delete item: ID {0}", itemId);
                request.setAttribute("errorMessage", "Failed to delete item");
            }
            
            doGet(request, response);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid item ID format", e);
            request.setAttribute("errorMessage", "Invalid item ID");
            doGet(request, response);
        }
    }
    
    /**
     * Handles updating an item's status
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @throws SQLException if a database error occurs
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    private void updateItemStatus(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            String status = request.getParameter("status");
            
            if (status == null || status.isEmpty()) {
                request.setAttribute("errorMessage", "Status cannot be empty");
                doGet(request, response);
                return;
            }
            
            boolean updated = itemService.updateItemStatus(itemId, status);
            
            if (updated) {
                LOGGER.log(Level.INFO, "Item status updated: ID {0}, Status: {1}", new Object[]{itemId, status});
                request.setAttribute("successMessage", "Item status updated successfully");
            } else {
                LOGGER.log(Level.WARNING, "Failed to update item status: ID {0}", itemId);
                request.setAttribute("errorMessage", "Failed to update item status");
            }
            
            doGet(request, response);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid item ID format", e);
            request.setAttribute("errorMessage", "Invalid item ID");
            doGet(request, response);
        }
    }
}
