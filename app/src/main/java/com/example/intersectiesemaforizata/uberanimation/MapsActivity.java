package com.example.intersectiesemaforizata.uberanimation;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.intersectiesemaforizata.R;
import com.example.intersectiesemaforizata.uberanimation.remote.IGoogleApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap maps;
    SupportMapFragment mapFragment;
    private List<LatLng> polylineList;
    private Marker marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;
    private Button btnGo;
    private EditText edtPlace;
    private String destination;
    private PolylineOptions polylineOptions, blackPolyLineOptions;
    private Polyline blackPolyline, greyPolyline;
    private LatLng myLocation;

    IGoogleApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        polylineList = new ArrayList<>();
        btnGo = findViewById(R.id.btnSearch);
        edtPlace = findViewById(R.id.edtPlace);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination = edtPlace.getText().toString();
                destination = destination.replace(" ", "+");// Replace space to + to make url
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;
        LatLng bucuresti = new LatLng(44.4267674, 26.1025384);
        maps.addMarker(new MarkerOptions().position(bucuresti).title("Bucuresti Oras"));
        maps.moveCamera(CameraUpdateFactory.newLatLng(bucuresti));

    }
}