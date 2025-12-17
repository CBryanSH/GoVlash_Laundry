package view;

import controller.CustomerController;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.Main;
import model.NotificationModel;
import model.ServiceModel;
import model.TransactionModel;
import model.UserModel;

/**
 * CustomerView
 * ----------
 * This class represents the Customer Dashboard UI.
 * 
 * Responsibilities:
 * - Allow customers to create new laundry transactions
 * - Display transaction history and notifications
 * - Handle customer navigation and UI interactions
 *
 * Notes:
 * - Serves as the View layer in MVC
 * - All validation and database operations are handled by CustomerController
 * - Uses Model objects only for data display
 */

public class CustomerView {

    private Stage stage;
    private UserModel customer;
    private CustomerController controller;
    private BorderPane root;
    
    // Set the Controller to CustomerController and the current user to this
    public CustomerView(Stage stage, UserModel customer) {
        this.stage = stage;
        this.customer = customer;
        this.controller = new CustomerController();

        root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(createTransactionHistorySection());

        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        stage.setScene(scene);
        stage.setTitle("GoVlash - Customer Dashboard");
        stage.show();
    }

    // ===== SIDEBAR (Updated Logout) =====
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label lblWelcome = new Label("Welcome,\n" + customer.getUserName());
        lblWelcome.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button btnNewOrder = createNavButton("New Transaction");
        Button btnHistory = createNavButton("My History");
        Button btnNotif = createNavButton("Notifications");
        Button btnLogout = createNavButton("Log Out");

        btnNewOrder.setOnAction(e -> root.setCenter(createTransactionSection()));
        btnHistory.setOnAction(e -> root.setCenter(createTransactionHistorySection()));
        btnNotif.setOnAction(e -> root.setCenter(createNotificationSection()));

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

        sidebar.getChildren().addAll(lblWelcome, new Separator(), btnNewOrder, btnHistory, btnNotif, new Separator(), btnLogout);
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

    // ==================
    // CREATE TRANSACTION
    // ==================
    private VBox createTransactionSection() {
        Label sectionTitle = new Label("Create New Transaction");
        sectionTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Service Selection
        Label lblService = new Label("Select Service:");
        ComboBox<ServiceModel> cmbServices = new ComboBox<>();
        cmbServices.setPromptText("Choose a service...");
        cmbServices.setMaxWidth(300);

        // Populate ComboBox from Controller
        cmbServices.getItems().setAll(controller.getAllServices());

        // Display Service Name properly in the dropdown
        cmbServices.setConverter(new StringConverter<ServiceModel>() {
            @Override
            public String toString(ServiceModel s) {
                return s == null ? "" : s.getServiceName();
            }
            @Override
            public ServiceModel fromString(String string) {
                return null;
            }
        });

        // Info Box to show ServiceDetail
        TextArea txtServiceInfo = new TextArea();
        txtServiceInfo.setEditable(false);
        txtServiceInfo.setPromptText("Service details will appear here...");
        txtServiceInfo.setMaxWidth(300);
        txtServiceInfo.setPrefHeight(80);
        txtServiceInfo.setWrapText(true);

        // Update Info Box when selection changes
        cmbServices.setOnAction(e -> {
            ServiceModel selected = cmbServices.getValue();
            if (selected != null) {
                txtServiceInfo.setText(
                    "Description: " + selected.getServiceDescription() + "\n" +
                    "Price: Rp " + selected.getServicePrice() + " / kg\n" +
                    "Duration: " + selected.getServiceDuration() + " days"
                );
            }
        });

        // Weight and Notes
        TextField txtWeight = new TextField();
        txtWeight.setPromptText("Total Weight (2–50 kg)");
        txtWeight.setMaxWidth(300);

        TextArea txtNotes = new TextArea();
        txtNotes.setPromptText("Transaction Notes (max 250 chars)");
        txtNotes.setMaxWidth(300);
        txtNotes.setPrefHeight(80);

        Button btnCreate = new Button("Create Order");
        Label lblResult = new Label();

        btnCreate.setOnAction(e -> {
            ServiceModel selectedService = cmbServices.getValue();
            
            if (selectedService == null) {
                lblResult.setText("Please select a service.");
                lblResult.setStyle("-fx-text-fill: red;");
                return;
            }

            // Send data to controller for validation
            String result = controller.createTransaction(
                selectedService.getServiceID(),
                customer.getUserID(), 
                txtWeight.getText(), 
                txtNotes.getText()
            );

            if ("Success".equals(result)) {
                lblResult.setText("Transaction created successfully!");
                lblResult.setStyle("-fx-text-fill: green;");
                // Reset form
                cmbServices.getSelectionModel().clearSelection();
                txtServiceInfo.clear();
                txtWeight.clear();
                txtNotes.clear();
            } else {
                lblResult.setText(result); // Show error from controller
                lblResult.setStyle("-fx-text-fill: red;");
            }
        });

        VBox content = new VBox(10, 
            sectionTitle, 
            lblService, cmbServices, txtServiceInfo,
            new Label("Weight (kg):"), txtWeight, 
            new Label("Notes:"), txtNotes, 
            btnCreate, lblResult
        );
        content.setPadding(new Insets(40));
        return content;
    }

    // =======
    // HISTORY
    // =======
    private VBox createTransactionHistorySection() {
        Label sectionTitle = new Label("My Transaction History");
        sectionTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Display transaction ID, Status, Date
        TableView<TransactionModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));

        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));

        TableColumn<TransactionModel, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        table.getColumns().add(colID);
        table.getColumns().add(colStatus);
        table.getColumns().add(colDate);

        Button btnRefresh = new Button("Refresh Data");
        btnRefresh.setOnAction(e -> {
            table.getItems().setAll(controller.getTransactionHistory(customer.getUserID()));
        });
        
        btnRefresh.fire(); 

        VBox content = new VBox(15, sectionTitle, btnRefresh, table);
        content.setPadding(new Insets(40));
        return content;
    }

    // =============
    // NOTIFICATIONS
    // =============
    private VBox createNotificationSection() {
        Label sectionTitle = new Label("My Notifications");
        sectionTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        ListView<NotificationModel> listView = new ListView<>();
        
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(NotificationModel n, boolean empty) {
                super.updateItem(n, empty);
                if (empty || n == null) {
                    setText(null);
                } else {
                    String status = n.isRead() ? "" : "[NEW] ";
                    setText(status + n.getMessage() + " (" + n.getCreatedAt() + ")");
                    if (!n.isRead()) setStyle("-fx-font-weight: bold;");
                    else setStyle("");
                }
            }
        });

        Button btnRead = new Button("Read Message");
        Button btnRefresh = new Button("Refresh");
        Button btnDelete = new Button("Delete");

        // Open Notification Detail and set isRead
        btnRead.setOnAction(e -> {
            NotificationModel selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showNotificationDetailPopup(selected);
                btnRefresh.fire();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please select a notification to read.");
                alert.show();
            }
        });
        
        // Delete the Notifications
        btnDelete.setOnAction(e -> {
            NotificationModel selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.delete();
                btnRefresh.fire();
            }
        });

        btnRefresh.setOnAction(e -> {
            listView.getItems().setAll(controller.getNotifications(customer.getUserID()));
        });
        
        btnRefresh.fire();

        HBox buttons = new HBox(10, btnRefresh, btnRead, btnDelete);
        VBox content = new VBox(15, sectionTitle, buttons, listView);
        content.setPadding(new Insets(40));
        return content;
    }

    // ===== POPUP WINDOW =====
    private void showNotificationDetailPopup(NotificationModel n) {
        // Controller marks it as read in database
        controller.markNotificationRead(n);

        // Show the Popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification Detail");
        alert.setHeaderText("Sent on: " + n.getCreatedAt());
        
        TextArea area = new TextArea(n.getMessage());
        area.setWrapText(true);
        area.setEditable(false);
        
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }
}