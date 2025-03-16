package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Grade tracker main interface
 */

public class App extends Application {

    private CourseManager courseManager;
    private StudentManager studentManager;
    private GradeManager gradeManager;
    
    private ListView<Course> courseListView;
    private ObservableList<Course> courseObservableList;
    
    private TextField nameField, codeField, instructorField;
    
    // Student fields
    private TextField studentIdField, firstNameField, lastNameField, emailField, majorField;
    private ComboBox<Integer> yearLevelComboBox;
    
    // Current logged-in student
    private Student currentStudent = null;

    @Override
    public void start(Stage stage) {
        // Initialize managers and data
        courseManager = new CourseManager();
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        
        courseObservableList = FXCollections.observableArrayList();
        
        // Add sample courses
        courseManager.addCourse(new Course("Introduction to Programming", "CS101", 3, "Dr. Smith", "Spring 2025"));
        courseManager.addCourse(new Course("Data Structures", "CS201", 4, "Dr. Johnson", "Spring 2025"));
        courseObservableList.addAll(courseManager.getAllCourses());
        
        // Create tab pane for different views
        TabPane tabPane = new TabPane();
        
        // Create tabs
        Tab signupTab = new Tab("Student Sign Up");
        Tab courseTab = new Tab("Courses");
        Tab gradesTab = new Tab("Grades");
        
        // Prevent tabs from being closed
        signupTab.setClosable(false);
        courseTab.setClosable(false);
        gradesTab.setClosable(false);
        
        // Add content to each tab
        signupTab.setContent(createSignupView());
        courseTab.setContent(createCourseView());
        gradesTab.setContent(createGradesView());
        
        // Add tabs to tab pane
        tabPane.getTabs().addAll(signupTab, courseTab, gradesTab);
        
        // Create scene and show
        Scene scene = new Scene(tabPane, 800, 700);
        stage.setTitle("Grade Tracker BETA");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Creates the student sign-up view.
     * 
     * @return BorderPane containing the sign-up form
     */
    private BorderPane createSignupView() {
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
        
        Label majorLabel = new Label("Major:");
        majorField = new TextField();
        
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
        
        formGrid.add(majorLabel, 0, 4);
        formGrid.add(majorField, 1, 4);
        
        formGrid.add(yearLevelLabel, 0, 5);
        formGrid.add(yearLevelComboBox, 1, 5);
        
        formGrid.add(signupButton, 1, 6);
        
        // Current profile section (shown after sign-up)
        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(20));
        profileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label profileTitle = new Label("Current Profile");
        profileTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Label profileNameLabel = new Label("Name: Not signed in");
        Label profileIdLabel = new Label("Student ID: N/A");
        Label profileEmailLabel = new Label("Email: N/A");
        Label profileMajorLabel = new Label("Major: N/A");
        Label profileYearLabel = new Label("Year Level: N/A");
        
        profileBox.getChildren().addAll(
            profileTitle, 
            profileNameLabel, 
            profileIdLabel, 
            profileEmailLabel,
            profileMajorLabel,
            profileYearLabel
        );
        
        // Assemble the layout
        signupPane.setTop(titleLabel);
        signupPane.setCenter(formGrid);
        signupPane.setRight(profileBox);
        
        return signupPane;
    }
    
    /**
     * Creates the course view.
     * 
     * @return BorderPane containing the course management interface
     */
    private BorderPane createCourseView() {
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
        
        Button enrollButton = new Button("Enroll in Selected Course");
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
        
        HBox buttonBox = new HBox(10, addButton, enrollButton);
        formGrid.add(buttonBox, 1, 3);
        
        // Assemble layout
        coursePane.setTop(titleLabel);
        coursePane.setCenter(courseListView);
        coursePane.setBottom(formGrid);
        
        return coursePane;
    }
    
    /**
     * Creates the grades view.
     * 
     * @return BorderPane containing the grades management interface
     */
    private BorderPane createGradesView() {
        BorderPane gradesPane = new BorderPane();
        
        // Title
        Label titleLabel = new Label("Grades Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setPadding(new Insets(10));
        
        // Create "Please sign in" view for when no student is logged in
        VBox notLoggedInBox = new VBox(10);
        notLoggedInBox.setAlignment(Pos.CENTER);
        Label signInLabel = new Label("Please sign up to view and manage grades");
        signInLabel.setFont(Font.font("Arial", 16));
        notLoggedInBox.getChildren().add(signInLabel);
        
        // Create the grades management UI
        VBox gradesBox = new VBox(15);
        gradesBox.setPadding(new Insets(10));
        
        // Course selection
        Label selectCourseLabel = new Label("Select Course:");
        ComboBox<Course> courseComboBox = new ComboBox<>();
        
        // Grade entry fields
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
        
        Button addGradeButton = new Button("Add Grade");
        addGradeButton.setOnAction(e -> {
            if (currentStudent == null) {
                showAlert("Not Signed In", "Please sign up first.");
                return;
            }
            
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse == null) {
                showAlert("No Course Selected", "Please select a course.");
                return;
            }
            
            try {
                String assignmentName = assignmentField.getText().trim();
                double score = Double.parseDouble(scoreField.getText().trim());
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                String date = dateField.getText().trim();
                
                if (assignmentName.isEmpty() || date.isEmpty()) {
                    showAlert("Missing Information", "Please fill in all fields.");
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
                showAlert("Success", "Grade added successfully!");
                
                // Clear fields
                assignmentField.clear();
                scoreField.clear();
                maxScoreField.clear();
                weightField.clear();
                dateField.clear();
                
                // Refresh the grades display
                // (In a complete implementation, you would update a grades list or table here)
                
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numbers for score, max score, and weight.");
            }
        });
        
        // Grade summary section
        Label summaryLabel = new Label("Grade Summary");
        summaryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Create a ListView to display grades
        ListView<Grades> gradesListView = new ListView<>();
        
        // Layout for grade entry form
        GridPane gradeFormGrid = new GridPane();
        gradeFormGrid.setHgap(10);
        gradeFormGrid.setVgap(10);
        
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
        
        gradeFormGrid.add(addGradeButton, 1, 6);
        
        // Add components to the main grades box
        gradesBox.getChildren().addAll(
            gradeFormGrid,
            new HBox(10, summaryLabel),
            gradesListView
        );
        
        // Initially show "Please sign in" message
        gradesPane.setTop(titleLabel);
        gradesPane.setCenter(notLoggedInBox);
        
        // Set up a method to switch to grades view when a student signs in
        // (In a complete implementation, you would call this method after successful sign-in)
        
        return gradesPane;
    }
    
    /**
     * Handles student sign-up.
     */
    private void signupStudent() {
        try {
            String studentIdText = studentIdField.getText().trim();
            if (!studentIdText.matches("[0-9]{7}")) {
                showAlert("Invalid student ID", "Please enter a 7-digit student ID.");
                return;
            }
            
            int studentId = Integer.parseInt(studentIdText);
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String major = majorField.getText().trim();
            Integer yearLevel = yearLevelComboBox.getValue();
            
            if (firstName.isEmpty() || lastName.isEmpty()) {
                showAlert("Missing information", "Please enter both first and last name.");
                return;
            }
            
            // Create new student with original constructor
            Student newStudent = new Student(studentId, 0, firstName, lastName);
            
            // Set additional fields if provided
            if (!email.isEmpty()) {
                newStudent.setEmail(email);
            }
            
            if (!major.isEmpty()) {
                newStudent.setMajor(major);
            }
            
            if (yearLevel != null) {
                newStudent.setYearLevel(yearLevel);
            }
            
            // Add student to manager
            if (studentManager.addStudent(newStudent)) {
                currentStudent = newStudent;
                
                // Show success message
                showAlert("Success", "Student account created successfully!");
                
                // Update profile display
                BorderPane signupPane = (BorderPane) ((TabPane) ((Scene) firstNameField.getScene()).getRoot()).getTabs().get(0).getContent();
                VBox profileBox = (VBox) signupPane.getRight();
                
                // Update profile labels
                Label nameLabel = (Label) profileBox.getChildren().get(1);
                nameLabel.setText("Name: " + newStudent.getFullName());
                
                Label idLabel = (Label) profileBox.getChildren().get(2);
                idLabel.setText("Student ID: " + newStudent.getStudentId());
                
                Label emailLabel = (Label) profileBox.getChildren().get(3);
                emailLabel.setText("Email: " + (newStudent.getEmail() != null ? newStudent.getEmail() : "Not provided"));
                
                Label majorLabel = (Label) profileBox.getChildren().get(4);
                majorLabel.setText("Major: " + (newStudent.getMajor() != null ? newStudent.getMajor() : "Not provided"));
                
                Label yearLabel = (Label) profileBox.getChildren().get(5);
                yearLabel.setText("Year Level: " + (newStudent.getYearLevel() > 0 ? newStudent.getYearLevel() : "Not provided"));
                
                // Switch to courses tab
                TabPane tabPane = (TabPane) ((Scene) firstNameField.getScene()).getRoot();
                tabPane.getSelectionModel().select(1);
                
                // Enable course enrollment button
                BorderPane coursePane = (BorderPane) tabPane.getTabs().get(1).getContent();
                GridPane formGrid = (GridPane) coursePane.getBottom();
                HBox buttonBox = (HBox) formGrid.getChildren().get(formGrid.getChildren().size() - 1);
                Button enrollButton = (Button) buttonBox.getChildren().get(1);
                enrollButton.setDisable(false);
                
                // Update the course combobox in the grades view
                BorderPane gradesPane = (BorderPane) tabPane.getTabs().get(2).getContent();
                
                // Switch from "Please sign in" to the actual grades management UI
                VBox gradesBox = new VBox(15);
                gradesBox.setPadding(new Insets(10));
                
                // Replace "Please sign in" message with grades management UI
                gradesPane.setCenter(gradesBox);
                
                // Clear form fields
                studentIdField.clear();
                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                majorField.clear();
                yearLevelComboBox.setValue(null);
                
            } else {
                showAlert("Error", "A student with this ID already exists.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid student ID format.");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
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
            courseObservableList.clear();
            courseObservableList.addAll(courseManager.getAllCourses());
            nameField.clear();
            codeField.clear();
            instructorField.clear();
        } else {
            showAlert("Missing Information", "Please provide both course name and code.");
        }
    }
    
    /**
     * Enrolls the current student in the selected course.
     */
    private void enrollInCourse() {
        if (currentStudent == null) {
            showAlert("Not Signed In", "Please sign up before enrolling in courses.");
            return;
        }
        
        Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("No Selection", "Please select a course to enroll in.");
            return;
        }
        
        String studentIdStr = String.valueOf(currentStudent.getStudentId());
        
        // Enroll the student in the course
        if (selectedCourse.enrollStudent(studentIdStr)) {
            // Also update the student's enrolled courses
            currentStudent.enrollInCourse(selectedCourse.getId());
            showAlert("Success", "Enrolled in course: " + selectedCourse.getName());
        } else {
            showAlert("Already Enrolled", "You are already enrolled in this course.");
        }
    }
    
    /**
     * Shows an alert dialog with the specified title and message.
     * 
     * @param title Alert title
     * @param message Alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

}