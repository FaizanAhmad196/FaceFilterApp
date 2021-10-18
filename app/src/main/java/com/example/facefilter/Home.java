package com.example.facefilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    ConstraintLayout face_opt, voice_opt;
//    private FirebaseAuth mAuth;
//    String TAG = "Firebase";
    int RC_SIGN_IN = 500;
    GoogleSignInClient mGoogleSignInClient;

    ImageView profile, userProfileImg;
    ConstraintLayout signupLayout, signinLayout, optLayout, google_signin_layout, logout, user_info_layout;
    TextView signInEmail, signUpEmail, goToSignin, goToSignup, signInBtn, signUpBtn, pwdEdt, pwdEdtSignUp, backBtn_profile_activity, userName, userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        try {
//            mAuth = FirebaseAuth.getInstance();
//        }catch(Exception e){
//            Log.i("e", "Exception");
//        }

        voice_opt = findViewById(R.id.voice_opt);
        face_opt = findViewById(R.id.face_opt);
        logout = findViewById(R.id.logout);
        profile = findViewById(R.id.profile);
        userProfileImg = findViewById(R.id.userProfileImg);

        signupLayout = findViewById(R.id.signUpLayout);
        signinLayout = findViewById(R.id.signInLayout);
        optLayout = findViewById(R.id.optLayout);

        signInEmail = findViewById(R.id.emailEdt);
        signUpEmail = findViewById(R.id.emailEdtSignUp);
        pwdEdt = findViewById(R.id.pwdEdt);
        pwdEdtSignUp = findViewById(R.id.pwdEdtSignUp);
        goToSignin = findViewById(R.id.gototsignin);
        goToSignup = findViewById(R.id.gototsignup);
        signInBtn = findViewById(R.id.signinBtn);
        signUpBtn = findViewById(R.id.signupBtn);
        userEmail = findViewById(R.id.userEmail);
        userName = findViewById(R.id.userName);
        user_info_layout = findViewById(R.id.user_info_layout);
        backBtn_profile_activity = findViewById(R.id.backBtn_profile_activity);
        google_signin_layout = findViewById(R.id.google_signin_layout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        optLayout.setVisibility(View.VISIBLE);
//        signupLayout.setVisibility(View.GONE);
//        signinLayout.setVisibility(View.GONE);

        face_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optLayout.setVisibility(View.GONE);
                signinLayout.setVisibility(View.GONE);
                signupLayout.setVisibility(View.GONE);
                user_info_layout.setVisibility(View.VISIBLE);


                SharedPreferences prefs = Home.this.getSharedPreferences(
                        "SIGNED_IN", Home.this.MODE_PRIVATE);
                String userNameStr = prefs.getString("userName", "User Name");
                String userEmailStr = prefs.getString("userEmail", "xyz@email.com");
                String userProfileStr = prefs.getString("userProfile", "null");

                if(userNameStr != null) {
                    userName.setText(userNameStr);
                }
                if(userEmailStr != null) {
                    userEmail.setText(userEmailStr);
                }
                if(userProfileStr != null && userProfileStr != "null"){
                    Log.i("Pic: ", userProfileStr);
                    Glide.with(Home.this)
                            .load(userProfileStr)
                            .into(userProfileImg);
                }

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        backBtn_profile_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optLayout.setVisibility(View.VISIBLE);
                signinLayout.setVisibility(View.GONE);
                signupLayout.setVisibility(View.GONE);
                user_info_layout.setVisibility(View.GONE);
            }
        });

        google_signin_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
//                if (signInEmail.getText().toString() != null && pwdEdt.getText().toString().trim() != null) {
//                    signin(signInEmail.getText().toString().trim(), pwdEdt.getText().toString().trim());
//                }else{
//                    Toast.makeText(Home.this, "Enter valid credentials", Toast.LENGTH_SHORT).show();
//                }
            }
        });

//        signUpBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (signUpEmail.getText().toString() != null && pwdEdtSignUp.getText().toString().trim() != null) {
//                    signup(signUpEmail.getText().toString().trim(), pwdEdtSignUp.getText().toString().trim());
//                }else{
//                    Toast.makeText(Home.this, "Enter valid credentials", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        goToSignin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                optLayout.setVisibility(View.GONE);
//                signupLayout.setVisibility(View.GONE);
//                signinLayout.setVisibility(View.VISIBLE);
//            }
//        });
//
//        goToSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                optLayout.setVisibility(View.GONE);
//                signupLayout.setVisibility(View.VISIBLE);
//                signinLayout.setVisibility(View.GONE);
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = this.getSharedPreferences(
                "SIGNED_IN", this.MODE_PRIVATE);

        Boolean isSignedIn = false;
        try {
            isSignedIn = prefs.getBoolean("isSignedIn", false);
        }catch(Exception e){

        }
        if (isSignedIn){
            optLayout.setVisibility(View.VISIBLE);
            signupLayout.setVisibility(View.GONE);
            signinLayout.setVisibility(View.GONE);
            user_info_layout.setVisibility(View.GONE);
        }else{
            optLayout.setVisibility(View.GONE);
            signupLayout.setVisibility(View.GONE);
            user_info_layout.setVisibility(View.GONE);
            signinLayout.setVisibility(View.VISIBLE);
        }
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            optLayout.setVisibility(View.VISIBLE);
//            signupLayout.setVisibility(View.GONE);
//            signinLayout.setVisibility(View.GONE);
//        }
    }

//    private void signup(String email, String password){
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            optLayout.setVisibility(View.VISIBLE);
//                            signupLayout.setVisibility(View.GONE);
//                            signinLayout.setVisibility(View.GONE);
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(Home.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }
//
//    private void signin(String email, String password){
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            optLayout.setVisibility(View.VISIBLE);
//                            signupLayout.setVisibility(View.GONE);
//                            signinLayout.setVisibility(View.GONE);
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(Home.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }

//    private void userData(){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // Name, email address, and profile photo Url
//            String name = user.getDisplayName();
//            String email = user.getEmail();
//            Uri photoUrl = user.getPhotoUrl();
//
//            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();
//
//            // The user's ID, unique to the Firebase project. Do NOT use this value to
//            // authenticate with your backend server, if you have one. Use
//            // FirebaseUser.getIdToken() instead.
//            String uid = user.getUid();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            SharedPreferences.Editor editor = getSharedPreferences("SIGNED_IN", MODE_PRIVATE).edit();
            editor.putBoolean("isSignedIn", true);
            editor.commit();
            editor.apply();

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                if (personName!= null){
                    Toast.makeText(this, personName + " has signed in successfully.", Toast.LENGTH_SHORT).show();
                    userName.setText(personName);
                    editor.putString("userName", personName);
                }else if(personGivenName != null){
                    userName.setText(personGivenName);
                    editor.putString("userName", personGivenName);
                    Toast.makeText(this, personGivenName + " has signed in successfully.", Toast.LENGTH_SHORT).show();
                }else if(personEmail != null){
                    Toast.makeText(this, personEmail + " has signed in successfully.", Toast.LENGTH_SHORT).show();
                }

                if(personEmail != null) {
                    editor.putString("userEmail", personEmail);
                    userEmail.setText(personEmail);
                }
                if(personPhoto != null){
                    editor.putString("userProfile", personPhoto.toString());
                    Glide.with(this)
                            .load(personPhoto.toString())
                            .into(userProfileImg);
                }

                editor.commit();
                editor.apply();
            }

            optLayout.setVisibility(View.VISIBLE);
            signinLayout.setVisibility(View.GONE);
            user_info_layout.setVisibility(View.GONE);
            signupLayout.setVisibility(View.GONE);

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            Log.i("e", e.getMessage());
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences.Editor editor = getSharedPreferences("SIGNED_IN", MODE_PRIVATE).edit();
                        editor.putBoolean("isSignedIn", false);
                        editor.commit();
                        editor.apply();

                        optLayout.setVisibility(View.GONE);
                        user_info_layout.setVisibility(View.GONE);
                        signinLayout.setVisibility(View.VISIBLE);
                        signupLayout.setVisibility(View.GONE);
                    }
                });
    }
}