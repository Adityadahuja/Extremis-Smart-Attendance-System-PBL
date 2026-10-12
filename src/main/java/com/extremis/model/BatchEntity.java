package com.extremis.model;

import java.io.Serializable;

/**
 * Model representing a Lab or Practical Batch within a Class (e.g. Batch A1).
 */
public class BatchEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long classId;
    private String code;
    private String name;
    private boolean active;

    // Joined helper fields
    private String classCode;
    private String className;

    // Aggregated statistics
    private int studentCount;
    private int teacherCount;

    public BatchEntity() {
        this.active = true;
    }

    public BatchEntity(Long id, Long classId, String code, String name, boolean active) {
        this.id = id;
        this.classId = classId;
        this.code = code;
        this.name = name;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getTeacherCount() { return teacherCount; }
    public void setTeacherCount(int teacherCount) { this.teacherCount = teacherCount; }
}
