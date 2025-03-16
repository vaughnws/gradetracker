package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class for the GradeController that handles UI creation and dialog management.
 */
public class GradeViewHelper {
    private GradeController parentController;
    
    /**
     * Constructs a new GradeViewHelper.
     * 
     * @param parentController The parent GradeController
     */
    public GradeViewHelper(GradeController parentController) {
        this.parentController = parentController;
    }
    
    /**
     * Creates the grade entry form section.
     * 
     * @param courseComboBox The course selection combobox
     * @return TitledPane containing the grade entry form
     */
    public TitledPane createGradeEntryForm(ComboBox<Course> courseComboBox) {
        // Create grid for the form
        GridPane gradeFormGrid = new GridPane();
        gradeFormGrid.setHgap(10);
        gradeFormGrid.setVgap(10);
        gradeFormGrid.setPadding(new Insets(10));
        
        // Create form fields
        Label assignmentLabel = new Label("Assignment Name:");
        TextField assignmentField = new TextField();
        
        Label moduleLabel = new Label("Module:");
        ComboBox<CourseModule> moduleComboBox = new ComboBox<>();
        
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
        gradeFormGrid.add(assignmentLabel, 0, 0);
        gradeFormGrid.add(assignmentField, 1, 0);
        
        gradeFormGrid.add(moduleLabel, 0, 1);
        gradeFormGrid.add(moduleComboBox, 1, 1);
        
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
        
        // Update module combobox when course changes
        courseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                moduleComboBox.getItems().clear();
                
                // Initialize modules for this course if needed
                parentController.getModuleManager().initializeModulesForCourse(newVal.getId());
                
                // Get all modules for the course
                List<CourseModule> modules = parentController.getModuleManager().getModulesForCourse(newVal.getId());
                moduleComboBox.getItems().addAll(modules);
                
                if (!modules.isEmpty()) {
                    moduleComboBox.setValue(modules.get(0));
                }
            }
        });
        
        // Handle add grade button action
        addGradeButton.setOnAction(e -> {
            if (parentController.getCurrentStudent() == null) {
                UIHelper.showAlert("Not Signed In", "Please sign up first.");
                return;
            }
            
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse == null) {
                UIHelper.showAlert("No Course Selected", "Please select a course.");
                return;
            }
            
            CourseModule selectedModule = moduleComboBox.getValue();
            if (selectedModule == null) {
                UIHelper.showAlert("No Module Selected", "Please select a module.");
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
                    String.valueOf(parentController.getCurrentStudent().getStudentId()),
                    selectedCourse.getId(),
                    assignmentName,
                    selectedModule.getModuleId(),
                    selectedModule.getModuleName(),
                    score,
                    maxScore,
                    weight,
                    date
                );
                
                parentController.getGradeManager().addGrade(newGrade);
                
                // Create corresponding due date if it doesn't exist
                if (parentController.getDueDateManager() != null) {
                    boolean dueDateExists = false;
                    for (DueDate dueDate : parentController.getDueDateManager().getAllDueDates()) {
                        if (dueDate.getCourseId().equals(selectedCourse.getId()) && 
                            dueDate.getModuleId().equals(selectedModule.getModuleId()) &&
                            dueDate.getAssignmentName().equals(assignmentName)) {
                            dueDateExists = true;
                            break;
                        }
                    }
                    
                    if (!dueDateExists) {
                        // Parse date string to LocalDate (simple implementation)
                        java.time.LocalDate dueDate;
                        try {
                            dueDate = java.time.LocalDate.parse(date);
                        } catch (Exception ex) {
                            // Use current date if parsing fails
                            dueDate = java.time.LocalDate.now();
                        }
                        
                        DueDate newDueDate = new DueDate(
                            selectedCourse.getId(),
                            selectedCourse.getName(),
                            assignmentName,
                            "Created from grade entry",
                            dueDate,
                            "Medium"
                        );
                        
                        newDueDate.setModuleId(selectedModule.getModuleId());
                        newDueDate.setModuleName(selectedModule.getModuleName());
                        newDueDate.setCompleted(true); // Mark as completed since grade exists
                        
                        parentController.getDueDateManager().addDueDate(newDueDate);
                    }
                }
                
                // Show success message
                UIHelper.showAlert("Success", "Grade added successfully!");
                
                // Clear fields
                assignmentField.clear();
                scoreField.clear();
                maxScoreField.clear();
                weightField.clear();
                dateField.clear();
                
                // Refresh the module views
                parentController.refreshModuleViews(selectedCourse);
                parentController.updateCourseAverageDisplay(selectedCourse);
                parentController.updateGradeDistributionChart(selectedCourse);
                
            } catch (NumberFormatException ex) {
                UIHelper.showAlert("Invalid Input", "Please enter valid numbers for score, max score, and weight.");
            }
        });
        
        // Create titled pane
        TitledPane gradeEntryPane = new TitledPane("Add New Grade", gradeFormGrid);
        gradeEntryPane.setCollapsible(true);
        gradeEntryPane.setExpanded(false);
        
        
        return gradeEntryPane;
    }
    
    /**
     * Creates a view showing all grades for a course without module separation.
     * 
     * @param course The selected course
     * @return VBox containing the grades view
     */
    public VBox createAllGradesView(Course course) {
        VBox container = new VBox(10);
        container.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
        
        Label title = new Label("All Grades for " + course.getName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        ListView<Grades> gradesListView = new ListView<>();
        ObservableList<Grades> gradesObservable = FXCollections.observableArrayList();
        
        // Get all grades for this student in this course
        List<Grades> courseGrades = parentController.getGradeManager().getGradesForStudentInCourse(
            parentController.getCurrentStudent().getStudentId(), 
            course.getId()
        );
        
        gradesObservable.addAll(courseGrades);
        gradesListView.setItems(gradesObservable);
        gradesListView.setPrefHeight(Math.min(gradesObservable.size() * 30 + 30, 300));
        
        // Create context menu for editing grades
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit Grade");
        editItem.setOnAction(e -> {
            Grades selectedGrade = gradesListView.getSelectionModel().getSelectedItem();
            if (selectedGrade != null) {
                showEditGradeDialog(selectedGrade);
                parentController.refreshModuleViews(parentController.getCourseComboBox().getValue());
                parentController.updateCourseAverageDisplay(course);
                parentController.updateGradeDistributionChart(course);
            }
        });
        
        MenuItem deleteItem = new MenuItem("Delete Grade");
        deleteItem.setOnAction(e -> {
            Grades selectedGrade = gradesListView.getSelectionModel().getSelectedItem();
            if (selectedGrade != null) {
                parentController.getGradeManager().removeGrade(selectedGrade.getGradeId());
                parentController.refreshModuleViews(parentController.getCourseComboBox().getValue());
                parentController.updateCourseAverageDisplay(course);
                parentController.updateGradeDistributionChart(course);
            }
        });
        
        contextMenu.getItems().addAll(editItem, deleteItem);
        gradesListView.setContextMenu(contextMenu);
        
        // Double-click to edit
        gradesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Grades selectedGrade = gradesListView.getSelectionModel().getSelectedItem();
                if (selectedGrade != null) {
                    showEditGradeDialog(selectedGrade);
                    parentController.refreshModuleViews(parentController.getCourseComboBox().getValue());
                    parentController.updateCourseAverageDisplay(course);
                    parentController.updateGradeDistributionChart(course);
                }
            }
        });
        
        container.getChildren().addAll(title, gradesListView);
        return container;
    }
    
    /**
     * Creates a section for a module with its grades.
     * 
     * @param module The module
     * @param courseGrades All grades for the course
     * @param course The current course
     * @param moduleListViews Map to store module listviews
     * @param moduleAverageLabels Map to store module average labels
     * @return VBox containing the module section
     */
    public VBox createModuleSection(CourseModule module, List<Grades> courseGrades, Course course, 
                                    Map<String, ListView<Grades>> moduleListViews, 
                                    Map<String, Label> moduleAverageLabels) {
        VBox moduleSection = new VBox(5);
        moduleSection.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
        
        // Module header with title, average, and buttons
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Title and info
        VBox titleBox = new VBox(5);
        
        Label moduleTitle = new Label(module.toString());
        moduleTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label averageLabel = new Label("Module Average: N/A");
        averageLabel.setFont(Font.font("Arial", 12));
        moduleAverageLabels.put(module.getModuleId(), averageLabel);
        
        titleBox.getChildren().addAll(moduleTitle, averageLabel);
        
        // Module actions
        HBox actionsBox = new HBox(5);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> showEditModuleDialog(module, course));
        
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> {
            // Check if module has any grades or due dates
            if (parentController.getModuleManager().moduleHasAssociatedData(
                    module.getModuleId(), 
                    parentController.getGradeManager(), 
                    parentController.getDueDateManager())) {
                UIHelper.showAlert("Cannot Remove", 
                    "This module has grades or due dates associated with it. " +
                    "Remove the grades and due dates first before removing the module.");
                return;
            }
            
            // Confirm removal
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Module Removal");
            confirmAlert.setHeaderText("Remove Module");
            confirmAlert.setContentText("Are you sure you want to remove " + module.getModuleName() + "?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                parentController.getModuleManager().removeModule(module.getModuleId());
                parentController.refreshModuleViews(course);
            }
        });
        
        actionsBox.getChildren().addAll(editButton, removeButton);
        
        // Add title and actions to header
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        headerBox.getChildren().addAll(titleBox, actionsBox);
        
        // Create a ListView for this module's grades
        ListView<Grades> gradesListView = new ListView<>();
        ObservableList<Grades> moduleGradesObservable = FXCollections.observableArrayList();
        
        // Filter grades for this module
        for (Grades grade : courseGrades) {
            if (grade.getModuleId().equals(module.getModuleId())) {
                moduleGradesObservable.add(grade);
            }
        }
        
        gradesListView.setItems(moduleGradesObservable);
        gradesListView.setPrefHeight(Math.min(moduleGradesObservable.size() * 30 + 10, 150));
        moduleListViews.put(module.getModuleId(), gradesListView);
        
        // Create context menu for editing grades
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit Grade");
        editItem.setOnAction(e -> {
            Grades selectedGrade = gradesListView.getSelectionModel().getSelectedItem();
            if (selectedGrade != null) {
                showEditGradeDialog(selectedGrade);
                parentController.refreshModuleViews(course);
                parentController.updateCourseAverageDisplay(course);
                parentController.updateGradeDistributionChart(course);
            }
        });
        
        MenuItem deleteItem = new MenuItem("Delete Grade");
        deleteItem.setOnAction(e -> {
            Grades selectedGrade = gradesListView.getSelectionModel().getSelectedItem();
            if (selectedGrade != null) {
                parentController.getGradeManager().removeGrade(selectedGrade.getGradeId());
                parentController.refreshModuleViews(course);
                parentController.updateCourseAverageDisplay(course);
                parentController.updateGradeDistributionChart(course);
            }
        });
        
        contextMenu.getItems().addAll(editItem, deleteItem);
        gradesListView.setContextMenu(contextMenu);
        
        // Double-click to edit
        gradesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Grades selectedGrade = gradesListView.getSelectionModel().getSelectedItem();
                if (selectedGrade != null) {
                    showEditGradeDialog(selectedGrade);
                    parentController.refreshModuleViews(course);
                    parentController.updateCourseAverageDisplay(course);
                    parentController.updateGradeDistributionChart(course);
                }
            }
        });
        
        // Add components to the module section
        moduleSection.getChildren().addAll(headerBox, gradesListView);
        
        // Calculate module average
        double moduleAverage = parentController.getGradeManager().calculateModuleAverage(
            String.valueOf(parentController.getCurrentStudent().getStudentId()),
            module.getModuleId()
        );
        
        if (moduleAverage >= 0) {
            averageLabel.setText(String.format("Module Average: %.1f%%", moduleAverage));
            
            // Set color based on grade
            if (moduleAverage >= 90) {
                averageLabel.setTextFill(Color.web("#28a745")); // Green
            } else if (moduleAverage >= 80) {
                averageLabel.setTextFill(Color.web("#17a2b8")); // Blue
            } else if (moduleAverage >= 70) {
                averageLabel.setTextFill(Color.web("#ffc107")); // Yellow
            } else if (moduleAverage >= 60) {
                averageLabel.setTextFill(Color.web("#fd7e14")); // Orange
            } else {
                averageLabel.setTextFill(Color.web("#dc3545")); // Red
            }
        }
        
        return moduleSection;
    }
    
    /**
     * Shows a dialog to add a new module.
     * 
     * @param course The course to add a module to
     */
    public void showAddModuleDialog(Course course) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Module");
        dialog.setHeaderText("Add a new module to " + course.getName());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        Label nameLabel = new Label("Module Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Introduction to Java");
        
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Module description...");
        descriptionArea.setPrefRowCount(3);
        
        // Add fields to the grid
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        
        grid.add(descriptionLabel, 0, 1);
        grid.add(descriptionArea, 1, 1);
        
        // Enable/disable save button based on input validation
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);
        
        // Set dialog content
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the first field
        nameField.requestFocus();
        
        // Process the result
        if (dialog.showAndWait().get() == saveButtonType) {
            String moduleName = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (moduleName.isEmpty()) {
                moduleName = "New Module";
            }
            
            // Create the new module
            parentController.getModuleManager().createModuleForCourse(course.getId(), moduleName, description);
            
            // Refresh the views
            parentController.refreshModuleViews(course);
        }
    }
    
    /**
     * Shows a dialog to edit an existing module.
     * 
     * @param module The module to edit
     * @param course The course the module belongs to
     */
    public void showEditModuleDialog(CourseModule module, Course course) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Module");
        dialog.setHeaderText("Edit module for " + course.getName());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        Label numberLabel = new Label("Module Number:");
        TextField numberField = new TextField(String.valueOf(module.getModuleNumber()));
        
        Label nameLabel = new Label("Module Name:");
        TextField nameField = new TextField(module.getModuleName());
        
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea(module.getDescription());
        descriptionArea.setPrefRowCount(3);
        
        // Add fields to the grid
        grid.add(numberLabel, 0, 0);
        grid.add(numberField, 1, 0);
        
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        
        grid.add(descriptionLabel, 0, 2);
        grid.add(descriptionArea, 1, 2);
        
        // Enable/disable save button based on input validation
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);
        
        // Validator for module number
        numberField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int number = Integer.parseInt(newVal);
                saveButton.setDisable(number < 1);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
            }
        });
        
        // Set dialog content
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field
        nameField.requestFocus();
        
        // Process the result
        if (dialog.showAndWait().get() == saveButtonType) {
            try {
                int moduleNumber = Integer.parseInt(numberField.getText().trim());
                String moduleName = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                if (moduleName.isEmpty()) {
                    moduleName = "Module " + moduleNumber;
                }
                
                // Update the module
                module.setModuleNumber(moduleNumber);
                module.setModuleName(moduleName);
                module.setDescription(description);
                
                // Refresh the views
                parentController.refreshModuleViews(course);
                
                // Update any grades or due dates that reference this module
                updateAssociatedData(module);
                
            } catch (NumberFormatException e) {
                UIHelper.showAlert("Invalid Input", "Please enter a valid module number.");
            }
        }
    }
    
    /**
     * Updates any grades or due dates that reference a module.
     * 
     * @param module The module that was updated
     */
    private void updateAssociatedData(CourseModule module) {
        // Update module name in grades
        for (Grades grade : parentController.getGradeManager().getAllGrades()) {
            if (grade.getModuleId().equals(module.getModuleId())) {
                grade.setModuleName(module.getModuleName());
            }
        }
        
        // Update module name in due dates if due date manager exists
        if (parentController.getDueDateManager() != null) {
            for (DueDate dueDate : parentController.getDueDateManager().getAllDueDates()) {
                if (dueDate.getModuleId().equals(module.getModuleId())) {
                    dueDate.setModuleName(module.getModuleName());
                }
            }
        }
    }
    
    /**
     * Shows a dialog to edit a grade.
     * 
     * @param grade The grade to edit
     */
    public void showEditGradeDialog(Grades grade) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Edit grade for: " + grade.getAssignmentName());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Course and Module fields (non-editable)
        Label courseLabel = new Label("Course:");
        TextField courseField = new TextField();
        courseField.setEditable(false);
        
        // Find the course name
        Course course = null;
        for (Course c : parentController.getCourseManager().getAllCourses()) {
            if (c.getId().equals(grade.getCourseId())) {
                course = c;
                break;
            }
        }
        courseField.setText(course != null ? course.getName() : "Unknown Course");
        
        Label moduleLabel = new Label("Module:");
        
        // Create module dropdown to allow changing module
        ComboBox<CourseModule> moduleComboBox = new ComboBox<>();
        parentController.getModuleManager().initializeModulesForCourse(grade.getCourseId());
        List<CourseModule> modules = parentController.getModuleManager().getModulesForCourse(grade.getCourseId());
        moduleComboBox.getItems().addAll(modules);
        
        // Select current module
        CourseModule currentModule = null;
        for (CourseModule module : modules) {
            if (module.getModuleId().equals(grade.getModuleId())) {
                currentModule = module;
                moduleComboBox.setValue(module);
                break;
            }
        }
        
        // If no module is selected, select the first one
        if (moduleComboBox.getValue() == null && !modules.isEmpty()) {
            moduleComboBox.setValue(modules.get(0));
        }
        
        // Editable fields
        Label assignmentLabel = new Label("Assignment:");
        TextField assignmentField = new TextField(grade.getAssignmentName());
        
        Label scoreLabel = new Label("Score:");
        TextField scoreField = new TextField(String.valueOf(grade.getScore()));
        
        Label maxScoreLabel = new Label("Max Score:");
        TextField maxScoreField = new TextField(String.valueOf(grade.getMaxScore()));
        
        Label weightLabel = new Label("Weight (%):");
        TextField weightField = new TextField(String.valueOf(grade.getWeight()));
        
        Label dateLabel = new Label("Date Submitted:");
        TextField dateField = new TextField(grade.getDateSubmitted());
        
        Label commentsLabel = new Label("Comments:");
        TextArea commentsArea = new TextArea(grade.getComments());
        commentsArea.setPrefRowCount(3);
        
        // Add fields to grid
        grid.add(courseLabel, 0, 0);
        grid.add(courseField, 1, 0);
        
        grid.add(moduleLabel, 0, 1);
        grid.add(moduleComboBox, 1, 1);
        
        grid.add(assignmentLabel, 0, 2);
        grid.add(assignmentField, 1, 2);
        
        grid.add(scoreLabel, 0, 3);
        grid.add(scoreField, 1, 3);
        
        grid.add(maxScoreLabel, 0, 4);
        grid.add(maxScoreField, 1, 4);
        
        grid.add(weightLabel, 0, 5);
        grid.add(weightField, 1, 5);
        
        grid.add(dateLabel, 0, 6);
        grid.add(dateField, 1, 6);
        
        grid.add(commentsLabel, 0, 7);
        grid.add(commentsArea, 1, 7);
        
        // Enable/disable save button based on input validation
        dialog.getDialogPane().lookupButton(saveButtonType).setDisable(false);
        
        // Validator for number fields
        Runnable validateFields = () -> {
            try {
                double score = Double.parseDouble(scoreField.getText().trim());
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                dialog.getDialogPane().lookupButton(saveButtonType).setDisable(false);
            } catch (NumberFormatException e) {
                dialog.getDialogPane().lookupButton(saveButtonType).setDisable(true);
            }
        };
        
        // Set up validation listeners
        scoreField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        maxScoreField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        weightField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        
        // Run initial validation
        validateFields.run();
        
        // Set dialog content
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the score field
        scoreField.requestFocus();
        
        // Process the result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            try {
                double score = Double.parseDouble(scoreField.getText().trim());
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                String assignmentName = assignmentField.getText().trim();
                String date = dateField.getText().trim();
                String comments = commentsArea.getText();
                CourseModule selectedModule = moduleComboBox.getValue();
                
                // Update the grade
                grade.setAssignmentName(assignmentName);
                grade.setScore(score);
                grade.setMaxScore(maxScore);
                grade.setWeight(weight);
                grade.setDateSubmitted(date);
                grade.setComments(comments);
                
                // Update module if changed
                if (selectedModule != null && !selectedModule.getModuleId().equals(grade.getModuleId())) {
                    grade.setModuleId(selectedModule.getModuleId());
                    grade.setModuleName(selectedModule.getModuleName());
                    
                    // Update corresponding due date if it exists
                    if (parentController.getDueDateManager() != null) {
                        for (DueDate dueDate : parentController.getDueDateManager().getAllDueDates()) {
                            if (dueDate.getCourseId().equals(grade.getCourseId()) && 
                                dueDate.getAssignmentName().equals(assignmentName)) {
                                dueDate.setModuleId(selectedModule.getModuleId());
                                dueDate.setModuleName(selectedModule.getModuleName());
                                break;
                            }
                        }
                    }
                }
                
                // Show success message
                UIHelper.showAlert("Success", "Grade updated successfully!");
                
                // Refresh the views
                Course selectedCourse = parentController.getCourseComboBox().getValue();
                if (selectedCourse != null) {
                    parentController.refreshModuleViews(selectedCourse);
                    parentController.updateCourseAverageDisplay(selectedCourse);
                    parentController.updateGradeDistributionChart(selectedCourse);
                }
                
            } catch (NumberFormatException e) {
                UIHelper.showAlert("Error", "Please enter valid numeric values.");
            }
        }
    }
}