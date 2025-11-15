package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import src.enums.InternshipLevel;
import src.enums.InternshipStatus;
import src.enums.Major;

/**
 * Internship Opportunity entity class representing an internship posting.
 * Represents an internship opportunity created by company representatives.
 * <p>
 * Key features and workflow:
 * <ul>
 * <li>Created by Company Representatives (up to 5 per representative)</li>
 * <li>Maximum of 10 slots per internship opportunity</li>
 * <li>Status workflow: Pending → Approved → Filled (or Rejected)</li>
 * <li>Must be approved by Career Center Staff before visible to students</li>
 * <li>Visibility can be toggled on/off by Company Representative</li>
 * <li>Students can only apply when status is "Approved" and visibility is "on"</li>
 * <li>Cannot accept applications after closing date or when status is "Filled"</li>
 * <li>Level determines eligibility: Year 1-2 students can only apply to Basic level</li>
 * <li>Preferred major filters which students can see and apply</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class InternshipOpportunity {
    /** Unique identifier for this internship opportunity */
    private String internshipID;
    
    /** Title of the internship position */
    private String title;
    
    /** Detailed description of the internship */
    private String description;
    
    /** Internship level (Basic, Intermediate, Advanced) - determines student eligibility */
    private InternshipLevel level;
    
    /** Preferred major for applicants (filters visibility and eligibility) */
    private Major preferredMajor;
    
    /** Date when applications open */
    private LocalDate openingDate;
    
    /** Date when applications close */
    private LocalDate closingDate;
    
    /** Current status (Pending, Approved, Rejected, Filled) */
    private InternshipStatus status;
    
    /** Name of the company offering this internship */
    private String companyName;
    
    /** ID of the company representative who created this opportunity */
    private String companyRepresentativeID;
    
    /** Total number of available slots (maximum 10) */
    private int totalSlots;
    
    /** Number of slots that have been filled by student confirmations */
    private int filledSlots;
    
    /** Visibility toggle - determines if students can see this opportunity */
    private boolean visible;
    
    /** List of student IDs who have applied to this internship */
    private List<String> applicantIDs;
    
    /**
     * Constructs a new Internship Opportunity with the specified details
     * Initializes with PENDING status, not visible, and no filled slots
     * Total slots cannot exceed 10 as per system requirements
     * @param internshipID unique internship identifier
     * @param title internship position title
     * @param description detailed description of the internship
     * @param level internship level (Basic, Intermediate, Advanced)
     * @param preferredMajor preferred major for applicants (filters eligibility)
     * @param openingDate application opening date
     * @param closingDate application closing date
     * @param companyName company offering the internship
     * @param companyRepresentativeID ID of representative who created this opportunity
     * @param totalSlots total number of available slots (max 10)
     */
    public InternshipOpportunity(String internshipID, String title, String description,
                                InternshipLevel level, Major preferredMajor,
                                LocalDate openingDate, LocalDate closingDate,
                                String companyName, String companyRepresentativeID,
                                int totalSlots , int filledSlots) {
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
        this.filledSlots = filledSlots;
        this.visible = false; // Initially not visible until approved
        this.applicantIDs = new ArrayList<>();
    }
    
    /**
     * Gets the unique internship identifier
     * @return the internship ID
     */
    public String getInternshipID() { return internshipID; }
    
    /**
     * Gets the internship title
     * @return the title of the internship position
     */
    public String getTitle() { return title; }
    
    /**
     * Sets the internship title
     * @param title the new internship title
     */
    public void setTitle(String title) { this.title = title; }
    
    /**
     * Gets the internship description
     * @return the detailed description of the internship
     */
    public String getDescription() { return description; }
    
    /**
     * Sets the internship description
     * @param description the new internship description
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Gets the internship level
     * @return the level (Basic, Intermediate, or Advanced)
     */
    public InternshipLevel getLevel() { return level; }
    
    /**
     * Sets the internship level
     * @param level the new internship level
     */
    public void setLevel(InternshipLevel level) { this.level = level; }
    
    /**
     * Gets the preferred major for applicants
     * @return the preferred major
     */
    public Major getPreferredMajor() { return preferredMajor; }
    
    /**
     * Sets the preferred major for applicants
     * @param preferredMajor the new preferred major
     */
    public void setPreferredMajor(Major preferredMajor) { this.preferredMajor = preferredMajor; }
    
    /**
     * Gets the application opening date
     * @return the date when applications open
     */
    public LocalDate getOpeningDate() { return openingDate; }
    
    /**
     * Sets the application opening date
     * @param openingDate the new opening date
     */
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }
    
    /**
     * Gets the application closing date
     * @return the date when applications close
     */
    public LocalDate getClosingDate() { return closingDate; }
    
    /**
     * Sets the application closing date
     * @param closingDate the new closing date
     */
    public void setClosingDate(LocalDate closingDate) { this.closingDate = closingDate; }
    
    /**
     * Gets the current internship status
     * @return the status (Pending, Approved, Rejected, or Filled)
     */
    public InternshipStatus getStatus() { return status; }
    
    /**
     * Sets the internship status
     * Status changes to Filled automatically when all slots are confirmed
     * @param status the new internship status
     */
    public void setStatus(InternshipStatus status) { this.status = status; }
    
    /**
     * Gets the company name
     * @return the name of the company offering this internship
     */
    public String getCompanyName() { return companyName; }
    
    /**
     * Sets the company name
     * @param companyName the new company name
     */
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    /**
     * Gets the ID of the company representative who created this opportunity
     * @return the company representative ID
     */
    public String getCompanyRepresentativeID() { return companyRepresentativeID; }
    
    /**
     * Gets the total number of available slots
     * @return the total slots (maximum 10)
     */
    public int getTotalSlots() { return totalSlots; }
    
    /**
     * Sets the total number of available slots
     * @param totalSlots the new total slots (maximum 10)
     */
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }
    
    /**
     * Gets the number of filled slots
     * @return the number of slots filled by student confirmations
     */
    public int getFilledSlots() { return filledSlots; }
    
    /**
     * Gets the number of available (unfilled) slots
     * @return the number of remaining slots
     */
    public int getAvailableSlots() { return totalSlots - filledSlots; }
    
    /**
     * Checks if the internship is visible to students
     * @return true if visible, false otherwise
     */
    public boolean isVisible() { return visible; }
    
    /**
     * Sets the visibility of the internship to students
     * Company Representatives can toggle this on/off
     * @param visible the new visibility status
     */
    public void setVisible(boolean visible) { this.visible = visible; }
    
    /**
     * Gets a copy of the list of applicant IDs
     * Returns a new list to prevent external modification
     * @return a new ArrayList containing student IDs who applied
     */
    public List<String> getApplicantIDs() { return new ArrayList<>(applicantIDs); }
    
    /**
     * Checks if the internship is currently open for student applications
     * Requirements: status must be Approved, visibility on, within date range, and slots available
     * @return true if open for applications, false otherwise
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
     * Checks if a student is eligible to apply for this internship
     * Eligibility based on: preferred major match and year/level compatibility
     * Year 1-2 students can ONLY apply to Basic level as per system requirements
     * @param student the student to check eligibility for
     * @return true if the student is eligible, false otherwise
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
     * Adds a student to the list of applicants for this internship
     * Only adds if not already applied and internship is open for applications
     * @param studentID the ID of the student to add
     * @return true if added successfully, false if already applied or not open
     */
    public boolean addApplicant(String studentID) {
        if (!applicantIDs.contains(studentID) && isOpenForApplications()) {
            applicantIDs.add(studentID);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a student from the list of applicants
     * Used when student withdraws application
     * @param studentID the ID of the student to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeApplicant(String studentID) {
        return applicantIDs.remove(studentID);
    }
    
    /**
     * Confirms a placement for a student and increments filled slots
     * Automatically updates status to FILLED when all slots are filled
     * @param studentID the ID of the student accepting the placement
     * @return true if placement confirmed, false if student not an applicant or no slots available
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
     * Checks if the internship is past its closing date
     * Students cannot apply after the closing date
     * @return true if past closing date, false otherwise
     */
    public boolean isPastClosingDate() {
        return LocalDate.now().isAfter(closingDate);
    }
    
    /**
     * Returns a string representation of the internship opportunity
     * @return formatted string containing internship details and slot information
     */
    @Override
    public String toString() {
        return String.format("Internship[ID=%s, Title=%s, Company=%s, Level=%s, Status=%s, Slots=%d/%d]",
                           internshipID, title, companyName, level, status, filledSlots, totalSlots);
    }
}