package database;

import java.sql.*;

/**
 * Connect class
 * ----------------
 * This class is responsible for:
 * - Establishing a connection to the MySQL database
 * - Executing SQL queries (SELECT)
 * - Executing SQL updates (INSERT, UPDATE, DELETE)
 * 
 * This class uses the Singleton pattern so that
 * only ONE database connection exists in the application.
 */

public final class Connect {
	
	private final String USERNAME = "root";				// Database username
	private final String PASSWORD = "";					// Database password (empty for local XAMPP)
	private final String DATABASE = "govlash_database"; // Database name in SQL
	private final String HOST = "localhost:3306";		// Database host and port
	private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE); // Full JDBC connection string
	
	// ===== JDBC OBJECTS =====
    private Connection con;     // Connection to the database
    private Statement st;       // Used to execute SQL statements

    // Singleton instance
    private static Connect connect;
	
    private Connect() {
        try {
            // 1. Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Establish connection to the database
            con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);

            // 3. Create a Statement object to run SQL queries
            st = con.createStatement();

        } catch (Exception e) {
            // If connection fails, stop the program
            e.printStackTrace();
            System.out.println("Failed to connect to the database.");
            System.exit(0);
        }
    }
    
    // Returns the single instance of the Connect class.
    // Creates a new instance ONLY if one does not already exist.
    public static Connect getConnection() {
		return connect = (connect == null) ? new Connect() : connect;
    }
    
    // This is used for executing SELECT queries
    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
    	try {
            rs = st.executeQuery(query);
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return rs;
    }
    
    // This is used for executing INSERT, DELETE queries
    public void executeUpdate(String query) {
    	try {
			st.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    // This is used for making a query with placeholders
    public PreparedStatement prepareStatement(String query) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ps;
    }
}
