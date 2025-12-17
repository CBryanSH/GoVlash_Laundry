package view;

import controller.AdminController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.Main;
import model.ServiceModel;
import model.TransactionModel;
import model.UserModel;

import java.util.Optional;

/**
 * AdminView
 * ----------
 * This class represents the Admin Dashboard UI.
 * 
 * Responsibilities:
 * - Display admin navigation (sidebar)
 * - Show different admin management pages:
 *   - Transactions
 *   - Services
 *   - Employees
 * - Handle UI interactions (button clicks, table selection)
 * 
 * Notes (MVC):
 * - This class ONLY handles UI and user interaction.
 * - Business logic is delegated to AdminController.
 * - Database operations are handled by Model classes.
 */

public class AdminView {

    private Stage stage;
    private UserModel admin;
    private AdminController controller;
    private BorderPane root;
    
    // Set the Controller to AdminController and the current user to this
    public AdminView(Stage stage, UserModel adminUser) {
        this.stage = stage;
        this.admin = adminUser;
        this.controller = new AdminController();

        root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(createTransactionSection()); // Default Page

        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        stage.setScene(scene);
        stage.setTitle("GoVlash - Admin Dashboard");
        stage.show();
    }
    
    // ===== SIDEBAR =====
    // Contains nav buttons, handles page switching inside the same scene
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label lblTitle = new Label("Welcome Admin ,\n" + admin.getUserName());
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnTrans = createNavButton("Manage Transactions");
        Button btnServ = createNavButton("Manage Services");
        Button btnEmp = createNavButton("Manage Employees");
        Button btnLogout = createNavButton("Log Out");

        btnTrans.setOnAction(e -> root.setCenter(createTransactionSection()));
        btnServ.setOnAction(e -> root.setCenter(createServiceSection()));
        btnEmp.setOnAction(e -> root.setCenter(createEmployeeSection()));
        
        // Logout Confirmation
        btnLogout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to log out?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                new LoginView(stage);
            }
        });
        sidebar.getChildren().addAll(lblTitle, new Separator(), btnTrans, btnServ, btnEmp, new Separator(), btnLogout);
        return sidebar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT;"));
        return btn;
    }
    
    // ===================
    // MANAGE TRANSACTIONS
    // ===================
    private VBox createTransactionSection() {
        Label title = new Label("All Transactions");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Filter Controls
        ComboBox<String> cmbFilter = new ComboBox<>();
        cmbFilter.getItems().addAll("All", "Finished");
        cmbFilter.getSelectionModel().selectFirst();

        Button btnNotify = new Button("Send Notification");
        
        // Display transaction ID, Cust, Status
        TableView<TransactionModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);	// Resize Collumns

        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, Integer> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));

        table.getColumns().add(colID);
        table.getColumns().add(colCust);
        table.getColumns().add(colStatus);

        // Actions
        cmbFilter.setOnAction(e -> {
            String filter = cmbFilter.getValue();
            table.getItems().setAll(controller.getTransactions(filter));
        });

        // Admin sends notification for Finished transactions
        btnNotify.setOnAction(e -> {
            TransactionModel selected = table.getSelectionModel().getSelectedItem();
            
            // Check if a row is selected
            if (selected == null) {
                showAlert("Error", "Please select a transaction from the table first.");
                return;
            }

            // Check if the status is actually 'Finished'
            if (!"Finished".equalsIgnoreCase(selected.getTransactionStatus())) {
                showAlert("Warning", "You can only send pickup notifications for 'Finished' transactions.");
                return;
            }
            
            // Send the notification via Controller
            controller.sendCompletionNotification(selected.getTransactionID(), selected.getCustomerID());
            
            // 4. Show success feedback
            showAlert("Success", "Notification sent to Customer ID: " + selected.getCustomerID());
        });

        // Initial Load
        cmbFilter.fireEvent(new javafx.event.ActionEvent());

        HBox tools = new HBox(10, new Label("Filter:"), cmbFilter, new Separator(), btnNotify);
        tools.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(15, title, tools, table);
        content.setPadding(new Insets(30));
        return content;
    }

    // ==================
    // 2. MANAGE SERVICES
    // ==================
    private VBox createServiceSection() {
        Label title = new Label("Manage Services");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Show All Service Name, Price and Durations
        TableView<ServiceModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        TableColumn<ServiceModel, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        
        TableColumn<ServiceModel, Integer> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        
        TableColumn<ServiceModel, Integer> colDur = new TableColumn<>("Duration (Days)");
        colDur.setCellValueFactory(new PropertyValueFactory<>("serviceDuration"));

        table.getColumns().add(colName);
        table.getColumns().add(colPrice);
        table.getColumns().add(colDur);

        Button btnAdd = new Button("Add New Service");
        Button btnDelete = new Button("Delete Selected");
        Button btnRefresh = new Button("Refresh");

        // Logic
        btnRefresh.setOnAction(e -> table.getItems().setAll(controller.getAllServices()));
        
        btnAdd.setOnAction(e -> showAddServicePopup(btnRefresh));
        
        btnDelete.setOnAction(e -> {
        	// Get selected service from table
            ServiceModel selected = table.getSelectionModel().getSelectedItem();

            // If nothing selected => show error popup
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Delete Service");
                alert.setHeaderText(null);
                alert.setContentText("Please select a service to delete.");
                alert.showAndWait();
                return;
            }

            // Confirmation popup before deleting
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Service");
            confirm.setContentText(
                "Are you sure you want to delete service:\n\n" +
                selected.getServiceName() + "?"
            );

            // Wait for user decision
            Optional<ButtonType> result = confirm.showAndWait();

            // If user clicks OK => delete
            if (result.isPresent() && result.get() == ButtonType.OK) {
                controller.deleteService(selected.getServiceID());

                // Refresh table after delete
                btnRefresh.fire();
            }
        });

        btnRefresh.fire(); // Load data

        HBox buttons = new HBox(10, btnAdd, btnDelete, btnRefresh);
        VBox content = new VBox(15, title, buttons, table);
        content.setPadding(new Insets(30));
        return content;
    }

    // ===================
    // 3. MANAGE EMPLOYEES
    // ===================
    private VBox createEmployeeSection() {
        Label title = new Label("Manage Employees");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
     // Show All Employee Name, Role
        TableView<UserModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<UserModel, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        TableColumn<UserModel, String> colRole = new TableColumn<>("Role");
        colRole.setCellValueFactory(new PropertyValueFactory<>("userRole"));

        table.getColumns().add(colName);
        table.getColumns().add(colRole);

        Button btnAdd = new Button("Add Employee");
        Button btnRefresh = new Button("Refresh");

        btnRefresh.setOnAction(e -> table.getItems().setAll(controller.getAllEmployees()));
        
        btnAdd.setOnAction(e -> showAddEmployeePopup(btnRefresh));

        btnRefresh.fire();

        HBox buttons = new HBox(10, btnAdd, btnRefresh);
        VBox content = new VBox(15, title, buttons, table);
        content.setPadding(new Insets(30));
        return content;
    }

    // ===== POPUPS for ADDING NEW SERVICE =====
    private void showAddServicePopup(Button refreshBtn) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Service");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        TextField txtName = new TextField();
        TextField txtDesc = new TextField();
        TextField txtPrice = new TextField();
        TextField txtDur = new TextField();
        
        grid.addRow(0, new Label("Name:"), txtName);
        grid.addRow(1, new Label("Description:"), txtDesc);
        grid.addRow(2, new Label("Price:"), txtPrice);
        grid.addRow(3, new Label("Duration (Days):"), txtDur);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return controller.addService(txtName.getText(), txtDesc.getText(), txtPrice.getText(), txtDur.getText());
            }
            return null;
        });
        
        // Show insert results
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(res -> {
            if(!"Success".equals(res)) showAlert("Error", res);
            else refreshBtn.fire();
        });
    }

    // ===== POPUP: ADD EMPLOYEE =====
    private void showAddEmployeePopup(Button refreshBtn) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Employee");
        dialog.setHeaderText(null);

        // Add standard buttons
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        // ===== UI COMPONENTS (Identical to RegisterView) =====
        TextField txtUsername = new TextField();
        TextField txtEmail = new TextField();
        PasswordField txtPassword = new PasswordField();
        PasswordField txtConfirm = new PasswordField();
        DatePicker datePicker = new DatePicker();
        
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton rbMale = new RadioButton("Male");
        RadioButton rbFemale = new RadioButton("Female");
        rbMale.setToggleGroup(genderGroup);
        rbFemale.setToggleGroup(genderGroup);

        ComboBox<String> cmbRole = new ComboBox<>();
        cmbRole.getItems().addAll("Laundry Staff", "Receptionist", "Admin");
        cmbRole.setPromptText("Select Role");

        // === Label Formatting ===
        Label lblMessage = new Label();
        lblMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        lblMessage.setWrapText(true);       // 1. Allow text to go to next line
        lblMessage.setMaxWidth(300);        // 2. Limit width to force wrapping inside the window

        // ===== LAYOUT (Same Grid Structure) =====
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));

        form.add(new Label("Username:"), 0, 0);
        form.add(txtUsername, 1, 0);

        form.add(new Label("Email:"), 0, 1);
        form.add(txtEmail, 1, 1);

        form.add(new Label("Password:"), 0, 2);
        form.add(txtPassword, 1, 2);

        form.add(new Label("Confirm Pass:"), 0, 3);
        form.add(txtConfirm, 1, 3);

        form.add(new Label("Gender:"), 0, 4);
        form.add(new VBox(5, rbMale, rbFemale), 1, 4);

        form.add(new Label("Date of Birth:"), 0, 5);
        form.add(datePicker, 1, 5);
        
        form.add(new Label("Role:"), 0, 6);
        form.add(cmbRole, 1, 6);
        
        form.add(lblMessage, 0, 7, 2, 1);

        dialog.getDialogPane().setContent(form);

        // ===== LOGIC: Handle Register Button Click =====
        final Button btnRegister = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        
        btnRegister.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            // Extract Gender
            String gender = null;
            if (genderGroup.getSelectedToggle() != null) {
                gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
            }

            // Extract Date
            String dobString = null;
            if (datePicker.getValue() != null) {
                dobString = datePicker.getValue().toString();
            }

            // Create User Model (Same as RegisterView)
            UserModel newEmployee = new UserModel(
                txtUsername.getText(),
                txtEmail.getText(),
                txtPassword.getText(),
                gender,
                dobString,
                cmbRole.getValue()
            );

            // Call Controller (Same structure)
            String result = controller.addEmployee(newEmployee, txtConfirm.getText());

            if ("Success".equals(result)) {
                // Allow dialog to close
            } else {
                // Failure: Show error and keep dialog open
                lblMessage.setText(result);
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
                event.consume(); // STOP the dialog from closing
            }
        });

        // Convert Result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                return "Success";
            }
            return null;
        });

        // Show Dialog
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(res -> {
            if ("Success".equals(res)) {
                showAlert("Success", "New employee registered successfully.");
                refreshBtn.fire(); 
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}