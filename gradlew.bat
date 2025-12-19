@echo off
setlocal
set DIR=%~dp0
if exist "%DIR%gradle\wrapper\gradle-wrapper.jar" (
  set WRAPPER_JAR=%DIR%gradle\wrapper\gradle-wrapper.jar
) else (
  echo Could not find gradle-wrapper.jar. Please run 'gradle wrapper' to generate the wrapper files.
  exit /b 1
)
set CLASSPATH=%WRAPPER_JAR%
set JAVA_EXE=java
if defined JAVA_HOME set JAVA_EXE="%JAVA_HOME%\bin\java"
%JAVA_EXE% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
