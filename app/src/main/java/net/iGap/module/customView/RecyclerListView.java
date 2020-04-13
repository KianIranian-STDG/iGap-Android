package net.iGap.module.customView;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerListView extends RecyclerView {
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public RecyclerListView(@NonNull Context context) {
        super(context);
    }

    public static class ItemViewHolder extends ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public abstract static class ItemAdapter extends Adapter {

    }
}
