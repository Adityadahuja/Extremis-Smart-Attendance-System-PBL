package com.extremis.dto;

import java.io.Serializable;

/**
 * Data Transfer Object representing a single date/session attendance record for a student,
 * used in the detailed student attendance history drawer/modal.
 */
public class StudentSessionHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long attendanceId;
    private Long studentId;
    private String date;
    private String subject;
    private String sessionTime;
    private String status; // "PRESENT", "LATE", "ABSENT", "EXCUSED", "OD", "NOT_RECORDED"
    private String checkInTime;
    private String checkOutTime;
    private boolean isOverride;
    private String overrideReason;
    private String overriddenBy;
    private String recordingMethod; // "Automatic GPS Check-In", "Faculty Override", "System Recorded"
    private String gpsCoordinates;
    private String photoPath;

    public StudentSessionHistoryDTO() {
    }

    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSubject() { return subject != null ? subject : "General Attendance"; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSessionTime() { return sessionTime != null ? sessionTime : "09:00 AM - 10:00 AM"; }
    public void setSessionTime(String sessionTime) { this.sessionTime = sessionTime; }

    public String getStatus() { return status != null ? status : "NOT_RECORDED"; }
    public void setStatus(String status) { this.status = status; }

    public String getCheckInTime() { return checkInTime != null ? checkInTime : "\u2014"; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime != null ? checkOutTime : "\u2014"; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public boolean isOverride() { return isOverride; }
    public void setOverride(boolean override) { isOverride = override; }

    public String getOverrideReason() { return overrideReason; }
    public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }

    public String getOverriddenBy() { return overriddenBy; }
    public void setOverriddenBy(String overriddenBy) { this.overriddenBy = overriddenBy; }

    public String getRecordingMethod() { return recordingMethod; }
    public void setRecordingMethod(String recordingMethod) { this.recordingMethod = recordingMethod; }

    public String getGpsCoordinates() { return gpsCoordinates; }
    public void setGpsCoordinates(String gpsCoordinates) { this.gpsCoordinates = gpsCoordinates; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
