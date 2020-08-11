package net.iGap.fragments.emoji.add;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.customView.StickerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {
    private List<StructIGSticker> igStickers = new ArrayList<>();
    private AddStickerDialogListener listener;
    private boolean needToShowEmoji;
    private String TAG = "abbasiStickerAdapter";

    public void setListener(AddStickerDialogListener listener) {
        this.listener = listener;
    }

    public StickerAdapter(boolean needToShowEmoji) {
        this.needToShowEmoji = needToShowEmoji;
    }

    @NotNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        StickerView stickerView = new StickerView(parent.getContext());
        stickerView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 75, Gravity.CENTER, 1, 0, 1, 2));
        return new StickerViewHolder(stickerView);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        holder.bindView(igStickers.get(position));

    }

    @Override
    public int getItemCount() {
        return igStickers.size();
    }

    public void setIgStickers(List<StructIGSticker> igStickers) {
        this.igStickers = igStickers;
        notifyDataSetChanged();
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder {
        StickerView normalStickerCell;

        StickerViewHolder(View itemView) {
            super(itemView);
            normalStickerCell = (StickerView) itemView;
        }

        public void bindView(StructIGSticker structIGSticker) {
            normalStickerCell.loadSticker(structIGSticker, needToShowEmoji);

            normalStickerCell.setOnClickListener(v -> listener.onStickerClick(structIGSticker));

            normalStickerCell.setOnLongClickListener(v -> {
                listener.onStickerLongClick(structIGSticker);
                return false;
            });
        }
    }

    public interface AddStickerDialogListener {
        void onStickerClick(StructIGSticker structIGSticker);

        void onStickerLongClick(StructIGSticker structIGSticker);
    }
}

