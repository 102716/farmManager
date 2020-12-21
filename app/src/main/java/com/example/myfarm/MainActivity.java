package com.example.myfarm;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private EditText mPhone,mEmail,mPassword,mFullname;
    private Button mRegisterBtn;
    private TextView mLoginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFullname = findViewById(R.id.username);
        mEmail= findViewById(R.id.email);
        mPassword = findViewById(R.id.password1);
//
        mRegisterBtn =findViewById(R.id.register);
        progressBar= findViewById(R.id.progressBar);
        mPhone = findViewById(R.id.number);
        mLoginBtn= findViewById(R.id.signinTv);

        /////////

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

//        if (fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), UserProfile.class));
//            finish();
//        }else{
//            startActivity(new Intent(getApplicationContext(), Register.class));
//        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullname = mFullname.getText().toString();
                final String phone = mPhone.getText().toString();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }if (TextUtils.isEmpty(password)){
                    mPassword.setError("password is required");
                    return;
                }
                if (password.length()<6){
                    mPassword.setError("password length must be greater than 6");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullname",fullname);
                            user.put("email", email);
                            user.put("phone",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"onsuccess: user Profile is created for "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onfailure:"+e.toString());
                                }
                            });
                            startActivity(new Intent(MainActivity.this,LogIn.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LogIn.class));
            }
        });
    }
}