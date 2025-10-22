package src.enums;

/**
 * Enumeration for internship opportunity status
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum InternshipStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    FILLED("Filled");
    
    private final String displayName;
    
    /**
     * Constructor for InternshipStatus enum
     * @param displayName human-readable name
     */
    InternshipStatus(String displayName) {
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
     * Get InternshipStatus enum from string
     * @param statusStr string representation
     * @return InternshipStatus enum or PENDING if not found
     */
    public static InternshipStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        
        try {
            return InternshipStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}