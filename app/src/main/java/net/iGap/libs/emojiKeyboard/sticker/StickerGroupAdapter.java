package net.iGap.libs.emojiKeyboard.sticker;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.libs.emojiKeyboard.View.StickerGridView;
import net.iGap.fragments.emoji.add.StickerAdapter;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

public class StickerGroupAdapter extends RecyclerView.Adapter<StickerGroupAdapter.StickerGroupViewHolder> implements StickerAdapter.AddStickerDialogListener {

    private List<StructIGStickerGroup> groups = new ArrayList<>();
    private StickerAdapter.AddStickerDialogListener listener;

    public void setListener(StickerAdapter.AddStickerDialogListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<StructIGStickerGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StickerGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StickerGridView stickerGridView = new StickerGridView(parent.getContext());
        stickerGridView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 4, 0, 0));
        stickerGridView.setListener(this);
        return new StickerGroupViewHolder(stickerGridView);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerGroupViewHolder holder, int position) {
        holder.bindView(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public void onStickerClick(StructIGSticker structIGSticker) {
        if (listener != null)
            listener.onStickerClick(structIGSticker);
    }

    @Override
    public void onStickerLongClick(StructIGSticker structIGSticker) {
        if (listener != null)
            listener.onStickerLongClick(structIGSticker);
    }

    class StickerGroupViewHolder extends RecyclerView.ViewHolder {
        StickerGridView stickerGridView;

        StickerGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerGridView = (StickerGridView) itemView;
        }

        private void bindView(StructIGStickerGroup group) {
            stickerGridView.setStickerGroup(group);
        }
    }
}
