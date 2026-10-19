param([Parameter(Mandatory=$true)][string]$OutputDirectory)
$ErrorActionPreference='Stop'
Add-Type -AssemblyName System.Drawing
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null
& "$PSScriptRoot/wigo-icon.ps1" -OutputDirectory $OutputDirectory
$image=New-Object System.Drawing.Bitmap(1024,500,[System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
$g=[System.Drawing.Graphics]::FromImage($image)
$icon=[System.Drawing.Image]::FromFile((Join-Path $OutputDirectory 'icone-512.png'))
$title=New-Object System.Drawing.Font('Segoe UI',52,[System.Drawing.FontStyle]::Bold,[System.Drawing.GraphicsUnit]::Pixel)
$subtitle=New-Object System.Drawing.Font('Segoe UI',25,[System.Drawing.FontStyle]::Regular,[System.Drawing.GraphicsUnit]::Pixel)
$small=New-Object System.Drawing.Font('Segoe UI',18,[System.Drawing.FontStyle]::Regular,[System.Drawing.GraphicsUnit]::Pixel)
try {
    $g.Clear([System.Drawing.ColorTranslator]::FromHtml('#124DB3'))
    $g.TextRenderingHint=[System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
    $g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($icon,18,86,328,328)
    $g.DrawString('Wigo Indústria',$title,[System.Drawing.Brushes]::White,346,167)
    $g.DrawString('Construa. Automatize. Defenda.',$subtitle,[System.Drawing.Brushes]::White,350,244)
    $g.DrawString('WIGO TECNOLOGIA',$small,[System.Drawing.Brushes]::White,350,310)
    $image.Save((Join-Path $OutputDirectory 'destaque.png'),[System.Drawing.Imaging.ImageFormat]::Png)
} finally { $g.Dispose();$image.Dispose();$icon.Dispose();$title.Dispose();$subtitle.Dispose();$small.Dispose() }
