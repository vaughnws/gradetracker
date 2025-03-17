# 📚 Grade Tracker

[![JavaFX](https://img.shields.io/badge/JavaFX-✓-brightgreen.svg)](https://openjfx.io/)
[![Java 11+](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

A comprehensive JavaFX application for tracking, managing, and analyzing student grades across multiple courses and modules.

![Grade Tracker Screenshot](/src/main/resources/screenshots/screenShot.png)

# ✨ Features

- **Student Management:** Register students and manage their course enrollments
- **Course Organization:** Create and organize courses with customizable modules
- **Grade Tracking:** Record and analyze grades with weighted averages
- **Assignment Management:** Track due dates with priority levels and completion status
- **Visual Analytics:** View grade distributions and progress through intuitive charts
- **GPA Calculation:** Automatically calculate GPA across all courses
- **Calendar View:** Visualize assignment due dates in a monthly calendar

# 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/vaughnws/gradetracker.git
   cd gradetracker
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   mvn javafx:run
   ```

# 🖥️ Usage

1. **Sign Up:** Create a student profile to begin tracking your academic progress
2. **Add Courses:** Set up your courses for the semester
3. **Create Modules:** Organize course content into logical modules
4. **Track Assignments:** Add assignments with due dates and priorities
5. **Record Grades:** Enter your grades as you receive them
6. **Monitor Progress:** View your performance through charts and averages

## 📂 Project Structure

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── App.java                   # Main application
│   │   ├── Course.java                # Course model
│   │   ├── CourseController.java      # Course UI controller
│   │   ├── CourseManager.java         # Course data manager
│   │   ├── CourseModule.java          # Course module model
│   │   ├── DueDate.java               # Due date model
│   │   ├── DueDateController.java     # Due date UI controller
│   │   ├── DueDateManager.java        # Due date data manager
│   │   ├── Grades.java                # Grade model
│   │   ├── GradeController.java       # Grade UI controller
│   │   ├── GradeManager.java          # Grade data manager
│   │   └── ...
│   └── resources/
│       ├── fxml/                      # FXML layout files
│       ├── css/                       # Style sheets
│       └── images/                    # Application images
└── test/
    └── java/com/example/              # Test files
```

# 👥 Team Members

- **Vaughn** - Lead Developer
- **Mason** - Also Lead Developer
- **Kyle** - Also Also Lead Developer

## 🚀 Stretch Goals

### Mobile Application
- Develop a mobile companion app for both iOS and Android
- Real-time syncing with the desktop application
- Push notifications for upcoming assignments and due dates
- Scan physical assignments/tests to automatically log grades

### Cloud Integration
- Implement cloud storage to access grades from anywhere
- Enable sharing grade reports with parents/guardians
- Secure data with end-to-end encryption
- Automatic grade data backup and versioning

### Smart Analytics
- Predict final grades based on current performance
- Suggest study strategies based on grade patterns
- Generate weekly performance reports
- Compare performance across semesters and academic years

### Learning Management System (LMS) Integration
- Connect with popular LMS platforms (Canvas, Blackboard, Moodle)
- Automatically import assignments and grades
- Submit assignments directly through the application
- Integrate with academic calendars for scheduling

### AI-Powered Study Assistant
- Analyze learning patterns and suggest optimal study times
- Create personalized study schedules based on grade goals
- Recommend resources for challenging course topics
- Virtual tutor for difficult subjects

# 🔧 Contributing

We welcome contributions to enhance Grade Tracker! Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

# 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

# 🙏 Acknowledgments

- [JavaFX](https://openjfx.io/) for the UI framework
- [Maven](https://maven.apache.org/) for project management
- Special thanks to our instructors for guidance and support
