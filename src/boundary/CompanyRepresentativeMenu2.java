package src.boundary;

import src.control.SystemManager;
import src.entity.*;
import src.enums.*;
import java.util.List;

/**
 * Company Representative Menu Part 2 - Additional methods for company representative interface
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class CompanyRepresentativeMenu2 {
    
    /**
     * View applications for internships
     */
    public static void viewApplications(SystemManager systemManager, CompanyRepresentative rep) {
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
    }
    
    /**
     * Manage applications (approve/reject)
     */
    public static void manageApplications(SystemManager systemManager, CompanyRepresentative rep, java.util.Scanner scanner) {
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
    public static void toggleInternshipVisibility(SystemManager systemManager, CompanyRepresentative rep, java.util.Scanner scanner) {
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