package control;

import enums.*;
import java.time.LocalDate;

/**
 * Filter Settings for Internship Listings
 * Stores user filter preferences for the current session
 * 
 * @author SC2002 Group
 * @version 1.0
 */
public class InternshipFilterSettings {
    private InternshipStatus statusFilter;
    private Major majorFilter;
    private InternshipLevel levelFilter;
    private LocalDate closingDateFrom;
    private LocalDate closingDateTo;
    private Integer minAvailableSlots;
    private boolean showOnlyApplied;
    
    /**
     * Constructor - initialises with no filters
     */
    public InternshipFilterSettings() {
        this.statusFilter = null;
        this.majorFilter = null;
        this.levelFilter = null;
        this.closingDateFrom = null;
        this.closingDateTo = null;
        this.minAvailableSlots = null;
        this.showOnlyApplied = false;
    }
    
    // Getters
    public InternshipStatus getStatusFilter() { return statusFilter; }
    public Major getMajorFilter() { return majorFilter; }
    public InternshipLevel getLevelFilter() { return levelFilter; }
    public LocalDate getClosingDateFrom() { return closingDateFrom; }
    public LocalDate getClosingDateTo() { return closingDateTo; }
    public Integer getMinAvailableSlots() { return minAvailableSlots; }
    public boolean isShowOnlyApplied() { return showOnlyApplied; }
    
    // Setters
    public void setStatusFilter(InternshipStatus statusFilter) { 
        this.statusFilter = statusFilter; 
    }
    
    public void setMajorFilter(Major majorFilter) { 
        this.majorFilter = majorFilter; 
    }
    
    public void setLevelFilter(InternshipLevel levelFilter) { 
        this.levelFilter = levelFilter; 
    }
    
    public void setClosingDateFrom(LocalDate closingDateFrom) { 
        this.closingDateFrom = closingDateFrom; 
    }
    
    public void setClosingDateTo(LocalDate closingDateTo) { 
        this.closingDateTo = closingDateTo; 
    }
    
    public void setMinAvailableSlots(Integer minAvailableSlots) { 
        this.minAvailableSlots = minAvailableSlots; 
    }
    
    public void setShowOnlyApplied(boolean showOnlyApplied) { 
        this.showOnlyApplied = showOnlyApplied; 
    }
    
    /**
     * Clear all filters
     */
    public void clearAll() {
        this.statusFilter = null;
        this.majorFilter = null;
        this.levelFilter = null;
        this.closingDateFrom = null;
        this.closingDateTo = null;
        this.minAvailableSlots = null;
        this.showOnlyApplied = false;
    }
    
    /**
     * Check if any filters are active
     * @return true if at least one filter is set
     */
    public boolean hasActiveFilters() {
        return statusFilter != null || majorFilter != null || levelFilter != null ||
               closingDateFrom != null || closingDateTo != null || minAvailableSlots != null ||
               showOnlyApplied;
    }
    
    /**
     * Get a summary of active filters to display to the user
     * @return string describing active filters
     */
    public String getFilterSummary() {
        if (!hasActiveFilters()) {
            return "No filters applied";
        }
        
        StringBuilder summary = new StringBuilder("Active filters: ");
        boolean first = true;
        
        if (statusFilter != null) {
            summary.append("Status=").append(statusFilter);
            first = false;
        }
        if (majorFilter != null) {
            if (!first) summary.append(", ");
            summary.append("Major=").append(majorFilter.getDisplayName());
            first = false;
        }
        if (levelFilter != null) {
            if (!first) summary.append(", ");
            summary.append("Level=").append(levelFilter);
            first = false;
        }
        if (closingDateFrom != null) {
            if (!first) summary.append(", ");
            summary.append("ClosingFrom=").append(closingDateFrom);
            first = false;
        }
        if (closingDateTo != null) {
            if (!first) summary.append(", ");
            summary.append("ClosingTo=").append(closingDateTo);
            first = false;
        }
        if (minAvailableSlots != null) {
            if (!first) summary.append(", ");
            summary.append("MinSlots=").append(minAvailableSlots);
            first = false;
        }
        if (showOnlyApplied) {
            if (!first) summary.append(", ");
            summary.append("ShowOnlyApplied");
        }
        
        return summary.toString();
    }
}
