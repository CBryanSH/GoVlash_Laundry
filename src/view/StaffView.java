package view;

import controller.StaffController;
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
 * StaffView
 * ----------
 * This class represents the Staff Dashboard UI.
 * 
 * Responsibilities:
 * - Display pending jobs assigned to the logged-in staff
 * - Allow staff to mark jobs as finished
 * - Provide refresh and logout functionality
 *
 * Notes:
 * - View layer in MVC architecture
 * - Uses StaffController for data retrieval and updates
 * - Contains no database or business logic
 */

public class StaffView {

    private Stage stage;
    private UserModel staffUser;
    private StaffController controller;
    private BorderPane root;

    // Set the Controller to StaffController and the current user to this
    public StaffView(Stage stage, UserModel staffUser) {
        this.stage = stage;
        this.staffUser = staffUser;
        this.controller = new StaffController();

        root = new BorderPane();
        root.setLeft(createSidebar());
        root.setCenter(createWorkArea());

        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        stage.setScene(scene);
        stage.setTitle("GoVlash - Laundry Staff Workspace");
        stage.show();
    }

    // ===== SIDEBAR =====
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        Label lblTitle = new Label("Staff Panel");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label lblUser = new Label("Hi Staff, " + staffUser.getUserName());
        lblUser.setStyle("-fx-text-fill: lightgray; -fx-font-size: 14px;");

        Button btnJobs = createNavButton("Pending Jobs");
        Button btnLogout = createNavButton("Log Out");

        // Navigation Actions
        btnJobs.setOnAction(e -> root.setCenter(createWorkArea()));

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

        sidebar.getChildren().addAll(lblTitle, lblUser, new Separator(), btnJobs, new Separator(), btnLogout);
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

    // ===========================================
    // MAIN WORK AREA: PENDING TRANSACTIONS
    // ===========================================
    private VBox createWorkArea() {
        Label title = new Label("Pending Job Queue");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Display transaction ID, ServiceID, Weight, Notes
        TableView<TransactionModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("Job ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));

        TableColumn<TransactionModel, Integer> colService = new TableColumn<>("Service ID");
        colService.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight (kg)");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));

        TableColumn<TransactionModel, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("transactionNotes"));

        table.getColumns().add(colID);
        table.getColumns().add(colService);
        table.getColumns().add(colWeight);
        table.getColumns().add(colNotes);

        Button btnFinish = new Button("Mark as Finished");
        Button btnRefresh = new Button("Refresh List");
        
        btnRefresh.setOnAction(e -> {
        	// Pass the ID of the currently logged-in staff
            table.getItems().setAll(controller.getPendingTransactions(staffUser.getUserID()));
        });

        btnFinish.setOnAction(e -> {
            TransactionModel selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a job to finish.");
                return;
            }

            // Confirmation before finishing
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setContentText("Mark Job #" + selected.getTransactionID() + " as Finished?");
            Optional<ButtonType> res = confirm.showAndWait();
            
            if (res.isPresent() && res.get() == ButtonType.OK) {
                controller.finishTransaction(selected);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Job marked as Finished!");
                btnRefresh.fire();
            }
        });

        // Initial Load
        btnRefresh.fire();

        HBox actions = new HBox(10, btnRefresh, btnFinish);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(15, title, actions, table);
        content.setPadding(new Insets(30));
        return content;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}