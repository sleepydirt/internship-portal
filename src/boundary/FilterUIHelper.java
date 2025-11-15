package boundary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import control.InternshipFilterSettings;
import enums.*;

/**
 * Filter UI Helper - Provides common filter input methods
 * Reduces code duplication across menu classes
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class FilterUIHelper {
    // ANSI formatting constants
    private static final String RESET = "\u001B[0m";
    private static final String ITALIC = "\u001B[3m";
    private static final String UNDERLINE = "\u001B[4m";
    
    private Scanner scanner;
    
    public FilterUIHelper(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Prompt user with filter builder template to update the user's filter by Major
     * @param filterSettings filter settings to update
     */
    public void promptMajorFilter(InternshipFilterSettings filterSettings) {
        System.out.println("\n" + ITALIC + UNDERLINE + "Filtering by Major" + RESET);
        int index = 1;
        for (Major m : Major.values()) {
            System.out.println(index + ". " + m.getDisplayName());
            index++;
        }
        System.out.print("Select Major (1-" + Major.values().length + ", or press Enter for all): ");
        
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            filterSettings.setMajorFilter(null);
            System.out.println("No major filter applied (showing all).");
        } else {
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= Major.values().length) {
                    filterSettings.setMajorFilter(Major.values()[choice - 1]);
                    System.out.println("Major filter set to: " + Major.values()[choice - 1].getDisplayName());
                } else {
                    System.out.println("Invalid input provided. Showing all results instead.");
                    filterSettings.setMajorFilter(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input provided. Showing all results instead.");
                filterSettings.setMajorFilter(null);
            }
        }
    }
    
    /**
     * Prompt user with filter builder template to update the user's filter by internship level
     * @param filterSettings filter settings to update
     */
    public void promptLevelFilter(InternshipFilterSettings filterSettings) {
        System.out.println("\n" + ITALIC + UNDERLINE + "Filtering by Level" + RESET);
        System.out.println("1. BASIC");
        System.out.println("2. INTERMEDIATE");
        System.out.println("3. ADVANCED");
        System.out.print("Select Level (1-3, or press Enter for all): ");
        
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            filterSettings.setLevelFilter(null);
            System.out.println("No level filter applied (showing all).");
        } else {
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 3) {
                    InternshipLevel level = InternshipLevel.values()[choice - 1];
                    filterSettings.setLevelFilter(level);
                    System.out.println("Level filter set to: " + level);
                } else {
                    System.out.println("Invalid input provided. Showing all results instead.");
                    filterSettings.setLevelFilter(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input provided. Showing all results instead.");
                filterSettings.setLevelFilter(null);
            }
        }
    }
    
    /**
     * Prompt user with filter builder template to update the user's filter by closing date
     * @param filterSettings filter settings to update
     */
    public void promptClosingDateFilter(InternshipFilterSettings filterSettings) {
        System.out.println("\n" + ITALIC + UNDERLINE + "Filtering by Closing Date" + RESET);
        
        System.out.print("Enter closing date FROM (YYYY-MM-DD, or press Enter to skip): ");
        String fromInput = scanner.nextLine().trim();
        if (!fromInput.isEmpty()) {
            LocalDate fromDate = parseDate(fromInput);
            if (fromDate != null) {
                filterSettings.setClosingDateFrom(fromDate);
                System.out.println("Closing date FROM set to: " + fromDate);
            } else {
                System.out.println("Invalid input provided. Showing all results instead.");
                filterSettings.setClosingDateFrom(null);
            }
        } else {
            filterSettings.setClosingDateFrom(null);
            System.out.println("No FROM date filter applied.");
        }
        
        System.out.print("Enter closing date TO (YYYY-MM-DD, or press Enter to skip): ");
        String toInput = scanner.nextLine().trim();
        if (!toInput.isEmpty()) {
            LocalDate toDate = parseDate(toInput);
            if (toDate != null) {
                filterSettings.setClosingDateTo(toDate);
                System.out.println("Closing date TO set to: " + toDate);
            } else {
                System.out.println("Invalid input provided. Showing all results instead.");
                filterSettings.setClosingDateTo(null);
            }
        } else {
            filterSettings.setClosingDateTo(null);
            System.out.println("No TO date filter applied.");
        }
    }
    
    /**
     * Prompt user with filter builder template to update the user's filter by number of available slots
     * @param filterSettings filter settings to update
     */
    public void promptSlotsFilter(InternshipFilterSettings filterSettings) {
        System.out.println("\n" + ITALIC + UNDERLINE + "Filtering by Available Slots" + RESET);
        System.out.print("Enter minimum available slots (or press Enter to skip): ");
        
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            filterSettings.setMinAvailableSlots(null);
            System.out.println("No slots filter applied.");
        } else {
            try {
                int minSlots = Integer.parseInt(input);
                if (minSlots > 0) {
                    filterSettings.setMinAvailableSlots(minSlots);
                    System.out.println("Minimum slots filter set to: " + minSlots);
                } else {
                    System.out.println("Invalid input provided. Showing all results instead.");
                    filterSettings.setMinAvailableSlots(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input provided. Showing all results instead.");
                filterSettings.setMinAvailableSlots(null);
            }
        }
    }
    
    /**
     * Prompt Career Staff/Company Rep with filter builder template to update their filters by internship status
     * @param filterSettings filter settings to update
     */
    public void promptStatusFilter(InternshipFilterSettings filterSettings) {
        System.out.println("\n" + ITALIC + UNDERLINE + "Filtering by Status" + RESET);
        System.out.println("1. PENDING");
        System.out.println("2. APPROVED");
        System.out.println("3. REJECTED");
        System.out.println("4. FILLED");
        System.out.print("Select Status (1-4, or press Enter for all): ");
        
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            filterSettings.setStatusFilter(null);
            System.out.println("No status filter applied (showing all).");
        } else {
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= 4) {
                    InternshipStatus status = InternshipStatus.values()[choice - 1];
                    filterSettings.setStatusFilter(status);
                    System.out.println("Status filter set to: " + status);
                } else {
                    System.out.println("Invalid input provided. Showing all results instead.");
                    filterSettings.setStatusFilter(null);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input provided. Showing all results instead.");
                filterSettings.setStatusFilter(null);
            }
        }
    }
    
    /**
     * Build complete filter settings by prompting user sequentially
     * Major → Level → Closing Date → Slots
     * @param filterSettings filter settings to populate
     */
    public void buildFiltersSequentially(InternshipFilterSettings filterSettings) {
        System.out.println("\n=== Configure Filters ===");
        System.out.println("(Press Enter to skip any filter)\n");
        
        promptMajorFilter(filterSettings);
        promptLevelFilter(filterSettings);
        promptClosingDateFilter(filterSettings);
        promptSlotsFilter(filterSettings);
        
        System.out.println("\n✓ Filters configured!");
    }
    
    /**
     * Build filters for company representative or staff (includes status)
     * Status → Major → Level → Closing Date → Slots
     * @param filterSettings filter settings to populate
     */
    public void buildFiltersWithStatus(InternshipFilterSettings filterSettings) {
        System.out.println("\n=== Configure Filters ===");
        System.out.println("(Press Enter to skip any filter)\n");
        
        promptStatusFilter(filterSettings);
        promptMajorFilter(filterSettings);
        promptLevelFilter(filterSettings);
        promptClosingDateFilter(filterSettings);
        promptSlotsFilter(filterSettings);
        
        System.out.println("\n✓ Filters configured!");
    }
    
    /**
     * Parse date string
     */
    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
