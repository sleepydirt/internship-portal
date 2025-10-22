package src.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Company Representative entity class
 * Represents a company representative who can manage internship opportunities
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean isApproved;
    private List<String> createdInternships; // List of internship IDs
    
    /**
     * Constructor for Company Representative
     * @param userID company email address
     * @param name representative name
     * @param password password
     * @param companyName company name
     * @param department department within company
     * @param position position/title
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
     * Get company name
     * @return company name
     */
    public String getCompanyName() {
        return companyName;
    }
    
    /**
     * Set company name
     * @param companyName new company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    /**
     * Get department
     * @return department
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Set department
     * @param department new department
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Get position
     * @return position
     */
    public String getPosition() {
        return position;
    }
    
    /**
     * Set position
     * @param position new position
     */
    public void setPosition(String position) {
        this.position = position;
    }
    
    /**
     * Check if representative is approved
     * @return true if approved
     */
    public boolean isApproved() {
        return isApproved;
    }
    
    /**
     * Set approval status
     * @param approved approval status
     */
    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }
    
    /**
     * Get list of created internship IDs
     * @return list of internship IDs
     */
    public List<String> getCreatedInternships() {
        return new ArrayList<>(createdInternships);
    }
    
    /**
     * Add a created internship
     * @param internshipID internship ID to add
     * @return true if added successfully
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
     * Remove a created internship
     * @param internshipID internship ID to remove
     * @return true if removed successfully
     */
    public boolean removeCreatedInternship(String internshipID) {
        return createdInternships.remove(internshipID);
    }
    
    /**
     * Check if representative can create more internships
     * @return true if can create more
     */
    public boolean canCreateMore() {
        return createdInternships.size() < 5;
    }
    
    @Override
    public String getUserType() {
        return "COMPANY_REPRESENTATIVE";
    }
    
    @Override
    public String getRole() {
        return "Company Representative";
    }
    
    @Override
    public String toString() {
        return String.format("CompanyRep[ID=%s, Name=%s, Company=%s, Approved=%s, Created=%d]", 
                           userID, name, companyName, isApproved, createdInternships.size());
    }
}