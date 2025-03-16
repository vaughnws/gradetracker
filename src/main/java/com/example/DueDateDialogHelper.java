package com.example;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing dialogs related to due dates.
 */
public class DueDateDialogHelper {
    private DueDateManager dueDateManager;
    private GradeManager gradeManager;
    private DueDateController parentController;
    private Student currentStudent;
    
    /**
     * Constructs a new DueDateDialogHelper.
     * 
     * @param dueDateManager The due date manager
     * @param gradeManager The grade manager
     * @param parentController The parent controller
     */
    public DueDateDialogHelper(DueDateManager dueDateManager, GradeManager gradeManager, DueDateController parentController) {
        this.dueDateManager = dueDateManager;
        this.gradeManager = gradeManager;
        this.parentController = parentController;
    }
    
    /**
     * Sets the grade manager.
     * 
     * @param gradeManager The grade manager
     */
    public void setGradeManager(GradeManager gradeManager) {
        this.gradeManager = gradeManager;
    }
    
    /**
     * Shows a dialog to add a new due date.
     */
    public void showAddDueDateDialog() {
        DueDate newDueDate = new DueDate("", "", "", "", LocalDate.now().plusDays(7), "Medium");
        
        if (showDueDateDialog(newDueDate, "Add New Due Date")) {
            dueDateManager.addDueDate(newDueDate);
            
            // Create corresponding grade entry if grade manager exists
            if (gradeManager != null && currentStudent != null) {
                // Create a new grade with default values
                Grades newGrade = new Grades(
                    String.valueOf(currentStudent.getStudentId()),
                    newDueDate.getCourseId(),
                    newDueDate.getAssignmentName(),
                    0.0,  // Default score
                    100.0, // Default max score
                    10.0,  // Default weight
                    newDueDate.getDueDateFormatted()
                );
                
                gradeManager.addGrade(newGrade);
            }
            
            parentController.refreshDueDatesView();
        }
    }
    
    /**
     * Shows a dialog to edit an existing due date.
     * 
     * @param dueDate The due date to edit
     */
    public void showEditDueDateDialog(DueDate dueDate) {
        if (showDueDateDialog(dueDate, "Edit Due Date")) {
            parentController.refreshDueDatesView();
        }
    }
    
    /**
     * Shows a dialog to add or edit a due date.
     * 
     * @param dueDate The due date to edit
     * @param title The dialog title
     * @return true if the dialog was confirmed, false otherwise
     */
    private boolean showDueDateDialog(DueDate dueDate, String title) {
        // Create the dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter due date details:");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            parentController.getCourseManager().getAllCourses()
        ));
        
        // Filter to only show enrolled courses
        if (currentStudent != null) {
            courseCombo.getItems().removeIf(course -> 
                !course.isStudentEnrolled(String.valueOf(currentStudent.getStudentId()))
            );
        }
        
        // Set selected course if it exists
        if (!dueDate.getCourseId().isEmpty()) {
            for (Course course : courseCombo.getItems()) {
                if (course.getId().equals(dueDate.getCourseId())) {
                    courseCombo.setValue(course);
                    break;
                }
            }
        }
        
        TextField assignmentField = new TextField(dueDate.getAssignmentName());
        DatePicker datePicker = new DatePicker(dueDate.getDueDate());
        
        ComboBox<String> priorityCombo = new ComboBox<>(
            javafx.collections.FXCollections.observableArrayList("High", "Medium", "Low")
        );
        priorityCombo.setValue(dueDate.getPriority());
        
        TextArea descriptionArea = new TextArea(dueDate.getDescription());
        descriptionArea.setPrefRowCount(3);
        
        CheckBox completedCheckBox = new CheckBox("Completed");
        completedCheckBox.setSelected(dueDate.isCompleted());
        
        // Add fields to the grid
        grid.add(new Label("Course:"), 0, 0);
        grid.add(courseCombo, 1, 0);
        
        grid.add(new Label("Assignment:"), 0, 1);
        grid.add(assignmentField, 1, 1);
        
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityCombo, 1, 3);
        
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        
        grid.add(completedCheckBox, 1, 5);
        
        // Enable/disable save button based on input validation
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Validator for required fields
        Runnable validateFields = () -> {
            boolean isValid = courseCombo.getValue() != null && 
                             !assignmentField.getText().trim().isEmpty() && 
                             datePicker.getValue() != null;
            saveButton.setDisable(!isValid);
        };
        
        // Set up validation listeners
        courseCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        assignmentField.textProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateFields.run());
        
        // Run initial validation
        validateFields.run();
        
        // Set dialog content
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the first field
        assignmentField.requestFocus();
        
        // Process the result
        if (dialog.showAndWait().get() == saveButtonType) {
            // Update due date with form values
            Course selectedCourse = courseCombo.getValue();
            dueDate.setCourseId(selectedCourse.getId());
            dueDate.setCourseName(selectedCourse.getName());
            dueDate.setAssignmentName(assignmentField.getText().trim());
            dueDate.setDueDate(datePicker.getValue());
            dueDate.setPriority(priorityCombo.getValue());
            dueDate.setDescription(descriptionArea.getText().trim());
            dueDate.setCompleted(completedCheckBox.isSelected());
            return true;
        }
        return false;
    }
    
    /**
     * Shows details for a specific due date.
     * 
     * @param dueDate The due date to show details for
     */
    public void showDueDateDetails(DueDate dueDate) {
        // Create a dialog to show due date details
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Due Date Details");
        dialog.setHeaderText("Assignment: " + dueDate.getAssignmentName());
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Add details
        grid.add(new Label("Course:"), 0, 0);
        grid.add(new Label(dueDate.getCourseName()), 1, 0);
        
        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(new Label(dueDate.getDueDateFormatted()), 1, 1);
        
        grid.add(new Label("Status:"), 0, 2);
        Label statusLabel = new Label(dueDate.getStatus());
        statusLabel.setStyle("-fx-text-fill: " + dueDate.getStatusColor() + ";");
        grid.add(statusLabel, 1, 2);
        
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(new Label(dueDate.getPriority()), 1, 3);
        
        grid.add(new Label("Description:"), 0, 4);
        TextArea descArea = new TextArea(dueDate.getDescription());
        descArea.setEditable(false);
        descArea.setPrefRowCount(3);
        grid.add(descArea, 1, 4);
        
        // Add button to edit grade if assignment is completed
        if (dueDate.isCompleted() && gradeManager != null && currentStudent != null) {
            Button editGradeButton = new Button("Edit Grade");
            grid.add(editGradeButton, 1, 5);
            
            editGradeButton.setOnAction(e -> {
                // Find the corresponding grade
                List<Grades> courseGrades = gradeManager.getGradesForStudentInCourse(
                    currentStudent.getStudentId(), 
                    dueDate.getCourseId()
                );
                
                Grades matchingGrade = null;
                for (Grades grade : courseGrades) {
                    if (grade.getAssignmentName().equals(dueDate.getAssignmentName())) {
                        matchingGrade = grade;
                        break;
                    }
                }
                
                if (matchingGrade != null) {
                    // Show grade editing dialog
                    showGradeEditDialog(matchingGrade);
                } else {
                    // Create a new grade if one doesn't exist
                    Grades newGrade = new Grades(
                        String.valueOf(currentStudent.getStudentId()),
                        dueDate.getCourseId(),
                        dueDate.getAssignmentName(),
                        0.0,  // Default score
                        100.0, // Default max score
                        10.0,  // Default weight
                        dueDate.getDueDateFormatted()
                    );
                    
                    gradeManager.addGrade(newGrade);
                    showGradeEditDialog(newGrade);
                }
                
                dialog.close();
            });
        }
        
        // Add buttons for actions
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType completeButton = new ButtonType(dueDate.isCompleted() ? "Mark Incomplete" : "Mark Complete");
        ButtonType editButton = new ButtonType("Edit");
        
        dialog.getDialogPane().getButtonTypes().addAll(completeButton, editButton, closeButton);
        
        // Handle button actions
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == completeButton) {
                boolean newCompletedState = !dueDate.isCompleted();
                dueDate.setCompleted(newCompletedState);
                
                // If marking as complete and grade manager exists, prompt to add grade
                if (newCompletedState && gradeManager != null && currentStudent != null) {
                    promptForGradeEntry(dueDate);
                }
                
                parentController.refreshDueDatesView();
            } else if (dialogButton == editButton) {
                showEditDueDateDialog(dueDate);
            }
            return null;
        });
        
        // Set content and show dialog
        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }
    
    /**
     * Prompts the user to enter a grade for a completed assignment.
     * 
     * @param dueDate The due date for the completed assignment
     */
    public void promptForGradeEntry(DueDate dueDate) {
        // Find if a grade already exists for this assignment
        List<Grades> courseGrades = gradeManager.getGradesForStudentInCourse(
            currentStudent.getStudentId(), 
            dueDate.getCourseId()
        );
        
        boolean gradeExists = false;
        for (Grades grade : courseGrades) {
            if (grade.getAssignmentName().equals(dueDate.getAssignmentName())) {
                gradeExists = true;
                showGradeEditDialog(grade);
                break;
            }
        }
        
        if (!gradeExists) {
            // Create a new grade
            Grades newGrade = new Grades(
                String.valueOf(currentStudent.getStudentId()),
                dueDate.getCourseId(),
                dueDate.getAssignmentName(),
                0.0,  // Default score
                100.0, // Default max score
                10.0,  // Default weight
                dueDate.getDueDateFormatted()
            );
            
            gradeManager.addGrade(newGrade);
            
            // Show dialog to edit the grade
            showGradeEditDialog(newGrade);
        }
    }
    
    /**
     * Shows a dialog with all due dates for a specific day.
     * 
     * @param date The date to show due dates for
     */
    public void showDueDatesForDay(LocalDate date) {
        // Create a dialog to show all due dates for this day
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Due Dates for " + date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dialog.setHeaderText("All assignments due on this date:");
        
        // Get due dates for this day
        List<DueDate> dueDatesForDay = new ArrayList<>();
        for (DueDate dueDate : dueDateManager.getAllDueDates()) {
            if (dueDate.getDueDate().equals(date)) {
                dueDatesForDay.add(dueDate);
            }
        }
        
        // Create a list view to display due dates
        ListView<DueDate> dueDatesList = new ListView<>();
        
        // Create cell factory for list items
        dueDatesList.setCellFactory(listView -> new ListCell<DueDate>() {
            @Override
            protected void updateItem(DueDate dueDate, boolean empty) {
                super.updateItem(dueDate, empty);
                
                if (empty || dueDate == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(dueDate.getAssignmentName() + " - " + dueDate.getCourseName());
                    
                    // Set text color based on status
                    setTextFill(javafx.scene.paint.Color.web(dueDate.getStatusColor()));
                    
                    // Set background color if completed
                    if (dueDate.isCompleted()) {
                        setStyle("-fx-background-color: #f8f9fa;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        dueDatesList.getItems().addAll(dueDatesForDay);
        
        // Set double-click action to open details
        dueDatesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                DueDate selectedDueDate = dueDatesList.getSelectionModel().getSelectedItem();
                if (selectedDueDate != null) {
                    dialog.close();
                    showDueDateDetails(selectedDueDate);
                }
            }
        });
        
        // Set minimum size for the dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(400);
        dialogPane.setMinHeight(300);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        dialogPane.setContent(dueDatesList);
        
        dialog.showAndWait();
    }
    
    /**
     * Shows a dialog to edit a grade.
     * 
     * @param grade The grade to edit
     */
    private void showGradeEditDialog(Grades grade) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Grade");
        dialog.setHeaderText("Enter grade for: " + grade.getAssignmentName());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField scoreField = new TextField(String.valueOf(grade.getScore()));
        TextField maxScoreField = new TextField(String.valueOf(grade.getMaxScore()));
        TextField weightField = new TextField(String.valueOf(grade.getWeight()));
        TextArea commentsArea = new TextArea(grade.getComments());
        commentsArea.setPrefRowCount(3);
        
        // Add fields to grid
        grid.add(new Label("Score:"), 0, 0);
        grid.add(scoreField, 1, 0);
        
        grid.add(new Label("Max Score:"), 0, 1);
        grid.add(maxScoreField, 1, 1);
        
        grid.add(new Label("Weight (%):"), 0, 2);
        grid.add(weightField, 1, 2);
        
        grid.add(new Label("Comments:"), 0, 3);
        grid.add(commentsArea, 1, 3);
        
        // Enable/disable save button based on input validation
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);
        
        // Validator for number fields
        Runnable validateFields = () -> {
            try {
                double score = Double.parseDouble(scoreField.getText().trim());
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                saveButton.setDisable(false);
            } catch (NumberFormatException e) {
                saveButton.setDisable(true);
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
        if (dialog.showAndWait().get() == saveButtonType) {
            try {
                double score = Double.parseDouble(scoreField.getText().trim());
                double maxScore = Double.parseDouble(maxScoreField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                
                // Update the grade
                grade.setScore(score);
                grade.setMaxScore(maxScore);
                grade.setWeight(weight);
                grade.setComments(commentsArea.getText().trim());
                
                // Show success message
                UIHelper.showAlert("Success", "Grade updated successfully!");
            } catch (NumberFormatException e) {
                UIHelper.showAlert("Error", "Please enter valid numeric values.");
            }
        }
    }


public void setCurrentStudent(Student student) {

    this.currentStudent = student;
}

}