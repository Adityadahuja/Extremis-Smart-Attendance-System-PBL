<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>Notifications - Extremis</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <style>
        .notice-card {
            background: #FFFFFF;
            border-radius: var(--card-radius);
            border: 1px solid var(--border-color);
            padding: 1rem;
            margin-bottom: 0.75rem;
            box-shadow: var(--shadow-soft);
        }
        .notice-card.unread {
            border-left: 4px solid var(--primary-blue-start);
            background: #F8FAFF;
        }
        .notice-card-inner {
            display: flex;
            align-items: flex-start;
            gap: 0.85rem;
        }
        .notice-icon-box {
            width: 40px;
            height: 40px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }
        .icon-box-red   { background: #FEE2E2; color: #EF4444; }
        .icon-box-blue  { background: #EFF6FF; color: #1D4ED8; }
        .icon-box-green { background: #DCFCE7; color: #16A34A; }
        .notice-top-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0.35rem;
        }
        .tag-pill {
            font-size: 0.72rem;
            font-weight: 700;
            padding: 2px 8px;
            border-radius: 999px;
        }
        .tag-red   { background: #FEE2E2; color: #EF4444; }
        .tag-blue  { background: #EFF6FF; color: #1D4ED8; }
        .tag-green { background: #DCFCE7; color: #16A34A; }
    </style>
</head>
<body>
<div class="app-container">

    <div class="content-wrap">
        <!-- Header with Back Arrow (Screen 2 Mockup) -->
        <header class="app-header">
            <div style="display: flex; align-items: center; gap: 0.85rem;">
                <a href="${pageContext.request.contextPath}/dashboard" class="header-action-btn" style="width: 32px; height: 32px;">
                    <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="19" y1="12" x2="5" y2="12"></line>
                        <polyline points="12 19 5 12 12 5"></polyline>
                    </svg>
                </a>
                <div style="font-size: 1.15rem; font-weight: 900; color: var(--text-main);">Notifications</div>
            </div>
        </header>

        <!-- Notices List -->
        <div>
            <c:forEach var="n" items="${notices}">
                <div class="notice-card ${!n.read ? 'unread' : ''}">
                    <div class="notice-card-inner">
                        <!-- Icon Box based on Category -->
                        <div class="notice-icon-box ${n.category == 'IMPORTANT' ? 'icon-box-red' : (n.category == 'ACADEMIC' ? 'icon-box-green' : 'icon-box-blue')}">
                            <c:choose>
                                <c:when test="${n.category == 'IMPORTANT'}">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                        <polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon>
                                        <path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path>
                                        <path d="M19.07 4.93a10 10 0 0 1 0 14.14"></path>
                                    </svg>
                                </c:when>
                                <c:when test="${n.category == 'ACADEMIC'}">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                    </svg>
                                </c:when>
                                <c:otherwise>
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                        <circle cx="12" cy="12" r="10"></circle>
                                        <line x1="12" y1="16" x2="12" y2="12"></line>
                                        <line x1="12" y1="8" x2="12.01" y2="8"></line>
                                    </svg>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Body -->
                        <div style="flex: 1; min-width: 0;">
                            <div class="notice-top-row">
                                <span class="tag-pill ${n.category == 'IMPORTANT' ? 'tag-red' : (n.category == 'ACADEMIC' ? 'tag-green' : 'tag-blue')}">
                                    ${n.category == 'IMPORTANT' ? 'Important' : (n.category == 'ACADEMIC' ? 'Academic' : 'General')}
                                </span>
                                <span style="font-size: 0.72rem; color: var(--text-muted); font-weight: 500;">
                                    ${n.formattedDate}
                                </span>
                            </div>
                            <div style="font-size: 0.82rem; color: var(--text-main); font-weight: 600; line-height: 1.45; white-space: pre-line;">
                                ${n.message}
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

    </div>

    <!-- Shared Bottom Nav Include -->
    <jsp:include page="/WEB-INF/views/includes/nav.jsp"/>

</div>
</body>
</html>
