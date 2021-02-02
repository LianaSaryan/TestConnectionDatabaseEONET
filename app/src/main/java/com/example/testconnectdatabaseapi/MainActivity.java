package com.example.testconnectdatabaseapi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.BreakIterator;

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

    // setting up API request
    private RequestQueue queue;
    static String TAG_SEARCH_NAME = "link" ;

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


        // prepare to request data from EONET API
        queue = Volley.newRequestQueue(this);

        // cancelling all requests about this search if in queue
        queue.cancelAll(TAG_SEARCH_NAME);

        // first StringRequest: getting items searched
        StringRequest stringRequest = searchNameStringRequest();

        // executing the request (adding to queue)
        queue.add(stringRequest);
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

    private StringRequest searchNameStringRequest() {

          String url = "https://eonet.sci.gsfc.nasa.gov/api/v3/events?limit=2";
        // 1st param => type of method (GET/PUT/POST/PATCH/etc)
        // 2nd param => complete url of the API
        // 3rd param => Response.Listener -> Success procedure
        // 4th param => Response.ErrorListener -> Error procedure
        return new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    //3rd param - method onResponse - lays the code procedure of success return
                    // SUCCESS
                    @Override
                    public void onResponse(String response) {
                        // try/catch block for returned JSON data
                        // see API's documentation for returned format
                        try {
                            JSONObject result = new JSONObject(response);
//                            int maxItems = result.getInt("end");
                            JSONArray resultList = result.getJSONArray("events");

                            // display results of events array (for testing)
                            System.out.println(resultList);

                            // get event data
                            for (int i = 0; i < resultList.length(); i++) {

                                JSONObject jsonobject = resultList.getJSONObject(i);
                                event_id = jsonobject.getString("id");
                                event_title = jsonobject.getString("title");
                                event_description = jsonobject.getString("description");
                                event_link = jsonobject.getString("link");
                                event_closed = jsonobject.getString("closed");

                                // increment count for database index
                                count++;

                                // write to database
                                writeEventData(count, event_closed, event_description, event_id, event_link, event_title);

                                // display on screen (for testing)
                                System.out.println(event_id);
                                System.out.println(event_title);
                                System.out.println(event_description);
                                System.out.println(event_link);
                                System.out.println(event_closed);
                            }

                            // catch for the JSON parsing error
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } // public void onResponse(String response)
                }, // Response.Listener<String>()
                new Response.ErrorListener() {
                    // 4th param - method onErrorResponse lays the code procedure of error return
                    // ERROR
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // display a simple message on the screen
                        Toast.makeText(MainActivity.this, "Events source is not responding (EONET API)", Toast.LENGTH_LONG).show();
                    }
                });
    }

}


