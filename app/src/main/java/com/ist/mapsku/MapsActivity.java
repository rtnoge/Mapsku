package com.ist.mapsku;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity<MainActivity> extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    SupportMapFragment mapFragment;

    String TAG = "Mapsku";

    /*/ Map<String, LatLng> locations = new HashMap<>();
    LatLng sabang = new LatLng(5.9668636, 95.1658195);
    LatLng merauke = new LatLng(-8.7142631, 139.7545927); /*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        /*/ locations.put("Dari Sabang", sabang);
        locations.put("Sampai Merauke", merauke); /*/

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*/Some locations lat-long/*/
//        Map<Marker, String> markers = new HashMap<Marker, String>();
//        for (Map.Entry<String, LatLng> location : locations.entrySet()) {
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(location.getValue());
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
//
//            Marker marker = mMap.addMarker(markerOptions);
//            markers.put(marker, location.getKey());
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(location.getValue()));
//            mMap.getUiSettings().setCompassEnabled(true);
//        }

        getLocation();
        changeCompassPosition();

        mMap.getUiSettings().setCompassEnabled(true);

        int top = (int) (20 * Resources.getSystem().getDisplayMetrics().density);
        mMap.setPadding(0, top, 5, 0);

//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
//                View view = getLayoutInflater()
//                        .inflate(R.layout.infowindowlayout, null);
//                TextView text = (TextView) view.findViewById(R.id.title);
////                text.setText(marker.getTitle());
//                text.setText(markers.get(marker));
//                return view;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        });
    }

    /*/method for scaled pin size/*/
    private Bitmap scaledPin() {
        int height = (int) (25 * Resources.getSystem().getDisplayMetrics().density);
        int width = (int) (25 * Resources.getSystem().getDisplayMetrics().density);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.asset);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return smallMarker;
    }

    /*/method for change compass position (left to right) additional when setPadding/*/
    private void changeCompassPosition() {
        try {
            View compassButton = mapFragment.getView().findViewWithTag("GoogleMapCompass");//to access the compass button
            Log.d(TAG, "compassButton " + compassButton);
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_END);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
            rlp.topMargin = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*/method for get current lat-long/*/
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MapsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());

            Bitmap smallMarker = scaledPin();

            String address = addresses.get(0).getAddressLine(0);
            String province = addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();

            Log.d(TAG, "Current LatLng: " + address);
            Toast.makeText(this, address, Toast.LENGTH_LONG).show();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(current_location);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            Marker marker = mMap.addMarker(markerOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 16));

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    View view = getLayoutInflater()
                            .inflate(R.layout.infowindowlayout, null);
                    TextView text = (TextView) view.findViewById(R.id.title);
                    text.setText(province);
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            /*/ show if set to one location /*/
            marker.showInfoWindow();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
        }
    }
}