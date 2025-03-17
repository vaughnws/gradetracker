package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.util.List;

/**
 * Grade tracker main interface kyle was also here
 */
public class App extends Application {

    private CourseManager courseManager;
    private StudentManager studentManager;
    private GradeManager gradeManager;
    private DueDateManager dueDateManager;
    private ModuleManager moduleManager; // Added module manager
    
    private StudentSignupController studentSignupController;
    private CourseController courseController;
    private GradeController gradeController;
    private DueDateController dueDateController;

    @Override
    public void start(Stage stage) {
        // Initialize managers - these should be shared across controllers
        courseManager = new CourseManager();
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        dueDateManager = new DueDateManager();
        moduleManager = new ModuleManager(); // Initialize the module manager
        
        // Add sample data to module manager if needed
        initializeSampleModules();
        
        // Initialize controllers with managers
        courseController = new CourseController(courseManager);
        studentSignupController = new StudentSignupController(studentManager);
        gradeController = new GradeController(gradeManager, courseManager);
        dueDateController = new DueDateController(dueDateManager, courseManager, moduleManager); // Pass module manager
        
        // Connect controllers to each other - critical for proper data flow
        connectControllers();
        
        // Create tabs
        TabPane tabPane = createTabPane();
        
        // Create scene and show
        Scene scene = new Scene(tabPane, 1000, 750); // Increased window size
        stage.setTitle("Grade Tracker BETA");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Initializes sample modules for courses if needed.
     */
    private void initializeSampleModules() {
        // For each course, make sure it has some modules
        for (Course course : courseManager.getAllCourses()) {
            moduleManager.initializeModulesForCourse(course.getId());
            
            // If no modules exist yet, add some sample ones
            List<CourseModule> modules = moduleManager.getModulesForCourse(course.getId());
            if (modules.size() <= 1) { // If only the general module exists
                moduleManager.createModuleForCourse(course.getId(), "Assignments", "Regular assignments for the course");
                moduleManager.createModuleForCourse(course.getId(), "Projects", "Course projects");
                moduleManager.createModuleForCourse(course.getId(), "Quizzes", "Regular quizzes");
                moduleManager.createModuleForCourse(course.getId(), "Exams", "Major exams");
            }
        }
    }
    
    /**
     * Connects all controllers to each other.
     */
    private void connectControllers() {
        // Connect StudentSignupController
        studentSignupController.setCourseController(courseController);
        studentSignupController.setGradeController(gradeController);
        studentSignupController.setDueDateController(dueDateController);
        
        // Connect CourseController
        // (Add any additional connections needed)
        
        // Connect GradeController with other components
        gradeController.setCourseController(courseController);
        gradeController.setDueDateManager(dueDateManager);
        gradeController.setModuleManager(moduleManager); // Important! Share the same module manager
        
        // Connect DueDateController with other components
        dueDateController.setCourseController(courseController);
        dueDateController.setStudentSignupController(studentSignupController);
        dueDateController.setGradeManager(gradeManager);
        dueDateController.setGradeController(gradeController); // NEW: Direct connection to grade controller
        
        // Set the module manager in the dialog helper
        DueDateDialogHelper dialogHelper = dueDateController.getDialogHelper();
        if (dialogHelper != null) {
            dialogHelper.setModuleManager(moduleManager);
            dialogHelper.setGradeController(gradeController); // NEW: Direct access to grade controller
        }
    }
    
    /**
     * Creates the tab pane with all tabs.
     * 
     * @return The configured tab pane
     */
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        
        // Set up tab selection change listener to update views
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Grades")) {
                    gradeController.refreshGradesView();
                } else if (newTab.getText().equals("Due Dates")) {
                    dueDateController.refreshDueDatesView();
                } else if (newTab.getText().equals("Courses")) {
                    courseController.refreshCourseView();
                }
            }
        });
        
        // Create and configure tabs
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
        
        return tabPane;
    }

    public static void main(String[] args) {
        launch();
    }
}