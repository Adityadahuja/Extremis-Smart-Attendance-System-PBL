package com.extremis.model;

import java.io.Serializable;

/**
 * Model representing a Teacher-to-Class and Batch assignment with subject/course.
 */
public class TeacherAssignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long facultyId;
    private Long classId;
    private Long batchId; // Optional: null or 0 means all batches / entire class
    private String subject;
    private String createdAt;

    // Joined helper fields
    private String facultyName;
    private String facultyCode;
    private String facultyEmail;
    private String classCode;
    private String className;
    private String batchCode;
    private String batchName;

    public TeacherAssignment() {
    }

    public TeacherAssignment(Long id, Long facultyId, Long classId, Long batchId, String subject, String createdAt) {
        this.id = id;
        this.facultyId = facultyId;
        this.classId = classId;
        this.batchId = batchId;
        this.subject = subject;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFacultyId() { return facultyId; }
    public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public String getFacultyCode() { return facultyCode; }
    public void setFacultyCode(String facultyCode) { this.facultyCode = facultyCode; }

    public String getFacultyEmail() { return facultyEmail; }
    public void setFacultyEmail(String facultyEmail) { this.facultyEmail = facultyEmail; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public String getDisplayTarget() {
        if (batchCode != null && !batchCode.trim().isEmpty()) {
            return classCode + " (Batch " + batchCode + ")";
        }
        return classCode + " (All Batches)";
    }
}
