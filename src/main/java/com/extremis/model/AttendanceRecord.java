package com.extremis.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing an Attendance Record entity.
 */
public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long studentId;
    private LocalDate date;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Double checkinLat;
    private Double checkinLng;
    private Double checkinAccuracy;
    private String checkinPhotoPath;
    private Double checkoutLat;
    private Double checkoutLng;
    private Double checkoutPhotoPath;
    private String status; // PRESENT, ABSENT, LATE, EXCUSED, OD
    private boolean isOverride;
    private String overrideReason;
    private String overriddenBy;
    private String studentName;
    private String studentRoll;
    private String branch;
    private String section;
    private String batchName;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM");

    public AttendanceRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Double getCheckinLat() {
        return checkinLat;
    }

    public void setCheckinLat(Double checkinLat) {
        this.checkinLat = checkinLat;
    }

    public Double getCheckinLng() {
        return checkinLng;
    }

    public void setCheckinLng(Double checkinLng) {
        this.checkinLng = checkinLng;
    }

    public Double getCheckinAccuracy() {
        return checkinAccuracy;
    }

    public void setCheckinAccuracy(Double checkinAccuracy) {
        this.checkinAccuracy = checkinAccuracy;
    }

    public String getCheckinPhotoPath() {
        return checkinPhotoPath;
    }

    public void setCheckinPhotoPath(String checkinPhotoPath) {
        this.checkinPhotoPath = checkinPhotoPath;
    }

    public Double getCheckoutLat() {
        return checkoutLat;
    }

    public void setCheckoutLat(Double checkoutLat) {
        this.checkoutLat = checkoutLat;
    }

    public Double getCheckoutLng() {
        return checkoutLng;
    }

    public void setCheckoutLng(Double checkoutLng) {
        this.checkoutLng = checkoutLng;
    }

    public Double getCheckoutPhotoPath() {
        return checkoutPhotoPath;
    }

    public void setCheckoutPhotoPath(Double checkoutPhotoPath) {
        this.checkoutPhotoPath = checkoutPhotoPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Convenience formatting methods for JSP EL
    public String getFormattedDate() {
        return date != null ? date.format(DATE_FMT) : "\u2014";
    }

    public String getDateFormatted() {
        return getFormattedDate();
    }

    public String getFormattedCheckInTime() {
        return checkInTime != null ? checkInTime.format(TIME_FMT) : "\u2014";
    }

    public String getCheckInFormatted() {
        return getFormattedCheckInTime();
    }

    public String getFormattedCheckOutTime() {
        return checkOutTime != null ? checkOutTime.format(TIME_FMT) : "\u2014";
    }

    public String getCheckOutFormatted() {
        return getFormattedCheckOutTime();
    }

    public boolean isOverride() { return isOverride; }
    public void setOverride(boolean override) { isOverride = override; }

    public String getOverrideReason() { return overrideReason; }
    public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }

    public String getOverriddenBy() { return overriddenBy; }
    public void setOverriddenBy(String overriddenBy) { this.overriddenBy = overriddenBy; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRoll() { return studentRoll; }
    public void setStudentRoll(String studentRoll) { this.studentRoll = studentRoll; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
}
