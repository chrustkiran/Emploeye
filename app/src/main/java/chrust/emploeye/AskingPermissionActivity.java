package chrust.emploeye;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class AskingPermissionActivity extends AppCompatActivity {
EditText sub_txt,body_txt;
ImageView chooser,sender,pic1;
int PICK_IMAGE_REQUEST = 1;
Uri filePath;
StorageReference storageReference;
FirebaseAuth mAuth;
private String subject,body,photo;
DatabaseReference databaseReference;
private Permission permission;
private String date;
DateFormat df;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asking_permission);
        storageReference = FirebaseStorage.getInstance().getReference();
        chooser = (ImageView)findViewById(R.id.chooser);
        sender = (ImageView)findViewById(R.id.sender);
        pic1 = (ImageView)findViewById(R.id.pic1);
        sub_txt = (EditText)findViewById(R.id.sub_txt);
        body_txt=(EditText)findViewById(R.id.body_txt);
        subject =""; //empty subject
        body = "";  //empty body
        photo = "";  //empty photo

        df = new SimpleDateFormat("dd/MM/yyyy");
        date = df.format(System.currentTimeMillis());
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Permissions").child(mAuth.getCurrentUser().getUid());
        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
                subject = sub_txt.getText().toString();
                body = body_txt.getText().toString();
                if(filePath == null){

                    permission = new Permission(subject,body,photo,date,"unseen"); //if photo is null permission
                    databaseReference.push().setValue(permission);
                    Toast.makeText(AskingPermissionActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
             filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                pic1.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void upload(){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AskingPermissionActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            permission = new Permission(subject,body,taskSnapshot.getDownloadUrl().toString(),date,"unseen");
                            databaseReference.push().setValue(permission);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AskingPermissionActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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


}
