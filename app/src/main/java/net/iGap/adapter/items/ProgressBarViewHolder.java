package net.iGap.adapter.items;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

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
