$ErrorActionPreference = 'Continue'

Write-Host "--- 1. Testing GET /login ---"
$sess = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$res = Invoke-WebRequest -Uri "http://localhost:8088/login" -WebSession $sess -UseBasicParsing
Write-Host "Status Code:" $res.StatusCode
if ($res.Content -match "Extremis" -and $res.Content -match "Student ID / RTU Roll No") {
    Write-Host "GET /login: PASSED"
} else {
    Write-Host "GET /login: FAILED"
}

Write-Host "`n--- 2. Testing POST /login for Bhavya (24EARAD031) ---"
$loginBody = "role=student&rollNo=24EARAD031&password=StudentPassword123!"
$loginRes = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $loginBody -ContentType "application/x-www-form-urlencoded" -WebSession $sess -MaximumRedirection 0 -UseBasicParsing -ErrorAction SilentlyContinue
Write-Host "Login Response Status:" $loginRes.StatusCode
Write-Host "Redirect Location:" $loginRes.Headers.Location

Write-Host "`n--- 3. Testing GET /dashboard ---"
$dashRes = Invoke-WebRequest -Uri "http://localhost:8088/dashboard" -WebSession $sess -UseBasicParsing
Write-Host "Dashboard Status:" $dashRes.StatusCode
if ($dashRes.Content -match "Bhavya Kumar Bohra" -and $dashRes.Content -match "24EARAD031") {
    Write-Host "Dashboard Content: PASSED (Bhavya Kumar Bohra found)"
} else {
    Write-Host "Dashboard Content: FAILED"
}

Write-Host "`n--- 4. Testing GET /notifications ---"
$notifRes = Invoke-WebRequest -Uri "http://localhost:8088/notifications" -WebSession $sess -UseBasicParsing
Write-Host "Notifications Status:" $notifRes.StatusCode
if ($notifRes.Content -match "Important" -and $notifRes.Content -match "Academic") {
    Write-Host "Notifications Content: PASSED"
} else {
    Write-Host "Notifications Content: FAILED"
}

Write-Host "`n--- 5. Testing GET /report ---"
$reportRes = Invoke-WebRequest -Uri "http://localhost:8088/report" -WebSession $sess -UseBasicParsing
Write-Host "Report Status:" $reportRes.StatusCode
if ($reportRes.Content -match "Attendance Report" -and $reportRes.Content -match "Present") {
    Write-Host "Report Content: PASSED"
} else {
    Write-Host "Report Content: FAILED"
}

Write-Host "`n--- 6. Testing GET /calendar ---"
$calRes = Invoke-WebRequest -Uri "http://localhost:8088/calendar" -WebSession $sess -UseBasicParsing
Write-Host "Calendar Status:" $calRes.StatusCode
if ($calRes.Content -match "Attendance Calendar" -and $calRes.Content -match "Legend") {
    Write-Host "Calendar Content: PASSED"
} else {
    Write-Host "Calendar Content: FAILED"
}

Write-Host "`n--- 7. Testing GET /checkin ---"
$checkinRes = Invoke-WebRequest -Uri "http://localhost:8088/checkin" -WebSession $sess -UseBasicParsing
Write-Host "CheckIn Status:" $checkinRes.StatusCode
if ($checkinRes.Content -match "Location Captured" -and $checkinRes.Content -match "Photo Verification") {
    Write-Host "CheckIn Content: PASSED"
} else {
    Write-Host "CheckIn Content: FAILED"
}

Write-Host "`n--- 8. Testing GET /profile ---"
$profRes = Invoke-WebRequest -Uri "http://localhost:8088/profile" -WebSession $sess -UseBasicParsing
Write-Host "Profile Status:" $profRes.StatusCode
if ($profRes.Content -match "Bhavya Kumar Bohra" -and $profRes.Content -match "bhavya.24earad031@aryacollege.edu.in") {
    Write-Host "Profile Content: PASSED"
} else {
    Write-Host "Profile Content: FAILED"
}

Write-Host "`n--- 9. Testing Single Active Session Enforcement ---"
$sess2 = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginRes2 = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method Post -Body $loginBody -ContentType "application/x-www-form-urlencoded" -WebSession $sess2 -MaximumRedirection 0 -UseBasicParsing -ErrorAction SilentlyContinue
Write-Host "Second Session Login Status:" $loginRes2.StatusCode

# Now first session requests /dashboard
$firstSessCheck = Invoke-WebRequest -Uri "http://localhost:8088/dashboard" -WebSession $sess -MaximumRedirection 0 -UseBasicParsing -ErrorAction SilentlyContinue
Write-Host "First Session /dashboard Status (should redirect to login):" $firstSessCheck.StatusCode
Write-Host "Redirect Location:" $firstSessCheck.Headers.Location

$firstSessLogin = Invoke-WebRequest -Uri "http://localhost:8088/login" -WebSession $sess -UseBasicParsing
if ($firstSessLogin.Content -match "You were logged out because your account was opened on another device.") {
    Write-Host "Single Session Invalidation Message: PASSED"
} else {
    Write-Host "Single Session Invalidation Message: FAILED"
}
