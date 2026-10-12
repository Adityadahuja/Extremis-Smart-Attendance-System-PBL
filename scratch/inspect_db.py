import sqlite3

conn = sqlite3.connect("extremis.db")
c = conn.cursor()

sql = """
SELECT COUNT(DISTINCT a.date) FROM attendance a
JOIN students s ON a.student_id = s.id
WHERE a.date >= '2026-10-01' AND a.date <= '2026-10-12'
AND (s.class_id IN (SELECT ta.class_id FROM teacher_assignments ta WHERE ta.faculty_id = 1)
     OR UPPER(s.branch) IN (SELECT UPPER(c.department) FROM classes c JOIN teacher_assignments ta ON ta.class_id = c.id WHERE ta.faculty_id = 1))
"""
c.execute(sql)
print("Conducted count with faculty_id:", c.fetchone()[0])

sql2 = """
SELECT s.id as s_id, s.roll_no, s.name, s.branch, s.section,
       s.class_id, c.code as class_code, c.name as class_name,
       s.batch_id, b.code as batch_code, b.name as batch_name,
       COALESCE(SUM(CASE WHEN UPPER(a.status) = 'PRESENT' THEN 1 ELSE 0 END), 0) as present_count,
       COALESCE(SUM(CASE WHEN UPPER(a.status) = 'LATE' THEN 1 ELSE 0 END), 0) as late_count,
       COALESCE(SUM(CASE WHEN UPPER(a.status) = 'ABSENT' THEN 1 ELSE 0 END), 0) as absent_count,
       COALESCE(SUM(CASE WHEN UPPER(a.status) IN ('EXCUSED', 'OD') THEN 1 ELSE 0 END), 0) as excused_count,
       COALESCE(SUM(CASE WHEN a.is_override = 1 THEN 1 ELSE 0 END), 0) as override_count,
       MAX(a.date) as last_date,
       (SELECT a2.status FROM attendance a2 WHERE a2.student_id = s.id AND a2.date >= '2026-10-01' AND a2.date <= '2026-10-12' ORDER BY a2.date DESC LIMIT 1) as last_status
FROM students s
LEFT JOIN classes c ON s.class_id = c.id
LEFT JOIN batches b ON s.batch_id = b.id
LEFT JOIN attendance a ON s.id = a.student_id AND a.date >= '2026-10-01' AND a.date <= '2026-10-12'
WHERE (s.class_id IN (SELECT ta.class_id FROM teacher_assignments ta WHERE ta.faculty_id = 1)
       OR UPPER(s.branch) IN (SELECT UPPER(c.department) FROM classes c JOIN teacher_assignments ta ON ta.class_id = c.id WHERE ta.faculty_id = 1))
GROUP BY s.id ORDER BY s.roll_no ASC
"""
c.execute(sql2)
rows = c.fetchall()
print("Students count with faculty_id:", len(rows))
for r in rows:
    print(r[0], r[1], r[2], r[10], r[11], r[12], r[13], r[14], r[15], r[16])
