package com.bidmaster.controller.seller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.bidmaster.model.Item;
import com.bidmaster.model.Bid;
import com.bidmaster.model.Category;
import com.bidmaster.model.Transaction;
import com.bidmaster.service.ItemService;
import com.bidmaster.service.BidService;
import com.bidmaster.service.CategoryService;
import com.bidmaster.service.TransactionService;
import com.bidmaster.service.impl.ItemServiceImpl;
import com.bidmaster.service.impl.BidServiceImpl;
import com.bidmaster.service.impl.CategoryServiceImpl;
import com.bidmaster.service.impl.TransactionServiceImpl;

@WebServlet("/SellerDashboardServlet")
public class SellerDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SellerDashboardServlet.class.getName());
    
    private ItemService itemService;
    private BidService bidService;
    private CategoryService categoryService;
    private TransactionService transactionService;
    
    public void init() {
        itemService = new ItemServiceImpl();
        bidService = new BidServiceImpl();
        categoryService = new CategoryServiceImpl();
        transactionService = new TransactionServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in and is a seller
        if (session == null || session.getAttribute("userId") == null || 
                !"seller".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int sellerId = (int) session.getAttribute("userId");
        
        try {
            // Get active listings
            List<Item> activeListings = itemService.getActiveItemsBySeller(sellerId);
            
            // Get all seller items
            List<Item> allSellerItems = itemService.getItemsBySeller(sellerId);
            
            // Get recent bids on seller items
            List<Bid> recentBids = bidService.getRecentBidsForSeller(sellerId, 20);
            
            // Get completed sales
            List<Transaction> completedSales = transactionService.getCompletedSalesBySeller(sellerId);
            
            // Get all categories for item creation/editing
            List<Category> categories = categoryService.getAllCategories();
            
            // Get seller statistics
            Map<String, Object> stats = getSellerStats(sellerId);
            
            // Set attributes for JSP
            request.setAttribute("activeListings", activeListings);
            request.setAttribute("allSellerItems", allSellerItems);
            request.setAttribute("recentBids", recentBids);
            request.setAttribute("completedSales", completedSales);
            request.setAttribute("categories", categories);
            request.setAttribute("stats", stats);
            
            // Forward to seller dashboard
            request.getRequestDispatcher("seller-dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in SellerDashboardServlet", e);
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle any POST requests if needed
        doGet(request, response);
    }
    
    /**
     * Gets statistics for the seller dashboard
     * 
     * @param sellerId The seller ID
     * @return Map containing seller statistics
     * @throws SQLException if a database error occurs
     */
    private Map<String, Object> getSellerStats(int sellerId) throws SQLException {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        // Active listings count
        int activeListingsCount = itemService.getActiveItemCountBySeller(sellerId);
        stats.put("activeListingsCount", activeListingsCount);
        
        // Completed sales count
        int completedSalesCount = transactionService.getCompletedSalesCountBySeller(sellerId);
        stats.put("completedSalesCount", completedSalesCount);
        
        // Total bids received
        int totalBidsReceived = bidService.getTotalBidsCountForSeller(sellerId);
        stats.put("totalBidsReceived", totalBidsReceived);
        
        // Total revenue
        double totalRevenue = transactionService.getTotalRevenueBySeller(sellerId);
        stats.put("totalRevenue", totalRevenue);
        
        return stats;
    }
}
