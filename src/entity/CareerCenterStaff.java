package src.entity;

/**
 * Career Center Staff entity class
 * Represents staff members who manage the internship system
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CareerCenterStaff extends User {
    private String staffDepartment;
    
    /**
     * Constructor for Career Center Staff
     * @param userID NTU account ID
     * @param name staff name
     * @param password password
     * @param staffDepartment staff department
     */
    public CareerCenterStaff(String userID, String name, String password, 
                            String staffDepartment) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
    }
    
    /**
     * Get staff department
     * @return staff department
     */
    public String getStaffDepartment() {
        return staffDepartment;
    }
    
    /**
     * Set staff department
     * @param staffDepartment new staff department
     */
    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }
    
    @Override
    public String getUserType() {
        return "CAREER_CENTER_STAFF";
    }
    
    @Override
    public String getRole() {
        return "Career Center Staff";
    }
    
    @Override
    public String toString() {
        return String.format("Staff[ID=%s, Name=%s, Department=%s]", 
                           userID, name, staffDepartment);
    }
}