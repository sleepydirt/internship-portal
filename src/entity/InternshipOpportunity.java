package src.entity;

import src.enums.InternshipLevel;
import src.enums.InternshipStatus;
import src.enums.Major;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Internship Opportunity entity class
 * Represents an internship opportunity created by company representatives
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class InternshipOpportunity {
    private String internshipID;
    private String title;
    private String description;
    private InternshipLevel level;
    private Major preferredMajor;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private InternshipStatus status;
    private String companyName;
    private String companyRepresentativeID;
    private int totalSlots;
    private int filledSlots;
    private boolean visible;
    private List<String> applicantIDs; // List of student IDs who applied
    
    /**
     * Constructor for InternshipOpportunity
     * @param internshipID unique internship identifier
     * @param title internship title
     * @param description internship description
     * @param level internship level
     * @param preferredMajor preferred major for applicants
     * @param openingDate application opening date
     * @param closingDate application closing date
     * @param companyName company offering the internship
     * @param companyRepresentativeID representative who created this opportunity
     * @param totalSlots total number of available slots
     */
    public InternshipOpportunity(String internshipID, String title, String description,
                                InternshipLevel level, Major preferredMajor,
                                LocalDate openingDate, LocalDate closingDate,
                                String companyName, String companyRepresentativeID,
                                int totalSlots) {
        this.internshipID = internshipID;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.status = InternshipStatus.PENDING;
        this.companyName = companyName;
        this.companyRepresentativeID = companyRepresentativeID;
        this.totalSlots = totalSlots;
        this.filledSlots = 0;
        this.visible = false; // Initially not visible until approved
        this.applicantIDs = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getInternshipID() { return internshipID; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public InternshipLevel getLevel() { return level; }
    public void setLevel(InternshipLevel level) { this.level = level; }
    
    public Major getPreferredMajor() { return preferredMajor; }
    public void setPreferredMajor(Major preferredMajor) { this.preferredMajor = preferredMajor; }
    
    public LocalDate getOpeningDate() { return openingDate; }
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }
    
    public LocalDate getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDate closingDate) { this.closingDate = closingDate; }
    
    public InternshipStatus getStatus() { return status; }
    public void setStatus(InternshipStatus status) { this.status = status; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCompanyRepresentativeID() { return companyRepresentativeID; }
    
    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }
    
    public int getFilledSlots() { return filledSlots; }
    
    public int getAvailableSlots() { return totalSlots - filledSlots; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public List<String> getApplicantIDs() { return new ArrayList<>(applicantIDs); }
    
    /**
     * Check if internship is currently open for applications
     * @return true if open for applications
     */
    public boolean isOpenForApplications() {
        LocalDate today = LocalDate.now();
        return status == InternshipStatus.APPROVED && 
               visible && 
               !today.isBefore(openingDate) && 
               !today.isAfter(closingDate) &&
               filledSlots < totalSlots;
    }
    
    /**
     * Check if a student is eligible to apply
     * @param student the student to check
     * @return true if eligible
     */
    public boolean isStudentEligible(Student student) {
        // Check major preference
        if (preferredMajor != student.getMajor() && preferredMajor != Major.OTHER) {
            return false;
        }
        
        // Check level eligibility based on year of study
        if (student.getYearOfStudy() <= 2 && level != InternshipLevel.BASIC) {
            return false; // Year 1-2 can only apply for basic level
        }
        
        return true;
    }
    
    /**
     * Add an applicant to this internship
     * @param studentID student ID to add
     * @return true if added successfully
     */
    public boolean addApplicant(String studentID) {
        if (!applicantIDs.contains(studentID) && isOpenForApplications()) {
            applicantIDs.add(studentID);
            return true;
        }
        return false;
    }
    
    /**
     * Remove an applicant from this internship
     * @param studentID student ID to remove
     * @return true if removed successfully
     */
    public boolean removeApplicant(String studentID) {
        return applicantIDs.remove(studentID);
    }
    
    /**
     * Confirm a placement for a student
     * @param studentID student ID
     * @return true if placement confirmed
     */
    public boolean confirmPlacement(String studentID) {
        if (applicantIDs.contains(studentID) && filledSlots < totalSlots) {
            filledSlots++;
            if (filledSlots >= totalSlots) {
                status = InternshipStatus.FILLED;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Check if internship is past closing date
     * @return true if past closing date
     */
    public boolean isPastClosingDate() {
        return LocalDate.now().isAfter(closingDate);
    }
    
    @Override
    public String toString() {
        return String.format("Internship[ID=%s, Title=%s, Company=%s, Level=%s, Status=%s, Slots=%d/%d]",
                           internshipID, title, companyName, level, status, filledSlots, totalSlots);
    }
}