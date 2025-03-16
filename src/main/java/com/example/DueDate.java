package com.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javafx.collections.*;

/**
 * Represents a due date for an assignment in the grade tracking application.
 */
public class DueDate implements Comparable<DueDate> {
    private final String dueDateId;
    private String courseId;
    private String courseName; // For easier display
    private String moduleId; // Added field for module
    private String moduleName; // Added module name for display
    private String assignmentName;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
    private String priority; // "High", "Medium", "Low"
    
    /**
     * Constructs a new DueDate with the given details.
     * 
     * @param courseId The ID of the associated course
     * @param courseName The name of the associated course
     * @param assignmentName The name of the assignment
     * @param description A description of the assignment
     * @param dueDate The date the assignment is due
     * @param priority The priority level of the assignment
     */
    public DueDate(String courseId, String courseName, String assignmentName, String description, 
                  LocalDate dueDate, String priority) {
        this.dueDateId = UUID.randomUUID().toString();
        this.courseId = courseId;
        this.courseName = courseName;
        this.assignmentName = assignmentName;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
        this.priority = priority;
        this.moduleId = ""; // Default empty module ID
        this.moduleName = "General"; // Default module name
    }
    
    /**
     * Gets the unique ID of this due date.
     * 
     * @return Due date ID
     */
    public String getDueDateId() {
        return dueDateId;
    }
    
    /**
     * Gets the ID of the associated course.
     * 
     * @return Course ID
     */
    public String getCourseId() {
        return courseId;
    }
    
    /**
     * Sets the ID of the associated course.
     * 
     * @param courseId New course ID
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    /**
     * Gets the name of the associated course.
     * 
     * @return Course name
     */
    public String getCourseName() {
        return courseName;
    }
    
    /**
     * Sets the name of the associated course.
     * 
     * @param courseName New course name
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    /**
     * Gets the ID of the associated module.
     * 
     * @return Module ID
     */
    public String getModuleId() {
        return moduleId;
    }
    
    /**
     * Sets the ID of the associated module.
     * 
     * @param moduleId New module ID
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
    
    /**
     * Gets the name of the associated module.
     * 
     * @return Module name
     */
    public String getModuleName() {
        return moduleName;
    }
    
    /**
     * Sets the name of the associated module.
     * 
     * @param moduleName New module name
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    /**
     * Gets the name of the assignment.
     * 
     * @return Assignment name
     */
    public String getAssignmentName() {
        return assignmentName;
    }
    
    /**
     * Sets the name of the assignment.
     * 
     * @param assignmentName New assignment name
     */
    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }
    
    /**
     * Gets the description of the assignment.
     * 
     * @return Assignment description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the assignment.
     * 
     * @param description New assignment description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the due date of the assignment.
     * 
     * @return Due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    /**
     * Gets the due date as a formatted string.
     * 
     * @return Formatted due date string
     */
    public String getDueDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return dueDate.format(formatter);
    }
    
    /**
     * Sets the due date of the assignment.
     * 
     * @param dueDate New due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    /**
     * Checks if the assignment is completed.
     * 
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }
    
    /**
     * Sets the completed status of the assignment.
     * 
     * @param completed New completed status
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    /**
     * Gets the priority level of the assignment.
     * 
     * @return Priority level
     */
    public String getPriority() {
        return priority;
    }
    
    /**
     * Sets the priority level of the assignment.
     * 
     * @param priority New priority level
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    /**
     * Calculates the days remaining until the due date.
     * 
     * @return Number of days remaining (negative if past due)
     */
    public long getDaysRemaining() {
        return LocalDate.now().until(dueDate).getDays();
    }
    
    /**
     * Determines the status of the due date (Upcoming, Due Soon, Overdue, Completed).
     * 
     * @return Status string
     */
    public String getStatus() {
        if (completed) {
            return "Completed";
        }
        
        long daysRemaining = getDaysRemaining();
        
        if (daysRemaining < 0) {
            return "Overdue";
        } else if (daysRemaining <= 3) {
            return "Due Soon";
        } else {
            return "Upcoming";
        }
    }
    
    /**
     * Gets the appropriate color for the due date based on its status.
     * 
     * @return CSS color string
     */
    public String getStatusColor() {
        String status = getStatus();
        
        switch (status) {
            case "Completed":
                return "#28a745"; // Green
            case "Overdue":
                return "#dc3545"; // Red
            case "Due Soon":
                return "#ffc107"; // Yellow
            case "Upcoming":
                return "#17a2b8"; // Blue
            default:
                return "#6c757d"; // Gray
        }
    }
    
    @Override
    public int compareTo(DueDate other) {
        // Sort by due date (ascending)
        return this.dueDate.compareTo(other.dueDate);
    }
    
    @Override
    public String toString() {
        String moduleInfo = moduleName != null && !moduleName.isEmpty() ? " [" + moduleName + "]" : "";
        return getStatus() + ": " + assignmentName + moduleInfo + " (" + courseName + ") - " + getDueDateFormatted();
    }
}