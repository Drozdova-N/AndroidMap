package com.example.dnina.navigator.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dnina.navigator.MainActivity;
import com.example.dnina.navigator.MapView;
import com.example.dnina.navigator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
//    private View mProgressView;
//    private View mLoginFormView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mEmailView.getText().length() != 0 && mPasswordView.getText().length()!=0)
                {  signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());}

            }
        });

        Button registrationButton = (Button) findViewById(R.id.registration_button);
        registrationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailView.getText().length() != 0 && mPasswordView.getText().length()!=0)
                {  registration(mEmailView.getText().toString(), mPasswordView.getText().toString());}

            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!= null)
        {
            MainActivity.startNewActivity(LoginActivity.this);
        }

    }

    public void signIn(String login, String password) {
        mAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    MapView.clearMyLocation();
                    MainActivity.startNewActivity(LoginActivity.this);

                } else
                    Toast.makeText(LoginActivity.this, "Не удалось выполнить вход:(", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void registration(String login, String password)
    {
        mAuth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Регистрация прошла успешна", Toast.LENGTH_LONG).show();

                }
                else  Toast.makeText(LoginActivity.this, "Не удалось зарегистрировать пользователя", Toast.LENGTH_LONG).show();

            }
        });
    }


    public static void startNewActivity(Activity activity)
    {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}

