package com.example.vamshijajam.whenbus;

/**
 * This is used for accessing the static database. The one that we get from information from the MTC database, the buses are the list of buses separated by a space indicating which are the buses that can visit the specified location.
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
