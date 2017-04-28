package com.example.vamshijajam.whenbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Onboard extends AppCompatActivity {

    ArrayList<String> p = new ArrayList<String>();
    final String regex = "^[a-zA-Z ]+$";
    HashMap<String,Integer> d = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        p = (ArrayList<String>)getIntent().getSerializableExtra("string");
        String display = p.get(0);
        final String busname = p.get(1);
        TextView textView = (TextView) findViewById(R.id.onboard_textView);
        textView.setText(display);

        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        Log.d("TAG", myRef.toString());

        Query Mytopquery = myRef.child("static").orderByChild("location");

        Log.d("TAG", Mytopquery.toString());
        final ArrayList boarding = new ArrayList();
        // Query code to ensure that the bus stop user chooses to board matches the bus he chose to be on
        Mytopquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                if(dataSnapshot.exists()) {
                    for (DataSnapshot usersnapshot : dataSnapshot.getChildren()) {
                        if(usersnapshot.exists()) {
                            String a = usersnapshot.child("buses").getValue().toString();
                            List<String> items = Arrays.asList(a.split("\\s*,\\s*"));
                            Set<String> hs = new HashSet<>();
                            hs.addAll(items);
                            if (hs.contains(busname)) {
                                boarding.add(usersnapshot.child("location").getValue().toString());
                                d.put(usersnapshot.child("location").getValue().toString(), 1);
                                Log.d("TAG", usersnapshot.child("location").getValue().toString());
                            }
                        }
                    }
                    Set<String> hs = new HashSet<>();
                    hs.addAll(boarding);
                    boarding.clear();
                    boarding.addAll(hs);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final AutoCompleteTextView onboard_auto = (AutoCompleteTextView) findViewById(R.id.onboard_location);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, boarding);
        onboard_auto.setThreshold(1);
        onboard_auto.setAdapter(adapter);

        Button onboard = (Button) findViewById(R.id.Onboard_button);
        onboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView auto_bustop = (AutoCompleteTextView) findViewById(R.id.onboard_location);
                String boarding_at = auto_bustop.getText().toString();
                Log.d("TAG",boarding_at);
                if (TextUtils.isEmpty(boarding_at)) {
                    auto_bustop.setError(" Enter your Destination");
                    return;
                } else if (!d.containsKey(boarding_at)) {
                    Toast.makeText(Onboard.this, "Invalid Bus Point", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    Log.d("TAG","valid");
                    Calendar c = Calendar.getInstance();
                    // Adding the object to the database, the code fot crowsourcing
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                    String formattedtime = df.format(c.getTime());
                    Myobj obj1 = new Myobj(busname, boarding_at,formattedtime);
                    System.out.println(formattedtime);
                    DatabaseReference newref = myRef.child("realtime").push();
                    newref.setValue(obj1);
                    Intent intent = new Intent(Onboard.this,thanks.class);
                    startActivity(intent);
                    return;
                }

            }
        });

        class item_select implements AdapterView.OnItemClickListener {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Objects Destination_item_selected = (Objects) onboard_auto.getAdapter().getItem(position);
            }
        }
    }



}
