package com.example.jameswinters.unlock_android;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class createAccount extends AppCompatActivity implements Button.OnClickListener {

    private FirebaseAuth mAuth;
    private String msg = "Android:";
    private static final String WRITE_DATA = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    EditText emailContainer;
    EditText passwordContainer;
    EditText usernameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getStoragePermissions();
        getLocationPermission();
        mAuth = FirebaseAuth.getInstance();
        emailContainer = findViewById(R.id.newEmailContainer);
        usernameContainer = findViewById(R.id.newUsernameContainer);
        passwordContainer = findViewById(R.id.newPasswordContainer);
        findViewById(R.id.createAccount).setOnClickListener(this);

    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(msg, "createUserWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            String username = usernameContainer.getText().toString();
                            createNewUser(user);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Log.d("Profile", "User Profile Updated successfully");

                                    }else {

                                        Log.d("Error","error while updating profile");

                                    }
                                    Intent i = new Intent(createAccount.this, MainActivity.class);
                                    POIXMLParser parser = new POIXMLParser(createAccount.this);
                                    sPOIXMLParser sparser = new sPOIXMLParser(createAccount.this);
                                    hPOIXMLParser hparser = new hPOIXMLParser(createAccount.this);
                                    ArrayList<POI> POIList = parser.getPOIList();
                                    ArrayList<sPOI> sPOIList = sparser.getsPOIList();
                                    ArrayList<hPOI> hPOIList = hparser.gethPOIList();
                                    MainActivity.savePOIListToSD(POIList,user);
                                    MainActivity.savesPOIListToSD(sPOIList,user);
                                    MainActivity.savehPOIListToSD(hPOIList,user);
                                    String name = usernameContainer.getText().toString();
                                    DatabaseReference initialScoreOnDb = FirebaseDatabase.getInstance().getReference().child("Scores");
                                    initialScoreOnDb.child(name).setValue(POIList.size()+hPOIList.size()+sPOIList.size());
                                    startActivity(i);
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(msg, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(createAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createNewUser(FirebaseUser userFromRegistration) {
        String username = usernameContainer.getText().toString();
        Log.d(msg, "Username:"+ username);
        String userId = userFromRegistration.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Usernames");
        myRef.child(userId).child("Name").setValue(username);
    }
    public void getStoragePermissions(){
        Log.d("Android", "getLocationPermission: getting location permissions");

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                WRITE_DATA) == PackageManager.PERMISSION_GRANTED){
            //mWriteStorage = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

    }
   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Android","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }*/
    private boolean validateForm() {
        boolean valid = true;

        String email = emailContainer.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailContainer.setError("Required.");
            valid = false;
        } else {
            emailContainer.setError(null);
        }

        String password = passwordContainer.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordContainer.setError("Required.");
            valid = false;
        } else {
            passwordContainer.setError(null);
        }
        String username = usernameContainer.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameContainer.setError("Required.");
            valid = false;
        } else {
            usernameContainer.setError(null);
        }

        return valid;
    }
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createAccount) {
            createAccount(emailContainer.getText().toString(),passwordContainer.getText().toString());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(msg, "onRequestPermissionsResult: called.");
        //mLocationPermissionGranted = false;
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                           // mLocationPermissionGranted = false;
                            Log.d(msg, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(msg, "onRequestPermissionsResult: permission granted");
                   // mLocationPermissionGranted = true;
                    // Initialize map
                   // initMap();
                }
            }
        }
    }
    private void getLocationPermission(){
        Log.d(msg, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d(msg, "getLocationPermission: Self Permission Granted");
                //Toast.makeText(this, "Self Permission Granted", Toast.LENGTH_SHORT).show();
                //mLocationPermissionGranted = true;

            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i =new Intent(createAccount.this, EmailPasswordActivity.class);
        startActivity(i);
    }
}