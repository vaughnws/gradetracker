
package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Grade tracker main interface
 */

public class App extends Application {

    private CourseManager courseManager;
    private ListView<Course> courseListView;
    private ObservableList<Course> courseObservableList;
    private TextField nameField, codeField, instructorField;

    @Override
    public void start(Stage stage) {
        // Initialize course manager and data
        courseManager = new CourseManager();
        courseObservableList = FXCollections.observableArrayList();
        
        // Add sample courses
        courseManager.addCourse(new Course("Introduction to Programming", "CS101", 3, "Dr. Smith", "Spring 2025"));
        courseManager.addCourse(new Course("Data Structures", "CS201", 4, "Dr. Johnson", "Spring 2025"));
        courseObservableList.addAll(courseManager.getAllCourses());
        
        // Create UI components
        BorderPane root = new BorderPane();
        
        // Title
        Label titleLabel = new Label("Grade Tracker");
        titleLabel.setFont(new Font("Arial", 20));
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
        
        // Layout for form
        HBox nameBox = new HBox(10, nameLabel, nameField);
        HBox codeBox = new HBox(10, codeLabel, codeField);
        HBox instructorBox = new HBox(10, instructorLabel, instructorField);
        
        VBox formBox = new VBox(10, nameBox, codeBox, instructorBox, addButton);
        formBox.setPadding(new Insets(10));
        
        // Assemble layout
        root.setTop(titleLabel);
        root.setCenter(courseListView);
        root.setBottom(formBox);
        
        // Create scene and show
        Scene scene = new Scene(root, 640, 640);
        stage.setTitle("Grade Tracker BETA");
        stage.setScene(scene);
        stage.show();
    }
    
    private void addCourse() {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String instructor = instructorField.getText().trim();
        
        if (!name.isEmpty() && !code.isEmpty()) {
            Course newCourse = new Course(name, code, 3, instructor, "Spring 2025");
            courseManager.addCourse(newCourse);
            courseObservableList.clear();
            courseObservableList.addAll(courseManager.getAllCourses());
            nameField.clear();
            codeField.clear();
            instructorField.clear();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}