SET pathToJars=build/jars

start Start_Dispatcher.bat %pathToJars%
timeout /t 1
start Start_DatabaseServer.bat %pathToJars%
timeout /t 1
start Start_ApplicationServer.bat %pathToJars%
timeout /t 1
start Start_Client.bat %pathToJars%