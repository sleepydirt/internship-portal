package enums;

/**
 * Enumeration for student majors and academic programs.
 * Represents the different academic majors available to students.
 * Used for filtering internship visibility and eligibility based on preferred major.
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum Major {
    /** Computer Science */
    CSC("Computer Science"),
    
    /** Electrical & Electronic Engineering */
    EEE("Electrical & Electronic Engineering"),
    
    /** Mechanical & Aerospace Engineering */
    MAE("Mechanical & Aerospace Engineering"),
    
    /** Civil & Environmental Engineering */
    CEE("Civil & Environmental Engineering"),
    
    /** Materials Science & Engineering */
    MSE("Materials Science & Engineering"),
    
    /** Chemical & Biomolecular Engineering */
    CBE("Chemical & Biomolecular Engineering"),
    
    /** Other majors - acts as wildcard for internships open to all majors */
    OTHER("Other");
    
    /** Human-readable display name for this major */
    private final String displayName;
    
    /**
     * Constructs a Major with the specified display name.
     * @param displayName the human-readable name for this major
     */
    Major(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the human-readable display name for this major.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Converts a string representation to a Major enum value.
     * Case-insensitive matching. Returns OTHER if string is null or invalid.
     * @param majorStr the string representation of the major
     * @return the corresponding Major enum value, or OTHER if not found
     */
    public static Major fromString(String majorStr) {
        if (majorStr == null) return OTHER;
        
        try {
            return Major.valueOf(majorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
    
    /**
     * Returns the display name of this major.
     * @return the human-readable major name
     */
    @Override
    public String toString() {
        return displayName;
    }
}