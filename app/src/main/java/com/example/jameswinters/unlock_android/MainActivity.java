package com.example.jameswinters.unlock_android;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    public ArrayList<POI> POIList = new ArrayList<>();
    public ArrayList<sPOI> sPOIList = new ArrayList<>();
    public ArrayList<hPOI> hPOIList = new ArrayList<>();
    private Intent i;
    //String username;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("POIs");
    DatabaseReference myhPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("hPOIs");
    DatabaseReference mysPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("sPOIs");
    TextView currentUserText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(currentUser.getDisplayName());
        i = getIntent();
        currentUserText = findViewById(R.id.currentUser);
        currentUserText.setText(getString(R.string.currentUserID,currentUser.getDisplayName()));
        Bundle b = i.getExtras();
        if (b!=null){
            POIList = (ArrayList<POI>)b.getSerializable("POIList");
            sPOIList =(ArrayList<sPOI>)b.getSerializable("sPOIList");
            hPOIList =(ArrayList<hPOI>)b.getSerializable("hPOIList");
            myPOIRef.setValue(POIList);
            mysPOIRef.setValue(sPOIList);
            myhPOIRef.setValue(hPOIList);
            checkForChangeInPOIs();
        }
        /*myPOIRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        //whenever data at this location is updated.
                        GenericTypeIndicator<ArrayList<POI>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<POI>>() {};
                        POIList = dataSnapshot.getValue(genericTypeIndicator);
                        init(POIList);
                //System.out.println("test"+ POIList.get(0).getTitle());
                checkForChangeInPOIs();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
//        System.out.println("test"+ POIList.get(0).getTitle());
        //      System.out.println("test spoi"+ sPOIList.get(0).getTitle());
        signOut();
        if(isServicesOk()){
            init();
            statusCheck();
        }
    }



    public void signOut(){
        Button signOut = findViewById(R.id.signOutMain);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent i = new Intent(MainActivity.this, EmailPasswordActivity.class);
                startActivity(i);
            }
        });
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void checkForChangeInPOIs() {
        ArrayList<POI> compare;
        POIXMLParser poixmlParser = new POIXMLParser(this);
        compare = poixmlParser.getPOIList();
        int size = POIList.size();
        while (compare.size() > size) {
            POIList.add(compare.get(size));
            myPOIRef.setValue(POIList);
            size++;
        }
        while (compare.size() < size) {
            POIList.remove(size-1);
            myPOIRef.setValue(POIList);
            size--;
        }
    }

    private void init(){
        Button button = (Button)findViewById(R.id.btnMap);
        Button button2 = (Button)findViewById(R.id.btnMap1);
        Button button3 = (Button)findViewById(R.id.btnMap2);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QRActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("POIList",POIList);
                bundle.putSerializable("sPOIList",sPOIList);
                bundle.putSerializable("hPOIList",hPOIList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("POIList",POIList);
                bundle.putSerializable("sPOIList",sPOIList);
                bundle.putSerializable("hPOIList",hPOIList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LeaderboardsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("POIList",POIList);
                bundle.putSerializable("sPOIList",sPOIList);
                bundle.putSerializable("hPOIList",hPOIList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and user can make map requests
            return true;
        }

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOk: an error occurred but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    @Override
    public void onBackPressed(){

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}