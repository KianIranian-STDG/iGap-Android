package net.iGap.fragments.giftStickers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.view.StickerView;

import java.util.List;

public class MyStickerListAdapter extends RecyclerView.Adapter<MyStickerListAdapter.ViewHolder> {

    private List<String> items;

    public MyStickerListAdapter(){

    }

    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_gift_sticker_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private StickerView stickerView;
        private AppCompatTextView giftStickerTitle;
        private AppCompatTextView giftStickerPrice;
        private AppCompatTextView giftStickerStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stickerView = itemView.findViewById(R.id.stickerView);
            giftStickerTitle = itemView.findViewById(R.id.giftStickerTitle);
            giftStickerPrice = itemView.findViewById(R.id.giftStickerPrice);
            giftStickerStatus = itemView.findViewById(R.id.giftStickerStatusView);
        }
    }
}
