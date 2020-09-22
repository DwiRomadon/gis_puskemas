package com.dwiromadon.puskes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dwiromadon.puskes.admin.HomeAdmin;
import com.dwiromadon.puskes.pengguna.HomeUser;

public class LoginActivity extends AppCompatActivity {


    Button button_signin;
    EditText edtUsername, edtPassword;
    LinearLayout skipLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        button_signin = (Button) findViewById(R.id.button_signin);
        edtUsername   = (EditText) findViewById(R.id.et_username);
        edtPassword   = (EditText) findViewById(R.id.et_password);
        skipLogin   = (LinearLayout) findViewById(R.id.skipLogin);

        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                if (userName.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Username Tidak Boleh Kosong",
                            Toast.LENGTH_LONG).show();
                }else if (password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Password Tidak Boleh Kosong",
                            Toast.LENGTH_LONG).show();
                }else {
                    if (userName.equals("admin")){
                        if(password.equals("123456")){
                            Intent i = new Intent(LoginActivity.this, HomeAdmin.class);
                            startActivity(i);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Password Salah",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Usename Salah",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, HomeUser.class);
                startActivity(i);
                finish();
            }
        });

    }
}