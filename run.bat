@echo off
title Launcher Sistem Manajemen Klinik
echo ============================================================
echo  🏥 Menjalankan Aplikasi Sistem Manajemen Klinik
echo ============================================================
echo.

:: Set environment JDK dari Apache NetBeans
set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [INFO] Menggunakan JDK: %JAVA_HOME%
echo [INFO] Menjalankan perintah Maven...
echo.

:: Jalankan kompilasi dan aplikasi JavaFX
"C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" clean compile javafx:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Gagal menjalankan aplikasi. Pastikan server database MySQL Anda sudah aktif!
)

echo.
pause
