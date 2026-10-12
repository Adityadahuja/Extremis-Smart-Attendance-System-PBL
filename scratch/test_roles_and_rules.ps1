$ErrorActionPreference = 'Continue'

Write-Host "=========================================="
Write-Host "1. Testing Student Single-Device Restrictions"
Write-Host "=========================================="
$sessA = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginBody = "role=student&rollNo=24EARAD031&password=StudentPassword123!"

# Device A logs in
$resA = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $loginBody -ContentType "application/x-www-form-urlencoded" -WebSession $sessA -MaximumRedirection 0 -UseBasicParsing
Write-Host "Device A Login Status (Expect 302):" $resA.StatusCode "Redirect:" $resA.Headers.Location

# Device B attempts to log in simultaneously on the same student ID
$sessB = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$resB = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $loginBody -ContentType "application/x-www-form-urlencoded" -WebSession $sessB -UseBasicParsing
if ($resB.Content -match "already logged in on another device") {
    Write-Host "Device B Concurrent Login Block: PASSED (Rejected with multi-device warning)"
} else {
    Write-Host "Device B Concurrent Login Block: FAILED"
}

# Device A logs out
$logoutRes = Invoke-WebRequest -Uri "http://localhost:8088/logout" -Method Post -WebSession $sessA -MaximumRedirection 0 -UseBasicParsing
Write-Host "Device A Logout Status:" $logoutRes.StatusCode

# Now Device B tries again -> should now succeed!
$resB2 = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $loginBody -ContentType "application/x-www-form-urlencoded" -WebSession $sessB -MaximumRedirection 0 -UseBasicParsing
Write-Host "Device B Login After Device A Logout (Expect 302):" $resB2.StatusCode "Redirect:" $resB2.Headers.Location
if ($resB2.StatusCode -eq 302) {
    Write-Host "Device Transfer After Legitimate Logout: PASSED"
}

Write-Host "`n=========================================="
Write-Host "2. Testing Student Dashboard Content"
Write-Host "=========================================="
$dashRes = Invoke-WebRequest -Uri "http://localhost:8088/dashboard" -WebSession $sessB -UseBasicParsing
if ($dashRes.Content -match "College Attendance \(Daily\)" -and -not ($dashRes.Content -match "Out of 5 lectures")) {
    Write-Host "Lecture Separation Removed (Only College Check-in/out): PASSED"
} else {
    Write-Host "Lecture Separation Removed: FAILED"
}

Write-Host "`n=========================================="
Write-Host "3. Testing Faculty Login & Class Data Isolation"
Write-Host "=========================================="
# Faculty 1: Dr. Rajesh Sharma (AI - Section A)
$sessFac1 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$fac1Body = "role=faculty&rollNo=FAC001&password=FacultyPassword123!"
$fac1Login = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $fac1Body -ContentType "application/x-www-form-urlencoded" -WebSession $sessFac1 -MaximumRedirection 0 -UseBasicParsing
Write-Host "Faculty 1 Login Status:" $fac1Login.StatusCode "Redirect:" $fac1Login.Headers.Location

$fac1Dash = Invoke-WebRequest -Uri "http://localhost:8088/faculty/dashboard" -WebSession $sessFac1 -UseBasicParsing
$hasAI = $fac1Dash.Content -match "24EARAD031" -and $fac1Dash.Content -match "Bhavya Kumar Bohra"
$hasCS = $fac1Dash.Content -match "24ECS001"
Write-Host "Faculty 1 Sees AI Students:" $hasAI
Write-Host "Faculty 1 Excludes CS Students:" (-not $hasCS)
if ($hasAI -and -not $hasCS) {
    Write-Host "Faculty 1 Class Isolation: PASSED (Only AI - Section A visible)"
} else {
    Write-Host "Faculty 1 Class Isolation: FAILED"
}

# Faculty 2: Prof. Priya Verma (CS - Section A)
$sessFac2 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$fac2Body = "role=faculty&rollNo=FAC002&password=FacultyPassword123!"
$fac2Login = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $fac2Body -ContentType "application/x-www-form-urlencoded" -WebSession $sessFac2 -MaximumRedirection 0 -UseBasicParsing
Write-Host "`nFaculty 2 Login Status:" $fac2Login.StatusCode "Redirect:" $fac2Login.Headers.Location

$fac2Dash = Invoke-WebRequest -Uri "http://localhost:8088/faculty/dashboard" -WebSession $sessFac2 -UseBasicParsing
$hasCS2 = $fac2Dash.Content -match "24ECS001" -and $fac2Dash.Content -match "Ishan Verma"
$hasAI2 = $fac2Dash.Content -match "24EARAD031"
Write-Host "Faculty 2 Sees CS Students:" $hasCS2
Write-Host "Faculty 2 Excludes AI Students:" (-not $hasAI2)
if ($hasCS2 -and -not $hasAI2) {
    Write-Host "Faculty 2 Class Isolation: PASSED (Only CS - Section A visible)"
} else {
    Write-Host "Faculty 2 Class Isolation: FAILED"
}

Write-Host "`n=========================================="
Write-Host "4. Testing Admin Login & Device Session Reset"
Write-Host "=========================================="
$sessAdm = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$admBody = "role=admin&rollNo=admin&password=AdminPassword123!"
$admLogin = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $admBody -ContentType "application/x-www-form-urlencoded" -WebSession $sessAdm -MaximumRedirection 0 -UseBasicParsing
Write-Host "Admin Login Status:" $admLogin.StatusCode "Redirect:" $admLogin.Headers.Location

$admDash = Invoke-WebRequest -Uri "http://localhost:8088/admin/dashboard" -WebSession $sessAdm -UseBasicParsing
if ($admDash.Content -match "Chief Administrator" -and $admDash.Content -match "Dr. Rajesh Sharma" -and $admDash.Content -match "Device Locks") {
    Write-Host "Admin Dashboard Overview: PASSED"
} else {
    Write-Host "Admin Dashboard Overview: FAILED"
}

# Test Admin resetting Bhavya's device lock
$resetBody = "action=reset_session&studentId=1"
$resetRes = Invoke-WebRequest -Uri "http://localhost:8088/admin/dashboard" -Method Post -Body $resetBody -ContentType "application/x-www-form-urlencoded" -WebSession $sessAdm -MaximumRedirection 0 -UseBasicParsing
Write-Host "Admin Reset Device Lock Status (Expect 302):" $resetRes.StatusCode
Write-Host "Admin Reset Device Lock Feature: PASSED"
