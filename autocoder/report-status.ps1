# Reads Claude Code stream-json from stdin. Every raw line goes to -LogFile.
# The console only shows what a person watching the run needs:
#   - issue moves on the GitHub project board,
#   - OpenSpec tasks ticked off in a tasks.md,
#   - git commits,
#   - the runner's own messages (STATUS: lines highlighted), tool errors and the final result.
# Tool calls and sub-agent chatter stay in the log only.
# Used by claude.cmd; exits 1 when the run ended in an error.
param(
    [Parameter(Mandatory = $true)][string]$LogFile,
    [int]$ProjectNumber = 5,
    [string]$ProjectOwner = 'hexmasternl'
)

$ErrorActionPreference = 'Continue'
[Console]::InputEncoding = [Text.Encoding]::UTF8
[Console]::OutputEncoding = [Text.Encoding]::UTF8

$start = Get-Date
$failed = $false

# Tool calls that may produce a console event, by tool_use id, until their result arrives.
$pending = @{}

# Board lookups: option id -> status name, item id -> "#<issue> <title>".
$statusNames = @{}
$itemNames = @{}

function Write-Status([string]$Text, [string]$Color = 'Gray') {
    $elapsed = (Get-Date) - $start
    $stamp = '{0:HH:mm:ss} +{1:hh\:mm\:ss}' -f (Get-Date), $elapsed
    foreach ($line in ($Text -split "`r?`n")) {
        if ($line.Trim().Length -eq 0) { continue }
        Write-Host "[$stamp] $line" -ForegroundColor $Color
    }
}

function Shorten([string]$Text, [int]$Max = 140) {
    if ($null -eq $Text) { return '' }
    $flat = ($Text -replace '\s+', ' ').Trim()
    if ($flat.Length -gt $Max) { return $flat.Substring(0, $Max - 3) + '...' }
    return $flat
}

function Load-Board {
    try {
        $fields = gh project field-list $ProjectNumber --owner $ProjectOwner --format json 2>$null | ConvertFrom-Json
        foreach ($field in @($fields.fields)) {
            foreach ($option in @($field.options)) {
                if ($option.id) { $script:statusNames[$option.id] = $option.name }
            }
        }
    } catch { }
    try {
        $items = gh project item-list $ProjectNumber --owner $ProjectOwner --format json --limit 500 2>$null | ConvertFrom-Json
        foreach ($item in @($items.items)) {
            $label = if ($item.content.number) { "#$($item.content.number) $($item.content.title)" } else { $item.title }
            $script:itemNames[$item.id] = $label
        }
    } catch { }
}

function Result-Text($Block) {
    if ($Block.content -is [string]) { return $Block.content }
    return (@($Block.content) | ForEach-Object { $_.text }) -join "`n"
}

function Unquote([string]$Value) { return $Value.Trim('"', "'") }

# Status moves in a shell command: one per "gh project item-edit" with a single-select option.
function Find-BoardMoves([string]$Command) {
    $moves = @()
    $segments = @($Command -split 'gh project item-edit')
    foreach ($segment in $segments[1..($segments.Count)]) {
        if ($null -eq $segment) { continue }
        $item = [regex]::Match($segment, '--id[ =]+(\S+)')
        $option = [regex]::Match($segment, '--single-select-option-id[ =]+(\S+)')
        if ($item.Success -and $option.Success) {
            $moves += [pscustomobject]@{ Item = (Unquote $item.Groups[1].Value); Option = (Unquote $option.Groups[1].Value) }
        }
    }
    return $moves
}

# Tasks ticked in an edit of an OpenSpec tasks.md: unchecked in the old text, checked in the new.
function Find-CompletedTasks($ToolInput) {
    $path = [string]$ToolInput.file_path
    if ($path -notmatch 'openspec[\\/]changes[\\/]([^\\/]+)[\\/]tasks\.md$') { return @() }
    $change = $Matches[1]
    $open = @{}
    foreach ($m in [regex]::Matches([string]$ToolInput.old_string, '(?m)^\s*- \[ \]\s*(.+?)\s*$')) {
        $open[$m.Groups[1].Value] = $true
    }
    $done = @()
    foreach ($m in [regex]::Matches([string]$ToolInput.new_string, '(?m)^\s*- \[[xX]\]\s*(.+?)\s*$')) {
        if ($open.ContainsKey($m.Groups[1].Value)) {
            $done += [pscustomobject]@{ Change = $change; Task = $m.Groups[1].Value }
        }
    }
    return $done
}

function Remember-Tool($Block) {
    $name = $Block.name
    $toolInput = $Block.input
    if ($name -in 'Bash', 'PowerShell') {
        $command = [string]$toolInput.command
        $moves = @(Find-BoardMoves $command)
        if ($moves.Count -gt 0 -or $command -match 'git\b.*\bcommit\b') {
            $script:pending[$Block.id] = @{ Kind = 'shell'; Moves = $moves }
        }
    }
    elseif ($name -eq 'Edit') {
        $tasks = @(Find-CompletedTasks $toolInput)
        if ($tasks.Count -gt 0) { $script:pending[$Block.id] = @{ Kind = 'tasks'; Tasks = $tasks } }
    }
}

function Report-Tool($Block) {
    $entry = $script:pending[$Block.tool_use_id]
    if ($null -eq $entry) { return }
    $script:pending.Remove($Block.tool_use_id)
    if ($Block.is_error) { return }

    if ($entry.Kind -eq 'tasks') {
        foreach ($task in $entry.Tasks) {
            Write-Status "TASK    $($task.Change): $(Shorten $task.Task 110)" 'Magenta'
        }
        return
    }

    foreach ($move in $entry.Moves) {
        if (-not $script:itemNames.ContainsKey($move.Item) -or -not $script:statusNames.ContainsKey($move.Option)) { Load-Board }
        $issue = if ($script:itemNames.ContainsKey($move.Item)) { $script:itemNames[$move.Item] } else { "item $($move.Item)" }
        $status = if ($script:statusNames.ContainsKey($move.Option)) { $script:statusNames[$move.Option] } else { "option $($move.Option)" }
        Write-Status "BOARD   $(Shorten $issue 90) -> $status" 'Cyan'
    }

    $output = Result-Text $Block
    foreach ($m in [regex]::Matches($output, '(?m)^\[(\S+)(?: \(root-commit\))? ([0-9a-f]{7,})\] (.+)$')) {
        Write-Status "COMMIT  $($m.Groups[2].Value) $(Shorten $m.Groups[3].Value 110)" 'Blue'
    }
}

Load-Board

while ($null -ne ($raw = [Console]::In.ReadLine())) {
    Add-Content -Path $LogFile -Value $raw -Encoding utf8

    try { $evt = $raw | ConvertFrom-Json -ErrorAction Stop }
    catch { continue }

    $nested = $null -ne $evt.parent_tool_use_id

    switch ($evt.type) {
        'system' {
            if ($evt.subtype -eq 'init') {
                Write-Status "Claude started (model $($evt.model), session $($evt.session_id))" 'Cyan'
            }
        }
        'assistant' {
            foreach ($block in @($evt.message.content)) {
                if ($block.type -eq 'tool_use') { Remember-Tool $block }
                elseif ($block.type -eq 'text' -and -not $nested) {
                    $color = if ($block.text -match '(?m)^STATUS:') { 'Green' } else { 'White' }
                    Write-Status $block.text $color
                }
            }
        }
        'user' {
            foreach ($block in @($evt.message.content)) {
                if ($block.type -ne 'tool_result') { continue }
                Report-Tool $block
                if ($block.is_error -and -not $nested) {
                    Write-Status ('! tool error: ' + (Shorten (Result-Text $block))) 'Yellow'
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
