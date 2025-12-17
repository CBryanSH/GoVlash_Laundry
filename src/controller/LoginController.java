package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.UserModel;

/**
 * LoginController
 * ----------
 * Handles user authentication and login validation.
 *
 * Responsibilities:
 * - Validate login input (username and password)
 * - Request user authentication from the UserModel
 * - Return the authenticated user for role-based navigation
 *
 * Notes:
 * - Part of the Controller layer in MVC
 * - Contains login-related business rules
 * - UI feedback is minimal to keep the View simple
 */

public class LoginController {
	// Defined here so that it doesn't ask for static later
	private UserModel userModel;

    public LoginController() {
        this.userModel = new UserModel();
    }

    public UserModel validateLogin(String username, String password) {
        // === Validate Input ===
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Validation Error", "Username and Password cannot be empty.");
            return null;
        }

        // === Request Data from Model ===
        UserModel user = userModel.getLoginUser(username, password);

        // === Handle specific failure case ===
        if (user == null) {
            showAlert("Login Failed", "Invalid Username or Password.");
        }

        return user;
    }

    // Helper to show alerts (keeps View cleaner)
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
