package src.boundary;

import java.util.List;
import src.control.SystemManager;
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
        final String RESET = "\u001B[0m";
        final String ITALIC = "\u001B[3m";
        final String UNDERLINE = "\u001B[4m";
        System.out.println("\n--- " + UNDERLINE + "Available Internships" + RESET + " ---");

        // Get filter options
        // MAJOR FILTER
        System.out.println(UNDERLINE + ITALIC + "Filtering by Major" + RESET);
        int index = 1;
        for (Major m : Major.values()) {
            System.out.println(index + ". " + m.getDisplayName());
            index++;
        }

        System.out.print("Select Major (1-" + Major.values().length + ", or press Enter for all): ");
        String majorFilter = scanner.nextLine().trim();
        int majorChoice = majorFilter.isEmpty() ? -1 : Integer.parseInt(majorFilter); // int choice index
        Major major;
        if (majorChoice >= 1 && majorChoice <= Major.values().length) {
            major = Major.values()[majorChoice - 1];
        } else {
            major = null; // user pressed Enter or invalid number
        }
        //Major major = majorFilter.isEmpty() ? null : Major.fromString(major);
        // expecting string

        // LEVEL FILTER
        System.out.println(UNDERLINE + ITALIC + "Filtering by Level" + RESET);
        System.out.println("1. BASIC");
        System.out.println("2. INTERMEDIATE");
        System.out.println("3. ADVANCED");
        System.out.print("Filter by Level (BASIC/INTERMEDIATE/ADVANCED or Enter for all): ");
        String choiceInput = scanner.nextLine().trim();
        int choice = choiceInput.isEmpty() ? -1 : Integer.parseInt(choiceInput);

        String levelFilter;
        switch (choice) {
            case 1:
                levelFilter = "BASIC";
                break;
            case 2:
                levelFilter = "INTERMEDIATE";
                break;
            case 3:
                levelFilter = "ADVANCED";
                break;
            default:
                levelFilter = null;
        }
        // String levelFilter = scanner.nextLine().trim();
        InternshipLevel level = (levelFilter == null || levelFilter.isEmpty()) ? null
                : InternshipLevel.fromString(levelFilter);

        Student student = (Student) currentUser;
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getVisibleInternships(student, InternshipStatus.APPROVED, major, level);

        if (internships.isEmpty()) {
            System.out.println("No internships available matching your criteria.");
            return;
        }

        System.out.println("\nFound " + internships.size() + " internship(s):");
        System.out.println("=" + "=".repeat(100));

        for (int i = 0; i < internships.size(); i++) {
            InternshipOpportunity internship = internships.get(i);
            System.out.printf("[%d] %s\n", i + 1, internship.getTitle());
            System.out.printf("    Company: %s | Level: %s | Major: %s\n",
                    internship.getCompanyName(),
                    internship.getLevel(),
                    internship.getPreferredMajor());
            System.out.printf("    Slots: %d/%d | Closing: %s\n",
                    internship.getFilledSlots(),
                    internship.getTotalSlots(),
                    internship.getClosingDate());
            System.out.printf("    Description: %s\n", internship.getDescription());
            System.out.println("    " + "-".repeat(90));
        }

        pauseForUser();
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
            InternshipOpportunity internship = systemManager.getInternship(app.getInternshipID());

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
            InternshipOpportunity internship = systemManager.getInternship(app.getInternshipID());

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
            InternshipOpportunity internship = systemManager.getInternship(app.getInternshipID());

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