package com.example.jameswinters.unlock_android;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public ArrayList<POI> POIList = new ArrayList<>();
    public ArrayList<sPOI> sPOIList = new ArrayList<>();
    public ArrayList<hPOI> hPOIList = new ArrayList<>();
    public ArrayList<bPOI> bPOIList = new ArrayList<>();
    private Intent i;
    //String username;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("POIs");
    DatabaseReference myhPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("hPOIs");
    DatabaseReference mysPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("sPOIs");
    DatabaseReference mybPOIRef = database.getReference("POIList").child(currentUser.getDisplayName()).child("bPOIs");

    TextView currentUserText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        //getStoragePermissions();
        System.out.println(currentUser.getDisplayName());
        i = getIntent();
        currentUserText = findViewById(R.id.currentUser);
        currentUserText.setText(getString(R.string.currentUserID,currentUser.getDisplayName()));
        Bundle b = i.getExtras();
        POIList = readPOIsFromSD(POIList,currentUser);
        sPOIList = readsPOIsFromSD(sPOIList,currentUser);
        hPOIList = readhPOIsFromSD(hPOIList,currentUser);
        bPOIList= readbPOIsFromSD(bPOIList,currentUser);
       // Toast.makeText(this,bPOIList.get(0).getTitle(),Toast.LENGTH_SHORT).show();
        checkForChangeInPOIs();
        checkForChangeInsPOIs();
        checkForChangeInhPOIs();
        checkForChangeInbPOIs();
        myPOIRef.setValue(POIList);
        mysPOIRef.setValue(sPOIList);
        myhPOIRef.setValue(hPOIList);
        mybPOIRef.setValue(bPOIList);
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
            size++;
        }
        while (compare.size() < size) {
            POIList.remove(size-1);
            size--;
        }
    }
    private void checkForChangeInsPOIs() {
        ArrayList<sPOI> compare;
        sPOIXMLParser spoixmlParser = new sPOIXMLParser(this);
        compare = spoixmlParser.getsPOIList();
        int size = sPOIList.size();
        while (compare.size() > size) {
            sPOIList.add(compare.get(size));
            size++;
        }
        while (compare.size() < size) {
            sPOIList.remove(size-1);
            size--;
        }
    }
    private void checkForChangeInhPOIs() {
        ArrayList<hPOI> compare;
        hPOIXMLParser hpoixmlParser = new hPOIXMLParser(this);
        compare = hpoixmlParser.gethPOIList();
        int size = hPOIList.size();
        while (compare.size() > size) {
            hPOIList.add(compare.get(size));
            size++;
        }
        while (compare.size() < size) {
            hPOIList.remove(size-1);
            size--;
        }
    }
    private void checkForChangeInbPOIs() {
        ArrayList<bPOI> compare;
        bPOIXMLParser bpoixmlParser = new bPOIXMLParser(this);
        compare = bpoixmlParser.getbPOIList();
        int size = bPOIList.size();
        while (compare.size() > size) {
            bPOIList.add(compare.get(size));
            size++;
        }
        while (compare.size() < size) {
            bPOIList.remove(size-1);
            size--;
        }
    }

    private void init(){
        Button button = (Button)findViewById(R.id.btnMap);
        Button button2 = (Button)findViewById(R.id.btnMap1);
        Button button3 = (Button)findViewById(R.id.btnMap2);
        Button button5 = (Button)findViewById(R.id.button_settings);


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QRActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LeaderboardsActivity.class);
                startActivity(intent);
            }

        });
        button5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        savePOIListToSD(POIList,currentUser);
        savehPOIListToSD(hPOIList,currentUser);
        savesPOIListToSD(sPOIList,currentUser);
        savebPOIListToSD(bPOIList,currentUser);
        myPOIRef.setValue(POIList);
        mysPOIRef.setValue(sPOIList);
        myhPOIRef.setValue(hPOIList);
        mybPOIRef.setValue(bPOIList);
        updateScore(POIList,sPOIList,hPOIList,currentUser,this);
        checkForChangeInPOIs();
        checkForChangeInbPOIs();
        checkForChangeInhPOIs();
        checkForChangeInsPOIs();
    }

    static public void savePOIListToSD(ArrayList<POI> POIs, FirebaseUser currentUser)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
            try {
                File dir = new File(path);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }
                OutputStream fos = null;
                ObjectOutputStream oos = null;
                File file = new File(path, "/POIList.dat");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(POIs);
                System.out.println("Storing data");
                oos.close();
                //Toast.makeText(context,"Written to SD", Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());

            }
        }
    }

    static public void savehPOIListToSD(ArrayList<hPOI> hPOIs, FirebaseUser currentUser)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
            try {
                File dir = new File(path);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }
                OutputStream fos = null;
                ObjectOutputStream oos = null;
                File file = new File(path, "/hPOIList.dat");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(hPOIs);
                oos.close();
               // Toast.makeText(context,"Written to SD", Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());

            }
        }
    }

    static public void savesPOIListToSD(ArrayList<sPOI> sPOIs, FirebaseUser currentUser)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
            try {
                File dir = new File(path);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }
                OutputStream fos = null;
                ObjectOutputStream oos = null;
                File file = new File(path, "/sPOIList.dat");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(sPOIs);
                oos.close();
                // Toast.makeText(context,"Written to SD", Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());

            }
        }
    }

    static public void savebPOIListToSD(ArrayList<bPOI> bPOIs, FirebaseUser currentUser)
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
            try {
                File dir = new File(path);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }
                OutputStream fos = null;
                ObjectOutputStream oos = null;
                File file = new File(path, "/bPOIList.dat");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(bPOIs);
                oos.close();
                // Toast.makeText(context,"Written to SD", Toast.LENGTH_SHORT).show();
            } catch(Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());

            }
        }
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


    static public ArrayList<POI> readPOIsFromSD(ArrayList<POI> POIs, FirebaseUser currentUser){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
        try (FileInputStream fis = new FileInputStream(new File(path, "POIList.dat"))) {
            try (ObjectInputStream ios = new ObjectInputStream(fis)) {
                POIs = (ArrayList<POI>)ios.readObject();
                //Toast.makeText(this,POIList.get(0).getTitle(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return POIs;
    }


    static public ArrayList<sPOI> readsPOIsFromSD(ArrayList<sPOI> sPOIs, FirebaseUser currentUser){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
        try (FileInputStream fis = new FileInputStream(new File(path, "sPOIList.dat"))) {
            try (ObjectInputStream ios = new ObjectInputStream(fis)) {
               sPOIs = (ArrayList<sPOI>)ios.readObject();
                //Toast.makeText(this,POIList.get(0).getTitle(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sPOIs;
    }


    static public ArrayList<hPOI> readhPOIsFromSD(ArrayList<hPOI> hPOIs, FirebaseUser currentUser){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
        try (FileInputStream fis = new FileInputStream(new File(path, "hPOIList.dat"))) {
            try (ObjectInputStream ios = new ObjectInputStream(fis)) {
                hPOIs = (ArrayList<hPOI>)ios.readObject();
                //Toast.makeText(this,POIList.get(0).getTitle(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hPOIs;
    }

    static public ArrayList<bPOI> readbPOIsFromSD(ArrayList<bPOI> bPOIs, FirebaseUser currentUser){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/lists/" + currentUser.getDisplayName();
        try (FileInputStream fis = new FileInputStream(new File(path, "bPOIList.dat"))) {
            try (ObjectInputStream ios = new ObjectInputStream(fis)) {
                bPOIs = (ArrayList<bPOI>)ios.readObject();
                //Toast.makeText(this,POIList.get(0).getTitle(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bPOIs;
    }

    static public void updateScore(ArrayList<POI> POIs,ArrayList<sPOI> sPOIs, ArrayList<hPOI> hPOIs, FirebaseUser currentUser,Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        //Toast.makeText(c,"Setting"+sp.getBoolean("leaderboard",true),Toast.LENGTH_SHORT).show();
        if (sp.getBoolean("leaderboard",false)) {
            int progressCount = 0;
            for (POI p : POIs) {
                boolean lsp = p.getLockStatus();
                if (!lsp) {
                    progressCount = progressCount + 1;
                }
            }
            for (sPOI s : sPOIs) {
                boolean lss = s.getLockStatus();
                if (!lss) {
                    progressCount = progressCount + 1;
                }
            }
            for (hPOI h : hPOIs) {
                boolean lsh = h.getVisibility();
                if (lsh) {
                    progressCount = progressCount + 1;
                }
            }
            DatabaseReference scoreOnDb = FirebaseDatabase.getInstance().getReference().child("Scores");
            scoreOnDb.child(currentUser.getDisplayName()).setValue(POIs.size() + sPOIs.size() + hPOIs.size() - progressCount);
        }
        if(!sp.getBoolean("leaderboard",false)){
            DatabaseReference scoreOnDb = FirebaseDatabase.getInstance().getReference().child("Scores");
            scoreOnDb.child(currentUser.getDisplayName()).removeValue();
        }
    }

    static public void muteAudio(Context c, MediaPlayer mediaPlayer){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        if (!sp.getBoolean("audio",true)){
            mediaPlayer.setVolume(0f,0f);
        }
    }

    @Override
    public void onBackPressed(){

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}