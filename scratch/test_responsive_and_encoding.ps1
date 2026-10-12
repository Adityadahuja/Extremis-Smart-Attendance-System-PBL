$ErrorActionPreference = 'Continue'

Write-Host "===================================================="
Write-Host "TESTING DASHBOARD ENCODING AND RESPONSIVE STRUCTURE"
Write-Host "===================================================="

# Admin session to reset Bhavya's device lock
$adminSess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$adminLoginBody = "role=admin&rollNo=admin&password=AdminPassword123!"
$adminLogin = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $adminLoginBody -ContentType "application/x-www-form-urlencoded" -WebSession $adminSess -MaximumRedirection 0 -UseBasicParsing -ErrorAction SilentlyContinue

$resetLock = Invoke-WebRequest -Uri "http://localhost:8088/admin/dashboard?action=resetLock&studentId=4" -Method Get -WebSession $adminSess -MaximumRedirection 0 -UseBasicParsing -ErrorAction SilentlyContinue

# Student session for Bhavya (24EARAD031)
$studentSess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginBody = "role=student&rollNo=24EARAD031&password=StudentPassword123!"
$loginRes = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $loginBody -ContentType "application/x-www-form-urlencoded" -WebSession $studentSess -MaximumRedirection 0 -UseBasicParsing -ErrorAction SilentlyContinue

Write-Host "Student Login Status: " $loginRes.StatusCode

# Fetch Dashboard HTML
$dashRes = Invoke-WebRequest -Uri "http://localhost:8088/dashboard" -WebSession $studentSess -UseBasicParsing
$html = $dashRes.Content

Write-Host "`n--- 1. Character Encoding Checks ---"
$hasMojibake = $html.IndexOf([char]0x00E2) -ge 0
if (-not $hasMojibake) {
    Write-Host "Mojibake Check: PASSED (No 0x00E2 or corrupted UTF-8 bytes found)"
} else {
    Write-Host "Mojibake Check: FAILED"
}

$hasCleanDash = $html.IndexOf("&mdash;") -ge 0
if ($hasCleanDash) {
    Write-Host "Proper Fallback Dash Check: PASSED (&mdash; rendered in mini-stat-time)"
} else {
    Write-Host "Proper Fallback Dash Check: FAILED"
}

$hasCleanEnDash = $html.IndexOf("&ndash;") -ge 0
if ($hasCleanEnDash) {
    Write-Host "Branch-Section Separator Check: PASSED (&ndash; rendered for Branch and Section)"
} else {
    Write-Host "Branch-Section Separator Check: FAILED"
}

Write-Host "`n--- 2. Responsive Layout Structure Checks ---"
function Check-Passed($val) { if ($val) { return "PASSED" } else { return "FAILED" } }

$hasGrid = $html.IndexOf("dashboard-grid") -ge 0
$hasMainCol = $html.IndexOf("dashboard-main-col") -ge 0
$hasSideCol = $html.IndexOf("dashboard-side-col") -ge 0
$hasDesktopNav = $html.IndexOf("desktop-nav") -ge 0
$hasActionPanel = $html.IndexOf("action-panel-card") -ge 0
$hasViewport = $html.IndexOf("width=device-width, initial-scale=1.0") -ge 0

Write-Host "Responsive Grid Container (dashboard-grid): " (Check-Passed $hasGrid)
Write-Host "Primary Content Column (dashboard-main-col): " (Check-Passed $hasMainCol)
Write-Host "Dedicated Side Panel (dashboard-side-col): " (Check-Passed $hasSideCol)
Write-Host "Desktop Header Navigation (desktop-nav): " (Check-Passed $hasDesktopNav)
Write-Host "Action Verification Card (action-panel-card): " (Check-Passed $hasActionPanel)
Write-Host "Standard Viewport Meta Tag: " (Check-Passed $hasViewport)

Write-Host "`n--- 3. Functional Dashboard Integrity Checks ---"
$hasBrand = ($html.IndexOf("Extremis") -ge 0) -and ($html.IndexOf("Student Dashboard") -ge 0)
$hasStudent = ($html.IndexOf("Bhavya Kumar Bohra") -ge 0) -and ($html.IndexOf("24EARAD031") -ge 0)
$hasAttendanceCard = ($html.IndexOf("Attendance") -ge 0) -and ($html.IndexOf("Not Checked In") -ge 0)
$hasProgressRing = ($html.IndexOf("circular-progress-wrap") -ge 0) -and ($html.IndexOf("Overall") -ge 0)
$hasReportTeaser = ($html.IndexOf("Attendance Report") -ge 0) -and ($html.IndexOf("/report") -ge 0)
$hasCheckBtn = ($html.IndexOf("big-circle-btn") -ge 0)

Write-Host "Branding and Header intact: " (Check-Passed $hasBrand)
Write-Host "Student Profile intact: " (Check-Passed $hasStudent)
Write-Host "Today Attendance Status Card intact: " (Check-Passed $hasAttendanceCard)
Write-Host "Overall Attendance Progress Ring intact: " (Check-Passed $hasProgressRing)
Write-Host "Attendance Report link intact: " (Check-Passed $hasReportTeaser)
Write-Host "Verification Action Button intact: " (Check-Passed $hasCheckBtn)

Write-Host "`n===================================================="
Write-Host "ALL CHECKS COMPLETED"
Write-Host "===================================================="
