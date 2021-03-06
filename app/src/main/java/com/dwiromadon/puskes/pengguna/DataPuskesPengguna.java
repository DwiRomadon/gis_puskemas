package com.dwiromadon.puskes.pengguna;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dwiromadon.puskes.R;
import com.dwiromadon.puskes.adapter.AdapterPenggunaPuskes;
import com.dwiromadon.puskes.adapter.AdapterPuskes;
import com.dwiromadon.puskes.admin.DataPuskesmas;
import com.dwiromadon.puskes.model.ModelPuskesAdmin;
import com.dwiromadon.puskes.server.BaseURL;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DataPuskesPengguna extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    ProgressDialog pDialog;

    AdapterPenggunaPuskes adapter;
    ListView list;

    ArrayList<ModelPuskesAdmin> newsList = new ArrayList<ModelPuskesAdmin>();
    private RequestQueue mRequestQueue;

    EditText edtSearch;

    int socketTimeout = 500000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_puskes_pengguna);

        getSupportActionBar().setTitle("Data Puskesmas");
        mRequestQueue = Volley.newRequestQueue(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        list = (ListView) findViewById(R.id.array_list);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        newsList.clear();
        adapter = new AdapterPenggunaPuskes(DataPuskesPengguna.this, newsList);
        list.setAdapter(adapter);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); //

        cari();
    }

    private void getAllPet(JSONObject jsonObject) {
        // Pass second argument as "null" for GET requests
        pDialog.setMessage("Loading");
        showDialog();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, BaseURL.getJarak + "0", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            boolean status = response.getBoolean("error");
                            if (status == false) {
                                String data = response.getString("data");
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelPuskesAdmin puskes = new ModelPuskesAdmin();
                                    final String _id = jsonObject.getString("_id");
                                    final String namaPuskes = jsonObject.getString("namaPuskes");
//                                    final String alamat = jsonObject.getString("alamat");
                                    final String notelp = jsonObject.getString("noTelp");
                                    final String arrGambar = jsonObject.getString("gambar");
                                    final String arrJamBuka = jsonObject.getString("jamBuka");
                                    final String lat = jsonObject.getString("lat");
                                    final String lon = jsonObject.getString("lon");
                                    final String jarak = jsonObject.getString("jarak");
                                    JSONObject jobjJarak = new JSONObject(jarak);
                                    JSONArray arrayGambar = new JSONArray(arrGambar);
                                    String gambar = arrayGambar.get(0).toString();
                                    String jarakDistance = jobjJarak.getString("distance");
                                    String destination = jobjJarak.getString("destination");
                                    String duration = jobjJarak.getString("duration");
                                    final String fasilitas = jsonObject.getString("fasilitas");
                                    puskes.setDuration(duration);
                                    puskes.setNamaPuskes(namaPuskes);
                                    puskes.setAlamat(destination);
                                    puskes.setNotelp(notelp);
                                    puskes.setGambar(gambar);
                                    puskes.setArrGambar(arrGambar);
                                    puskes.setJamBuka(arrJamBuka);
                                    puskes.set_id(_id);
                                    puskes.setLat(lat);
                                    puskes.setLon(lon);
                                    puskes.setJarak(jarakDistance);
                                    puskes.setFasilitas(fasilitas);

                                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            // TODO Auto-generated method stub
                                            inputHistori(newsList.get(position).get_id());
                                            Intent a = new Intent(DataPuskesPengguna.this, DetailPuskesPengguna.class);
                                            a.putExtra("namaPuskes", newsList.get(position).getNamaPuskes());
                                            a.putExtra("_id", newsList.get(position).get_id());
                                            a.putExtra("alamat", newsList.get(position).getAlamat());
                                            a.putExtra("noTelp", newsList.get(position).getNotelp());
                                            a.putExtra("gambar", newsList.get(position).getArrGambar());
                                            a.putExtra("jambuka", newsList.get(position).getJamBuka());
                                            a.putExtra("lat", newsList.get(position).getLat());
                                            a.putExtra("lon", newsList.get(position).getLon());
                                            a.putExtra("fasilitas", newsList.get(position).getFasilitas());
                                            startActivity(a);
                                        }
                                    });
                                    newsList.add(puskes);
//                                    newsList.clear();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });

        req.setRetryPolicy(policy);
        /* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DataPuskesPengguna.this, HomeUser.class);
        startActivity(i);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     *
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            Log.d("Lat Lon = ", String.valueOf(currentLatitude) + " " + String.valueOf(currentLongitude));

            try {
                newsList.clear();
                JSONObject jsonObj1=null;
                jsonObj1=new JSONObject();
                jsonObj1.put("lat", String.valueOf(currentLatitude));
                jsonObj1.put("lon", String.valueOf(currentLongitude));

                Log.d("Data = ", jsonObj1.toString());
                getAllPet(jsonObj1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//            cari();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

    public void cari(){
        edtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final List<ModelPuskesAdmin> filteredList = new ArrayList<ModelPuskesAdmin>();

                for (int i = 0; i < newsList.size(); i++) {

                    final String text = newsList.get(i).getNamaPuskes().toLowerCase();
                    if (text.contains(query)) {
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // TODO Auto-generated method stub
                                Intent a = new Intent(DataPuskesPengguna.this, DetailPuskesPengguna.class);
                                a.putExtra("namaPuskes", filteredList.get(position).getNamaPuskes());
                                a.putExtra("_id", filteredList.get(position).get_id());
                                a.putExtra("alamat", filteredList.get(position).getAlamat());
                                a.putExtra("noTelp", filteredList.get(position).getNotelp());
                                a.putExtra("gambar", filteredList.get(position).getArrGambar());
                                a.putExtra("jambuka", filteredList.get(position).getJamBuka());
                                a.putExtra("lat", filteredList.get(position).getLat());
                                a.putExtra("lon", filteredList.get(position).getLon());
                                a.putExtra("fasilitas", filteredList.get(position).getFasilitas());
                                startActivity(a);
                            }
                        });
                        filteredList.add(newsList.get(i));
                    }
                }
                adapter = new AdapterPenggunaPuskes(DataPuskesPengguna.this, filteredList);
                list.setAdapter(adapter);
            }
        });
    }

    public void inputHistori(String _id){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", _id);
        params.put("macAddress", getMacAddr());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, BaseURL.inputHistory, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String strMsg = jsonObject.getString("msg");
                            boolean status= jsonObject.getBoolean("error");
                            if(status == false){
                                Log.d("Msg = ", strMsg);
                            }else {
                                Log.d("Msg = ", strMsg);
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

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }
}