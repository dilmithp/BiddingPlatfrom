package com.bidmaster.controller.item;

import java.io.IOException;
import java.sql.SQLException;
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
 * Servlet implementation class DeleteItemServlet
 * Handles deleting items
 */
@WebServlet("/DeleteItemServlet")
public class DeleteItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DeleteItemServlet.class.getName());
    
    private ItemService itemService;
    
    public void init() {
        itemService = new ItemServiceImpl();
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("role");
        
        try {
            // Get item ID from request parameter
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            
            // Get item details
            Item item = itemService.getItemById(itemId);
            
            if (item == null) {
                LOGGER.log(Level.WARNING, "Item not found: {0}", itemId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user is the seller of this item or an admin
            if (item.getSellerId() != userId && !"admin".equals(userRole)) {
                LOGGER.log(Level.WARNING, "Unauthorized access to delete item: {0} by user: {1}", 
                        new Object[]{itemId, userId});
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to delete this item");
                return;
            }
            
            // Delete item image if exists
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                ImageUploadUtil.deleteImage(item.getImageUrl(), getServletContext());
            }
            
            // Delete item
            boolean deleted = itemService.deleteItem(itemId);
            
            if (deleted) {
                LOGGER.log(Level.INFO, "Item deleted: {0}", itemId);
                
                // Redirect based on user role
                if ("admin".equals(userRole)) {
                    // Redirect admin to item management page
                    response.sendRedirect("admin/AdminItemServlet?message=Item deleted successfully");
                } else {
                    // Redirect seller to their items page
                    response.sendRedirect("MyItemsServlet?message=Item deleted successfully");
                }
            } else {
                request.setAttribute("errorMessage", "Failed to delete item");
                
                // Redirect based on user role
                if ("admin".equals(userRole)) {
                    request.getRequestDispatcher("admin/AdminItemServlet").forward(request, response);
                } else {
                    request.getRequestDispatcher("MyItemsServlet").forward(request, response);
                }
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid item ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item ID");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in DeleteItemServlet", e);
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
