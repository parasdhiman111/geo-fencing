package com.example.geofencing;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";
    private GeofenceHelper geofenceHelper;
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private int FINE_LOCATION_REQUEST_ACCESS_CODE=10001;
    private float GEO_FENCE_RADIUS=200;
    private String GEO_FENCE_ID="MY_GEOFENCE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient= LocationServices.getGeofencingClient(this);
        geofenceHelper=new GeofenceHelper(this);




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
        LatLng barara = new LatLng(30.216269, 77.035372);
        mMap.addMarker(new MarkerOptions().position(barara).title("Marker in Barara"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barara,16));
        enableUserLocation();

        mMap.setOnMapLongClickListener(this);


    }

    private  void enableUserLocation()
    {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                mMap.setMyLocationEnabled(true);

            }
            else
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_REQUEST_ACCESS_CODE);
                }
                else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_REQUEST_ACCESS_CODE);

                }
            }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==FINE_LOCATION_REQUEST_ACCESS_CODE)
        {
            if(grantResults.length > 0  && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                mMap.setMyLocationEnabled(true);
            }
            else
            {

            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng,GEO_FENCE_RADIUS);
        addGeofence(latLng,GEO_FENCE_RADIUS);
    }

    private void addGeofence(LatLng latLng,float radius)
    {
        Geofence geofence=geofenceHelper.getGeofence(GEO_FENCE_ID,latLng,radius,
                Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL
                | Geofence.GEOFENCE_TRANSITION_EXIT);
        PendingIntent pendingIntent=geofenceHelper.getPendingIntent();
        GeofencingRequest geofencingRequest= geofenceHelper.getGeofencingRequest(geofence);
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG,"onFailure"+errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions= new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }

    private void addCircle(LatLng latLng,float radius)
    {
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }
}
