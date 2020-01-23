package com.example.xbeeandroidapp.util;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xbeeandroidapp.activity.DeviceParametersActivity;
import com.example.xbeeandroidapp.R;
import com.example.xbeeandroidapp.model.XbeeDevice;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<XbeeDevice> devices;

    public RecyclerViewAdapter(List<XbeeDevice> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.xbee_device, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        XbeeDevice device = devices.get(position);
        CardView cardView = holder.cardView;
        TextView nodeId = cardView.findViewById(R.id.nodeId);
        TextView xbee64bitAddress = cardView.findViewById(R.id.mac);
        TextView mac16bit = cardView.findViewById(R.id.mac_16bit);
        TextView role = cardView.findViewById(R.id.role);
        ImageView iv = cardView.findViewById(R.id.xbee_icon);

        nodeId.setText(device.getNodeId());
        xbee64bitAddress.setText(device.getxBee64BitAddress());
        mac16bit.setText(device.getxBee16BitAddress());
        role.setText(device.getRole());
        Drawable icon;
        switch (device.getRole()) {
            case "ZigBee Coordinator API":
                icon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.xbee_64_coordinator);
                iv.setImageDrawable(icon);
                break;
            case "ZigBee Router API":
                icon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.xbee_64_router);
                iv.setImageDrawable(icon);
                break;
            case "ZigBee End Device API":
                icon = ContextCompat.getDrawable(cardView.getContext(), R.drawable.xbee_64_end_device);
                iv.setImageDrawable(icon);
        }
        cardView.setOnClickListener((v) -> {
            Intent intent = new Intent(cardView.getContext(), DeviceParametersActivity.class);
            intent.putExtra("xbee64bitAddress", device.getxBee64BitAddress());
            intent.putExtra("mac16bit", device.getxBee16BitAddress());
            intent.putExtra("role", device.getRole());
            cardView.getContext().startActivity(intent);
        });
    }

    public List<XbeeDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<XbeeDevice> devices) {
        this.devices = devices;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
