# Android Fingerprint Alma Script'i
# Bu script SHA-1 ve SHA-256 fingerprint'lerini almanıza yardımcı olur

Write-Host "=== Android Keystore Fingerprint Alma Aracı ===" -ForegroundColor Cyan
Write-Host ""

# Java/Keytool yollarını ara
$possibleKeytoolPaths = @(
    "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe",
    "C:\Program Files\Android\Android Studio\jre\bin\keytool.exe",
    "C:\Program Files\Java\jdk*\bin\keytool.exe",
    "C:\Program Files\Java\jre*\bin\keytool.exe",
    "$env:JAVA_HOME\bin\keytool.exe"
)

$keytoolPath = $null
foreach ($path in $possibleKeytoolPaths) {
    $resolved = Get-ChildItem -Path $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($resolved) {
        $keytoolPath = $resolved.FullName
        break
    }
}

if (-not $keytoolPath) {
    Write-Host "HATA: keytool bulunamadı!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Lütfen aşağıdaki adımlardan birini takip edin:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "YÖNTEM 1: Android Studio Terminal'den" -ForegroundColor Green
    Write-Host "1. Android Studio'yu açın"
    Write-Host "2. Alt + F12 ile Terminal'i açın"
    Write-Host "3. Aşağıdaki komutu çalıştırın:"
    Write-Host ""
    Write-Host '   gradlew signingReport' -ForegroundColor White
    Write-Host ""
    Write-Host "YÖNTEM 2: Manuel keytool komutu" -ForegroundColor Green
    Write-Host "Debug keystore için:"
    Write-Host ""
    Write-Host '   keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android' -ForegroundColor White
    Write-Host ""
    Write-Host "YÖNTEM 3: Gradle Wrapper ile (Java gerektirir)" -ForegroundColor Green
    Write-Host "Proje dizininde:"
    Write-Host ""
    Write-Host '   .\gradlew signingReport' -ForegroundColor White
    Write-Host ""
    Write-Host "NOT: SHA-1 ve SHA-256 değerlerini Firebase Console veya Google Cloud Console'a girmeniz gerekecek." -ForegroundColor Cyan
    exit 1
}

Write-Host "Keytool bulundu: $keytoolPath" -ForegroundColor Green
Write-Host ""

# Debug keystore yolu
$debugKeystore = "$env:USERPROFILE\.android\debug.keystore"

if (Test-Path $debugKeystore) {
    Write-Host "=== DEBUG KEYSTORE FINGERPRINTS ===" -ForegroundColor Yellow
    Write-Host "Keystore: $debugKeystore" -ForegroundColor Gray
    Write-Host ""

    & $keytoolPath -list -v -keystore $debugKeystore -alias androiddebugkey -storepass android -keypass android 2>&1 | Select-String -Pattern "(SHA1|SHA256)"

    Write-Host ""
} else {
    Write-Host "UYARI: Debug keystore bulunamadı: $debugKeystore" -ForegroundColor Yellow
    Write-Host ""
}

# Release keystore kontrolü (varsa)
$releaseKeystorePaths = @(
    ".\app\*.keystore",
    ".\app\*.jks",
    ".\*.keystore",
    ".\*.jks"
)

$releaseKeystore = $null
foreach ($pattern in $releaseKeystorePaths) {
    $found = Get-ChildItem -Path $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $releaseKeystore = $found.FullName
        break
    }
}

if ($releaseKeystore) {
    Write-Host "=== RELEASE KEYSTORE BULUNDU ===" -ForegroundColor Yellow
    Write-Host "Keystore: $releaseKeystore" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Release keystore için şifre ve alias bilgisi gerekiyor." -ForegroundColor Cyan
    Write-Host "Manuel olarak çalıştırmak için:"
    Write-Host ""
    Write-Host "   keytool -list -v -keystore `"$releaseKeystore`" -alias <YOUR_ALIAS>" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host "NOT: Release keystore bulunamadı. Sadece debug fingerprint'leri gösteriliyor." -ForegroundColor Gray
    Write-Host ""
}

Write-Host "=== BİLGİ ===" -ForegroundColor Cyan
Write-Host "Bu fingerprint'leri şu yerlerde kullanabilirsiniz:"
Write-Host "  - Firebase Console (Project Settings > Your apps)"
Write-Host "  - Google Cloud Console (APIs & Services > Credentials)"
Write-Host "  - Google Maps Platform"
Write-Host "  - Google Sign-In"
Write-Host "  - Facebook App Dashboard"
Write-Host ""

