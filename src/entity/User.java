package src.entity;

/**
 * Abstract base class for all users in the system
 * Implements common user functionality
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public abstract class User {
    protected String userID;
    protected String name;
    protected String password;
    
    /**
     * Constructor for User
     * @param userID unique identifier for the user
     * @param name full name of the user
     * @param password user's password
     */
    public User(String userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
    }
    
    /**
     * Get user ID
     * @return user ID
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Get user name
     * @return user name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set user name
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get user password
     * @return password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Change user password
     * @param newPassword new password
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    /**
     * Validate password
     * @param inputPassword password to validate
     * @return true if password matches
     */
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    /**
     * Get user type (to be implemented by subclasses)
     * @return user type as string
     */
    public abstract String getUserType();
    
    /**
     * Get user role for display purposes
     * @return role description
     */
    public abstract String getRole();
    
    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, Type=%s]", 
                           userID, name, getUserType());
    }
}