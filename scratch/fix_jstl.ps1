Get-ChildItem -Path src/main/webapp -Filter *.jsp -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $newContent = $content.Replace("jakarta.tags.core", "http://java.sun.com/jsp/jstl/core")
    [System.IO.File]::WriteAllText($_.FullName, $newContent, [System.Text.Encoding]::UTF8)
}
Write-Host "JSTL URI updated in all JSPs."
