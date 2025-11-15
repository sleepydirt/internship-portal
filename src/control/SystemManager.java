package src.control;

import java.util.HashMap;
import java.util.Map;

/**
 * System Manager - Application Context for the Internship Management System
 * Manages dependency injection and application lifecycle
 * Implements Singleton pattern to ensure single instance
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class SystemManager {
    private static SystemManager instance;

    // Repositories (data layer)
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;

    // Utilities
    private final IdGenerator idGenerator;

    // Managers (business logic layer)
    private final UserManager userManager;
    private final InternshipManager internshipManager;
    private final ApplicationManager applicationManager;
    private final DataManager dataManager;
    
    // Session-based filter settings (per user)
    private final Map<String, InternshipFilterSettings> userFilterSettings;
    
    // Track if user has viewed internships (for first-time filter prompt)
    private final Map<String, Boolean> hasViewedInternships;

    /**
     * Private constructor - initializes all dependencies
     */
    private SystemManager() {
        // Initialize repositories (data storage)
        this.userRepository = new UserRepository();
        this.internshipRepository = new InternshipRepository();
        this.applicationRepository = new ApplicationRepository();

        // Initialize utilities
        this.idGenerator = new IdGenerator(internshipRepository, applicationRepository);

        // Initialize managers with their dependencies (dependency injection)
        this.userManager = new UserManager(userRepository);
        this.internshipManager = new InternshipManager(internshipRepository, userRepository, idGenerator);
        this.applicationManager = new ApplicationManager(applicationRepository, userRepository, internshipRepository,
                idGenerator);
        this.dataManager = new DataManager(userRepository, internshipRepository, applicationRepository, userManager);
        
        // Initialize session-based filter settings
        this.userFilterSettings = new HashMap<>();
        this.hasViewedInternships = new HashMap<>();
    }

    /**
     * Get singleton instance
     * 
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

    /**
     * Shutdown the system gracefully
     */
    public void shutdown() {
        saveSystem();
        System.out.println("System shutdown complete.");
    }

    // Getter methods for managers (public API)
    public UserManager getUserManager() {
        return userManager;
    }

    public InternshipManager getInternshipManager() {
        return internshipManager;
    }

    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    // Repository access for boundary layer (read-only data access)
    /**
     * Get user repository for data access
     * 
     * @return user repository
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Get internship repository for data access
     * 
     * @return internship repository
     */
    public InternshipRepository getInternshipRepository() {
        return internshipRepository;
    }

    /**
     * Get application repository for data access
     * 
     * @return application repository
     */
    public ApplicationRepository getApplicationRepository() {
        return applicationRepository;
    }
    
    // Filter settings management (session-scoped)
    /**
     * Get filter settings for a user 
     * Creates new filter settings if doesn't exist
     * 
     * @param userID user ID
     * @return filter settings for the user
     */
    public InternshipFilterSettings getFilterSettings(String userID) {
        return userFilterSettings.computeIfAbsent(userID, k -> new InternshipFilterSettings());
    }
    
    /**
     * Clear filter settings for a user
     * 
     * @param userID user ID
     */
    public void clearFilterSettings(String userID) {
        userFilterSettings.remove(userID);
        hasViewedInternships.remove(userID);
    }
    
    /**
     * Check if user has viewed internships before in this session
     * 
     * @param userID user ID
     * @return true if user has viewed internships before
     */
    public boolean hasViewedInternships(String userID) {
        return hasViewedInternships.getOrDefault(userID, false);
    }
    
    /**
     * Mark that user has viewed internships
     * 
     * @param userID user ID
     */
    public void markInternshipsViewed(String userID) {
        hasViewedInternships.put(userID, true);
    }
}