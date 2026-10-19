# Renders the same simple vector geometry used by Android, for store/QA previews.
param([Parameter(Mandatory=$true)][string]$OutputDirectory)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
foreach ($size in @(512,48)) {
    $bitmap = New-Object System.Drawing.Bitmap($size,$size)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $blue = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#124DB3'))
    try {
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
        $graphics.Clear($blue.Color)
        $graphics.ScaleTransform(($size/108.0),($size/108.0))
        $points = @(@(29,73),@(29,49),@(43,41),@(43,49),@(57,41),@(57,51),@(64,51),@(64,32),@(74,32),@(77,73))
        [System.Drawing.PointF[]]$polygon = $points | ForEach-Object { New-Object System.Drawing.PointF($_[0],$_[1]) }
        $graphics.FillPolygon([System.Drawing.Brushes]::White,$polygon)
        foreach ($x in @(35,49,63)) { $graphics.FillRectangle($blue,[single]$x,[single]58,[single]8,[single]7) }
        $bitmap.Save((Join-Path $OutputDirectory "icone-$size.png"),[System.Drawing.Imaging.ImageFormat]::Png)
    } finally { $graphics.Dispose(); $bitmap.Dispose(); $blue.Dispose() }
}
