package com.bidmaster.service;

import java.sql.SQLException;
import java.util.List;
import com.bidmaster.model.Transaction;

/**
 * Service interface for Transaction operations
 */
public interface TransactionService {
    /**
     * Creates a new transaction
     * 
     * @param transaction The transaction to create
     * @return The ID of the created transaction
     * @throws SQLException if a database error occurs
     */
    int createTransaction(Transaction transaction) throws SQLException;
    
    /**
     * Gets a transaction by its ID
     * 
     * @param transactionId The transaction ID
     * @return The transaction, or null if not found
     * @throws SQLException if a database error occurs
     */
    Transaction getTransactionById(int transactionId) throws SQLException;
    
    /**
     * Gets transactions by seller ID
     * 
     * @param sellerId The seller ID
     * @return List of transactions for the seller
     * @throws SQLException if a database error occurs
     */
    List<Transaction> getTransactionsBySeller(int sellerId) throws SQLException;
    
    /**
     * Gets transactions by buyer ID
     * 
     * @param buyerId The buyer ID
     * @return List of transactions for the buyer
     * @throws SQLException if a database error occurs
     */
    List<Transaction> getTransactionsByBuyer(int buyerId) throws SQLException;
    
    /**
     * Gets transactions by item ID
     * 
     * @param itemId The item ID
     * @return List of transactions for the item
     * @throws SQLException if a database error occurs
     */
    List<Transaction> getTransactionsByItem(int itemId) throws SQLException;
    
    /**
     * Gets all transactions
     * 
     * @return List of all transactions
     * @throws SQLException if a database error occurs
     */
    List<Transaction> getAllTransactions() throws SQLException;
    
    /**
     * Updates a transaction's status
     * 
     * @param transactionId The transaction ID
     * @param status The new status
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean updateTransactionStatus(int transactionId, String status) throws SQLException;
    
    /**
     * Updates a transaction's payment method
     * 
     * @param transactionId The transaction ID
     * @param paymentMethod The new payment method
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean updateTransactionPaymentMethod(int transactionId, String paymentMethod) throws SQLException;
    
    /**
     * Gets the total revenue from completed transactions
     * 
     * @return The total revenue
     * @throws SQLException if a database error occurs
     */
    double getTotalRevenue() throws SQLException;
    
    /**
     * Gets the revenue for a specific period
     * 
     * @param days The number of days to look back
     * @return The revenue for the period
     * @throws SQLException if a database error occurs
     */
    double getRevenueForPeriod(int days) throws SQLException;
}
