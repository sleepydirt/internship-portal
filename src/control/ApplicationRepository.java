package src.control;

import src.entity.Application;
import java.util.*;

/**
 * Application Repository - Manages application data storage and retrieval
 * Provides data access methods for application entities
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class ApplicationRepository {
    private Map<String, Application> applications;

    /**
     * Constructor for ApplicationRepository
     */
    public ApplicationRepository() {
        this.applications = new HashMap<>();
    }

    /**
     * Get application by ID
     * 
     * @param applicationID application ID
     * @return application or null if not found
     */
    public Application getById(String applicationID) {
        return applications.get(applicationID);
    }

    /**
     * Add application to repository
     * 
     * @param application application to add
     * @return true if added successfully, false if application already exists
     */
    public boolean add(Application application) {
        if (applications.containsKey(application.getApplicationID())) {
            return false;
        }
        applications.put(application.getApplicationID(), application);
        return true;
    }

    /**
     * Remove application from repository
     * 
     * @param applicationID application ID to remove
     * @return true if removed successfully, false if application not found
     */
    public boolean remove(String applicationID) {
        return applications.remove(applicationID) != null;
    }

    /**
     * Check if application exists
     * 
     * @param applicationID application ID
     * @return true if application exists
     */
    public boolean exists(String applicationID) {
        return applications.containsKey(applicationID);
    }

    /**
     * Get all applications
     * 
     * @return map of all applications
     */
    public Map<String, Application> getAll() {
        return applications;
    }

    /**
     * Get count of applications
     * 
     * @return total number of applications
     */
    public int size() {
        return applications.size();
    }

    /**
     * Check if repository is empty
     * 
     * @return true if no applications exist
     */
    public boolean isEmpty() {
        return applications.isEmpty();
    }
}
