package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.util.List;

/**
 * Grade tracker main interface
 */
public class App extends Application {

    private CourseManager courseManager;
    private StudentManager studentManager;
    private GradeManager gradeManager;
    
    private StudentSignupController studentSignupController;
    private CourseController courseController;
    private GradeController gradeController;

    @Override
    public void start(Stage stage) {
        // Initialize managers
        courseManager = new CourseManager();
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        
        // Initialize controllers
        courseController = new CourseController(courseManager);
        studentSignupController = new StudentSignupController(studentManager);
        gradeController = new GradeController(gradeManager, courseManager);
        
        // Connect controllers to each other
        studentSignupController.setCourseController(courseController);
        studentSignupController.setGradeController(gradeController);
        gradeController.setCourseController(courseController);
        
        // Create tab pane for different views
        TabPane tabPane = new TabPane();
        
        // Create tabs
        Tab signupTab = new Tab("Sign In");
        Tab courseTab = new Tab("Courses");
        Tab gradesTab = new Tab("Grades");
        
        // Prevent tabs from being closed
        signupTab.setClosable(false);
        courseTab.setClosable(false);
        gradesTab.setClosable(false);
        
        // Add content to each tab
        signupTab.setContent(studentSignupController.createSignupView());
        courseTab.setContent(courseController.createCourseView());
        gradesTab.setContent(gradeController.createGradesView());
        
        // Add tabs to tab pane
        tabPane.getTabs().addAll(signupTab, courseTab, gradesTab);
        
        // Create scene and show
        Scene scene = new Scene(tabPane, 800, 700);
        stage.setTitle("Grade Tracker BETA");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}