package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Company Representative entity class representing users from companies.
 * Represents a company representative who can create and manage internship opportunities.
 * <p>
 * Key capabilities and restrictions:
 * <ul>
 * <li>Must register with company details (initially empty list at system start)</li>
 * <li>User ID is the company email address</li>
 * <li>Can only login after approval by Career Center Staff</li>
 * <li>Can create up to 5 internship opportunities for their company</li>
 * <li>Can approve or reject student applications to their internships</li>
 * <li>Can toggle visibility of internship opportunities on/off</li>
 * <li>Can view application details and student details for their internships</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CompanyRepresentative extends User {
    /** Name of the company the representative works for */
    private String companyName;
    
    /** Department within the company */
    private String department;
    
    /** Position/title of the representative in the company */
    private String position;
    
    /** Approval status - must be approved by Career Center Staff before login */
    private boolean isApproved;
    
    /** List of internship IDs created by this representative (maximum 5) */
    private List<String> createdInternships;
    
    /**
     * Constructs a new Company Representative with company and position details
     * Representative requires approval from Career Center Staff before being able to login
     * Initially starts with no created internships and unapproved status
     * @param userID company email address (serves as unique identifier)
     * @param name representative's full name
     * @param password password for authentication (default: "password")
     * @param companyName name of the company the representative works for
     * @param department department within the company
     * @param position position/title of the representative
     */
    public CompanyRepresentative(String userID, String name, String password,
                                String companyName, String department, String position) {
        super(userID, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.isApproved = false; // Requires approval from Career Center Staff
        this.createdInternships = new ArrayList<>();
    }
    
    /**
     * Gets the company name
     * @return the name of the company
     */
    public String getCompanyName() {
        return companyName;
    }
    
    /**
     * Sets the company name
     * @param companyName the new company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    /**
     * Gets the department within the company
     * @return the department name
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Sets the department within the company
     * @param department the new department name
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Gets the representative's position/title
     * @return the position or title
     */
    public String getPosition() {
        return position;
    }
    
    /**
     * Sets the representative's position/title
     * @param position the new position or title
     */
    public void setPosition(String position) {
        this.position = position;
    }
    
    /**
     * Checks if the representative has been approved by Career Center Staff
     * Representatives must be approved before they can login and create internships
     * @return true if approved, false otherwise
     */
    public boolean isApproved() {
        return isApproved;
    }
    
    /**
     * Sets the approval status of the representative
     * Called by Career Center Staff to authorize or reject account creation
     * @param approved the new approval status
     */
    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }
    
    /**
     * Gets a copy of the list of internship IDs created by this representative
     * Returns a new list to prevent external modification
     * @return a new ArrayList containing the created internship IDs
     */
    public List<String> getCreatedInternships() {
        return new ArrayList<>(createdInternships);
    }
    
    /**
     * Adds a newly created internship to the representative's list
     * Maximum of 5 internships can be created per representative as per system requirements
     * @param internshipID the ID of the internship to add
     * @return true if added successfully, false if limit reached or already exists
     */
    public boolean addCreatedInternship(String internshipID) {
        if (createdInternships.size() >= 5) {
            return false; // Maximum 5 internships per representative
        }
        if (!createdInternships.contains(internshipID)) {
            createdInternships.add(internshipID);
            return true;
        }
        return false;
    }
    
    /**
     * Removes an internship from the representative's created list
     * @param internshipID the ID of the internship to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeCreatedInternship(String internshipID) {
        return createdInternships.remove(internshipID);
    }
    
    /**
     * Checks if the representative can create more internship opportunities
     * Representatives are limited to 5 internships as per system requirements
     * @return true if fewer than 5 internships have been created, false otherwise
     */
    public boolean canCreateMore() {
        return createdInternships.size() < 5;
    }
    
    /**
     * {@inheritDoc}
     * @return "COMPANY_REPRESENTATIVE" user type identifier
     */
    @Override
    public String getUserType() {
        return "COMPANY_REPRESENTATIVE";
    }
    
    /**
     * {@inheritDoc}
     * @return "Company Representative" role description
     */
    @Override
    public String getRole() {
        return "Company Representative";
    }
    
    /**
     * Returns a string representation of the company representative
     * @return formatted string containing representative details and internship count
     */
    @Override
    public String toString() {
        return String.format("CompanyRep[ID=%s, Name=%s, Company=%s, Approved=%s, Created=%d]", 
                           userID, name, companyName, isApproved, createdInternships.size());
    }
}