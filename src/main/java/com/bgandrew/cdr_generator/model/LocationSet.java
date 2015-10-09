/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bgandrew.cdr_generator.model;

/**
 *
 *  Represents set of location (home, work, somewhere else) where a customer can appear.
 */
public class LocationSet {
    
    // list of  supported cities
    public enum CITY {
        LA, // Los Angeles
        SD, // San Diego
        SJ  // San Jose
    }
    
    public final CITY city;
    public final Location home;
    public final Location  work;
    public final Location other;
    
    public LocationSet(CITY city, Location home, Location work, Location other) {
        this.city = city;
        this.home = home;
        this.work = work;
        this.other = other;
    }

    @Override
    public String toString() {
        return "LocationSet{" + "city=" + city + ", home=" + home + ", work=" + work + ", other=" + other + '}';
    }
    
    
    
}
