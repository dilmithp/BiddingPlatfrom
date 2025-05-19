package com.bidmaster.controller.bid;

import java.io.IOException;
import java.math.BigDecimal;
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
import com.bidmaster.service.BidService;
import com.bidmaster.service.ItemService;
import com.bidmaster.service.impl.BidServiceImpl;
import com.bidmaster.service.impl.ItemServiceImpl;

@WebServlet("/PlaceBidServlet")
public class PlaceBidServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PlaceBidServlet.class.getName());
    
    private BidService bidService;
    private ItemService itemService;
    
    public void init() {
        bidService = new BidServiceImpl();
        itemService = new ItemServiceImpl();
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
        
        int bidderId = (int) session.getAttribute("userId");
        
        try {
            // Get parameters
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            BigDecimal bidAmount = new BigDecimal(request.getParameter("bidAmount"));
            
            // Get item details
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                request.setAttribute("errorMessage", "Item not found");
                request.getRequestDispatcher("ItemDetailsServlet?id=" + itemId).forward(request, response);
                return;
            }
            
            // Check if bidder is not the seller
            if (item.getSellerId() == bidderId) {
                request.setAttribute("errorMessage", "You cannot bid on your own item");
                request.getRequestDispatcher("ItemDetailsServlet?id=" + itemId).forward(request, response);
                return;
            }
            
            // Check if item is active
            if (!"active".equals(item.getStatus())) {
                request.setAttribute("errorMessage", "This item is not available for bidding");
                request.getRequestDispatcher("ItemDetailsServlet?id=" + itemId).forward(request, response);
                return;
            }
            
            // Check if bid amount is higher than current price
            if (bidAmount.compareTo(item.getCurrentPrice()) <= 0) {
                request.setAttribute("errorMessage", "Bid amount must be higher than current price");
                request.getRequestDispatcher("ItemDetailsServlet?id=" + itemId).forward(request, response);
                return;
            }
            
            // Place bid
            bidService.placeBid(itemId, bidderId, bidAmount);
            
            // Redirect to item details with success message
            request.setAttribute("successMessage", "Your bid has been placed successfully");
            request.getRequestDispatcher("ItemDetailsServlet?id=" + itemId).forward(request, response);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid number format", e);
            request.setAttribute("errorMessage", "Invalid input format");
            request.getRequestDispatcher("ItemListServlet").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error placing bid", e);
            request.setAttribute("errorMessage", "Error placing bid: " + e.getMessage());
            
            // Try to get itemId for redirection
            try {
                int itemId = Integer.parseInt(request.getParameter("itemId"));
                request.getRequestDispatcher("ItemDetailsServlet?id=" + itemId).forward(request, response);
            } catch (NumberFormatException ex) {
                request.getRequestDispatcher("ItemListServlet").forward(request, response);
            }
        }
    }
}
