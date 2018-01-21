package com.wyb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public ArrayList<LocPojo> List;
    ArrayList<Marker> myMarkers;

    OnGetVolleyResponse ogvr;
    SharedPreferences preferences;
    String loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        loc=preferences.getString("Place", "");
        Log.d("vibssss",loc);

        List = new ArrayList<>();

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                "http://192.168.43.37/wyb/lat_long.php?location="+loc,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject Object = null;
                            try {
                                Object = response.getJSONObject(i);

                                Log.d("yahahu", "onResponse: " + Object.get("res_name"));
                                List.add(new LocPojo(Object.getDouble("lat"), Object.getDouble("lon"),Object.getString("res_name"),Object.getString("addr"),Object.getString("location")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ogvr.onSuccess(List);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("yahahu", "onErrorResponse: " + error.toString());
                        Log.d("yahahu", "onErrorResponse: " + error.getMessage());
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(jsonArrayRequest);
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        ogvr = new OnGetVolleyResponse() {

            @Override
            public void onSuccess(ArrayList<LocPojo> List) {
                LocPojo loc, location;
                location = List.get(0);

                for (int i = 0; i < List.size(); i++) {
                    loc = List.get(i);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLat(), loc.getLon())).title(loc.getName()).snippet(loc.getAddr()));

                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(), location.getLon()), 12.0f));


            }

        };
    }
}
