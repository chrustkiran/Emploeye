package chrust.emploeye;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ArrivedActivity extends AppCompatActivity {

    private  TextView timer;
    private ImageView gallery;
    private Handler handler = new Handler();
    int time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrived);

        timer = (TextView)findViewById(R.id.timer);
        gallery = (ImageView)findViewById(R.id.gallery);
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("0:"+checkDigit(time));
                time++;
            }

            public void onFinish() {
                timer.setText("try again");
            }

        }.start();

    }

public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
        }

        }
