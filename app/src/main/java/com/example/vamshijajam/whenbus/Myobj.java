package com.example.vamshijajam.whenbus;

/**
 * Created by vamshijajam on 4/8/2017.
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
