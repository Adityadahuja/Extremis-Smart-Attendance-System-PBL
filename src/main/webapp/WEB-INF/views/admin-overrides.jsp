<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Extremis - Attendance Override Audit History</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Top Navigation Bar -->
    <nav class="portal-navbar">
        <div class="portal-navbar-inner">
            <div class="portal-brand">
                <div class="portal-brand-logo">E</div>
                <div>
                    <div class="portal-brand-title">Extremis</div>
                    <div class="portal-brand-sub">Admin Control Center</div>
                </div>
            </div>

            <ul class="portal-nav-links">
                <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="portal-nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/teachers" class="portal-nav-link">Teachers</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/classes" class="portal-nav-link">Classes &amp; Batches</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/assignments" class="portal-nav-link">Assignments</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/overrides" class="portal-nav-link active">Override History</a></li>
            </ul>

            <div class="portal-nav-user">
                <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0;">
                    <button type="submit" class="btn-secondary-action btn-sm-action" title="Logout" style="color: var(--danger-red-start);">
                        Logout
                    </button>
                </form>
            </div>
        </div>
    </nav>

    <div class="app-container" style="max-width: 1200px; padding: 1.5rem 1.25rem 3rem;">

        <div class="mb-16">
            <h2 class="font-bold text-2xl text-dark">Immutable Attendance Override Audit History</h2>
            <p class="text-xs text-muted">Complete audit trail of every manual attendance correction, recording author, reason, and status transitions</p>
        </div>

        <!-- Audit Table -->
        <div class="portal-table-container">
            <div class="table-header-bar">
                <div>
                    <h3 class="table-title">Audit Trail Records</h3>
                    <p class="table-subtitle">Permanent, tamper-evident log of faculty attendance adjustments</p>
                </div>
                <span class="status-pill pill-neutral font-mono text-xs">Logged Events: ${auditLogs.size()}</span>
            </div>

            <div class="portal-table-responsive">
                <table class="portal-data-table">
                    <thead>
                        <tr>
                            <th>Log ID</th>
                            <th>Student</th>
                            <th>Attendance Date</th>
                            <th>Original Status</th>
                            <th>Updated Status</th>
                            <th>Faculty Author</th>
                            <th>Override Timestamp</th>
                            <th>Mandatory Reason</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty auditLogs}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                                        No attendance overrides have been logged yet. All attendance records are currently automatic check-ins.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${auditLogs}">
                                    <tr>
                                        <td><span class="font-mono text-muted text-xs">#${log.id}</span></td>
                                        <td>
                                            <div class="flex items-center gap-8">
                                                <div class="step-icon-circle bg-blue-light text-blue" style="width: 30px; height: 30px; font-weight: 700; font-size: 0.78rem;">
                                                    ${log.studentName.substring(0, 1)}
                                                </div>
                                                <div>
                                                    <span class="font-bold text-dark">${log.studentName}</span>
                                                    <span class="text-xs text-muted font-mono" style="display:block;">${log.studentRoll}</span>
                                                </div>
                                            </div>
                                        </td>
                                        <td><span class="font-mono text-dark font-semibold">${log.attendanceDate}</span></td>
                                        <td>
                                            <span class="badge-status badge-absent" style="opacity: 0.85;">${log.originalStatus}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${log.updatedStatus == 'PRESENT'}">
                                                    <span class="badge-status badge-present">&#x2713; ${log.updatedStatus}</span>
                                                </c:when>
                                                <c:when test="${log.updatedStatus == 'LATE'}">
                                                    <span class="badge-status badge-late">&#x23F1; ${log.updatedStatus}</span>
                                                </c:when>
                                                <c:when test="${log.updatedStatus == 'EXCUSED'}">
                                                    <span class="badge-status badge-excused">&#x1F4CB; ${log.updatedStatus}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-status badge-absent">${log.updatedStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="font-bold text-dark">${log.facultyName != null ? log.facultyName : 'Authorized Faculty'}</span>
                                            <c:if test="${not empty log.facultyCode}">
                                                <span class="text-xs text-muted font-mono" style="display:block;">ID: ${log.facultyCode}</span>
                                            </c:if>
                                        </td>
                                        <td><span class="text-xs text-muted font-mono">${log.overrideTime}</span></td>
                                        <td>
                                            <div style="background: #F8FAFC; border: 1px solid #E2E8F0; border-radius: 6px; padding: 0.35rem 0.65rem; max-width: 280px; font-size: 0.78rem; color: #334155;">
                                                "${log.reason}"
                                            </div>
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
</body>
</html>
