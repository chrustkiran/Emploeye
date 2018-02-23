package chrust.emploeye;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignIn extends AppCompatActivity {
    private TextView input_email,input_password,link_sign_up;
    private ImageView btn_login;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
       /* getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.abs_layout);
*/
       getSupportActionBar().hide();
        input_email = (TextView)findViewById(R.id.input_email);
        input_password = (TextView)findViewById(R.id.input_password);
        btn_login = (ImageView)findViewById(R.id.btn_login);
        link_sign_up = (TextView)findViewById(R.id.link_signup);
       // databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        // databaseReference.child("name").setValue("Chrust");
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,MainActivity.class));
            }
        });

        link_sign_up = (TextView)findViewById(R.id.link_signup);
        link_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,SignUp.class));
            }
        });


    }

}
