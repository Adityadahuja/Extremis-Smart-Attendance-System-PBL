<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Extremis - Student Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="mobile-container">

    <!-- Top Header -->
    <header class="page-header">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn-icon" aria-label="Back">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
        </a>
        <h1 class="page-title">My Profile</h1>
        <div style="width: 40px;"></div>
    </header>

    <div class="content-scrollable">

        <!-- Profile Hero Card -->
        <div class="card profile-hero-card text-center mb-16">
            <div class="profile-hero-avatar-wrap">
                <div class="profile-hero-avatar">
                    ${student.name.substring(0, 1)}
                </div>
                <div class="avatar-verified-badge" title="Verified Student">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                </div>
            </div>
            <h2 class="profile-hero-name font-bold text-dark text-lg mt-8">${student.name}</h2>
            <p class="text-sm text-blue font-semibold mt-2">${student.rollNo}</p>
            <p class="text-xs text-muted mt-2">${student.branch} &bull; ${student.section}</p>
        </div>

        <!-- Academic & Personal Details Card -->
        <div class="card details-card mb-20">
            <h3 class="font-bold text-dark text-sm mb-12 uppercase tracking-wider text-muted">Academic Details</h3>
            
            <div class="detail-row">
                <span class="detail-label">Full Name</span>
                <span class="detail-value">${student.name}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">College Email</span>
                <span class="detail-value text-break">${student.email}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">RTU Roll Number</span>
                <span class="detail-value font-mono font-bold">${student.rollNo}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Branch</span>
                <span class="detail-value">${student.branch}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Section</span>
                <span class="detail-value">${student.section}</span>
            </div>
        </div>

        <!-- App Information Card -->
        <div class="card details-card mb-20">
            <h3 class="font-bold text-dark text-sm mb-12 uppercase tracking-wider text-muted">System Status</h3>
            <div class="detail-row">
                <span class="detail-label">Single Device Session</span>
                <span class="status-pill pill-present">Active & Secured</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">App Version</span>
                <span class="detail-value">Extremis v2.4 (Java Servlets)</span>
            </div>
        </div>

        <!-- Logout Action Button -->
        <form action="${pageContext.request.contextPath}/logout" method="post" class="mb-24">
            <button type="submit" class="btn btn-outline-danger w-full flex items-center justify-center gap-8">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                    <polyline points="16 17 21 12 16 7"/>
                    <line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                <span>Log Out of Account</span>
            </button>
        </form>

    </div>

    <!-- Bottom Navigation -->
    <jsp:include page="/WEB-INF/views/includes/nav.jsp" />

</div>
</body>
</html>
