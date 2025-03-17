package com.example;

import java.util.UUID;

/** 
 * Represents a module within a course.
 * Each course has exactly 9 modules that cover different material. kyle was here
 */
public class CourseModule {
    private final String moduleId;
    private String courseId;
    private String moduleName;
    private int moduleNumber; // 1-9
    private String description;
    
    /**
     * Constructs a new CourseModule with the given details.
     * 
     * @param courseId ID of the course this module belongs to
     * @param moduleName Name of the module
     * @param moduleNumber Number of the module (1-9)
     * @param description Description of the module content
     */
    public CourseModule(String courseId, String moduleName, int moduleNumber, String description) {
        this.moduleId = UUID.randomUUID().toString();
        this.courseId = courseId;
        this.moduleName = moduleName;
        this.moduleNumber = moduleNumber;
        this.description = description;
    }
    
    /**
     * Gets the unique ID of this module.
     * 
     * @return Module ID
     */
    public String getModuleId() {
        return moduleId;
    }
    
    /**
     * Gets the ID of the course this module belongs to.
     * 
     * @return Course ID
     */
    public String getCourseId() {
        return courseId;
    }
    
    /**
     * Sets the course ID.
     * 
     * @param courseId New course ID
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    /**
     * Gets the name of the module.
     * 
     * @return Module name
     */
    public String getModuleName() {
        return moduleName;
    }
    
    /**
     * Sets the module name.
     * 
     * @param moduleName New module name
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    /**
     * Gets the module number (1-9).
     * 
     * @return Module number
     */
    public int getModuleNumber() {
        return moduleNumber;
    }
    
    /**
     * Sets the module number.
     * 
     * @param moduleNumber New module number
     */
    public void setModuleNumber(int moduleNumber) {
        if (moduleNumber >= 1 && moduleNumber <= 9) {
            this.moduleNumber = moduleNumber;
        } else {
            throw new IllegalArgumentException("Module number must be between 1 and 9");
        }
    }
    
    /**
     * Gets the description of the module.
     * 
     * @return Module description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the module description.
     * 
     * @param description New module description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Module " + moduleNumber + ": " + moduleName;
    }
}