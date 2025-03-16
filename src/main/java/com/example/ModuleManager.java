package com.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the collection of course modules in the application.
 */
public class ModuleManager {
    private final List<CourseModule> modules;
    
    /**
     * Creates a new ModuleManager with an empty collection of modules.
     */
    public ModuleManager() {
        this.modules = new ArrayList<>();
    }
    
    /**
     * Adds a new module to the collection.
     * 
     * @param module Module to add
     * @return The added module
     */
    public CourseModule addModule(CourseModule module) {
        modules.add(module);
        return module;
    }
    
    /**
     * Creates a new module for a course with the next available module number.
     * 
     * @param courseId ID of the course
     * @param moduleName Name of the module
     * @param description Description of the module
     * @return The newly created module
     */
    public CourseModule createModuleForCourse(String courseId, String moduleName, String description) {
        // Find the highest module number for this course
        int highestModuleNumber = 0;
        for (CourseModule module : getModulesForCourse(courseId)) {
            if (module.getModuleNumber() > highestModuleNumber) {
                highestModuleNumber = module.getModuleNumber();
            }
        }
        
        // Create new module with next number
        int nextModuleNumber = highestModuleNumber + 1;
        CourseModule newModule = new CourseModule(
            courseId, 
            moduleName != null && !moduleName.isEmpty() ? moduleName : "Module " + nextModuleNumber,
            nextModuleNumber,
            description != null ? description : "Content for module " + nextModuleNumber
        );
        
        addModule(newModule);
        return newModule;
    }
    
    /**
     * Retrieves a module by its unique ID.
     * 
     * @param moduleId ID of the module to retrieve
     * @return Module with the specified ID, or null if not found
     */
    public CourseModule getModuleById(String moduleId) {
        for (CourseModule module : modules) {
            if (module.getModuleId().equals(moduleId)) {
                return module;
            }
        }
        return null;
    }
    
    /**
     * Gets a list of all modules.
     * 
     * @return List of all modules
     */
    public List<CourseModule> getAllModules() {
        return new ArrayList<>(modules);
    }
    
    /**
     * Gets all modules for a specific course, sorted by module number.
     * 
     * @param courseId ID of the course
     * @return List of modules for the specified course
     */
    public List<CourseModule> getModulesForCourse(String courseId) {
        List<CourseModule> result = new ArrayList<>();
        
        for (CourseModule module : modules) {
            if (module.getCourseId().equals(courseId)) {
                result.add(module);
            }
        }
        
        // Sort by module number
        result.sort(Comparator.comparingInt(CourseModule::getModuleNumber));
        
        return result;
    }
    
    /**
     * Initializes default modules for a course if none exist.
     * Unlike the previous version, this does not create a fixed number of modules.
     * It only creates a "General" module if no modules exist for the course.
     * 
     * @param courseId ID of the course
     */
    public void initializeModulesForCourse(String courseId) {
        List<CourseModule> existingModules = getModulesForCourse(courseId);
        
        // If there are no modules, create a general module
        if (existingModules.isEmpty()) {
            CourseModule generalModule = new CourseModule(
                courseId, 
                "General", 
                1, 
                "General course content"
            );
            addModule(generalModule);
        }
    }
    
    /**
     * Updates an existing module.
     * 
     * @param moduleId ID of the module to update
     * @param updatedModule Updated module data
     * @return true if the module was updated, false if the module was not found
     */
    public boolean updateModule(String moduleId, CourseModule updatedModule) {
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getModuleId().equals(moduleId)) {
                modules.set(i, updatedModule);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes a module from the collection.
     * 
     * @param moduleId ID of the module to remove
     * @return true if the module was removed, false if the module was not found
     */
    public boolean removeModule(String moduleId) {
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getModuleId().equals(moduleId)) {
                modules.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a module has any grades or due dates associated with it.
     * 
     * @param moduleId ID of the module to check
     * @param gradeManager Grade manager to check for grades
     * @param dueDateManager Due date manager to check for due dates
     * @return true if the module has associated data, false otherwise
     */
    public boolean moduleHasAssociatedData(String moduleId, GradeManager gradeManager, DueDateManager dueDateManager) {
        // Check if there are any grades for this module
        for (Grades grade : gradeManager.getAllGrades()) {
            if (grade.getModuleId().equals(moduleId)) {
                return true;
            }
        }
        
        // Check if there are any due dates for this module
        for (DueDate dueDate : dueDateManager.getAllDueDates()) {
            if (dueDate.getModuleId().equals(moduleId)) {
                return true;
            }
        }
        
        return false;
    }
}