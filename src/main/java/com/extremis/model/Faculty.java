package com.extremis.model;

import java.io.Serializable;

public class Faculty implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String facultyId;
    private String name;
    private String email;
    private String branch;
    private String section;
    private String department;
    private boolean active = true;
    private String passwordHash;
    private String sessionToken;
    private int assignmentCount;

    public Faculty() {
        this.active = true;
    }

    public Faculty(Long id, String facultyId, String name, String email, String branch, String section, String passwordHash) {
        this.id = id;
        this.facultyId = facultyId;
        this.name = name;
        this.email = email;
        this.branch = branch;
        this.section = section;
        this.department = branch;
        this.passwordHash = passwordHash;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getDepartment() {
        if (department != null && !department.trim().isEmpty()) {
            return department;
        }
        return branch != null ? branch : "General";
    }
    public void setDepartment(String department) { this.department = department; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getAssignedClass() {
        return (branch != null ? branch : "") + " - " + (section != null ? section : "");
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public int getAssignmentCount() { return assignmentCount; }
    public void setAssignmentCount(int assignmentCount) { this.assignmentCount = assignmentCount; }
}
