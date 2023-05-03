package com.example.butorshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();

    private FirebaseAuth mAuth;
    private SharedPreferences preferences;
    EditText usernameET;
    EditText passwordET;
    EditText passwordAgainET;
    EditText emailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key=getIntent().getIntExtra("secret_key",0);

        if(secret_key!=99)
        {
            finish();
        }
        usernameET=findViewById(R.id.re_username);
        passwordET=findViewById(R.id.re_password);
        passwordAgainET=findViewById(R.id.re_rep_password);
        emailET=findViewById(R.id.re_email);

        preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String megkapottusername=preferences.getString("username","");
        String megkapottpassword=preferences.getString("password","");
        usernameET.setText(megkapottusername);
        passwordET.setText(megkapottpassword);

        mAuth=FirebaseAuth.getInstance();
    }

    public void regist(View view) {
        String re_username=usernameET.getText().toString();
        String re_password=passwordET.getText().toString();
        String re_rep_password=passwordAgainET.getText().toString();
        String re_email=emailET.getText().toString();

        if(TextUtils.isEmpty(re_username)) {
            usernameET.setError("You didnt give an username");
            return;
        }
        else if(TextUtils.isEmpty(re_password))
        {
            passwordET.setError("You didnt give a password");
            return;
        }
        else if(TextUtils.isEmpty(re_rep_password))
        {
            passwordAgainET.setError("You didnt give a password");
            return;
        }
        else if(TextUtils.isEmpty(re_email))
        {
            emailET.setError("You didnt give a email");
            return;
        }
        else if(!re_password.equals(re_rep_password))
        {
            passwordET.setError("Password and Password Again didnt match");
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(re_email,re_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        goToShop();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"User wasnt created "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void goToShop() {
        Intent intent=new Intent(this,ShopActivity.class);
        startActivity(intent);
    }

    public void close(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}