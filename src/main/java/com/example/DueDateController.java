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
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the due dates view.
 */
public class DueDateController {
    private DueDateManager dueDateManager;
    private CourseManager courseManager;
    private GradeManager gradeManager;
    private CourseController courseController;
    private StudentSignupController studentSignupController;
    private DueDateDialogHelper dialogHelper;
    
    private BorderPane dueDatesPane;
    private TabPane viewTabPane;
    private ListView<DueDate> dueDatesListView;
    private ObservableList<DueDate> dueDatesObservableList;
    private GridPane calendarGridPane;
    private ComboBox<String> filterComboBox;
    private ComboBox<Course> courseFilterComboBox;
    
    private YearMonth currentYearMonth;
    private Label calendarTitleLabel;
    
    // Reference to the current student
    private Student currentStudent = null;
    
    /**
     * Constructs a new DueDateController.
     * 
     * @param dueDateManager The due date manager
     * @param courseManager The course manager
     */
    public DueDateController(DueDateManager dueDateManager, CourseManager courseManager) {
        this.dueDateManager = dueDateManager;
        this.courseManager = courseManager;
        this.dueDatesObservableList = FXCollections.observableArrayList();
        this.currentYearMonth = YearMonth.now();
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
     * Sets the reference to the student signup controller.
     * 
     * @param studentSignupController The student signup controller
     */
    public void setStudentSignupController(StudentSignupController studentSignupController) {
        this.studentSignupController = studentSignupController;
    }
    
    /**
     * Sets the reference to the grade manager.
     * 
     * @param gradeManager The grade manager
     */
    public void setGradeManager(GradeManager gradeManager) {
        this.gradeManager = gradeManager;
        
        // Initialize the dialog helper if not already created
        if (dialogHelper == null) {
            dialogHelper = new DueDateDialogHelper(dueDateManager, gradeManager, this);
        } else {
            dialogHelper.setGradeManager(gradeManager);
        }
    }
    
    /**
     * Sets the current student.
     * 
     * @param student The current student
     */
    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        
        // Update dialog helper
        if (dialogHelper == null) {
            dialogHelper = new DueDateDialogHelper(dueDateManager, gradeManager, this);
        }
        dialogHelper.setCurrentStudent(student);
        
        // Update the UI based on login status
        if (dueDatesPane != null) {
            // Get the center content
            Node centerContent = dueDatesPane.getCenter();
            
            // Create content box if not already created
            VBox contentBox = new VBox(10);
            contentBox.setPadding(new Insets(10));
            
            if (student != null) {
                // User is logged in, show due dates interface
                
                // Create top controls section if not already created
                HBox controlsBox = new HBox(15);
                controlsBox.setPadding(new Insets(5));
                controlsBox.setAlignment(Pos.CENTER_LEFT);
                
                Button addDueDateButton = new Button("Add Due Date");
                addDueDateButton.setOnAction(e -> dialogHelper.showAddDueDateDialog());
                
                Label filterLabel = new Label("Filter:");
                if (filterComboBox == null) {
                    filterComboBox = new ComboBox<>(FXCollections.observableArrayList(
                        "All", "Upcoming", "Due Soon", "Overdue", "Completed", "High Priority", "Medium Priority", "Low Priority"
                    ));
                    filterComboBox.setValue("All");
                    filterComboBox.setOnAction(e -> refreshDueDatesView());
                }
                
                Label courseFilterLabel = new Label("Course:");
                if (courseFilterComboBox == null) {
                    courseFilterComboBox = new ComboBox<>();
                    courseFilterComboBox.setOnAction(e -> refreshDueDatesView());
                }

                Button addCourseFilterButton = new Button("Refresh");
                addCourseFilterButton.setOnAction(e -> refreshDueDatesView());

                
                // Add "All Courses" option
                Label allCoursesLabel = new Label("All Courses");
                allCoursesLabel.setOnMouseClicked(e -> {
                    courseFilterComboBox.setValue(null);
                    refreshDueDatesView();
                });
                
                // Update the course filter
                updateCourseFilter();
                
                // Add controls to box
                controlsBox.getChildren().clear();
                controlsBox.getChildren().addAll(
                    addDueDateButton, 
                    addCourseFilterButton,
                    filterLabel, 
                    filterComboBox,
                    courseFilterLabel,
                    courseFilterComboBox,
                    allCoursesLabel
                );
                
                // Create or get tab pane
                if (viewTabPane == null) {
                    viewTabPane = createViewTabPane();
                }
                
                // Clear and add components to content box
                contentBox.getChildren().clear();
                contentBox.getChildren().addAll(controlsBox, viewTabPane);
                
                // Update the views
                refreshDueDatesView();
                
                // Set content box as center
                dueDatesPane.setCenter(contentBox);
            } else {
                // User is not logged in, show "Please sign in" message
                VBox notLoggedInBox = new VBox(10);
                notLoggedInBox.setAlignment(Pos.CENTER);
                Label signInLabel = new Label("Please sign up to view and manage due dates");
                signInLabel.setFont(Font.font("Arial", 16));
                notLoggedInBox.getChildren().add(signInLabel);
                
                dueDatesPane.setCenter(notLoggedInBox);
            }
        }
    }
    
    /**
     * Creates the due dates view.
     * 
     * @return BorderPane containing the due dates interface
     */
    public BorderPane createDueDatesView() {
        dueDatesPane = new BorderPane();
        
        // Initialize the dialog helper
        if (dialogHelper == null) {
            dialogHelper = new DueDateDialogHelper(dueDateManager, gradeManager, this);
            dialogHelper.setCurrentStudent(currentStudent);
        }
        
        // Title
        Label titleLabel = new Label("Assignment Due Dates");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setPadding(new Insets(10));
        dueDatesPane.setTop(titleLabel);
        
        // Create "Please sign in" view for when no student is logged in
        VBox notLoggedInBox = new VBox(10);
        notLoggedInBox.setAlignment(Pos.CENTER);
        Label signInLabel = new Label("Please sign up to view and manage due dates");
        signInLabel.setFont(Font.font("Arial", 16));
        notLoggedInBox.getChildren().add(signInLabel);
        
        // Set the appropriate content based on whether a student is logged in
        if (currentStudent == null) {
            dueDatesPane.setCenter(notLoggedInBox);
        } else {
            // Initialize the view using setCurrentStudent
            setCurrentStudent(currentStudent);
        }
        
        // Set up a periodic refresh task to update status colors
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                javafx.util.Duration.seconds(30), // Refresh every 30 seconds
                event -> {
                    if (currentStudent != null) {
                        refreshDueDatesView();
                    }
                }
            )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
        
        return dueDatesPane;
    }
    
    /**
     * Creates the view tab pane.
     * 
     * @return TabPane with list and calendar views
     */
    private TabPane createViewTabPane() {
        TabPane tabPane = new TabPane();
        
        // List View Tab
        Tab listViewTab = new Tab("List View");
        listViewTab.setClosable(false);
        
        if (dueDatesListView == null) {
            dueDatesObservableList = FXCollections.observableArrayList();
            dueDatesListView = new ListView<>(dueDatesObservableList);
            dueDatesListView.setCellFactory(createDueDateCellFactory());
            
            // Context menu for list items
            ContextMenu contextMenu = new ContextMenu();
            MenuItem markCompleteItem = new MenuItem("Mark as Complete");
            markCompleteItem.setOnAction(e -> {
                DueDate selectedDueDate = dueDatesListView.getSelectionModel().getSelectedItem();
                if (selectedDueDate != null) {
                    selectedDueDate.setCompleted(!selectedDueDate.isCompleted());
                    
                    // If marking as complete, prompt for grade
                    if (selectedDueDate.isCompleted()) {
                        dialogHelper.promptForGradeEntry(selectedDueDate);
                    }
                    
                    refreshDueDatesView();
                }
            });
            
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(e -> {
                DueDate selectedDueDate = dueDatesListView.getSelectionModel().getSelectedItem();
                if (selectedDueDate != null) {
                    dialogHelper.showEditDueDateDialog(selectedDueDate);
                }
            });
            
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(e -> {
                DueDate selectedDueDate = dueDatesListView.getSelectionModel().getSelectedItem();
                if (selectedDueDate != null) {
                    dueDateManager.removeDueDate(selectedDueDate.getDueDateId());
                    refreshDueDatesView();
                }
            });
            
            contextMenu.getItems().addAll(markCompleteItem, editItem, deleteItem);
            dueDatesListView.setContextMenu(contextMenu);
            
            // Set double-click action
            dueDatesListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    DueDate selectedDueDate = dueDatesListView.getSelectionModel().getSelectedItem();
                    if (selectedDueDate != null) {
                        dialogHelper.showDueDateDetails(selectedDueDate);
                    }
                }
            });
        }
        
        // Set list view in tab
        listViewTab.setContent(dueDatesListView);
        
        // Calendar View Tab
        Tab calendarViewTab = new Tab("Calendar View");
        calendarViewTab.setClosable(false);
        
        // Create calendar container
        VBox calendarContainer = new VBox(10);
        calendarContainer.setPadding(new Insets(10));
        
        // Calendar navigation controls
        HBox calendarNavBox = new HBox(15);
        calendarNavBox.setAlignment(Pos.CENTER);
        
        Button prevMonthButton = new Button("←");
        calendarTitleLabel = new Label();
        Button nextMonthButton = new Button("→");
        
        prevMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendarView();
        });
        
        nextMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendarView();
        });
        
        calendarNavBox.getChildren().addAll(prevMonthButton, calendarTitleLabel, nextMonthButton);
        
        // Calendar grid
        calendarGridPane = new GridPane();
        calendarGridPane.setHgap(5);
        calendarGridPane.setVgap(5);
        calendarGridPane.setAlignment(Pos.CENTER);
        
        // Day of week headers
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            dayLabel.setPrefWidth(100);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGridPane.add(dayLabel, i, 0);
        }
        
        // Add calendar components to container
        calendarContainer.getChildren().addAll(calendarNavBox, calendarGridPane);
        
        // Set calendar in tab
        calendarViewTab.setContent(calendarContainer);
        
        // Add tabs to tab pane
        tabPane.getTabs().addAll(listViewTab, calendarViewTab);
        
        // Set up tab change listener to update views
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == calendarViewTab) {
                updateCalendarView();
            } else {
                updateListView();
            }
        });
        
        return tabPane;
    }
    
    /**
     * Updates the calendar view based on the current year-month.
     */
    private void updateCalendarView() {
        // Update calendar title
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        calendarTitleLabel.setText(currentYearMonth.format(formatter));
        
        // Clear calendar except headers
        for (int i = calendarGridPane.getChildren().size() - 1; i >= 0; i--) {
            Node child = calendarGridPane.getChildren().get(i);
            Integer rowIndex = GridPane.getRowIndex(child);
            if (rowIndex != null && rowIndex > 0) {
                calendarGridPane.getChildren().remove(i);
            }
        }
        
        // Get date info for the month
        int month = currentYearMonth.getMonthValue();
        int year = currentYearMonth.getYear();
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // 0 = Sunday
        int daysInMonth = currentYearMonth.lengthOfMonth();
        
        // Get all due dates for the month
        LocalDate startDate = firstOfMonth;
        LocalDate endDate = firstOfMonth.plusMonths(1).minusDays(1);
        List<DueDate> monthDueDates = dueDateManager.getDueDatesInRange(startDate, endDate);
        
        // Map due dates by day of month
        Map<Integer, List<DueDate>> dueDatesByDay = new HashMap<>();
        for (DueDate dueDate : monthDueDates) {
            int day = dueDate.getDueDate().getDayOfMonth();
            if (!dueDatesByDay.containsKey(day)) {
                dueDatesByDay.put(day, new ArrayList<>());
            }
            dueDatesByDay.get(day).add(dueDate);
        }
        
        // Create calendar cells
        int day = 1;
        int row = 1;
        
        // Fill first row with empty cells until the first day of the month
        for (int col = 0; col < dayOfWeek; col++) {
            VBox emptyCell = createEmptyCalendarCell();
            calendarGridPane.add(emptyCell, col, row);
        }
        
        // Fill the remaining cells with days
        for (int col = dayOfWeek; col < 7; col++, day++) {
            VBox dayCell = createCalendarDayCell(day, dueDatesByDay.getOrDefault(day, new ArrayList<>()));
            calendarGridPane.add(dayCell, col, row);
        }
        
        // Fill remaining rows
        while (day <= daysInMonth) {
            row++;
            for (int col = 0; col < 7 && day <= daysInMonth; col++, day++) {
                VBox dayCell = createCalendarDayCell(day, dueDatesByDay.getOrDefault(day, new ArrayList<>()));
                calendarGridPane.add(dayCell, col, row);
            }
        }
    }
    
    /**
     * Creates an empty calendar cell.
     * 
     * @return VBox representing an empty calendar cell
     */
    private VBox createEmptyCalendarCell() {
        VBox cell = new VBox(5);
        cell.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");
        cell.setPrefSize(100, 100);
        cell.setPadding(new Insets(5));
        return cell;
    }
    
    /**
     * Creates a calendar cell for a specific day.
     * 
     * @param day The day of the month
     * @param dueDates List of due dates for that day
     * @return VBox representing a calendar day cell
     */
    private VBox createCalendarDayCell(int day, List<DueDate> dueDates) {
        VBox cell = new VBox(5);
        cell.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6;");
        cell.setPrefSize(100, 100);
        cell.setPadding(new Insets(5));
        
        // Day number
        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Add day label
        cell.getChildren().add(dayLabel);
        
        // Highlight today
        LocalDate cellDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), day);
        if (cellDate.equals(LocalDate.now())) {
            cell.setStyle("-fx-background-color: #e8f4f8; -fx-border-color: #0099cc; -fx-border-width: 2;");
            dayLabel.setStyle("-fx-text-fill: #0099cc;");
        }
        
        // Add due dates
        int maxToShow = 3; // Show max 3 due dates in each cell
        for (int i = 0; i < Math.min(dueDates.size(), maxToShow); i++) {
            DueDate dueDate = dueDates.get(i);
            Label dueDateLabel = new Label(dueDate.getAssignmentName());
            dueDateLabel.setStyle("-fx-text-fill: " + dueDate.getStatusColor() + ";");
            dueDateLabel.setMaxWidth(90);
            cell.getChildren().add(dueDateLabel);
            
            // Add tooltip with full information
            Tooltip tooltip = new Tooltip(dueDate.toString());
            Tooltip.install(dueDateLabel, tooltip);
            
            // Add click handler to show details
            dueDateLabel.setOnMouseClicked(e -> dialogHelper.showDueDateDetails(dueDate));
        }
        
        // If there are more due dates than we can show, add a "more" indicator
        if (dueDates.size() > maxToShow) {
            Label moreLabel = new Label("+" + (dueDates.size() - maxToShow) + " more");
            moreLabel.setStyle("-fx-text-fill: #6c757d;");
            cell.getChildren().add(moreLabel);
            
            // Add click handler to show all due dates for this day
            moreLabel.setOnMouseClicked(e -> dialogHelper.showDueDatesForDay(cellDate));
        }
        
        return cell;
    }
    
    /**
     * Updates the list view with filtered due dates.
     */
    private void updateListView() {
        refreshDueDatesView();
    }
    
    /**
     * Creates a cell factory for due date list items.
     * 
     * @return Callback to create list cells for due dates
     */
    private Callback<ListView<DueDate>, ListCell<DueDate>> createDueDateCellFactory() {
        return listView -> new ListCell<DueDate>() {
            @Override
            protected void updateItem(DueDate dueDate, boolean empty) {
                super.updateItem(dueDate, empty);
                
                if (empty || dueDate == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create content for cell
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    
                    // Create colored indicator based on status
                    Region statusIndicator = new Region();
                    statusIndicator.setMinWidth(12);
                    statusIndicator.setMinHeight(12);
                    statusIndicator.setStyle("-fx-background-color: " + dueDate.getStatusColor() + "; -fx-background-radius: 6;");
                    
                    // Create labels for information
                    Label assignmentLabel = new Label(dueDate.getAssignmentName());
                    assignmentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    
                    Label courseLabel = new Label(dueDate.getCourseName());
                    
                    Label dateLabel = new Label(dueDate.getDueDateFormatted());
                    
                    Label statusLabel = new Label(dueDate.getStatus());
                    statusLabel.setStyle("-fx-text-fill: " + dueDate.getStatusColor() + ";");
                    
                    // Create checkbox for completed status
                    CheckBox completedCheckBox = new CheckBox();
                    completedCheckBox.setSelected(dueDate.isCompleted());
                    completedCheckBox.setOnAction(e -> {
                        // Update due date completion status
                        boolean newCompletedState = completedCheckBox.isSelected();
                        dueDate.setCompleted(newCompletedState);
                        
                        // If marking as complete, prompt for grade entry
                        if (newCompletedState) {
                            dialogHelper.promptForGradeEntry(dueDate);
                        }
                        
                        refreshDueDatesView();
                    });
                    
                    // First section with status indicator and assignment name
                    VBox mainInfo = new VBox(5);
                    HBox assignmentBox = new HBox(10);
                    assignmentBox.getChildren().addAll(statusIndicator, assignmentLabel);
                    mainInfo.getChildren().addAll(assignmentBox, courseLabel);
                    
                    // Middle section with date and status
                    VBox dateInfo = new VBox(5);
                    dateInfo.setAlignment(Pos.CENTER);
                    dateInfo.getChildren().addAll(dateLabel, statusLabel);
                    
                    // Use HBox to arrange sections
                    container.getChildren().addAll(mainInfo, dateInfo, completedCheckBox);
                    HBox.setHgrow(mainInfo, Priority.ALWAYS);
                    
                    // Add some padding
                    container.setPadding(new Insets(5));
                    
                    setGraphic(container);
                    setText(null);
                    
                    // Set background color if completed
                    if (dueDate.isCompleted()) {
                        setStyle("-fx-background-color: #f8f9fa;");
                    } else {
                        setStyle("");
                    }
                }
            }
        };
    }
    
    /**
     * Updates the course filter combobox with enrolled courses.
     */
    private void updateCourseFilter() {
        if (currentStudent == null || courseFilterComboBox == null) return;
        
        // Save current selection
        Course currentSelection = courseFilterComboBox.getValue();
        
        // Create observable list with enrolled courses
        ObservableList<Course> courseList = FXCollections.observableArrayList();
        
        // Add courses the student is enrolled in
        for (Course course : courseManager.getAllCourses()) {
            if (course.isStudentEnrolled(String.valueOf(currentStudent.getStudentId()))) {
                courseList.add(course);
            }
        }
        
        // Update the combobox items
        courseFilterComboBox.setItems(courseList);
        
        // Restore selection if possible, otherwise set to null (All Courses)
        if (currentSelection != null && courseList.contains(currentSelection)) {
            courseFilterComboBox.setValue(currentSelection);
        } else {
            courseFilterComboBox.setValue(null);
        }
    }
    
    /**
     * Refreshes the due dates view with the current filter.
     */
    public void refreshDueDatesView() {
        if (dueDatesPane == null || currentStudent == null) return;
        
        // Make sure the UI components are initialized
        if (dueDatesObservableList == null || filterComboBox == null || courseFilterComboBox == null) {
            // The UI hasn't been fully initialized yet
            return;
        }
        
        // Update enrolled courses in the course filter
        updateCourseFilter();
        
        // Get the current filter
        String filter = filterComboBox.getValue();
        Course selectedCourse = courseFilterComboBox.getValue();
        
        // Get filtered due dates
        List<DueDate> filteredDueDates = new ArrayList<>();
        
        // First, filter by course if selected
        List<DueDate> courseFilteredDueDates;
        if (selectedCourse != null) {
            courseFilteredDueDates = dueDateManager.getDueDatesForCourse(selectedCourse.getId());
        } else {
            courseFilteredDueDates = dueDateManager.getDueDatesForStudent(currentStudent.getStudentId(), courseManager);
        }
        
        // Then, apply status/priority filter
        switch (filter) {
            case "Upcoming":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.getStatus().equals("Upcoming")) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            case "Due Soon":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.getStatus().equals("Due Soon")) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            case "Overdue":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.getStatus().equals("Overdue")) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            case "Completed":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.isCompleted()) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            case "High Priority":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.getPriority().equals("High")) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            case "Medium Priority":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.getPriority().equals("Medium")) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            case "Low Priority":
                for (DueDate dueDate : courseFilteredDueDates) {
                    if (dueDate.getPriority().equals("Low")) {
                        filteredDueDates.add(dueDate);
                    }
                }
                break;
            default: // "All"
                filteredDueDates.addAll(courseFilteredDueDates);
                break;
        }
        
        // Update the observable list
        dueDatesObservableList.clear();
        dueDatesObservableList.addAll(filteredDueDates);
        
        // Update calendar if it's the current tab
        if (viewTabPane != null) {
            Tab selectedTab = viewTabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null && selectedTab.getText().equals("Calendar View")) {
                updateCalendarView();
            }
        }
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
     * Gets the course manager.
     * 
     * @return The course manager
     */
    public CourseManager getCourseManager() {
        return courseManager;
    }

}