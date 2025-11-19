package entity;

import enums.ApplicationStatus;
import java.time.LocalDateTime;

/**
 * Application entity class representing a student's application to an internship.
 * Represents a student's application to an internship opportunity with status tracking.
 * <p>
 * Application workflow:
 * <ul>
 * <li>Status starts as "Pending" when created</li>
 * <li>Company Representative can approve (status becomes "Successful") or reject</li>
 * <li>Students can accept placement if status is "Successful"</li>
 * <li>Withdrawal can be requested before or after placement confirmation</li>
 * <li>Withdrawal requests require Career Center Staff approval</li>
 * <li>All status updates are timestamped</li>
 * </ul>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class Application {
    /** Unique identifier for this application */
    private String applicationID;
    
    /** ID of the student who submitted this application */
    private String studentID;
    
    /** ID of the internship being applied to */
    private String internshipID;
    
    /** Current status of the application (Pending, Successful, Unsuccessful, Withdrawn) */
    private ApplicationStatus status;
    
    /** Date and time when the application was submitted */
    private LocalDateTime applicationDate;
    
    /** Date and time of the last status update */
    private LocalDateTime statusUpdateDate;
    
    /** Reason provided for withdrawal request */
    private String withdrawalReason;
    
    /** Flag indicating if withdrawal has been requested */
    private boolean withdrawalRequested;
    
    /** Flag indicating if withdrawal request has been approved by Career Center Staff */
    private boolean withdrawalApproved;
    
    /**
     * Constructs a new Application with pending status
     * Initializes application and status update dates to current time
     * Default status is PENDING, withdrawal flags are false
     * @param applicationID unique application identifier
     * @param studentID ID of the student submitting the application
     * @param internshipID ID of the internship being applied to
     */
    public Application(String applicationID, String studentID, String internshipID) {
        this.applicationID = applicationID;
        this.studentID = studentID;
        this.internshipID = internshipID;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.statusUpdateDate = LocalDateTime.now();
        this.withdrawalRequested = false;
        this.withdrawalApproved = false;
    }
    
    /**
     * Gets the unique application identifier
     * @return the application ID
     */
    public String getApplicationID() { return applicationID; }
    
    /**
     * Gets the ID of the student who submitted this application
     * @return the student ID
     */
    public String getStudentID() { return studentID; }
    
    /**
     * Gets the ID of the internship being applied to
     * @return the internship ID
     */
    public String getInternshipID() { return internshipID; }
    
    /**
     * Gets the current application status
     * @return the application status (Pending, Successful, Unsuccessful, or Withdrawn)
     */
    public ApplicationStatus getStatus() { return status; }
    
    /**
     * Updates the application status and records the status update timestamp
     * Called by Company Representative when approving/rejecting applications
     * @param status the new application status
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.statusUpdateDate = LocalDateTime.now();
    }
    
    /**
     * Gets the date and time when the application was submitted
     * @return the application submission date and time
     */
    public LocalDateTime getApplicationDate() { return applicationDate; }
    
    /**
     * Sets the application date
     * Used by DataManager when loading applications from file to restore original timestamps
     * @param applicationDate the application submission date and time
     */
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    /**
     * Gets the date and time of the last status update
     * Updated automatically when status changes
     * @return the status update date and time
     */
    public LocalDateTime getStatusUpdateDate() { return statusUpdateDate; }
    
    /**
     * Sets the status update date
     * Used by DataManager when loading applications from file to restore original timestamps
     * @param statusUpdateDate the status update date and time
     */
    public void setStatusUpdateDate(LocalDateTime statusUpdateDate) {
        this.statusUpdateDate = statusUpdateDate;
    }
    
    /**
     * Gets the reason provided for withdrawal request
     * @return the withdrawal reason, or null if no withdrawal requested
     */
    public String getWithdrawalReason() { return withdrawalReason; }
    
    /**
     * Submits a withdrawal request with the specified reason
     * Sets withdrawal requested flag and clears any previous approval
     * Subject to approval by Career Center Staff as per system requirements
     * @param reason the reason for requesting withdrawal
     */
    public void requestWithdrawal(String reason) {
        this.withdrawalReason = reason;
        this.withdrawalRequested = true;
        this.withdrawalApproved = false;
    }
    
    /**
     * Checks if a withdrawal request has been submitted
     * @return true if withdrawal has been requested, false otherwise
     */
    public boolean isWithdrawalRequested() { return withdrawalRequested; }
    
    /**
     * Checks if the withdrawal request has been approved by Career Center Staff
     * @return true if withdrawal has been approved, false otherwise
     */
    public boolean isWithdrawalApproved() { return withdrawalApproved; }
    
    /**
     * Approves the withdrawal request and updates status to WITHDRAWN
     * Called by Career Center Staff when approving withdrawal requests
     */
    public void approveWithdrawal() {
        this.withdrawalApproved = true;
        setStatus(ApplicationStatus.WITHDRAWN);
    }
    
    /**
     * Rejects the withdrawal request and clears the withdrawal reason
     * Called by Career Center Staff when rejecting withdrawal requests
     */
    public void rejectWithdrawal() {
        this.withdrawalRequested = false;
        this.withdrawalReason = null;
    }
    
    /**
     * Checks if the application is eligible for withdrawal
     * Can be withdrawn if status is Pending or Successful (before/after placement)
     * @return true if withdrawal is allowed, false otherwise
     */
    public boolean canBeWithdrawn() {
        return status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL;
    }
    
    /**
     * Returns a string representation of the application
     * @return formatted string containing application details and status
     */
    @Override
    public String toString() {
        return String.format("Application[ID=%s, Student=%s, Internship=%s, Status=%s, Date=%s]",
                           applicationID, studentID, internshipID, status, 
                           applicationDate.toLocalDate());
    }
}