package com.bidmaster.controller.item;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.bidmaster.model.Category;
import com.bidmaster.model.Item;
import com.bidmaster.service.CategoryService;
import com.bidmaster.service.ItemService;
import com.bidmaster.service.impl.CategoryServiceImpl;
import com.bidmaster.service.impl.ItemServiceImpl;
import com.bidmaster.util.ImageUploadUtil;
import com.bidmaster.util.ValidationUtil;

/**
 * Servlet implementation class UpdateItemServlet
 * Handles updating item details
 */
@WebServlet("/UpdateItemServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class UpdateItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UpdateItemServlet.class.getName());
    
    private ItemService itemService;
    private CategoryService categoryService;
    
    public void init() {
        itemService = new ItemServiceImpl();
        categoryService = new CategoryServiceImpl();
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        
        try {
            // Get item ID from request parameter
            int itemId = Integer.parseInt(request.getParameter("id"));
            
            // Get item details
            Item item = itemService.getItemById(itemId);
            
            if (item == null) {
                LOGGER.log(Level.WARNING, "Item not found: {0}", itemId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user is the seller of this item
            if (item.getSellerId() != userId && !"admin".equals(session.getAttribute("role"))) {
                LOGGER.log(Level.WARNING, "Unauthorized access to edit item: {0} by user: {1}", 
                        new Object[]{itemId, userId});
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to edit this item");
                return;
            }
            
            // Get all categories for dropdown
            List<Category> categories = categoryService.getAllCategories();
            
            // Set attributes for JSP
            request.setAttribute("item", item);
            request.setAttribute("categories", categories);
            
            // Forward to edit item page
            request.getRequestDispatcher("items/edit.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid item ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item ID");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in UpdateItemServlet", e);
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
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
        
        try {
            // Get item ID from request parameter
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            
            // Get existing item
            Item item = itemService.getItemById(itemId);
            
            if (item == null) {
                LOGGER.log(Level.WARNING, "Item not found: {0}", itemId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // Check if user is the seller of this item
            if (item.getSellerId() != userId && !"admin".equals(session.getAttribute("role"))) {
                LOGGER.log(Level.WARNING, "Unauthorized access to update item: {0} by user: {1}", 
                        new Object[]{itemId, userId});
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to update this item");
                return;
            }
            
            // Get form parameters
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String startingPriceStr = request.getParameter("startingPrice");
            String reservePriceStr = request.getParameter("reservePrice");
            String categoryIdStr = request.getParameter("categoryId");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String status = request.getParameter("status");
            
            // Validate input
            boolean isValid = true;
            
            if (!ValidationUtil.isNotEmpty(title)) {
                request.setAttribute("titleError", "Title is required");
                isValid = false;
            }
            
            if (!ValidationUtil.isNotEmpty(description)) {
                request.setAttribute("descriptionError", "Description is required");
                isValid = false;
            }
            
            BigDecimal startingPrice = null;
            try {
                startingPrice = new BigDecimal(startingPriceStr);
                if (startingPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    request.setAttribute("startingPriceError", "Starting price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("startingPriceError", "Please enter a valid starting price");
                isValid = false;
            }
            
            BigDecimal reservePrice = null;
            if (reservePriceStr != null && !reservePriceStr.isEmpty()) {
                try {
                    reservePrice = new BigDecimal(reservePriceStr);
                    if (reservePrice.compareTo(BigDecimal.ZERO) <= 0) {
                        request.setAttribute("reservePriceError", "Reserve price must be greater than 0");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("reservePriceError", "Please enter a valid reserve price");
                    isValid = false;
                }
            }
            
            int categoryId = 0;
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("categoryIdError", "Please select a category");
                isValid = false;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            
            try {
                startTime = LocalDateTime.parse(startTimeStr, formatter);
            } catch (Exception e) {
                request.setAttribute("startTimeError", "Please enter a valid start time");
                isValid = false;
            }
            
            try {
                endTime = LocalDateTime.parse(endTimeStr, formatter);
            } catch (Exception e) {
                request.setAttribute("endTimeError", "Please enter a valid end time");
                isValid = false;
            }
            
            if (startTime != null && endTime != null) {
                if (startTime.isAfter(endTime)) {
                    request.setAttribute("endTimeError", "End time must be after start time");
                    isValid = false;
                }
            }
            
            // Process image upload if provided
            String imageUrl = item.getImageUrl(); // Keep existing image by default
            try {
                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    // Delete old image if exists
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        ImageUploadUtil.deleteImage(imageUrl, getServletContext());
                    }
                    
                    // Upload new image
                    imageUrl = ImageUploadUtil.uploadImage(filePart, getServletContext());
                }
            } catch (Exception e) {
                request.setAttribute("imageError", "Error uploading image: " + e.getMessage());
                isValid = false;
            }
            
            if (!isValid) {
                // Get all categories for dropdown
                List<Category> categories = categoryService.getAllCategories();
                
                // Set attributes for JSP
                request.setAttribute("item", item);
                request.setAttribute("categories", categories);
                request.setAttribute("title", title);
                request.setAttribute("description", description);
                request.setAttribute("startingPrice", startingPriceStr);
                request.setAttribute("reservePrice", reservePriceStr);
                request.setAttribute("categoryId", categoryIdStr);
                request.setAttribute("startTime", startTimeStr);
                request.setAttribute("endTime", endTimeStr);
                request.setAttribute("status", status);
                
                // Forward back to edit page with errors
                request.getRequestDispatcher("items/edit.jsp").forward(request, response);
                return;
            }
            
            // Update item object
            item.setTitle(title);
            item.setDescription(description);
            item.setStartingPrice(startingPrice);
            item.setReservePrice(reservePrice);
            item.setImageUrl(imageUrl);
            item.setCategoryId(categoryId);
            item.setStartTime(startTime);
            item.setEndTime(endTime);
            item.setStatus(status);
            
            // Save updated item
            boolean updated = itemService.updateItem(item);
            
            if (updated) {
                LOGGER.log(Level.INFO, "Item updated: {0}", itemId);
                
                // Redirect to item details with success message
                response.sendRedirect("ItemDetailsServlet?id=" + itemId + "&message=Item updated successfully");
            } else {
                request.setAttribute("errorMessage", "Failed to update item");
                
                // Get all categories for dropdown
                List<Category> categories = categoryService.getAllCategories();
                
                // Set attributes for JSP
                request.setAttribute("item", item);
                request.setAttribute("categories", categories);
                
                // Forward back to edit page with error
                request.getRequestDispatcher("items/edit.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid item ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item ID");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in UpdateItemServlet", e);
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
