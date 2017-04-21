package com.example.vamshijajam.whenbus;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    private static final String TAG = "TAG";
    public String query_Destination, query_Busno;
    AutoCompleteTextView auto, auto1, auto2;
    ArrayList dest = new ArrayList();
    ArrayList busses = new ArrayList();
    final String regex = "^[a-zA-Z ]+$";
    public ArrayList<Myobj> q = new ArrayList<>();
    FirebaseDatabase database;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("WhenBus");
//        toolbar.setLogo(R.drawable.b1);
        // Write a message to the database

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("static");
        Log.d("TAG", myRef.toString());
        Query Mytopquery = myRef.orderByChild("location");
        Mytopquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) return;
                for (DataSnapshot usersnapshot : dataSnapshot.getChildren()) {
                    if (usersnapshot.exists()) {
                        dest.add(usersnapshot.child("location").getValue().toString());
                        busses.add(usersnapshot.child("buses").getValue().toString());
                        Log.d("TAG", usersnapshot.child("location").getValue().toString());
                    }
                }

                Set<String> hs = new HashSet<>();
                hs.addAll(dest);
                dest.clear();
                dest.addAll(hs);
                hs.clear();
                hs.addAll(busses);
                busses.clear();
                busses.addAll(hs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        auto = (AutoCompleteTextView) findViewById(R.id.autocomplete_Destination);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, dest);
        auto.setThreshold(1);
        auto.setAdapter(adapter);

//        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
//        String[] items = new String[]{"", "2", "three"};
//        ArrayAdapter<String> adapter12 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter12);
//

        auto2 = (AutoCompleteTextView) findViewById(R.id.autocomplete_Location);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.select_dialog_item, dest);
        auto2.setThreshold(1);
        auto2.setAdapter(adapter);

        auto1 = (AutoCompleteTextView) findViewById(R.id.autoComplete_busno);
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.select_dialog_item, busses);
        auto1.setThreshold(1);
        auto1.setAdapter(adapter1);


        Button b = (Button) findViewById(R.id.search_bus);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AutoCompleteTextView destination_text = (AutoCompleteTextView) findViewById(R.id.autocomplete_Destination);
                AutoCompleteTextView from_text = (AutoCompleteTextView) findViewById(R.id.autocomplete_Location);
                AutoCompleteTextView busno_text = (AutoCompleteTextView) findViewById(R.id.autoComplete_busno);

                String from_given = from_text.getText().toString();
                String destination_given = destination_text.getText().toString();
                String busno_given = busno_text.getText().toString();
//                System.out.println(from_given + "----" + destination_given + "---" + busno_given);
                if (TextUtils.isEmpty(destination_given)) {
                    destination_text.setError(" Enter your Destination");
                    return;
                } else if (!destination_given.matches(regex)) {
                    Toast.makeText(MainActivity.this, "Invalid Destination", Toast.LENGTH_LONG).show();
                    return;
                }


                ArrayList<String> q = new ArrayList<String>();
                q.add(busno_given);
                q.add(destination_given);
                q.add(from_given);
                Intent MyIntent = new Intent(MainActivity.this, display.class);
                MyIntent.putExtra("List", q);
                startActivity(MyIntent);


            }
        });
        class item_select implements AdapterView.OnItemClickListener {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Objects Destination_item_selected = (Objects) auto.getAdapter().getItem(position);
                query_Destination = Destination_item_selected.toString();
                Objects Busno_item_selected = (Objects) auto1.getAdapter().getItem(position);
                query_Busno = Busno_item_selected.toString();
            }
        }

    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.help: {
                Toast.makeText(this, "display help activity", Toast.LENGTH_SHORT).show();
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                TextView textView = new TextView(MainActivity.this);
                textView.setTextSize(16);
                textView.setPadding(50,0,50,0);
                textView.setTextColor(Color.parseColor("#000000"));
                textView.setText(    "\n How to use the app?\n\n"+

                        "1. Enter the destination in Chennai where you want to go(this is a mandatory requirement).\n"+
                        "2. Enter the preferred bus stop source location, if you don't enter this we will suggest you based on the nearest 3 bus stops to your location.\n"+
                        "3. Enter a particular bus number that you want to travel via.\n"+
                        "4. Once you choose the appropriate parameters, we will suggest you the nearest buses to you.\n"+
                        "5. Click on ONBOARD to contribute to the real time app and join us in making our database wider!!");
                layout.addView(textView);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Help?");
                builder.setView(layout);
                builder.setIcon(R.drawable.b1);
                AlertDialog alertdialog = builder.create();
                builder.setNegativeButton("OK", null);
                builder.show();
                return true;
            }
            case R.id.feedback:
                Toast.makeText(this, "You pressed FEEDBACK", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
