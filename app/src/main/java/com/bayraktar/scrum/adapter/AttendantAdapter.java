package com.bayraktar.scrum.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.User;
import com.bumptech.glide.Glide;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bayraktar.scrum.App.firebaseService;

public class AttendantAdapter extends RecyclerView.Adapter<AttendantAdapter.ViewHolder> {

    List<String> userIDList;
    List<Boolean> selectedUsers;
    List<User> users;
    private AttendantAdapter.OnAttendantListener onAttendantListener;

    public AttendantAdapter(AttendantAdapter.OnAttendantListener onAttendantListener) {
        users = new ArrayList<>();
        userIDList = new ArrayList<>();
        selectedUsers = new ArrayList<>();
        this.onAttendantListener = onAttendantListener;
    }

    public void setUserIDList(List<String> userIDList) {
        this.userIDList = userIDList;
        selectedUsers = new ArrayList<>();
        for (int i = 0; i < userIDList.size(); i++) {
            selectedUsers.add(true);
        }

        notifyDataSetChanged();
    }

    public void setSelectedUsers(List<Boolean> selectedUsers) {
        this.selectedUsers = selectedUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttendantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_attendants, parent, false);
        return new AttendantAdapter.ViewHolder(view, onAttendantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final AttendantAdapter.ViewHolder holder, final int position) {
        String item = userIDList.get(position);
        boolean selected = selectedUsers.get(position);
        holder.cbUser.setChecked(selected);
        firebaseService.getUser(item, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        users.add(position, user);
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
    }

    @Override
    public int getItemCount() {
        return userIDList.size();
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser(int position) {
        return users.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        final CardView cvUserProfile;
        final ImageView ivUser;
        final TextView tvUsername;
        final TextView tvEmail;
        final TextView tvJobTitle;
        final MaterialCheckBox cbUser;
        AttendantAdapter.OnAttendantListener onAttendantListener;

        public ViewHolder(@NonNull View itemView, AttendantAdapter.OnAttendantListener onAttendantListener) {
            super(itemView);
            cvUserProfile = itemView.findViewById(R.id.cvUserProfile);
            ivUser = itemView.findViewById(R.id.ivUser);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);

            cbUser = itemView.findViewById(R.id.cbUser);

            cbUser.setOnCheckedChangeListener(this);
            cvUserProfile.setOnClickListener(this);
            this.onAttendantListener = onAttendantListener;
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onAttendantListener.onCheckedListener(getAdapterPosition(), isChecked);
        }

        @Override
        public void onClick(View v) {
            cbUser.setChecked(!cbUser.isChecked());
        }
    }

    public interface OnAttendantListener {
        void onCheckedListener(int position, boolean isChecked);
    }
}