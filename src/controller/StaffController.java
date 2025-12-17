package controller;

import java.util.List;
import model.TransactionModel;

/**
 * StaffController
 * ----------
 * Handles operations related to laundry staff work assignments.
 *
 * Responsibilities:
 * - Retrieve pending jobs assigned to the logged-in staff
 * - Update transaction status when a job is completed
 *
 * Notes:
 * - Part of the Controller layer in MVC
 * - Focused only on staff-related transaction logic
 * - Delegates all database operations to TransactionModel
 */

public class StaffController {
	// Defined here so that it doesn't ask for static later
    private TransactionModel transactionModel;

    public StaffController() {
        this.transactionModel = new TransactionModel();
    }

    // Get ONLY Pending Transactions assigned to the specific logged-in staff
    public List<TransactionModel> getPendingTransactions(int staffID) {
        return transactionModel.getJobQueueForStaff(staffID);
    }

    // Finish an Order (Change TransactionStatus)
    public void finishTransaction(TransactionModel t) {
        if (t != null) {
            // Update Database to 'Finished'
            t.updateTransactionStatus(); 
        }
    }
}