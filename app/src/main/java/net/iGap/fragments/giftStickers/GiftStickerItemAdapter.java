package net.iGap.fragments.giftStickers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.customView.StickerView;

import java.util.List;

public class GiftStickerItemAdapter extends RecyclerView.Adapter<GiftStickerItemAdapter.ViewHolder> {

    private List<StructIGSticker> items;
    private Delegate delegate;

    public GiftStickerItemAdapter(Delegate delegate, List<StructIGSticker> items) {
        this.delegate = delegate;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_sticker_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(items.get(position));
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

        public void bindView(StructIGSticker structIGSticker) {
            stickerView.loadSticker(structIGSticker);
            itemView.setOnClickListener(v -> delegate.onItemClick(structIGSticker));
        }
    }

    public interface Delegate {
        void onItemClick(StructIGSticker structIGSticker);
    }
}
