package com.bayraktar.scrum.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bayraktar.scrum.R;
import com.bayraktar.scrum.model.NotificationPermission;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<NotificationPermission> notificationPermissionList;
    List<Boolean> prevValues;
    private OnNotificationListener onNotificationListener;

    public NotificationAdapter(OnNotificationListener onNotificationListener) {
        notificationPermissionList = new ArrayList<>();
        prevValues = new ArrayList<>();
        this.onNotificationListener = onNotificationListener;
    }

    public void setNotificationPermissionList(List<NotificationPermission> notificationPermissionList) {
        this.notificationPermissionList = notificationPermissionList;
        prevValues = new ArrayList<>(notificationPermissionList.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(view, onNotificationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationPermission item = notificationPermissionList.get(position);
        holder.tvNotificationItem.setText(item.getNotificationPermissionName());
        holder.swNotificationItem.setChecked(item.isNotificationPermissionValue());
    }

    @Override
    public int getItemCount() {
        return notificationPermissionList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        final TextView tvNotificationItem;
        final CardView cvNotificationItem;
        final SwitchCompat swNotificationItem;
        OnNotificationListener onNotificationListener;

        public ViewHolder(@NonNull View itemView, OnNotificationListener onNotificationListener) {
            super(itemView);
            cvNotificationItem = itemView.findViewById(R.id.cvNotificationItem);
            tvNotificationItem = itemView.findViewById(R.id.tvNotificationItem);
            swNotificationItem = itemView.findViewById(R.id.swNotificationItem);
            cvNotificationItem.setOnClickListener(this);
            swNotificationItem.setOnCheckedChangeListener(this);
            this.onNotificationListener = onNotificationListener;
        }

        @Override
        public void onClick(View v) {
            swNotificationItem.setChecked(!swNotificationItem.isChecked());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.swNotificationItem) {
                onNotificationListener.onNotificationClick(getAdapterPosition(), isChecked);
            }
        }
    }

    public interface OnNotificationListener {
        void onNotificationClick(int position, boolean isChecked);
    }
}
