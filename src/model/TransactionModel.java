package model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import database.Connect;

/**
 * TransactionModel
 * ----------------
 * Represents a laundry transaction made by a customer.
 *
 * Responsibilities:
 * - Create new transactions
 * - Fetch transactions with different filters (status, customer, staff)
 * - Assign staff to transactions
 * - Update transaction status
 *
 * Handles nullable fields such as:
 * - ReceptionistID
 * - LaundryStaffID
 */

public class TransactionModel {
	
	// Table Attributes
	private int transactionID;
    private int serviceID;
    private int customerID;
    private Integer receptionistID;   	// Nullable
    private Integer laundryStaffID;    	// Nullable
    private String transactionDate;
    private String transactionStatus;
    private double totalWeight;
    private String transactionNotes;
    
    // Database Connection instance
    private final Connect db = Connect.getConnection();

    // For fetching
    public TransactionModel() {}
    
    // Constructor for creating transaction (Customer)
    public TransactionModel(int serviceID, int customerID,
                            double totalWeight, String transactionNotes) {

        this.serviceID = serviceID;
        this.customerID = customerID;
        this.totalWeight = totalWeight;
        this.transactionNotes = transactionNotes;
        this.transactionStatus = "Pending";
    }

    // Constructor for fetching from DB
    public TransactionModel(int transactionID, int serviceID, int customerID,
                            Integer receptionistID, Integer laundryStaffID,
                            String transactionDate, String transactionStatus,
                            double totalWeight, String transactionNotes) {

        this.transactionID = transactionID;
        this.serviceID = serviceID;
        this.customerID = customerID;
        this.receptionistID = receptionistID;
        this.laundryStaffID = laundryStaffID;
        this.transactionDate = transactionDate;
        this.transactionStatus = transactionStatus;
        this.totalWeight = totalWeight;
        this.transactionNotes = transactionNotes;
    }

    // Getters only (no setters for safety)
    public int getTransactionID() { return transactionID; }
    public int getServiceID() { return serviceID; }
    public int getCustomerID() { return customerID; }
    public Integer getReceptionistID() { return receptionistID; }
    public Integer getLaundryStaffID() { return laundryStaffID; }
    public String getTransactionDate() { return transactionDate; }
    public String getTransactionStatus() { return transactionStatus; }
    public double getTotalWeight() { return totalWeight; }
    public String getTransactionNotes() { return transactionNotes; }
    
    public List<TransactionModel> getAllTransactions(String statusFilter) {
        List<TransactionModel> list = new ArrayList<>();
        String query = "SELECT * FROM Transactions";

        if ("Finished".equalsIgnoreCase(statusFilter)) {
            query += " WHERE TransactionStatus = 'Finished'";
        } else if ("Pending".equalsIgnoreCase(statusFilter)) {
            query += " WHERE TransactionStatus = 'Pending'";
        }
        
        query += " ORDER BY TransactionDate DESC"; 

        try (ResultSet rs = db.executeQuery(query)) {
            while (rs.next()) {
                list.add(new TransactionModel(
                    rs.getInt("TransactionID"),
                    rs.getInt("ServiceID"),
                    rs.getInt("CustomerID"),
                    (Integer) rs.getObject("ReceptionistID"), // Handle Nullable
                    (Integer) rs.getObject("LaundryStaffID"), // Handle Nullable
                    rs.getString("TransactionDate"),
                    rs.getString("TransactionStatus"),
                    rs.getDouble("TotalWeight"),
                    rs.getString("TransactionNotes")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Get Pending transactions assigned to a specific Laundry Staff
    public List<TransactionModel> getJobQueueForStaff(int staffID) {
        List<TransactionModel> list = new ArrayList<>();
        
        // Filter by BOTH Staff ID and 'Pending' status
        String query = "SELECT * FROM Transactions WHERE LaundryStaffID = " + staffID + 
                       " AND TransactionStatus = 'Pending' " + 
                       " ORDER BY TransactionDate DESC";

        try (ResultSet rs = db.executeQuery(query)) {
            while (rs.next()) {
                list.add(new TransactionModel(
                    rs.getInt("TransactionID"),
                    rs.getInt("ServiceID"),
                    rs.getInt("CustomerID"),
                    (Integer) rs.getObject("ReceptionistID"),
                    (Integer) rs.getObject("LaundryStaffID"),
                    rs.getString("TransactionDate"),
                    rs.getString("TransactionStatus"),
                    rs.getDouble("TotalWeight"),
                    rs.getString("TransactionNotes")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<TransactionModel> getByCustomer(int customerID) {
        ArrayList<TransactionModel> list = new ArrayList<>();

        String query =
            "SELECT * FROM Transactions " +
            "WHERE CustomerID = " + customerID +
            " ORDER BY TransactionDate DESC";

        ResultSet rs = db.executeQuery(query);

        try {
            while (rs.next()) {
                list.add(new TransactionModel(
                    rs.getInt("TransactionID"),
                    rs.getInt("ServiceID"),
                    rs.getInt("CustomerID"),
                    (Integer) rs.getObject("ReceptionistID"),
                    (Integer) rs.getObject("LaundryStaffID"),
                    rs.getString("TransactionDate"),
                    rs.getString("TransactionStatus"),
                    rs.getDouble("TotalWeight"),
                    rs.getString("TransactionNotes")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    // Filters for Status = 'Pending' AND LaundryStaffID IS NULL
    public List<TransactionModel> getUnassignedPendingTransactions() {
        List<TransactionModel> list = new ArrayList<>();
        String query = "SELECT * FROM Transactions WHERE TransactionStatus = 'Pending' AND LaundryStaffID IS NULL";
        
        try (ResultSet rs = db.executeQuery(query)) {
            while (rs.next()) {
                list.add(new TransactionModel(
                    rs.getInt("TransactionID"),
                    rs.getInt("ServiceID"),
                    rs.getInt("CustomerID"),
                    (Integer) rs.getObject("ReceptionistID"),
                    (Integer) rs.getObject("LaundryStaffID"),
                    rs.getString("TransactionDate"),
                    rs.getString("TransactionStatus"),
                    rs.getDouble("TotalWeight"),
                    rs.getString("TransactionNotes")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Updates LaundryStaffID AND ReceptionistID
    public void assignStaff(int staffID, int receptionistID) {
        String query = String.format(
            "UPDATE Transactions SET LaundryStaffID = %d, ReceptionistID = %d WHERE TransactionID = %d",
            staffID, receptionistID, this.transactionID
        );
        db.executeUpdate(query);
    }
    
    public void updateTransactionStatus() {
        String query = String.format(
            "UPDATE Transactions SET TransactionStatus = 'Finished' WHERE TransactionID = %d",
            this.transactionID
        );
        db.executeUpdate(query);
        
        this.transactionStatus = "Finished";
    }
    
    public void save() {
        String query =
            "INSERT INTO Transactions " +
            "(ServiceID, CustomerID, TransactionDate, TransactionStatus, TotalWeight, TransactionNotes) " +
            "VALUES (" +
            serviceID + ", " +
            customerID + ", " +
            "NOW(), 'Pending', " +
            totalWeight + ", " +
            "'" + transactionNotes + "'" +
            ")";

        db.executeUpdate(query);
    }


}
