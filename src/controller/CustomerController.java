package controller;

import java.util.ArrayList;
import java.util.List;

import model.NotificationModel;
import model.ServiceModel;
import model.TransactionModel;

/**
 * CustomerController
 * ----------
 * Handles customer-related operations such as creating transactions,
 * viewing transaction history, and managing notifications.
 *
 * Responsibilities:
 * - Validate and create new transactions
 * - Retrieve customer transaction history
 * - Retrieve, read, and delete notifications
 * - Provide available services for customer selection
 *
 * Notes:
 * - Part of the Controller layer in MVC
 * - Contains business rules and input validation
 * - Delegates database operations to Model classes
 */

public class CustomerController {
	
	// ===== CREATE TRANSACTION =====
	public String createTransaction(int serviceID, int customerID, String weightStr, String notes) {
        
        // Validate weight and TransactionNotes
        double weight;
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            return "Weight must be a valid number.";
        }

        if (weight < 2 || weight > 50) {
            return "Weight must be between 2 and 50kg.";
        }

        if (notes.length() > 250) {
            return "Notes cannot exceed 250 characters.";
        }
        
        // Makes a new Transaction Object
        TransactionModel t = new TransactionModel(serviceID, customerID, weight, notes);
        t.save();	// Save it to the database

        return "Success";
    }

    // ===== TRANSACTION HISTORY =====
    public ArrayList<TransactionModel> getTransactionHistory(int customerID) {
    	// Get all Transaction according to its customerID (Currently logged in)
    	TransactionModel tmodel = new TransactionModel();
        return tmodel.getByCustomer(customerID);
    }

    // ===== NOTIFICATIONS =====
    public ArrayList<NotificationModel> getNotifications(int customerID) {
    	// Get all Notification according to its recipientsID (Currently logged in)
        NotificationModel nmodel = new NotificationModel();
        return nmodel.getByRecipient(customerID);
    }
    
    // Mark Selected Notification as read
    public void markNotificationRead(NotificationModel notification) {
        notification.markAsRead();
    }
    
    // Delete Selected Notification
    public void deleteNotification(NotificationModel notification) {
        notification.delete();
    }
    
    // === Fetch all services for the ComboBox ===
    public List<ServiceModel> getAllServices() {
        ServiceModel serviceModel = new ServiceModel();
        return serviceModel.getAllServices();
    }
}
