package model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import database.Connect;

/**
 * ServiceModel
 * ------------
 * Represents a laundry service offered by the system.
 *
 * Responsibilities:
 * - Fetch service list from database
 * - Insert new services
 * - Delete services
 */

public class ServiceModel {
	
	// Table Attributes
    private int serviceID;
    private String serviceName;
    private String serviceDescription;
    private int servicePrice;
    private int serviceDuration;

    // Instance of Connect to reuse in methods
    private final Connect db = Connect.getConnection();

    // 1. Empty constructor (for fetching lists or deleting by ID)
    public ServiceModel() {
    }

    // 2. Constructor for CREATING a new service (No ID yet)
    public ServiceModel(String serviceName, String serviceDescription, int servicePrice, int serviceDuration) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
    }

    // 3. Constructor for FETCHING existing services
    public ServiceModel(int serviceID, String serviceName, String serviceDescription, int servicePrice, int serviceDuration) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
    }

    // ===== DATABASE METHOD: Get All Services =====
    public List<ServiceModel> getAllServices() {
        List<ServiceModel> list = new ArrayList<>();
        String query = "SELECT * FROM Services";

        try {
            ResultSet rs = db.executeQuery(query);

            while (rs.next()) {
                list.add(new ServiceModel(
                    rs.getInt("ServiceID"),
                    rs.getString("ServiceName"),
                    rs.getString("ServiceDescription"),
                    rs.getInt("ServicePrice"),
                    rs.getInt("ServiceDuration")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== DATABASE METHOD: Save (Insert) =====
    public void save() {
        String query = String.format(
            "INSERT INTO Services (ServiceName, ServiceDescription, ServicePrice, ServiceDuration) " +
            "VALUES ('%s', '%s', %d, %d)", 
            serviceName, serviceDescription, servicePrice, serviceDuration
        );
        db.executeUpdate(query);
    }

    // ===== DATABASE METHOD: Delete Selected ServiceID =====
    public void delete(int id) {
        String query = "DELETE FROM Services WHERE ServiceID = " + id;
        db.executeUpdate(query);
    }

    // ===== GETTERS =====
    public int getServiceID() { return serviceID; }
    public String getServiceName() { return serviceName; }
    public String getServiceDescription() { return serviceDescription; }
    public int getServicePrice() { return servicePrice; }
    public int getServiceDuration() { return serviceDuration; }
}