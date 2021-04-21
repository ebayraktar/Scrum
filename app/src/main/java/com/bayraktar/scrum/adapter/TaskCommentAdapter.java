package com.bayraktar.scrum.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.TaskComment;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bayraktar.scrum.App.firebaseService;

public class TaskCommentAdapter extends RecyclerView.Adapter<TaskCommentAdapter.ViewHolder> {

    List<String> taskCommentModelList;
    OnCommentListener onCommentListener;
    Context context;

    public TaskCommentAdapter(Context context, OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
        taskCommentModelList = new ArrayList<>();
        this.context = context;
    }

    public void setTaskCommentModelList(List<String> taskCommentModelList) {
        this.taskCommentModelList = taskCommentModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task_comment, parent, false);
        return new TaskCommentAdapter.ViewHolder(view, onCommentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskCommentAdapter.ViewHolder holder, int position) {
        String key = taskCommentModelList.get(position);

        firebaseService.getComment(key, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    TaskComment item = snapshot.getValue(TaskComment.class);
                    if (item != null) {
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

                                        holder.tvConstituent.setText(user.getName());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (item.getCommentDate() != null) {
//                            holder.tvDate.setText(DateFormat.getMediumDateFormat(context).format(item.getCommentDate()));
                            holder.tvDate.setText(new SimpleDateFormat("HH:mm | dd/MM/yy", Locale.getDefault()).format(item.getCommentDate()));
                        }
                        holder.tvComment.setText(item.getComment());
                        if (item.getAttachmentURL() != null && !item.getAttachmentURL().trim().equals("")) {
                            holder.tvAttachment.setVisibility(View.VISIBLE);
                            holder.ivAttachment.setVisibility(View.VISIBLE);
                        }
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
        return taskCommentModelList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView ivConstituent;
        final TextView tvConstituent;
        final TextView tvDate;
        final TextView tvComment;
        final ImageView ivAttachment;
        final TextView tvAttachment;
        OnCommentListener onCommentListener;

        public ViewHolder(@NonNull View itemView, OnCommentListener onCommentListener) {
            super(itemView);
            ivConstituent = itemView.findViewById(R.id.ivConstituent);
            tvConstituent = itemView.findViewById(R.id.tvConstituent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
            tvAttachment = itemView.findViewById(R.id.tvAttachment);

            ivAttachment.setOnClickListener(this);
            tvAttachment.setOnClickListener(this);

            this.onCommentListener = onCommentListener;
        }

        @Override
        public void onClick(View v) {
            onCommentListener.onAttachmentClick(getAdapterPosition());
        }
    }


    public interface OnCommentListener {
        void onAttachmentClick(int position);
    }
}
