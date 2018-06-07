package com.example.jameswinters.unlock_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
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
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
/* This class allows a user to either login with their existing account or go createAccount to make a new account*/
public class EmailPasswordActivity extends AppCompatActivity implements Button.OnClickListener {
    private FirebaseAuth mAuth;
    private String msg = "Android:";
    private boolean mWriteStorage = false;
    private static final String WRITE_DATA = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1233;
    EditText emailContainer ;
    EditText passwordContainer ;
    TextView mStatusTextView ;
    TextView mDetailTextView ;


    //Button signIn = findViewById(R.id.signIn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        getPermissions();//get storage, location permissions when app first loads.

        emailContainer = findViewById(R.id.emailContainer);//instantiate objects
        passwordContainer = findViewById(R.id.passwordContainer);
        mStatusTextView = findViewById(R.id.mStatusTextView);
        mDetailTextView = findViewById(R.id.mDetailTextView);
        findViewById(R.id.signIn).setOnClickListener(this);
        findViewById(R.id.newAccount).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    public void onStart() {
        super.onStart();
        // Move straight to main activity if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){
            Intent i = new Intent(EmailPasswordActivity.this,MainActivity.class);
            startActivity(i);
        }
    }

    // function to move to createAccount activity
    private void createAccount(){
        Intent i = new Intent(this,createAccount.class);
        startActivity(i);
    }


    private void signIn(String email, String password){
        if (!validateForm()) {//check user has entered fields in form
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, read user's data from firebase
                            Log.d(msg, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("POIList").child(user.getDisplayName());
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent i =new Intent(EmailPasswordActivity.this,MainActivity.class);
                                    GenericTypeIndicator<ArrayList<POI>> genericTypeIndicatorPOI = new GenericTypeIndicator<ArrayList<POI>>() {};
                                    GenericTypeIndicator<ArrayList<sPOI>> genericTypeIndicatorsPOI = new GenericTypeIndicator<ArrayList<sPOI>>() {};
                                    GenericTypeIndicator<ArrayList<hPOI>> genericTypeIndicatorhPOI = new GenericTypeIndicator<ArrayList<hPOI>>() {};
                                    GenericTypeIndicator<ArrayList<bPOI>> genericTypeIndicatorbPOI = new GenericTypeIndicator<ArrayList<bPOI>>() {};
                                    ArrayList<POI> POIList=new ArrayList<>();
                                    ArrayList<sPOI> sPOIList=new ArrayList<>();
                                    ArrayList<hPOI> hPOIList=new ArrayList<>();
                                    ArrayList<bPOI> bPOIList=new ArrayList<>();
                                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                                        switch (d.getKey()) {
                                            case "POIs":
                                                POIList = d.getValue(genericTypeIndicatorPOI);
                                                break;
                                            case "sPOIs":
                                                sPOIList =d.getValue(genericTypeIndicatorsPOI);
                                                break;
                                            case "hPOIs":
                                                hPOIList = d.getValue(genericTypeIndicatorhPOI);
                                                break;
                                            case "bPOIs":
                                                bPOIList = d.getValue(genericTypeIndicatorbPOI);


                                        }
                                    }
                                    //save data obatined from firebase to sd card of phone
                                    MainActivity.savePOIListToSD(POIList,user);
                                    MainActivity.savesPOIListToSD(sPOIList,user);
                                    MainActivity.savehPOIListToSD(hPOIList,user);
                                    MainActivity.savebPOIListToSD(bPOIList,user);
                                    startActivity(i);//go to main activity
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(msg, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(msg, "onRequestPermissionsResult: called.");
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
                }
            }

        }
    }
    private void getPermissions(){
        //Request permissions for storage, location
        Log.d(msg, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.d(msg, "getLocationPermission: Self Permission Granted");

            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }


    }

    //check user has filled in EditTexts
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

        return valid;
    }



    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.newAccount) {
            createAccount();
        } else if (i == R.id.signIn) {
            //get user-entered values from editTexts and sign in with these
            signIn(emailContainer.getText().toString(), passwordContainer.getText().toString());
       }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}