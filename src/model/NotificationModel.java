package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import database.Connect;

/**
 * NotificationModel
 * -----------------
 * This model represents notifications in the system.
 * A notification is sent to a user when a transaction-related event occurs.
 *
 * Responsibilities:
 * - Store notification data
 * - Insert new notifications into the database
 * - Retrieve notifications for a specific user
 * - Update read status
 * - Delete notifications
 */

public class NotificationModel {
	// Table Attributes
	private int notificationID;
	private int transactionID;
    private int recipientID;
    private String message;
    private String createdAt;
    private boolean isRead;
    
    // Database Connection instance
    private final Connect db = Connect.getConnection();
    
    // Empty constructor (used for method calls)
    public NotificationModel() {}

    // Constructor for FETCHING
    public NotificationModel(int notificationID, int recipientID, int transactionID, String message, String createdAt, boolean isRead) {
        this.notificationID = notificationID;
        this.recipientID = recipientID;
        this.transactionID = transactionID;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Constructor for SAVING
    public NotificationModel(int recipientID, int transactionID, String message) {
        this.recipientID = recipientID;
        this.transactionID = transactionID;
        this.message = message;
        this.isRead = false;
    }

    // Getters
    public int getNotificationID() { return notificationID; }
    public int getRecipientID() { return recipientID; }
    public int getTransactionID() { return transactionID; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }

    // Save new notification and CreatedAt is automatically set to NOW()
    public void save() {
        String query = "INSERT INTO Notifications (RecipientID, TransactionID, NotificationMessage, CreatedAt, IsRead) VALUES (?, ?, ?, NOW(), ?)";
        
        try {
            if (db == null) return;

            PreparedStatement ps = db.prepareStatement(query);
            
            ps.setInt(1, recipientID);
            ps.setInt(2, transactionID); 
            ps.setString(3, message);   
            ps.setInt(4, 0);             
            
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get notifications by recipientID (CurrentlyLoggedin)
    public ArrayList<NotificationModel> getByRecipient(int recipientID) {
        ArrayList<NotificationModel> list = new ArrayList<>();
        
        // Ordered by date CreatedAt in descending
        String query = "SELECT * FROM Notifications WHERE RecipientID = " + recipientID + " ORDER BY CreatedAt DESC";

        try (ResultSet rs = db.executeQuery(query)) {
            while (rs.next()) {
                list.add(new NotificationModel(
                    rs.getInt("NotificationID"),
                    rs.getInt("RecipientID"),
                    rs.getInt("TransactionID"), 
                    rs.getString("NotificationMessage"),
                    rs.getString("CreatedAt"),
                    rs.getBoolean("IsRead")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Mark notifications as read (Change IsRead boolean to true)
    public void markAsRead() {
        String query =
            "UPDATE Notifications SET IsRead = true " +
            "WHERE NotificationID = " + notificationID;

        db.executeUpdate(query);
        this.isRead = true;
    }

    // Delete notification
    public void delete() {
        String query =
            "DELETE FROM Notifications " +
            "WHERE NotificationID = " + notificationID;

        db.executeUpdate(query);
    }
}
