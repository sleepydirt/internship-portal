package enums;

/**
 * Enumeration for application status in the internship management system.
 * Represents the different states an application can be in during its lifecycle.
 * <p>
 * Status workflow:
 * <ul>
 * <li>PENDING - Initial status when student submits application</li>
 * <li>SUCCESSFUL - Application approved by Company Representative</li>
 * <li>UNSUCCESSFUL - Application rejected by Company Representative</li>
 * <li>WITHDRAWN - Application withdrawn by student (requires Career Center Staff approval)</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum ApplicationStatus {
    /** Application is pending review by Company Representative */
    PENDING("Pending"),
    
    /** Application has been approved by Company Representative */
    SUCCESSFUL("Successful"),
    
    /** Application has been rejected by Company Representative */
    UNSUCCESSFUL("Unsuccessful"),
    
    /** Application has been withdrawn by student with Career Center Staff approval */
    WITHDRAWN("Withdrawn");
    
    /** Human-readable display name for this status */
    private final String displayName;
    
    /**
     * Constructs an ApplicationStatus with the specified display name.
     * @param displayName the human-readable name for this status
     */
    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the human-readable display name for this status.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Converts a string representation to an ApplicationStatus enum value.
     * Case-insensitive matching. Returns PENDING if string is null or invalid.
     * @param statusStr the string representation of the status
     * @return the corresponding ApplicationStatus enum value, or PENDING if not found
     */
    public static ApplicationStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        
        try {
            return ApplicationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
    
    /**
     * Returns the display name of this status.
     * @return the human-readable status name
     */
    @Override
    public String toString() {
        return displayName;
    }
}