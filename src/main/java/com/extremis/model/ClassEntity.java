package com.extremis.model;

import java.io.Serializable;

/**
 * Model representing an Academic Class (e.g. AI - Section A, CS - Section A).
 */
public class ClassEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String department;
    private String semester;
    private boolean active;

    // Aggregated statistics
    private int studentCount;
    private int teacherCount;
    private int batchCount;

    public ClassEntity() {
        this.active = true;
    }

    public ClassEntity(Long id, String code, String name, String department, String semester, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.department = department;
        this.semester = semester;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public int getTeacherCount() { return teacherCount; }
    public void setTeacherCount(int teacherCount) { this.teacherCount = teacherCount; }

    public int getBatchCount() { return batchCount; }
    public void setBatchCount(int batchCount) { this.batchCount = batchCount; }
}
