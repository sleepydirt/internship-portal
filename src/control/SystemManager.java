package src.control;

import src.entity.*;
import src.enums.*;
import java.util.*;

/**
 * System Manager - Central controller for the Internship Management System
 * Implements Singleton pattern to ensure single instance
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class SystemManager {
    private static SystemManager instance;
    
    // Data storage
    private Map<String, User> users;
    private Map<String, InternshipOpportunity> internships;
    private Map<String, Application> applications;
    private List<CompanyRepresentative> pendingApprovals;
    
    // Managers for different functionalities
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private DataManager dataManager;
    
    /**
     * Private constructor for Singleton pattern
     */
    private SystemManager() {
        users = new HashMap<>();
        internships = new HashMap<>();
        applications = new HashMap<>();
        pendingApprovals = new ArrayList<>();
        
        // Initialize managers
        userManager = new UserManager(this);
        internshipManager = new InternshipManager(this);
        applicationManager = new ApplicationManager(this);
        dataManager = new DataManager(this);
    }
    
    /**
     * Get singleton instance
     * @return SystemManager instance
     */
    public static SystemManager getInstance() {
        if (instance == null) {
            instance = new SystemManager();
        }
        return instance;
    }
    
    /**
     * Initialize the system by loading data from files
     */
    public void initializeSystem() {
        System.out.println("Loading system data...");
        dataManager.loadAllData();
        System.out.println("System initialization complete.");
    }
    
    /**
     * Save all system data to files
     */
    public void saveSystem() {
        System.out.println("Saving system data...");
        dataManager.saveAllData();
        System.out.println("System data saved.");
    }
    
    // Getter methods for managers
    public UserManager getUserManager() { return userManager; }
    public InternshipManager getInternshipManager() { return internshipManager; }
    public ApplicationManager getApplicationManager() { return applicationManager; }
    public DataManager getDataManager() { return dataManager; }
    
    // Getter methods for data collections
    public Map<String, User> getUsers() { return users; }
    public Map<String, InternshipOpportunity> getInternships() { return internships; }
    public Map<String, Application> getApplications() { return applications; }
    public List<CompanyRepresentative> getPendingApprovals() { return pendingApprovals; }
    
    /**
     * Add user to the system
     * @param user user to add
     * @return true if added successfully
     */
    public boolean addUser(User user) {
        if (!users.containsKey(user.getUserID())) {
            users.put(user.getUserID(), user);
            
            // Add company representatives to pending approvals
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved()) {
                    pendingApprovals.add(rep);
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Get user by ID
     * @param userID user ID
     * @return user or null if not found
     */
    public User getUser(String userID) {
        return users.get(userID);
    }
    
    /**
     * Add internship opportunity
     * @param internship internship to add
     * @return true if added successfully
     */
    public boolean addInternship(InternshipOpportunity internship) {
        if (!internships.containsKey(internship.getInternshipID())) {
            internships.put(internship.getInternshipID(), internship);
            return true;
        }
        return false;
    }
    
    /**
     * Get internship by ID
     * @param internshipID internship ID
     * @return internship or null if not found
     */
    public InternshipOpportunity getInternship(String internshipID) {
        return internships.get(internshipID);
    }
    
    /**
     * Add application
     * @param application application to add
     * @return true if added successfully
     */
    public boolean addApplication(Application application) {
        if (!applications.containsKey(application.getApplicationID())) {
            applications.put(application.getApplicationID(), application);
            return true;
        }
        return false;
    }
    
    /**
     * Get application by ID
     * @param applicationID application ID
     * @return application or null if not found
     */
    public Application getApplication(String applicationID) {
        return applications.get(applicationID);
    }
    
    /**
     * Generate unique internship ID
     * @return unique internship ID
     */
    public String generateInternshipID() {
        return "INT" + String.format("%06d", internships.size() + 1);
    }
    
    /**
     * Generate unique application ID
     * @return unique application ID
     */
    public String generateApplicationID() {
        return "APP" + String.format("%06d", applications.size() + 1);
    }
    
    /**
     * Shutdown the system gracefully
     */
    public void shutdown() {
        saveSystem();
        System.out.println("System shutdown complete.");
    }
}