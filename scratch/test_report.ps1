$s = New-Object Microsoft.PowerShell.Commands.WebRequestSession
sqlite3 extremis.db "UPDATE students SET session_token=NULL WHERE roll_no='24EARAD031';"
$login = Invoke-WebRequest -Uri "http://localhost:8088/login" -Method POST -WebSession $s -Body @{
    role = "student"
    rollNo = "24EARAD031"
    password = "StudentPassword123!"
} -UseBasicParsing -MaximumRedirection 5

$report = Invoke-WebRequest -Uri "http://localhost:8088/report" -WebSession $s -UseBasicParsing
Write-Host "REPORT_HTTP_STATUS: $($report.StatusCode)"
Write-Host "CONTAINS_DAILY_LOG: $($report.Content -match 'Daily Log')"
Write-Host "CONTAINS_OCTOBER: $($report.Content -match 'October 2026')"
