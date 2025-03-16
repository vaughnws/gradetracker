package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a course in the grade tracking application.
 * A course has a unique ID, name, code, credits, and a list of enrolled students.
 */
public class Course {
    private final String courseid; // Unique identifier for the course
    private String courseName; // Course name (e.g., "Introduction to Computer Science")
    private String courseCode; // Course code (e.g., "CS101")
    private int credits; // Number of credit hours
    private String instructor; // Name of the course instructor
    private String semester; // Semester (e.g., "Fall 2025")
    private List<String> enrolledStudentIds; // IDs of students enrolled in this course
    
    /**
     * Constructs a new Course with the given details.
     * 
     * @param name Course name
     * @param code Course code
     * @param credits Number of credit hours
     * @param instructor Name of the course instructor
     * @param semester Current semester
     */
    public Course(String name, String code, int credits, String instructor, String semester) {
        this.courseid = UUID.randomUUID().toString(); // Generate a unique ID
        this.courseName = name;
        this.courseCode = code;
        this.credits = credits;
        this.instructor = instructor;
        this.semester = semester;
        this.enrolledStudentIds = new ArrayList<>();
    }

    // SETTERS AND GETTERS

    // course id
    /**
     * Returns the unique ID of the course.
     * 
     * @return Course ID
     */
    public String getId() {
        return courseid;
    }
    
    // course name
    /**
     * Returns the course name.
     * 
     * @return Course name
     */
    public String getName() {
        return courseName;
    }

    /**
     * Sets the course name.
     * 
     * @param name New course name
     */
    public void setName(String name) {
        this.courseName = name;
    }

    // course code
    /**
     * Returns the course code.
     * 
     * @return Course code
     */
    public String getCode() {
        return courseCode;
    }

    /**
     * Sets the course code.
     * 
     * @param code New course code
     */
    public void setCode(String code) {
        this.courseCode = code;
    }

    // credit hours
    /**
     * Returns the number of credit hours.
     * 
     * @return Credit hours
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Sets the number of credit hours.
     * 
     * @param credits New credit hours
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    // course instructor
    /**
     * Returns the name of the course instructor.
     * 
     * @return Course instructor name
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Sets the name of the course instructor.
     * 
     * @param instructor New course instructor name
     */
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    // semester
    /**
     * Returns the current semester.
     * 
     * @return Current semester
     */
    public String getSemester() {
        return semester;
    }
    
    /**
     * Sets the current semester.
     * 
     * @param semester New current semester
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    /**
     * Add a student to this course.
     * 
     * @param studentId The ID of the student to enroll
     * @return true if the student was successfully enrolled, false if already enrolled
     */
    public boolean enrollStudent(String studentId) {
        if (!enrolledStudentIds.contains(studentId)) {
            enrolledStudentIds.add(studentId);
            return true;
        }
        return false;
    }
    
    /**
     * Remove a student from this course.
     * 
     * @param studentId The ID of the student to remove
     * @return true if the student was successfully removed, false if not enrolled
     */
    public boolean removeStudent(String studentId) {
        return enrolledStudentIds.remove(studentId);
    }
    
    /**
     * Check if a student is enrolled in this course.
     * 
     * @param studentId The ID of the student to check
     * @return true if the student is enrolled, false otherwise
     */
    public boolean isStudentEnrolled(String studentId) {
        return enrolledStudentIds.contains(studentId);
    }
    
    /**
     * Get the list of enrolled student IDs.
     * 
     * @return List of enrolled student IDs
     */
    public List<String> getEnrolledStudentIds() {
        return new ArrayList<>(enrolledStudentIds);
    }
    
    /**
     * Get the number of students enrolled in this course.
     * 
     * @return Number of enrolled students
     */
    public int getEnrollmentCount() {
        return enrolledStudentIds.size();
    }

    // this is a bit long, but needed for displaying the full student info.
    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (Instructor: " + instructor + ", " + semester + ")";
    }
}