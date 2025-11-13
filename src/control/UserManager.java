package src.control;

import src.entity.*;
import src.enums.Major;
import java.util.*;

/**
 * User Manager - Handles user-related operations
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class UserManager {
    private UserRepository userRepository;

    /**
     * Constructor for UserManager
     * 
     * @param userRepository reference to UserRepository
     */
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticate user login
     * 
     * @param userID   user ID
     * @param password password
     * @return authenticated user or null if authentication fails
     */
    public User authenticateUser(String userID, String password) {
        User user = userRepository.getById(userID);
        if (user != null && user.validatePassword(password)) {
            // Check if company representative is approved
            if (user instanceof CompanyRepresentative) {
                CompanyRepresentative rep = (CompanyRepresentative) user;
                if (!rep.isApproved()) {
                    return null; // Not approved yet
                }
            }
            return user;
        }
        return null;
    }

    /**
     * Change user password
     * 
     * @param userID      user ID
     * @param oldPassword old password
     * @param newPassword new password
     * @return true if password changed successfully
     */
    public boolean changePassword(String userID, String oldPassword, String newPassword) {
        User user = userRepository.getById(userID);
        if (user != null && user.validatePassword(oldPassword)) {
            user.changePassword(newPassword);
            return true;
        }
        return false;
    }

    /**
     * Register a new company representative
     * 
     * @param email       company email
     * @param name        representative name
     * @param password    password
     * @param companyName company name
     * @param department  department
     * @param position    position
     * @return true if registration successful
     */
    public boolean registerCompanyRepresentative(String email, String name, String password,
            String companyName, String department, String position) {
        if (userRepository.getById(email) != null) {
            return false; // User already exists
        }

        CompanyRepresentative rep = new CompanyRepresentative(email, name, password,
                companyName, department, position);
        return userRepository.add(rep);
    }

    /**
     * Get all students
     * 
     * @return list of students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User user : userRepository.getAll().values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }

    /**
     * Get all company representatives
     * 
     * @return list of company representatives
     */
    public List<CompanyRepresentative> getAllCompanyRepresentatives() {
        List<CompanyRepresentative> reps = new ArrayList<>();
        for (User user : userRepository.getAll().values()) {
            if (user instanceof CompanyRepresentative) {
                reps.add((CompanyRepresentative) user);
            }
        }
        return reps;
    }

    /**
     * Get all career center staff
     * 
     * @return list of career center staff
     */
    public List<CareerCenterStaff> getAllCareerCenterStaff() {
        List<CareerCenterStaff> staff = new ArrayList<>();
        for (User user : userRepository.getAll().values()) {
            if (user instanceof CareerCenterStaff) {
                staff.add((CareerCenterStaff) user);
            }
        }
        return staff;
    }

    /**
     * Get pending company representative approvals
     * 
     * @return list of pending company representatives
     */
    public List<CompanyRepresentative> getPendingApprovals() {
        return new ArrayList<>(userRepository.getPendingApprovals());
    }

    /**
     * Approve company representative
     * 
     * @param representativeID representative ID
     * @return true if approved successfully
     */
    public boolean approveCompanyRepresentative(String representativeID) {
        User user = userRepository.getById(representativeID);
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            rep.setApproved(true);
            userRepository.removeFromPendingApprovals(rep);
            return true;
        }
        return false;
    }

    /**
     * Reject company representative
     * 
     * @param representativeID representative ID
     * @return true if rejected successfully
     */
    public boolean rejectCompanyRepresentative(String representativeID) {
        User user = userRepository.getById(representativeID);
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            userRepository.removeFromPendingApprovals(rep);
            userRepository.remove(representativeID);
            return true;
        }
        return false;
    }

    /**
     * Validate student ID format
     * 
     * @param studentID student ID to validate
     * @return true if valid format
     */
    public boolean isValidStudentID(String studentID) {
        if (studentID == null || studentID.length() != 9) {
            return false;
        }
        return studentID.startsWith("U") &&
                studentID.substring(1, 8).matches("\\d{7}") &&
                Character.isLetter(studentID.charAt(8));
    }

    /**
     * Validate email format for company representatives
     * 
     * @param email email to validate
     * @return true if valid email format
     */
    public boolean isValidEmail(String email) {
        if (email == null)
            return false;
        return email.contains("@") && email.contains(".");
    }

    /**
     * Create default users for testing
     */
    public void createDefaultUsers() {
        // Create default students
        Student student1 = new Student("U1234567A", "John Doe", "password", 3, Major.CSC);
        Student student2 = new Student("U2345678B", "Jane Smith", "password", 2, Major.EEE);
        Student student3 = new Student("U3456789C", "Bob Johnson", "password", 4, Major.MAE);

        userRepository.add(student1);
        userRepository.add(student2);
        userRepository.add(student3);

        // Create default career center staff
        CareerCenterStaff staff1 = new CareerCenterStaff("staff01@ntu.edu.sg", "Admin User", "password",
                "Career Services");
        CareerCenterStaff staff2 = new CareerCenterStaff("staff02@ntu.edu.sg", "Manager User", "password",
                "Student Affairs");

        userRepository.add(staff1);
        userRepository.add(staff2);

        // Create default company representative (pre-approved for testing)
        CompanyRepresentative rep1 = new CompanyRepresentative("hr@techcorp.com", "Alice Wilson", "password",
                "TechCorp Pte Ltd", "Human Resources", "HR Manager");
        rep1.setApproved(true);
        userRepository.add(rep1);

        System.out.println("Default users created successfully.");
    }
}