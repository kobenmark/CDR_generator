package com.bgandrew.cdr_generator.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Utility class to hold CDR representation
 *
 */

public class CDR {
    
    
    
    CDR (Customer caller, Customer recipient, LocalDateTime timestamp,
    		Customer.CallType type, int duration, int length) {
        this.caller = caller;
        this.recipient = recipient;
        this.timestamp = timestamp;
        this.type = type;
        this.duration = duration;
        this.length = length;
    }
    
    
    private final Customer caller;
    private final Customer recipient;
    
    private final LocalDateTime timestamp;
    
    private final Customer.CallType type;
    
    private final int duration; // seconds, for 
    private final int length; // symbols, for SMS
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(formatTimestamp(timestamp)).append(Constants.DELIMITER).
           append(caller.getMSISDN()).append(Constants.DELIMITER).
           append(caller.getIMEI()).append(Constants.DELIMITER).
           append(caller.getIMSI()).append(Constants.DELIMITER).
           append(caller.getLocation().latitude).append(Constants.DELIMITER).
           append(caller.getLocation().longitude).append(Constants.DELIMITER).
           
           append(recipient.getMSISDN()).append(Constants.DELIMITER).
           append(recipient.getIMEI()).append(Constants.DELIMITER).
           append(recipient.getIMSI()).append(Constants.DELIMITER).
           append(recipient.getLocation().latitude).append(Constants.DELIMITER).
           append(recipient.getLocation().longitude).append(Constants.DELIMITER).
                
           append(type).append(Constants.DELIMITER).
           append(duration).append(Constants.DELIMITER).
           append(length);
        
         
        return sb.toString();
    }
    
    private static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    
}
