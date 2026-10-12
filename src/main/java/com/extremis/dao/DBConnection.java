package com.extremis.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * SQLite Database Connection and Schema Initializer.
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:sqlite:extremis.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initSchema();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public static void main(String[] args) {
        initSchema();
        System.out.println("SCHEMA_INITIALIZED_SUCCESS");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initializes database tables and seed data if not already present.
     */
    public static synchronized void initSchema() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Students Table
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "roll_no TEXT UNIQUE NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "branch TEXT NOT NULL, " +
                    "section TEXT NOT NULL, " +
                    "password_hash TEXT NOT NULL, " +
                    "session_token TEXT, " +
                    "permissions_accepted INTEGER DEFAULT 0)");

            // 2. Attendance Table
            stmt.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "check_in_time TEXT, " +
                    "check_out_time TEXT, " +
                    "checkin_lat REAL, " +
                    "checkin_lng REAL, " +
                    "checkin_accuracy REAL, " +
                    "checkin_photo_path TEXT, " +
                    "checkout_lat REAL, " +
                    "checkout_lng REAL, " +
                    "checkout_photo_path TEXT, " +
                    "status TEXT NOT NULL, " +
                    "UNIQUE(student_id, date), " +
                    "FOREIGN KEY(student_id) REFERENCES students(id))");

            // 3. Notices Table
            stmt.execute("CREATE TABLE IF NOT EXISTS notices (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category TEXT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "created_at TEXT NOT NULL, " +
                    "is_read INTEGER DEFAULT 0)");

            // 4. Faculties Table
            stmt.execute("CREATE TABLE IF NOT EXISTS faculties (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "faculty_id TEXT UNIQUE NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "branch TEXT NOT NULL, " +
                    "section TEXT NOT NULL, " +
                    "password_hash TEXT NOT NULL, " +
                    "session_token TEXT)");

            // 5. Admins Table
            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "password_hash TEXT NOT NULL, " +
                    "session_token TEXT)");

            // 6. Classes Table
            stmt.execute("CREATE TABLE IF NOT EXISTS classes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "code TEXT UNIQUE NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "department TEXT NOT NULL, " +
                    "semester TEXT, " +
                    "is_active INTEGER DEFAULT 1)");

            // 7. Batches Table
            stmt.execute("CREATE TABLE IF NOT EXISTS batches (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "class_id INTEGER NOT NULL, " +
                    "code TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "is_active INTEGER DEFAULT 1, " +
                    "UNIQUE(class_id, code), " +
                    "FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE)");

            // 8. Teacher Assignments Table
            stmt.execute("CREATE TABLE IF NOT EXISTS teacher_assignments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "faculty_id INTEGER NOT NULL, " +
                    "class_id INTEGER NOT NULL, " +
                    "batch_id INTEGER, " +
                    "subject TEXT, " +
                    "created_at TEXT NOT NULL, " +
                    "FOREIGN KEY(faculty_id) REFERENCES faculties(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY(class_id) REFERENCES classes(id) ON DELETE CASCADE)");

            // 9. Attendance Override Audit Table (Immutable History)
            stmt.execute("CREATE TABLE IF NOT EXISTS attendance_overrides (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "attendance_id INTEGER, " +
                    "student_id INTEGER NOT NULL, " +
                    "faculty_id INTEGER NOT NULL, " +
                    "original_status TEXT NOT NULL, " +
                    "updated_status TEXT NOT NULL, " +
                    "reason TEXT NOT NULL, " +
                    "attendance_date TEXT NOT NULL, " +
                    "override_time TEXT NOT NULL, " +
                    "FOREIGN KEY(student_id) REFERENCES students(id), " +
                    "FOREIGN KEY(faculty_id) REFERENCES faculties(id))");

            try { stmt.execute("ALTER TABLE notices ADD COLUMN is_read INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE students ADD COLUMN permissions_accepted INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE students ADD COLUMN class_id INTEGER"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE students ADD COLUMN batch_id INTEGER"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE faculties ADD COLUMN is_active INTEGER DEFAULT 1"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE faculties ADD COLUMN department TEXT"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN is_override INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN override_reason TEXT"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN overridden_by TEXT"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkin_lat REAL"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkin_lng REAL"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkin_accuracy REAL"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkin_photo_path TEXT"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkout_lat REAL"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkout_lng REAL"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE attendance ADD COLUMN checkout_photo_path TEXT"); } catch (SQLException ignored) {}

            // Seed sample classes and batches
            seedClassesAndBatches(conn);

            // Seed sample students
            seedStudents(conn);

            // Seed sample faculties
            ResultSet rsF = stmt.executeQuery("SELECT COUNT(*) FROM faculties");
            if (rsF.next() && rsF.getInt(1) == 0) {
                seedFaculties(conn);
            }

            // Seed sample teacher assignments
            seedTeacherAssignments(conn);

            // Seed sample admins
            ResultSet rsAdm = stmt.executeQuery("SELECT COUNT(*) FROM admins");
            if (rsAdm.next() && rsAdm.getInt(1) == 0) {
                seedAdmins(conn);
            }

            // Seed sample notices if notices table is empty
            ResultSet rsN = stmt.executeQuery("SELECT COUNT(*) FROM notices");
            if (rsN.next() && rsN.getInt(1) == 0) {
                seedNotices(conn);
            }

            // Seed sample attendance
            normalizeAndSeedHistoricalAttendance(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void normalizeAndSeedHistoricalAttendance(Connection conn) {
        // 1. Clean legacy epoch timestamps
        try (Statement s = conn.createStatement()) {
            s.executeUpdate("UPDATE attendance SET date = '2026-10-01' WHERE date = '1790793000000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-03' WHERE date = '1790965800000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-04' WHERE date = '1791052200000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-05' WHERE date = '1791138600000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-06' WHERE date = '1791225000000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-07' WHERE date = '1791311400000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-08' WHERE date = '1791397800000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-09' WHERE date = '1791484200000'");
            s.executeUpdate("UPDATE attendance SET date = '2026-10-10' WHERE date = '1791570600000'");

            s.executeUpdate("UPDATE students SET class_id = 1, batch_id = 1 WHERE branch = 'AI' AND (class_id IS NULL OR class_id != 1)");
            s.executeUpdate("UPDATE students SET class_id = 2, batch_id = 3 WHERE branch = 'CS' AND (class_id IS NULL OR class_id != 2)");
            s.executeUpdate("UPDATE students SET class_id = 3, batch_id = 5 WHERE branch = 'ECE' AND (class_id IS NULL OR class_id != 3)");
        } catch (SQLException ignored) {}

        // 2. Conducted dates in October 2026
        String[] dates = {"2026-10-01", "2026-10-02", "2026-10-03", "2026-10-05", "2026-10-06", "2026-10-07", "2026-10-08", "2026-10-09", "2026-10-10", "2026-10-12"};

        String insertAtt = "INSERT OR IGNORE INTO attendance (student_id, date, check_in_time, check_out_time, checkin_lat, checkin_lng, checkin_accuracy, status, is_override, override_reason, overridden_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertAtt);
             Statement s = conn.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, roll_no, class_id FROM students ORDER BY id")) {

            java.util.List<Long> studentIds = new java.util.ArrayList<>();
            while (rs.next()) {
                studentIds.add(rs.getLong("id"));
            }

            for (Long sId : studentIds) {
                for (int i = 0; i < dates.length; i++) {
                    String d = dates[i];
                    long hash = (sId * 31 + i);

                    String status;
                    String cin = "08:50:00";
                    String cout = "15:45:00";
                    int isOverride = 0;
                    String reason = null;
                    String by = null;

                    // Distribute statuses realistically
                    if (sId == 8L) {
                        // Devendra: 50% critical shortage
                        status = (i % 2 == 0) ? "PRESENT" : "ABSENT";
                    } else if (sId == 6L) {
                        // Rohan: 70% attendance (below 75% threshold)
                        if (i == 2 || i == 5 || i == 7) status = "ABSENT";
                        else if (i == 4) {
                            status = "PRESENT";
                            isOverride = 1;
                            reason = "Medical emergency verified by faculty";
                            by = "Dr. Rajesh Sharma (FAC001)";
                        } else status = "PRESENT";
                    } else if (sId == 7L) {
                        // Sneha: 90% (1 excused)
                        if (i == 3) status = "EXCUSED";
                        else status = "PRESENT";
                    } else if (sId == 1L || sId == 2L) {
                        // Bhavya: 90% Present, 10% Late on Oct 12
                        if (i == dates.length - 1) {
                            status = "LATE";
                            cin = "09:22:00";
                            isOverride = 1;
                            reason = "College bus delay confirmed";
                            by = "Dr. Rajesh Sharma (FAC001)";
                        } else status = "PRESENT";
                    } else {
                        // General pattern: 80% to 100%
                        if (hash % 10 == 0) {
                            status = "ABSENT";
                            cin = null;
                            cout = null;
                        } else if (hash % 8 == 0) {
                            status = "LATE";
                            cin = "09:18:00";
                        } else {
                            status = "PRESENT";
                        }
                    }

                    ps.setLong(1, sId);
                    ps.setString(2, d);
                    ps.setString(3, cin);
                    ps.setString(4, cout);
                    ps.setDouble(5, 26.9131);
                    ps.setDouble(6, 75.8089);
                    ps.setDouble(7, 15.0);
                    ps.setString(8, status);
                    ps.setInt(9, isOverride);
                    ps.setString(10, reason);
                    ps.setString(11, by);
                    ps.executeUpdate();
                }
            }

            seedSampleOverrideAudits(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedSampleOverrideAudits(Connection conn) {
        try (Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM attendance_overrides");
            if (rs.next() && rs.getInt(1) > 0) return;
        } catch (SQLException ignored) {}

        String sql = "INSERT OR IGNORE INTO attendance_overrides (attendance_id, student_id, faculty_id, original_status, updated_status, reason, attendance_date, override_time) " +
                     "VALUES (?, ?, 1, ?, ?, ?, ?, datetime('now', '-2 days'))";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, 1L);
            ps.setLong(2, 6L);
            ps.setString(3, "ABSENT");
            ps.setString(4, "PRESENT");
            ps.setString(5, "Medical emergency certificate verified by department");
            ps.setString(6, "2026-10-06");
            ps.executeUpdate();

            ps.setLong(1, 2L);
            ps.setLong(2, 1L);
            ps.setString(3, "NOT_MARKED");
            ps.setString(4, "LATE");
            ps.setString(5, "College bus breakdown verified with transport office");
            ps.setString(6, "2026-10-12");
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    private static void seedClassesAndBatches(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM classes");
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        String insertClass = "INSERT OR IGNORE INTO classes (code, name, department, semester, is_active) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(insertClass)) {
            ps.setString(1, "AI-A");
            ps.setString(2, "Artificial Intelligence - Section A");
            ps.setString(3, "AI");
            ps.setString(4, "5th Semester");
            ps.executeUpdate();

            ps.setString(1, "CS-A");
            ps.setString(2, "Computer Science - Section A");
            ps.setString(3, "CS");
            ps.setString(4, "5th Semester");
            ps.executeUpdate();

            ps.setString(1, "ECE-A");
            ps.setString(2, "Electronics & Comm - Section A");
            ps.setString(3, "ECE");
            ps.setString(4, "5th Semester");
            ps.executeUpdate();
        }

        Long aiClassId = null, csClassId = null, eceClassId = null;
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT id, code FROM classes")) {
            while (rs.next()) {
                String code = rs.getString("code");
                if ("AI-A".equals(code)) aiClassId = rs.getLong("id");
                else if ("CS-A".equals(code)) csClassId = rs.getLong("id");
                else if ("ECE-A".equals(code)) eceClassId = rs.getLong("id");
            }
        }

        String insertBatch = "INSERT OR IGNORE INTO batches (class_id, code, name, is_active) VALUES (?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(insertBatch)) {
            if (aiClassId != null) {
                ps.setLong(1, aiClassId);
                ps.setString(2, "A1");
                ps.setString(3, "Batch A1 (Roll 1-30)");
                ps.executeUpdate();

                ps.setLong(1, aiClassId);
                ps.setString(2, "A2");
                ps.setString(3, "Batch A2 (Roll 31-60)");
                ps.executeUpdate();
            }
            if (csClassId != null) {
                ps.setLong(1, csClassId);
                ps.setString(2, "C1");
                ps.setString(3, "Batch C1 (Roll 1-30)");
                ps.executeUpdate();

                ps.setLong(1, csClassId);
                ps.setString(2, "C2");
                ps.setString(3, "Batch C2 (Roll 31-60)");
                ps.executeUpdate();
            }
            if (eceClassId != null) {
                ps.setLong(1, eceClassId);
                ps.setString(2, "E1");
                ps.setString(3, "Batch E1 (Roll 1-30)");
                ps.executeUpdate();

                ps.setLong(1, eceClassId);
                ps.setString(2, "E2");
                ps.setString(3, "Batch E2 (Roll 31-60)");
                ps.executeUpdate();
            }
        }

        try (Statement s = conn.createStatement()) {
            if (aiClassId != null) {
                s.executeUpdate("UPDATE students SET class_id = " + aiClassId + " WHERE branch = 'AI'");
            }
            if (csClassId != null) {
                s.executeUpdate("UPDATE students SET class_id = " + csClassId + " WHERE branch = 'CS'");
            }
            if (eceClassId != null) {
                s.executeUpdate("UPDATE students SET class_id = " + eceClassId + " WHERE branch = 'ECE'");
            }
            s.executeUpdate("UPDATE students SET batch_id = (SELECT id FROM batches WHERE batches.class_id = students.class_id ORDER BY id ASC LIMIT 1) WHERE batch_id IS NULL");
        }
    }

    private static void seedTeacherAssignments(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM teacher_assignments");
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        String insertAssign = "INSERT INTO teacher_assignments (faculty_id, class_id, batch_id, subject, created_at) " +
                "VALUES ((SELECT id FROM faculties WHERE faculty_id = ?), " +
                "(SELECT id FROM classes WHERE code = ?), " +
                "(SELECT id FROM batches WHERE code = ? LIMIT 1), ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertAssign)) {
            ps.setString(1, "FAC001");
            ps.setString(2, "AI-A");
            ps.setString(3, "A1");
            ps.setString(4, "Deep Learning");
            ps.setString(5, LocalDate.now().toString());
            ps.executeUpdate();

            ps.setString(1, "FAC001");
            ps.setString(2, "AI-A");
            ps.setString(3, "A2");
            ps.setString(4, "Machine Learning Lab");
            ps.setString(5, LocalDate.now().toString());
            ps.executeUpdate();

            ps.setString(1, "FAC002");
            ps.setString(2, "CS-A");
            ps.setString(3, "C1");
            ps.setString(4, "Operating Systems");
            ps.setString(5, LocalDate.now().toString());
            ps.executeUpdate();

            ps.setString(1, "FAC003");
            ps.setString(2, "ECE-A");
            ps.setString(3, "E1");
            ps.setString(4, "VLSI Design");
            ps.setString(5, LocalDate.now().toString());
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private static void seedFaculties(Connection conn) throws SQLException {
        String facPassHash = hashPassword("FacultyPassword123!");
        String insertFac = "INSERT OR IGNORE INTO faculties (faculty_id, name, email, branch, section, password_hash) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertFac)) {
            // Faculty 1: AI - Section A
            ps.setString(1, "FAC001");
            ps.setString(2, "Dr. Rajesh Sharma");
            ps.setString(3, "rajesh.sharma@aryacollege.edu.in");
            ps.setString(4, "AI");
            ps.setString(5, "Section A");
            ps.setString(6, facPassHash);
            ps.executeUpdate();

            // Faculty 2: CS - Section A
            ps.setString(1, "FAC002");
            ps.setString(2, "Prof. Priya Verma");
            ps.setString(3, "priya.verma@aryacollege.edu.in");
            ps.setString(4, "CS");
            ps.setString(5, "Section A");
            ps.setString(6, facPassHash);
            ps.executeUpdate();

            // Faculty 3: ECE - Section A
            ps.setString(1, "FAC003");
            ps.setString(2, "Dr. Amit Patel");
            ps.setString(3, "amit.patel@aryacollege.edu.in");
            ps.setString(4, "ECE");
            ps.setString(5, "Section A");
            ps.setString(6, facPassHash);
            ps.executeUpdate();
        }
    }

    private static void seedAdmins(Connection conn) throws SQLException {
        String adminPassHash = hashPassword("AdminPassword123!");
        String insertAdm = "INSERT OR IGNORE INTO admins (username, name, email, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertAdm)) {
            ps.setString(1, "admin");
            ps.setString(2, "Chief Administrator");
            ps.setString(3, "admin@aryacollege.edu.in");
            ps.setString(4, adminPassHash);
            ps.executeUpdate();
        }
    }

    private static void seedStudents(Connection conn) throws SQLException {
        String studentPassHash = hashPassword("StudentPassword123!");
        String insertStudent = "INSERT OR IGNORE INTO students (roll_no, name, email, branch, section, password_hash, permissions_accepted) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String[][] students = {
            // Class 1: AI - Section A (Faculty: Dr. Rajesh Sharma - FAC001)
            {"24EARAD031", "Bhavya Kumar Bohra", "bhavya.24earad031@aryacollege.edu.in", "AI", "Section A"},
            {"24EARAD001", "Aarav Sharma", "aarav.24earad001@aryacollege.edu.in", "AI", "Section A"},
            {"24EARAD002", "Ananya Gupta", "ananya.24earad002@aryacollege.edu.in", "AI", "Section A"},
            {"24EARAD003", "Rohan Mehta", "rohan.24earad003@aryacollege.edu.in", "AI", "Section A"},
            {"24EARAD004", "Sneha Jain", "sneha.24earad004@aryacollege.edu.in", "AI", "Section A"},
            {"24EARAD005", "Devendra Rathore", "devendra.24earad005@aryacollege.edu.in", "AI", "Section A"},

            // Class 2: CS - Section A (Faculty: Prof. Priya Verma - FAC002)
            {"24ECS001", "Ishan Verma", "ishan.24ecs001@aryacollege.edu.in", "CS", "Section A"},
            {"24ECS002", "Diya Joshi", "diya.24ecs002@aryacollege.edu.in", "CS", "Section A"},
            {"24ECS003", "Kabir Singh", "kabir.24ecs003@aryacollege.edu.in", "CS", "Section A"},
            {"24ECS004", "Riya Kapoor", "riya.24ecs004@aryacollege.edu.in", "CS", "Section A"},
            {"24ECS005", "Aryan Choudhary", "aryan.24ecs005@aryacollege.edu.in", "CS", "Section A"},

            // Class 3: ECE - Section A (Faculty: Dr. Amit Patel - FAC003)
            {"24ECE001", "Aditya Roy", "aditya.24ece001@aryacollege.edu.in", "ECE", "Section A"},
            {"24ECE002", "Tanvi Shah", "tanvi.24ece002@aryacollege.edu.in", "ECE", "Section A"},
            {"24ECE003", "Vikram Malhotra", "vikram.24ece003@aryacollege.edu.in", "ECE", "Section A"},
            {"24ECE004", "Meera Nair", "meera.24ece004@aryacollege.edu.in", "ECE", "Section A"}
        };

        try (PreparedStatement ps = conn.prepareStatement(insertStudent)) {
            for (String[] s : students) {
                ps.setString(1, s[0]);
                ps.setString(2, s[1]);
                ps.setString(3, s[2]);
                ps.setString(4, s[3]);
                ps.setString(5, s[4]);
                ps.setString(6, studentPassHash);
                ps.setInt(7, 1);
                ps.executeUpdate();
            }

            // STU2024001 alias for Bhavya
            ps.setString(1, "STU2024001");
            ps.setString(2, "Bhavya Kumar Bohra");
            ps.setString(3, "bhavya.24earad031@aryacollege.edu.in");
            ps.setString(4, "AI");
            ps.setString(5, "Section A");
            ps.setString(6, studentPassHash);
            ps.setInt(7, 1);
            ps.executeUpdate();
        }
    }

    private static void seedNotices(Connection conn) throws SQLException {
        // Seed Notices matching Screen 2 mockup
        String insertNotice = "INSERT INTO notices (category, title, message, created_at, is_read) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertNotice)) {
            ps.setString(1, "IMPORTANT");
            ps.setString(2, "Important Notice");
            ps.setString(3, "Midterm exam form submission\nlast date: 15 Oct 2026.");
            ps.setString(4, "2026-10-10 10:00:00");
            ps.setInt(5, 0);
            ps.executeUpdate();

            ps.setString(1, "GENERAL");
            ps.setString(2, "General");
            ps.setString(3, "College will remain closed on\n12 Oct 2026.");
            ps.setString(4, "2026-10-08 09:30:00");
            ps.setInt(5, 1);
            ps.executeUpdate();

            ps.setString(1, "ACADEMIC");
            ps.setString(2, "Academic");
            ps.setString(3, "Regular classes will resume\nfrom 13 Oct 2026.");
            ps.setString(4, "2026-10-05 11:00:00");
            ps.setInt(5, 1);
            ps.executeUpdate();

            ps.setString(1, "GENERAL");
            ps.setString(2, "General");
            ps.setString(3, "Review your attendance regularly.");
            ps.setString(4, "2026-10-01 08:30:00");
            ps.setInt(5, 1);
            ps.executeUpdate();
        }
    }

    private static void seedAttendance(Connection conn) throws SQLException {
        // Seed 30 days of attendance records for October 2026 matching Screen 3 & 7 mockup
        // (Present: 22, Absent: 3, OD: 0, Total: 25)
        String insertAtt = "INSERT OR IGNORE INTO attendance (student_id, date, check_in_time, check_out_time, checkin_lat, checkin_lng, checkin_accuracy, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertAtt)) {
            // Days: 1 to 10
            for (int day = 1; day <= 10; day++) {
                String dateStr = String.format("2026-10-%02d", day);
                LocalDate d = LocalDate.parse(dateStr);
                boolean isSunday = d.getDayOfWeek().getValue() == 7;

                ps.setLong(1, 1L); // student 1
                ps.setString(2, dateStr);

                if (day == 4 || day == 7 || isSunday) {
                    // Absent
                    ps.setNull(3, java.sql.Types.VARCHAR);
                    ps.setNull(4, java.sql.Types.VARCHAR);
                    ps.setNull(5, java.sql.Types.REAL);
                    ps.setNull(6, java.sql.Types.REAL);
                    ps.setNull(7, java.sql.Types.REAL);
                    ps.setString(8, "ABSENT");
                } else if (day == 10) {
                    // Today: 10 Oct, 08:52 AM checked in, not checked out yet
                    ps.setString(3, "08:52:00");
                    ps.setNull(4, java.sql.Types.VARCHAR);
                    ps.setDouble(5, 26.9131);
                    ps.setDouble(6, 75.8089);
                    ps.setDouble(7, 18.0);
                    ps.setString(8, "PRESENT");
                } else {
                    // Present
                    String cin = String.format("08:%02d:00", 50 + (day % 6));
                    String cout = String.format("15:%02d:00", 40 + (day % 5));
                    ps.setString(3, cin);
                    ps.setString(4, cout);
                    ps.setDouble(5, 26.9131);
                    ps.setDouble(6, 75.8089);
                    ps.setDouble(7, 18.0);
                    ps.setString(8, "PRESENT");
                }
                ps.executeUpdate();
            }
        }
    }

    /**
     * Utility to hash passwords using SHA-256.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }
}
