package com.bidmaster.controller.item;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.bidmaster.model.Item;
import com.bidmaster.service.ItemService;
import com.bidmaster.service.impl.ItemServiceImpl;
import com.bidmaster.util.ImageUploadUtil;
import com.bidmaster.util.ValidationUtil;

@WebServlet("/CreateItemServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15    // 15 MB
)
public class CreateItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateItemServlet.class.getName());
    
    private ItemService itemService;
    
    public void init() {
        itemService = new ItemServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        
        request.getRequestDispatcher("items/create.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("LoginServlet");
            return;
        }
        
        int sellerId = (int) session.getAttribute("userId");
        
        // Get form parameters
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String startingPriceStr = request.getParameter("startingPrice");
        String reservePriceStr = request.getParameter("reservePrice");
        String categoryIdStr = request.getParameter("categoryId");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        
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
            
            if (startTime.isBefore(LocalDateTime.now())) {
                request.setAttribute("startTimeError", "Start time must be in the future");
                isValid = false;
            }
        }
        
        // Process image upload
        String imageUrl = null;
        try {
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                imageUrl = ImageUploadUtil.uploadImage(filePart, getServletContext());
            }
        } catch (Exception e) {
            request.setAttribute("imageError", "Error uploading image: " + e.getMessage());
            isValid = false;
        }
        
        if (!isValid) {
            // Return to form with error messages
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("startingPrice", startingPriceStr);
            request.setAttribute("reservePrice", reservePriceStr);
            request.setAttribute("categoryId", categoryIdStr);
            request.setAttribute("startTime", startTimeStr);
            request.setAttribute("endTime", endTimeStr);
            request.getRequestDispatcher("items/create.jsp").forward(request, response);
            return;
        }
        
        try {
            // Create item object
            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setStartingPrice(startingPrice);
            item.setReservePrice(reservePrice);
            item.setCurrentPrice(startingPrice);
            item.setImageUrl(imageUrl);
            item.setCategoryId(categoryId);
            item.setSellerId(sellerId);
            item.setStartTime(startTime);
            item.setEndTime(endTime);
            item.setStatus("pending");
            
            // Save item
            int itemId = itemService.createItem(item);
            
            LOGGER.log(Level.INFO, "Item created: {0}, ID: {1}", new Object[]{title, itemId});
            
            // Redirect to item details page
            response.sendRedirect("ItemDetailsServlet?id=" + itemId + "&message=Item created successfully");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating item", e);
            request.setAttribute("errorMessage", "Error creating item: " + e.getMessage());
            
            // Return to form with error message
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("startingPrice", startingPriceStr);
            request.setAttribute("reservePrice", reservePriceStr);
            request.setAttribute("categoryId", categoryIdStr);
            request.setAttribute("startTime", startTimeStr);
            request.setAttribute("endTime", endTimeStr);
            request.getRequestDispatcher("items/create.jsp").forward(request, response);
        }
    }
}
