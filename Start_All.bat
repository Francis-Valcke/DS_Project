SET pathToJars=build/jars

start Start_Dispatcher.bat %pathToJars% dispatcher 1000 1001
timeout /t 1
start Start_DatabaseServer.bat %pathToJars% %cd%/%pathToJars%/db_alpha.db 1100 1101
timeout /t 1
start Start_ApplicationServer.bat %pathToJars% app_alpha 1200 1201
timeout /t 1
start Start_Client.bat %pathToJars%