package com.bgandrew.cdr_generator.model;

import com.google.gson.annotations.Expose;
import com.bgandrew.cdr_generator.utils.Utils;
import java.time.DayOfWeek;
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
    
    
    private Location location;
    
    // set of home , work and other locations 
    private final LocationSet locationSet;
    
    
    
    @Expose
    private int activity = 0; 
    
    private LocalDateTime lastCallTime = LocalDateTime.MIN; // TODO set start date somewhere in configs
    
    private int numberOfCalls = 0;
    
    private static final Random random = new Random();
    
    
    public Customer() {
        IMEI = 0;
        MSISDN = 0;
        IMSI = 0;
        activity = 0;
        lastCallTime = LocalDateTime.MIN;
        locationSet = null;
    }
    
    private Customer(long MSISDN, long IMEI, long IMSI, int activity, LocalDateTime startTime, LocationSet locationSet) {
        this.IMEI = IMEI;
        this.MSISDN = MSISDN;
        this.IMSI = IMSI;
        this.activity = activity;
        this.locationSet = locationSet;  
        lastCallTime = startTime;
        
        location = Utils.CITY_LOCATIONS.get(locationSet.city); // initial value, just in case
        
    }
    
    // TODO convert to Builder
    public static Customer generatePhone (LocalDateTime startTime, LocationSet locationSet) {
        return new Customer (Utils.generateMSISDN(locationSet.city), Utils.generateIMSI(), Utils.generateIMSI(), generateActivityValue(), startTime, locationSet);
       
    }
   
    private static int generateActivityValue() {
        return random.nextInt(1000);  
    }
    
    
    private LocalDateTime generateCallTime(Customer recepient) {
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
    
    
    private void updateCallLocation(LocalDateTime time) {
        // generate random location to avoid determenistic behavior
        Location newLocation = Utils.randomLocationInCity(locationSet.city);
        
        final int hour = time.getHour();
        final DayOfWeek day = time.getDayOfWeek();
        
        if (hour < 8 || hour > 20) {
            // almost certainly at home
            if (Utils.trueWithProbability(0.99f)) newLocation = locationSet.home;
        } else  if (hour > 10 && hour < 19) {            
            if (day.compareTo(DayOfWeek.SATURDAY) <0) { // working day
                // almost certainly at work
                if (Utils.trueWithProbability(0.99f)) newLocation = locationSet.work;
            } else { // weekend
                if (Utils.trueWithProbability(0.6f)) {
                    // having fun in some other place
                    newLocation = locationSet.other;
                } else if (Utils.trueWithProbability(0.4f)) {
                    //or at home
                    newLocation = locationSet.home;
                }
            }
        } else { // 8-10 or 19-20
            if (Utils.trueWithProbability(0.5f)) {
                // either on the way, or in "other" place
                newLocation = locationSet.other;
            }
        }
        
        location = newLocation;
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
    
    public LocalDateTime getLastCallTime() {
        return lastCallTime;
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }
    
    public int getActivity() {
        return activity;
    }
    
    public Location getLocation(){
        return location;
    }
    
    
    public CDR call (Customer recepient, CallType type, int duration, int length) {
        
        final LocalDateTime time = generateCallTime(recepient);
        
        if (time.isBefore(lastCallTime) || time.isBefore(recepient.lastCallTime) ) {
            throw new IllegalArgumentException("This call can interfere with previous calls. " +
                   "call time is " + time + " last call time is " + lastCallTime + " and recepient "
                    + "last call time is " + recepient.getLastCallTime());
        }
        
        updateCallLocation(time);
        recepient.updateCallLocation(time);
        
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
        return "Customer{" + "IMEI=" + IMEI + ", MSISDN=" + MSISDN + ", IMSI=" + IMSI + ", location=" + location + ", locationSet=" + locationSet + ", activity=" + activity + ", lastCallTime=" + lastCallTime + ", numberOfCalls=" + numberOfCalls + '}';
    }

    
    
    
}
