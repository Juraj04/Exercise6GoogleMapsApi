package com.example.janik.exercise6googlemapsapi;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplication());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        LoadJsonData task = new LoadJsonData();
        task.execute("http://ptm.fi/materials/golfcourses/golf_courses.json");


    }



    public class LoadJsonData extends AsyncTask<String,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            JSONObject json = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                json = new JSONObject(stringBuilder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject == null) return;

            HashMap<String, Float> colors = new HashMap<>();
            colors.put("Kulta", BitmapDescriptorFactory.HUE_AZURE);
            colors.put("Kulta/Etu", BitmapDescriptorFactory.HUE_MAGENTA);
            colors.put("?", BitmapDescriptorFactory.HUE_YELLOW);
            colors.put("Etu",BitmapDescriptorFactory.HUE_ORANGE);

            try {
                JSONArray courses = jsonObject.getJSONArray("courses");
                LatLng latlng = null;
                for (int i = 0; i< courses.length();i++){
                    JSONObject course = courses.getJSONObject(i);

                    String type = course.getString("type");
                    double lat = course.getDouble("lat");
                    double lng = course.getDouble("lng");
                    latlng = new LatLng(lat,lng);
                    String title = course.getString("course");
                    String address = course.getString("address");
                    String phone = course.getString("phone");
                    String email = course.getString("email");
                    String web = course.getString("web");


                    mMap.addMarker(new MarkerOptions()
                            .position(latlng)
                            .title(title)
                            .icon(BitmapDescriptorFactory.defaultMarker(colors.get(type)))
                            .snippet(address + "\n" + phone + "\n" + email + "\n" + web));

                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,6));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
