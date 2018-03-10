package chrust.emploeye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    EditText email_sign_up,password_sign_up,repassword_sign_up,company,nic,name,address;
    ImageView btn_verify;
    FirebaseAuth mAuth;
    DatabaseReference mData;
    ProgressDialog progressDialog;
    String user_name, user_address,user_email,user_nic,user_comapny;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        email_sign_up = (EditText)findViewById(R.id.email_sign_up);
        password_sign_up = (EditText)findViewById(R.id.password_sign_up);
        repassword_sign_up = (EditText)findViewById(R.id.repassword_sign_up);
        btn_verify = (ImageView)findViewById(R.id.btn_verify);
        company = (EditText) findViewById(R.id.comapny);
        nic = (EditText)findViewById(R.id.nic);
        name = (EditText)findViewById(R.id.name);
        address = (EditText)findViewById(R.id.address);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                //Toast.makeText(SignUp.this,"Hiii",Toast.LENGTH_SHORT).show();
                user_address = address.getText().toString();
                user_comapny = company.getText().toString();
                user_nic = nic.getText().toString();
                user_email = email_sign_up.getText().toString();
                user_name = name.getText().toString();
                signUp();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void signUp(){
        String email = email_sign_up.getText().toString().trim();
        String password = password_sign_up.getText().toString().trim();
        String repassword = repassword_sign_up.getText().toString().trim();
        if(!password.equals(repassword)){
            repassword_sign_up.setText("");
            repassword_sign_up.setError("password does not match!");
        }
        if(TextUtils.isEmpty(email)){
            email_sign_up.setError("Your email is empty!");
            return;
        }
        if(TextUtils.isEmpty(password)){
            password_sign_up.setError("Your password is empty");
            return;
        }
        if (TextUtils.isEmpty(repassword)){
            repassword_sign_up.setError("Re enter your password please!");
            return;
        }
        if(password.length()<6){
            password_sign_up.setError( "Password must be greater than 6"
            );
        }
        else{
            progressDialog.setMessage("Siging up...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if(task.isSuccessful()){

                        User user = new User(user_name,user_email,user_address,user_email,user_nic);
                        FirebaseDatabase.getInstance().getReference().child("Users").
                                child(mAuth.getCurrentUser().getUid()).setValue(user);
                        //FirebaseDatabase.getInstance().getReference().child("CompanyUser").child(user_email).setValue(user_comapny.toString());
                        startActivity(new Intent(SignUp.this,MainActivity.class));

                    }
                        else{
                        Toast.makeText(SignUp.this,"Incorrect email or password!",Toast.LENGTH_SHORT);
                    }
                }

            });

        }



    }



    }



