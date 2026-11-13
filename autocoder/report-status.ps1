# Reads Claude Code stream-json from stdin and prints a readable, timestamped
# status line per event. Every raw line is also appended to -LogFile.
# Used by claude.cmd; exits 1 when the run ended in an error.
param(
    [Parameter(Mandatory = $true)][string]$LogFile
)

$ErrorActionPreference = 'Continue'
[Console]::InputEncoding = [Text.Encoding]::UTF8
[Console]::OutputEncoding = [Text.Encoding]::UTF8

$start = Get-Date
$failed = $false

function Write-Status([string]$Text, [string]$Color = 'Gray', [bool]$Nested = $false) {
    $elapsed = (Get-Date) - $start
    $stamp = '{0:HH:mm:ss} +{1:hh\:mm\:ss}' -f (Get-Date), $elapsed
    $indent = if ($Nested) { '    | ' } else { '' }
    foreach ($line in ($Text -split "`r?`n")) {
        if ($line.Trim().Length -eq 0) { continue }
        Write-Host "[$stamp] $indent$line" -ForegroundColor $Color
    }
}

function Shorten([string]$Text, [int]$Max = 140) {
    if ($null -eq $Text) { return '' }
    $flat = ($Text -replace '\s+', ' ').Trim()
    if ($flat.Length -gt $Max) { return $flat.Substring(0, $Max - 3) + '...' }
    return $flat
}

function Describe-Tool($Name, $ToolInput) {
    switch ($Name) {
        'Bash'       { return "run: $(Shorten $ToolInput.command)" }
        'PowerShell' { return "run: $(Shorten $ToolInput.command)" }
        'Read'       { return "read $($ToolInput.file_path)" }
        'Write'      { return "write $($ToolInput.file_path)" }
        'Edit'       { return "edit $($ToolInput.file_path)" }
        'Glob'       { return "find $($ToolInput.pattern)" }
        'Grep'       { return "search '$(Shorten $ToolInput.pattern 60)'" }
        'Agent'      { return "agent $($ToolInput.subagent_type): $(Shorten $ToolInput.description 80)" }
        'Task'       { return "agent $($ToolInput.subagent_type): $(Shorten $ToolInput.description 80)" }
        'Skill'      { return "skill $($ToolInput.skill)" }
        'TodoWrite'  {
            $todos = @($ToolInput.todos)
            $done = @($todos | Where-Object { $_.status -eq 'completed' }).Count
            $current = $todos | Where-Object { $_.status -eq 'in_progress' } | Select-Object -First 1
            $now = if ($current) { " - now: $(Shorten $current.content 80)" } else { '' }
            return "todo $done/$($todos.Count) done$now"
        }
        default      { return "$Name" }
    }
}

while ($null -ne ($raw = [Console]::In.ReadLine())) {
    Add-Content -Path $LogFile -Value $raw -Encoding utf8

    try { $evt = $raw | ConvertFrom-Json -ErrorAction Stop }
    catch { Write-Status $raw 'DarkGray'; continue }

    $nested = $null -ne $evt.parent_tool_use_id

    switch ($evt.type) {
        'system' {
            if ($evt.subtype -eq 'init') {
                Write-Status "Claude started (model $($evt.model), session $($evt.session_id))" 'Cyan'
            }
        }
        'assistant' {
            foreach ($block in @($evt.message.content)) {
                if ($block.type -eq 'text') {
                    $color = if ($block.text -match '(?m)^STATUS:') { 'Green' } elseif ($nested) { 'DarkGray' } else { 'White' }
                    Write-Status $block.text $color $nested
                }
                elseif ($block.type -eq 'tool_use') {
                    Write-Status ('> ' + (Describe-Tool $block.name $block.input)) 'DarkCyan' $nested
                }
            }
        }
        'user' {
            foreach ($block in @($evt.message.content)) {
                if ($block.type -eq 'tool_result' -and $block.is_error) {
                    $text = if ($block.content -is [string]) { $block.content } else { ($block.content | ForEach-Object { $_.text }) -join ' ' }
                    Write-Status ('! tool error: ' + (Shorten $text)) 'Yellow' $nested
                }
            }
        }
        'result' {
            $failed = [bool]$evt.is_error
            $minutes = [math]::Round($evt.duration_ms / 60000, 1)
            $cost = if ($null -ne $evt.total_cost_usd) { ', cost $' + [math]::Round($evt.total_cost_usd, 2) } else { '' }
            $color = if ($failed) { 'Red' } else { 'Green' }
            Write-Status "Run ended: $($evt.subtype) after $minutes min, $($evt.num_turns) turns$cost" $color
            if ($evt.result) { Write-Status $evt.result $color }
        }
    }
}

if ($failed) { exit 1 }
exit 0
