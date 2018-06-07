package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.datatype.Duration;

import de.codecrafters.tableview.TableDataAdapter;
import de.codecrafters.tableview.TableHeaderAdapter;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

//LeaderboardsActivity  accesses the scores section of the firebase database to pull down all public users
//scores and then adds them in order of score from highest to lowest to a tableview
public class LeaderboardsActivity extends AppCompatActivity {

    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    int count;
    TableView<String[]> table;
    TableDataAdapter<String[]> myDataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_leaderboards);
        Intent i;
        count=0;
        i = getIntent();

        POIList = MainActivity.readPOIsFromSD(POIList, currentUser);
        sPOIList = MainActivity.readsPOIsFromSD(sPOIList, currentUser);
        hPOIList = MainActivity.readhPOIsFromSD(hPOIList, currentUser);
        DatabaseReference scoresRef;
        table = findViewById(R.id.leaderboard);

        //set table headings
        TableHeaderAdapter myHeaderAdapter =
                new SimpleTableHeaderAdapter(this, "Username", "Unlock Progress %");

        table.setHeaderAdapter(myHeaderAdapter);
        //get database reference for scores section of database
        scoresRef = FirebaseDatabase.getInstance().getReference().child("Scores");
        //orderby value returns the scores from lowest to highest
        scoresRef.orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //add the username (the key) and the score (the value) to the table
                MakeTable(dataSnapshot.getKey(), dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void MakeTable(String username, int score){
        //since scores are stored as total locked POIs,such that the orderByValue
        //function of the firebase reading will return from high to low, to draw unlock progess in the table from high to low
        //we must subtract the number of locked locations from the total number of locations.
        int size = POIList.size()+hPOIList.size()+sPOIList.size();
        int poisunlocked = size-score;
        float poisunlockedpercent = ((float)poisunlocked/(float)size)*100;
        int poisint = (int)poisunlockedpercent;
        String poiout = Integer.toString(poisint);

        if (count ==0){//set initial table value
            String[][] myData = new String[][]{{username,poiout}};
            myDataAdapter =
                    new SimpleTableDataAdapter(this,myData);
            table.setDataAdapter(myDataAdapter);
        }
        if(count>0){
            myDataAdapter.add(new String[]{username, poiout});
        }
        count++;//increment each time function is called
    }

}
