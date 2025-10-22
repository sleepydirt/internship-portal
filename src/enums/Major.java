package src.enums;

/**
 * Enumeration for student majors
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public enum Major {
    CSC("Computer Science"),
    EEE("Electrical & Electronic Engineering"),
    MAE("Mechanical & Aerospace Engineering"),
    CEE("Civil & Environmental Engineering"),
    MSE("Materials Science & Engineering"),
    CBE("Chemical & Biomolecular Engineering"),
    SCSE("School of Computer Science & Engineering"),
    EEE_SCSE("Electrical & Electronic Engineering / Computer Science"),
    OTHER("Other");
    
    private final String displayName;
    
    /**
     * Constructor for Major enum
     * @param displayName human-readable name
     */
    Major(String displayName) {
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
     * Get Major enum from string
     * @param majorStr string representation
     * @return Major enum or OTHER if not found
     */
    public static Major fromString(String majorStr) {
        if (majorStr == null) return OTHER;
        
        try {
            return Major.valueOf(majorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}