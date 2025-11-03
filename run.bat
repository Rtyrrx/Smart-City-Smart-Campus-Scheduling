@echo off
REM Batch script to run SmartCity project without IDE issues

echo ================================================
echo Smart City Scheduler - Run Script
echo ================================================
echo.

:menu
echo Select an option:
echo 1. Compile the project
echo 2. Run all tests
echo 3. Run scheduler on ALL datasets (automated)
echo 4. Run scheduler on small_cyclic.json
echo 5. Run scheduler on small_dag.json
echo 6. Run scheduler on medium_cyclic.json
echo 7. Run scheduler on large_dag.json
echo 8. Regenerate datasets (optional - datasets already exist)
echo 9. Exit
echo.
set /p choice="Enter your choice (1-9): "

if "%choice%"=="1" goto compile
if "%choice%"=="2" goto test
if "%choice%"=="3" goto run_all
if "%choice%"=="4" goto run_small_cyclic
if "%choice%"=="5" goto run_small_dag
if "%choice%"=="6" goto run_medium_cyclic
if "%choice%"=="7" goto run_large_dag
if "%choice%"=="8" goto generate
if "%choice%"=="9" goto end
goto menu

:compile
echo.
echo Compiling project...
mvn clean compile
echo.
pause
goto menu

:test
echo.
echo Running tests...
mvn test
echo.
pause
goto menu

:run_all
echo.
echo ================================================
echo Running scheduler on ALL 9 datasets
echo ================================================
echo.
echo [1/9] Processing small_dag.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\small_dag.json"
echo.
echo [2/9] Processing small_cyclic.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\small_cyclic.json"
echo.
echo [3/9] Processing small_mixed.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\small_mixed.json"
echo.
echo [4/9] Processing medium_dag.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\medium_dag.json"
echo.
echo [5/9] Processing medium_cyclic.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\medium_cyclic.json"
echo.
echo [6/9] Processing medium_dense.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\medium_dense.json"
echo.
echo [7/9] Processing large_dag.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\large_dag.json"
echo.
echo [8/9] Processing large_cyclic.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\large_cyclic.json"
echo.
echo [9/9] Processing large_dense.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\large_dense.json"
echo.
echo ================================================
echo Completed processing all 9 datasets!
echo ================================================
echo.
pause
goto menu

:generate
echo.
echo ================================================
echo WARNING: Datasets already exist!
echo ================================================
echo This will OVERWRITE existing datasets in data/ folder.
echo.
set /p confirm="Are you sure you want to regenerate? (Y/N): "
if /i "%confirm%"=="Y" goto do_generate
if /i "%confirm%"=="y" goto do_generate
echo.
echo Generation cancelled. Returning to menu...
echo.
pause
goto menu

:do_generate
echo.
echo Generating datasets...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.data.DatasetGenerator" -Dexec.args="data"
echo.
pause
goto menu

:run_small_cyclic
echo.
echo Running scheduler on small_cyclic.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\small_cyclic.json"
echo.
pause
goto menu

:run_small_dag
echo.
echo Running scheduler on small_dag.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\small_dag.json"
echo.
pause
goto menu

:run_medium_cyclic
echo.
echo Running scheduler on medium_cyclic.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\medium_cyclic.json"
echo.
pause
goto menu

:run_large_dag
echo.
echo Running scheduler on large_dag.json...
mvn exec:java -Dexec.mainClass="com.rtyrrx.mst.SmartCityScheduler" -Dexec.args="data\large_dag.json"
echo.
pause
goto menu

:end
echo.
echo Goodbye!
exit /b 0
