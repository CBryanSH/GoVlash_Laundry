package view;

import controller.ReceptionistController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.Main;
import model.TransactionModel;
import model.UserModel;

import java.util.Optional;

/**
 * ReceptionistView
 * ----------
 * This class represents the Receptionist Dashboard UI.
 * 
 * Responsibilities:
 * - Display receptionist dashboard and navigation sidebar
 * - Show unassigned transactions and available laundry staff
 * - Allow assignment of transactions to staff
 * - Handle UI events such as button clicks and table selections
 *
 * Notes:
 * - Acts as the View layer in MVC
 * - Delegates all business logic to ReceptionistController
 * - Does not access the database directly
 */

public class ReceptionistView {

    private Stage stage;
    private UserModel receptionistUser;
    private ReceptionistController controller;
    private BorderPane root;
    
    // Set the Controller to ReceptionistController and the current user to this
    public ReceptionistView(Stage stage, UserModel receptionistUser) {
        this.stage = stage;
        this.receptionistUser = receptionistUser;
        this.controller = new ReceptionistController();

        root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(createAssignmentSection());

        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        stage.setScene(scene);
        stage.setTitle("GoVlash - Receptionist Dashboard");
        stage.show();
    }

    // ===== SIDEBAR =====
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label lblTitle = new Label("Receptionist");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label lblUser = new Label("User: " + receptionistUser.getUserName());
        lblUser.setStyle("-fx-text-fill: lightgray; -fx-font-size: 14px;");

        Button btnAssign = createNavButton("Assign Tasks");
        Button btnLogout = createNavButton("Log Out");

        btnAssign.setOnAction(e -> root.setCenter(createAssignmentSection()));
        
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

        sidebar.getChildren().addAll(lblTitle, lblUser, new Separator(), btnAssign, new Separator(), btnLogout);
        return sidebar;
    }
    
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER_LEFT; -fx-padding: 10;"));
        return btn;
    }

    // ==================================================
    // Assigning Transaction (Transactions & Staff Table)
    // ==================================================
    private VBox createAssignmentSection() {
        Label title = new Label("Assign Orders to Staff");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // === LEFT TABLE: Unassigned Transactions ===
        // Display transaction ID, Date, Weight
        Label lblLeft = new Label("Pending Transactions (Unassigned)");
        lblLeft.setStyle("-fx-font-weight: bold;");
        
        TableView<TransactionModel> tableTrans = new TableView<>();
        tableTrans.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        
        tableTrans.getColumns().add(colID);
        tableTrans.getColumns().add(colDate);
        tableTrans.getColumns().add(colWeight);

        // === RIGHT TABLE: Laundry Staff List ===
        // Display Staff ID, Name
        Label lblRight = new Label("Available Laundry Staff");
        lblRight.setStyle("-fx-font-weight: bold;");
        
        TableView<UserModel> tableStaff = new TableView<>();
        tableStaff.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        TableColumn<UserModel, Integer> colStaffID = new TableColumn<>("Staff ID");
        colStaffID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        
        TableColumn<UserModel, String> colStaffName = new TableColumn<>("Name");
        colStaffName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        tableStaff.getColumns().add(colStaffID);
        tableStaff.getColumns().add(colStaffName);

        // === LAYOUT FOR TABLES ===
        // Using HBox to put them side by side
        VBox leftBox = new VBox(5, lblLeft, tableTrans);
        VBox rightBox = new VBox(5, lblRight, tableStaff);
        
        // Allow them to grow and fill space equally
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        
        HBox tablesContainer = new HBox(20, leftBox, rightBox);
        tablesContainer.setPrefHeight(400);

        // === ACTION BUTTONS ===
        Button btnAssign = new Button("<< Assign Selected Staff to Order >>");
        btnAssign.setStyle("-fx-font-weight: bold; -fx-base: #2ecc71;");
        Button btnRefresh = new Button("Refresh Lists");

        // Logic: Refresh
        btnRefresh.setOnAction(e -> {
            tableTrans.getItems().setAll(controller.getUnassignedTransactions());
            tableStaff.getItems().setAll(controller.getLaundryStaffList());
        });

        // Logic: Assign
        btnAssign.setOnAction(e -> {
            TransactionModel selectedTrans = tableTrans.getSelectionModel().getSelectedItem();
            UserModel selectedStaff = tableStaff.getSelectionModel().getSelectedItem();

            // Call Controller
            String result = controller.assignJob(selectedTrans, selectedStaff, receptionistUser.getUserID());

            if ("Success".equals(result)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Job assigned successfully!");
                alert.show();
                
                // Refresh tables
                btnRefresh.fire();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(result);
                alert.show();
            }
        });

        // Initial Load
        btnRefresh.fire();

        VBox content = new VBox(15, title, tablesContainer, btnAssign, btnRefresh);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);
        return content;
    }
}