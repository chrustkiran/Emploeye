package chrust.emploeye;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckLocationActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Double target_lat;
    private Double target_lng;
    private String name;
    private ImageView visit;
    private LatLng myLocation;
    private LatLng target;
    private Location mLastLocation;
    private ProgressDialog progressDialog;
    private List<Polyline> polylines;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private Boolean buttonPressed;
    private Marker marker;
    Routing routing;
    Routing routing1;
    DatabaseReference ref;
    private static final int[] COLORS = new int[]{R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorControlActivated, R.color.colorAccent, R.color.primary_dark_material_light};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylines = new ArrayList<>();

        //Initilizing
        visit = (ImageView) findViewById(R.id.visit);

        buttonPressed = false;

        //startService(new Intent(this, YourServices.class)); //run in background

        target_lat = Double.parseDouble(getIntent().getExtras().getString("lat"));
        target_lng = Double.parseDouble(getIntent().getExtras().getString("lng"));
        name = getIntent().getExtras().getString("name");


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
        this.target = new LatLng(target_lat, target_lng);
        mMap.addMarker(new MarkerOptions().position(target).title(name).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).anchor(0.5f,0.5f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 16));

        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTarget();
                buttonPressed =true;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
       // mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();



    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                build();
        mGoogleApiClient.connect();
    }


    private void goToTarget() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            this.myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Loation").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_1)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));

        }



       /* final Handler handler = new  Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(15).color(CheckLocationActivity.this.getResources().getColor(R.color.colorPrimary));

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    Polyline polylin = mMap.addPolyline(rectLine);
                    md.getDurationText(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        new GMapV2DirectionAsyncTask(handler, myLocation, target, GMapV2Direction.MODE_DRIVING).execute();
*/
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route.", true);
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(myLocation, target)
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(progressDialog!=null){
        progressDialog.dismiss();
        }
        CameraUpdate center = CameraUpdateFactory.newLatLng(myLocation);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int j = 0; j < route.size(); j++) {

            //In case of more than 5 alternative routes
            int colorIndex = j % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + j * 3);
            polyOptions.addAll(route.get(j).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (j + 1) + ": distance - " + route.get(j).getDistanceValue() + ": duration - " + route.get(j).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
       /* options.position(myLocation);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_ic));
        mMap.addMarker(options);*/

        // End marker
        /*options = new MarkerOptions();
        options.position(target);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target));
        mMap.addMarker(options);*/

    }

    @Override
    public void onRoutingCancelled() {
        Log.i("Log", "Routing was cancelled.");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);


    }

    private void changingLine() {   //creating moving lines
        FirebaseDatabase.getInstance().getReference().child("current").child("chrust").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentLocation currentLocation = dataSnapshot.getValue(CurrentLocation.class);
                if(currentLocation.getAvailable().equals("t")){
                    if(routing1!=null){
                        polylines.clear();  //deleting existing path.
                    }
                    routing1 = new Routing.Builder()
                            .travelMode(Routing.TravelMode.DRIVING)
                            .withListener(CheckLocationActivity.this)
                            .waypoints(target, new LatLng(currentLocation.getLat(),currentLocation.getLng()))
                            .build();
                    routing1.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isArraived(Location currentLocation , Location targetLocation){
        if(currentLocation.distanceTo(targetLocation)<100){
            return true;
        }
        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(buttonPressed) {
            mLastLocation = location;

            LatLng latLng_locationChanged = new LatLng(location.getLatitude(), location.getLongitude());
            if(marker !=null){
                marker.remove();
            }
                marker = mMap.addMarker(new MarkerOptions().position(latLng_locationChanged).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_1)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_locationChanged));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom));

            ref = FirebaseDatabase.getInstance().getReference().child("current");
            ref.child("chrust").child("lat").setValue(mLastLocation.getLatitude());

            ref.child("chrust").child("lng").setValue(mLastLocation.getLongitude());
            ref.child("chrust").child("available").setValue("t");

            if(buttonPressed) {
                changingLine();

                Location currentLocation = new Location("");
                currentLocation.setLatitude(mLastLocation.getLatitude());
                currentLocation.setLongitude(mLastLocation.getLongitude());

                Location targertLocation = new Location("");
                targertLocation.setLongitude(target.longitude);
                targertLocation.setLatitude(target.latitude);

                if(isArraived(currentLocation,targertLocation)){
                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    startActivity(new Intent(CheckLocationActivity.this,ArrivedActivity.class));
                                    buttonPressed=false;
                                    onStop();
                                    CheckLocationActivity.this.finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }


            }


        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        ref = FirebaseDatabase.getInstance().getReference().child("current");
        ref.child("chrust").child("available").setValue("f");
    }
}

