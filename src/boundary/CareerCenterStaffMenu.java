package src.boundary;

import src.control.SystemManager;
import src.entity.*;
import src.enums.*;
import java.util.List;
import java.util.Map;

/**
 * Career Center Staff Menu - Interface for career center staff users
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CareerCenterStaffMenu extends BaseMenu {
    
    /**
     * Constructor for CareerCenterStaffMenu
     * @param systemManager reference to system manager
     * @param currentUser currently logged in staff member
     */
    public CareerCenterStaffMenu(SystemManager systemManager, User currentUser) {
        super(systemManager, currentUser);
    }
    
    @Override
    public void displayMenu() {
        CareerCenterStaff staff = (CareerCenterStaff) currentUser;
        
        while (true) {
            System.out.println("\n=== Career Center Staff Dashboard ===");
            System.out.println("Welcome, " + staff.getName());
            System.out.println("Department: " + staff.getStaffDepartment());
            
            System.out.println("\n--- Menu Options ---");
            System.out.println("1. Approve/Reject Company Representatives");
            System.out.println("2. Approve/Reject Internship Opportunities");
            System.out.println("3. Manage Withdrawal Requests");
            System.out.println("4. Generate Reports");
            System.out.println("5. View System Statistics");
            System.out.println("6. View All Internships");
            System.out.println("7. Change Password");
            System.out.println("8. View Profile");
            System.out.println("9. Logout");
            
            int choice = getIntInput("Enter your choice: ", 1, 9);
            
            switch (choice) {
                case 1:
                    manageCompanyRepresentatives();
                    break;
                case 2:
                    manageInternshipOpportunities();
                    break;
                case 3:
                    manageWithdrawalRequests();
                    break;
                case 4:
                    generateReports();
                    break;
                case 5:
                    viewSystemStatistics();
                    break;
                case 6:
                    viewAllInternships();
                    break;
                case 7:
                    handlePasswordChange();
                    break;
                case 8:
                    displayUserInfo();
                    pauseForUser();
                    break;
                case 9:
                    handleLogout();
                    return;
            }
        }
    }
    
    /**
     * Manage company representative approvals
     */
    private void manageCompanyRepresentatives() {
        System.out.println("\n--- Manage Company Representatives ---");
        List<CompanyRepresentative> pendingReps = systemManager.getUserManager().getPendingApprovals();
        
        if (pendingReps.isEmpty()) {
            System.out.println("No pending company representative approvals.");
            return;
        }
        
        System.out.println("Pending approvals:");
        for (int i = 0; i < pendingReps.size(); i++) {
            CompanyRepresentative rep = pendingReps.get(i);
            System.out.printf("[%d] %s (%s)\n", i + 1, rep.getName(), rep.getUserID());
            System.out.printf("    Company: %s | Department: %s | Position: %s\n",
                            rep.getCompanyName(), rep.getDepartment(), rep.getPosition());
        }
        
        int choice = getIntInput("Select representative to review (0 to cancel): ", 0, pendingReps.size());
        if (choice == 0) return;
        
        CompanyRepresentative selectedRep = pendingReps.get(choice - 1);
        
        System.out.println("\n--- Representative Details ---");
        System.out.println("Name: " + selectedRep.getName());
        System.out.println("Email: " + selectedRep.getUserID());
        System.out.println("Company: " + selectedRep.getCompanyName());
        System.out.println("Department: " + selectedRep.getDepartment());
        System.out.println("Position: " + selectedRep.getPosition());
        
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        System.out.println("3. Cancel");
        
        int action = getIntInput("Enter your choice: ", 1, 3);
        
        switch (action) {
            case 1:
                if (systemManager.getUserManager().approveCompanyRepresentative(selectedRep.getUserID())) {
                    System.out.println("Company representative approved successfully!");
                } else {
                    System.out.println("Failed to approve representative.");
                }
                break;
            case 2:
                if (confirmAction("Are you sure you want to reject this representative?")) {
                    if (systemManager.getUserManager().rejectCompanyRepresentative(selectedRep.getUserID())) {
                        System.out.println("Company representative rejected and removed from system.");
                    } else {
                        System.out.println("Failed to reject representative.");
                    }
                }
                break;
            case 3:
                return;
        }
    }
    
    /**
     * Manage internship opportunity approvals
     */
    private void manageInternshipOpportunities() {
        System.out.println("\n--- Manage Internship Opportunities ---");
        List<InternshipOpportunity> pendingInternships = systemManager.getInternshipManager()
                .getPendingInternships();
        
        if (pendingInternships.isEmpty()) {
            System.out.println("No pending internship opportunities.");
            return;
        }
        
        System.out.println("Pending internships:");
        for (int i = 0; i < pendingInternships.size(); i++) {
            InternshipOpportunity internship = pendingInternships.get(i);
            System.out.printf("[%d] %s - %s\n", i + 1, internship.getInternshipID(), internship.getTitle());
            System.out.printf("    Company: %s | Level: %s | Slots: %d\n",
                            internship.getCompanyName(), internship.getLevel(), internship.getTotalSlots());
        }
        
        int choice = getIntInput("Select internship to review (0 to cancel): ", 0, pendingInternships.size());
        if (choice == 0) return;
        
        InternshipOpportunity selectedInternship = pendingInternships.get(choice - 1);
        
        System.out.println("\n--- Internship Details ---");
        System.out.println("ID: " + selectedInternship.getInternshipID());
        System.out.println("Title: " + selectedInternship.getTitle());
        System.out.println("Company: " + selectedInternship.getCompanyName());
        System.out.println("Level: " + selectedInternship.getLevel());
        System.out.println("Preferred Major: " + selectedInternship.getPreferredMajor());
        System.out.println("Opening Date: " + selectedInternship.getOpeningDate());
        System.out.println("Closing Date: " + selectedInternship.getClosingDate());
        System.out.println("Total Slots: " + selectedInternship.getTotalSlots());
        System.out.println("Description: " + selectedInternship.getDescription());
        
        System.out.println("\n1. Approve");
        System.out.println("2. Reject");
        System.out.println("3. Cancel");
        
        int action = getIntInput("Enter your choice: ", 1, 3);
        
        switch (action) {
            case 1:
                if (systemManager.getInternshipManager().approveInternship(selectedInternship.getInternshipID())) {
                    System.out.println("Internship opportunity approved successfully!");
                    System.out.println("It is now visible to eligible students.");
                } else {
                    System.out.println("Failed to approve internship.");
                }
                break;
            case 2:
                if (confirmAction("Are you sure you want to reject this internship?")) {
                    if (systemManager.getInternshipManager().rejectInternship(selectedInternship.getInternshipID())) {
                        System.out.println("Internship opportunity rejected.");
                    } else {
                        System.out.println("Failed to reject internship.");
                    }
                }
                break;
            case 3:
                return;
        }
    }
    
    /**
     * Manage withdrawal requests
     */
    private void manageWithdrawalRequests() {
        System.out.println("\n--- Manage Withdrawal Requests ---");
        List<Application> withdrawalRequests = systemManager.getApplicationManager().getWithdrawalRequests();
        
        if (withdrawalRequests.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }
        
        System.out.println("Pending withdrawal requests:");
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            Application app = withdrawalRequests.get(i);
            InternshipOpportunity internship = systemManager.getInternship(app.getInternshipID());
            User student = systemManager.getUser(app.getStudentID());
            
            System.out.printf("[%d] %s - %s\n", i + 1,
                            student != null ? student.getName() : "Unknown",
                            internship != null ? internship.getTitle() : "Unknown");
            System.out.printf("    Reason: %s\n", app.getWithdrawalReason());
            System.out.printf("    Application Status: %s\n", app.getStatus());
        }
        
        int choice = getIntInput("Select request to review (0 to cancel): ", 0, withdrawalRequests.size());
        if (choice == 0) return;
        
        Application selectedApp = withdrawalRequests.get(choice - 1);
        InternshipOpportunity internship = systemManager.getInternship(selectedApp.getInternshipID());
        User student = systemManager.getUser(selectedApp.getStudentID());
        
        System.out.println("\n--- Withdrawal Request Details ---");
        System.out.println("Student: " + (student != null ? student.getName() : "Unknown"));
        System.out.println("Internship: " + (internship != null ? internship.getTitle() : "Unknown"));
        System.out.println("Company: " + (internship != null ? internship.getCompanyName() : "Unknown"));
        System.out.println("Application Status: " + selectedApp.getStatus());
        System.out.println("Reason: " + selectedApp.getWithdrawalReason());
        
        System.out.println("\n1. Approve Withdrawal");
        System.out.println("2. Reject Withdrawal");
        System.out.println("3. Cancel");
        
        int action = getIntInput("Enter your choice: ", 1, 3);
        
        switch (action) {
            case 1:
                if (systemManager.getApplicationManager().approveWithdrawal(selectedApp.getApplicationID())) {
                    System.out.println("Withdrawal request approved successfully!");
                } else {
                    System.out.println("Failed to approve withdrawal.");
                }
                break;
            case 2:
                if (systemManager.getApplicationManager().rejectWithdrawal(selectedApp.getApplicationID())) {
                    System.out.println("Withdrawal request rejected.");
                } else {
                    System.out.println("Failed to reject withdrawal.");
                }
                break;
            case 3:
                return;
        }
    }
    
    /**
     * Generate reports
     */
    private void generateReports() {
        System.out.println("\n--- Generate Reports ---");
        System.out.println("1. Internship Statistics Report");
        System.out.println("2. Application Statistics Report");
        System.out.println("3. Filtered Internship Report");
        System.out.println("4. Cancel");
        
        int choice = getIntInput("Enter your choice: ", 1, 4);
        
        switch (choice) {
            case 1:
                generateInternshipStatisticsReport();
                break;
            case 2:
                generateApplicationStatisticsReport();
                break;
            case 3:
                generateFilteredInternshipReport();
                break;
            case 4:
                return;
        }
    }
    
    /**
     * Generate internship statistics report
     */
    private void generateInternshipStatisticsReport() {
        System.out.println("\n--- Internship Statistics Report ---");
        Map<String, Integer> stats = systemManager.getInternshipManager().getInternshipStatistics();
        
        System.out.println("Total Internships: " + stats.get("Total"));
        System.out.println("Pending: " + stats.get("Pending"));
        System.out.println("Approved: " + stats.get("Approved"));
        System.out.println("Rejected: " + stats.get("Rejected"));
        System.out.println("Filled: " + stats.get("Filled"));
        
        pauseForUser();
    }
    
    /**
     * Generate application statistics report
     */
    private void generateApplicationStatisticsReport() {
        System.out.println("\n--- Application Statistics Report ---");
        Map<String, Integer> stats = systemManager.getApplicationManager().getApplicationStatistics();
        
        System.out.println("Total Applications: " + stats.get("Total"));
        System.out.println("Pending: " + stats.get("Pending"));
        System.out.println("Successful: " + stats.get("Successful"));
        System.out.println("Unsuccessful: " + stats.get("Unsuccessful"));
        System.out.println("Withdrawn: " + stats.get("Withdrawn"));
        System.out.println("Withdrawal Requests: " + stats.get("Withdrawal Requests"));
        
        pauseForUser();
    }
    
    /**
     * Generate filtered internship report
     */
    private void generateFilteredInternshipReport() {
        System.out.println("\n--- Filtered Internship Report ---");
        
        // Get filter criteria
        System.out.println("Filter options (press Enter to skip):");
        
        System.out.print("Filter by Status (PENDING/APPROVED/REJECTED/FILLED or Enter for all): ");
        String statusFilter = scanner.nextLine().trim();
        InternshipStatus status = statusFilter.isEmpty() ? null : InternshipStatus.fromString(statusFilter);
        
        System.out.print("Filter by Major (or Enter for all): ");
        String majorFilter = scanner.nextLine().trim();
        Major major = majorFilter.isEmpty() ? null : Major.fromString(majorFilter);
        
        System.out.print("Filter by Level (BASIC/INTERMEDIATE/ADVANCED or Enter for all): ");
        String levelFilter = scanner.nextLine().trim();
        InternshipLevel level = levelFilter.isEmpty() ? null : InternshipLevel.fromString(levelFilter);
        
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getAllInternships(status, major, level);
        
        System.out.println("\nFound " + internships.size() + " internship(s):");
        System.out.println("=" + "=".repeat(120));
        
        for (InternshipOpportunity internship : internships) {
            System.out.printf("ID: %s | Title: %s\n", internship.getInternshipID(), internship.getTitle());
            System.out.printf("Company: %s | Level: %s | Major: %s\n",
                            internship.getCompanyName(), internship.getLevel(), internship.getPreferredMajor());
            System.out.printf("Status: %s | Visible: %s | Slots: %d/%d\n",
                            internship.getStatus(), internship.isVisible(),
                            internship.getFilledSlots(), internship.getTotalSlots());
            System.out.printf("Dates: %s to %s | Applicants: %d\n",
                            internship.getOpeningDate(), internship.getClosingDate(),
                            internship.getApplicantIDs().size());
            System.out.println("-".repeat(120));
        }
        
        pauseForUser();
    }
    
    /**
     * View system statistics
     */
    private void viewSystemStatistics() {
        System.out.println("\n--- System Statistics ---");
        
        // User statistics
        System.out.println("=== User Statistics ===");
        List<Student> students = systemManager.getUserManager().getAllStudents();
        List<CompanyRepresentative> reps = systemManager.getUserManager().getAllCompanyRepresentatives();
        List<CareerCenterStaff> staff = systemManager.getUserManager().getAllCareerCenterStaff();
        
        System.out.println("Total Students: " + students.size());
        System.out.println("Total Company Representatives: " + reps.size());
        System.out.println("Approved Company Representatives: " + 
                         reps.stream().filter(CompanyRepresentative::isApproved).count());
        System.out.println("Pending Company Representatives: " + 
                         systemManager.getUserManager().getPendingApprovals().size());
        System.out.println("Total Career Center Staff: " + staff.size());
        
        // Internship statistics
        System.out.println("\n=== Internship Statistics ===");
        Map<String, Integer> internshipStats = systemManager.getInternshipManager().getInternshipStatistics();
        internshipStats.forEach((key, value) -> System.out.println(key + ": " + value));
        
        // Application statistics
        System.out.println("\n=== Application Statistics ===");
        Map<String, Integer> applicationStats = systemManager.getApplicationManager().getApplicationStatistics();
        applicationStats.forEach((key, value) -> System.out.println(key + ": " + value));
        
        pauseForUser();
    }
    
    /**
     * View all internships
     */
    private void viewAllInternships() {
        System.out.println("\n--- All Internships ---");
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getAllInternships(null, null, null);
        
        if (internships.isEmpty()) {
            System.out.println("No internships found.");
            return;
        }
        
        System.out.println("Found " + internships.size() + " internship(s):");
        System.out.println("=" + "=".repeat(120));
        
        for (InternshipOpportunity internship : internships) {
            System.out.printf("ID: %s | Title: %s\n", internship.getInternshipID(), internship.getTitle());
            System.out.printf("Company: %s | Level: %s | Status: %s\n",
                            internship.getCompanyName(), internship.getLevel(), internship.getStatus());
            System.out.printf("Major: %s | Visible: %s | Slots: %d/%d\n",
                            internship.getPreferredMajor(), internship.isVisible(),
                            internship.getFilledSlots(), internship.getTotalSlots());
            System.out.printf("Dates: %s to %s | Applicants: %d\n",
                            internship.getOpeningDate(), internship.getClosingDate(),
                            internship.getApplicantIDs().size());
            System.out.println("-".repeat(120));
        }
        
        pauseForUser();
    }
}