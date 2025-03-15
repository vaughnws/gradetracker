package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a course in the grade tracking application.
 * A course has a unique ID, name, code, credits, and a list of enrolled students.
 */
public class Course {
    private final String id; // Unique identifier for the course
    private String name; // Course name (e.g., "Introduction to Computer Science")
    private String code; // Course code (e.g., "CS101")
    private int credits; // Number of credit hours
    private String instructor; // Name of the course instructor
    private String semester; // Semester (e.g., "Fall 2025")
    
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
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.instructor = instructor;
        this.semester = semester;
    }

//SETTERS AND GETTERS


// course id
    /**
     * Returns the unique ID of the course.
     * 
     * @return Course ID
     */
    public String getId() {
        return id;
    }
//course name
    /**
     * Returns the course name.
     * 
     * @return Course name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the course name.
     * 
     * @param name New course name
     */
    public void setName(String name) {
        this.name = name;
    }

    //course code
    /**
     * Returns the course code.
     * 
     * @return Course code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the course code.
     * 
     * @param code New course code
     */
    public void setCode(String code) {
        this.code = code;
    }

    //credit hours
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

    //course instructor
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

}