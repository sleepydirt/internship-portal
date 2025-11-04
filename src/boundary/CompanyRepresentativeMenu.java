package src.boundary;

import src.control.SystemManager;
import src.entity.*;
import src.enums.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Company Representative Menu - Interface for company representative users
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CompanyRepresentativeMenu extends BaseMenu {
    
    /**
     * Constructor for CompanyRepresentativeMenu
     * @param systemManager reference to system manager
     * @param currentUser currently logged in company representative
     */
    public CompanyRepresentativeMenu(SystemManager systemManager, User currentUser) {
        super(systemManager, currentUser);
    }
    
    @Override
    public void displayMenu() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        
        while (true) {
            System.out.println("\n=== Company Representative Dashboard ===");
            System.out.println("Welcome, " + rep.getName());
            System.out.println("Company: " + rep.getCompanyName());
            System.out.println("Department: " + rep.getDepartment());
            System.out.println("Created Internships: " + rep.getCreatedInternships().size() + "/5");
            
            System.out.println("\n--- Menu Options ---");
            System.out.println("1. Create Internship Opportunity");
            System.out.println("2. View My Internships");
            System.out.println("3. Edit Internship (Pending Only)");
            System.out.println("4. Delete Internship");
            System.out.println("5. View Applications");
            System.out.println("6. Manage Applications");
            System.out.println("7. Toggle Internship Visibility");
            System.out.println("8. Change Password");
            System.out.println("9. View Profile");
            System.out.println("10. Logout");
            
            int choice = getIntInput("Enter your choice: ", 1, 10);
            
            switch (choice) {
                case 1:
                    createInternshipOpportunity();
                    break;
                case 2:
                    viewMyInternships();
                    break;
                case 3:
                    editInternship();
                    break;
                case 4:
                    deleteInternship();
                    break;
                case 5:
                    viewApplications();
                    break;
                case 6:
                    manageApplications();
                    break;
                case 7:
                    toggleInternshipVisibility();
                    break;
                case 8:
                    handlePasswordChange();
                    break;
                case 9:
                    displayUserInfo();
                    pauseForUser();
                    break;
                case 10:
                    handleLogout();
                    return;
            }
        }
    }
    
    /**
     * Create a new internship opportunity
     */
    private void createInternshipOpportunity() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        
        if (!rep.canCreateMore()) {
            System.out.println("\nYou have reached the maximum of 5 internship opportunities.");
            return;
        }
        
        System.out.println("\n--- Create Internship Opportunity ---");
        
        String title = getStringInput("Enter internship title: ", true);
        String description = getStringInput("Enter internship description: ", true);
        
        // Get internship level
        System.out.println("Select internship level:");
        System.out.println("1. Basic");
        System.out.println("2. Intermediate");
        System.out.println("3. Advanced");
        int levelChoice = getIntInput("Enter choice: ", 1, 3);
        InternshipLevel level = InternshipLevel.values()[levelChoice - 1];
        
        // Get preferred major
        System.out.println("Select preferred major:");
        Major[] majors = Major.values();
        for (int i = 0; i < majors.length; i++) {
            System.out.printf("%d. %s\n", i + 1, majors[i].getDisplayName());
        }
        int majorChoice = getIntInput("Enter choice: ", 1, majors.length);
        Major preferredMajor = majors[majorChoice - 1];
        
        // Get dates
        LocalDate openingDate = getDateInput("Enter opening date (YYYY-MM-DD): ");
        LocalDate closingDate = getDateInput("Enter closing date (YYYY-MM-DD): ");
        
        if (closingDate.isBefore(openingDate)) {
            System.out.println("Closing date cannot be before opening date.");
            return;
        }
        
        int totalSlots = getIntInput("Enter total number of slots (1-10): ", 1, 10);
        
        // Confirm creation
        System.out.println("\n--- Internship Summary ---");
        System.out.println("Title: " + title);
        System.out.println("Company: " + rep.getCompanyName());
        System.out.println("Level: " + level);
        System.out.println("Preferred Major: " + preferredMajor);
        System.out.println("Opening Date: " + openingDate);
        System.out.println("Closing Date: " + closingDate);
        System.out.println("Total Slots: " + totalSlots);
        
        if (confirmAction("Create this internship opportunity?")) {
            InternshipOpportunity internship = systemManager.getInternshipManager()
                    .createInternship(title, description, level, preferredMajor,
                                    openingDate, closingDate, rep.getUserID(), totalSlots);
            
            if (internship != null) {
                System.out.println("Internship opportunity created successfully!");
                System.out.println("Internship ID: " + internship.getInternshipID());
                System.out.println("Status: Pending approval from Career Center Staff");
            } else {
                System.out.println("Failed to create internship opportunity.");
            }
        }
    }
    
    /**
     * View internships created by this representative
     */
    private void viewMyInternships() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        
        System.out.println("\n--- My Internships ---");
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getInternshipsByRepresentative(rep.getUserID());
        
        if (internships.isEmpty()) {
            System.out.println("You have not created any internships yet.");
            return;
        }
        
        System.out.println("Found " + internships.size() + " internship(s):");
        System.out.println("=" + "=".repeat(100));
        
        for (InternshipOpportunity internship : internships) {
            System.out.printf("ID: %s | Title: %s\n", 
                            internship.getInternshipID(), internship.getTitle());
            System.out.printf("Level: %s | Major: %s | Status: %s\n",
                            internship.getLevel(), internship.getPreferredMajor(), internship.getStatus());
            System.out.printf("Dates: %s to %s | Visible: %s\n",
                            internship.getOpeningDate(), internship.getClosingDate(), internship.isVisible());
            System.out.printf("Slots: %d/%d | Applicants: %d\n",
                            internship.getFilledSlots(), internship.getTotalSlots(), 
                            internship.getApplicantIDs().size());
            System.out.printf("Description: %s\n", internship.getDescription());
            System.out.println("-".repeat(100));
        }
        
        pauseForUser();
    }
    
    /**
     * Get date input with validation
     */
    private LocalDate getDateInput(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine().trim();
            
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }
    
    /**
     * Edit a pending internship
     */
    private void editInternship() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        
        System.out.println("\n--- Edit Internship ---");
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getInternshipsByRepresentative(rep.getUserID()).stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .toList();
        
        if (internships.isEmpty()) {
            System.out.println("You have no pending internships that can be edited.");
            return;
        }
        
        System.out.println("Pending internships:");
        for (int i = 0; i < internships.size(); i++) {
            InternshipOpportunity internship = internships.get(i);
            System.out.printf("[%d] %s - %s\n", i + 1, internship.getInternshipID(), internship.getTitle());
        }
        
        int choice = getIntInput("Select internship to edit (0 to cancel): ", 0, internships.size());
        if (choice == 0) return;
        
        InternshipOpportunity internship = internships.get(choice - 1);
        
        System.out.println("\nCurrent details:");
        System.out.println("Title: " + internship.getTitle());
        System.out.println("Description: " + internship.getDescription());
        System.out.println("Level: " + internship.getLevel());
        System.out.println("Preferred Major: " + internship.getPreferredMajor());
        System.out.println("Opening Date: " + internship.getOpeningDate());
        System.out.println("Closing Date: " + internship.getClosingDate());
        System.out.println("Total Slots: " + internship.getTotalSlots());
        
        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        String newTitle = getStringInput("New title [" + internship.getTitle() + "]: ", false);
        if (newTitle.isEmpty()) newTitle = internship.getTitle();
        
        String newDescription = getStringInput("New description [" + internship.getDescription() + "]: ", false);
        if (newDescription.isEmpty()) newDescription = internship.getDescription();
        
        // For simplicity, we'll keep the current level, major, dates, and slots
        // In a full implementation, you'd allow editing these as well
        
        if (confirmAction("Save changes?")) {
            if (systemManager.getInternshipManager().updateInternship(
                    internship.getInternshipID(), newTitle, newDescription,
                    internship.getLevel(), internship.getPreferredMajor(),
                    internship.getOpeningDate(), internship.getClosingDate(),
                    internship.getTotalSlots())) {
                System.out.println("Internship updated successfully!");
            } else {
                System.out.println("Failed to update internship.");
            }
        }
    }
    
    /**
     * Delete an internship
     */
    private void deleteInternship() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        
        System.out.println("\n--- Delete Internship ---");
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getInternshipsByRepresentative(rep.getUserID());
        
        if (internships.isEmpty()) {
            System.out.println("You have no internships to delete.");
            return;
        }
        
        System.out.println("Your internships:");
        for (int i = 0; i < internships.size(); i++) {
            InternshipOpportunity internship = internships.get(i);
            System.out.printf("[%d] %s - %s (Status: %s, Applicants: %d)\n", 
                            i + 1, internship.getInternshipID(), internship.getTitle(),
                            internship.getStatus(), internship.getApplicantIDs().size());
        }
        
        int choice = getIntInput("Select internship to delete (0 to cancel): ", 0, internships.size());
        if (choice == 0) return;
        
        InternshipOpportunity internship = internships.get(choice - 1);
        
        System.out.println("\nWARNING: This action cannot be undone.");
        if (internship.getApplicantIDs().size() > 0) {
            System.out.println("This internship has " + internship.getApplicantIDs().size() + " applicants.");
        }
        
        if (confirmAction("Are you sure you want to delete this internship?")) {
            if (systemManager.getInternshipManager().deleteInternship(
                    internship.getInternshipID(), rep.getUserID())) {
                System.out.println("Internship deleted successfully!");
            } else {
                System.out.println("Failed to delete internship. It may have applications or be approved.");
            }
        }
    }
    
    /**
     * View applications for internships
     */
    private void viewApplications() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        System.out.println("\n--- View Applications ---");
        List<Application> applications = systemManager.getApplicationManager()
                .getApplicationsByRepresentative(rep.getUserID());
        
        if (applications.isEmpty()) {
            System.out.println("No applications found for your internships.");
            return;
        }
        
        System.out.println("Found " + applications.size() + " application(s):");
        System.out.println("=" + "=".repeat(100));
        
        for (Application app : applications) {
            InternshipOpportunity internship = systemManager.getInternship(app.getInternshipID());
            User student = systemManager.getUser(app.getStudentID());
            
            System.out.printf("Application ID: %s\n", app.getApplicationID());
            System.out.printf("Internship: %s\n", internship != null ? internship.getTitle() : "Unknown");
            System.out.printf("Student: %s (%s)\n", 
                            student != null ? student.getName() : "Unknown",
                            app.getStudentID());
            
            if (student instanceof Student) {
                Student s = (Student) student;
                System.out.printf("Student Details: Year %d, Major %s\n", 
                                s.getYearOfStudy(), s.getMajor());
            }
            
            System.out.printf("Status: %s\n", app.getStatus());
            System.out.printf("Applied Date: %s\n", app.getApplicationDate().toLocalDate());
            System.out.println("-".repeat(100));
        }
        pauseForUser();
    }
    
    /**
     * Manage applications (approve/reject)
     */
    private void manageApplications() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        System.out.println("\n--- Manage Applications ---");
        List<Application> applications = systemManager.getApplicationManager()
                .getApplicationsByRepresentative(rep.getUserID()).stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                .toList();
        
        if (applications.isEmpty()) {
            System.out.println("No pending applications to manage.");
            return;
        }
        
        System.out.println("Pending applications:");
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            InternshipOpportunity internship = systemManager.getInternship(app.getInternshipID());
            User student = systemManager.getUser(app.getStudentID());
            
            System.out.printf("[%d] %s - %s (%s)\n", i + 1,
                            internship != null ? internship.getTitle() : "Unknown",
                            student != null ? student.getName() : "Unknown",
                            app.getApplicationID());
        }
        
        System.out.print("Select application to manage (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice <= 0 || choice > applications.size()) return;
            
            Application selectedApp = applications.get(choice - 1);
            InternshipOpportunity internship = systemManager.getInternship(selectedApp.getInternshipID());
            User student = systemManager.getUser(selectedApp.getStudentID());
            
            System.out.println("\nApplication Details:");
            System.out.println("Application ID: " + selectedApp.getApplicationID());
            System.out.println("Internship: " + (internship != null ? internship.getTitle() : "Unknown"));
            System.out.println("Student: " + (student != null ? student.getName() : "Unknown"));
            System.out.println("Applied Date: " + selectedApp.getApplicationDate().toLocalDate());
            
            if (internship != null) {
                System.out.println("Available Slots: " + internship.getAvailableSlots());
            }
            
            System.out.println("\n1. Approve Application");
            System.out.println("2. Reject Application");
            System.out.println("3. Cancel");
            
            System.out.print("Enter your choice: ");
            int action = Integer.parseInt(scanner.nextLine().trim());
            
            switch (action) {
                case 1:
                    if (systemManager.getApplicationManager().approveApplication(
                            selectedApp.getApplicationID(), rep.getUserID())) {
                        System.out.println("Application approved successfully!");
                    } else {
                        System.out.println("Failed to approve application. Check available slots.");
                    }
                    break;
                case 2:
                    if (systemManager.getApplicationManager().rejectApplication(
                            selectedApp.getApplicationID(), rep.getUserID())) {
                        System.out.println("Application rejected successfully!");
                    } else {
                        System.out.println("Failed to reject application.");
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
    
    /**
     * Toggle internship visibility
     */
    private void toggleInternshipVisibility() {
        CompanyRepresentative rep = (CompanyRepresentative) currentUser;
        System.out.println("\n--- Toggle Internship Visibility ---");
        List<InternshipOpportunity> internships = systemManager.getInternshipManager()
                .getInternshipsByRepresentative(rep.getUserID()).stream()
                .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
                .toList();
        
        if (internships.isEmpty()) {
            System.out.println("You have no approved internships to toggle visibility.");
            return;
        }
        
        System.out.println("Approved internships:");
        for (int i = 0; i < internships.size(); i++) {
            InternshipOpportunity internship = internships.get(i);
            System.out.printf("[%d] %s - %s (Visible: %s)\n", i + 1,
                            internship.getInternshipID(), internship.getTitle(),
                            internship.isVisible() ? "Yes" : "No");
        }
        
        System.out.print("Select internship to toggle visibility (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice <= 0 || choice > internships.size()) return;
            
            InternshipOpportunity internship = internships.get(choice - 1);
            boolean newVisibility = !internship.isVisible();
            
            if (systemManager.getInternshipManager().toggleInternshipVisibility(
                    internship.getInternshipID(), newVisibility)) {
                System.out.println("Visibility toggled successfully!");
                System.out.println("Internship is now " + (newVisibility ? "visible" : "hidden") + " to students.");
            } else {
                System.out.println("Failed to toggle visibility.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }
}