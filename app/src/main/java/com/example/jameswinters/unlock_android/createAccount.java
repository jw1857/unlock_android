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
    EditText emailContainer;
    EditText passwordContainer;
    EditText usernameContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        emailContainer = findViewById(R.id.newEmailContainer);
        usernameContainer = findViewById(R.id.newUsernameContainer);
        passwordContainer = findViewById(R.id.newPasswordContainer);
        Button buttonCreateAccount = findViewById(R.id.createAccount);
        buttonCreateAccount.setOnClickListener(this);

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
                                    bPOIXMLParser bparser = new bPOIXMLParser(createAccount.this);
                                    ArrayList<POI> POIList = parser.getPOIList();
                                    ArrayList<sPOI> sPOIList = sparser.getsPOIList();
                                    ArrayList<hPOI> hPOIList = hparser.gethPOIList();
                                    ArrayList<bPOI> bPOIList = bparser.getbPOIList();
                                    MainActivity.savePOIListToSD(POIList,user);
                                    MainActivity.savesPOIListToSD(sPOIList,user);
                                    MainActivity.savehPOIListToSD(hPOIList,user);
                                    MainActivity.savebPOIListToSD(bPOIList,user);
                                    System.out.print("Test bPOI" + bPOIList.get(0).getTitle());
                                   // String name = usernameContainer.getText().toString();
                                    //DatabaseReference initialScoreOnDb = FirebaseDatabase.getInstance().getReference().child("Scores");
                                    //initialScoreOnDb.child(name).setValue(POIList.size()+hPOIList.size()+sPOIList.size());
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i =new Intent(createAccount.this, EmailPasswordActivity.class);
        startActivity(i);
    }
}