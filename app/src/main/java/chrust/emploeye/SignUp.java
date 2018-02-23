package chrust.emploeye;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    EditText email_sign_up,password_sign_up,repassword_sign_up;
    ImageView btn_send,btn_verify;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        btn_send = (ImageView)findViewById(R.id.btn_send);
        email_sign_up = (EditText)findViewById(R.id.email_sign_up);
        password_sign_up = (EditText)findViewById(R.id.password_sign_up);
        repassword_sign_up = (EditText)findViewById(R.id.repassword_sign_up);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    signUp();
            }
        });
    }


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
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful()){

                    }
                        else{
                        Toast.makeText(SignUp.this,"Incorrect email or password!",Toast.LENGTH_SHORT);
                    }
                }

            });

        }



    }



    }



