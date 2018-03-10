package chrust.emploeye;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ImageView add_places;
RecyclerView rv;
DatabaseReference databaseReference;
FirebaseAuth mAuth;
TasksAdapter adapter;
LinearLayoutManager linearLayoutManager;
ArrayList<Task> taskArrayList;
ImageView perm_btn;
//MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskArrayList = new ArrayList<Task>();
        rv =(RecyclerView)findViewById(R.id.rv);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        add_places = (ImageView)findViewById(R.id.add_location);
        add_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddplaceActivity.class));
            }
        });
        perm_btn = (ImageView)findViewById(R.id.perm_btn);
        mAuth = FirebaseAuth.getInstance();  //creating auth instance
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tasks").child(mAuth.getCurrentUser().getUid()); // creating databse reference of the same user
        linearLayoutManager = new LinearLayoutManager(this);

        perm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AskingPermissionActivity.class));
            }
        });     // user can ask permission for taking leave like mail
        rv.setHasFixedSize(true);
        rv.setLayoutManager(linearLayoutManager);
        adapter = new TasksAdapter(Task.class,R.layout.tasks_card,TaskViewHolder.class,databaseReference,this); // firebaase adapter to view the tasks
        rv.setAdapter(adapter);


}
    }






