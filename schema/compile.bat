@echo off
protoc --java_out=./ *.proto
echo Compilation completed
pause