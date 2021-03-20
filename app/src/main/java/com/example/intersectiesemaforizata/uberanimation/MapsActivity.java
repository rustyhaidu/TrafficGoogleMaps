package com.example.intersectiesemaforizata.uberanimation;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.intersectiesemaforizata.R;
import com.example.intersectiesemaforizata.backup.Semafor;
import com.example.intersectiesemaforizata.backup.Spital;
import com.example.intersectiesemaforizata.uberanimation.remote.IGoogleApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
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
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;

    private Spital spitalAleator;
    // private TextView spitalAlesTv;
    List<Semafor> semafoare = new ArrayList<>();

    // Unused objects
    private LatLng mDestination;
    private LatLng myLocation;
    // ---------
    IGoogleApi mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        polylineList = new ArrayList<>();
        btnGo = findViewById(R.id.btnSearch);
        edtPlace = findViewById(R.id.edtPlace);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination = edtPlace.getText().toString();
                destination = destination.replace(" ", "+");
                mapFragment.getMapAsync(MapsActivity.this);
            }
        });
        mService = Common.getGoogleApi();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng bucharest = new LatLng(44.4444282, 26.1279446);

        // ------------ Add Markers from backup package ---------------
        List<Spital> spitale = new ArrayList<>();
        spitale.add(new Spital(new LatLng(44.4417171, 26.172688), "Spital Pantelimon"));
        spitale.add(new Spital(new LatLng(44.4389115, 26.1468962), "Spitalul Monza"));
        spitale.add(new Spital(new LatLng(44.4409689, 26.1193474), "Spitalul Foisorul de Foc"));
        spitale.add(new Spital(new LatLng(44.4367611, 26.0703311), "Spitalul Universitar"));
        spitale.add(new Spital(new LatLng(44.3906953, 26.143549), "Spitalul Sf. Ioan"));

        Random random = new Random();
        int spitalAleatorNr = random.nextInt(spitale.size());

        Semafor semafor = new Semafor(new LatLng(44.386393, 26.139455), "Strada Vitan-Barzesti", 1);
        semafoare.add(semafor);
        semafoare.add(new Semafor(new LatLng(44.385642, 26.139563), "Strada Sergent Ion", 1));
        semafoare.add(new Semafor(new LatLng(44.385826, 26.139058), "Strada Sergent Ion", 2));
        semafoare.add(new Semafor(new LatLng(44.403278, 26.142435), "Splaiul Unirii", 1));
        semafoare.add(new Semafor(new LatLng(44.403934, 26.142426), "Splaiul Unirii", 2));

        //mDestination = new LatLng(44.4538018, 26.1006797);
        // spitalAleator = spitale.get(spitalAleatorNr);
        spitalAleator = spitale.get(4);
        mDestination = spitalAleator.getCoordonate();

        // spitalAlesTv = findViewById(R.id.spitalAles);
        // spitalAlesTv.setText(spitalAleator.getNumeSpital());

        // Spital Exact - Sfantul Ioan
        // spitalAlesTv.setText(spitale.get(4).getNumeSpital());

        // Google map setup 1.0
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Show marker on the screen and adjust the zoom level
        //map.addMarker(new MarkerOptions().position(mOrigin).title("Origin"));
        mMap.addMarker(new MarkerOptions().position(mDestination).title(spitalAleator.getNumeSpital()));
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 8f));

        mMap.addMarker(new MarkerOptions()
                .position(semafoare.get(0).getCoordonite())
                .title("Semafor Vitan")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic)));

        mMap.addMarker(new MarkerOptions()
                .position(semafoare.get(1).getCoordonite())
                .title("Strada Sergent Ion 1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic)));

        mMap.addMarker(new MarkerOptions()
                .position(semafoare.get(2).getCoordonite())
                .title("Strada Sergent Ion 2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic)));

        mMap.addMarker(new MarkerOptions()
                .position(semafoare.get(3).getCoordonite())
                .title("Splaiul Unirii 1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic)));

        mMap.addMarker(new MarkerOptions()
                .position(semafoare.get(4).getCoordonite())
                .title("Splaiul Unirii 2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic)));


        // -----------------------------

        mMap.addMarker(new MarkerOptions().position(bucharest).title("Marker in Bucharest"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bucharest));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target)
                .zoom(17)
                .bearing(30)
                .tilt(45)
                .build()));

        String requestUrl = null;
        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + bucharest.latitude + "," + bucharest.longitude + "&" +
                    //"destination=" + destination + "&" +
                    "destination=" + spitalAleator.getCoordonate().latitude + "," + spitalAleator.getCoordonate().longitude + "&" +
                    "key=" + getResources().getString(R.string.map_key);
            Log.d("URL", requestUrl);
            mService.getDataFromGoogleApi(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");
                            polylineList = decodePoly(polyline);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng latLng : polylineList) builder.include(latLng);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                    mMap.animateCamera(mCameraUpdate);

                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.GRAY);
                    polylineOptions.width(5);
                    polylineOptions.startCap(new SquareCap());
                    polylineOptions.endCap(new SquareCap());
                    polylineOptions.jointType(JointType.ROUND);
                    polylineOptions.addAll(polylineList);
                    greyPolyline = mMap.addPolyline(polylineOptions);

                    blackPolylineOptions = new PolylineOptions();
                    blackPolylineOptions.color(Color.BLACK);
                    blackPolylineOptions.width(5);
                    blackPolylineOptions.startCap(new SquareCap());
                    blackPolylineOptions.endCap(new SquareCap());
                    blackPolylineOptions.jointType(JointType.ROUND);
                    blackPolylineOptions.addAll(polylineList);
                    blackPolyline = mMap.addPolyline(blackPolylineOptions);

                    mMap.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size() - 1)));

                    final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                    valueAnimator.setDuration(2000);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            List<LatLng> points = greyPolyline.getPoints();
                            int percentValue = (int) valueAnimator.getAnimatedValue();
                            int size = points.size();
                            int newPoints = (int) (size * (percentValue / 100.0f));
                            List<LatLng> p = points.subList(0, newPoints);
                            blackPolyline.setPoints(p);
                        }
                    });
                    valueAnimator.start();
                    BitmapDescriptor bitmapDescriptor = bitmapDescriptorFromVector(getApplicationContext(),
                            R.drawable.ic_baseline_directions_car_24);
                    marker = mMap.addMarker(new MarkerOptions().position(bucharest).
                            flat(true).icon(bitmapDescriptor));

                    handler = new Handler();
                    index = -1;
                    next = 1;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (index < polylineList.size() - 1) {
                                index++;
                                next = index + 1;
                            }
                            if (index < polylineList.size() - 1) {
                                startPosition = polylineList.get(index);
                                endPosition = polylineList.get(next);
                            }
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                            valueAnimator.setDuration(3000);
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    v = valueAnimator.getAnimatedFraction();
                                    lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                                    lat = v * endPosition.latitude + (1 - v) * startPosition.latitude;
                                    LatLng newPos = new LatLng(lat, lng);

                                    // Add Toast to see if the car is in near a Semaphor
                                    if(semafoare.get(4).getCoordonite().latitude == lat
                                        && semafoare.get(4).getCoordonite().longitude == lng){
                                        Toast.makeText(MapsActivity.this, "All Lights Are Green", Toast.LENGTH_SHORT).show();
                                    }

                                    marker.setPosition(newPos);
                                    marker.setAnchor(0.5f, 0.5f);
                                    marker.setRotation(getBearing(startPosition, newPos));
                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                            .target(newPos)
                                            .zoom(15.5f)
                                            .build()));
                                }
                            });
                            valueAnimator.start();
                            handler.postDelayed(this, 3000);
                        }

                    }, 3000);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(MapsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private float getBearing(LatLng startPosition, LatLng newPos) {
        double lat = Math.abs(startPosition.latitude - newPos.latitude);
        double lng = Math.abs(startPosition.longitude - newPos.longitude);

        if (startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) Math.toDegrees(Math.atan(lng / lat));
        else if (startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private List<LatLng> decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}