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

public class LeaderboardsActivity extends AppCompatActivity {
    // ArrayList<UnlockUser> UserList = new ArrayList<>();
    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    TableLayout tbl;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        Intent i;
        i = getIntent();
        Bundle b = i.getExtras();
        POIList = MainActivity.readPOIsFromSD(POIList, currentUser);
        sPOIList = MainActivity.readsPOIsFromSD(sPOIList, currentUser);
        hPOIList = MainActivity.readhPOIsFromSD(hPOIList, currentUser);
        DatabaseReference scoresRef;
        tbl = findViewById(R.id.leaderboard);
        scoresRef = FirebaseDatabase.getInstance().getReference().child("Scores");
        scoresRef.orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
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
        TableRow tr = new TableRow(LeaderboardsActivity.this);
        TextView un = new TextView(LeaderboardsActivity.this);
        un.setWidth(100);
        un.setHeight(50);
        un.setGravity(Gravity.CENTER);
        un.setText(username);
        un.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView pu = new TextView(LeaderboardsActivity.this);
        int size = POIList.size()+hPOIList.size()+sPOIList.size();
        int poisunlocked = size-score;
        float poisunlockedpercent = ((float)poisunlocked/(float)size)*100;
        int poisint = (int)poisunlockedpercent;
        String poiout = Integer.toString(poisint);
        pu.setWidth(100);
        pu.setGravity(Gravity.CENTER);
        pu.setHeight(50);
        pu.setText(poiout);
        pu.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(un);
        tr.addView(pu);
        tbl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));


    }

}
