package com.bayraktar.scrum.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseService;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    List<Project> projectList;
    OnProjectListener onProjectListener;
    Context context;

    public ProjectAdapter(Context context, OnProjectListener onProjectListener) {
        this.context = context;
        this.onProjectListener = onProjectListener;
        projectList = new ArrayList<>();
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_list, parent, false);
        return new ProjectAdapter.ViewHolder(view, onProjectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProjectAdapter.ViewHolder holder, int position) {
        Project item = projectList.get(position);

        Glide.with(holder.itemView)
                .load(item.getProjectImageURL())
                .fitCenter()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .into(holder.ivProject);
        holder.tvProject.setText(item.getProjectName());
        holder.tvDate.setText(DateFormat.getMediumDateFormat(context).format(item.getCreateDate()));
        String privacyMode = context.getString(R.string.privacy_public);
        if (item.isPrivacyMode()) {
            privacyMode = context.getString(R.string.privacy_private);
        }
        holder.tvPrivacyValue.setText(privacyMode);


        firebaseService.getUser(item.getConstituentID(), new ValueEventListener() {
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

//        Glide.with(holder.itemView)
//                .load(item.getConstituent().getPhotoURL())
//                .fitCenter()
//                .placeholder(R.drawable.ic_image)
//                .error(R.drawable.ic_broken_image)
//                .into(holder.ivConstituent);
//        holder.tvConstituent.setText(item.getConstituent().getName());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ConstraintLayout clProjectContent;

        final ImageView ivProject;
        final TextView tvProject;
        final TextView tvDate;
        final TextView tvPrivacyValue;

        final ImageView ivConstituent;
        final TextView tvConstituent;

        final ProjectAdapter.OnProjectListener onProjectListener;

        public ViewHolder(@NonNull View itemView, ProjectAdapter.OnProjectListener onProjectListener) {
            super(itemView);
            clProjectContent = itemView.findViewById(R.id.clProjectContent);

            ivProject = itemView.findViewById(R.id.ivProject);
            tvProject = itemView.findViewById(R.id.tvProject);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrivacyValue = itemView.findViewById(R.id.tvPrivacyValue);

            ivConstituent = itemView.findViewById(R.id.ivConstituent);
            tvConstituent = itemView.findViewById(R.id.tvConstituent);

            clProjectContent.setOnClickListener(this);
            this.onProjectListener = onProjectListener;
        }

        @Override
        public void onClick(View v) {
            onProjectListener.onProjectClick(v, getAdapterPosition());
        }
    }

    public interface OnProjectListener {
        void onProjectClick(View v, int position);
    }
}
