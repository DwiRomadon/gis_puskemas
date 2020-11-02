package com.dwiromadon.puskes.pengguna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dwiromadon.puskes.LoginActivity;
import com.dwiromadon.puskes.R;

public class Tentang extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Tentang.this, HomeUser.class);
        startActivity(i);
        finish();
    }
}