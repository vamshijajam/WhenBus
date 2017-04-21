package com.example.vamshijajam.whenbus;

/**
 * Created by vamshijajam on 4/10/2017.
 */

public class Firebaseclass {
    public String location;
    public String latitude;
    public String longitude;
    public String buses;

    public Firebaseclass(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)

    }

    public Firebaseclass(String location,String latitude,String longitude,String buses)
    {
        this.buses = buses;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
