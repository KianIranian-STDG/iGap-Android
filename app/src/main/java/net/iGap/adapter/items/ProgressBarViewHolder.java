package net.iGap.adapter.items;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;

public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
    private ProgressBar progress;

    public ProgressBarViewHolder(@NonNull View itemView) {
        super(itemView);
        progress = itemView.findViewById(R.id.progress);
    }

    public void bindView() {

    }
}
