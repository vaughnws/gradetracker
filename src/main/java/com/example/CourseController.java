package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

/**
 * Controller for the course management view.
 */
public class CourseController {
    private CourseManager courseManager;
    private ListView<Course> courseListView;
    private ObservableList<Course> courseObservableList;
    
    private TextField nameField, codeField, instructorField;
    private Button enrollButton;
    
    // Reference to the current student
    private Student currentStudent = null;
    
    /**
     * Constructs a new CourseController.
     * 
     * @param courseManager The course manager
     */
    public CourseController(CourseManager courseManager) {
        this.courseManager = courseManager;
        this.courseObservableList = FXCollections.observableArrayList();
        
        // Add sample courses
        if (courseManager.getCourseCount() == 0) {
            courseManager.addCourse(new Course("Introduction to Programming", "CS101", 3, "Dr. Smith", "Spring 2025"));
            courseManager.addCourse(new Course("Data Structures", "CS201", 4, "Dr. Johnson", "Spring 2025"));
        }
        
        this.courseObservableList.addAll(courseManager.getAllCourses());
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
     * Gets the ObservableList of courses.
     * 
     * @return ObservableList of courses
     */
    public ObservableList<Course> getCourseObservableList() {
        return courseObservableList;
    }
    
    /**
     * Creates the course view.
     * 
     * @return BorderPane containing the course management interface
     */
    public BorderPane createCourseView() {
        BorderPane coursePane = new BorderPane();
        
        // Title
        Label titleLabel = new Label("Course Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setPadding(new Insets(10));
        
        // Course list
        courseListView = new ListView<>(courseObservableList);
        
        // Form for adding courses
        Label nameLabel = new Label("Course Name:");
        nameField = new TextField();
        
        Label codeLabel = new Label("Course Code:");
        codeField = new TextField();

        Label instructorLabel = new Label("Instructor Name:");
        instructorField = new TextField();
        
        Button addButton = new Button("Add Course");
        addButton.setOnAction(e -> addCourse());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshCourseView());
        
        enrollButton = new Button("Enroll in Selected Course");
        enrollButton.setOnAction(e -> enrollInCourse());
        enrollButton.setDisable(true); // Initially disabled until user signs up
        
        // Layout for form
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        
        formGrid.add(codeLabel, 0, 1);
        formGrid.add(codeField, 1, 1);
        
        formGrid.add(instructorLabel, 0, 2);
        formGrid.add(instructorField, 1, 2);
        
        HBox buttonBox = new HBox(10, addButton, enrollButton, refreshButton);
        formGrid.add(buttonBox, 1, 3);
        
        // Assemble layout
        coursePane.setTop(titleLabel);
        coursePane.setCenter(courseListView);
        coursePane.setBottom(formGrid);
        
        return coursePane;
    }
    
    /**
     * Refreshes the course view.
     */
    public void refreshCourseView() {
        courseObservableList.clear();
        courseObservableList.addAll(courseManager.getAllCourses());
    }
    
    /**
     * Enables the enrollment button when a student is signed in.
     */
    public void enableEnrollment() {
        if (enrollButton != null) {
            enrollButton.setDisable(false);
        }
    }
    
    /**
     * Handles adding a new course.
     */
    private void addCourse() {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String instructor = instructorField.getText().trim();
        
        if (!name.isEmpty() && !code.isEmpty()) {
            Course newCourse = new Course(name, code, 3, instructor, "Spring 2025");
            courseManager.addCourse(newCourse);
            refreshCourseView(); // Use the refresh method
            nameField.clear();
            codeField.clear();
            instructorField.clear();
        } else {
            UIHelper.showAlert("Missing Information", "Please provide both course name and code.");
        }
    }
    
    /**
     * Enrolls the current student in the selected course.
     */
    private void enrollInCourse() {
        if (currentStudent == null) {
            UIHelper.showAlert("Not Signed In", "Please sign up before enrolling in courses.");
            return;
        }
        
        Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            UIHelper.showAlert("No Selection", "Please select a course to enroll in.");
            return;
        }
        
        String studentIdStr = String.valueOf(currentStudent.getStudentId());
        
        // Enroll the student in the course
        if (selectedCourse.enrollStudent(studentIdStr)) {
            // Also update the student's enrolled courses
            currentStudent.enrollInCourse(selectedCourse.getId());
            UIHelper.showAlert("Success", "Enrolled in course: " + selectedCourse.getName());
        } else {
            UIHelper.showAlert("Already Enrolled", "You are already enrolled in this course.");
        }
    }
}