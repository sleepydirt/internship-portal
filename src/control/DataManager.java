package src.control;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import src.entity.*;
import src.enums.*;

/**
 * Data Manager - Handles file I/O operations for data persistence
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class DataManager {
    private SystemManager systemManager;
    private static final String DATA_DIR = "data/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final String INTERNSHIPS_FILE = DATA_DIR + "internships.txt";
    private static final String APPLICATIONS_FILE = DATA_DIR + "applications.txt";
    
    /**
     * Constructor for DataManager
     * @param systemManager reference to system manager
     */
    public DataManager(SystemManager systemManager) {
        this.systemManager = systemManager;
        createDataDirectory();
    }
    
    /**
     * Create data directory if it doesn't exist
     */
    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    /**
     * Load all data from files
     */
    public void loadAllData() {
        loadUsers();
        loadInternships();
        loadApplications();
        
        // If no data exists, create default data
        if (systemManager.getUsers().isEmpty()) {
            System.out.println("No existing data found. Creating default users...");
            systemManager.getUserManager().createDefaultUsers();
            saveAllData(); // Save the default data
        }
    }
    
    /**
     * Save all data to files
     */
    public void saveAllData() {
        saveUsers();
        saveInternships();
        saveApplications();
    }
    
    /**
     * Load users from file
     */
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseUserFromLine(line);
                if (user != null) {
                    systemManager.addUser(user);
                }
            }
            System.out.println("Loaded " + systemManager.getUsers().size() + " users.");
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    /**
     * Save users to file
     */
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : systemManager.getUsers().values()) {
                writer.println(formatUserToLine(user));
            }
            System.out.println("Saved " + systemManager.getUsers().size() + " users.");
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Load internships from file
     */
    private void loadInternships() {
        File file = new File(INTERNSHIPS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                InternshipOpportunity internship = parseInternshipFromLine(line);
                if (internship != null) {
                    systemManager.addInternship(internship);
                }
            }
            System.out.println("Loaded " + systemManager.getInternships().size() + " internships.");
        } catch (IOException e) {
            System.err.println("Error loading internships: " + e.getMessage());
        }
    }
    
    /**
     * Save internships to file
     */
    private void saveInternships() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INTERNSHIPS_FILE))) {
            for (InternshipOpportunity internship : systemManager.getInternships().values()) {
                writer.println(formatInternshipToLine(internship));
            }
            System.out.println("Saved " + systemManager.getInternships().size() + " internships.");
        } catch (IOException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }
    }
    
    /**
     * Load applications from file
     */
    private void loadApplications() {
        File file = new File(APPLICATIONS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Application application = parseApplicationFromLine(line);
                if (application != null) {
                    systemManager.addApplication(application);
                }
            }
            System.out.println("Loaded " + systemManager.getApplications().size() + " applications.");
        } catch (IOException e) {
            System.err.println("Error loading applications: " + e.getMessage());
        }
    }
    
    /**
     * Save applications to file
     */
    private void saveApplications() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPLICATIONS_FILE))) {
            for (Application application : systemManager.getApplications().values()) {
                writer.println(formatApplicationToLine(application));
            }
            System.out.println("Saved " + systemManager.getApplications().size() + " applications.");
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }
    
    /**
     * Parse user from file line
     * Format: UserType|UserID|Name|Password|AdditionalFields...
     */
    private User parseUserFromLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 4) return null;
            
            String userType = parts[0];
            String userID = parts[1];
            String name = parts[2];
            String password = parts[3];
            
            switch (userType) {
                case "STUDENT":
                    if (parts.length >= 6) {
                        int yearOfStudy = Integer.parseInt(parts[4]);
                        Major major = Major.fromString(parts[5]);
                        Student student = new Student(userID, name, password, yearOfStudy, major);
                        
                        // Load applied internships if present
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            String[] appliedInternships = parts[6].split(",");
                            for (String internshipID : appliedInternships) {
                                student.applyForInternship(internshipID.trim());
                            }
                        }
                        
                        // Load accepted internship if present
                        if (parts.length > 7 && !parts[7].isEmpty()) {
                            student.acceptInternship(parts[7]);
                        }
                        
                        return student;
                    }
                    break;
                    
                case "COMPANY_REPRESENTATIVE":
                    if (parts.length >= 8) {
                        String companyName = parts[4];
                        String department = parts[5];
                        String position = parts[6];
                        boolean isApproved = Boolean.parseBoolean(parts[7]);
                        
                        CompanyRepresentative rep = new CompanyRepresentative(
                            userID, name, password, companyName, department, position);
                        rep.setApproved(isApproved);
                        
                        // Load created internships if present
                        if (parts.length > 8 && !parts[8].isEmpty()) {
                            String[] createdInternships = parts[8].split(",");
                            for (String internshipID : createdInternships) {
                                rep.addCreatedInternship(internshipID.trim());
                            }
                        }
                        
                        return rep;
                    }
                    break;
                    
                case "CAREER_CENTER_STAFF":
                    if (parts.length >= 5) {
                        String staffDepartment = parts[4];
                        return new CareerCenterStaff(userID, name, password, staffDepartment);
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error parsing user line: " + line + " - " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Format user to file line
     */
    private String formatUserToLine(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUserType()).append("|");
        sb.append(user.getUserID()).append("|");
        sb.append(user.getName()).append("|");
        sb.append(user.getPassword()).append("|");
        
        if (user instanceof Student) {
            Student student = (Student) user;
            sb.append(student.getYearOfStudy()).append("|");
            sb.append(student.getMajor().name()).append("|");
            sb.append(String.join(",", student.getAppliedInternships())).append("|");
            sb.append(student.getAcceptedInternshipID() != null ? student.getAcceptedInternshipID() : "");
            
        } else if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            sb.append(rep.getCompanyName()).append("|");
            sb.append(rep.getDepartment()).append("|");
            sb.append(rep.getPosition()).append("|");
            sb.append(rep.isApproved()).append("|");
            sb.append(String.join(",", rep.getCreatedInternships()));
            
        } else if (user instanceof CareerCenterStaff) {
            CareerCenterStaff staff = (CareerCenterStaff) user;
            sb.append(staff.getStaffDepartment());
        }
        
        return sb.toString();
    }
    
    /**
     * Parse internship from file line
     */
    private InternshipOpportunity parseInternshipFromLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 12) return null;
            
            String internshipID = parts[0];
            String title = parts[1];
            String description = parts[2];
            InternshipLevel level = InternshipLevel.fromString(parts[3]);
            Major preferredMajor = Major.fromString(parts[4]);
            LocalDate openingDate = LocalDate.parse(parts[5]);
            LocalDate closingDate = LocalDate.parse(parts[6]);
            InternshipStatus status = InternshipStatus.fromString(parts[7]);
            String companyName = parts[8];
            String companyRepresentativeID = parts[9];
            int totalSlots = Integer.parseInt(parts[10]);
            int filledSlots = Integer.parseInt(parts[11]);
            boolean visible = parts.length > 12 ? Boolean.parseBoolean(parts[12]) : false;
            
            InternshipOpportunity internship = new InternshipOpportunity(
                internshipID, title, description, level, preferredMajor,
                openingDate, closingDate, companyName, companyRepresentativeID, totalSlots, filledSlots);
            
            internship.setStatus(status);
            internship.setVisible(visible);
            
            // Set filled slots (this would require additional method in InternshipOpportunity)
            // For now, we'll handle this through applications
            
            // Load applicants if present
            if (parts.length > 13 && !parts[13].isEmpty()) {
                String[] applicants = parts[13].split(",");
                for (String applicantID : applicants) {
                    internship.addApplicant(applicantID.trim());
                }
            }
            
            return internship;
            
        } catch (Exception e) {
            System.err.println("Error parsing internship line: " + line + " - " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Format internship to file line
     */
    private String formatInternshipToLine(InternshipOpportunity internship) {
        StringBuilder sb = new StringBuilder();
        sb.append(internship.getInternshipID()).append("|");
        sb.append(internship.getTitle()).append("|");
        sb.append(internship.getDescription()).append("|");
        sb.append(internship.getLevel().name()).append("|");
        sb.append(internship.getPreferredMajor().name()).append("|");
        sb.append(internship.getOpeningDate()).append("|");
        sb.append(internship.getClosingDate()).append("|");
        sb.append(internship.getStatus().name()).append("|");
        sb.append(internship.getCompanyName()).append("|");
        sb.append(internship.getCompanyRepresentativeID()).append("|");
        sb.append(internship.getTotalSlots()).append("|");
        sb.append(internship.getFilledSlots()).append("|");
        sb.append(internship.isVisible()).append("|");
        sb.append(String.join(",", internship.getApplicantIDs()));
        
        return sb.toString();
    }
    
    /**
     * Parse application from file line
     */
    private Application parseApplicationFromLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 6) return null;
            
            String applicationID = parts[0];
            String studentID = parts[1];
            String internshipID = parts[2];
            ApplicationStatus status = ApplicationStatus.fromString(parts[3]);
            LocalDateTime applicationDate = LocalDateTime.parse(parts[4]);
            LocalDateTime statusUpdateDate = LocalDateTime.parse(parts[5]);
            
            Application application = new Application(applicationID, studentID, internshipID);
            application.setStatus(status);
            
            // Load withdrawal information if present
            if (parts.length > 6 && !parts[6].isEmpty()) {
                application.requestWithdrawal(parts[6]);
                if (parts.length > 7) {
                    boolean withdrawalApproved = Boolean.parseBoolean(parts[7]);
                    if (withdrawalApproved) {
                        application.approveWithdrawal();
                    }
                }
            }
            
            return application;
            
        } catch (Exception e) {
            System.err.println("Error parsing application line: " + line + " - " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Format application to file line
     */
    private String formatApplicationToLine(Application application) {
        StringBuilder sb = new StringBuilder();
        sb.append(application.getApplicationID()).append("|");
        sb.append(application.getStudentID()).append("|");
        sb.append(application.getInternshipID()).append("|");
        sb.append(application.getStatus().name()).append("|");
        sb.append(application.getApplicationDate()).append("|");
        sb.append(application.getStatusUpdateDate()).append("|");
        sb.append(application.getWithdrawalReason() != null ? application.getWithdrawalReason() : "").append("|");
        sb.append(application.isWithdrawalApproved());
        
        return sb.toString();
    }
}