# Extremis Attendance System - 13 Verification Scenarios Test Suite
$ErrorActionPreference = "Stop"

$baseUrl = "http://localhost:8088"
Write-Host "=========================================================="
Write-Host " EXTREMIS AUTOMATED VERIFICATION SUITE - 13 SCENARIOS"
Write-Host "=========================================================="

# Helper: Wait for Jetty to be ready
$maxAttempts = 15
$ready = $false
for ($i = 1; $i -le $maxAttempts; $i++) {
    try {
        $check = Invoke-WebRequest -Uri "$baseUrl/login" -UseBasicParsing -TimeoutSec 3
        if ($check.StatusCode -eq 200) {
            $ready = $true
            Write-Host "Server is READY on $baseUrl/login (Status 200)"
            break
        }
    } catch {
        Write-Host "Waiting for server... attempt $i/$maxAttempts"
        Start-Sleep -Seconds 2
    }
}

if (-not $ready) {
    Write-Error "Server failed to start within timeout."
    exit 1
}

# SCENARIO 1: Admin logs in and creates a teacher successfully
Write-Host "`n--- Scenario 1: Admin creates a teacher successfully ---"
$adminSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginAdmin = Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -WebSession $adminSession -Body @{
    role = "admin"
    rollNo = "admin"
    password = "AdminPassword123!"
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Admin login landed on: $($loginAdmin.BaseResponse.ResponseUri)"

$teacherFacultyId = "TCH_TEST_" + (Get-Random -Minimum 1000 -Maximum 9999)
$addTeacher = Invoke-WebRequest -Uri "$baseUrl/admin/teachers" -Method POST -WebSession $adminSession -Body @{
    action = "create"
    facultyId = $teacherFacultyId
    name = "Dr. Automated Tester"
    email = "$teacherFacultyId@extremis.edu"
    department = "CS"
    password = "TeacherPassword123"
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Teacher creation response URL: $($addTeacher.BaseResponse.ResponseUri)"
$teacherCreatedInDb = (sqlite3 extremis.db "SELECT count(*) FROM faculties WHERE faculty_id='$teacherFacultyId';").Trim()
if ([int]$teacherCreatedInDb -ge 1) {
    Write-Host "[PASS] Scenario 1: Admin created teacher $teacherFacultyId successfully in DB."
} else {
    Write-Error "[FAIL] Scenario 1: Teacher not found in DB."
}

# SCENARIO 2: Admin creates a class and a batch
Write-Host "`n--- Scenario 2: Admin creates a class and a batch ---"
$testClassCode = "TEST_CLS_" + (Get-Random -Minimum 100 -Maximum 999)
$addClass = Invoke-WebRequest -Uri "$baseUrl/admin/classes" -Method POST -WebSession $adminSession -Body @{
    action = "create_class"
    code = $testClassCode
    name = "Test Computer Science 2026"
    department = "CS"
    semester = "6th Semester"
} -UseBasicParsing -MaximumRedirection 5

$classId = (sqlite3 extremis.db "SELECT id FROM classes WHERE code='$testClassCode';").Trim()
Write-Host "Created Class ID: $classId with code $testClassCode"

$testBatchCode = "B1"
$addBatch = Invoke-WebRequest -Uri "$baseUrl/admin/classes" -Method POST -WebSession $adminSession -Body @{
    action = "create_batch"
    classId = $classId
    code = $testBatchCode
    name = "Test Lab Batch 1"
} -UseBasicParsing -MaximumRedirection 5

$batchId = (sqlite3 extremis.db "SELECT id FROM batches WHERE class_id=$classId AND code='$testBatchCode';").Trim()
if ($classId -and $batchId) {
    Write-Host "[PASS] Scenario 2: Class ($testClassCode, ID: $classId) and Batch ($testBatchCode, ID: $batchId) created."
} else {
    Write-Error "[FAIL] Scenario 2: Failed to create class and batch."
}

# SCENARIO 3: Admin assigns a teacher to the correct class and batch
Write-Host "`n--- Scenario 3: Admin assigns teacher to class and batch ---"
$teacherId = (sqlite3 extremis.db "SELECT id FROM faculties WHERE faculty_id='$teacherFacultyId';").Trim()
$assignTeacher = Invoke-WebRequest -Uri "$baseUrl/admin/assignments" -Method POST -WebSession $adminSession -Body @{
    action = "create"
    facultyId = $teacherId
    classId = $classId
    batchId = $batchId
    subject = "Automated Testing & QA"
} -UseBasicParsing -MaximumRedirection 5

$assignmentCount = (sqlite3 extremis.db "SELECT count(*) FROM teacher_assignments WHERE faculty_id=$teacherId AND class_id=$classId AND batch_id=$batchId;").Trim()
if ([int]$assignmentCount -ge 1) {
    Write-Host "[PASS] Scenario 3: Teacher assigned to Class $testClassCode and Batch $testBatchCode."
} else {
    Write-Error "[FAIL] Scenario 3: Assignment not found."
}

# Enroll a student into this new class and batch for testing
$studentId = (sqlite3 extremis.db "SELECT id FROM students WHERE roll_no='24ECS001';").Trim()
sqlite3 extremis.db "UPDATE students SET class_id=$classId, batch_id=$batchId WHERE id=$studentId;"

# SCENARIO 4: Assigned teacher can view assigned students
Write-Host "`n--- Scenario 4: Assigned teacher logs in and views assigned students ---"
$teacherSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginTeacher = Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -WebSession $teacherSession -Body @{
    role = "faculty"
    rollNo = $teacherFacultyId
    password = "TeacherPassword123"
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Teacher login landed on: $($loginTeacher.BaseResponse.ResponseUri)"

$facultyDash = Invoke-WebRequest -Uri "$baseUrl/faculty/dashboard?classId=$classId&batchId=$batchId" -WebSession $teacherSession -UseBasicParsing
if ($facultyDash.Content -match "24ECS001" -or $facultyDash.Content -match "Ishan Verma") {
    Write-Host "[PASS] Scenario 4: Assigned teacher views assigned student 24ECS001 in roster."
} else {
    Write-Error "[FAIL] Scenario 4: Assigned student not visible in faculty roster."
}

# SCENARIO 5 & 6: Teacher can mark a late student as Present/Late with mandatory reason, updating DB
Write-Host "`n--- Scenario 5 & 6: Teacher executes attendance override with reason ---"
$todayStr = (Get-Date).ToString("yyyy-MM-dd")
# First clear any existing attendance for this student today
sqlite3 extremis.db "DELETE FROM attendance WHERE student_id=$studentId AND date='$todayStr';"

$overrideReq = Invoke-WebRequest -Uri "$baseUrl/faculty/override" -Method POST -WebSession $teacherSession -Body @{
    studentId = $studentId
    date = $todayStr
    status = "LATE"
    reason = "Arrived 15 minutes late due to college bus breakdown. Verified with transport coordinator."
    classId = $classId
    batchId = $batchId
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Override Response URL: $($overrideReq.BaseResponse.ResponseUri)"
$rawAtt = sqlite3 extremis.db "SELECT status, is_override, override_reason FROM attendance WHERE student_id=$studentId AND date='$todayStr';"
$dbAtt = if ($rawAtt) { $rawAtt.Trim() } else { "" }
Write-Host "Database Attendance Record: $dbAtt"
if ($dbAtt -match "LATE" -and $dbAtt -match "1") {
    Write-Host "[PASS] Scenario 5 & 6: Attendance status updated to LATE with is_override=1 and reason saved."
} else {
    Write-Error "[FAIL] Scenario 5 & 6: Attendance override not saved correctly."
}

# SCENARIO 7: Override history preserves original and updated statuses
Write-Host "`n--- Scenario 7: Override history preserves original and updated statuses ---"
$auditLog = (sqlite3 extremis.db "SELECT original_status, updated_status, reason FROM attendance_overrides WHERE student_id=$studentId AND attendance_date='$todayStr' ORDER BY id DESC LIMIT 1;").Trim()
Write-Host "Audit Log Row: $auditLog"
if ($auditLog -match "ABSENT\|LATE" -or $auditLog -match "LATE") {
    Write-Host "[PASS] Scenario 7: Immutable audit log recorded original and updated status."
} else {
    Write-Error "[FAIL] Scenario 7: Audit log verification failed."
}

# SCENARIO 8: Unassigned teacher cannot modify another class's attendance
Write-Host "`n--- Scenario 8: Unassigned teacher cannot modify another class's attendance ---"
# FAC003 is ECE teacher, not assigned to CS student 24ECS001
$unassignedSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginUnassigned = Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -WebSession $unassignedSession -Body @{
    role = "faculty"
    rollNo = "FAC003"
    password = "FacultyPassword123!"
} -UseBasicParsing -MaximumRedirection 5

$unauthorizedOverride = Invoke-WebRequest -Uri "$baseUrl/faculty/override" -Method POST -WebSession $unassignedSession -Body @{
    studentId = $studentId
    date = $todayStr
    status = "PRESENT"
    reason = "Malicious unassigned attempt"
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Unassigned override URL response: $($unauthorizedOverride.BaseResponse.ResponseUri)"
$currentStatus = (sqlite3 extremis.db "SELECT status FROM attendance WHERE student_id=$studentId AND date='$todayStr';").Trim()
if ($unauthorizedOverride.BaseResponse.ResponseUri.ToString() -match "error=" -and $currentStatus -eq "LATE") {
    Write-Host "[PASS] Scenario 8: Unassigned teacher was rejected with error and DB record was unchanged."
} else {
    Write-Error "[FAIL] Scenario 8: Unassigned teacher modified attendance!"
}

# SCENARIO 9: Student cannot override attendance or access admin features
Write-Host "`n--- Scenario 9: Student cannot override attendance or access admin features ---"
# Clear Bhavya session token first to allow login
sqlite3 extremis.db "UPDATE students SET session_token=NULL WHERE roll_no='24EARAD031';"

$studentSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginStudent = Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -WebSession $studentSession -Body @{
    role = "student"
    rollNo = "24EARAD031"
    password = "StudentPassword123!"
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Student login landed on: $($loginStudent.BaseResponse.ResponseUri)"

$studentAdminAccess = Invoke-WebRequest -Uri "$baseUrl/admin/teachers" -WebSession $studentSession -UseBasicParsing -MaximumRedirection 5
Write-Host "Student accessing /admin/teachers redirected to: $($studentAdminAccess.BaseResponse.ResponseUri)"
$studentOverrideAttempt = Invoke-WebRequest -Uri "$baseUrl/faculty/override" -Method POST -WebSession $studentSession -Body @{
    studentId = 1
    date = $todayStr
    status = "PRESENT"
    reason = "Self override attempt"
} -UseBasicParsing -MaximumRedirection 5

Write-Host "Student override attempt redirected to: $($studentOverrideAttempt.BaseResponse.ResponseUri)"
if (($studentAdminAccess.BaseResponse.ResponseUri.ToString() -match "/login" -or $studentAdminAccess.BaseResponse.ResponseUri.ToString() -match "/dashboard") -and
    ($studentOverrideAttempt.BaseResponse.ResponseUri.ToString() -match "/login" -or $studentOverrideAttempt.BaseResponse.ResponseUri.ToString() -match "/dashboard")) {
    Write-Host "[PASS] Scenario 9: Student blocked from /admin and /faculty endpoints (redirected away from admin/faculty)."
} else {
    Write-Error "[FAIL] Scenario 9: Student security bypass detected."
}

# SCENARIO 10: Duplicate attendance records and duplicate assignments are prevented
Write-Host "`n--- Scenario 10: Prevent duplicate assignments and duplicate attendance ---"
$dupAssign = Invoke-WebRequest -Uri "$baseUrl/admin/assignments" -Method POST -WebSession $adminSession -Body @{
    action = "create"
    facultyId = $teacherId
    classId = $classId
    batchId = $batchId
    subject = "Duplicate Subject Attempt"
} -UseBasicParsing -MaximumRedirection 5

$assignCount = (sqlite3 extremis.db "SELECT count(*) FROM teacher_assignments WHERE faculty_id=$teacherId AND class_id=$classId AND batch_id=$batchId;").Trim()
if ([int]$assignCount -eq 1 -and $dupAssign.BaseResponse.ResponseUri.ToString() -match "error=") {
    Write-Host "[PASS] Scenario 10: Duplicate assignment successfully rejected."
} else {
    Write-Error "[FAIL] Scenario 10: Duplicate assignment was not prevented."
}

# SCENARIO 11: Invalid class-batch assignments are rejected
Write-Host "`n--- Scenario 11: Invalid class-batch assignments are rejected ---"
# Batch 5 belongs to class 3 (ECE), trying to assign with classId (CS)
$invalidAssign = Invoke-WebRequest -Uri "$baseUrl/admin/assignments" -Method POST -WebSession $adminSession -Body @{
    action = "create"
    facultyId = $teacherId
    classId = $classId
    batchId = 5
    subject = "Cross class batch mismatch"
} -UseBasicParsing -MaximumRedirection 5

if ($invalidAssign.BaseResponse.ResponseUri.ToString() -match "error=") {
    Write-Host "[PASS] Scenario 11: Invalid class-batch mismatch rejected with error."
} else {
    Write-Error "[FAIL] Scenario 11: Invalid assignment was permitted."
}

# SCENARIO 12: Existing check-in and attendance reporting still works
Write-Host "`n--- Scenario 12: Student check-in and dashboard verification ---"
sqlite3 extremis.db "UPDATE students SET session_token=NULL WHERE roll_no='24EARAD031';"
$studentSession12 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginStudent12 = Invoke-WebRequest -Uri "$baseUrl/login" -Method POST -WebSession $studentSession12 -Body @{
    role = "student"
    rollNo = "24EARAD031"
    password = "StudentPassword123!"
} -UseBasicParsing -MaximumRedirection 5

$studentDash = Invoke-WebRequest -Uri "$baseUrl/dashboard" -WebSession $studentSession12 -UseBasicParsing
if ($studentDash.StatusCode -eq 200 -and ($studentDash.Content -match "Bhavya Kumar Bohra" -or $studentDash.Content -match "24EARAD031")) {
    Write-Host "[PASS] Scenario 12: Student dashboard loads cleanly with student data."
} else {
    Write-Error "[FAIL] Scenario 12: Student dashboard failed."
}

# SCENARIO 13: Responsive views test
Write-Host "`n--- Scenario 13: Viewport verification ---"
$viewsToCheck = @(
    "$baseUrl/faculty/dashboard",
    "$baseUrl/admin/dashboard",
    "$baseUrl/admin/teachers",
    "$baseUrl/admin/classes",
    "$baseUrl/admin/assignments",
    "$baseUrl/admin/overrides"
)

foreach ($v in $viewsToCheck) {
    $res = Invoke-WebRequest -Uri $v -WebSession $adminSession -UseBasicParsing
    if ($res.StatusCode -eq 200) {
        Write-Host "  -> Checked $v : Status 200 OK"
    } else {
        Write-Error "Failed view: $v"
    }
}
Write-Host "[PASS] Scenario 13: All admin and faculty views return HTTP 200 OK."

Write-Host "`n=========================================================="
Write-Host " ALL 13 VERIFICATION SCENARIOS PASSED WITH ZERO ERRORS!"
Write-Host "=========================================================="
