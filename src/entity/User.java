package entity;

/**
 * Abstract base class for all users in the Internship Placement Management System.
 * Implements common user functionality including authentication and profile management.
 * Serves as the parent class for Student, CompanyRepresentative, and CareerCenterStaff.
 * <p>
 * User ID formats:
 * <ul>
 * <li>Students: U followed by 7 digits and a letter (e.g., U2345123F)</li>
 * <li>Company Representatives: Company email address</li>
 * <li>Career Center Staff: NTU account</li>
 * </ul>
 * All users initially use the default password "password" which can be changed after login
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public abstract class User {
    /** Unique identifier for the user */
    protected String userID;
    
    /** Full name of the user */
    protected String name;
    
    /** User's password for authentication */
    protected String password;
    
    /**
     * Constructs a new User with the specified credentials
     * @param userID unique identifier for the user (format varies by user type)
     * @param name full name of the user
     * @param password user's password for authentication (default is "password")
     */
    public User(String userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
    }
    
    /**
     * Gets the unique user identifier
     * @return the user ID
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Gets the user's full name
     * @return the user's name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the user's full name
     * @param name the new name for the user
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the user's password
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Changes the user's password to a new password
     * @param newPassword the new password to set
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    /**
     * Validates if the provided password matches the user's password
     * @param inputPassword the password to validate
     * @return true if the input password matches, false otherwise
     */
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    /**
     * Gets the user type identifier
     * Must be implemented by subclasses to return their specific type
     * @return the user type as a string (e.g., "STUDENT", "COMPANY_REPRESENTATIVE", "CAREER_CENTER_STAFF")
     */
    public abstract String getUserType();
    
    /**
     * Gets the user's role description for display purposes
     * Must be implemented by subclasses to return their specific role
     * @return the role description as a string
     */
    public abstract String getRole();
    
    /**
     * Returns a string representation of the user
     * @return formatted string containing user ID, name, and type
     */
    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, Type=%s]", 
                           userID, name, getUserType());
    }
}