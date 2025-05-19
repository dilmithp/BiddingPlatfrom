package com.bidmaster.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bidmaster.dao.FeedbackDAO;
import com.bidmaster.dao.impl.FeedbackDAOImpl;
import com.bidmaster.model.Feedback;
import com.bidmaster.service.FeedbackService;

/**
 * Implementation of FeedbackService interface
 */
public class FeedbackServiceImpl implements FeedbackService {
    private static final Logger LOGGER = Logger.getLogger(FeedbackServiceImpl.class.getName());
    
    private FeedbackDAO feedbackDAO;
    
    public FeedbackServiceImpl() {
        this.feedbackDAO = new FeedbackDAOImpl();
    }

    @Override
    public int createFeedback(Feedback feedback) throws SQLException {
        try {
            // Check if feedback already exists
            if (feedbackDAO.hasFeedbackForTransaction(feedback.getFromUserId(), feedback.getTransactionId())) {
                throw new SQLException("You have already provided feedback for this transaction");
            }
            
            return feedbackDAO.insertFeedback(feedback);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating feedback", e);
            throw e;
        }
    }

    @Override
    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        try {
            return feedbackDAO.getFeedbackById(feedbackId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedback by ID", e);
            throw e;
        }
    }

    @Override
    public List<Feedback> getFeedbackByTransaction(int transactionId) throws SQLException {
        try {
            return feedbackDAO.getFeedbackByTransaction(transactionId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedback by transaction", e);
            throw e;
        }
    }

    @Override
    public List<Feedback> getFeedbackGivenByUser(int userId) throws SQLException {
        try {
            return feedbackDAO.getFeedbackGivenByUser(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedback given by user", e);
            throw e;
        }
    }

    @Override
    public List<Feedback> getFeedbackReceivedByUser(int userId) throws SQLException {
        try {
            return feedbackDAO.getFeedbackReceivedByUser(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting feedback received by user", e);
            throw e;
        }
    }

    @Override
    public boolean updateFeedback(Feedback feedback) throws SQLException {
        try {
            return feedbackDAO.updateFeedback(feedback);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating feedback", e);
            throw e;
        }
    }

    @Override
    public boolean deleteFeedback(int feedbackId) throws SQLException {
        try {
            return feedbackDAO.deleteFeedback(feedbackId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting feedback", e);
            throw e;
        }
    }

    @Override
    public double getAverageRatingForUser(int userId) throws SQLException {
        try {
            return feedbackDAO.getAverageRatingForUser(userId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting average rating for user", e);
            throw e;
        }
    }

    @Override
    public boolean hasFeedbackForTransaction(int userId, int transactionId) throws SQLException {
        try {
            return feedbackDAO.hasFeedbackForTransaction(userId, transactionId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking feedback existence", e);
            throw e;
        }
    }
}
