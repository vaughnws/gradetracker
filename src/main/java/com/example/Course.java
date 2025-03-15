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

}