package chrust.emploeye;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chrustkiran on 23/02/2018.
 */

public class TaskViewHolder extends RecyclerView.ViewHolder {
TextView name_task_txt,end_txt,start_txt,lng_txt,lat_txt,date_txt,state_txt;
ImageView image_map;
    public TaskViewHolder(View itemView) {


        super(itemView);
        name_task_txt = (TextView)itemView.findViewById(R.id.name_task_txt);
        date_txt = (TextView)itemView.findViewById(R.id.date_task_txt);
        end_txt =(TextView)itemView.findViewById(R.id.end_time_txt);
        start_txt = (TextView)itemView.findViewById(R.id.start_time_txt);
        image_map = (ImageView)itemView.findViewById(R.id.image_map);
        state_txt = (TextView)itemView.findViewById(R.id.state_text);

    }


}
