@echo off
rem Autocoder: starts Claude Code unattended to implement the triaged issues
rem in the Ready column of the "final approach" GitHub project (number 5).
rem
rem Usage:  autocoder\claude.cmd [model]
rem         model defaults to sonnet (for example: autocoder\claude.cmd opus)
rem
rem Creates worktree ..\final-approach-worktrees\runner-yyyyMMdd-xxxx on branch
rem runner/run-yyyyMMdd-xxxx from origin/development, then runs Claude in it
rem with the instructions in autocoder\runner-prompt.md.

setlocal EnableExtensions

set "MODEL=%~1"
if "%MODEL%"=="" set "MODEL=sonnet"

for %%i in ("%~dp0..") do set "REPO=%%~fi"
set "PROMPT_FILE=%REPO%\autocoder\runner-prompt.md"

where claude >nul 2>&1 || (echo claude is not on the PATH. & exit /b 1)
where gh >nul 2>&1 || (echo gh is not on the PATH. & exit /b 1)
if not exist "%PROMPT_FILE%" (echo Missing %PROMPT_FILE%. & exit /b 1)

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd"') do set "RUNDATE=%%i"
for /f %%i in ('powershell -NoProfile -Command "-join (1..4 | ForEach-Object { [char[]]'abcdefghijklmnopqrstuvwxyz0123456789' | Get-Random })"') do set "RAND=%%i"

set "RUN_ID=runner-%RUNDATE%-%RAND%"
set "BRANCH=runner/run-%RUNDATE%-%RAND%"
for %%i in ("%REPO%\..\final-approach-worktrees\%RUN_ID%") do set "WORKTREE=%%~fi"

echo Run:      %RUN_ID%
echo Branch:   %BRANCH%
echo Worktree: %WORKTREE%
echo Model:    %MODEL%
echo.

git -C "%REPO%" fetch origin || (echo git fetch failed. & exit /b 1)
git -C "%REPO%" worktree add -b "%BRANCH%" "%WORKTREE%" origin/development || (echo Could not create the worktree. & exit /b 1)

cd /d "%WORKTREE%" || exit /b 1

rem Stream every event as JSON and turn it into status lines on the terminal.
rem The raw stream is kept in the log file next to the worktree.
set "LOG_FILE=%WORKTREE%.log"
echo Log:      %LOG_FILE%
echo.

claude -p "You are the autocoder runner. Read and follow %PROMPT_FILE% exactly. Run id: %RUN_ID%. Branch: %BRANCH%. Worktree: %WORKTREE%." --model %MODEL% --dangerously-skip-permissions --output-format stream-json --verbose | powershell -NoProfile -ExecutionPolicy Bypass -File "%REPO%\autocoder\report-status.ps1" -LogFile "%LOG_FILE%"
set "RESULT=%ERRORLEVEL%"

echo.
echo Autocoder run %RUN_ID% finished with exit code %RESULT%.
echo Full log: %LOG_FILE%
echo The worktree stays at %WORKTREE% until you remove it:
echo   git worktree remove "%WORKTREE%"
exit /b %RESULT%
