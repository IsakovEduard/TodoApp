<#
.SYNOPSIS
    Starts the Todo application in development mode (dev profile).

.DESCRIPTION
    Runs the Spring Boot app with the "dev" profile (local H2 file ./data/todo-dev and a
    dev-only JWT secret), builds the web UI first, and opens the browser when the app is ready.
    Press Ctrl+C in this window to stop the application.

.PARAMETER Port
    Port to listen on. Default 8080.

.PARAMETER SkipFrontend
    Skip the frontend build. Faster, but the UI is not rebuilt, so UI changes do not show up.

.PARAMETER NoBrowser
    Do not open the browser automatically.

.EXAMPLE
    .\scripts\start-dev.ps1
    .\scripts\start-dev.ps1 -Port 9090 -SkipFrontend -NoBrowser
#>
[CmdletBinding()]
param(
    [ValidateRange(1, 65535)]
    [int]$Port = 8080,
    [switch]$SkipFrontend,
    [switch]$NoBrowser
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

function Test-PortFree([int]$candidate) {
    try {
        $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Any, $candidate)
        $listener.Start()
        $listener.Stop()
        return $true
    }
    catch {
        return $false
    }
}

function Test-FileLocked([string]$path) {
    if (-not (Test-Path $path)) {
        return $false
    }
    try {
        $stream = [System.IO.File]::Open($path, 'Open', 'ReadWrite', 'None')
        $stream.Close()
        return $false
    }
    catch [System.IO.IOException] {
        return $true
    }
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'Java was not found. Install JDK 17 and make sure "java" is on the PATH.'
}

if (-not (Test-PortFree $Port)) {
    throw "Port $Port is already in use. Stop the other application or run: .\scripts\start-dev.ps1 -Port <free port>"
}

$databaseFile = Join-Path $projectRoot 'data\todo-dev.mv.db'
if (Test-FileLocked $databaseFile) {
    throw "The dev database ($databaseFile) is in use, so another instance of the app is probably already running. Stop it first (Ctrl+C in its window), then start again."
}

if ($SkipFrontend -and -not (Test-Path (Join-Path $projectRoot 'target\classes\static\index.html'))) {
    Write-Warning 'The UI has not been built yet, so only the API will work. Run once without -SkipFrontend.'
}

$url = "http://localhost:$Port"
$browserJob = $null
if (-not $NoBrowser) {
    $browserJob = Start-Job -ArgumentList $url -ScriptBlock {
        param($target)
        for ($attempt = 0; $attempt -lt 180; $attempt++) {
            Start-Sleep -Seconds 2
            try {
                $response = Invoke-WebRequest -Uri "$target/login" -UseBasicParsing -TimeoutSec 2
                if ($response.StatusCode -eq 200) {
                    Start-Process $target
                    return
                }
            }
            catch {
                # not ready yet
            }
        }
    }
}

$mavenArguments = @(
    'spring-boot:run',
    '-Dspring-boot.run.profiles=dev',
    "-Dspring-boot.run.arguments=--server.port=$Port"
)
if ($SkipFrontend) {
    $mavenArguments += '-DskipFrontend'
}

Write-Host "Starting Todo app (dev profile) on $url ..." -ForegroundColor Cyan
Write-Host 'The first start builds the web UI and takes a little longer. Press Ctrl+C to stop.' -ForegroundColor DarkGray

try {
    & (Join-Path $projectRoot 'mvnw.cmd') @mavenArguments
    if ($LASTEXITCODE -ne 0) {
        throw "Maven exited with code $LASTEXITCODE."
    }
}
finally {
    if ($browserJob) {
        Stop-Job $browserJob -ErrorAction SilentlyContinue
        Remove-Job $browserJob -Force -ErrorAction SilentlyContinue
    }
}
