package chrust.emploeye;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.widget.TextView;

import static com.google.android.gms.internal.zzail.runOnUiThread;

/**
 * Created by Chrustkiran on 12/05/2018.
 */

public class TimerArrived {

    public TextView timer;
    private Handler handler = new Handler();
    int time = 0;

    public TimerArrived(TextView timer){
        this.timer = timer;
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
    public int getTime(){
        return time;
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

}
