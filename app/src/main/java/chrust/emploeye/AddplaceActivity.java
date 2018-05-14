package chrust.emploeye;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddplaceActivity extends FragmentActivity implements OnMapReadyCallback {

    private DatabaseReference taskref;
    private DatabaseReference mData;
    private String user = "0756761432";
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private ImageView choose;
    private Location myLocation;
    private static String comapny;
    private String task_name;
    private String task_date;
    private String start_time;
    private String end_time;
    private String lat;
    private String lng;
    private long startTimeLong;
    private long endTimeLong;
    public SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplace);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        choose = (ImageView) findViewById(R.id.choose);
        mAuth = FirebaseAuth.getInstance();

        sdf = new SimpleDateFormat("HH:mm");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // map instance

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //location manager instance
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } //check the permission
        mMap.setMyLocationEnabled(true); //just to make easy to user to pick the location, it will show where he is.
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));

        }

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplication(),mMap.getCameraPosition().target.latitude+","+mMap.getCameraPosition().target.longitude,Toast.LENGTH_SHORT).show();
                taskref = FirebaseDatabase.getInstance().getReference().child("tasks");
                showNameDialog(); //set task it will ask the task's name



            }
        });
    }


    protected void showNameDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(AddplaceActivity.this);
        View promptView = layoutInflater.inflate(R.layout.task_name, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddplaceActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.task_name);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       AddplaceActivity.this.task_name = editText.getText().toString(); // getting typed name
                       showCalendatDialog(); //it will ask date to choose task's date
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    protected  void showCalendatDialog(){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(AddplaceActivity.this);
        View promptView = layoutInflater.inflate(R.layout.task_date, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddplaceActivity.this);
        alertDialogBuilder.setView(promptView);

        final CalendarView calendarView = (CalendarView) promptView.findViewById(R.id.task_calendar);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        DateFormat df = new  SimpleDateFormat("dd/MM/yyyy"); //to change the date format into string
                        Date today = new Date();
                        Date selected = new Date(calendarView.getDate());

                            AddplaceActivity.this.task_date = df.format(selected); //getting task's date
                            try {
                                showStartTimeDialog();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }






                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void showStartTimeDialog() throws ParseException {
        LayoutInflater layoutInflater = LayoutInflater.from(AddplaceActivity.this);
        View promptView = layoutInflater.inflate(R.layout.task_start_time, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddplaceActivity.this);
        alertDialogBuilder.setView(promptView);

        final TimePicker timePicker = (TimePicker) promptView.findViewById(R.id.start_timePicker);
        startTimeLong = sdf.parse(timePicker.getHour()+":"+timePicker.getMinute()).getTime();
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        AddplaceActivity.this.start_time = timePicker.getHour()+":"+timePicker.getMinute(); //getting start time

                        showEndTimeDialog();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }
    protected void showEndTimeDialog(){
        LayoutInflater layoutInflater = LayoutInflater.from(AddplaceActivity.this);
        View promptView = layoutInflater.inflate(R.layout.task_end_time, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddplaceActivity.this);
        alertDialogBuilder.setView(promptView);

        final TimePicker timePicker = (TimePicker) promptView.findViewById(R.id.end_timePicker); //its need to be validated its needede to be validated be
        //cause end time is always > starting time.
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        //have to check validation here!!
                        try {
                            endTimeLong = sdf.parse(timePicker.getHour()+":"+timePicker.getMinute()).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        AddplaceActivity.this.end_time = timePicker.getHour() + ":" + timePicker.getMinute();


                        if(task_name!=null && task_date !=null && start_time!=null && end_time !=null ){
                            if(new Date(endTimeLong).after(new Date(startTimeLong))) {
                                Task task = new Task(task_date, task_name, Double.toString(mMap.getCameraPosition().target.latitude),
                                        Double.toString(mMap.getCameraPosition().target.longitude), start_time, end_time, "active");
                                taskref.child(mAuth.getCurrentUser().getUid()).push().setValue(task);
                                Toast.makeText(AddplaceActivity.this, "Task is added", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(AddplaceActivity.this,"Task can't end before it gets started",Toast.LENGTH_SHORT).show();
                                showEndTimeDialog();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }


}
