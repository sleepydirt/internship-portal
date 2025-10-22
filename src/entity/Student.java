package src.entity;

import src.enums.Major;
import java.util.ArrayList;
import java.util.List;

/**
 * Student entity class representing a student user
 * Students can apply for internships based on their profile
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class Student extends User {
    private int yearOfStudy;
    private Major major;
    private List<String> appliedInternships; // List of internship IDs
    private String acceptedInternshipID;
    
    /**
     * Constructor for Student
     * @param userID student ID (format: U1234567A)
     * @param name student name
     * @param password student password
     * @param yearOfStudy year of study (1-4)
     * @param major student's major
     */
    public Student(String userID, String name, String password, 
                   int yearOfStudy, Major major) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.appliedInternships = new ArrayList<>();
        this.acceptedInternshipID = null;
    }
    
    /**
     * Get year of study
     * @return year of study
     */
    public int getYearOfStudy() {
        return yearOfStudy;
    }
    
    /**
     * Set year of study
     * @param yearOfStudy new year of study
     */
    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
    
    /**
     * Get student major
     * @return major
     */
    public Major getMajor() {
        return major;
    }
    
    /**
     * Set student major
     * @param major new major
     */
    public void setMajor(Major major) {
        this.major = major;
    }
    
    /**
     * Get list of applied internship IDs
     * @return list of internship IDs
     */
    public List<String> getAppliedInternships() {
        return new ArrayList<>(appliedInternships);
    }
    
    /**
     * Apply for an internship
     * @param internshipID internship ID to apply for
     * @return true if application successful
     */
    public boolean applyForInternship(String internshipID) {
        if (appliedInternships.size() >= 3) {
            return false; // Maximum 3 applications
        }
        if (!appliedInternships.contains(internshipID)) {
            appliedInternships.add(internshipID);
            return true;
        }
        return false; // Already applied
    }
    
    /**
     * Accept an internship placement
     * @param internshipID internship ID to accept
     * @return true if acceptance successful
     */
    public boolean acceptInternship(String internshipID) {
        if (appliedInternships.contains(internshipID) && acceptedInternshipID == null) {
            acceptedInternshipID = internshipID;
            // Remove all other applications
            appliedInternships.clear();
            appliedInternships.add(internshipID);
            return true;
        }
        return false;
    }
    
    /**
     * Get accepted internship ID
     * @return accepted internship ID or null if none
     */
    public String getAcceptedInternshipID() {
        return acceptedInternshipID;
    }
    
    /**
     * Check if student has accepted an internship
     * @return true if internship accepted
     */
    public boolean hasAcceptedInternship() {
        return acceptedInternshipID != null;
    }
    
    /**
     * Withdraw from an internship application
     * @param internshipID internship ID to withdraw from
     * @return true if withdrawal successful
     */
    public boolean withdrawFromInternship(String internshipID) {
        if (acceptedInternshipID != null && acceptedInternshipID.equals(internshipID)) {
            acceptedInternshipID = null;
        }
        return appliedInternships.remove(internshipID);
    }
    
    /**
     * Check if student can apply for more internships
     * @return true if can apply for more
     */
    public boolean canApplyForMore() {
        return appliedInternships.size() < 3 && acceptedInternshipID == null;
    }
    
    @Override
    public String getUserType() {
        return "STUDENT";
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Year=%d, Major=%s, Applied=%d]", 
                           userID, name, yearOfStudy, major, appliedInternships.size());
    }
}