package com.example.bustracking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {

    private Context context;
    private List<BusModel> busList;

    public BusAdapter(Context context, List<BusModel> busList) {
        this.context = context;
        this.busList = busList;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bus_list_item, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        BusModel bus = busList.get(position);
        holder.txtBusNumber.setText("Bus No: " + bus.getBusNumber());
        holder.txtDriverName.setText("Driver: " + bus.getDriverName());

        holder.btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackBusActivity.class);
            intent.putExtra("driverId", bus.getDriverId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public void updateList(List<BusModel> newList) {
        this.busList = newList;
        notifyDataSetChanged();
    }

    public static class BusViewHolder extends RecyclerView.ViewHolder {
        TextView txtBusNumber, txtDriverName;
        Button btnTrack;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBusNumber = itemView.findViewById(R.id.txtBusNumber);
            txtDriverName = itemView.findViewById(R.id.txtDriverName);
            btnTrack = itemView.findViewById(R.id.btnTrack);
        }
    }
}
