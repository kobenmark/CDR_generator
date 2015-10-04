package com.bgandrew.cdr_generator.model;

import com.google.gson.annotations.Expose;
import com.bgandrew.cdr_generator.utils.Utils;
import java.time.LocalDateTime;
import java.util.Random;

/** This class represents a customer from it's device perspective. It's used to control call times, 
 *  location and level of customer's activity. 
 *
 */

public class Customer implements Comparable<Customer> {
    
    public static enum CallType {
        Voice,
        SMS
    }
    
    @Expose
    private final long IMEI;
    @Expose
    private final long MSISDN;
    @Expose
    private final long IMSI;
    
    
    //TODO make location to change from one call to another
    @Expose
    private double latitude = Utils.generateLatitude();
    @Expose
    private double longitude = Utils.generateLongitude();
    
    @Expose
    private int activity = 0; 
    
    private LocalDateTime lastCallTime = LocalDateTime.MIN; // TODO set start date somewhere in configs
    
    private int numberOfCalls = 0;
    
    private static final Random random = new Random();
    
    
    public Customer() {
        this.IMEI = 0;
        this.MSISDN = 0;
        this.IMSI = 0;
        latitude = 0;
        longitude = 0;
        this.activity = 0;
        lastCallTime = LocalDateTime.MIN;
    }
    
    public Customer(long MSISDN, long IMEI, long IMSI, int activity, LocalDateTime startTime) {
        this.IMEI = IMEI;
        this.MSISDN = MSISDN;
        this.IMSI = IMSI;
        this.activity = activity;
        latitude = Utils.generateLatitude();
        longitude = Utils.generateLongitude();
        lastCallTime = startTime;
    }
    
    public static Customer generatePhone (LocalDateTime startTime) {
        return new Customer (Utils.generateMSISDN(), Utils.generateIMSI(), Utils.generateIMSI(), generateActivityValue(), startTime);
       
    }
   
    private static int generateActivityValue() {
        return random.nextInt(1000);  
    }
    
    
    private LocalDateTime pickNextCallTime(Customer recepient) {
        LocalDateTime result = lastCallTime.isAfter(recepient.getLastCallTime()) ? 
                lastCallTime : recepient.getLastCallTime();
        
        // interval between two calls for 1 person in normal condition. about 15 minutes.
        int offsetMinutes = 1 + Math.round(random.nextInt(30));
        // interval between calls at night time (about 6 hours)
        int offsetHours = 1 + random.nextInt(10);
                
        int hours = result.getHour();
        
        // we should rarely call at night so if last call was late next call should happen
        // with big delay
        if (hours < 8) {
            result = result.plusHours(offsetHours);
            if (result.getHour() > 8) {
                result = result.withHour(8);
            }
        } else if (hours < 10) {
            offsetMinutes *= 2;
        } 
        // after 8 pm calls happen a bit more rarely than normal.
        else if (hours > 20) {
            offsetMinutes *=2;
        }
        result = result.plusMinutes(offsetMinutes);
        
        return result;
        
    }
    
    
    public long getIMEI() {
        return IMEI;
    }

    public long getMSISDN() {
        return MSISDN;
    }

    public long getIMSI() {
        return IMSI;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getLastCallTime() {
        return lastCallTime;
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }
    
    public int getActivity() {
        return activity;
    }
    
    
    public CDR call (Customer recepient, CallType type, int duration, int length) {
        
        LocalDateTime time = pickNextCallTime(recepient);
        
        if (time.isBefore(lastCallTime) || time.isBefore(recepient.lastCallTime) ) {
            throw new IllegalArgumentException("This call can interfere with previous calls. " +
                   "call time is " + time + " last call time is " + lastCallTime + " and recepient "
                    + "last call time is " + recepient.getLastCallTime());
        }
        numberOfCalls++;
        lastCallTime = time.plusSeconds(duration);
        recepient.lastCallTime = lastCallTime; // same as for current phone
        recepient.numberOfCalls++;
        
        return new CDR (this, recepient, time, type, duration, length);
    }
        
    @Override
    public int compareTo(Customer p) {
        
        if (this == p)
            return 0;
        if (p == null)
            return -1;
        if (activity == p.activity) 
            return 0;
        
        return activity > p.activity ? 1 : -1;
        
    }

    @Override
    public String toString() {
        return "Customer{" + "IMEI=" + IMEI + ", MSISDN=" + MSISDN + ", IMSI=" + IMSI + ", latitude=" + latitude + ", longitude=" + longitude + ", activity=" + activity + ", lastCallTime=" + lastCallTime + ", numberOfCalls=" + numberOfCalls + '}';
    }
    
    
    
    
}
