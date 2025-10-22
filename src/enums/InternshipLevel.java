package src.enums;

/**
 * Enumeration for internship levels
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum InternshipLevel {
    BASIC("Basic"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");
    
    private final String displayName;
    
    /**
     * Constructor for InternshipLevel enum
     * @param displayName human-readable name
     */
    InternshipLevel(String displayName) {
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
     * Get InternshipLevel enum from string
     * @param levelStr string representation
     * @return InternshipLevel enum or BASIC if not found
     */
    public static InternshipLevel fromString(String levelStr) {
        if (levelStr == null) return BASIC;
        
        try {
            return InternshipLevel.valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BASIC;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}