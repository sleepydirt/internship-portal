package control;

import entity.InternshipOpportunity;
import java.util.*;

/**
 * Internship Repository - Manages internship data storage and retrieval
 * Provides data access methods for internship opportunities
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class InternshipRepository {
    private Map<String, InternshipOpportunity> internships;

    /**
     * Constructor for InternshipRepository
     */
    public InternshipRepository() {
        this.internships = new HashMap<>();
    }

    /**
     * Get internship by ID
     * 
     * @param internshipID internship ID
     * @return internship or null if not found
     */
    public InternshipOpportunity getById(String internshipID) {
        return internships.get(internshipID);
    }

    /**
     * Add internship to repository
     * 
     * @param internship internship to add
     * @return true if added successfully, false if internship already exists
     */
    public boolean add(InternshipOpportunity internship) {
        if (internships.containsKey(internship.getInternshipID())) {
            return false;
        }
        internships.put(internship.getInternshipID(), internship);
        return true;
    }

    /**
     * Remove internship from repository
     * 
     * @param internshipID internship ID to remove
     * @return true if removed successfully, false if internship not found
     */
    public boolean remove(String internshipID) {
        return internships.remove(internshipID) != null;
    }

    /**
     * Check if internship exists
     * 
     * @param internshipID internship ID
     * @return true if internship exists
     */
    public boolean exists(String internshipID) {
        return internships.containsKey(internshipID);
    }

    /**
     * Get all internships
     * 
     * @return map of all internships
     */
    public Map<String, InternshipOpportunity> getAll() {
        return internships;
    }

    /**
     * Get count of internships
     * 
     * @return total number of internships
     */
    public int size() {
        return internships.size();
    }

    /**
     * Check if repository is empty
     * 
     * @return true if no internships exist
     */
    public boolean isEmpty() {
        return internships.isEmpty();
    }
}
