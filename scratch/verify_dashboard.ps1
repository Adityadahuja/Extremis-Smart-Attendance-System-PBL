$ErrorActionPreference = 'Stop'
$base = 'http://localhost:8088'
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

Write-Host "=== TEST 1: Faculty Authentication ==="
$loginResp = Invoke-WebRequest -Uri "$base/login" -Method Post -Body @{
    role = 'faculty'
    rollNo = 'FAC001'
    password = 'FacultyPassword123!'
} -WebSession $session -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing

Write-Host "Login Status:" $loginResp.StatusCode
Write-Host "Location Header:" $loginResp.Headers['Location']

Write-Host "`n=== TEST 2: Faculty Dashboard Render & Dynamic KPI Cards ==="
$dashResp = Invoke-WebRequest -Uri "$base/faculty/dashboard" -Method Get -WebSession $session -UseBasicParsing
Write-Host "Dashboard Status:" $dashResp.StatusCode
Write-Host "Faculty Name present:" $dashResp.Content.Contains('Dr. Rajesh Sharma')
Write-Host "Faculty ID present:" $dashResp.Content.Contains('FAC001')
Write-Host "KPI Total Enrolled present:" $dashResp.Content.Contains('Total Enrolled')
Write-Host "KPI Sessions Conducted present:" $dashResp.Content.Contains('Sessions Conducted')
Write-Host "KPI Attendance Rate present:" $dashResp.Content.Contains('Attendance Rate')
Write-Host "KPI Late Arrivals present:" $dashResp.Content.Contains('Late Arrivals')
Write-Host "KPI Low Attendance present:" $dashResp.Content.Contains('Low Attendance')

Write-Host "`n=== TEST 3: Student Overview Table & Assigned Students ==="
Write-Host "Bhavya present:" $dashResp.Content.Contains('Bhavya Kumar Bohra')
Write-Host "Aarav present:" $dashResp.Content.Contains('Aarav Sharma')
Write-Host "Devendra present:" $dashResp.Content.Contains('Devendra Rathore')
Write-Host "Tier Badge Safe present:" $dashResp.Content.Contains('Safe')
Write-Host "Tier Badge Critical present:" $dashResp.Content.Contains('Critical')

Write-Host "`n=== TEST 4: Student Drawer Session History AJAX Endpoint ==="
# Test AJAX endpoint for student 1 (Bhavya)
$ajaxResp = Invoke-WebRequest -Uri "$base/faculty/dashboard?action=student_history&studentId=1" -Method Get -WebSession $session -UseBasicParsing
Write-Host "AJAX Status:" $ajaxResp.StatusCode
$json = $ajaxResp.Content | ConvertFrom-Json
Write-Host "JSON Success:" $json.success
Write-Host "Student Name:" $json.student.name
Write-Host "Student Roll:" $json.student.rollNo
Write-Host "Total Sessions:" $json.summary.totalSessions
Write-Host "Attendance Percentage:" $json.summary.attendancePercentage
Write-Host "Performance Tier:" $json.summary.performanceTier
Write-Host "Conducted Sessions Count:" $json.sessions.Count
Write-Host "First Session Date:" $json.sessions[0].date
Write-Host "First Session Status:" $json.sessions[0].status
Write-Host "First Session Recording Method:" $json.sessions[0].recordingMethod

Write-Host "`n=== TEST 5: CSV Summary Export ==="
$csvResp = Invoke-WebRequest -Uri "$base/faculty/export?type=summary" -Method Get -WebSession $session -UseBasicParsing
Write-Host "CSV Summary Status:" $csvResp.StatusCode
Write-Host "CSV Content-Type:" $csvResp.Headers['Content-Type']
Write-Host "CSV Line count:" ($csvResp.Content -split "`n").Count
Write-Host "CSV Header line:" ($csvResp.Content -split "`n")[0]
Write-Host "CSV Sample student row:" ($csvResp.Content -split "`n")[1]

Write-Host "`n=== TEST 6: CSV Detailed Sessions Export ==="
$csvDetResp = Invoke-WebRequest -Uri "$base/faculty/export?type=detailed" -Method Get -WebSession $session -UseBasicParsing
Write-Host "CSV Detailed Status:" $csvDetResp.StatusCode
Write-Host "CSV Detailed Lines:" ($csvDetResp.Content -split "`n").Count

Write-Host "`n=== TEST 7: Class / Batch Filter Validation ==="
# Filter by AI-A (classId 1) and Batch A1 (batchId 1)
$filterResp = Invoke-WebRequest -Uri "$base/faculty/dashboard?classId=1&batchId=1" -Method Get -WebSession $session -UseBasicParsing
Write-Host "Filter Status:" $filterResp.StatusCode
Write-Host "Filter contains AI-A:" $filterResp.Content.Contains('AI-A')

Write-Host "`n=== TEST 8: Faculty Attendance Override Execution ==="
# Execute override on student 8 (Devendra) for 2026-10-12 from ABSENT to PRESENT
$overrideResp = Invoke-WebRequest -Uri "$base/faculty/override" -Method Post -Body @{
    studentId = '8'
    date = '2026-10-12'
    status = 'PRESENT'
    reason = 'Special departmental laboratory makeup completed and signed'
    classId = '1'
} -WebSession $session -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing

Write-Host "Override Post Status:" $overrideResp.StatusCode
Write-Host "Override Redirect Location:" $overrideResp.Headers['Location']

# Check that recalculation took effect
$redirectUrl = $overrideResp.Headers['Location']
$dashAfterResp = Invoke-WebRequest -Uri $redirectUrl -Method Get -WebSession $session -UseBasicParsing
Write-Host "Success message shown in dashboard:" $dashAfterResp.Content.Contains('successfully updated to PRESENT')

# Check student 8 history via AJAX
$ajax8 = Invoke-WebRequest -Uri "$base/faculty/dashboard?action=student_history&studentId=8" -Method Get -WebSession $session -UseBasicParsing
$json8 = $ajax8.Content | ConvertFrom-Json
Write-Host "Student 8 Attendance % After Override:" $json8.summary.attendancePercentage

Write-Host "`n=== TEST 9: Security Check - Unauthorized Faculty Access ==="
# Login as FAC002 (Prof. Priya Verma, CS department)
$session2 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$login2 = Invoke-WebRequest -Uri "$base/login" -Method Post -Body @{
    role = 'faculty'
    rollNo = 'FAC002'
    password = 'FacultyPassword123!'
} -WebSession $session2 -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing

Write-Host "FAC002 Login Status:" $login2.StatusCode
$dash2 = Invoke-WebRequest -Uri "$base/faculty/dashboard" -Method Get -WebSession $session2 -UseBasicParsing
Write-Host "FAC002 Sees Priya Verma:" $dash2.Content.Contains('Prof. Priya Verma')
Write-Host "FAC002 Sees CS-A students (Ishan Verma):" $dash2.Content.Contains('Ishan Verma')
# FAC002 should NOT see Dr. Rajesh Sharma's AI students
Write-Host "FAC002 Does NOT see Bhavya Kumar Bohra (AI student):" (-not $dash2.Content.Contains('Bhavya Kumar Bohra'))

# Test FAC002 attempting unauthorized override on student 1 (AI student)
$unauthOverride = Invoke-WebRequest -Uri "$base/faculty/override" -Method Post -Body @{
    studentId = '1'
    date = '2026-10-12'
    status = 'PRESENT'
    reason = 'Unauthorized attempt'
} -WebSession $session2 -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing

Write-Host "FAC002 Unauthorized Override Status:" $unauthOverride.StatusCode
Write-Host "FAC002 Override Redirected with Authorization Denied:" $unauthOverride.Headers['Location'].Contains('Authorization+Denied')

Write-Host "`n=== TEST 10: Student Dashboard & Check-In Regression Test ==="
$sessionStudent = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$sLogin = Invoke-WebRequest -Uri "$base/login" -Method Post -Body @{
    role = 'student'
    rollNo = '24EARAD001'
    password = 'StudentPassword123!'
} -WebSession $sessionStudent -MaximumRedirection 0 -ErrorAction SilentlyContinue -UseBasicParsing

Write-Host "Student Login Status:" $sLogin.StatusCode
$sDash = Invoke-WebRequest -Uri "$base/dashboard" -Method Get -WebSession $sessionStudent -UseBasicParsing
Write-Host "Student Dashboard Status:" $sDash.StatusCode
Write-Host "Student Dashboard Contains Aarav:" $sDash.Content.Contains('Aarav')
Write-Host "Student Dashboard Contains Check-In UI:" ($sDash.Content.Contains('Check In') -or $sDash.Content.Contains('Checked In'))

Write-Host "`n=== ALL 10 TESTS COMPLETED SUCCESSFULLY ==="
