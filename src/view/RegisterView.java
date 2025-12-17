package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Main;
import model.UserModel;

import controller.LoginController;
import controller.RegisterController;

/**
 * RegisterView
 * ----------
 * Displays the user registration interface.
 *
 * Responsibilities:
 * - Collect registration data
 * - Display validation messages
 * - Submit data to controller for processing
 *
 * MVC Role:
 * - Presentation layer only
 * - Business rules handled in Controller
 */

public class RegisterView {
	
	// === Initialize at the class to be able to use clearForm elsewhere ===
    private Label lblMessage = new Label();
    private TextField txtUsername;
    private TextField txtEmail;
    private PasswordField txtPassword;
    private PasswordField txtConfirm;
    private ToggleGroup genderGroup;
    private DatePicker datePicker;

    public RegisterView(Stage stage) {

        Label title = new Label("Register");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        txtUsername = new TextField();
        txtEmail = new TextField();
        txtPassword = new PasswordField();
        txtConfirm = new PasswordField();
        genderGroup = new ToggleGroup();
        RadioButton rbMale = new RadioButton("Male");
        RadioButton rbFemale = new RadioButton("Female");
        rbMale.setToggleGroup(genderGroup);
        rbFemale.setToggleGroup(genderGroup);

        txtUsername.setPromptText("Username");
        txtEmail.setPromptText("Email");
        txtPassword.setPromptText("Password");
        txtConfirm.setPromptText("Confirm Password");

        datePicker = new DatePicker();

        Button btnRegister = new Button("Register");
        Button btnBack = new Button("Back to Login");

        btnRegister.setPrefWidth(200);
        btnBack.setPrefWidth(200);

        lblMessage.setStyle("-fx-text-fill: red;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        form.add(new Label("Username:"), 0, 0);
        form.add(txtUsername, 1, 0);

        form.add(new Label("Email:"), 0, 1);
        form.add(txtEmail, 1, 1);

        form.add(new Label("Password:"), 0, 2);
        form.add(txtPassword, 1, 2);
        
        form.add(new Label("Confirm Password:"), 0, 3);
        form.add(txtConfirm, 1, 3);

        form.add(new Label("Gender:"), 0, 4);
        form.add(new VBox(5, rbMale, rbFemale), 1, 4);

        form.add(new Label("Date of Birth:"), 0, 5);
        form.add(datePicker, 1, 5);

        VBox root = new VBox(25, title, form, btnRegister, btnBack, lblMessage);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, Main.WIDTH, Main.HEIGHT));
        stage.setTitle("Register");
        stage.show();

        // ===== Register Button =====
        btnRegister.setOnAction(e -> {
        	
        	// Check is genderGroup filled
            String gender = null;
            if (genderGroup.getSelectedToggle() != null) {
                gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
            }
            
            // Check is datePicker chosen
            String dobString = null;
            if (datePicker.getValue() != null) {
                dobString = datePicker.getValue().toString();
            }
            
            // Create new model
            UserModel user = new UserModel(
                txtUsername.getText(),
                txtEmail.getText(),
                txtPassword.getText(),
                gender,
                dobString,
                "CUSTOMER"
            );
            
            // Set controller to RegisterController
            RegisterController controller = new RegisterController();
            String result = controller.register(user, txtConfirm.getText());
            
            // Show if register is successfull
            if (result.equals("Success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText(null);
                alert.setContentText("Account created! Click OK to enter your dashboard.");
                
                // Wait for user to click OK
                alert.showAndWait();

                // Auto-Login to get the real UserID from Database
                LoginController loginHelper = new LoginController();
                UserModel loggedInUser = loginHelper.validateLogin(txtUsername.getText(), txtPassword.getText());

                // Redirect to Customer Dashboard
                if (loggedInUser != null) {
                    new CustomerView(stage, loggedInUser);
                } else {
                    // Fallback if something weird happens
                    new LoginView(stage);
                }

            } else {
                // Show validation error
                showMessage(result, false);
            }
        });

        btnBack.setOnAction(e -> new LoginView(stage));
    }
    
    // Validation messasge guide
    public void showMessage(String message, boolean success) {
        lblMessage.setStyle(success
                ? "-fx-text-fill: green;"
                : "-fx-text-fill: red;");
        lblMessage.setText(message);
    }
}
