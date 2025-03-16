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
    private DueDateManager dueDateManager;
    
    private StudentSignupController studentSignupController;
    private CourseController courseController;
    private GradeController gradeController;
    private DueDateController dueDateController;

    @Override
    public void start(Stage stage) {
        // Initialize managers
        courseManager = new CourseManager();
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        dueDateManager = new DueDateManager();
        
        // Initialize controllers
        courseController = new CourseController(courseManager);
        studentSignupController = new StudentSignupController(studentManager);
        gradeController = new GradeController(gradeManager, courseManager);
        dueDateController = new DueDateController(dueDateManager, courseManager);
        
        // Connect controllers to each other
        studentSignupController.setCourseController(courseController);
        studentSignupController.setGradeController(gradeController);
        studentSignupController.setDueDateController(dueDateController);
        
        gradeController.setCourseController(courseController);
        
        dueDateController.setCourseController(courseController);
        dueDateController.setStudentSignupController(studentSignupController);
        dueDateController.setGradeManager(gradeManager);
        
        // Create tab pane for different views
        TabPane tabPane = new TabPane();
        
        // Create tabs
        Tab signupTab = new Tab("Student Sign Up");
        Tab courseTab = new Tab("Courses");
        Tab gradesTab = new Tab("Grades");
        Tab dueDatesTab = new Tab("Due Dates");
        
        // Prevent tabs from being closed
        signupTab.setClosable(false);
        courseTab.setClosable(false);
        gradesTab.setClosable(false);
        dueDatesTab.setClosable(false);
        
        // Add content to each tab
        signupTab.setContent(studentSignupController.createSignupView());
        courseTab.setContent(courseController.createCourseView());
        gradesTab.setContent(gradeController.createGradesView());
        dueDatesTab.setContent(dueDateController.createDueDatesView());
        
        // Add tabs to tab pane
        tabPane.getTabs().addAll(signupTab, courseTab, gradesTab, dueDatesTab);
        
        // Create scene and show
        Scene scene = new Scene(tabPane, 900, 700);
        stage.setTitle("Grade Tracker BETA");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}