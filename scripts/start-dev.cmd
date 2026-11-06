@echo off
rem Double-click or run from any shell. Passes all arguments to start-dev.ps1 (e.g. -Port 9090 -NoBrowser).
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-dev.ps1" %*
if errorlevel 1 pause
