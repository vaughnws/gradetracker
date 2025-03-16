package com.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.collections.*;

/**
 * Manages the collection of due dates in the application.
 * Provides methods to add, retrieve, update, and delete due dates.
 */
public class DueDateManager {
    
    private final List<DueDate> dueDates;
    
    /**
     * Creates a new DueDateManager with an empty collection of due dates.
     */
    public DueDateManager() {
        this.dueDates = new ArrayList<>();
    }
    
    /**
     * Adds a new due date to the collection.
     * 
     * @param dueDate Due date to add
     */
    public void addDueDate(DueDate dueDate) {
        dueDates.add(dueDate);
    }
    
    /**
     * Retrieves a due date by its unique ID.
     * 
     * @param dueDateId ID of the due date to retrieve
     * @return Due date with the specified ID, or null if not found
     */
    public DueDate getDueDateById(String dueDateId) {
        for (DueDate dueDate : dueDates) {
            if (dueDate.getDueDateId().equals(dueDateId)) {
                return dueDate;
            }
        }
        return null;
    }
    
    /**
     * Gets a list of all due dates, sorted by date.
     * 
     * @return List of all due dates
     */
    public List<DueDate> getAllDueDates() {
        List<DueDate> sortedDueDates = new ArrayList<>(dueDates);
        Collections.sort(sortedDueDates);
        return sortedDueDates;
    }
    
    /**
     * Updates an existing due date.
     * 
     * @param dueDateId ID of the due date to update
     * @param updatedDueDate Updated due date data
     * @return true if the due date was updated, false if the due date was not found
     */
    public boolean updateDueDate(String dueDateId, DueDate updatedDueDate) {
        for (int i = 0; i < dueDates.size(); i++) {
            if (dueDates.get(i).getDueDateId().equals(dueDateId)) {
                dueDates.set(i, updatedDueDate);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes a due date from the collection.
     * 
     * @param dueDateId ID of the due date to remove
     * @return true if the due date was removed, false if the due date was not found
     */
    public boolean removeDueDate(String dueDateId) {
        for (int i = 0; i < dueDates.size(); i++) {
            if (dueDates.get(i).getDueDateId().equals(dueDateId)) {
                dueDates.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the number of due dates in the collection.
     * 
     * @return The number of due dates
     */
    public int getDueDateCount() {
        return dueDates.size();
    }
    
    /**
     * Gets all due dates for a specific course.
     * 
     * @param courseId ID of the course
     * @return List of due dates for the specified course
     */
    public List<DueDate> getDueDatesForCourse(String courseId) {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (dueDate.getCourseId().equals(courseId)) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all due dates for a specific module.
     * 
     * @param moduleId ID of the module
     * @return List of due dates for the specified module
     */
    public List<DueDate> getDueDatesForModule(String moduleId) {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (dueDate.getModuleId().equals(moduleId)) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all due dates for a specific date range.
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of due dates within the specified date range
     */
    public List<DueDate> getDueDatesInRange(LocalDate startDate, LocalDate endDate) {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            LocalDate date = dueDate.getDueDate();
            if ((date.isEqual(startDate) || date.isAfter(startDate)) && 
                (date.isEqual(endDate) || date.isBefore(endDate))) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all due dates for a specific student across all courses.
     * 
     * @param studentId The ID of the student
     * @param courseManager The course manager to check enrollment
     * @return List of due dates for the courses the student is enrolled in
     */
    public List<DueDate> getDueDatesForStudent(int studentId, CourseManager courseManager) {
        List<DueDate> result = new ArrayList<>();
        String studentIdStr = String.valueOf(studentId);
        
        for (Course course : courseManager.getAllCourses()) {
            if (course.isStudentEnrolled(studentIdStr)) {
                result.addAll(getDueDatesForCourse(course.getId()));
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all due dates for a specific student in a specific module.
     * 
     * @param studentId The ID of the student
     * @param moduleId The ID of the module
     * @param courseManager The course manager to check enrollment
     * @return List of due dates for the specified module the student is enrolled in
     */
    public List<DueDate> getDueDatesForStudentInModule(int studentId, String moduleId, CourseManager courseManager) {
        List<DueDate> result = new ArrayList<>();
        String studentIdStr = String.valueOf(studentId);
        
        // Get all due dates for the specified module
        List<DueDate> moduleDueDates = getDueDatesForModule(moduleId);
        
        // Filter for only courses the student is enrolled in
        for (DueDate dueDate : moduleDueDates) {
            Course course = courseManager.getCourseById(dueDate.getCourseId());
            if (course != null && course.isStudentEnrolled(studentIdStr)) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all due dates with a specific status.
     * 
     * @param status The status to filter by ("Upcoming", "Due Soon", "Overdue", "Completed")
     * @return List of due dates with the specified status
     */
    public List<DueDate> getDueDatesByStatus(String status) {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (dueDate.getStatus().equals(status)) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all due dates with a specific priority.
     * 
     * @param priority The priority to filter by ("High", "Medium", "Low")
     * @return List of due dates with the specified priority
     */
    public List<DueDate> getDueDatesByPriority(String priority) {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (dueDate.getPriority().equals(priority)) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all upcoming due dates (not completed and not overdue).
     * 
     * @return List of upcoming due dates
     */
    public List<DueDate> getUpcomingDueDates() {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (!dueDate.isCompleted() && dueDate.getDaysRemaining() >= 0) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all overdue due dates (not completed and past due).
     * 
     * @return List of overdue due dates
     */
    public List<DueDate> getOverdueDueDates() {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (!dueDate.isCompleted() && dueDate.getDaysRemaining() < 0) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * Gets all completed due dates.
     * 
     * @return List of completed due dates
     */
    public List<DueDate> getCompletedDueDates() {
        List<DueDate> result = new ArrayList<>();
        
        for (DueDate dueDate : dueDates) {
            if (dueDate.isCompleted()) {
                result.add(dueDate);
            }
        }
        
        Collections.sort(result);
        return result;
    }
}