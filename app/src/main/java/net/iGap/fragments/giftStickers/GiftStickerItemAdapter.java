package net.iGap.fragments.giftStickers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.view.StickerView;

import java.util.List;

public class GiftStickerItemAdapter extends RecyclerView.Adapter<GiftStickerItemAdapter.ViewHolder> {

    private List<String> items;
    private BottomSheetItemClickCallback callback;

    public GiftStickerItemAdapter(BottomSheetItemClickCallback callback){
        this.callback = callback;
    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_sticker_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> callback.onClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private StickerView stickerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stickerView = itemView.findViewById(R.id.stickerItem);
        }
    }
}
