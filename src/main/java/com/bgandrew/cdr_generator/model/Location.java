package com.bgandrew.cdr_generator.model;

/**
 *
 * simple value class for location
 */
public class Location {
   
    public final double longitude;
    public final double latitude;
    
    public Location (double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    } 
    
    @Override
    public String toString() {
        return "(" + latitude + ", " + longitude + ")";
    }
    
}
