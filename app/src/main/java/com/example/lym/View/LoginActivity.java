package com.example.lym.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.lym.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView tLogin;

    Button btnLogin;

    Button btnSignup;


    EditText eTUsername;

    EditText eTPW;
    FirebaseAuth mAuth;


    private void Login() {
        String Username = eTUsername.getText().toString();
        String PW = eTPW.getText().toString();
        mAuth.signInWithEmailAndPassword(Username, PW)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent myintent = new Intent(LoginActivity.this, TakePhotoActivity.class);
                            startActivity(myintent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @SuppressLint("MissingInflatedId")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tLogin = (TextView) findViewById(R.id.tLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        eTUsername = (EditText) findViewById(R.id.eTUsername);
        eTPW = (EditText) findViewById(R.id.eTPW);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(myintent);
            }
        });

    }
}