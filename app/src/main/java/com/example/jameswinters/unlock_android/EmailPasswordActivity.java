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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmailPasswordActivity extends AppCompatActivity implements Button.OnClickListener {
    private FirebaseAuth mAuth;
    private String msg = "Android:";
    private static final String WRITE_DATA = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1233;
    private boolean mWriteStorage =false;
    EditText emailContainer ;
    EditText passwordContainer ;
    TextView mStatusTextView ;
    TextView mDetailTextView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        getPermissions();

        emailContainer = findViewById(R.id.emailContainer);
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){
            Intent i = new Intent(EmailPasswordActivity.this,MainActivity.class);
            startActivity(i);
        }
    }

    private void createAccount(){
        Intent i = new Intent(this,createAccount.class);
        startActivity(i);
    }


    private void signIn(String email, String password){
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
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
                                    MainActivity.savePOIListToSD(POIList,user);
                                    MainActivity.savesPOIListToSD(sPOIList,user);
                                    MainActivity.savehPOIListToSD(hPOIList,user);
                                    MainActivity.savebPOIListToSD(bPOIList,user);
                                    startActivity(i);
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
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    private void updateUI(FirebaseUser user) {
        //hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            //findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            //findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            //findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

           // findViewById(R.id.verifyEmail).setEnabled(!user.isEmailVerified());
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            // findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            //findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            //findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }
    public void getStoragePermissions(){
        Log.d("Android", "getLocationPermission: getting location permissions");

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                WRITE_DATA) == PackageManager.PERMISSION_GRANTED){
            mWriteStorage = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
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
    private void getPermissions(){
        Log.d(msg, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
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
   /* private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmail).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verifyEmail).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(msg, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }*/


    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.newAccount) {
            createAccount();
        } else if (i == R.id.signIn) {
            signIn(emailContainer.getText().toString(), passwordContainer.getText().toString());
       } /* else if (i == R.id.verifyEmail) {
            sendEmailVerification();
        }*/
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