package net.iGap.fragments.giftStickers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.view.StickerView;

import java.util.List;

public class GiftStickerPackageListAdapter extends RecyclerView.Adapter<GiftStickerPackageListAdapter.ViewHolder> {

    private List<StructIGStickerGroup> items;
    private Delegate delegate;

    public GiftStickerPackageListAdapter(Delegate callback) {
        this.delegate = callback;
    }

    public List<StructIGStickerGroup> getItems() {
        return items;
    }

    public void setItems(List<StructIGStickerGroup> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_sticker_package_item_view, parent, false));
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
        private AppCompatTextView giftStickerPackageTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stickerView = itemView.findViewById(R.id.stickerView);
            giftStickerPackageTitle = itemView.findViewById(R.id.giftStickerPackageTitle);
        }

        public void bindView(StructIGStickerGroup stickerGroup) {
            itemView.setOnClickListener(v -> delegate.onClick(stickerGroup));
            stickerView.loadStickerGroup(stickerGroup);
            giftStickerPackageTitle.setText(stickerGroup.getName());
        }
    }

    public interface Delegate {
        void onClick(StructIGStickerGroup stickerGroup);
    }
}
