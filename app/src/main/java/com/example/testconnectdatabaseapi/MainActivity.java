package com.example.testconnectdatabaseapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // Event data
    static int count = 2;
    static String event_closed = "";
    static String event_description = "";
    static String event_id = "";
    static String event_link = "";
    static String event_title = "";


    // establishing database reference
    static FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "Firebase Connection Success", Toast.LENGTH_LONG).show();

        count = 2;
        event_closed = "null";
        event_description = "null";
        event_id = "EONET_5224";
        event_link = "https://eonet.sci.gsfc.nasa.gov/api/v3/events/EONET_5224";
        event_title = "Wildfire - Monte Patria Commune (Rotonda Monte Patria Fire), Chile";

        writeEventData(count, event_closed, event_description, event_id, event_link, event_title);
        Toast.makeText(MainActivity.this, "Writing to Database Success", Toast.LENGTH_LONG).show();

        readEventData();
        Toast.makeText(MainActivity.this, "Reading to Database Success", Toast.LENGTH_LONG).show();

    }

    public static void writeEventData(int count, String closed, String description, String id, String link, String title) {
        // Prepare to write to database
        DatabaseReference myRef = database.getReference("Events/" + Integer.toString(count));

        // Write to database
        myRef.child("closed").setValue(event_closed);
        myRef.child("description").setValue(event_description);
        myRef.child("id").setValue(event_id);
        myRef.child("link").setValue(event_link);
        myRef.child("title").setValue(event_title);
    }

    public static void readEventData(){
        // Prepare to read from database
        DatabaseReference myRef = database.getReference("Events/" + Integer.toString(count));

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Event meta data
                String title = dataSnapshot.child("title").getValue().toString();
                String id = dataSnapshot.child("id").getValue().toString();
                String description = dataSnapshot.child("description").getValue().toString();
                String link = dataSnapshot.child("link").getValue().toString();
                String closed = dataSnapshot.child("closed").getValue().toString();
                
                // Print read data onto Logcat console
                System.out.println("title: " + title);
                System.out.println("id: " + id);
                System.out.println("description: " + description);
                System.out.println("link: " + link);
                System.out.println("closed: " + closed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                System.out.println("Failed to read value." + databaseError.toException());
            }
        };
        myRef.addValueEventListener(eventListener);
    }

}


