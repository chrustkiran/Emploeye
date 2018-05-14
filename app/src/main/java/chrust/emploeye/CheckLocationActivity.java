package chrust.emploeye;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    FirebaseAuth mAuth;
    boolean notArrivedCurrentLocation;
    LocationManager mLocationManager;
    TextView timerText;
    Button btn_gallery;
    String key;
    ImageView gallery;
    Uri filepath;
    StorageReference storageReference;
    int photoUploaded = 0;
    String user_name;
    int points =0;
    String start_time;
    String end_time;
    int final_point;
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
        mAuth = FirebaseAuth.getInstance();
        buttonPressed = false;
        btn_gallery = (Button) findViewById(R.id.btn_gallery);
        timerText = (TextView) findViewById(R.id.timerText);
        gallery =  (ImageView) findViewById(R.id.gallery);
        storageReference = FirebaseStorage.getInstance().getReference();


        notArrivedCurrentLocation = true; //setting user is not reached to the target
        //startService(new Intent(this, YourServices.class)); //run in background

        //getting extras from previous intent
        target_lat = Double.parseDouble(getIntent().getExtras().getString("lat"));
        target_lng = Double.parseDouble(getIntent().getExtras().getString("lng"));
        name = getIntent().getExtras().getString("name");
        key = getIntent().getExtras().getString("key");
        start_time = getIntent().getExtras().getString("end_time");
        end_time = getIntent().getExtras().getString("end_time");

        //make timerText and galley button invisible
        btn_gallery.setVisibility(View.INVISIBLE);
        btn_gallery.setClickable(false);
        timerText.setVisibility(View.INVISIBLE);
        gallery.setVisibility(View.INVISIBLE);

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
        mMap.addMarker(new MarkerOptions().position(target).title(name).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).anchor(0.5f, 0.5f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 16));

        visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTarget();
                buttonPressed = true;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                build();
        mGoogleApiClient.connect();
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
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
        else{
            this.myLocation = new LatLng(8.590236, 81.205848);
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
        FirebaseDatabase.getInstance().getReference().child("current").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentLocation currentLocation = dataSnapshot.getValue(CurrentLocation.class);
                if(currentLocation.getAvailable().equals("t")){
                    if(routing1!=null){
                        for(Polyline line : polylines)
                        {
                            line.remove(); //clearing all the polylines existed already on the map
                        }

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
                marker = mMap.addMarker(new MarkerOptions().position(latLng_locationChanged).title("my loc").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_1)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_locationChanged));

            mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom));

            ref = FirebaseDatabase.getInstance().getReference().child("current");
            ref.child(mAuth.getCurrentUser().getUid()).child("lat").setValue(mLastLocation.getLatitude());

            ref.child(mAuth.getCurrentUser().getUid()).child("lng").setValue(mLastLocation.getLongitude());
            ref.child(mAuth.getCurrentUser().getUid()).child("available").setValue("t");

            if(buttonPressed) {
                changingLine();

                final Location currentLocation = new Location("");
                currentLocation.setLatitude(mLastLocation.getLatitude());
                currentLocation.setLongitude(mLastLocation.getLongitude());

                final Location targertLocation = new Location("");
                targertLocation.setLongitude(target.longitude);
                targertLocation.setLatitude(target.latitude);


                if(isArraived(currentLocation,targertLocation ) && notArrivedCurrentLocation){
                    final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //setting up visibility
                                    timerText.setVisibility(View.VISIBLE);
                                    btn_gallery.setVisibility(View.VISIBLE);
                                    btn_gallery.setClickable(true);

                                    TimerArrived timerArrived = new TimerArrived(timerText);
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                    Date arrivedTime = Calendar.getInstance().getTime();
                                    btn_gallery.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            uploadingPhoto();
                                        }
                                    });
                                    //CheckLocationActivity.this.finish();

                                    if(isGone(currentLocation,targertLocation)){
                                        DialogInterface.OnClickListener dialogClickListener =
                                                new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckLocationActivity.this);
                                        builder.setMessage("Seems you are leaving from your target.your duration is "+timerArrived.getTime()).show();
                                        FirebaseDatabase.getInstance().getReference().child("tasks").child(mAuth.getCurrentUser().getUid()).
                                                child(key).child("state").setValue("finished");
                                        //calculating performance
                                        try {
                                            performance(timerArrived.getTime(),arrivedTime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        //FirebaseDatabase.getInstance().getReference().child("performance").
                                        startActivity(new Intent(CheckLocationActivity.this,MainActivity.class));
                                    }

                                    notArrivedCurrentLocation = false;
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You have reached your target?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }




            }


        }

    }
    private boolean isGone(Location currentLocation , Location targetLocation){  //chech whether employee is gone from his target
        if(currentLocation.distanceTo(targetLocation)>100){
            return true;
        }
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();

        ref = FirebaseDatabase.getInstance().getReference().child("current");
        ref.child(mAuth.getCurrentUser().getUid()).child("available").setValue("f");   //when this app is closed users availibilty will be set false
    }

    private void uploadingPhoto(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //gallery.setImageBitmap(imageBitmap);
            filepath = getImageUri(CheckLocationActivity.this,imageBitmap); //getting uri for this captured image
            upload();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void upload(){
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        if(filepath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Activityimages/"+ UUID.randomUUID().toString());
            ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dataRef.child("activityPhotos").child(mAuth.getCurrentUser().getUid()).
                                    child(name).push().setValue(taskSnapshot.getDownloadUrl().toString());
                            progressDialog.dismiss();
                            photoUploaded = 1;
                            Toast.makeText(CheckLocationActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CheckLocationActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    void performance(int timer, Date arrivedTime) throws ParseException {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey()==mAuth.getCurrentUser().getUid()){
                    User user = dataSnapshot.getValue(User.class);
                    user_name = user.getName();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("performance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getKey()==user_name){
                     points = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date start_time_date = sdf.parse(start_time);
        Date end_time_date = sdf.parse(end_time);
        long totolasecond = (end_time_date.getTime()-start_time_date.getTime())/1000;
         final_point = 100*(timer/(int)totolasecond);
        if(start_time_date.getTime() > arrivedTime.getTime()){
            //giving extra points
            final_point = final_point+100;
        }
        if(end_time_date.getTime()<arrivedTime.getTime()){
            //detucting points
            final_point = final_point-10;
        }
        final_point = (final_point+photoUploaded*100)/3;
        if(points != 0){
            //if there has already been any points, the final points is average of the previous one
            FirebaseDatabase.getInstance().getReference().child("performance").child(user_name).setValue((points+final_point)/2);
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("performace").child(user_name).setValue(final_point);
        }
    }

}

