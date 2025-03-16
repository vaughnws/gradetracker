package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

/**
 * Controller for the grades management view.
 */
public class GradeController {
    private GradeManager gradeManager;
    private CourseManager courseManager;
    private CourseController courseController;
    private BorderPane gradesPane;
    
    // UI elements
    private VBox notLoggedInBox;
    private VBox gradesBox;
    private ComboBox<Course> courseComboBox;
    private ListView<Grades> gradesListView;
    private ObservableList<Grades> gradesObservableList;
    
    // Reference to the current student
    private Student currentStudent = null;
    
    /**
     * Constructs a new GradeController.
     * 
     * @param gradeManager The grade manager
     * @param courseManager The course manager
     */
    public GradeController(GradeManager gradeManager, CourseManager courseManager) {
        this.gradeManager = gradeManager;
        this.courseManager = courseManager;
        this.gradesObservableList = FXCollections.observableArrayList();
    }
    
    /**
     * Sets the reference to the course controller.
     * 
     * @param courseController The course controller
     */
    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }
    
    /**
     * Sets the current student.
     * 
     * @param student The current student
     */
    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }
    
    /**
     * Creates the grades view.
     * 
     * @return BorderPane containing the grades management interface
     */
    public BorderPane createGradesView() {
        gradesPane = new BorderPane();
        
        // Title
        Label titleLabel = new Label("Grades Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setPadding(new Insets(10));
        
        // Create "Please sign in" view for when no student is logged in
        notLoggedInBox = new VBox(10);
        notLoggedInBox.setAlignment(Pos.CENTER);
        Label signInLabel = new Label("Please sign up to view and manage grades");
        signInLabel.setFont(Font.font("Arial", 16));
        notLoggedInBox.getChildren().add(signInLabel);
        
        // Create the grades management UI
        gradesBox = new VBox(15);
        gradesBox.setPadding(new Insets(10));
        
        // Course selection
        Label selectCourseLabel = new Label("Select Course:");
        courseComboBox = new ComboBox<>();
        
        // Initialize with available courses if courseController is set
        if (courseController != null) {
            courseComboBox.setItems(courseController.getCourseObservableList());
        }
        
        // Grade entry fields
        GridPane gradeFormGrid = new GridPane();
        gradeFormGrid.setHgap(10);
        gradeFormGrid.setVgap(10);
        
        Label assignmentLabel = new Label("Assignment Name:");
        TextField assignmentField = new TextField();
        
        Label scoreLabel = new Label("Score:");
        TextField scoreField = new TextField();
        
        Label maxScoreLabel = new Label("Max Score:");
        TextField maxScoreField = new TextField();
        
        Label weightLabel = new Label("Weight (%):");
        TextField weightField = new TextField();
        
        Label dateLabel = new Label("Date Submitted:");
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");
        
        // Add components to the grid
        gradeFormGrid.add(selectCourseLabel, 0, 0);
        gradeFormGrid.add(courseComboBox, 1, 0);
        
        gradeFormGrid.add(assignmentLabel, 0, 1);
        gradeFormGrid.add(assignmentField, 1, 1);
        
        gradeFormGrid.add(scoreLabel, 0, 2);
        gradeFormGrid.add(scoreField, 1, 2);
        
        gradeFormGrid.add(maxScoreLabel, 0, 3);
        gradeFormGrid.add(maxScoreField, 1, 3);
        
        gradeFormGrid.add(weightLabel, 0, 4);
        gradeFormGrid.add(weightField, 1, 4);
        
        gradeFormGrid.add(dateLabel, 0, 5);
        gradeFormGrid.add(dateField, 1, 5);
        
        // Add Grade button
        Button addGradeButton = new Button("Add Grade");
        gradeFormGrid.add(addGradeButton, 1, 6);
        
        // Create a ListView to display grades
        gradesListView = new ListView<>(gradesObservableList);
        
        // Label for grade summary
        Label gradesListLabel = new Label("Your Grades:");
        gradesListLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Update the grades list when a course is selected
        courseComboBox.setOnAction(e -> {
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse != null && currentStudent != null) {
                // Clear the current grades list
                gradesObservableList.clear();
                
                // Get grades for the student in the selected course
                List<Grades> courseGrades = gradeManager.getGradesForStudentInCourse(
                    currentStudent.getStudentId(), 
                    selectedCourse.getId()
                );
                
                // Add grades to the observable list
                gradesObservableList.addAll(courseGrades);
                
                // If no grades, add a message
                if (gradesObservableList.isEmpty()) {
                    Label noGradesLabel = new Label("No grades found for this course.");
                    gradesListView.setPlaceholder(noGradesLabel);
                }
            }
        });
        
        // Handle add grade button action
        addGradeButton.setOnAction(e -> {
            if (currentStudent == null) {
                UIHelper.showAlert("Not Signed In", "Please sign up first.");
                return;
            }
            
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse == null) {
                UIHelper.showAlert("No Course Selected", "Please select a course.");
                return;
            }
            
            try {
                String assignmentName = assignmentField.getText().trim();
                double score = Double.parseDouble(scoreField.getText().trim());
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                String date = dateField.getText().trim();
                
                if (assignmentName.isEmpty() || date.isEmpty()) {
                    UIHelper.showAlert("Missing Information", "Please fill in all fields.");
                    return;
                }
                
                Grades newGrade = new Grades(
                    String.valueOf(currentStudent.getStudentId()),
                    selectedCourse.getId(),
                    assignmentName,
                    score,
                    maxScore,
                    weight,
                    date
                );
                
                gradeManager.addGrade(newGrade);
                
                // Add the new grade to the list
                gradesObservableList.add(newGrade);
                
                // Show success message
                UIHelper.showAlert("Success", "Grade added successfully!");
                
                // Clear fields
                assignmentField.clear();
                scoreField.clear();
                maxScoreField.clear();
                weightField.clear();
                dateField.clear();
                
            } catch (NumberFormatException ex) {
                UIHelper.showAlert("Invalid Input", "Please enter valid numbers for score, max score, and weight.");
            }
        });
        
        // Add components to the grades box
        gradesBox.getChildren().addAll(
            gradeFormGrid,
            new HBox(10, gradesListLabel),
            gradesListView
        );
        
        // Initially show "Please sign in" message if no student is logged in
        if (currentStudent == null) {
            gradesPane.setCenter(notLoggedInBox);
        } else {
            gradesPane.setCenter(gradesBox);
        }
        
        gradesPane.setTop(titleLabel);
        
        return gradesPane;
    }
    
    /**
     * Refreshes the grades view after a student signs in.
     */
    public void refreshGradesView() {
        if (gradesPane == null) return;
        
        // Update course combo box items if courses have changed
        if (courseController != null) {
            courseComboBox.setItems(courseController.getCourseObservableList());
        }
        
        // Switch view based on logged-in status
        if (currentStudent == null) {
            gradesPane.setCenter(notLoggedInBox);
        } else {
            gradesPane.setCenter(gradesBox);
        }
    }
}