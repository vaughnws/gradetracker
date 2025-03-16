package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the collection of courses in the application.
 * Provides methods to add, retrieve, update, and delete courses.
 * Uses ArrayList.
 */
public class CourseManager {

    private final List<Course> courses;
    
    /**
     * Creates a new CourseManager with an empty collection of courses.
     */
    public CourseManager() {
        this.courses = new ArrayList<>();
    }

    // Add a course
    /**
     * Adds a new course to the collection.
     * 
     * @param course Course to add
     */
    public void addCourse(Course course) {
        courses.add(course);
    }

    // Retrieve course by ID
    /**
     * Retrieves a course by its unique ID.
     * 
     * @param id ID of the course to retrieve
     * @return Course with the specified ID, or null if not found
     */
    public Course getCourseById(String courseid) {
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (course.getId().equals(courseid)) {
                return course;
            }
        }
        return null;
    }

    //list courses
    /**
     * Gets a list of all courses.
     * 
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses); // Return a copy to prevent external modification
    }

    //update course
    /**
     * Updates an existing course.
     * 
     * @param courseId The ID of the course to update
     * @param updatedCourse The updated course data
     * @return true if the course was updated, false if the course was not found
     */
    public boolean updateCourse(String courseId, Course updatedCourse) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(courseId)) {
                courses.set(i, updatedCourse);
                return true;
            }
        }
        return false;
    }

        /**
     * Removes a course from the collection.
     * 
     * @param courseId The ID of the course to remove
     * @return true if the course was removed, false if the course was not found
     */
    public boolean removeCourse(String courseId) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(courseId)) {
                courses.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the number of courses in the collection.
     * 
     * @return The number of courses
     */
    public int getCourseCount() {
        return courses.size();
    }
    
    /**
     * Finds courses by name or code (case-insensitive partial match).
     * 
     * @param searchTerm The search term to match against course names or codes
     * @return List of courses that match the search term
     */
    public List<Course> findCourses(String searchTerm) {
        String term = searchTerm.toLowerCase();
        List<Course> result = new ArrayList<>();
        
        for (Course course : courses) {
            if (course.getName().toLowerCase().contains(term) || 
                course.getCode().toLowerCase().contains(term)) {
                result.add(course);
            }
        }
        
        return result;
    }
    
    /**
     * Gets all courses for a specific semester.
     * 
     * @param semester The semester to filter by
     * @return List of courses offered in the specified semester
     */
    public List<Course> getCoursesBySemester(String semester) {
        List<Course> result = new ArrayList<>();
        
        for (Course course : courses) {
            if (course.getSemester().equalsIgnoreCase(semester)) {
                result.add(course);
            }
        }
        
        return result;
    }
    
    /**
     * Gets all courses taught by a specific instructor.
     * 
     * @param instructor The instructor to filter by
     * @return List of courses taught by the specified instructor
     */
    public List<Course> getCoursesByInstructor(String instructor) {
        List<Course> result = new ArrayList<>();
        
        for (Course course : courses) {
            if (course.getInstructor().equalsIgnoreCase(instructor)) {
                result.add(course);
            }
        }
        
        return result;
    }
    
    /**
     * Gets all courses in which you are enrolled.
     * 
     * @param studentId The ID of the student
     * @return List of courses the student is enrolled in
     */
    public List<Course> getCoursesForStudent(String studentId) {
        List<Course> result = new ArrayList<>();
        
        for (Course course : courses) {
            if (course.isStudentEnrolled(studentId)) {
                result.add(course);
            }
        }
        
        return result;
    }

}