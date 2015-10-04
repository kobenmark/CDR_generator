Usage : java -jar CDR_generator.jar -d <devise number> (optional, default value is 1000)  -c <calls per device> (optional. default value is 10000) -export(optional - makes program to store devices list in devices.json file) -import (optional - will cause program to load devices from devices.json file)

This will generate data.csv file in the same directory as a jar file with records for <device number> of devises each making <calls per device> number of calls.
For example, device "java -jar CDR_generator.jar -d 10 -c 10" will generate small file with 100 records (for test purposes). "java -jar CDR_generator.jar" generates a file with 10 million records.
