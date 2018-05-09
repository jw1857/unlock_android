package com.example.jameswinters.unlock_android;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

public class createAccount extends AppCompatActivity implements Button.OnClickListener {

    private FirebaseAuth mAuth;
    private String msg = "Android:";
    EditText emailContainer;
    EditText passwordContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        emailContainer = findViewById(R.id.newEmailContainer);
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(msg, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent i = new Intent(createAccount.this, MainActivity.class);
                            POIXMLParser parser = new POIXMLParser(createAccount.this);
                            ArrayList<POI> POIList = parser.getPOIList();
                            Bundle b = new Bundle();
                            b.putSerializable("POIList", POIList);
                            i.putExtras(b);
                            startActivity(i);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(msg, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(createAccount.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
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