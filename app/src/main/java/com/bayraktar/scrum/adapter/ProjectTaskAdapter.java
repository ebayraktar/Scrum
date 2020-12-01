package com.bayraktar.scrum.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Task;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseService;

public class ProjectTaskAdapter extends RecyclerView.Adapter<ProjectTaskAdapter.ViewHolder> {

    List<String> projectTaskList;
    OnProjectTaskListener onProjectTaskListener;
    Context context;

    public ProjectTaskAdapter(Context context, OnProjectTaskListener onProjectTaskListener) {
        this.context = context;
        this.onProjectTaskListener = onProjectTaskListener;
        projectTaskList = new ArrayList<>();
    }

    public void setProjectTaskList(List<String> projectTaskListModelList) {
        this.projectTaskList = projectTaskListModelList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ProjectTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_task_item, parent, false);
        return new ProjectTaskAdapter.ViewHolder(view, onProjectTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProjectTaskAdapter.ViewHolder holder, int position) {
        final String taskID = projectTaskList.get(position);

        firebaseService.getTask(taskID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Task item = snapshot.getValue(Task.class);

                    holder.tvTask.setText(item.getTaskName());

                    firebaseService.getUser(item.getConstituent(), new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    Glide.with(holder.itemView)
                                            .load(user.getPhotoURL())
                                            .fitCenter()
                                            .placeholder(R.drawable.ic_image)
                                            .error(R.drawable.ic_broken_image)
                                            .into(holder.ivConstituent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (item.getStartDate() != null) {
                        holder.tvStartDate.setText(DateFormat.getMediumDateFormat(context).format(item.getStartDate()));
                    }
                    if (item.getDeadline() != null) {
                        holder.tvDeadline.setText(DateFormat.getMediumDateFormat(context).format(item.getDeadline()));
                        Date now = Calendar.getInstance().getTime();
                        if (item.getDeadline().before(now)) {
                            holder.ivDate.setColorFilter(ContextCompat.getColor(context, R.color.colorRed), PorterDuff.Mode.SRC_IN);
                            holder.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                            holder.tvStartDate.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                        }
                    }

                    int statusColor;
                    switch (item.getTaskStatusID()) {
                        case 1:
                            statusColor = R.color.colorGreen;
                            break;
                        case 2:
                            statusColor = R.color.colorDarkGrey;
                            break;
                        case 3:
                            statusColor = R.color.colorBlue;
                            break;
                        default:
                            statusColor = R.color.colorOrange;
                            break;
                    }
                    holder.viewStatus.setBackgroundResource(statusColor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return projectTaskList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ConstraintLayout clProjectTaskContent;

        final TextView tvTask;
        final ImageView ivConstituent;
        final ImageView ivDate;
        final TextView tvStartDate;
        final TextView tvDeadline;

        final View viewStatus;

        final ProjectTaskAdapter.OnProjectTaskListener onProjectTaskListener;

        public ViewHolder(@NonNull View itemView, ProjectTaskAdapter.OnProjectTaskListener onProjectTaskListener) {
            super(itemView);
            clProjectTaskContent = itemView.findViewById(R.id.clProjectTaskContent);

            tvTask = itemView.findViewById(R.id.tvTask);

            ivConstituent = itemView.findViewById(R.id.ivConstituent);

            ivDate = itemView.findViewById(R.id.ivDate);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);

            viewStatus = itemView.findViewById(R.id.viewStatus);

            clProjectTaskContent.setOnClickListener(this);
            this.onProjectTaskListener = onProjectTaskListener;
        }

        @Override
        public void onClick(View v) {
            onProjectTaskListener.onProjectTaskClick(v, getAdapterPosition());
        }
    }

    public interface OnProjectTaskListener {
        void onProjectTaskClick(View v, int position);
    }
}
