package net.iGap.module.customView;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerListView extends RecyclerView {

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public RecyclerListView(@NonNull Context context) {
        super(context);
    }

    public static class ItemViewHolder extends ViewHolder {

        public ItemViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && itemView.isEnabled())
                    onItemClickListener.onClick(itemView, getAdapterPosition());
            });
        }
    }

    public abstract static class ItemAdapter<VH extends ItemViewHolder> extends Adapter<VH> {
        public abstract boolean isEnable(ViewHolder holder, int viewType, int position);
    }
}
