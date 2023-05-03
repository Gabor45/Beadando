package com.example.butorshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int secret_key = 99;
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private static final int RC_SIGN_IN=123;


    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignin;

    private NotificationHandler mNoti;

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignin=GoogleSignIn.getClient(this,gso);

        mNoti=new NotificationHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("username",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply();
    }

    public void login(View view) {
       String usernameStr = username.getText().toString();
       String passwordStr = password.getText().toString();

        if(TextUtils.isEmpty(usernameStr)) {
            username.setError("You didnt give an email");
            return;
        }
        else if(TextUtils.isEmpty(passwordStr))
        {
            password.setError("You didnt give a password");
            return;
        }
        else
        {
            mAuth.signInWithEmailAndPassword(usernameStr,passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        mNoti.send("Welcome to ButorShop");
                        goToShop();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Login failed: "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
    private void goToShop() {
        Intent intent=new Intent(this,ShopActivity.class);
        startActivity(intent);
    }
    public void goToregist(View view) {
        Intent regi = new Intent(this, RegisterActivity.class);
        regi.putExtra("secret_key", 99);
        startActivity(regi);
    }

    public void registwithgoogle(View view) {
        Intent signInIntent=mGoogleSignin.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }catch(ApiException e){
                Toast.makeText(MainActivity.this,"Login failed with Google: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String IdToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(IdToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    goToShop();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Login failed with Google: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}