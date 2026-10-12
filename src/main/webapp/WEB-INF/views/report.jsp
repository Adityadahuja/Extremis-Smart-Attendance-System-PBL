<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Extremis - Attendance Report</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="mobile-container">

    <!-- Top Header -->
    <header class="page-header">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn-icon" aria-label="Back to Dashboard">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
        </a>
        <h1 class="page-title">Attendance Report</h1>
        <a href="${pageContext.request.contextPath}/calendar?month=${selectedMonth}" class="btn-icon" title="View Calendar Grid">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
        </a>
    </header>

    <div class="content-scrollable">

        <!-- Filter Form -->
        <form method="get" action="${pageContext.request.contextPath}/report" class="filter-card">
            <div class="form-row">
                <div class="form-group flex-1">
                    <label class="form-label" for="monthSelect">Month</label>
                    <select id="monthSelect" name="month" class="form-select" onchange="this.form.submit()">
                        <option value="2026-10" ${selectedMonth == '2026-10' ? 'selected' : ''}>October 2026</option>
                        <option value="2026-09" ${selectedMonth == '2026-09' ? 'selected' : ''}>September 2026</option>
                        <option value="2026-08" ${selectedMonth == '2026-08' ? 'selected' : ''}>August 2026</option>
                    </select>
                </div>
                <div class="form-group flex-1">
                    <label class="form-label" for="filterSelect">Status</label>
                    <select id="filterSelect" name="filter" class="form-select" onchange="this.form.submit()">
                        <option value="ALL" ${selectedFilter == 'ALL' ? 'selected' : ''}>All</option>
                        <option value="PRESENT" ${selectedFilter == 'PRESENT' ? 'selected' : ''}>Present</option>
                        <option value="ABSENT" ${selectedFilter == 'ABSENT' ? 'selected' : ''}>Absent</option>
                        <option value="OD" ${selectedFilter == 'OD' ? 'selected' : ''}>On Duty (OD)</option>
                    </select>
                </div>
            </div>
        </form>

        <!-- Summary Stats 4-Grid Tiles -->
        <div class="stats-grid-4">
            <div class="stat-tile stat-present">
                <div class="stat-val">${presentCount}</div>
                <div class="stat-lbl">Present</div>
            </div>
            <div class="stat-tile stat-absent">
                <div class="stat-val">${absentCount}</div>
                <div class="stat-lbl">Absent</div>
            </div>
            <div class="stat-tile stat-od">
                <div class="stat-val">${odCount}</div>
                <div class="stat-lbl">OD</div>
            </div>
            <div class="stat-tile stat-total">
                <div class="stat-val">${totalCount}</div>
                <div class="stat-lbl">Total</div>
            </div>
        </div>

        <!-- Table View with horizontal scroll -->
        <div class="table-card">
            <div class="card-section-header flex-between mb-8">
                <span class="font-bold text-dark">Daily Log</span>
                <a href="${pageContext.request.contextPath}/calendar?month=${selectedMonth}" class="link-blue text-sm">Switch to Calendar &rarr;</a>
            </div>
            <div class="table-responsive">
                <table class="report-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Check In</th>
                            <th>Check Out</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty records}">
                                <tr>
                                    <td colspan="4" class="text-center py-16 text-muted">No attendance records found for this period.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="r" items="${records}">
                                    <tr>
                                        <td class="font-semibold text-dark">${r.date}</td>
                                        <td>${r.checkInFormatted}</td>
                                        <td>${r.checkOutFormatted}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.status == 'PRESENT'}">
                                                    <span class="status-pill pill-present">Present</span>
                                                </c:when>
                                                <c:when test="${r.status == 'LATE'}">
                                                    <span class="status-pill" style="background:#FFFBEB;color:#D97706;border:1px solid #FDE68A;">Late</span>
                                                </c:when>
                                                <c:when test="${r.status == 'EXCUSED'}">
                                                    <span class="status-pill" style="background:#EFF6FF;color:#2563EB;border:1px solid #BFDBFE;">Excused</span>
                                                </c:when>
                                                <c:when test="${r.status == 'ABSENT'}">
                                                    <span class="status-pill pill-absent">Absent</span>
                                                </c:when>
                                                <c:when test="${r.status == 'OD'}">
                                                    <span class="status-pill pill-od">OD</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-pill pill-neutral">${r.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

    <!-- Bottom Navigation -->
    <jsp:include page="/WEB-INF/views/includes/nav.jsp" />

</div>
</body>
</html>
