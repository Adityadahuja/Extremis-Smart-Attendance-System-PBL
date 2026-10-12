<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Extremis - Attendance Calendar</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="mobile-container">

    <!-- Top Header -->
    <header class="page-header">
        <a href="${pageContext.request.contextPath}/report?month=${selectedMonth}" class="btn-icon" aria-label="Back to Report">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
        </a>
        <h1 class="page-title">Attendance Calendar</h1>
        <a href="${pageContext.request.contextPath}/report?month=${selectedMonth}" class="btn-icon" title="View Table Log">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="8" y1="6" x2="21" y2="6"/>
                <line x1="8" y1="12" x2="21" y2="12"/>
                <line x1="8" y1="18" x2="21" y2="18"/>
                <line x1="3" y1="6" x2="3.01" y2="6"/>
                <line x1="3" y1="12" x2="3.01" y2="12"/>
                <line x1="3" y1="18" x2="3.01" y2="18"/>
            </svg>
        </a>
    </header>

    <div class="content-scrollable">

        <!-- Month Switcher Card -->
        <div class="calendar-card">
            <div class="month-header flex-between mb-16">
                <h2 class="font-bold text-dark text-lg">${monthName}</h2>
                <div class="month-nav-btns flex gap-8">
                    <a href="${pageContext.request.contextPath}/calendar?month=2026-09" class="btn-arrow" aria-label="Previous Month">&lsaquo;</a>
                    <a href="${pageContext.request.contextPath}/calendar?month=2026-10" class="btn-arrow" aria-label="Current Month">&bull;</a>
                    <a href="${pageContext.request.contextPath}/calendar?month=2026-11" class="btn-arrow" aria-label="Next Month">&rsaquo;</a>
                </div>
            </div>

            <!-- Day of Week Headers -->
            <div class="calendar-grid days-header mb-8">
                <div class="day-name">Sun</div>
                <div class="day-name">Mon</div>
                <div class="day-name">Tue</div>
                <div class="day-name">Wed</div>
                <div class="day-name">Thu</div>
                <div class="day-name">Fri</div>
                <div class="day-name">Sat</div>
            </div>

            <!-- Calendar Cells -->
            <div class="calendar-grid cells-body">
                <!-- Blank days before start of month -->
                <c:forEach begin="1" end="${blankDaysBefore}" var="b">
                    <div class="cal-cell cell-empty"></div>
                </c:forEach>

                <!-- Month days -->
                <c:forEach var="cell" items="${calendarCells}">
                    <c:choose>
                        <c:when test="${cell.today}">
                            <div class="cal-cell cell-today" title="Today">
                                <span class="cell-day">${cell.dayNumber}</span>
                            </div>
                        </c:when>
                        <c:when test="${cell.status == 'PRESENT'}">
                            <div class="cal-cell cell-present" title="Present">
                                <span class="cell-day">${cell.dayNumber}</span>
                            </div>
                        </c:when>
                        <c:when test="${cell.status == 'ABSENT'}">
                            <div class="cal-cell cell-absent" title="Absent">
                                <span class="cell-day">${cell.dayNumber}</span>
                            </div>
                        </c:when>
                        <c:when test="${cell.status == 'OD'}">
                            <div class="cal-cell cell-od" title="On Duty">
                                <span class="cell-day">${cell.dayNumber}</span>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="cal-cell cell-not-marked" title="Not Marked">
                                <span class="cell-day">${cell.dayNumber}</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </div>

        <!-- Legend Card -->
        <div class="card legend-card mt-16">
            <h3 class="font-bold text-dark text-sm mb-12">Legend</h3>
            <div class="legend-grid">
                <div class="legend-item">
                    <span class="legend-dot dot-present"></span>
                    <span class="text-sm">Present</span>
                </div>
                <div class="legend-item">
                    <span class="legend-dot dot-absent"></span>
                    <span class="text-sm">Absent</span>
                </div>
                <div class="legend-item">
                    <span class="legend-dot dot-today"></span>
                    <span class="text-sm">Today</span>
                </div>
                <div class="legend-item">
                    <span class="legend-dot dot-not-marked"></span>
                    <span class="text-sm">Not Marked</span>
                </div>
            </div>
        </div>

    </div>

    <!-- Bottom Navigation -->
    <jsp:include page="/WEB-INF/views/includes/nav.jsp" />

</div>
</body>
</html>
