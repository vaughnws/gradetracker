package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the grades management view.
 */
public class GradeController {
    private GradeManager gradeManager;
    private CourseManager courseManager;
    private ModuleManager moduleManager;
    private DueDateManager dueDateManager;
    private CourseController courseController;
    private BorderPane gradesPane;
    private GradeViewHelper viewHelper;
    
    // UI elements
    private VBox notLoggedInBox;
    private ScrollPane mainScrollPane;
    private VBox gradesBox;
    private ComboBox<Course> courseComboBox;
    private Map<String, ListView<Grades>> moduleListViews;
    private Map<String, Label> moduleAverageLabels;
    private VBox moduleContentBox;
    private ProgressBar courseAverageProgressBar;
    private Label courseAverageLabel;
    private CheckBox showModulesCheckBox;
    private PieChart gradesPieChart;
    
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
        this.moduleManager = new ModuleManager();
        this.moduleListViews = new HashMap<>();
        this.moduleAverageLabels = new HashMap<>();
        this.viewHelper = new GradeViewHelper(this);
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
     * Sets the due date manager.
     * 
     * @param dueDateManager The due date manager
     */
    public void setDueDateManager(DueDateManager dueDateManager) {
        this.dueDateManager = dueDateManager;
    }
    
    /**
     * Gets the module manager.
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    /**
     * Gets the grade manager.
     */
    public GradeManager getGradeManager() {
        return gradeManager;
    }
    
    /**
     * Gets the course manager.
     */
    public CourseManager getCourseManager() {
        return courseManager;
    }
    
    /**
     * Gets the due date manager.
     */
    public DueDateManager getDueDateManager() {
        return dueDateManager;
    }
    
    /**
     * Gets the current student.
     */
    public Student getCurrentStudent() {
        return currentStudent;
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
        
        // Make the entire view scrollable
        mainScrollPane = new ScrollPane();
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setContent(gradesBox);
        
        // Course selection and average display
        HBox courseSelectionBox = new HBox(15);
        courseSelectionBox.setAlignment(Pos.CENTER_LEFT);
        courseSelectionBox.setPadding(new Insets(10));
        
        VBox courseSelectionVbox = new VBox(10);
        HBox courseSelectionUI = createCourseSelectionUI();
        HBox courseAverageDisplay = createCourseAverageDisplay();
        
        courseSelectionVbox.getChildren().addAll(courseSelectionUI, courseAverageDisplay);
        courseSelectionBox.getChildren().add(courseSelectionVbox);
        
        // Grade entry form section
        TitledPane gradeEntryPane = viewHelper.createGradeEntryForm(courseComboBox);
        
        // Add refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            refreshGradesView();
            refreshModuleViews(courseComboBox.getValue());
            updateCourseAverageDisplay(courseComboBox.getValue());
            updateGradeDistributionChart(courseComboBox.getValue());
        });
        
        // Module view option
        HBox optionsBox = new HBox(15);
        optionsBox.setAlignment(Pos.CENTER_LEFT);
        optionsBox.setPadding(new Insets(5));
        
        showModulesCheckBox = new CheckBox("Show Modules");
        showModulesCheckBox.setSelected(true);
        showModulesCheckBox.setOnAction(e -> refreshModuleViews(courseComboBox.getValue()));
        
        Button addModuleButton = new Button("Add Module");
        addModuleButton.setOnAction(e -> {
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse != null) {
                viewHelper.showAddModuleDialog(selectedCourse);
            } else {
                UIHelper.showAlert("No Course Selected", "Please select a course to add a module.");
            }
        });
        
        optionsBox.getChildren().addAll(showModulesCheckBox, addModuleButton, refreshButton);
        
        // Create user-friendly grade dashboard with pie chart
        VBox dashboardBox = createGradeDashboard();
        
        // Create modules content area
        moduleContentBox = new VBox(15);
        moduleContentBox.setPadding(new Insets(10));
        
        // Add components to the grades box
        gradesBox.getChildren().addAll(
            courseSelectionBox,
            dashboardBox,
            gradeEntryPane,
            optionsBox,
            new Separator(),
            moduleContentBox
        );
        
        // Initially show "Please sign in" message if no student is logged in
        if (currentStudent == null) {
            gradesPane.setCenter(notLoggedInBox);
        } else {
            gradesPane.setCenter(mainScrollPane);
        }
        
        gradesPane.setTop(titleLabel);
        
        return gradesPane;
    }
    
/**
 * Creates a user-friendly grade dashboard with a pie chart.
 * 
 * @return VBox containing the grade dashboard
 */
private VBox createGradeDashboard() {
    VBox dashboardBox = new VBox(15);
    dashboardBox.setPadding(new Insets(10));
    dashboardBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-radius: 5;");
    dashboardBox.setAlignment(Pos.CENTER);
    
    Label dashboardTitle = new Label("Grade Dashboard");
    dashboardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    
    // Add pie chart (it will be populated when a course is selected)
    gradesPieChart = new PieChart();
    gradesPieChart.setTitle("Grade Distribution");
    gradesPieChart.setLabelsVisible(true);
    gradesPieChart.setPrefHeight(250);
    gradesPieChart.setLegendVisible(false); // Remove the legend
    
    // Create a vertical layout with pie chart on top and indicators below
    VBox dashboardContent = new VBox(20);
    dashboardContent.setAlignment(Pos.CENTER);
    
    // Grade indicators section
    HBox gradeIndicatorBox = new HBox(20);
    gradeIndicatorBox.setAlignment(Pos.CENTER);
    gradeIndicatorBox.setPadding(new Insets(10));
    
    // Create grade indicators for A, B, C, D, F
    gradeIndicatorBox.getChildren().addAll(
        createGradeIndicator("A", "#28a745", "90-100%"),
        createGradeIndicator("B", "#17a2b8", "80-89%"),
        createGradeIndicator("C", "#ffc107", "70-79%"),
        createGradeIndicator("D", "#fd7e14", "60-69%"),
        createGradeIndicator("F", "#dc3545", "0-59%")
    );
    
    // Add pie chart and grade indicators to the dashboard content
    dashboardContent.getChildren().addAll(gradesPieChart, gradeIndicatorBox);
    
    dashboardBox.getChildren().addAll(dashboardTitle, dashboardContent);
    
    return dashboardBox;
}
    
/**
 * Updates the pie chart with grade distribution for the selected course.
 * 
 * @param course The selected course
 */
public void updateGradeDistributionChart(Course course) {
    if (course == null || currentStudent == null || gradesPieChart == null) return;
    
    // Get grades for this student in this course
    List<Grades> courseGrades = gradeManager.getGradesForStudentInCourse(
        currentStudent.getStudentId(), 
        course.getId()
    );
    
    // Count grades by letter grade
    int countA = 0;
    int countB = 0;
    int countC = 0;
    int countD = 0;
    int countF = 0;
    
    for (Grades grade : courseGrades) {
        String letterGrade = grade.getLetterGrade();
        switch (letterGrade) {
            case "A":
                countA++;
                break;
            case "B":
                countB++;
                break;
            case "C":
                countC++;
                break;
            case "D":
                countD++;
                break;
            case "F":
                countF++;
                break;
        }
    }
    
    // Create data for the pie chart
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    
    if (countA > 0) pieChartData.add(new PieChart.Data("A", countA));
    if (countB > 0) pieChartData.add(new PieChart.Data("B", countB));
    if (countC > 0) pieChartData.add(new PieChart.Data("C", countC));
    if (countD > 0) pieChartData.add(new PieChart.Data("D", countD));
    if (countF > 0) pieChartData.add(new PieChart.Data("F", countF));
    
    // If no grades, add a placeholder
    if (pieChartData.isEmpty()) {
        pieChartData.add(new PieChart.Data("No Grades", 1));
    }
    
    // Update the pie chart
    gradesPieChart.setData(pieChartData);
    
    // Set colors for the pie chart slices
    int i = 0;
    for (PieChart.Data data : pieChartData) {
        String color;
        if (data.getName().startsWith("A")) {
            color = "#28a745"; // Green
        } else if (data.getName().startsWith("B")) {
            color = "#17a2b8"; // Blue
        } else if (data.getName().startsWith("C")) {
            color = "#ffc107"; // Yellow
        } else if (data.getName().startsWith("D")) {
            color = "#fd7e14"; // Orange
        } else if (data.getName().startsWith("F")) {
            color = "#dc3545"; // Red
        } else {
            color = "#6c757d"; // Gray for "No Grades"
        }
        
        String rgbColor = color;
        Node slice = gradesPieChart.getData().get(i).getNode();
        if (slice != null) {
            slice.setStyle("-fx-pie-color: " + rgbColor + ";");
        }
        i++;
    }
}
    
    /**
     * Creates a grade indicator for the dashboard.
     * 
     * @param grade The letter grade
     * @param color The color for the grade
     * @param range The percentage range
     * @return VBox containing the grade indicator
     */
    private VBox createGradeIndicator(String grade, String color, String range) {
        VBox indicatorBox = new VBox(5);
        indicatorBox.setAlignment(Pos.CENTER);
        indicatorBox.setPrefWidth(80);
        
        Label gradeLabel = new Label(grade);
        gradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gradeLabel.setTextFill(Color.web(color));
        
        Rectangle colorBox = new Rectangle(40, 20);
        colorBox.setFill(Color.web(color));
        colorBox.setArcWidth(5);
        colorBox.setArcHeight(5);
        
        Label rangeLabel = new Label(range);
        rangeLabel.setFont(Font.font("Arial", 12));
        
        indicatorBox.getChildren().addAll(gradeLabel, colorBox, rangeLabel);
        return indicatorBox;
    }
    
    /**
     * Creates the course selection UI.
     * 
     * @return HBox containing the course selection components
     */
    private HBox createCourseSelectionUI() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label selectCourseLabel = new Label("Select Course:");
        courseComboBox = new ComboBox<>();
        
        // Initialize with available courses if courseController is set
        if (courseController != null) {
            courseComboBox.setItems(courseController.getCourseObservableList());
        }
        
        // Update the grades list when a course is selected
        courseComboBox.setOnAction(e -> {
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse != null && currentStudent != null) {
                refreshModuleViews(selectedCourse);
                updateCourseAverageDisplay(selectedCourse);
                updateGradeDistributionChart(selectedCourse);
            }
        });
        
        box.getChildren().addAll(selectCourseLabel, courseComboBox);
        return box;
    }
    
    /**
     * Creates the course average display.
     * 
     * @return HBox containing the course average display
     */
    private HBox createCourseAverageDisplay() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        
        courseAverageLabel = new Label("Course Average: N/A");
        courseAverageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        courseAverageLabel.setTextFill(Color.NAVY);
        
        // Create progress bar for visualizing course average
        courseAverageProgressBar = new ProgressBar(0);
        courseAverageProgressBar.setPrefWidth(200);
        courseAverageProgressBar.setStyle("-fx-accent: linear-gradient(to right, #00c6ff, #0072ff);");
        
        box.getChildren().addAll(courseAverageLabel, courseAverageProgressBar);
        return box;
    }
    
    /**
     * Updates the course average display.
     * 
     * @param course The selected course
     */
    public void updateCourseAverageDisplay(Course course) {
        if (course == null || currentStudent == null) return;
        
        double courseAverage = gradeManager.calculateCourseAverage(
            String.valueOf(currentStudent.getStudentId()),
            course.getId()
        );
        
        if (courseAverage >= 0) {
            courseAverageLabel.setText(String.format("Course Average: %.1f%%", courseAverage));
            courseAverageProgressBar.setProgress(courseAverage / 100.0);
            
            // Set color based on grade
            String color;
            if (courseAverage >= 90) {
                color = "-fx-accent: linear-gradient(to right, #00E676, #00C853);"; // Green
            } else if (courseAverage >= 80) {
                color = "-fx-accent: linear-gradient(to right, #00B0FF, #0091EA);"; // Blue
            } else if (courseAverage >= 70) {
                color = "-fx-accent: linear-gradient(to right, #FFEE58, #FDD835);"; // Yellow
            } else if (courseAverage >= 60) {
                color = "-fx-accent: linear-gradient(to right, #FFA726, #FB8C00);"; // Orange
            } else {
                color = "-fx-accent: linear-gradient(to right, #FF5252, #D50000);"; // Red
            }
            
            courseAverageProgressBar.setStyle(color);
        } else {
            courseAverageLabel.setText("Course Average: N/A");
            courseAverageProgressBar.setProgress(0);
        }
    }
    
    /**
     * Refreshes the module views with the grades for the selected course.
     * 
     * @param selectedCourse The selected course
     */
    public void refreshModuleViews(Course selectedCourse) {
        if (currentStudent == null || selectedCourse == null) return;
        
        // Clear the module views
        moduleListViews.clear();
        moduleAverageLabels.clear();
        moduleContentBox.getChildren().clear();
        
        if (!showModulesCheckBox.isSelected()) {
            // If not showing modules, display all grades in a single list
            moduleContentBox.getChildren().add(viewHelper.createAllGradesView(selectedCourse));
            return;
        }
        
        // Get all modules for this course
        moduleManager.initializeModulesForCourse(selectedCourse.getId());
        List<CourseModule> modules = moduleManager.getModulesForCourse(selectedCourse.getId());
        
        // Get all grades for this student in this course
        List<Grades> courseGrades = gradeManager.getGradesForStudentInCourse(
            currentStudent.getStudentId(), 
            selectedCourse.getId()
        );
        
        // Create a section for each module
        for (CourseModule module : modules) {
            VBox moduleSection = viewHelper.createModuleSection(module, courseGrades, selectedCourse, moduleListViews, moduleAverageLabels);
            moduleContentBox.getChildren().add(moduleSection);
        }
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
            gradesPane.setCenter(mainScrollPane);
            
            // If a course is selected, refresh the module views
            if (courseComboBox.getValue() != null) {
                refreshModuleViews(courseComboBox.getValue());
                updateCourseAverageDisplay(courseComboBox.getValue());
                updateGradeDistributionChart(courseComboBox.getValue());
            }
        }
    }
    
    /**
     * Gets the module content box.
     */
    public VBox getModuleContentBox() {
        return moduleContentBox;
    }
    
    /**
     * Gets the course combo box.
     */
    public ComboBox<Course> getCourseComboBox() {
        return courseComboBox;
    }

        /**
     * Sets the module manager.
     * 
     * @param moduleManager The module manager
     */
        public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }
}