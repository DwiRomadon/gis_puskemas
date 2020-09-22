package com.dwiromadon.puskes.pengguna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dwiromadon.puskes.R;
import com.dwiromadon.puskes.server.BaseURL;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailPuskesPengguna extends AppCompatActivity {

    Intent i;
    String _id, namaPuskes, alamat, noTelp, gambar, jamBuka, lat, lon;
    ArrayList gam = new ArrayList();
    ArrayList listJamBuka = new ArrayList();
    CarouselView carouselView;

    EditText edtPetshop, edtAlamat, edtNoTelp;
    Spinner spnJamBuka;
    Button goRoutes;

    String goolgeMap = "com.google.android.apps.maps"; // identitas package aplikasi google masps android
    Uri gmmIntentUri;
    Intent mapIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_puskes_pengguna);

        getSupportActionBar().setTitle("Detail Petshop");

        i = getIntent();
        _id = i.getStringExtra("_id");
        namaPuskes = i.getStringExtra("namaPuskes");
        alamat = i.getStringExtra("alamat");
        noTelp = i.getStringExtra("noTelp");
        jamBuka = i.getStringExtra("jambuka");
        gambar = i.getStringExtra("gambar");
        lat = i.getStringExtra("lat");
        lon = i.getStringExtra("lon");

        final String latLong = lat + "," + lon;

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        edtPetshop = (EditText) findViewById(R.id.edtPetshop);
        edtAlamat = (EditText) findViewById(R.id.edtAlamat);
        edtNoTelp = (EditText) findViewById(R.id.edtNotelp);

        spnJamBuka = (Spinner) findViewById(R.id.jamBuka);

        goRoutes = (Button) findViewById(R.id.goRoutes);

        edtPetshop.setText(namaPuskes);
        edtAlamat.setText(alamat);
        edtNoTelp.setText(noTelp);

        try {
            JSONArray arrayGambar = new JSONArray(gambar);
            JSONArray arrayJamBuka = new JSONArray(jamBuka);
            for (int i = 0; i < arrayGambar.length(); i++){
                gam.add(BaseURL.baseUrl + "gambar/" + arrayGambar.get(i).toString());
            }

            for (int i = 0; i < arrayJamBuka.length(); i++){
                JSONObject objJamBuka = arrayJamBuka.getJSONObject(i);
                listJamBuka.add(objJamBuka.getString("hari") + " / " + objJamBuka.getString("jam"));
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listJamBuka);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnJamBuka.setAdapter(adapter);


            carouselView.setPageCount(gam.size());
            carouselView.setImageListener(imageListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        goRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmmIntentUri = Uri.parse("google.navigation:q=" + latLong);

                // Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
                mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
                mapIntent.setPackage(goolgeMap);

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(DetailPuskesPengguna.this, "Google Maps Belum Terinstal. Install Terlebih dahulu.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        edtNoTelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String temp = "tel:" + noTelp;
                intent.setData(Uri.parse(temp));
                startActivity(intent);
            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get().load(String.valueOf(gam.get(position))).fit().centerCrop().into(imageView);
            //imageView.setImageResource(sampleImages[position]);
        }
    };

    View.OnClickListener pauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            carouselView.pauseCarousel();
        }
    };
}