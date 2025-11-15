package src.boundary;

import src.control.SystemManager;
import src.entity.User;
import java.util.Scanner;

/**
 * Main Menu - Entry point for the application interface
 * 
 * <p>
 * Provides options for user login, company representative registration, and system exit.
 * Routes authenticated users to their respective menus based on their user type.
 * </p>
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class MainMenu {
	
	/** Reference to the system manager for accessing system operations */
    private SystemManager systemManager;
    
    /** Scanner for reading user input from the console */
    private Scanner scanner;
    
    /**
     * Constructor for MainMenu
     * @param systemManager reference to system manager
     */
    public MainMenu(SystemManager systemManager) {
        this.systemManager = systemManager;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Display main menu and handle user interactions
     * <p>
     * Users can choose to login, register as a company representative, or exit the system.
     * Routes users to the appropriate sub-menu based on their user type after login.
     * </p>
     */
    public void displayMainMenu() {
        System.out.println("\n=== Welcome to Internship Placement Management System ===");
        
        while (true) {
        	//Display main menu options
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register Company Representative");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        handleCompanyRegistration();
                        break;
                    case 3:
                        handleExit();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * Handle user login
     * <p>
     * Prompts user for credentials, authenticates using the UserManager, 
     * and routes the user to the appropriate menu based on their user type.
     * </p>
     */
    private void handleLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter User ID: ");
        String userID = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        User user = systemManager.getUserManager().authenticateUser(userID, password);
        
        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.getName());
            
            // Route to appropriate menu based on user type
            switch (user.getUserType()) {
                case "STUDENT":
                    StudentMenu studentMenu = new StudentMenu(systemManager, user);
                    studentMenu.displayMenu();
                    break;
                case "COMPANY_REPRESENTATIVE":
                    CompanyRepresentativeMenu companyMenu = new CompanyRepresentativeMenu(systemManager, user);
                    companyMenu.displayMenu();
                    break;
                case "CAREER_CENTER_STAFF":
                    CareerCenterStaffMenu staffMenu = new CareerCenterStaffMenu(systemManager, user);
                    staffMenu.displayMenu();
                    break;
                default:
                    System.out.println("Unknown user type.");
            }
        } else {
            System.out.println("Login failed. Invalid credentials or account not approved.");
        }
    }
    
    /**
     * Handle company representative registration
     * <p>
     * Prompts the user for company information and personal details, validates input,
     * and registers the account pending approval by career center staff.
     * </p>
     */
    private void handleCompanyRegistration() {
        System.out.println("\n--- Company Representative Registration ---");
        
        //Prompt for company email and validate
        System.out.print("Enter Company Email: ");
        String email = scanner.nextLine().trim();
        
        if (!systemManager.getUserManager().isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }
        
        if (systemManager.getUser(email) != null) {
            System.out.println("User with this email already exists.");
            return;
        }
        
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Enter Company Name: ");
        String companyName = scanner.nextLine().trim();
        
        System.out.print("Enter Department: ");
        String department = scanner.nextLine().trim();
        
        System.out.print("Enter Position: ");
        String position = scanner.nextLine().trim();

        if (systemManager.getUserManager().registerCompanyRepresentative(
                email, name, password, companyName, department, position)) {
            System.out.println("Registration successful! Your account is pending approval from Career Center Staff.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }
    
    /**
     * Handle application exit
     */
    private void handleExit() {
    	//Display exit message
        System.out.println("\nThank you for using the Internship Placement Management System!");
        System.out.println("Saving data and shutting down...");
        systemManager.shutdown(); //save all system data
        scanner.close();
    }
}
