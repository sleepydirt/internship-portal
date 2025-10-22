package src;

import src.boundary.MainMenu;
import src.control.SystemManager;

/**
 * Main class for the Internship Placement Management System
 * Entry point of the application
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class Main {
    /**
     * Main method to start the application
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== Internship Placement Management System ===");
        System.out.println("Initializing system...");
        
        // Initialize system manager and load data
        SystemManager systemManager = SystemManager.getInstance();
        systemManager.initializeSystem();
        
        // Start main menu
        MainMenu mainMenu = new MainMenu(systemManager);
        mainMenu.displayMainMenu();
    }
}