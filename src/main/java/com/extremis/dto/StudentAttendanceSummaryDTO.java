package com.extremis.dto;

import java.io.Serializable;

/**
 * Data Transfer Object representing an individual student's aggregated attendance
 * performance metrics across all conducted academic sessions.
 */
public class StudentAttendanceSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long studentId;
    private String rollNo;
    private String name;
    private String branch;
    private String section;
    private Long classId;
    private String classCode;
    private String className;
    private Long batchId;
    private String batchCode;
    private String batchName;

    private int totalSessions;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private int excusedCount;
    private double attendancePercentage;

    private String lastDate;
    private String lastStatus;
    private String performanceTier; // "SAFE", "WARNING", "CRITICAL"
    private int overriddenCount;

    public StudentAttendanceSummaryDTO() {
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }

    public int getExcusedCount() { return excusedCount; }
    public void setExcusedCount(int excusedCount) { this.excusedCount = excusedCount; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = Math.round(attendancePercentage * 10.0) / 10.0;
    }

    public String getLastDate() { return lastDate != null ? lastDate : "\u2014"; }
    public void setLastDate(String lastDate) { this.lastDate = lastDate; }

    public String getLastStatus() { return lastStatus != null ? lastStatus : "NOT_RECORDED"; }
    public void setLastStatus(String lastStatus) { this.lastStatus = lastStatus; }

    public String getPerformanceTier() { return performanceTier != null ? performanceTier : "SAFE"; }
    public void setPerformanceTier(String performanceTier) { this.performanceTier = performanceTier; }

    public int getOverriddenCount() { return overriddenCount; }
    public void setOverriddenCount(int overriddenCount) { this.overriddenCount = overriddenCount; }

    public String getFormattedPercentage() {
        return String.format("%.1f%%", attendancePercentage);
    }
}
