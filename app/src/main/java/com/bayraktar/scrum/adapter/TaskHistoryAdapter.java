package com.bayraktar.scrum.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.TaskHistory;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bayraktar.scrum.App.firebaseService;

public class TaskHistoryAdapter extends RecyclerView.Adapter<TaskHistoryAdapter.ViewHolder> {

    List<String> taskHistoryList;
    Context context;

    public TaskHistoryAdapter(Context context) {
        this.context = context;
        taskHistoryList = new ArrayList<>();
    }

    public void setTaskHistoryList(List<String> taskHistoryList) {
        this.taskHistoryList = taskHistoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task_history, parent, false);
        return new TaskHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String key = taskHistoryList.get(position);
        firebaseService.getTaskHistory(key, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TaskHistory item = snapshot.getValue(TaskHistory.class);
                    if (item != null) {

                        int icon = R.drawable.ic_suspended;
                        int status = R.string.menu_pending;
                        int color = R.color.colorOrange;
                        switch (item.getCurrentTaskStatus()) {
                            case 1:
                                icon = R.drawable.ic_active;
                                status = R.string.menu_active;
                                color = R.color.colorGreen;
                                break;
                            case 2:
                                icon = R.drawable.ic_completed;
                                status = R.string.menu_completed;
                                color = R.color.colorDarkGrey;
                                break;
                            case 3:
                                status = R.string.menu_suspended;
                                color = R.color.colorBlue;
                                break;
                        }

                        firebaseService.getUser(item.getConstituent(), new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null) {
                                        holder.tvConstituent.setText(user.getName());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.ivStatus.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN);
                        holder.ivStatus.setImageResource(icon);
                        holder.tvStatus.setText(status);
//                        holder.tvChangeDate.setText(DateFormat.getMediumDateFormat(context).format(item.getChangeDate()));
                        holder.tvChangeDate.setText(new SimpleDateFormat("HH:mm | EEE dd MMM y", Locale.getDefault()).format(item.getChangeDate()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return taskHistoryList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivStatus;
        final TextView tvChangeDate;
        final TextView tvConstituent;
        final TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.ivStatus);
            tvChangeDate = itemView.findViewById(R.id.tvChangeDate);
            tvConstituent = itemView.findViewById(R.id.tvConstituent);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
