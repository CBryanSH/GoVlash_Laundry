package controller;

import java.util.List;
import model.TransactionModel;
import model.UserModel;

/**
 * ReceptionistController
 * ----------
 * Handles transaction assignment workflow between receptionists
 * and laundry staff.
 *
 * Responsibilities:
 * - Retrieve unassigned pending transactions
 * - Retrieve available laundry staff
 * - Assign transactions to staff members
 *
 * Notes:
 * - Acts as the Controller layer in MVC
 * - Ensures assignment logic is separated from the View
 * - Relies on Model methods for database updates
 */

public class ReceptionistController {
	// Defined here so that it doesn't ask for static later
    private TransactionModel transactionModel;
    private UserModel userModel;

    public ReceptionistController() {
        this.transactionModel = new TransactionModel();
        this.userModel = new UserModel();
    }

    // Get Transactions that need assignment (Have no StaffID & ReceptionistID)
    public List<TransactionModel> getUnassignedTransactions() {
        return transactionModel.getUnassignedPendingTransactions();
    }

    // Get list of Laundry Staff Employees
    public List<UserModel> getLaundryStaffList() {
        return userModel.getLaundryStaff();
    }

    // Assigning a Transaction with no StaffID & ReceptionistID to the selected Laundry Staff
    public String assignJob(TransactionModel transaction, UserModel staff, int receptionistID) {
        if (transaction == null) return "Please select a Transaction.";
        if (staff == null) return "Please select a Staff worker.";

        // Execute Update, Pass the selected StaffID and the logged-in Receptionist's ID
        transaction.assignStaff(staff.getUserID(), receptionistID);
        
        return "Success";
    }
}