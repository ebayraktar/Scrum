package com.bayraktar.scrum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.Application;
import com.bayraktar.scrum.model.Invitation;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseService;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    List<Application> applicationList;
    private ApplicationAdapter.OnApplicationListener onApplicationListener;

    public ApplicationAdapter(ApplicationAdapter.OnApplicationListener onApplicationListener) {
        applicationList = new ArrayList<>();
        this.onApplicationListener = onApplicationListener;
    }

    public void setApplicationPermissionList(List<Application> applicationList) {
        this.applicationList = applicationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ApplicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_applications, parent, false);
        return new ApplicationAdapter.ViewHolder(view, onApplicationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ApplicationAdapter.ViewHolder holder, int position) {
        Application item = applicationList.get(position);
        if (item.getInvitationStatus() == 0) {
            firebaseService.getUser(item.getAppliedUserID(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            holder.tvUsername.setText(user.getName());
                            Glide.with(holder.itemView)
                                    .load(user.getPhotoURL())
                                    .placeholder(R.drawable.ic_image)
                                    .fitCenter()
                                    .error(R.drawable.ic_broken_image)
                                    .into(holder.ivUser);
                            holder.tvEmail.setText(user.getEmail());
                            holder.tvJobTitle.setText(user.getJobTitle());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.cvUserProfile.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CardView cvUserProfile;
        final ImageView ivUser;
        final TextView tvUsername;
        final TextView tvEmail;
        final TextView tvJobTitle;
        final ImageView ivAccept;
        final ImageView ivReject;
        OnApplicationListener onApplicationListener;

        public ViewHolder(@NonNull View itemView, OnApplicationListener onApplicationListener) {
            super(itemView);
            cvUserProfile = itemView.findViewById(R.id.cvUserProfile);
            ivUser = itemView.findViewById(R.id.ivUser);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            ivAccept = itemView.findViewById(R.id.ivAccept);
            ivReject = itemView.findViewById(R.id.ivReject);

            cvUserProfile.setOnClickListener(this);
            ivAccept.setOnClickListener(this);
            ivReject.setOnClickListener(this);
            this.onApplicationListener = onApplicationListener;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cvUserProfile:
                    onApplicationListener.onApplicationClick(getAdapterPosition());
                    break;
                case R.id.ivAccept:
                    onApplicationListener.onAnswerClick(getAdapterPosition(), true);
                    break;
                case R.id.ivReject:
                    onApplicationListener.onAnswerClick(getAdapterPosition(), false);
                    break;
            }
        }
    }

    public interface OnApplicationListener {
        void onApplicationClick(int position);

        void onAnswerClick(int position, boolean isAccept);
    }
}
