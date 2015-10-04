Usage : java -jar CDR_generator.jar -d <devise number> (optional, default value is 1000)  -c <calls per device> (optional. default value is 10000)

This will generate data.csv file in the same directory as a jar file with records for <device number> of devises each making <calls per device> number of calls.
For example, device "java -jar CDR_generator.jar -d 10 -c 10" will generate small file with 100 records (for test purposes). "java -jar CDR_generator.jar" generates a file with 10 million records.

Some issues in current version: 
1. each devise making same amount of calls (going to implement some kind of activity coefficient which should be random for each devise and) 
2. locations for the devises are constant (easy to change). All locations are somewhere around Los Angeles.
3. generating time is quite long (30 s for 10 million records on my pc, going to optimise it on stage 4)  
