package src.control;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import src.entity.*;
import src.enums.*;

/**
 * Internship Manager - Handles internship-related operations
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class InternshipManager {
    private SystemManager systemManager;
    
    /**
     * Constructor for InternshipManager
     * @param systemManager reference to system manager
     */
    public InternshipManager(SystemManager systemManager) {
        this.systemManager = systemManager;
    }
    
    /**
     * Create a new internship opportunity
     * @param title internship title
     * @param description internship description
     * @param level internship level
     * @param preferredMajor preferred major
     * @param openingDate opening date
     * @param closingDate closing date
     * @param companyRepresentativeID representative creating the internship
     * @param totalSlots total number of slots
     * @param filledSlots total number of filled slots
     * @return created internship or null if creation failed
     */
    public InternshipOpportunity createInternship(String title, String description,
                                                 InternshipLevel level, Major preferredMajor,
                                                 LocalDate openingDate, LocalDate closingDate,
                                                 String companyRepresentativeID, int totalSlots , int filledSlots) {
        
        User user = systemManager.getUser(companyRepresentativeID);
        if (!(user instanceof CompanyRepresentative)) {
            return null;
        }
        
        CompanyRepresentative rep = (CompanyRepresentative) user;
        if (!rep.canCreateMore()) {
            return null; // Already created maximum internships
        }
        
        String internshipID = systemManager.generateInternshipID();
        InternshipOpportunity internship = new InternshipOpportunity(
            internshipID, title, description, level, preferredMajor,
            openingDate, closingDate, rep.getCompanyName(),
            companyRepresentativeID, totalSlots, filledSlots
        );
        
        if (systemManager.addInternship(internship)) {
            rep.addCreatedInternship(internshipID);
            return internship;
        }
        
        return null;
    }
    
    /**
     * Get internships created by a company representative
     * @param representativeID representative ID
     * @return list of internships
     */
    public List<InternshipOpportunity> getInternshipsByRepresentative(String representativeID) {
        return systemManager.getInternships().values().stream()
                .filter(internship -> internship.getCompanyRepresentativeID().equals(representativeID))
                .collect(Collectors.toList());
    }
    
    /**
     * Get internships visible to students based on filters
     * @param student student viewing the internships
     * @param statusFilter status filter (null for no filter)
     * @param majorFilter major filter (null for no filter)
     * @param levelFilter level filter (null for no filter)
     * @return filtered list of internships
     */
    public List<InternshipOpportunity> getVisibleInternships(Student student,
                                                            InternshipStatus statusFilter,
                                                            Major majorFilter,
                                                            InternshipLevel levelFilter) {
        return systemManager.getInternships().values().stream()
                .filter(internship -> internship.isVisible())
                .filter(internship -> internship.getStatus() == InternshipStatus.APPROVED)
                .filter(internship -> internship.isStudentEligible(student))
                .filter(internship -> statusFilter == null || internship.getStatus() == statusFilter)
                .filter(internship -> majorFilter == null || internship.getPreferredMajor() == majorFilter)
                .filter(internship -> levelFilter == null || internship.getLevel() == levelFilter)
                .sorted(Comparator.comparing(InternshipOpportunity::getTitle))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all internships for career center staff with filters
     * @param statusFilter status filter (null for no filter)
     * @param majorFilter major filter (null for no filter)
     * @param levelFilter level filter (null for no filter)
     * @return filtered list of internships
     */
    public List<InternshipOpportunity> getAllInternships(InternshipStatus statusFilter,
                                                        Major majorFilter,
                                                        InternshipLevel levelFilter) {
        return systemManager.getInternships().values().stream()
                .filter(internship -> statusFilter == null || internship.getStatus() == statusFilter)
                .filter(internship -> majorFilter == null || internship.getPreferredMajor() == majorFilter)
                .filter(internship -> levelFilter == null || internship.getLevel() == levelFilter)
                .sorted(Comparator.comparing(InternshipOpportunity::getTitle))
                .collect(Collectors.toList());
    }
    
    /**
     * Get pending internships for approval
     * @return list of pending internships
     */
    public List<InternshipOpportunity> getPendingInternships() {
        return systemManager.getInternships().values().stream()
                .filter(internship -> internship.getStatus() == InternshipStatus.PENDING)
                .sorted(Comparator.comparing(InternshipOpportunity::getTitle))
                .collect(Collectors.toList());
    }
    
    /**
     * Approve an internship opportunity
     * @param internshipID internship ID
     * @return true if approved successfully
     */
    public boolean approveInternship(String internshipID) {
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        if (internship != null && internship.getStatus() == InternshipStatus.PENDING) {
            internship.setStatus(InternshipStatus.APPROVED);
            internship.setVisible(true); // Make visible when approved
            return true;
        }
        return false;
    }
    
    /**
     * Reject an internship opportunity
     * @param internshipID internship ID
     * @return true if rejected successfully
     */
    public boolean rejectInternship(String internshipID) {
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        if (internship != null && internship.getStatus() == InternshipStatus.PENDING) {
            internship.setStatus(InternshipStatus.REJECTED);
            return true;
        }
        return false;
    }
    
    /**
     * Toggle internship visibility
     * @param internshipID internship ID
     * @param visible new visibility status
     * @return true if toggled successfully
     */
    public boolean toggleInternshipVisibility(String internshipID, boolean visible) {
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        if (internship != null && internship.getStatus() == InternshipStatus.APPROVED) {
            internship.setVisible(visible);
            return true;
        }
        return false;
    }
    
    /**
     * Update internship details (before approval)
     * @param internshipID internship ID
     * @param title new title
     * @param description new description
     * @param level new level
     * @param preferredMajor new preferred major
     * @param openingDate new opening date
     * @param closingDate new closing date
     * @param totalSlots new total slots
     * @return true if updated successfully
     */
    public boolean updateInternship(String internshipID, String title, String description,
                                   InternshipLevel level, Major preferredMajor,
                                   LocalDate openingDate, LocalDate closingDate, int totalSlots) {
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        if (internship != null && internship.getStatus() == InternshipStatus.PENDING) {
            internship.setTitle(title);
            internship.setDescription(description);
            internship.setLevel(level);
            internship.setPreferredMajor(preferredMajor);
            internship.setOpeningDate(openingDate);
            internship.setClosingDate(closingDate);
            internship.setTotalSlots(totalSlots);
            return true;
        }
        return false;
    }
    
    /**
     * Delete an internship opportunity (before approval or if no applications)
     * @param internshipID internship ID
     * @param representativeID representative requesting deletion
     * @return true if deleted successfully
     */
    public boolean deleteInternship(String internshipID, String representativeID) {
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        if (internship != null && 
            internship.getCompanyRepresentativeID().equals(representativeID) &&
            (internship.getStatus() == InternshipStatus.PENDING || 
             internship.getApplicantIDs().isEmpty())) {
            
            // Remove from company representative's list
            User user = systemManager.getUser(representativeID);
            if (user instanceof CompanyRepresentative) {
                ((CompanyRepresentative) user).removeCreatedInternship(internshipID);
            }
            
            // Remove from system
            systemManager.getInternships().remove(internshipID);
            return true;
        }
        return false;
    }
    
    /**
     * Get internship statistics
     * @return map of statistics
     */
    public Map<String, Integer> getInternshipStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        Collection<InternshipOpportunity> internships = systemManager.getInternships().values();
        
        stats.put("Total", internships.size());
        stats.put("Pending", (int) internships.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count());
        stats.put("Approved", (int) internships.stream().filter(i -> i.getStatus() == InternshipStatus.APPROVED).count());
        stats.put("Rejected", (int) internships.stream().filter(i -> i.getStatus() == InternshipStatus.REJECTED).count());
        stats.put("Filled", (int) internships.stream().filter(i -> i.getStatus() == InternshipStatus.FILLED).count());
        
        return stats;
    }
}