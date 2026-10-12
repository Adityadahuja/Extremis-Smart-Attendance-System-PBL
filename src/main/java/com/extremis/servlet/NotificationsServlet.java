package com.extremis.servlet;

import com.extremis.dao.NoticeDAO;
import com.extremis.model.Notice;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {

    private final NoticeDAO noticeDAO = new NoticeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Notice> notices = noticeDAO.getAllNotices();
        req.setAttribute("notices", notices);
        req.setAttribute("activeTab", "home");
        req.getRequestDispatcher("/WEB-INF/views/notifications.jsp").forward(req, resp);
    }
}
