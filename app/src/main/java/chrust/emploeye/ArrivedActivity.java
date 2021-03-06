package chrust.emploeye;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ArrivedActivity extends AppCompatActivity {

    public   TextView timer;
    java.util.Date noteTS;
    private ImageView gallery;
    private Handler handler = new Handler();
    int time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrived);

        timer = (TextView)findViewById(R.id.timer);
        gallery = (ImageView)findViewById(R.id.gallery);

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               // time++;
                                //timer.setText(time);
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }
    @SuppressLint("ResourceType")
    private void updateTextView() {
        time++;
        if(Integer.toString(time/60).length()==1 && Integer.toString(time%60).length()==1) {
            timer.setText("0"+time / 60 + ":0" + time % 60);
        }
        else if(Integer.toString(time/60).length()==2 && Integer.toString(time%60).length()==1){
            timer.setText(time / 60 + ":0" + time % 60);
        }
        else if(Integer.toString(time/60).length()==1 && Integer.toString(time%60).length()==2){
            timer.setText("0"+time / 60 + ":" + time % 60);
        }
        else if(Integer.toString(time/60).length()==2 && Integer.toString(time%60).length()==2){
            timer.setText(time / 60 + ":" + time % 60);
        }



    }

    private void uploadingPhoto(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            gallery.setImageBitmap(imageBitmap);
        }
    }



        }
