package chrust.emploeye;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

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
TextView logout;
//MyAdapter myAdapter;
   // SharedPreferences
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
        logout = (TextView)findViewById(R.id.logout) ;
        perm_btn = (ImageView)findViewById(R.id.perm_btn);
        mAuth = FirebaseAuth.getInstance();  //creating auth instance
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tasks").child(mAuth.getCurrentUser().getUid()); //creating reference

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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                sharedpreferences.edit().putBoolean("logged",false).apply();
                editor.clear();
                editor.commit();
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,SignIn.class));
            }
        });

}

    @Override
    public void onBackPressed() {
        this.finish();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        super.onBackPressed();
    }
}






