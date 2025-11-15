package src.boundary;

import java.util.List;
import src.control.SystemManager;
import src.control.InternshipFilterSettings;
import src.entity.*;
import src.enums.*;

/**
 * Student Menu - Interface for student users
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class StudentMenu extends BaseMenu {

    /**
     * Constructor for StudentMenu
     * 
     * @param systemManager reference to system manager
     * @param currentUser   currently logged in student
     */
    public StudentMenu(SystemManager systemManager, User currentUser) {
        super(systemManager, currentUser);
    }

    @Override
    public void displayMenu() {
        Student student = (Student) currentUser;

        while (true) {
            System.out.println("\n=== Student Dashboard ===");
            System.out.println("Welcome, " + student.getName());
            System.out.println("Year: " + student.getYearOfStudy() + " | Major: " + student.getMajor());
            System.out.println("Applied Internships: " + student.getAppliedInternships().size() + "/3");

            if (student.hasAcceptedInternship()) {
                System.out.println("Accepted Internship: " + student.getAcceptedInternshipID());
            }

            System.out.println("\n--- Menu Options ---");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Accept Internship Placement");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("7. View Profile");
            System.out.println("8. Logout");

            int choice = getIntInput("Enter your choice: ", 1, 8);

            switch (choice) {
                case 1:
                    viewAvailableInternships();
                    break;
                case 2:
                    applyForInternship();
                    break;
                case 3:
                    viewMyApplications();
                    break;
                case 4:
                    acceptInternshipPlacement();
                    break;
                case 5:
                    requestWithdrawal();
                    break;
                case 6:
                    handlePasswordChange();
                    break;
                case 7:
                    displayUserInfo();
                    pauseForUser();
                    break;
                case 8:
                    handleLogout();
                    return;
            }
        }
    }

    /**
     * View available internships with filtering options
     */
    private void viewAvailableInternships() {
        Student student = (Student) currentUser;
        InternshipFilterSettings filterSettings = systemManager.getFilterSettings(student.getUserID());
        FilterUIHelper filterHelper = new FilterUIHelper(scanner);
        
        // Check if filters have been configured before
        if (!filterSettings.hasActiveFilters() && !hasViewedBefore(student.getUserID())) {
            // First time viewing, so display filter builder template
            filterHelper.buildFiltersSequentially(filterSettings);
            markAsViewed(student.getUserID());
        }
        
        // Display internships with current filters
        displayFilteredInternships(student, filterSettings);
        
        // Single prompt: reconfigure or return
        System.out.print("\nPress 'f' to reconfigure filters, or Enter to return: ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("f")) {
            filterSettings.clearAll();
            filterHelper.buildFiltersSequentially(filterSettings);
            displayFilteredInternships(student, filterSettings);
            System.out.print("\nPress Enter to return: ");
            scanner.nextLine();
        }
    }
    
    /**
     * Check if user has viewed internships before in this session
     * @param userID user ID to check
     * @return true if user has configured filters before
     */
    private boolean hasViewedBefore(String userID) {
        return systemManager.hasViewedInternships(userID);
    }
    
    /**
     * Mark that user has viewed internships and configured filters
     * @param userID user ID to mark
     */
    private void markAsViewed(String userID) {
        systemManager.markInternshipsViewed(userID);
    }
    
    /**
     * Display filtered internships
     */
    private void displayFilteredInternships(Student student, InternshipFilterSettings filterSettings) {
        // Show active filters at the top
        if (filterSettings.hasActiveFilters()) {
            System.out.println("\n" + filterSettings.getFilterSummary());
        } else {
            System.out.println("\nNo filters applied (showing all eligible internships)");
        }
        
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getVisibleInternshipsWithFilters(student, filterSettings);

        if (internships.isEmpty()) {
            System.out.println("No internships available matching your criteria.");
            return;
        }

        System.out.println("\n--- \u001B[4mAvailable Internships\u001B[0m ---");
        System.out.println("Found " + internships.size() + " internship(s):");
        System.out.println("=" + "=".repeat(100));

        for (int i = 0; i < internships.size(); i++) {
            InternshipOpportunity internship = internships.get(i);
            
            // Mark if student has applied
            String appliedMark = student.getAppliedInternships().contains(internship.getInternshipID()) 
                               ? " [APPLIED]" : "";
            
            // Mark if visibility is off (student can still see because they applied)
            String visibilityMark = !internship.isVisible() ? " [HIDDEN]" : "";
            
            System.out.printf("[%d] %s%s%s\n", i + 1, internship.getTitle(), appliedMark, visibilityMark);
            System.out.printf("    Company: %s | Level: %s | Major: %s\n",
                    internship.getCompanyName(),
                    internship.getLevel(),
                    internship.getPreferredMajor().getDisplayName());
            System.out.printf("    Slots: %d/%d | Closing: %s\n",
                    internship.getFilledSlots(),
                    internship.getTotalSlots(),
                    internship.getClosingDate());
            System.out.printf("    Description: %s\n", internship.getDescription());
            System.out.println("    " + "-".repeat(90));
        }
    }

    /**
     * Apply for an internship
     */
    private void applyForInternship() {
        Student student = (Student) currentUser;

        if (!student.canApplyForMore()) {
            System.out.println("\nYou cannot apply for more internships.");
            if (student.hasAcceptedInternship()) {
                System.out.println("You have already accepted an internship placement.");
            } else {
                System.out.println("You have reached the maximum of 3 applications.");
            }
            return;
        }

        System.out.println("\n--- Apply for Internship ---");
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getVisibleInternships(student, InternshipStatus.APPROVED, null, null);

        if (internships.isEmpty()) {
            System.out.println("No internships available for application.");
            return;
        }

        // Display available internships
        System.out.println("Available internships:");
        for (int i = 0; i < internships.size(); i++) {
            InternshipOpportunity internship = internships.get(i);
            System.out.printf("[%d] %s - %s (%s, %d slots available)\n",
                    i + 1, internship.getTitle(), internship.getCompanyName(),
                    internship.getLevel(), internship.getAvailableSlots());
        }

        int choice = getIntInput("Select internship to apply (0 to cancel): ", 0, internships.size());
        if (choice == 0)
            return;

        InternshipOpportunity selectedInternship = internships.get(choice - 1);

        // Check if already applied
        if (student.getAppliedInternships().contains(selectedInternship.getInternshipID())) {
            System.out.println("You have already applied for this internship.");
            return;
        }

        // Confirm application
        System.out.println("\nInternship Details:");
        System.out.println("Title: " + selectedInternship.getTitle());
        System.out.println("Company: " + selectedInternship.getCompanyName());
        System.out.println("Level: " + selectedInternship.getLevel());
        System.out.println("Description: " + selectedInternship.getDescription());

        if (confirmAction("Do you want to apply for this internship?")) {
            Application application = systemManager.getApplicationManager()
                    .submitApplication(student.getUserID(), selectedInternship.getInternshipID());

            if (application != null) {
                System.out.println("Application submitted successfully!");
                System.out.println("Application ID: " + application.getApplicationID());
            } else {
                System.out.println("Failed to submit application. Please check eligibility requirements.");
            }
        }
    }

    /**
     * View student's applications
     */
    private void viewMyApplications() {

        System.out.println("\n--- My Applications ---");
        Student student = (Student) currentUser;

        List<Application> applications = systemManager.getApplicationManager()
                .getApplicationsByStudent(student.getUserID());

        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
            return;
        }

        System.out.println("Found " + applications.size() + " application(s):");
        System.out.println("=" + "=".repeat(80));

        for (Application app : applications) {
            InternshipOpportunity internship = systemManager.getInternshipRepository().getById(app.getInternshipID());

            System.out.printf("Application ID: %s\n", app.getApplicationID());
            System.out.printf("Internship: %s - %s\n",
                    internship != null ? internship.getTitle() : "Unknown",
                    internship != null ? internship.getCompanyName() : "Unknown");
            System.out.printf("Status: %s\n", app.getStatus());
            System.out.printf("Applied Date: %s\n", app.getApplicationDate().toLocalDate());

            if (app.isWithdrawalRequested()) {
                System.out.printf("Withdrawal Requested: %s\n", app.getWithdrawalReason());
                System.out.printf("Withdrawal Status: %s\n",
                        app.isWithdrawalApproved() ? "Approved" : "Pending");
            }

            System.out.println("-".repeat(80));
        }

        pauseForUser();
    }

    /**
     * Accept an internship placement
     */
    private void acceptInternshipPlacement() {
        Student student = (Student) currentUser;

        if (student.hasAcceptedInternship()) {
            System.out.println("\nYou have already accepted an internship placement.");
            return;
        }

        System.out.println("\n--- Accept Internship Placement ---");
        List<Application> applications = systemManager.getApplicationManager()
                .getApplicationsByStudent(student.getUserID());

        List<Application> successfulApps = applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
                .toList();

        if (successfulApps.isEmpty()) {
            System.out.println("You have no successful applications to accept.");
            return;
        }

        System.out.println("Successful applications available for acceptance:");
        for (int i = 0; i < successfulApps.size(); i++) {
            Application app = successfulApps.get(i);
            InternshipOpportunity internship = systemManager.getInternshipRepository().getById(app.getInternshipID());

            System.out.printf("[%d] %s - %s\n", i + 1,
                    internship != null ? internship.getTitle() : "Unknown",
                    internship != null ? internship.getCompanyName() : "Unknown");
        }

        int choice = getIntInput("Select application to accept (0 to cancel): ", 0, successfulApps.size());
        if (choice == 0)
            return;

        Application selectedApp = successfulApps.get(choice - 1);

        System.out.println(
                "\nWARNING: Accepting this placement will automatically withdraw all your other applications.");
        if (confirmAction("Are you sure you want to accept this internship placement?")) {
            if (systemManager.getApplicationManager().acceptPlacement(
                    selectedApp.getApplicationID(), student.getUserID())) {
                System.out.println("Internship placement accepted successfully!");
                System.out.println("All other applications have been withdrawn.");
            } else {
                System.out.println("Failed to accept placement. Please try again.");
            }
        }
    }

    /**
     * Request withdrawal from an application
     */
    private void requestWithdrawal() {
        Student student = (Student) currentUser;

        System.out.println("\n--- Request Withdrawal ---");
        List<Application> applications = systemManager.getApplicationManager()
                .getApplicationsByStudent(student.getUserID());

        List<Application> withdrawableApps = applications.stream()
                .filter(Application::canBeWithdrawn)
                .filter(app -> !app.isWithdrawalRequested())
                .toList();

        if (withdrawableApps.isEmpty()) {
            System.out.println("You have no applications that can be withdrawn.");
            return;
        }

        System.out.println("Applications available for withdrawal:");
        for (int i = 0; i < withdrawableApps.size(); i++) {
            Application app = withdrawableApps.get(i);
            InternshipOpportunity internship = systemManager.getInternshipRepository().getById(app.getInternshipID());

            System.out.printf("[%d] %s - %s (Status: %s)\n", i + 1,
                    internship != null ? internship.getTitle() : "Unknown",
                    internship != null ? internship.getCompanyName() : "Unknown",
                    app.getStatus());
        }

        int choice = getIntInput("Select application to withdraw (0 to cancel): ", 0, withdrawableApps.size());
        if (choice == 0)
            return;

        Application selectedApp = withdrawableApps.get(choice - 1);
        String reason = getStringInput("Enter reason for withdrawal: ", true);

        if (confirmAction("Are you sure you want to request withdrawal?")) {
            if (systemManager.getApplicationManager().requestWithdrawal(
                    selectedApp.getApplicationID(), student.getUserID(), reason)) {
                System.out.println("Withdrawal request submitted successfully!");
                System.out.println("Your request is pending approval from Career Center Staff.");
            } else {
                System.out.println("Failed to submit withdrawal request.");
            }
        }
    }
}