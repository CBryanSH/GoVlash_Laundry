package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import database.Connect;

/**
 * UserModel
 * ---------
 * Represents a user account in the system.
 *
 * Roles supported:
 * - Customer
 * - Admin
 * - Laundry Staff
 * - Receptionist
 *
 * Responsibilities:
 * - Register users
 * - Authenticate login
 * - Fetch All Employee
 * - Check username/email uniqueness
 */


public class UserModel {
	
	// Table Attributes
    private int userID;           
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userGender;
    private String userDOB;     
    private String userRole;
    
    // Database Connection instance
    private final Connect db = Connect.getConnection();
    
    // Empty constructor (used for method calls)
    public UserModel() {}

    // ===== Constructor for REGISTER (no ID yet) =====
    public UserModel(String userName, String userEmail,
                     String userPassword, String userGender,
                     String userDOB, String userRole) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.userDOB = userDOB;
        this.userRole = userRole;
    }

    // ===== Constructor for LOGIN (data from DB) =====
    public UserModel(int userID, String userName, String userEmail,
                     String userPassword, String userGender,
                     String userDOB, String userRole) {

        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.userDOB = userDOB;
        this.userRole = userRole;
    }
    
    // ===== CHECK USERNAME EXISTS =====
    public static boolean isUsernameExists(String username) {
        String query =
            "SELECT * FROM Users WHERE UserName = '" + username + "'";

        try (ResultSet rs = Connect.getConnection().executeQuery(query)) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ===== CHECK EMAIL EXISTS =====
    public static boolean isEmailExists(String email) {
        String query =
            "SELECT * FROM Users WHERE UserEmail = '" + email + "'";

        try (ResultSet rs = Connect.getConnection().executeQuery(query)) {
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Get Laundry Staffs
    public List<UserModel> getLaundryStaff() {
        List<UserModel> list = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE UserRole = 'Laundry Staff'";

        try (ResultSet rs = db.executeQuery(query)) {
            while (rs.next()) {
                list.add(new UserModel(
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("UserEmail"),
                    rs.getString("UserPassword"),
                    rs.getString("UserGender"),
                    rs.getString("UserDOB"),
                    rs.getString("UserRole")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // ===== GET Employee List =====
    public List<UserModel> getAllEmployees() {
        List<UserModel> list = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE UserRole IN ('Admin', 'Laundry Staff', 'Receptionist')";

        try (ResultSet rs = db.executeQuery(query)) {
            while (rs.next()) {
                list.add(new UserModel(
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("UserEmail"),
                    rs.getString("UserPassword"),
                    rs.getString("UserGender"),
                    rs.getString("UserDOB"),
                    rs.getString("UserRole")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== SAVE USER =====
    public void save() {
        String query = "INSERT INTO Users (UserName, UserEmail, UserPassword, UserGender, UserDOB, UserRole) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(query);
            ps.setString(1, userName);
            ps.setString(2, userEmail);
            ps.setString(3, userPassword);
            ps.setString(4, userGender);
            ps.setString(5, userDOB);
            ps.setString(6, userRole);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // === Getting Log In Accounts Data ===
    public UserModel getLoginUser(String username, String password) {
        UserModel user = null;
        String query = "SELECT * FROM Users WHERE UserName = ? AND UserPassword = ?";

        try {
            PreparedStatement ps = db.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new UserModel(
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("UserEmail"),
                    rs.getString("UserPassword"),
                    rs.getString("UserGender"),
                    rs.getString("UserDOB"),
                    rs.getString("UserRole")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // ===== Getters =====
    public int getUserID() { return userID; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getUserPassword() { return userPassword; }
    public String getUserGender() { return userGender; }
    public String getUserDOB() { return userDOB; }
    public String getUserRole() { return userRole; }
}
