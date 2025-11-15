package entity;

import enums.Major;
import java.util.ArrayList;
import java.util.List;

/**
 * Student entity class representing a student user in the internship management system.
 * Students can apply for internships based on their profile and eligibility criteria.
 * <p>
 * Key capabilities and restrictions:
 * <ul>
 * <li>Registration is automatic by reading from student list file</li>
 * <li>Can apply for maximum of 3 internship opportunities at once</li>
 * <li>Year 1-2 students can ONLY apply for Basic-level internships</li>
 * <li>Year 3-4 students can apply for any level (Basic, Intermediate, Advanced)</li>
 * <li>Only 1 internship placement can be accepted</li>
 * <li>Accepting an internship automatically withdraws all other applications</li>
 * <li>Withdrawal requests require Career Center Staff approval</li>
 * <li>Can only view internships where visibility is toggled "on" and match their profile</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class Student extends User {
    /** Current year of study (1-4) */
    private int yearOfStudy;
    
    /** Student's academic major */
    private Major major;
    
    /** List of internship IDs the student has applied to (maximum 3) */
    private List<String> appliedInternships;
    
    /** ID of the internship that the student has accepted, null if none */
    private String acceptedInternshipID;
    
    /**
     * Constructs a new Student with the specified profile information
     * Initializes empty application list and no accepted internship
     * Student accounts are created automatically from student list file during system initialization
     * @param userID student ID (format: U followed by 7 digits and a letter, e.g., U2345123F)
     * @param name student's full name
     * @param password student's password for authentication (default: "password")
     * @param yearOfStudy current year of study (1-4, determines internship level eligibility)
     * @param major student's academic major (determines which internships are visible)
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
     * Gets the student's current year of study
     * @return the year of study (1-4)
     */
    public int getYearOfStudy() {
        return yearOfStudy;
    }
    
    /**
     * Sets the student's current year of study
     * @param yearOfStudy the new year of study (1-4)
     */
    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
    
    /**
     * Gets the student's academic major
     * @return the student's major
     */
    public Major getMajor() {
        return major;
    }
    
    /**
     * Sets the student's academic major
     * @param major the new major for the student
     */
    public void setMajor(Major major) {
        this.major = major;
    }
    
    /**
     * Gets a copy of the list of internship IDs the student has applied to
     * Returns a new list to prevent external modification
     * @return a new ArrayList containing the internship IDs
     */
    public List<String> getAppliedInternships() {
        return new ArrayList<>(appliedInternships);
    }
    
    /**
     * Applies for an internship by adding it to the student's application list
     * Maximum of 3 applications allowed. Prevents duplicate applications.
     * Note: Actual eligibility checking (year vs level, major matching) should be done
     * by the system before calling this method. Year 1-2 students can only apply for
     * Basic-level internships, while Year 3-4 can apply for any level.
     * @param internshipID the ID of the internship to apply for
     * @return true if application was successful, false if already applied or limit reached
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
     * Accepts an internship placement and automatically withdraws all other applications
     * Only 1 internship placement can be accepted per student (as per system requirements)
     * Can only accept if: (1) application status is "Successful", (2) student has applied,
     * and (3) hasn't already accepted another internship
     * @param internshipID the ID of the internship to accept
     * @return true if acceptance was successful, false otherwise
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
     * Gets the ID of the internship that the student has accepted
     * @return the accepted internship ID, or null if no internship has been accepted
     */
    public String getAcceptedInternshipID() {
        return acceptedInternshipID;
    }
    
    /**
     * Checks if the student has accepted an internship placement
     * @return true if the student has accepted an internship, false otherwise
     */
    public boolean hasAcceptedInternship() {
        return acceptedInternshipID != null;
    }
    
    /**
     * Requests withdrawal from an internship application
     * If withdrawing from an accepted internship, clears the accepted status
     * Note: All withdrawal requests (before or after placement confirmation) are subject
     * to approval from Career Center Staff as per system requirements
     * @param internshipID the ID of the internship to withdraw from
     * @return true if the internship was found and removed, false otherwise
     */
    public boolean withdrawFromInternship(String internshipID) {
        if (acceptedInternshipID != null && acceptedInternshipID.equals(internshipID)) {
            acceptedInternshipID = null;
        }
        return appliedInternships.remove(internshipID);
    }
    
    /**
     * Checks if the student can apply for more internships
     * Students can apply if they have less than 3 applications and haven't accepted an internship
     * Once an internship is accepted, all other applications are withdrawn and no new applications allowed
     * @return true if the student can submit more applications, false otherwise
     */
    public boolean canApplyForMore() {
        return appliedInternships.size() < 3 && acceptedInternshipID == null;
    }
    
    /**
     * {@inheritDoc}
     * @return "STUDENT" user type identifier
     */
    @Override
    public String getUserType() {
        return "STUDENT";
    }
    
    /**
     * {@inheritDoc}
     * @return "Student" role description
     */
    @Override
    public String getRole() {
        return "Student";
    }
    
    /**
     * Returns a string representation of the student
     * @return formatted string containing student details and application count
     */
    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Year=%d, Major=%s, Applied=%d]", 
                           userID, name, yearOfStudy, major, appliedInternships.size());
    }
}