package main;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

/**
 * Main
 * ----------
 * Entry point of the GoVlash application.
 *
 * Responsibilities:
 * - Initialize the JavaFX application
 * - Configure the main application window
 * - Load the initial LoginView
 *
 * Notes:
 * - Extends JavaFX Application class
 * - Controls global window size settings
 * - Does not contain business or UI logic
 */

public class Main extends Application {
	
	public static final int WIDTH = 900;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage stage) {
    	// Fixed Size for all pages
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setResizable(false);

        new LoginView(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}

