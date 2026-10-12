<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Extremis - Master Administration Portal</title>
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
                <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="portal-nav-link active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/teachers" class="portal-nav-link">Teachers</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/classes" class="portal-nav-link">Classes &amp; Batches</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/assignments" class="portal-nav-link">Assignments</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/overrides" class="portal-nav-link">Override History</a></li>
            </ul>

            <div class="portal-nav-user">
                <div class="portal-user-badge">
                    <span class="portal-user-name">${admin.name}</span>
                    <span class="portal-user-role">Administrator</span>
                </div>
                <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0;">
                    <button type="submit" class="btn-secondary-action btn-sm-action" title="Logout" style="color: var(--danger-red-start);">
                        Logout
                    </button>
                </form>
            </div>
        </div>
    </nav>

    <div class="app-container" style="max-width: 1200px; padding: 1.5rem 1.25rem 3rem;">

        <!-- Flash messages -->
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert-banner alert-banner-success">
                <span><strong>Success:</strong> ${sessionScope.flashSuccess}</span>
            </div>
            <% session.removeAttribute("flashSuccess"); %>
        </c:if>

        <!-- Admin Welcome Card -->
        <div class="card mb-16" style="background: linear-gradient(135deg, #0F172A 0%, #1E1B4B 100%); color: #FFFFFF; padding: 1.5rem;">
            <span class="status-pill" style="background: rgba(147, 51, 234, 0.35); color: #C084FC; margin-bottom: 6px;">
                Master Administration
            </span>
            <h2 class="font-bold text-2xl text-white" style="color: #FFFFFF; margin-top: 4px;">Welcome, ${admin.name}</h2>
            <p class="text-xs text-muted" style="color: #94A3B8; margin-top: 2px;">Extremis Student Attendance Management System Control Console</p>
        </div>

        <!-- System Statistics Grid -->
        <div class="stats-grid-4 mb-16" style="grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));">
            <div class="stat-tile">
                <div class="stat-val text-blue">${totalStudents}</div>
                <div class="stat-lbl">Students</div>
            </div>
            <div class="stat-tile">
                <div class="stat-val text-green">${totalFaculties}</div>
                <div class="stat-lbl">Teachers</div>
            </div>
            <div class="stat-tile">
                <div class="stat-val text-blue">${totalClasses}</div>
                <div class="stat-lbl">Classes</div>
            </div>
            <div class="stat-tile">
                <div class="stat-val" style="color: #0284C7;">${totalBatches}</div>
                <div class="stat-lbl">Batches</div>
            </div>
            <div class="stat-tile">
                <div class="stat-val" style="color: #7C3AED;">${totalAssignments}</div>
                <div class="stat-lbl">Assignments</div>
            </div>
            <div class="stat-tile">
                <div class="stat-val" style="color: #D97706;">${totalOverrides}</div>
                <div class="stat-lbl">Overrides</div>
            </div>
            <div class="stat-tile stat-present">
                <div class="stat-val text-green">${todayCheckIns}</div>
                <div class="stat-lbl">Today Check-Ins</div>
            </div>
            <div class="stat-tile stat-absent">
                <div class="stat-val text-red">${activeSessions}</div>
                <div class="stat-lbl">Locked Devices</div>
            </div>
        </div>

        <!-- Quick Access Navigation Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1rem; margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/admin/teachers" class="card" style="text-decoration: none; border: 1.5px solid #E2E8F0; transition: transform 0.2s, box-shadow 0.2s; padding: 1.25rem;">
                <div class="flex items-center gap-8 mb-8">
                    <div class="step-icon-circle bg-blue-light text-blue" style="width: 38px; height: 38px; font-size: 1.1rem;">👥</div>
                    <div>
                        <h4 class="font-bold text-dark text-base">Teacher Management</h4>
                        <p class="text-xs text-muted">Add, edit, activate/deactivate teachers</p>
                    </div>
                </div>
                <span class="link-blue text-xs font-semibold">Manage Teachers &rarr;</span>
            </a>

            <a href="${pageContext.request.contextPath}/admin/classes" class="card" style="text-decoration: none; border: 1.5px solid #E2E8F0; transition: transform 0.2s, box-shadow 0.2s; padding: 1.25rem;">
                <div class="flex items-center gap-8 mb-8">
                    <div class="step-icon-circle bg-blue-light text-blue" style="width: 38px; height: 38px; font-size: 1.1rem;">🏫</div>
                    <div>
                        <h4 class="font-bold text-dark text-base">Classes &amp; Batches</h4>
                        <p class="text-xs text-muted">Configure academic sections &amp; batches</p>
                    </div>
                </div>
                <span class="link-blue text-xs font-semibold">Manage Classes &rarr;</span>
            </a>

            <a href="${pageContext.request.contextPath}/admin/assignments" class="card" style="text-decoration: none; border: 1.5px solid #E2E8F0; transition: transform 0.2s, box-shadow 0.2s; padding: 1.25rem;">
                <div class="flex items-center gap-8 mb-8">
                    <div class="step-icon-circle bg-blue-light text-blue" style="width: 38px; height: 38px; font-size: 1.1rem;">🔗</div>
                    <div>
                        <h4 class="font-bold text-dark text-base">Teacher Assignments</h4>
                        <p class="text-xs text-muted">Assign teachers to classes and batches</p>
                    </div>
                </div>
                <span class="link-blue text-xs font-semibold">Assign Teachers &rarr;</span>
            </a>

            <a href="${pageContext.request.contextPath}/admin/overrides" class="card" style="text-decoration: none; border: 1.5px solid #E2E8F0; transition: transform 0.2s, box-shadow 0.2s; padding: 1.25rem;">
                <div class="flex items-center gap-8 mb-8">
                    <div class="step-icon-circle bg-blue-light text-blue" style="width: 38px; height: 38px; font-size: 1.1rem;">📋</div>
                    <div>
                        <h4 class="font-bold text-dark text-base">Override Audit History</h4>
                        <p class="text-xs text-muted">Review immutable attendance corrections</p>
                    </div>
                </div>
                <span class="link-blue text-xs font-semibold">View Audit Trail &rarr;</span>
            </a>
        </div>

        <!-- Student Device Session Manager Card -->
        <div class="portal-table-container">
            <div class="table-header-bar">
                <div>
                    <h3 class="table-title">Student Device Security &amp; Single-Device Locks</h3>
                    <p class="table-subtitle">Enforce single-device attendance policy and unlock student devices when needed</p>
                </div>
                <span class="status-pill pill-neutral font-mono text-xs">Total Students: ${students.size()}</span>
            </div>

            <div class="portal-table-responsive">
                <table class="portal-data-table">
                    <thead>
                        <tr>
                            <th>Student Name</th>
                            <th>Roll Number</th>
                            <th>Branch &amp; Section</th>
                            <th>Device Session Status</th>
                            <th style="text-align: right;">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${students}">
                            <tr>
                                <td>
                                    <div class="flex items-center gap-8">
                                        <div class="step-icon-circle bg-blue-light text-blue" style="width: 30px; height: 30px; font-weight: 700; font-size: 0.8rem;">
                                            ${s.name.substring(0, 1)}
                                        </div>
                                        <span class="font-bold text-dark">${s.name}</span>
                                    </div>
                                </td>
                                <td><span class="font-mono text-muted font-semibold">${s.rollNo}</span></td>
                                <td>${s.branch} - ${s.section}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty s.sessionToken}">
                                            <span class="badge-status badge-absent" style="gap: 4px;">
                                                🔒 Device Locked
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-present">
                                                ✓ Unlocked / Available
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: right;">
                                    <c:if test="${not empty s.sessionToken}">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/dashboard" style="display:inline; margin: 0;">
                                            <input type="hidden" name="action" value="reset_session">
                                            <input type="hidden" name="studentId" value="${s.id}">
                                            <button type="submit" class="btn-sm-action btn-secondary-action" style="color: var(--danger-red-start);">
                                                Reset Device Lock
                                            </button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</body>
</html>
