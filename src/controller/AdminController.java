package controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import model.NotificationModel;
import model.ServiceModel;
import model.TransactionModel;
import model.UserModel;

/**
 * AdminController
 * ----------
 * Handles administrative operations such as service management, employee management,
 * transaction monitoring, and notification dispatching.
 *
 * Responsibilities:
 * - Manage laundry services (add, delete, retrieve)
 * - Manage employee accounts and validation
 * - View and filter all transactions
 * - Send system-generated notifications to customers
 *
 * Notes:
 * - Acts as the Controller layer in MVC
 * - Performs validation before delegating persistence to Models
 * - Does not directly interact with the View or UI components
 */

public class AdminController {
	// Defined here so that it doesn't ask for static later
	private TransactionModel transactionModel;
    private ServiceModel serviceModel;
    
    public AdminController() {
        this.transactionModel = new TransactionModel();
        this.serviceModel = new ServiceModel();
    }

    // ================= SERVICES =================
    public List<ServiceModel> getAllServices() {
        return serviceModel.getAllServices();
    }
    
    // Input Validation for adding services
    public String addService(String name, String desc, String priceStr, String durationStr) {
    	// Can't be empty
        if(name.isEmpty() || desc.isEmpty()) return "Fields cannot be empty";
        
        try {
            int price = Integer.parseInt(priceStr);
            int duration = Integer.parseInt(durationStr);
            
            // Validate Price and TransactionDuration
            if(price <= 0) return "Price must be > 0";
            if(duration < 1 || duration > 30) return "Duration must be 1-30 days";
            
            // 2. Create Model Object
            ServiceModel newService = new ServiceModel(name, desc, price, duration);
            
            // 3. Delegate to Model to Save
            newService.save();
            
            return "Success";
        } catch (NumberFormatException e) {
            return "Price and Duration must be numbers";
        }
    }
    
    // Deleting a service
    public void deleteService(int serviceID) {
        serviceModel.delete(serviceID);
    }

    // ================= EMPLOYEES =================
    public List<UserModel> getAllEmployees() {
    	return new UserModel().getAllEmployees();
    }

    // Validation for adding a new Employee
    public String addEmployee(UserModel employee, String confirmPassword) {
        // ===== EMPTY CHECK =====
        if (employee.getUserName().isEmpty() || 
            employee.getUserEmail().isEmpty() || 
            employee.getUserPassword().isEmpty() || 
            employee.getUserGender() == null || 
            employee.getUserDOB() == null ||
            employee.getUserRole() == null) {
            
            return "All fields must be filled.";
        }

        // ===== USERNAME UNIQUE =====
        if (UserModel.isUsernameExists(employee.getUserName())) {
            return "Username already exists.";
        }

        // ===== EMAIL FORMAT (Specific to Staff) =====
        if (!employee.getUserEmail().endsWith("@govlash.com")) {
            return "Email must end with '@govlash.com'.";
        }

        // ===== EMAIL UNIQUE =====
        if (UserModel.isEmailExists(employee.getUserEmail())) {
            return "Email already exists.";
        }

        // ===== PASSWORD LENGTH =====
        if (employee.getUserPassword().length() < 6) {
            return "Password must be at least 6 characters long.";
        }

        // ===== PASSWORD MATCH =====
        if (!employee.getUserPassword().equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        // ===== AGE CHECK (Specific to Staff: 17+) =====
        LocalDate dob = LocalDate.parse(employee.getUserDOB());
        int age = Period.between(dob, LocalDate.now()).getYears();

        if (age < 17) {
            return "Employees must be at least 17 years old.";
        }

        // ===== SAVE =====
        employee.save(); 
        return "Success";
    }

    // ================= TRANSACTIONS =================
    public List<TransactionModel> getTransactions(String statusFilter) {
        return transactionModel.getAllTransactions(statusFilter); 
    }

    // Auto-generate Notification message and send
    public void sendCompletionNotification(int transactionID, int customerID) {
        // 1. Create the premade message using String.format to insert the ID
        String message = String.format("Good news! Your order #%d is finished and ready for pickup. Thank you for choosing GoVlash!", transactionID);
        
        // 2. Create the notification object
        NotificationModel notif = new NotificationModel(customerID, transactionID, message);
        
        // 3. Save to database
        notif.save(); 
    }
}