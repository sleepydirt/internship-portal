package src.control;

import src.entity.*;
import src.enums.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Application Manager - Handles application-related operations
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class ApplicationManager {
    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private InternshipRepository internshipRepository;
    private IdGenerator idGenerator;

    /**
     * Constructor for ApplicationManager
     * 
     * @param applicationRepository reference to application repository
     */
    public ApplicationManager(ApplicationRepository applicationRepository, UserRepository userRepository,
            InternshipRepository internshipRepository, IdGenerator idGenerator) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * Submit an application for an internship
     * 
     * @param studentID    student ID
     * @param internshipID internship ID
     * @return application if successful, null otherwise
     */
    public Application submitApplication(String studentID, String internshipID) {
        User user = userRepository.getById(studentID);
        InternshipOpportunity internship = internshipRepository.getById(internshipID);

        if (!(user instanceof Student) || internship == null) {
            return null;
        }

        Student student = (Student) user;

        // Check if student can apply for more internships
        if (!student.canApplyForMore()) {
            return null;
        }

        // Check if internship is open for applications
        if (!internship.isOpenForApplications()) {
            return null;
        }

        // Check if student is eligible
        if (!internship.isStudentEligible(student)) {
            return null;
        }

        // Check if already applied
        if (student.getAppliedInternships().contains(internshipID)) {
            return null;
        }

        // Create application
        String applicationID = idGenerator.generateApplicationID();
        Application application = new Application(applicationID, studentID, internshipID);

        if (applicationRepository.add(application)) {
            student.applyForInternship(internshipID);
            internship.addApplicant(studentID);
            return application;
        }

        return null;
    }

    /**
     * Get applications by student
     * 
     * @param studentID student ID
     * @return list of applications
     */
    public List<Application> getApplicationsByStudent(String studentID) {
        return applicationRepository.getAll().values().stream()
                .filter(app -> app.getStudentID().equals(studentID))
                .sorted(Comparator.comparing(Application::getApplicationDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get applications for an internship
     * 
     * @param internshipID internship ID
     * @return list of applications
     */
    public List<Application> getApplicationsByInternship(String internshipID) {
        return applicationRepository.getAll().values().stream()
                .filter(app -> app.getInternshipID().equals(internshipID))
                .sorted(Comparator.comparing(Application::getApplicationDate))
                .collect(Collectors.toList());
    }

    /**
     * Get applications by company representative
     * 
     * @param representativeID representative ID
     * @return list of applications
     */
    public List<Application> getApplicationsByRepresentative(String representativeID) {
        List<String> internshipIDs = internshipRepository.getAll().values().stream()
                .filter(internship -> internship.getCompanyRepresentativeID().equals(representativeID))
                .map(InternshipOpportunity::getInternshipID)
                .collect(Collectors.toList());

        return applicationRepository.getAll().values().stream()
                .filter(app -> internshipIDs.contains(app.getInternshipID()))
                .sorted(Comparator.comparing(Application::getApplicationDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Approve an application
     * 
     * @param applicationID    application ID
     * @param representativeID representative approving
     * @return true if approved successfully
     */
    public boolean approveApplication(String applicationID, String representativeID) {
        Application application = applicationRepository.getById(applicationID);
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        InternshipOpportunity internship = internshipRepository.getById(application.getInternshipID());
        if (internship == null || !internship.getCompanyRepresentativeID().equals(representativeID)) {
            return false;
        }

        // Check if internship still has available slots
        if (internship.getAvailableSlots() <= 0) {
            return false;
        }

        application.setStatus(ApplicationStatus.SUCCESSFUL);
        return true;
    }

    /**
     * Reject an application
     * 
     * @param applicationID    application ID
     * @param representativeID representative rejecting
     * @return true if rejected successfully
     */
    public boolean rejectApplication(String applicationID, String representativeID) {
        Application application = applicationRepository.getById(applicationID);
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        InternshipOpportunity internship = internshipRepository.getById(application.getInternshipID());
        if (internship == null || !internship.getCompanyRepresentativeID().equals(representativeID)) {
            return false;
        }

        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        return true;
    }

    /**
     * Accept internship placement (by student)
     * 
     * @param applicationID application ID
     * @param studentID     student accepting
     * @return true if accepted successfully
     */
    public boolean acceptPlacement(String applicationID, String studentID) {
        Application application = applicationRepository.getById(applicationID);
        if (application == null ||
                !application.getStudentID().equals(studentID) ||
                application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }

        User user = userRepository.getById(studentID);
        InternshipOpportunity internship = internshipRepository.getById(application.getInternshipID());

        if (!(user instanceof Student) || internship == null) {
            return false;
        }

        Student student = (Student) user;

        // Accept the internship
        if (student.acceptInternship(application.getInternshipID())) {
            // Confirm placement in internship
            internship.confirmPlacement(studentID);

            // Withdraw all other applications
            withdrawAllOtherApplications(studentID, applicationID);

            return true;
        }

        return false;
    }

    /**
     * Request withdrawal from an application
     * 
     * @param applicationID application ID
     * @param studentID     student requesting withdrawal
     * @param reason        withdrawal reason
     * @return true if withdrawal requested successfully
     */
    public boolean requestWithdrawal(String applicationID, String studentID, String reason) {
        Application application = applicationRepository.getById(applicationID);
        if (application == null ||
                !application.getStudentID().equals(studentID) ||
                !application.canBeWithdrawn()) {
            return false;
        }

        application.requestWithdrawal(reason);
        return true;
    }

    /**
     * Approve withdrawal request (by career center staff)
     * 
     * @param applicationID application ID
     * @return true if withdrawal approved
     */
    public boolean approveWithdrawal(String applicationID) {
        Application application = applicationRepository.getById(applicationID);
        if (application == null || !application.isWithdrawalRequested()) {
            return false;
        }

        application.approveWithdrawal();

        // Update student and internship records
        User user = userRepository.getById(application.getStudentID());
        InternshipOpportunity internship = internshipRepository.getById(application.getInternshipID());

        if (user instanceof Student && internship != null) {
            Student student = (Student) user;
            student.withdrawFromInternship(application.getInternshipID());
            internship.removeApplicant(application.getStudentID());

            // If this was an accepted placement, free up the slot
            if (student.getAcceptedInternshipID() != null &&
                    student.getAcceptedInternshipID().equals(application.getInternshipID())) {
                // Note: This would require additional logic to decrement filled slots
                // For simplicity, we'll assume the internship manager handles this
            }
        }

        return true;
    }

    /**
     * Reject withdrawal request (by career center staff)
     * 
     * @param applicationID application ID
     * @return true if withdrawal rejected
     */
    public boolean rejectWithdrawal(String applicationID) {
        Application application = applicationRepository.getById(applicationID);
        if (application == null || !application.isWithdrawalRequested()) {
            return false;
        }

        application.rejectWithdrawal();
        return true;
    }

    /**
     * Get all withdrawal requests
     * 
     * @return list of applications with withdrawal requests
     */
    public List<Application> getWithdrawalRequests() {
        return applicationRepository.getAll().values().stream()
                .filter(Application::isWithdrawalRequested)
                .filter(app -> !app.isWithdrawalApproved())
                .sorted(Comparator.comparing(Application::getStatusUpdateDate))
                .collect(Collectors.toList());
    }

    /**
     * Withdraw all other applications when a student accepts a placement
     * 
     * @param studentID             student ID
     * @param acceptedApplicationID accepted application ID
     */
    private void withdrawAllOtherApplications(String studentID, String acceptedApplicationID) {
        List<Application> studentApplications = getApplicationsByStudent(studentID);
        for (Application app : studentApplications) {
            if (!app.getApplicationID().equals(acceptedApplicationID) &&
                    (app.getStatus() == ApplicationStatus.PENDING ||
                            app.getStatus() == ApplicationStatus.SUCCESSFUL)) {
                app.setStatus(ApplicationStatus.WITHDRAWN);

                // Remove from internship applicant list
                InternshipOpportunity internship = internshipRepository.getById(app.getInternshipID());
                if (internship != null) {
                    internship.removeApplicant(studentID);
                }
            }
        }
    }

    /**
     * Get application statistics
     * 
     * @return map of statistics
     */
    public Map<String, Integer> getApplicationStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        Collection<Application> applications = applicationRepository.getAll().values();

        stats.put("Total", applications.size());
        stats.put("Pending",
                (int) applications.stream().filter(a -> a.getStatus() == ApplicationStatus.PENDING).count());
        stats.put("Successful",
                (int) applications.stream().filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL).count());
        stats.put("Unsuccessful",
                (int) applications.stream().filter(a -> a.getStatus() == ApplicationStatus.UNSUCCESSFUL).count());
        stats.put("Withdrawn",
                (int) applications.stream().filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count());
        stats.put("Withdrawal Requests",
                (int) applications.stream().filter(Application::isWithdrawalRequested).count());

        return stats;
    }
}