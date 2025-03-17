package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

/**
 * A component for displaying the program average across all courses.
 */
public class ProgramAverageDisplay extends VBox {
    private Label averageLabel;
    private Label gpaLabel;
    private Circle indicatorCircle;
    
    /**
     * Creates a new program average display.
     */
    public ProgramAverageDisplay() {
        // Configure the container
        setSpacing(5);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #b0c4de; " +
                "-fx-border-radius: 8; -fx-background-radius: 8;");
        setMinWidth(150);
        setPrefWidth(150);
        
        // Create title
        Label titleLabel = new Label("Program Average");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Create indicator circle
        indicatorCircle = new Circle(15);
        indicatorCircle.setFill(Color.GRAY);
        
        // Create average label
        averageLabel = new Label("N/A");
        averageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Create GPA label
        gpaLabel = new Label("GPA: N/A");
        gpaLabel.setFont(Font.font("Arial", 12));
        
        // Add all components
        getChildren().addAll(titleLabel, indicatorCircle, averageLabel, gpaLabel);
    }
    
    /**
     * Updates the display with the given student's grades.
     * 
     * @param gradeManager The grade manager
     * @param studentId The student ID
     */
    public void update(GradeManager gradeManager, String studentId) {
        if (gradeManager == null || studentId == null || studentId.isEmpty()) {
            reset();
            return;
        }
        
        // Get all grades for the student
        List<Grades> allGrades = gradeManager.getGradesForStudent(studentId);
        
        if (allGrades.isEmpty()) {
            reset();
            return;
        }
        
        // Calculate the weighted average
        double totalWeightedProportion = 0;
        double totalWeight = 0;
        double totalGradePoints = 0;
        
        for (Grades grade : allGrades) {
            // Calculate weighted proportion
            double proportion = grade.getScore() / grade.getMaxScore();
            totalWeightedProportion += proportion * grade.getWeight();
            totalWeight += grade.getWeight();
            
            // For GPA calculation
            double gradePercent = proportion * 100;
            double gradePoints = 0;
            if (gradePercent >= 90) gradePoints = 4.0;
            else if (gradePercent >= 80) gradePoints = 3.0;
            else if (gradePercent >= 70) gradePoints = 2.0;
            else if (gradePercent >= 60) gradePoints = 1.0;
            
            totalGradePoints += gradePoints * grade.getWeight();
        }
        
        if (totalWeight > 0) {
            // Calculate program average
            double programAverage = (totalWeightedProportion / totalWeight) * 100;
            double gpa = totalGradePoints / totalWeight;
            
            // Update labels
            averageLabel.setText(String.format("%.1f%%", programAverage));
            gpaLabel.setText(String.format("GPA: %.2f", gpa));
            
            // Update circle color based on grade
            Color indicatorColor;
            if (programAverage >= 90) {
                indicatorColor = Color.web("#28a745"); // Green - A
            } else if (programAverage >= 80) {
                indicatorColor = Color.web("#17a2b8"); // Blue - B
            } else if (programAverage >= 70) {
                indicatorColor = Color.web("#ffc107"); // Yellow - C
            } else if (programAverage >= 60) {
                indicatorColor = Color.web("#fd7e14"); // Orange - D
            } else {
                indicatorColor = Color.web("#dc3545"); // Red - F
            }
            
            indicatorCircle.setFill(indicatorColor);
        } else {
            reset();
        }
    }
    
    /**
     * Resets the display to its default state.
     */
    private void reset() {
        averageLabel.setText("N/A");
        gpaLabel.setText("GPA: N/A");
        indicatorCircle.setFill(Color.GRAY);
    }
}