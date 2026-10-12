package com.extremis.dto;

import java.io.Serializable;

/**
 * Data point for daily attendance trend charting.
 */
public class DailyTrendDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private int totalEnrolled;
    private int presentCount;
    private int lateCount;
    private int absentCount;
    private int excusedCount;
    private double attendancePercentage;

    public DailyTrendDTO() {
    }

    public DailyTrendDTO(String date, int totalEnrolled, int presentCount, int lateCount, int absentCount, int excusedCount) {
        this.date = date;
        this.totalEnrolled = totalEnrolled;
        this.presentCount = presentCount;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.excusedCount = excusedCount;
        int attended = presentCount + lateCount;
        int denom = totalEnrolled > 0 ? totalEnrolled : 1;
        this.attendancePercentage = Math.round((attended * 100.0 / denom) * 10.0) / 10.0;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTotalEnrolled() { return totalEnrolled; }
    public void setTotalEnrolled(int totalEnrolled) { this.totalEnrolled = totalEnrolled; }

    public int getPresentCount() { return presentCount; }
    public void setPresentCount(int presentCount) { this.presentCount = presentCount; }

    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }

    public int getAbsentCount() { return absentCount; }
    public void setAbsentCount(int absentCount) { this.absentCount = absentCount; }

    public int getExcusedCount() { return excusedCount; }
    public void setExcusedCount(int excusedCount) { this.excusedCount = excusedCount; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
}
