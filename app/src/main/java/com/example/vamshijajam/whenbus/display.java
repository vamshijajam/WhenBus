package com.example.vamshijajam.whenbus;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class display extends AppCompatActivity {
    ArrayList<String> q2 = new ArrayList<String>();
    ArrayList<Myobj> q = new ArrayList<Myobj>();
    FirebaseDatabase database;
    public ArrayList<String> asd = new ArrayList<String>();
    public ArrayList<String> asd2 = new ArrayList<String>();
    ArrayList<String> busnostring = new ArrayList<String>();

    final HashMap<String, ArrayList<String> > latlong = new HashMap<>();

    final ArrayList<String> locs = new ArrayList<>();
    TreeMap<String,Integer> timesmap = new TreeMap<>();
    ArrayList<String> times = new ArrayList<>();

    final ArrayList<Firebaseclass> staticdata = new ArrayList<>();

    private LocationManager locationManager;
    private LocationListener locationListener;
    Location locationglob = null;
    double currlat;
    double currlong;
    String busno_given = null;
    Set<String> hs = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);





        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("TAG3","\n " + location.getLongitude() + " " + location.getLatitude());
                locationglob = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        configure();


        database = FirebaseDatabase.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        q2 = (ArrayList<String>)getIntent().getSerializableExtra("List");

        final String destination_given = q2.get(1);
        final String from_given = q2.get(2);
        busno_given = q2.get(0);


        Log.d("TAG","Second Acticity"+busno_given+" "+from_given+" "+destination_given);
        hs = new HashSet<>();
        final String[] a = new String[1];


        Query queryfinal = database.getReference();

        queryfinal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                for (DataSnapshot usersnapshot : dataSnapshot.child("static").getChildren()) {
                    if(usersnapshot.exists()) {
                        staticdata.add(new Firebaseclass(usersnapshot.child("location").getValue().toString(),usersnapshot.child("latitude").getValue().toString(),usersnapshot.child("longitude").getValue().toString(),usersnapshot.child("buses").getValue().toString()));
                    }
                }


                for(int i  = 0;i<staticdata.size();i++) {
                        List<String> items = null;
                        if (staticdata.get(i).location.equals(destination_given)) {
                            items = Arrays.asList(staticdata.get(i).buses.split("\\s*,\\s*"));
                            Log.d("TAG", " " + items.size());
                            hs.addAll(items);
                        }
                }

                for(int i  = 0;i<staticdata.size();i++) {
                    ArrayList<String> coord = new ArrayList<String>();
                    coord.add(staticdata.get(i).latitude);
                    coord.add(staticdata.get(i).longitude);
                    coord.add(staticdata.get(i).buses);
                    latlong.put(staticdata.get(i).location,coord);

                }

                if(from_given.length()==0) {
                    for(int i  = 0;i<staticdata.size();i++) {
                        List<String> items = Arrays.asList(staticdata.get(i).buses.split("\\s*,\\s*"));
                        for(String bus : items) {
                            if(hs.contains(bus)) {
                                locs.add(staticdata.get(i).location);
                            }
                        }
                    }

                    final ArrayList<String> lats = new ArrayList<>();
                    final ArrayList<String> longs = new ArrayList<>();
                    String finish = null;
                    for(String l :locs) {
                        lats.add(latlong.get(l).get(0));
                        longs.add(latlong.get(l).get(1));
                    }
                    Log.d("TAG",locs.size()+" ");
                    Log.d("TAG",hs.size()+" hs");
                    for(int i = 0;i<locs.size();i++) {
                        if(i!=0)
                            finish = finish + lats.get(i)+"%2C";
                        else
                            finish = lats.get(i) + "%2C";
                        finish = finish + longs.get(i);
                        if(i!=locs.size()-1) {
                            finish += "%7C";
                        }
                    }
                    String origin = Double.toString(currlat) + ","+Double.toString(currlong);
                    String url_dist = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+origin+"&destinations="+finish+"&key=AIzaSyBs6WoDvMO6SGb7wQUEAFEErMA7mBvnVeg";
                    Log.d("TAG",url_dist);
                    new jsontask().execute(url_dist);
                }
                else {
                    Set<String> hs2 = new HashSet<String>();
                    for (int i = 0; i < staticdata.size(); i++) {
                        if (!staticdata.get(i).location.equals(from_given)) continue;
                        List<String> items = Arrays.asList(staticdata.get(i).buses.split("\\s*,\\s*"));
                        hs2.addAll(items);
                    }
                    System.out.println(hs2.size()+" is size4");
                    hs.retainAll(hs2);
                    System.out.println(timesmap.size()+" is size2");
                    DatabaseReference myref1 = database.getReference().child("realtime");
                    Query Mytopquery1;
                    if(busno_given.length()==0) {
                        Mytopquery1 = myref1.orderByChild("time");
                    }
                    else {
                        Mytopquery1 = myref1.orderByChild("busname").equalTo(busno_given);
                    }
                    q = new ArrayList<Myobj>();
                    Mytopquery1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()) return;
                            for (DataSnapshot usersnapshot : dataSnapshot.getChildren()) {
                                if(usersnapshot.exists()) {
                                    String a = usersnapshot.child("busname").getValue().toString();
                                    String d = usersnapshot.child("location").getValue().toString();
                                    String e = usersnapshot.child("time").getValue().toString();
                                    Myobj object = new Myobj(a, d, e);
                                    if(hs.contains(a)) {
                                        q.add(object);
                                        Log.d("TAG", a);
                                    }
                                }
                            }
//                        System.out.println(q.size());
                            Collections.sort(q, new Comparator<Myobj>() {
                                @Override
                                public int compare(Myobj fruit2, Myobj fruit1)
                                {

                                    return  fruit1.time.compareTo(fruit2.time);
                                }
                            });
                            System.out.println(q.size());
                            for(int i = 0;i<q.size();i++) {
                                String p = q.get(i).busname;
                                String p1 = q.get(i).location;
                                String p2 = q.get(i).time;
                                String add = "Location: "+p1+"\n"+
                                        "Bus NO  : "+p+"\n"+
                                        "Time   : "+p2+"\n";
                                System.out.println("---------------------------ass");
                                asd.add(add);
                                busnostring.add(p);
                            }
                            populate();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public class jsontask extends AsyncTask<String,String,String>
    {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream Istream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(Istream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject dist_time = new JSONObject(result);
                JSONArray row = dist_time.getJSONArray("rows");
                Log.d("TAG",row.toString());
                JSONArray elements = row.getJSONObject(0).getJSONArray("elements");
                Log.d("TAG2",row.getJSONObject(0).toString());
                for(int i =0 ;i<elements.length();i++)
                {
                    String s = elements.getJSONObject(i).getJSONObject("duration").getString("text");
                    String ssplit[]  = s.split("\\s+");
                    times.add(ssplit[0]);
                    timesmap.put(ssplit[0],i);
                }
                int count = 0;
                int topresults = 2;


                ArrayList<String> loc2 = new ArrayList<>();
                for (Map.Entry<String,Integer> entry : timesmap.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    loc2.add(locs.get(value));

                    System.out.println(locs.get(value));

                    asd2.add("Bus Stop Name: "+ locs.get(value)+"\n"+"Time Away: "
                            + key+" minutes \n"+"Buses Available: "+latlong.get(locs.get(value)).get(2));
                    count += 1;
                    if(count>=topresults) break;
                }
                System.out.println(timesmap.size()+" is size");

                Set<String> hs2 = new HashSet<String>();
                for (int i = 0; i < staticdata.size(); i++) {
                    if (!loc2.contains(staticdata.get(i).location)) continue;
                    List<String> items = Arrays.asList(staticdata.get(i).buses.split("\\s*,\\s*"));
                    hs2.addAll(items);
                }
                System.out.println(hs2.size()+" is size4");
                hs.retainAll(hs2);

                System.out.println(timesmap.size()+" is size3");
                DatabaseReference myref1 = database.getReference().child("realtime");
                Query Mytopquery1;
                if(busno_given.length()==0) {
                    Mytopquery1 = myref1.orderByChild("time");
                }
                else {
                    Mytopquery1 = myref1.orderByChild("busname").equalTo(busno_given);
                }
                q = new ArrayList<Myobj>();
                Mytopquery1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) return;
                        for (DataSnapshot usersnapshot : dataSnapshot.getChildren()) {
                            if(usersnapshot.exists()) {
                                String a = usersnapshot.child("busname").getValue().toString();
                                String d = usersnapshot.child("location").getValue().toString();
                                String e = usersnapshot.child("time").getValue().toString();
                                Myobj object = new Myobj(a, d, e);
                                if(hs.contains(a)) {
                                    q.add(object);
                                    Log.d("TAG", a);
                                }
                            }
                        }
//                        System.out.println(q.size());
                        Collections.sort(q, new Comparator<Myobj>() {
                            @Override
                            public int compare(Myobj fruit2, Myobj fruit1)
                            {

                                return  fruit1.time.compareTo(fruit2.time);
                            }
                        });
                        System.out.println(q.size());
                        for(int i = 0;i<q.size();i++) {
                            String p = q.get(i).busname;
                            String p1 = q.get(i).location;
                            String p2 = q.get(i).time;
                            String add = "Location: "+p1+"\n"+
                                    "Bus NO  : "+p+"\n"+
                                    "Time   : "+p2+"\n";
                            System.out.println("---------------------------ass");
                            asd.add(add);
                            busnostring.add(p);
                        }
                        populate2();
                        populate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
    private void populate() {
//        String[] asd = {"jamuna bus-stop  to main gate", "gurunath to main gate", "tharamani guesthouse to main gate", "cse block to main gate"};
        ArrayAdapter<String> arrray = new ArrayAdapter<String>(this, R.layout.activity_listview, asd);

        final ListView list = (ListView) findViewById(R.id.listview1234);
        list.setAdapter(arrray);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clicked = list.getItemAtPosition(position);
                String aa = clicked.toString();
                ArrayList<String> str = new ArrayList<String>();
                str.add(aa);
                Log.d("TAG","Testing bus number "+position+" "+busnostring.get(position));
                str.add(busnostring.get(position));
                Intent myIntent = new Intent(display.this, Onboard.class);
                myIntent.putExtra("string",str);
                startActivity(myIntent);
            }
        });
    }

    private void populate2() {
//        String[] asd = {"jamuna bus-stop  to main gate", "gurunath to main gate", "tharamani guesthouse to main gate", "cse block to main gate"};
        ArrayAdapter<String> arrray = new ArrayAdapter<String>(this, R.layout.activity_listview, asd2);
        final ListView list = (ListView) findViewById(R.id.list_busstop);
        list.setAdapter(arrray);
    }



    private void configure() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
        locationManager.requestLocationUpdates("passive", 0, 0, locationListener);
        locationManager.requestLocationUpdates("network", 0, 0, locationListener);
        if(locationglob==null) {
            Log.d("TAG3","null again");
            currlat = 12.990157;
            currlong = 80.229341;
        }
        else {
            currlat = locationglob.getLatitude();
            currlong = locationglob.getLongitude();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure();
                break;
            default:
                break;

        }
    }


}
