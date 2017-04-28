package com.example.vamshijajam.whenbus;

/**
 * This class is for reading the real time data into an object, the busname field is the  bus id, 
 * the location is the last updated location and the time , and the time is the time at which the particular bus eas seen at the specified location.
 */

public class Myobj {
    public String busname;
    public String location;
    public String time;


    public Myobj(){
    // Default constructor required for calls to DataSnapshot.getValue(Post.class)

    }

    public Myobj( String busno, String Location,
             String Time)
    {
        this.busname = busno;
        this.location = Location;
        this.time = Time;
    }
}
