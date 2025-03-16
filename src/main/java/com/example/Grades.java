package com.example;

import java.util.UUID;
import java.util.List;

/**
 * Represents a grade entry in the grade tracking application.
 * Each grade is associated with a student, course, and module.
 */
public class Grades {
    private final String gradeId;
    private String studentId;
    private String courseId;
    private String assignmentName;
    private String moduleId; // Added field for module
    private String moduleName; // Added module name for display
    private double score;
    private double maxScore;
    private double weight;
    private String letterGrade;
    private String comments;
    private String dateSubmitted;

    /**
     * Constructs a new Grade entry with the given details.
     * 
     * @param studentId ID of the student who received this grade
     * @param courseId ID of the course this grade belongs to
     * @param assignmentName Name of the assignment
     * @param moduleId ID of the module this assignment belongs to
     * @param moduleName Name of the module this assignment belongs to
     * @param score Numeric score received
     * @param maxScore Maximum possible score
     * @param weight Weight of this assignment in the overall course grade
     * @param dateSubmitted Date when the assignment was submitted
     */
    public Grades(String studentId, String courseId, String assignmentName, String moduleId, String moduleName,
                 double score, double maxScore, double weight, String dateSubmitted) {
        this.gradeId = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.assignmentName = assignmentName;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.score = score;
        this.maxScore = maxScore;
        this.weight = weight;
        this.dateSubmitted = dateSubmitted;
        this.comments = "";
        this.calculateLetterGrade();
    }

    /**
     * Constructs a new Grade entry with the given details (without module for backward compatibility).
     * 
     * @param studentId ID of the student who received this grade
     * @param courseId ID of the course this grade belongs to
     * @param assignmentName Name of the assignment
     * @param score Numeric score received
     * @param maxScore Maximum possible score
     * @param weight Weight of this assignment in the overall course grade
     * @param dateSubmitted Date when the assignment was submitted
     */
    public Grades(String studentId, String courseId, String assignmentName, 
                 double score, double maxScore, double weight, String dateSubmitted) {
        this(studentId, courseId, assignmentName, "", "General", score, maxScore, weight, dateSubmitted);
    }

    /**
     * Calculate the letter grade based on the percentage score.
     */
    private void calculateLetterGrade() {
        double percentage = (score / maxScore) * 100;
        
        if (percentage >= 90) {
            this.letterGrade = "A";
        } else if (percentage >= 80) {
            this.letterGrade = "B";
        } else if (percentage >= 70) {
            this.letterGrade = "C";
        } else if (percentage >= 60) {
            this.letterGrade = "D";
        } else {
            this.letterGrade = "F";
        }
    }

    // GETTERS AND SETTERS

    /**
     * Returns the unique ID of this grade entry.
     * 
     * @return Grade ID
     */
    public String getGradeId() {
        return gradeId;
    }

    /**
     * Returns the ID of the student who received this grade.
     * 
     * @return Student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     * 
     * @param studentId New student ID
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * Returns the ID of the course this grade belongs to.
     * 
     * @return Course ID
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID.
     * 
     * @param courseId New course ID
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * Returns the name of the assignment.
     * 
     * @return Assignment name
     */
    public String getAssignmentName() {
        return assignmentName;
    }

    /**
     * Sets the assignment name.
     * 
     * @param assignmentName New assignment name
     */
    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    /**
     * Returns the ID of the module this grade belongs to.
     * 
     * @return Module ID
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * Sets the module ID.
     * 
     * @param moduleId New module ID
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * Returns the name of the module this grade belongs to.
     * 
     * @return Module name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Sets the module name.
     * 
     * @param moduleName New module name
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Returns the numeric score received.
     * 
     * @return Score
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the numeric score and recalculates the letter grade.
     * 
     * @param score New score
     */
    public void setScore(double score) {
        this.score = score;
        calculateLetterGrade();
    }

    /**
     * Returns the maximum possible score.
     * 
     * @return Maximum score
     */
    public double getMaxScore() {
        return maxScore;
    }

    /**
     * Sets the maximum possible score and recalculates the letter grade.
     * 
     * @param maxScore New maximum score
     */
    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
        calculateLetterGrade();
    }

    /**
     * Returns the weight of this assignment in the overall course grade.
     * 
     * @return Weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of this assignment.
     * 
     * @param weight New weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the letter grade.
     * 
     * @return Letter grade
     */
    public String getLetterGrade() {
        return letterGrade;
    }

    /**
     * Returns the comments on this grade entry.
     * 
     * @return Comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments on this grade entry.
     * 
     * @param comments New comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Returns the date when the assignment was submitted.
     * 
     * @return Date submitted
     */
    public String getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * Sets the date when the assignment was submitted.
     * 
     * @param dateSubmitted New date submitted
     */
    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * Returns the percentage score.
     * 
     * @return Percentage score
     */
    public double getPercentage() {
        return (score / maxScore) * 100;
    }

    @Override
    public String toString() {
        return assignmentName + " (" + moduleName + "): " + score + "/" + maxScore + " (" + letterGrade + ", " + String.format("%.1f", getPercentage()) + "%)";
    }
}