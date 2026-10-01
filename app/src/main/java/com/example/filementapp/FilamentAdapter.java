package com.example.filementapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FilamentAdapter extends RecyclerView.Adapter<FilamentAdapter.FilamentViewHolder> {

    public interface OnItemClickListener {
        void onClick(Filament filament);
        void onLongClick(Filament filament);
    }

    private List<Filament> filaments = new ArrayList<>();
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilamentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new FilamentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilamentViewHolder holder, int position) {
        Filament f = filaments.get(position);
        holder.titleText.setText(f.getBrand() + " · " + f.getMaterial());
        String sub = f.getColor();
        if (f.getPurchaseDate() != null && !f.getPurchaseDate().isEmpty()) {
            sub += "  ·  købt " + f.getPurchaseDate();
        }
        holder.subtitleText.setText(sub);
        holder.remainingBar.setProgress(f.getRemainingPercent());
        holder.gramsText.setText(f.getRemainingWeightG() + " / " + f.getTotalWeightG()
                + " g  (" + f.getRemainingPercent() + "%)");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(f);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(f);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return filaments.size();
    }

    public void setFilaments(List<Filament> filaments) {
        this.filaments = filaments;
        notifyDataSetChanged();
    }

    static class FilamentViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView subtitleText;
        ProgressBar remainingBar;
        TextView gramsText;

        FilamentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            subtitleText = itemView.findViewById(R.id.subtitleText);
            remainingBar = itemView.findViewById(R.id.remainingBar);
            gramsText = itemView.findViewById(R.id.gramsText);
        }
    }
}