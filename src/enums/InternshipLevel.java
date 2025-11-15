package enums;

/**
 * Enumeration for internship difficulty levels.
 * Represents the different complexity levels of internship opportunities.
 * <p>
 * Eligibility restrictions:
 * <ul>
 * <li>Year 1-2 students: Can ONLY apply to BASIC level internships</li>
 * <li>Year 3 and above students: Can apply to any level (BASIC, INTERMEDIATE, or ADVANCED)</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum InternshipLevel {
    /** Basic level internship - accessible to all students (Year 1-4) */
    BASIC("Basic"),
    
    /** Intermediate level internship - restricted to Year 3 and above students only */
    INTERMEDIATE("Intermediate"),
    
    /** Advanced level internship - restricted to Year 3 and above students only */
    ADVANCED("Advanced");
    
    /** Human-readable display name for this level */
    private final String displayName;
    
    /**
     * Constructs an InternshipLevel with the specified display name.
     * @param displayName the human-readable name for this level
     */
    InternshipLevel(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the human-readable display name for this level.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Converts a string representation to an InternshipLevel enum value.
     * Case-insensitive matching. Returns BASIC if string is null or invalid.
     * @param levelStr the string representation of the level
     * @return the corresponding InternshipLevel enum value, or BASIC if not found
     */
    public static InternshipLevel fromString(String levelStr) {
        if (levelStr == null) return BASIC;
        
        try {
            return InternshipLevel.valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BASIC;
        }
    }
    
    /**
     * Returns the display name of this level.
     * @return the human-readable level name
     */
    @Override
    public String toString() {
        return displayName;
    }
}