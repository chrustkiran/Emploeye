package chrust.emploeye;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Chrustkiran on 23/02/2018.
 */

public class TasksAdapter extends FirebaseRecyclerAdapter<Task,TaskViewHolder> {
Context context;
    public TasksAdapter(Class modelClass, int modelLayout, Class viewHolderClass, DatabaseReference ref, Context context) {

        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
    }



    @Override
    protected void populateViewHolder(TaskViewHolder viewHolder, final Task model, int position) { //fetch from firebase
        viewHolder.name_task_txt.setText(model.getName());
        viewHolder.date_txt.setText("On "+model.getDate());
        viewHolder.start_txt.setText("from "+model.getStart_time());
        viewHolder.end_txt.setText("to "+model.getEnd_time());
        viewHolder.image_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,CheckLocationActivity.class);
                intent.putExtra("lat",model.getLat());
                intent.putExtra("lng",model.getLng());
                intent.putExtra("name",model.getName());
                context.startActivity(intent);
            }
        });

    }
}
