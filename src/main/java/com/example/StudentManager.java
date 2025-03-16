package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of students in the application.
 * Provides methods to add, retrieve, update, and delete students.
 */
public class StudentManager {
    
    private final List<Student> students;
    
    /**
     * Creates a new StudentManager with an empty collection of students.
     */
    public StudentManager() {
        this.students = new ArrayList<>();
    }
    
    /**
     * Adds a new student to the collection.
     * 
     * @param student Student to add
     * @return true if the student was added, false if a student with the same ID already exists
     */
    public boolean addStudent(Student student) {
        // Check if a student with the same ID already exists
        for (Student existingStudent : students) {
            if (existingStudent.getStudentId() == student.getStudentId()) {
                return false;
            }
        }
        
        students.add(student);
        return true;
    }
    
    /**
     * Retrieves a student by their ID.
     * 
     * @param studentId ID of the student to retrieve
     * @return Student with the specified ID, or null if not found
     */
    public Student getStudentById(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }
    
    /**
     * Gets a list of all students.
     * 
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students); // Return a copy to prevent external modification
    }
    
    /**
     * Updates an existing student.
     * 
     * @param studentId ID of the student to update
     * @param updatedStudent Updated student data
     * @return true if the student was updated, false if the student was not found
     */
    public boolean updateStudent(int studentId, Student updatedStudent) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                students.set(i, updatedStudent);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes a student from the collection.
     * 
     * @param studentId ID of the student to remove
     * @return true if the student was removed, false if the student was not found
     */
    public boolean removeStudent(int studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                students.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the number of students in the collection.
     * 
     * @return The number of students
     */
    public int getStudentCount() {
        return students.size();
    }
    
    /**
     * Finds students by name (case-insensitive partial match).
     * 
     * @param searchName The search term to match against student names
     * @return List of students that match the search term
     */
    public List<Student> findStudentsByName(String searchName) {
        String term = searchName.toLowerCase();
        List<Student> result = new ArrayList<>();
        
        for (Student student : students) {
            if (student.getFirstName().toLowerCase().contains(term) || 
                student.getLastName().toLowerCase().contains(term) ||
                student.getFullName().toLowerCase().contains(term)) {
                result.add(student);
            }
        }
        
        return result;
    }
}