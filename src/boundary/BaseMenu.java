package src.boundary;

import src.control.SystemManager;
import src.entity.User;
import java.util.Scanner;

/**
 * Base Menu - Abstract base class for all user-specific menus
 * Provides common functionality and interface
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public abstract class BaseMenu {
    protected SystemManager systemManager;
    protected User currentUser;
    protected Scanner scanner;
    
    /**
     * Constructor for BaseMenu
     * @param systemManager reference to system manager
     * @param currentUser currently logged in user
     */
    public BaseMenu(SystemManager systemManager, User currentUser) {
        this.systemManager = systemManager;
        this.currentUser = currentUser;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Display the main menu for this user type
     * To be implemented by subclasses
     */
    public abstract void displayMenu();
    
    /**
     * Handle password change functionality
     */
    protected void handlePasswordChange() {
        System.out.println("\n--- Change Password ---");
        
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();
        
        if (!currentUser.validatePassword(currentPassword)) {
            System.out.println("Current password is incorrect.");
            return;
        }
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine().trim();
        
        if (newPassword.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return;
        }
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Password confirmation does not match.");
            return;
        }
        
        if (systemManager.getUserManager().changePassword(
                currentUser.getUserID(), currentPassword, newPassword)) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password.");
        }
    }
    
    /**
     * Pause and wait for user input
     */
    protected void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Display user information
     */
    protected void displayUserInfo() {
        System.out.println("\n--- User Information ---");
        System.out.println("User ID: " + currentUser.getUserID());
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Role: " + currentUser.getRole());
    }
    
    /**
     * Get integer input with validation
     * @param prompt prompt message
     * @param min minimum value
     * @param max maximum value
     * @return validated integer input
     */
    protected int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * Get string input with validation
     * @param prompt prompt message
     * @param required whether the input is required (non-empty)
     * @return validated string input
     */
    protected String getStringInput(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!required || !input.isEmpty()) {
                return input;
            }
            System.out.println("This field is required. Please enter a value.");
        }
    }
    
    /**
     * Confirm action with user
     * @param message confirmation message
     * @return true if user confirms
     */
    protected boolean confirmAction(String message) {
        System.out.print(message + " (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
    
    /**
     * Display logout message and return to main menu
     */
    protected void handleLogout() {
        System.out.println("\nLogging out " + currentUser.getName() + "...");
        // Clear filter settings for this user session
        systemManager.clearFilterSettings(currentUser.getUserID());
        System.out.println("Thank you for using the system!");
    }
}