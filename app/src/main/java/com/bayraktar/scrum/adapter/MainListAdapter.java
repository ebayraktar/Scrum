package com.bayraktar.scrum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Project;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bayraktar.scrum.App.currentUser;
import static com.bayraktar.scrum.App.firebaseService;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {

    List<Project> projectList;
    OnMainListListener onMainListListener;
    Context context;
    final String myFormat = "dd/MM/yyyy"; //In which you need put here

    public MainListAdapter(Context context, OnMainListListener onMainListListener) {
        this.context = context;
        this.onMainListListener = onMainListListener;
        projectList = new ArrayList<>();
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main, parent, false);
        return new MainListAdapter.ViewHolder(view, onMainListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainListAdapter.ViewHolder holder, int position) {
        final Project item = projectList.get(position);
        if (currentUser == null) {
            return;
        }
        Glide.with(holder.itemView)
                .load(item.getProjectImageURL())
                .fitCenter()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_broken_image)
                .into(holder.ivProject);
        holder.tvProject.setText(item.getProjectName());

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        holder.tvDate.setText(sdf.format(item.getCreateDate().getTime()));

        if (item.getConstituentID().equals(currentUser.getUserID())) {
            Glide.with(holder.itemView)
                    .load(currentUser.getPhotoURL())
                    .fitCenter()
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.ivConstituent);
            holder.tvConstituent.setText(currentUser.getName());
        } else {
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
        }
        if (item.getConstituentID().equals(currentUser.getUserID())) {
            holder.tvApplication.setText("Proje Size Ait");
        } else if (item.getMembers() != null && item.getMembers().containsKey(currentUser.getUserID())) {
            holder.tvApplication.setText("Katıldınız");
        } else if (item.getInvitations() != null && item.getInvitations().containsKey(currentUser.getUserID())) {
            holder.tvApplication.setText("Davet Edildiniz");
        } else if (item.getApplications() != null && item.getApplications().containsKey(currentUser.getUserID())) {
            holder.tvApplication.setText("Başvurdunuz");
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView ivProject;
        final TextView tvProject;
        final TextView tvDate;

        final ImageView ivConstituent;
        final TextView tvConstituent;

        final CardView cvInvitation;
        final TextView tvApplication;

        final CardView cvDetail;

        final OnMainListListener onMainListListener;

        public ViewHolder(@NonNull View itemView, OnMainListListener onMainListListener) {
            super(itemView);

            ivProject = itemView.findViewById(R.id.ivProject);
            tvProject = itemView.findViewById(R.id.tvProject);
            tvDate = itemView.findViewById(R.id.tvDate);

            ivConstituent = itemView.findViewById(R.id.ivConstituent);
            tvConstituent = itemView.findViewById(R.id.tvConstituent);

            cvInvitation = itemView.findViewById(R.id.cvInvitation);
            tvApplication = itemView.findViewById(R.id.tvApplication);

            cvDetail = itemView.findViewById(R.id.cvDetail);

            cvInvitation.setOnClickListener(this);
            cvDetail.setOnClickListener(this);
            this.onMainListListener = onMainListListener;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cvInvitation:
                    onMainListListener.onMainListInvitationClick(v, getAdapterPosition());
                    break;
                case R.id.cvDetail:
                    onMainListListener.onMainListDetailClick(v, getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnMainListListener {
        void onMainListInvitationClick(View v, int position);

        void onMainListDetailClick(View v, int position);
    }
}
