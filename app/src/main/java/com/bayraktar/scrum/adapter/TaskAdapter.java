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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.Task;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.bayraktar.scrum.App.firebaseService;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<Task> taskListModelList;
    OnTaskListener onTaskListener;
    Context context;

    public TaskAdapter(Context context, OnTaskListener onTaskListener) {
        this.context = context;
        this.onTaskListener = onTaskListener;
        taskListModelList = new ArrayList<>();
    }

    public void setTaskList(List<Task> taskListModelList) {
        this.taskListModelList = taskListModelList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task_list, parent, false);
        return new TaskAdapter.ViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskAdapter.ViewHolder holder, int position) {
        final Task item = taskListModelList.get(position);

        holder.tvTitle.setText(item.getTaskName());

        firebaseService.getProject(item.getProjectID(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Project project = snapshot.getValue(Project.class);
                    if (project != null) {
                        Glide.with(holder.itemView)
                                .load(project.getProjectImageURL())
                                .fitCenter()
                                .placeholder(R.drawable.ic_image)
                                .error(R.drawable.ic_broken_image)
                                .into(holder.ivProject);
                        holder.tvProject.setText(project.getProjectName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


//        holder.tvStartDate.setText(DateFormat.getMediumDateFormat(context).format(item.getStartDate()));
        holder.tvStartDate.setText(new SimpleDateFormat("EEE dd MMM y", Locale.getDefault()).format(item.getStartDate()));

//        holder.tvDeadline.setText(DateFormat.getMediumDateFormat(context).format(item.getDeadline()));
        holder.tvDeadline.setText(new SimpleDateFormat("EEE dd MMM y", Locale.getDefault()).format(item.getDeadline()));
        Date now = Calendar.getInstance().getTime();
        if (item.getDeadline().before(now)) {
            holder.ivDate.setColorFilter(ContextCompat.getColor(context, R.color.colorRed), PorterDuff.Mode.SRC_IN);
            holder.tvStartDate.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.tvDeadline.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        }

        int statusColor;
        int statusText;
        switch (item.getTaskStatusID()) {
            case 1:
                statusColor = R.color.colorGreen;
                statusText = R.string.menu_active;
                break;
            case 2:
                statusColor = R.color.colorDarkGrey;
                statusText = R.string.menu_completed;
                break;
            case 3:
                statusColor = R.color.colorBlue;
                statusText = R.string.menu_suspended;
                break;
            default:
                statusColor = R.color.colorOrange;
                statusText = R.string.menu_pending;
                break;
        }
        holder.cvStatus.setCardBackgroundColor(context.getResources().getColor(statusColor));
        holder.cvTitle.setCardBackgroundColor(context.getResources().getColor(statusColor));
        holder.viewStatus.setBackgroundResource(statusColor);
        holder.tvStatus.setText(statusText);
    }

    @Override
    public int getItemCount() {
        return taskListModelList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ConstraintLayout clTaskContent;
        final TextView tvTitle;
        final CardView cvTitle;

        final ImageView ivProject;
        final TextView tvProject;
        final ImageView ivConstituent;
        final ImageView ivDate;
        final TextView tvStartDate;
        final TextView tvDeadline;

        final View viewStatus;
        final TextView tvStatus;
        final CardView cvStatus;

        final OnTaskListener onTaskListener;

        public ViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            clTaskContent = itemView.findViewById(R.id.clTaskContent);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            cvTitle = itemView.findViewById(R.id.cvTitle);

            ivProject = itemView.findViewById(R.id.ivProject);
            tvProject = itemView.findViewById(R.id.tvProject);

            ivConstituent = itemView.findViewById(R.id.ivConstituent);

            ivDate = itemView.findViewById(R.id.ivDate);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);

            viewStatus = itemView.findViewById(R.id.viewStatus);
            cvStatus = itemView.findViewById(R.id.cvStatus);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            clTaskContent.setOnClickListener(this);
            this.onTaskListener = onTaskListener;
        }

        @Override
        public void onClick(View v) {
            onTaskListener.onTaskClick(v, getAdapterPosition());
        }
    }

    public interface OnTaskListener {
        void onTaskClick(View v, int position);
    }
}
