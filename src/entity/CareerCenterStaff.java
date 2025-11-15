package entity;

/**
 * Career Center Staff entity class representing administrative users.
 * Represents staff members who manage and oversee the internship system.
 * <p>
 * Key capabilities and responsibilities:
 * <ul>
 * <li>Registration is automatic by reading from staff list file</li>
 * <li>User ID is their NTU account</li>
 * <li>Can authorize or reject Company Representative account creation</li>
 * <li>Can approve or reject internship opportunities submitted by companies</li>
 * <li>Can approve or reject student withdrawal requests (before and after placement)</li>
 * <li>Can generate comprehensive reports on internship opportunities with filters</li>
 * <li>Acts as the administrative authority for the entire system</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CareerCenterStaff extends User {
    /** The department the staff member belongs to */
    private String staffDepartment;
    
    /**
     * Constructs a new Career Center Staff member
     * Staff accounts are created automatically from staff list file during system initialization
     * @param userID NTU account ID (serves as unique identifier)
     * @param name staff member's full name
     * @param password password for authentication (default: "password")
     * @param staffDepartment the department the staff member belongs to
     */
    public CareerCenterStaff(String userID, String name, String password, 
                            String staffDepartment) {
        super(userID, name, password);
        this.staffDepartment = staffDepartment;
    }
    
    /**
     * Gets the staff member's department
     * @return the department name
     */
    public String getStaffDepartment() {
        return staffDepartment;
    }
    
    /**
     * Sets the staff member's department
     * @param staffDepartment the new department name
     */
    public void setStaffDepartment(String staffDepartment) {
        this.staffDepartment = staffDepartment;
    }
    
    /**
     * {@inheritDoc}
     * @return "CAREER_CENTER_STAFF" user type identifier
     */
    @Override
    public String getUserType() {
        return "CAREER_CENTER_STAFF";
    }
    
    /**
     * {@inheritDoc}
     * @return "Career Center Staff" role description
     */
    @Override
    public String getRole() {
        return "Career Center Staff";
    }
    
    /**
     * Returns a string representation of the staff member
     * @return formatted string containing staff details
     */
    @Override
    public String toString() {
        return String.format("Staff[ID=%s, Name=%s, Department=%s]", 
                           userID, name, staffDepartment);
    }
}