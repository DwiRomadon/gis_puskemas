package com.dwiromadon.puskes.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dwiromadon.puskes.R;
import com.dwiromadon.puskes.server.BaseURL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.vistrav.pop.Pop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailPuskes extends AppCompatActivity {

    Intent i;
    String _id, namaPuskes, alamat, noTelp, gambar, jamBuka, lat, lon;
    ArrayList gam = new ArrayList();
    ArrayList listJamBuka = new ArrayList();
    CarouselView carouselView;

    FloatingActionButton btnEditNama, btnSimpanNama, btnEditAlamat,
            btnSimpanAlamst, btnEditNoTelp, btnSimpanNoTelp, fabUbahJamBuka;

    EditText edtPusekes, edtAlamat, edtNoTelp;
    Spinner spnJamBuka;
    FloatingActionButton fabAddGambar;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_puskes);

        getSupportActionBar().hide();
        mRequestQueue = Volley.newRequestQueue(this);

        i = getIntent();
        _id = i.getStringExtra("_id");
        namaPuskes = i.getStringExtra("namaPuskes");
        alamat = i.getStringExtra("alamat");
        noTelp = i.getStringExtra("noTelp");
        gambar = i.getStringExtra("gambar");
        jamBuka = i.getStringExtra("jambuka");
        lat = i.getStringExtra("lat");
        lon = i.getStringExtra("lon");

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        edtPusekes = (EditText) findViewById(R.id.edtPusekes);
        edtAlamat = (EditText) findViewById(R.id.edtAlamat);
        edtNoTelp = (EditText) findViewById(R.id.edtNotelp);

        btnEditNama = (FloatingActionButton) findViewById(R.id.fabButtonEditNama);
        btnEditAlamat = (FloatingActionButton) findViewById(R.id.fabButtonEditAlamat);
        btnEditNoTelp = (FloatingActionButton) findViewById(R.id.fabButtonEditNotelp);
        btnSimpanNama = (FloatingActionButton) findViewById(R.id.fabButtonSimpanNama);
        btnSimpanAlamst = (FloatingActionButton) findViewById(R.id.fabButtonSimpanAlamat);
        btnSimpanNoTelp = (FloatingActionButton) findViewById(R.id.fabButtonSimpanNotelp);
        fabUbahJamBuka = (FloatingActionButton) findViewById(R.id.fabUbahJamBuka);

        spnJamBuka = (Spinner) findViewById(R.id.jamBuka);

        fabAddGambar = (FloatingActionButton) findViewById(R.id.fabAddGambar);

        fabAddGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailPuskes.this, TambahGambar.class);
                i.putExtra("_id", _id);
                startActivity(i);
                finish();
            }
        });

        edtPusekes.setText(namaPuskes);
        edtAlamat.setText(alamat);
        edtNoTelp.setText(noTelp);


        try {
            JSONArray arrayGambar = new JSONArray(gambar);
            JSONArray arrayJamBuka = new JSONArray(jamBuka);
            for (int i = 0; i < arrayGambar.length(); i++) {
                gam.add(BaseURL.baseUrl + "gambar/" + arrayGambar.get(i).toString());
            }

            for (int i = 0; i < arrayJamBuka.length(); i++) {
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


        btnEditNama.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                btnSimpanNama.setVisibility(View.VISIBLE);
                btnEditNama.setVisibility(View.GONE);
                edtPusekes.setFocusableInTouchMode(true);
                edtPusekes.requestFocus();

                if (edtPusekes.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        btnSimpanNama.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                btnSimpanNama.setVisibility(View.GONE);
                btnEditNama.setVisibility(View.VISIBLE);
                edtPusekes.setFocusableInTouchMode(false);
                edtPusekes.setFocusable(false);
                String namaPuskes = edtPusekes.getText().toString();
                try {
                    JSONObject jsonObj1 = null;
                    jsonObj1 = new JSONObject();
                    jsonObj1.put("namaPuskes", namaPuskes);
                    updateData(jsonObj1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnEditAlamat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                Pop.on(DetailPuskes.this)
                        .with()
                        .title("Ubah Lokasi")
                        .cancelable(false)
                        .layout(R.layout.pop_up)
                        .when(new Pop.Yah() {
                            @Override
                            public void clicked(DialogInterface dialog, View view) {
                                EditText lat = (EditText) view.findViewById(R.id.edtLat);
                                EditText lon = (EditText) view.findViewById(R.id.edtLon);

                                String lt = lat.getText().toString();
                                String lg = lon.getText().toString();
                                try {
                                    JSONObject jsonObj1 = null;
                                    jsonObj1 = new JSONObject();
                                    jsonObj1.put("lat", lt);
                                    jsonObj1.put("lon", lg);
                                    updateData(jsonObj1);
                                    Intent i = new Intent(DetailPuskes.this, DataPuskesmas.class);
                                    startActivity(i);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .when(new Pop.Nah() { // ignore if dont need negative button
                            @Override
                            public void clicked(DialogInterface dialog, View view) {
                            }
                        })
                        .show(new Pop.View() { // assign value to view element
                            @Override
                            public void prepare(View view) {
                            }
                        });
            }
        });


        btnEditNoTelp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                btnSimpanNoTelp.setVisibility(View.VISIBLE);
                btnEditNoTelp.setVisibility(View.GONE);
                edtNoTelp.setFocusableInTouchMode(true);
                edtNoTelp.requestFocus();

                if (edtNoTelp.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        btnSimpanNoTelp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                btnSimpanNoTelp.setVisibility(View.GONE);
                btnEditNoTelp.setVisibility(View.VISIBLE);
                edtNoTelp.setFocusableInTouchMode(false);
                edtNoTelp.setFocusable(false);
                String noTel = edtNoTelp.getText().toString();
                try {
                    JSONObject jsonObj1 = null;
                    jsonObj1 = new JSONObject();
                    jsonObj1.put("noTelp", noTel);
                    updateData(jsonObj1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        fabUbahJamBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailPuskes.this, UbahJamBuka.class);
                i.putExtra("_id", _id);
                i.putExtra("jamBuka", jamBuka);
                startActivity(i);
                finish();
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


    public void updateData(JSONObject datas){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.updatePetDataPuskes+ _id, datas,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String strMsg = jsonObject.getString("msg");
                            boolean status= jsonObject.getBoolean("error");
                            if(status == false){
                                Toast.makeText(getApplicationContext(), strMsg, Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(), strMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mRequestQueue.add(req);
    }
}