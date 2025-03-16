package com.example;

public class Student 
{
    private int studentId;
    private int coursesCompleted;
    private String firstName;
    private String lastName;
    private String fullName;

    public Student(int studentId, int coursesCompleted, String firstName, String lastName)
    {
        this.studentId = studentId;
        this.coursesCompleted = coursesCompleted;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getStudentId()
    {
        return this.studentId;
    }

    public void setStudentId(int studentId)
    {
        if (String.valueOf(studentId).matches("[0-9]{7}"))
            this.studentId = studentId;
        else 
            throw new IllegalArgumentException("Invalid student ID");
    }

    public int getCoursesCompleted()
    {
        return this.coursesCompleted;
    }

    public void setCoursesCompleted(int coursesCompleted)
    {
        this.coursesCompleted = coursesCompleted;
    }

    public String getFirstName()
    {
        return this.firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return this.lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFullName()
    {
        this.fullName = firstName + " " + lastName;
        return this.fullName;
    }
    
}
