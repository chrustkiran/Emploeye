package chrust.emploeye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignIn extends AppCompatActivity {
    private TextView link_sign_up;
    private ImageView btn_login;
    private ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    private EditText input_email,input_password;
    String email,password;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

       getSupportActionBar().hide();
       mAuth = FirebaseAuth.getInstance();
       progressDialog = new ProgressDialog(this);
        input_email = (EditText)findViewById(R.id.input_email);
        input_password = (EditText)findViewById(R.id.input_password);
        btn_login = (ImageView)findViewById(R.id.btn_login);
        link_sign_up = (TextView)findViewById(R.id.link_signup);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                email = input_email.getText().toString();
                password = input_password.getText().toString();

                if (TextUtils.isEmpty(email)) { //email is not empty
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) { // password is not empty
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    progressDialog.setMessage("Signing in...");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    input_password.setError("Your password's length must be greater than 6");
                                } else {
                                    Toast.makeText(SignIn.this,"Signing in failed!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    });

                }

            }
        });

        link_sign_up = (TextView)findViewById(R.id.link_signup);
        link_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this,SignUp.class)); // go to sign up class
            }
        });


    }

}
