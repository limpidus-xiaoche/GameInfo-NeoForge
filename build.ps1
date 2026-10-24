# GameInfo 1.21.1 NeoForge 离线构建脚本
# 用法: powershell -ExecutionPolicy Bypass -File build.ps1
# 依赖: 本地 1.21.1 NeoForge 运行库（无需 maven.neoforged.net / Gradle）+ JDK 21

$ErrorActionPreference = "Stop"
$javac = "C:\Users\Administrator\AppData\Local\Temp\jdk21\jdk-21.0.2\bin\javac.exe"
$jar   = "C:\Users\Administrator\AppData\Local\Temp\jdk21\jdk-21.0.2\bin\jar.exe"
$proj  = "D:\Users\GameInfo-1.21.1"
$src   = Join-Path $proj "src\main\java"
$res   = Join-Path $proj "src\main\resources"
$out   = Join-Path $proj "build\classes"
$outjar = Join-Path $proj "build\GameInfo-1.21.1-3.0.8.jar"

# 目标实例实际运行 NeoForge 21.1.250（fml 4.0.44）
$core = @(
    "D:\.minecraft\libraries\net\neoforged\neoforge\21.1.250\neoforge-21.1.250-universal.jar",
    "D:\.minecraft\libraries\net\neoforged\neoforge\21.1.250\neoforge-21.1.250-client.jar",
    "D:\.minecraft\libraries\net\minecraft\client\1.21.1-20240808.144430\client-1.21.1-20240808.144430-srg.jar"
)
# 版本 JSON 导出的运行时库路径清单（缺失项为 linux/macos natives，与编译无关）
$libs = Get-Content "C:\Users\Administrator\AppData\Local\Temp\gameinfo263\libpaths.txt" | Where-Object { Test-Path $_ }
$cp = ($libs + $core) -join ";"

if (Test-Path $out) { Remove-Item $out -Recurse -Force }
New-Item -ItemType Directory -Force -Path $out | Out-Null

$files = (Get-ChildItem $src -Recurse -Filter "*.java").FullName
& $javac -encoding UTF-8 -cp $cp -d $out $files
if ($LASTEXITCODE -ne 0) { throw "编译失败" }

if (Test-Path $outjar) { Remove-Item $outjar -Force }
& $jar --create --file $outjar -C $out . -C $res .
if ($LASTEXITCODE -ne 0) { throw "打包失败" }

Write-Host "构建完成: $outjar ($((Get-Item $outjar).Length) bytes)"
