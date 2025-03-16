package com.example;

public class Student 
{
    private int studentId;
    private int coursesCompleted;
    private String firstName;
    private String lastName;
    private String fullName;

    /**
     * The constructor `Student` initializes the fields of the `Student` class with the provided values.
     * 
     * @param studentId The `studentId` parameter is an integer that represents the unique ID of a student.
     * @param coursesCompleted The `coursesCompleted` parameter is an integer that represents the number of
     * courses completed by a student.
     * @param firstName The `firstName` parameter is a String that represents the first name of a student.
     * @param lastName The `lastName` parameter is a String that represents the last name of a student.
     */

    public Student(int studentId, int coursesCompleted, String firstName, String lastName)
    {
        this.studentId = studentId;
        this.coursesCompleted = coursesCompleted;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * The function `getStudentId` returns the student ID of the object.
     * 
     * @return The method `getStudentId` is returning the value of the `studentId` field of the current
     * object.
     */
    public int getStudentId()
    {
        return this.studentId;
    }

    /**
     * The setStudentId method in Java validates and sets the student ID if it is a 7-digit number,
     * otherwise it throws an IllegalArgumentException.
     * 
     * @param studentId The `setStudentId` method takes an integer parameter `studentId` which represents
     * the ID of a student. The method validates the student ID to ensure it is a 7-digit number before
     * setting it. If the student ID is not a 7-digit number, it throws an `IllegalArgumentException`
     */
    public void setStudentId(int studentId)
    {
        if (String.valueOf(studentId).matches("[0-9]{7}"))
            this.studentId = studentId;
        else 
            throw new IllegalArgumentException("Invalid student ID");
    }

    /**
     * The function `getCoursesCompleted` returns the number of courses completed.
     * 
     * @return The method `getCoursesCompleted` is returning the number of courses completed, which is
     * stored in the variable `coursesCompleted`.
     */
    public int getCoursesCompleted()
    {
        return this.coursesCompleted;
    }

    /**
     * This Java function sets the number of courses completed for a student.
     * 
     * @param coursesCompleted The `setCoursesCompleted` method is used to set the number of courses
     * completed by a student. The `coursesCompleted` parameter represents the number of courses completed
     * that you want to set for the student.
     */
    public void setCoursesCompleted(int coursesCompleted)
    {
        this.coursesCompleted = coursesCompleted;
    }

    /**
     * The getFirstName function in Java returns the value of the firstName attribute.
     * 
     * @return The `firstName` attribute of the current object is being returned.
     */
    public String getFirstName()
    {
        return this.firstName;
    }

    /**
     * The function sets the first name of an object to the provided value.
     * 
     * @param firstName The parameter `firstName` in the `setFirstName` method is a String type that
     * represents the first name of a person.
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * The getLastName function in Java returns the last name of an object.
     * 
     * @return The `lastName` attribute of the current object is being returned.
     */
    public String getLastName()
    {
        return this.lastName;
    }

    /**
     * The function `setLastName` sets the value of the `lastName` variable in a Java class.
     * 
     * @param lastName The parameter `lastName` is a String type that represents the last name of a person.
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * The getFullName function concatenates the firstName and lastName fields to return the full name.
     * 
     * @return The `getFullName()` method is returning the full name by concatenating the `firstName` and
     * `lastName` with a space in between.
     */
    public String getFullName()
    {
        this.fullName = firstName + " " + lastName;
        return this.fullName;
    }
    
}
