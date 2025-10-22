package src.entity;

import src.enums.ApplicationStatus;
import java.time.LocalDateTime;

/**
 * Application entity class
 * Represents a student's application to an internship opportunity
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class Application {
    private String applicationID;
    private String studentID;
    private String internshipID;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    private LocalDateTime statusUpdateDate;
    private String withdrawalReason;
    private boolean withdrawalRequested;
    private boolean withdrawalApproved;
    
    /**
     * Constructor for Application
     * @param applicationID unique application identifier
     * @param studentID ID of the applying student
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
    
    // Getters and Setters
    public String getApplicationID() { return applicationID; }
    
    public String getStudentID() { return studentID; }
    
    public String getInternshipID() { return internshipID; }
    
    public ApplicationStatus getStatus() { return status; }
    
    /**
     * Update application status
     * @param status new status
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.statusUpdateDate = LocalDateTime.now();
    }
    
    public LocalDateTime getApplicationDate() { return applicationDate; }
    
    public LocalDateTime getStatusUpdateDate() { return statusUpdateDate; }
    
    public String getWithdrawalReason() { return withdrawalReason; }
    
    /**
     * Request withdrawal with reason
     * @param reason withdrawal reason
     */
    public void requestWithdrawal(String reason) {
        this.withdrawalReason = reason;
        this.withdrawalRequested = true;
        this.withdrawalApproved = false;
    }
    
    public boolean isWithdrawalRequested() { return withdrawalRequested; }
    
    public boolean isWithdrawalApproved() { return withdrawalApproved; }
    
    /**
     * Approve withdrawal request
     */
    public void approveWithdrawal() {
        this.withdrawalApproved = true;
        setStatus(ApplicationStatus.WITHDRAWN);
    }
    
    /**
     * Reject withdrawal request
     */
    public void rejectWithdrawal() {
        this.withdrawalRequested = false;
        this.withdrawalReason = null;
    }
    
    /**
     * Check if application can be withdrawn
     * @return true if can be withdrawn
     */
    public boolean canBeWithdrawn() {
        return status == ApplicationStatus.PENDING || status == ApplicationStatus.SUCCESSFUL;
    }
    
    @Override
    public String toString() {
        return String.format("Application[ID=%s, Student=%s, Internship=%s, Status=%s, Date=%s]",
                           applicationID, studentID, internshipID, status, 
                           applicationDate.toLocalDate());
    }
}