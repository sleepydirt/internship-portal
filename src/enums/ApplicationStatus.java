package src.enums;

/**
 * Enumeration for application status
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum ApplicationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    WITHDRAWN("Withdrawn");
    
    private final String displayName;
    
    /**
     * Constructor for ApplicationStatus enum
     * @param displayName human-readable name
     */
    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get display name
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get ApplicationStatus enum from string
     * @param statusStr string representation
     * @return ApplicationStatus enum or PENDING if not found
     */
    public static ApplicationStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        
        try {
            return ApplicationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}