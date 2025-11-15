package enums;

/**
 * Enumeration for internship opportunity status.
 * Represents the different states an internship opportunity can be in.
 * <p>
 * Status workflow:
 * <ul>
 * <li>PENDING - Initial status when Company Representative creates internship</li>
 * <li>APPROVED - Approved by Career Center Staff, visible to eligible students</li>
 * <li>REJECTED - Rejected by Career Center Staff</li>
 * <li>FILLED - All available slots have been confirmed by students</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum InternshipStatus {
    /** Internship is pending approval from Career Center Staff */
    PENDING("Pending"),
    
    /** Internship has been approved and is visible to students */
    APPROVED("Approved"),
    
    /** Internship has been rejected by Career Center Staff */
    REJECTED("Rejected"),
    
    /** All internship slots have been filled by student confirmations */
    FILLED("Filled");
    
    /** Human-readable display name for this status */
    private final String displayName;
    
    /**
     * Constructs an InternshipStatus with the specified display name.
     * @param displayName the human-readable name for this status
     */
    InternshipStatus(String displayName) {
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
     * Converts a string representation to an InternshipStatus enum value.
     * Case-insensitive matching. Returns PENDING if string is null or invalid.
     * @param statusStr the string representation of the status
     * @return the corresponding InternshipStatus enum value, or PENDING if not found
     */
    public static InternshipStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        
        try {
            return InternshipStatus.valueOf(statusStr.toUpperCase());
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