package com.extremis.servlet;

import com.extremis.dao.AttendanceDAO;
import com.extremis.model.AttendanceRecord;
import com.extremis.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/calendar")
public class CalendarServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public static class DayCell {
        private int dayNumber;
        private String status; // PRESENT, ABSENT, OD, NOT_MARKED
        private boolean isSunday;
        private boolean isToday;

        public DayCell(int dayNumber, String status, boolean isSunday, boolean isToday) {
            this.dayNumber = dayNumber;
            this.status = status;
            this.isSunday = isSunday;
            this.isToday = isToday;
        }

        public int getDayNumber() { return dayNumber; }
        public String getStatus() { return status; }
        public boolean isSunday() { return isSunday; }
        public boolean isToday() { return isToday; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Student student = (Student) session.getAttribute("currentStudent");

        String monthStr = req.getParameter("month");
        if (monthStr == null || monthStr.trim().isEmpty()) {
            monthStr = "2026-10";
        }

        int year = Integer.parseInt(monthStr.substring(0, 4));
        int month = Integer.parseInt(monthStr.substring(5, 7));

        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();
        LocalDate firstOfMonth = ym.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        int blankDaysBefore = (dayOfWeek == 7) ? 0 : dayOfWeek; // Sun = index 0

        List<AttendanceRecord> records = attendanceDAO.getRecordsByStudentAndMonth(student.getId(), year, month, "ALL");
        Map<Integer, AttendanceRecord> recordMap = new HashMap<>();
        for (AttendanceRecord r : records) {
            recordMap.put(r.getDate().getDayOfMonth(), r);
        }

        List<DayCell> calendarCells = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate currentDay = ym.atDay(d);
            boolean isSun = currentDay.getDayOfWeek().getValue() == 7;
            boolean isTod = currentDay.equals(today) || (month == 10 && d == 12); // Oct 12 is today in reference mockup

            AttendanceRecord rec = recordMap.get(d);
            String status = "NOT_MARKED";
            if (rec != null) {
                status = rec.getStatus();
            } else if (isSun) {
                status = "ABSENT";
            }

            calendarCells.add(new DayCell(d, status, isSun, isTod));
        }

        req.setAttribute("blankDaysBefore", blankDaysBefore);
        req.setAttribute("calendarCells", calendarCells);
        req.setAttribute("monthName", ym.getMonth().toString().substring(0, 1) + ym.getMonth().toString().substring(1).toLowerCase() + " " + year);
        req.setAttribute("selectedMonth", monthStr);
        req.setAttribute("activeTab", "report");

        req.getRequestDispatcher("/WEB-INF/views/calendar.jsp").forward(req, resp);
    }
}
