package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the collection of grades in the application.
 * Provides methods to add, retrieve, update, and delete grades.
 */
public class GradeManager {
    
    private final List<Grades> allGrades;
    
    /**
     * Creates a new GradeManager with an empty collection of grades.
     */
    public GradeManager() {
        this.allGrades = new ArrayList<>();
    }
    
    /**
     * Adds a new grade to the collection.
     * 
     * @param grade Grade to add
     */
    public void addGrade(Grades grade) {
        allGrades.add(grade);
    }
    
    /**
     * Retrieves a grade by its unique ID.
     * 
     * @param gradeId ID of the grade to retrieve
     * @return Grade with the specified ID, or null if not found
     */
    public Grades getGradeById(String gradeId) {
        for (Grades grade : allGrades) {
            if (grade.getGradeId().equals(gradeId)) {
                return grade;
            }
        }
        return null;
    }
    
    /**
     * Updates an existing grade.
     * 
     * @param gradeId ID of the grade to update
     * @param updatedGrade Updated grade data
     * @return true if the grade was updated, false if the grade was not found
     */
    public boolean updateGrade(String gradeId, Grades updatedGrade) {
        for (int i = 0; i < allGrades.size(); i++) {
            if (allGrades.get(i).getGradeId().equals(gradeId)) {
                allGrades.set(i, updatedGrade);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes a grade from the collection.
     * 
     * @param gradeId ID of the grade to remove
     * @return true if the grade was removed, false if the grade was not found
     */
    public boolean removeGrade(String gradeId) {
        for (int i = 0; i < allGrades.size(); i++) {
            if (allGrades.get(i).getGradeId().equals(gradeId)) {
                allGrades.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets all grades for a specific student.
     * 
     * @param studentId ID of the student
     * @return List of grades for the specified student
     */
    public List<Grades> getGradesForStudent(String studentId) {
        List<Grades> result = new ArrayList<>();
        
        for (Grades grade : allGrades) {
            if (grade.getStudentId().equals(studentId)) {
                result.add(grade);
            }
        }
        
        return result;
    }
    
    /**
     * Gets all grades for a specific course.
     * 
     * @param courseId ID of the course
     * @return List of grades for the specified course
     */
    public List<Grades> getGradesForCourse(String courseId) {
        List<Grades> result = new ArrayList<>();
        
        for (Grades grade : allGrades) {
            if (grade.getCourseId().equals(courseId)) {
                result.add(grade);
            }
        }
        
        return result;
    }
    
    /**
     * Gets all grades for a specific student in a specific course.
     * 
     * @param studentId ID of the student (can be int or String)
     * @param courseId ID of the course
     * @return List of grades for the specified student in the specified course
     */
    public List<Grades> getGradesForStudentInCourse(Object studentId, String courseId) {
        String studentIdStr = String.valueOf(studentId);
        List<Grades> result = new ArrayList<>();
        
        for (Grades grade : allGrades) {
            if (grade.getStudentId().equals(studentIdStr) && grade.getCourseId().equals(courseId)) {
                result.add(grade);
            }
        }
        
        return result;
    }
    
    /**
     * Calculates the weighted average grade for a student in a course.
     * 
     * @param studentId ID of the student
     * @param courseId ID of the course
     * @return Weighted average grade as a percentage, or -1 if no grades are found
     */
    public double calculateCourseAverage(String studentId, String courseId) {
        List<Grades> courseGrades = getGradesForStudentInCourse(studentId, courseId);
        
        if (courseGrades.isEmpty()) {
            return -1;
        }
        
        double totalWeightedScore = 0;
        double totalWeight = 0;
        
        for (Grades grade : courseGrades) {
            double weightedScore = (grade.getScore() / grade.getMaxScore()) * grade.getWeight();
            totalWeightedScore += weightedScore;
            totalWeight += grade.getWeight();
        }
        
        if (totalWeight == 0) {
            return -1;
        }
        
        return (totalWeightedScore / totalWeight) * 100;
    }
    
    /**
     * Gets the current GPA for a student across all courses.
     * 
     * @param studentId ID of the student
     * @return Current GPA on a 4.0 scale, or -1 if no grades are found
     */
    public double calculateGPA(String studentId) {
        List<Grades> studentGrades = getGradesForStudent(studentId);
        
        if (studentGrades.isEmpty()) {
            return -1;
        }
        
        // Group grades by course
        Map<String, List<Grades>> courseGrades = new HashMap<>();
        
        for (Grades grade : studentGrades) {
            String courseId = grade.getCourseId();
            if (!courseGrades.containsKey(courseId)) {
                courseGrades.put(courseId, new ArrayList<>());
            }
            courseGrades.get(courseId).add(grade);
        }
        
        double totalGradePoints = 0;
        int totalCredits = 0;
        
        for (Map.Entry<String, List<Grades>> entry : courseGrades.entrySet()) {
            String courseId = entry.getKey();
            
            // Calculate course average
            double courseAverage = calculateCourseAverage(studentId, courseId);
            
            // Find the course to get the credits
            Course course = null; // This needs to be retrieved from a CourseManager
            
            // For now, assume a fixed credit value
            int credits = 3;
            
            // Convert percentage to GPA scale
            double gradePoints = 0;
            
            if (courseAverage >= 90) {
                gradePoints = 4.0;
            } else if (courseAverage >= 80) {
                gradePoints = 3.0;
            } else if (courseAverage >= 70) {
                gradePoints = 2.0;
            } else if (courseAverage >= 60) {
                gradePoints = 1.0;
            }
            
            totalGradePoints += gradePoints * credits;
            totalCredits += credits;
        }
        
        if (totalCredits == 0) {
            return -1;
        }
        
        return totalGradePoints / totalCredits;
    }
}