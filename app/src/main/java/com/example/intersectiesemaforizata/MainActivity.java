package com.example.intersectiesemaforizata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng mOrigin;
    private LatLng mDestination;
    private final static int MY_PERMISSIONS_REQUEST = 32;
    private Spital spitalAleator;
    private TextView spitalAlesTv;
    List<Semafor> semafoare = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOrigin = new LatLng(44.4461241, 26.1274945);

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
        spitalAleator = spitale.get(spitalAleatorNr);
        mDestination = spitalAleator.getCoordonate();

        spitalAlesTv = findViewById(R.id.spitalAles);
        spitalAlesTv.setText(spitalAleator.getNumeSpital());

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        /* Aleea Avrig Sector 2 - Spitalul Floreasca Sector 2
        44.4461241,26.1274945 - 44.4538018,26.1006797 */

        LatLng bucuresti = new LatLng(44.4267674, 26.1025384);
        map.addMarker(new MarkerOptions().position(bucuresti).title("Bucuresti Oras"));
        map.moveCamera(CameraUpdateFactory.newLatLng(bucuresti));

        // Google map setup 1.0
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        // Show marker on the screen and adjust the zoom level
        map.addMarker(new MarkerOptions().position(mOrigin).title("Origin"));
        map.addMarker(new MarkerOptions().position(mDestination).title(spitalAleator.getNumeSpital()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, 8f));

        map.addMarker(new MarkerOptions()
                .position(semafoare.get(0).getCoordonite())
                .title("Semafor Vitan")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic)));

        new TaskDirectionRequest().execute(getRequestedUrl(mOrigin, spitalAleator.getCoordonate()));

    }

    private void requestPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    MY_PERMISSIONS_REQUEST);
        }
    }

    private String getRequestedUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.map_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + APIKEY;
        return url;
    }

    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuilder stringBuffer = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        assert httpURLConnection != null;
        httpURLConnection.disconnect();
        return responseString;
    }

    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);

            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }

    //Parse JSON Object from Google Direction API & display it on Map
    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }
            if (polylineOptions != null) {
                map.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }

}