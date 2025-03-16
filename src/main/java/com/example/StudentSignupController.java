package com.example;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for the student sign-up view.
 */
public class StudentSignupController {
    private StudentManager studentManager;
    private GradeController gradeController;
    private CourseController courseController;
    private DueDateController dueDateController;
    
    // Student fields
    private TextField studentIdField, firstNameField, lastNameField, emailField;
    private ComboBox<Integer> yearLevelComboBox;
    private VBox profileBox;
    
    // Reference to the current student
    private Student currentStudent = null;
    
    /**
     * Constructs a new StudentSignupController.
     * 
     * @param studentManager The student manager
     */
    public StudentSignupController(StudentManager studentManager) {
        this.studentManager = studentManager;
    }
    
    /**
     * Sets the reference to the grade controller.
     * 
     * @param gradeController The grade controller
     */
    public void setGradeController(GradeController gradeController) {
        this.gradeController = gradeController;
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
     * Sets the reference to the due date controller.
     * 
     * @param dueDateController The due date controller
     */
    public void setDueDateController(DueDateController dueDateController) {
        this.dueDateController = dueDateController;
    }
    
    /**
     * Gets the current student.
     * 
     * @return The current student
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }
    
    /**
     * Creates the student sign-up view.
     * 
     * @return BorderPane containing the sign-up form
     */
    public BorderPane createSignupView() {
        BorderPane signupPane = new BorderPane();
        
        // Title
        Label titleLabel = new Label("Student Sign Up");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setPadding(new Insets(20));
        
        // Create a grid for the form
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));
        formGrid.setAlignment(Pos.CENTER);
        
        // Create form fields
        Label studentIdLabel = new Label("Student ID (7 digits):");
        studentIdField = new TextField();
        studentIdField.setPromptText("1234567");
        
        Label firstNameLabel = new Label("First Name:");
        firstNameField = new TextField();
        
        Label lastNameLabel = new Label("Last Name:");
        lastNameField = new TextField();
        
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("student@example.com");
        
        Label yearLevelLabel = new Label("Year Level:");
        yearLevelComboBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        
        // Create sign-up button
        Button signupButton = new Button("Sign Up");
        signupButton.setOnAction(e -> signupStudent());
        
        // Add components to the grid
        formGrid.add(studentIdLabel, 0, 0);
        formGrid.add(studentIdField, 1, 0);
        
        formGrid.add(firstNameLabel, 0, 1);
        formGrid.add(firstNameField, 1, 1);
        
        formGrid.add(lastNameLabel, 0, 2);
        formGrid.add(lastNameField, 1, 2);
        
        formGrid.add(emailLabel, 0, 3);
        formGrid.add(emailField, 1, 3);
        
        formGrid.add(yearLevelLabel, 0, 4);
        formGrid.add(yearLevelComboBox, 1, 4);
        
        formGrid.add(signupButton, 1, 5);
        
        // Current profile section (shown after sign-up)
        profileBox = new VBox(10);
        profileBox.setPadding(new Insets(20));
        profileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label profileTitle = new Label("Current Profile");
        profileTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label profileNameLabel = new Label("Name: Not signed in");
        Label profileIdLabel = new Label("Student ID: N/A");
        Label profileEmailLabel = new Label("Email: N/A");
        Label profileYearLabel = new Label("Year Level: N/A");
        
        profileBox.getChildren().addAll(
            profileTitle, 
            profileNameLabel, 
            profileIdLabel, 
            profileEmailLabel,
            profileYearLabel
        );
        
        // Assemble the layout
        signupPane.setTop(titleLabel);
        signupPane.setCenter(formGrid);
        signupPane.setRight(profileBox);
        
        return signupPane;
    }
    
    /**
     * Handles student sign-up.
     */
    private void signupStudent() {
        try {
            String studentIdText = studentIdField.getText().trim();
            if (!studentIdText.matches("[0-9]{7}")) {
                UIHelper.showAlert("Invalid student ID", "Please enter a 7-digit student ID.");
                return;
            }
            
            int studentId = Integer.parseInt(studentIdText);
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            Integer yearLevel = yearLevelComboBox.getValue();
            
            if (firstName.isEmpty() || lastName.isEmpty()) {
                UIHelper.showAlert("Missing information", "Please enter both first and last name.");
                return;
            }
            
            // Create new student with original constructor
            Student newStudent = new Student(studentId, 0, firstName, lastName);
            
            // Set additional fields if provided
            if (!email.isEmpty()) {
                newStudent.setEmail(email);
            }
            
            if (yearLevel != null) {
                newStudent.setYearLevel(yearLevel);
            }
            
            // Add student to manager
            if (studentManager.addStudent(newStudent)) {
                currentStudent = newStudent;
                
                // Show success message
                UIHelper.showAlert("Success", "Student account created successfully!");
                
                // Update profile display
                updateProfileDisplay(newStudent);
                
                // Enable course enrollment in the course controller
                courseController.setCurrentStudent(currentStudent);
                courseController.enableEnrollment();
                
                // Update the grades view
                gradeController.setCurrentStudent(currentStudent);
                gradeController.refreshGradesView();
                
                // Update the due dates view
                if (dueDateController != null) {
                    dueDateController.setCurrentStudent(currentStudent);
                    dueDateController.refreshDueDatesView();
                }
                
                // Switch to courses tab
                TabPane tabPane = (TabPane) profileBox.getScene().getRoot();
                tabPane.getSelectionModel().select(1);
                
                // Clear form fields
                clearFormFields();
                
            } else {
                UIHelper.showAlert("Error", "A student with this ID already exists.");
            }
        } catch (NumberFormatException e) {
            UIHelper.showAlert("Error", "Invalid student ID format.");
        } catch (IllegalArgumentException e) {
            UIHelper.showAlert("Error", e.getMessage());
        } catch (Exception e) {
            UIHelper.showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Updates the profile display with student information.
     * 
     * @param student The student to display
     */
    private void updateProfileDisplay(Student student) {
        Label nameLabel = (Label) profileBox.getChildren().get(1);
        nameLabel.setText("Name: " + student.getFullName());
        
        Label idLabel = (Label) profileBox.getChildren().get(2);
        idLabel.setText("Student ID: " + student.getStudentId());
        
        Label emailLabel = (Label) profileBox.getChildren().get(3);
        emailLabel.setText("Email: " + (student.getEmail() != null ? student.getEmail() : "Not provided"));
        
        Label yearLabel = (Label) profileBox.getChildren().get(4);
        yearLabel.setText("Year Level: " + (student.getYearLevel() > 0 ? student.getYearLevel() : "Not provided"));
    }
    
    /**
     * Clears the form fields.
     */
    private void clearFormFields() {
        studentIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        yearLevelComboBox.setValue(null);
    }
}