package com.extremis.model;

import java.io.Serializable;

/**
 * Model representing an immutable audit log record for a faculty attendance override.
 */
public class AttendanceOverrideAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long attendanceId;
    private Long studentId;
    private Long facultyId;
    private String originalStatus;
    private String updatedStatus;
    private String reason;
    private String attendanceDate;
    private String overrideTime;

    // Joined helper fields
    private String studentName;
    private String studentRollNo;
    private String facultyName;
    private String facultyCode;

    public AttendanceOverrideAudit() {
    }

    public AttendanceOverrideAudit(Long id, Long attendanceId, Long studentId, Long facultyId,
                                  String originalStatus, String updatedStatus, String reason,
                                  String attendanceDate, String overrideTime) {
        this.id = id;
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.facultyId = facultyId;
        this.originalStatus = originalStatus;
        this.updatedStatus = updatedStatus;
        this.reason = reason;
        this.attendanceDate = attendanceDate;
        this.overrideTime = overrideTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getFacultyId() { return facultyId; }
    public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }

    public String getOriginalStatus() { return originalStatus; }
    public void setOriginalStatus(String originalStatus) { this.originalStatus = originalStatus; }

    public String getUpdatedStatus() { return updatedStatus; }
    public void setUpdatedStatus(String updatedStatus) { this.updatedStatus = updatedStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(String attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getOverrideTime() { return overrideTime; }
    public void setOverrideTime(String overrideTime) { this.overrideTime = overrideTime; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public String getStudentRoll() { return studentRollNo; }
    public void setStudentRoll(String studentRoll) { this.studentRollNo = studentRoll; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public String getFacultyCode() { return facultyCode; }
    public void setFacultyCode(String facultyCode) { this.facultyCode = facultyCode; }
}
