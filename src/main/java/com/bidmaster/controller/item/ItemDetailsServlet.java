package com.bidmaster.controller.item;

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

import com.bidmaster.model.Bid;
import com.bidmaster.model.Item;
import com.bidmaster.model.User;
import com.bidmaster.model.Category;
import com.bidmaster.service.BidService;
import com.bidmaster.service.CategoryService;
import com.bidmaster.service.ItemService;
import com.bidmaster.service.UserService;
import com.bidmaster.service.WatchlistService;
import com.bidmaster.service.impl.BidServiceImpl;
import com.bidmaster.service.impl.CategoryServiceImpl;
import com.bidmaster.service.impl.ItemServiceImpl;
import com.bidmaster.service.impl.UserServiceImpl;
import com.bidmaster.service.impl.WatchlistServiceImpl;

/**
 * Servlet implementation class ItemDetailsServlet
 * Handles displaying item details
 */
@WebServlet("/ItemDetailsServlet")
public class ItemDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ItemDetailsServlet.class.getName());
    
    private ItemService itemService;
    private UserService userService;
    private BidService bidService;
    private CategoryService categoryService;
    private WatchlistService watchlistService;
    
    public void init() {
        itemService = new ItemServiceImpl();
        userService = new UserServiceImpl();
        bidService = new BidServiceImpl();
        categoryService = new CategoryServiceImpl();
        watchlistService = new WatchlistServiceImpl();
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
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
            
            // Get seller information
            User seller = userService.getUserById(item.getSellerId());
            
            // Get category information
            Category category = categoryService.getCategoryById(item.getCategoryId());
            
            // Get bids for this item
            List<Bid> bids = bidService.getBidsByItem(itemId);
            
            // Get bid count
            int bidCount = bidService.countBidsForItem(itemId);
            
            // Get similar items (same category)
            List<Item> similarItems = itemService.getItemsByCategory(item.getCategoryId());
            
            // Check if item is in user's watchlist
            boolean inWatchlist = false;
            if (request.getSession(false) != null && request.getSession().getAttribute("userId") != null) {
                int userId = (int) request.getSession().getAttribute("userId");
                inWatchlist = watchlistService.isItemInWatchlist(userId, itemId);
            }
            
            // Set attributes for JSP
            request.setAttribute("item", item);
            request.setAttribute("seller", seller);
            request.setAttribute("category", category);
            request.setAttribute("bids", bids);
            request.setAttribute("bidCount", bidCount);
            request.setAttribute("similarItems", similarItems);
            request.setAttribute("inWatchlist", inWatchlist);
            
            // Forward to item details page
            request.getRequestDispatcher("items/details.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid item ID format", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item ID");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error in ItemDetailsServlet", e);
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
        // Forward POST requests to doGet
        doGet(request, response);
    }
}
