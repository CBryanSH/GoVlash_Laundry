package controller;

import java.time.LocalDate;
import java.time.Period;

import model.UserModel;

/**
 * RegisterController
 * ----------
 * Handles user registration and account validation.
 *
 * Responsibilities:
 * - Validate registration input fields
 * - Enforce username and email uniqueness
 * - Validate password rules and age requirement
 * - Save new user data through the UserModel
 *
 * Notes:
 * - Acts as the Controller layer in MVC
 * - Performs all business validations before persistence
 * - Does not handle UI components directly
 */

public class RegisterController {
	// Register Input Validation
	public String register(UserModel user, String confirmPassword) {

        // ===== EMPTY CHECK =====
        if (user.getUserName().isEmpty() ||
            user.getUserEmail().isEmpty() ||
            user.getUserPassword().isEmpty() ||
            user.getUserGender() == null ||
            user.getUserDOB() == null) {

            return "All fields must be filled.";
        }

        // ===== USERNAME UNIQUE =====
        if (UserModel.isUsernameExists(user.getUserName())) {
            return "Username already exists.";
        }

        // ===== EMAIL FORMAT =====
        if (!user.getUserEmail().endsWith("@email.com")) {
            return "Email must end with @email.com";
        }

        // ===== EMAIL UNIQUE =====
        if (UserModel.isEmailExists(user.getUserEmail())) {
            return "Email already exists.";
        }

        // ===== PASSWORD LENGTH =====
        if (user.getUserPassword().length() < 6) {
            return "Password must be at least 6 characters long.";
        }

        // ===== PASSWORD MATCH =====
        if (!user.getUserPassword().equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        // ===== AGE CHECK =====
        LocalDate dob = LocalDate.parse(user.getUserDOB());
        int age = Period.between(dob, LocalDate.now()).getYears();

        if (age < 12) {
            return "You must be at least 12 years old.";
        }

        // ===== SAVE =====
        user.save();
        return "Success";
    }
}
