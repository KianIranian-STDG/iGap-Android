package net.iGap.fragments.giftStickers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.view.StickerView;

import java.util.List;

public class GiftStickerPackageListAdapter extends RecyclerView.Adapter<GiftStickerPackageListAdapter.ViewHolder> {

    private List<String> items;
    private BottomSheetItemClickCallback callback;

    public GiftStickerPackageListAdapter(BottomSheetItemClickCallback callback) {
        this.callback = callback;
    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
        Log.wtf(this.getClass().getName(), "setItems: " + getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.wtf(this.getClass().getName(), "onCreateViewHolder");
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_sticker_package_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.wtf(this.getClass().getName(), "onBindViewHolder: " + position);
        holder.itemView.setOnClickListener(v -> callback.onClick(holder.getAdapterPosition()));
        holder.giftStickerPackageTitle.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        int tmp = items != null ? items.size() : 0;
        Log.wtf(this.getClass().getName(), "getItemCount: " + tmp);
        return tmp;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private StickerView stickerView;
        private AppCompatTextView giftStickerPackageTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stickerView = itemView.findViewById(R.id.stickerView);
            giftStickerPackageTitle = itemView.findViewById(R.id.giftStickerPackageTitle);
        }
    }
}
