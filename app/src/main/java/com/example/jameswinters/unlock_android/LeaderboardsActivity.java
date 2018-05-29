package com.example.jameswinters.unlock_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

public class LeaderboardsActivity extends AppCompatActivity {
    // ArrayList<UnlockUser> UserList = new ArrayList<>();
    private ArrayList<POI> POIList;
    private ArrayList<sPOI> sPOIList;
    private ArrayList<hPOI> hPOIList;
    TableLayout tbl;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        Intent i;
        i = getIntent();
        Bundle b = i.getExtras();
        if (b!=null) {
            POIList = (ArrayList<POI>) b.getSerializable("POIList");
            sPOIList = (ArrayList<sPOI>) b.getSerializable("sPOIList");
            hPOIList=(ArrayList<hPOI>) b.getSerializable("hPOIList");
        }
        tbl= findViewById(R.id.leaderboard);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Scores");
        scoresRef.orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                MakeTable(dataSnapshot.getKey(),dataSnapshot.getValue(Integer.class));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        /*DatabaseReference fbDb;
        fbDb = FirebaseDatabase.getInstance().getReference().child("Usernames");
        fbDb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            UserList.add(new UnlockUser(snapshot.child("Name").getValue(String.class)));
                        }
                        for (final UnlockUser u:UserList){
                            System.out.println(u.getUsername());
                            DatabaseReference lockdb = FirebaseDatabase.getInstance().getReference().child("POIList").child(u.getUsername());
                            lockdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    u.setUnlockCount(dataSnapshot.child("Score").getValue(Integer.class));
                                    MakeTable(u);
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
                });*/
    }

    void MakeTable(String username, int score){
        TableRow tr = new TableRow(LeaderboardsActivity.this);
        TextView un = new TextView(LeaderboardsActivity.this);
        un.setText(username);
        un.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView pu = new TextView(LeaderboardsActivity.this);
        String poisunlocked = Integer.toString(POIList.size()-score);
        pu.setText(poisunlocked);
        pu.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(un);
        tr.addView(pu);
        tbl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));


    }

}


/* Adding another test comment to verify S05, test branch and master branch have been merged succesfully */