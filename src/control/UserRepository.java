package control;

import entity.*;
import java.util.*;

/**
 * User Repository - Manages user data storage and retrieval
 * Provides data access methods for user entities
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class UserRepository {
    private Map<String, User> users;
    private List<CompanyRepresentative> pendingApprovals;

    /**
     * Constructor for UserRepository
     */
    public UserRepository() {
        this.users = new HashMap<>();
        this.pendingApprovals = new ArrayList<>();
    }

    /**
     * Get user by ID
     * 
     * @param userID user ID
     * @return user or null if not found
     */
    public User getById(String userID) {
        return users.get(userID);
    }

    /**
     * Add user to repository
     * 
     * @param user user to add
     * @return true if added successfully, false if user already exists
     */
    public boolean add(User user) {
        if (users.containsKey(user.getUserID())) {
            return false;
        }

        users.put(user.getUserID(), user);

        // Add company representatives to pending approvals if not approved
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (!rep.isApproved()) {
                pendingApprovals.add(rep);
            }
        }

        return true;
    }

    /**
     * Remove user from repository
     * 
     * @param userID user ID to remove
     * @return true if removed successfully, false if user not found
     */
    public boolean remove(String userID) {
        User user = users.remove(userID);
        if (user != null && user instanceof CompanyRepresentative) {
            pendingApprovals.remove(user);
        }
        return user != null;
    }

    /**
     * Check if user exists
     * 
     * @param userID user ID
     * @return true if user exists
     */
    public boolean exists(String userID) {
        return users.containsKey(userID);
    }

    /**
     * Get all users
     * 
     * @return map of all users
     */
    public Map<String, User> getAll() {
        return users;
    }

    /**
     * Get pending company representative approvals
     * 
     * @return list of pending company representatives
     */
    public List<CompanyRepresentative> getPendingApprovals() {
        return new ArrayList<>(pendingApprovals);
    }

    /**
     * Remove from pending approvals (after approval/rejection)
     * 
     * @param representative company representative to remove
     */
    public void removeFromPendingApprovals(CompanyRepresentative representative) {
        pendingApprovals.remove(representative);
    }

    /**
     * Get count of users
     * 
     * @return total number of users
     */
    public int size() {
        return users.size();
    }

    /**
     * Check if repository is empty
     * 
     * @return true if no users exist
     */
    public boolean isEmpty() {
        return users.isEmpty();
    }
}
