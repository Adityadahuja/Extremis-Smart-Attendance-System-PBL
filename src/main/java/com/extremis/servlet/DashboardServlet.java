package com.extremis.servlet;

import com.extremis.dao.NoticeDAO;
import com.extremis.model.AttendanceRecord;
import com.extremis.model.Notice;
import com.extremis.model.Student;
import com.extremis.service.AttendanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalTime;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final AttendanceService attendanceService = new AttendanceService();
    private final NoticeDAO noticeDAO = new NoticeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Student student = (Student) session.getAttribute("currentStudent");

        AttendanceRecord todayAttendance = attendanceService.getTodayAttendance(student.getId());
        Notice latestImportant = noticeDAO.getLatestImportantNotice();
        boolean hasUnread = noticeDAO.hasUnreadNotices();
        int overallPct = attendanceService.getOverallAttendancePercentage(student.getId());

        LocalTime serverTime = attendanceService.getEffectiveTime();
        boolean isEvening = !serverTime.isBefore(AttendanceService.CHECKOUT_START);

        // Calculate button state
        boolean canCheckIn = attendanceService.isCheckInAllowed(student.getId());
        boolean canCheckOut = attendanceService.isCheckOutAllowed(student.getId());

        String buttonLabel = "Check In";
        String buttonColor = "blue";
        String buttonSubtext = "Available 8:50 AM – 9:05 AM";
        boolean buttonEnabled = false;
        String actionUrl = req.getContextPath() + "/checkin";

        if (todayAttendance != null && todayAttendance.getCheckOutTime() != null) {
            buttonLabel = "Checked Out";
            buttonColor = "gray";
            buttonSubtext = "Departed at " + todayAttendance.getFormattedCheckOutTime();
            buttonEnabled = false;
        } else if (isEvening) {
            buttonLabel = "Check Out";
            actionUrl = req.getContextPath() + "/checkout";
            if (canCheckOut) {
                buttonColor = "red";
                buttonSubtext = "Evening departure available";
                buttonEnabled = true;
            } else {
                buttonColor = "gray";
                buttonSubtext = (todayAttendance == null) ? "Cannot check out: Not checked in today" : "Check Out window opens after 3:40 PM";
                buttonEnabled = false;
            }
        } else if (todayAttendance != null && todayAttendance.getCheckInTime() != null) {
            buttonLabel = "Checked In";
            buttonColor = "gray";
            buttonSubtext = "Checked in at " + todayAttendance.getFormattedCheckInTime();
            buttonEnabled = false;
        } else if (serverTime.isBefore(AttendanceService.CHECKIN_START)) {
            buttonLabel = "Check In";
            buttonColor = "gray";
            buttonSubtext = "Opens at 8:50 AM";
            buttonEnabled = false;
        } else if (canCheckIn) {
            buttonLabel = "Check In";
            buttonColor = "blue";
            buttonSubtext = "Available 8:50 AM – 9:05 AM";
            buttonEnabled = true;
        } else {
            buttonLabel = "Check-in Closed";
            buttonColor = "gray";
            buttonSubtext = "Check In window closed";
            buttonEnabled = false;
        }

        req.setAttribute("todayAttendance", todayAttendance);
        req.setAttribute("latestNotice", latestImportant);
        req.setAttribute("hasUnreadNotices", hasUnread);
        req.setAttribute("overallPercentage", overallPct);
        req.setAttribute("serverTime", serverTime.toString().substring(0, 5));
        req.setAttribute("buttonLabel", buttonLabel);
        req.setAttribute("buttonColor", buttonColor);
        req.setAttribute("buttonSubtext", buttonSubtext);
        req.setAttribute("buttonEnabled", buttonEnabled);
        req.setAttribute("actionUrl", actionUrl);
        req.setAttribute("activeTab", "home");

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
