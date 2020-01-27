package net.iGap.fragments.giftStickers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.helper.HelperCalander;
import net.iGap.view.StickerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyStickerListAdapter extends RecyclerView.Adapter<MyStickerListAdapter.ViewHolder> {

    private List<StructIGGiftSticker> items = new ArrayList<>();
    private Delegate delegate;

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setItems(List<StructIGGiftSticker> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_gift_sticker_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private StickerView stickerView;
        private AppCompatTextView giftStickerTitle;
        private AppCompatTextView giftStickerPrice;
        private AppCompatTextView giftStickerStatus;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerView = itemView.findViewById(R.id.stickerView);
            giftStickerTitle = itemView.findViewById(R.id.giftStickerTitle);
            giftStickerPrice = itemView.findViewById(R.id.giftStickerPrice);
            giftStickerStatus = itemView.findViewById(R.id.giftStickerStatusView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bindView(StructIGGiftSticker giftSticker) {
            stickerView.loadSticker(giftSticker.getStructIGSticker());
            giftStickerTitle.setText(giftSticker.getRrn());

            DecimalFormat df = new DecimalFormat("#,###");
            giftStickerPrice.setText((HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(giftSticker.getStructIGSticker().getGiftAmount()))) : df.format(Double.valueOf(giftSticker.getStructIGSticker().getGiftAmount()))) + " " + itemView.getContext().getResources().getString(R.string.rial));

            giftStickerStatus.setText(giftSticker.getStatus());

            progressBar.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> delegate.onClick(giftSticker, visibility -> progressBar.setVisibility(visibility)));
        }
    }

    public interface Delegate {
        void onClick(StructIGGiftSticker giftSticker, ProgressDelegate progressDelegate);
    }

    public interface ProgressDelegate {
        void onView(int visibility);
    }
}
