package view;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Main;
import model.UserModel;

/**
 * LoginView
 * --------
 * Displays the login interface and handles user authentication input.
 *
 * Responsibilities:
 * - Collect username and password
 * - Display login feedback
 * - Redirect user based on role after successful login
 *
 * MVC Role:
 * - UI-only component
 * - Authentication handled by Controller and Model
 */

public class LoginView {
	// Set the Controller to loginController
	private LoginController loginController;

    public LoginView(Stage stage) {

        // ===== Title =====
        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        
        // ===== Controller =====
        this.loginController = new LoginController();

        // ===== Labels =====
        Label lblUsername = new Label("Username:");
        Label lblPassword = new Label("Password:");

        lblUsername.setStyle("-fx-font-size: 14px;");
        lblPassword.setStyle("-fx-font-size: 14px;");

        // ===== Input Fields =====
        TextField txtUsername = new TextField();
        PasswordField txtPassword = new PasswordField();

        txtUsername.setPrefWidth(250);
        txtPassword.setPrefWidth(250);

        // ===== Buttons =====
        Button btnLogin = new Button("Login");
        Button btnGuest = new Button("Register");

        btnLogin.setPrefWidth(200);
        btnGuest.setPrefWidth(200);

        // ===== Message Label =====
        Label lblMessage = new Label();
        lblMessage.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // ===== Form Layout =====
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        form.add(lblUsername, 0, 0);
        form.add(txtUsername, 1, 0);
        form.add(lblPassword, 0, 1);
        form.add(txtPassword, 1, 1);

        // ===== Main Layout =====
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                title,
                form,
                btnLogin,
                btnGuest,
                lblMessage
        );

        // ===== Scene =====
        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();

        // ===== Button Navigation =====
        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            // Ask Controller to validate
            UserModel user = loginController.validateLogin(username, password);

            // If valid, Redirect based on Role 
            if (user != null) {
                redirectUser(stage, user);
            }
        });

        btnGuest.setOnAction(e -> new RegisterView(stage));
    }
    
    // === Function for redirecting according to roles ===
    private void redirectUser(Stage stage, UserModel user) {
        String role = user.getUserRole();
        
        if (role.equalsIgnoreCase("ADMIN")) {
             new AdminView(stage, user);
        } 
        if (role.equalsIgnoreCase("CUSTOMER")) {
             new CustomerView(stage, user); 
        } 
        else if (role.equalsIgnoreCase("LAUNDRY STAFF")) {
             new StaffView(stage, user);
        } 
        else if (role.equalsIgnoreCase("RECEPTIONIST")) {
             new ReceptionistView(stage, user);
        }
    };
}
