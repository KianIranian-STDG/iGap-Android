package net.iGap.emojiKeyboard.sticker;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.fragments.emoji.add.StickerAdapter;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StickerGroupAdapter extends RecyclerView.Adapter<StickerGroupAdapter.StickerGroupViewHolder> implements StickerAdapter.AddStickerDialogListener {

    private List<StructIGStickerGroup> groups = new ArrayList<>();
    private StickerAdapter.AddStickerDialogListener listener;
    private HashMap<Object, Integer> groupPositionHashMap = new HashMap<>();
    private int totalItems;


    public void setListener(StickerAdapter.AddStickerDialogListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<StructIGStickerGroup> groups) {
        this.groups = groups;
        notifyDataChanged();
    }


    public int getPositionForGroup(Object pack) {
        Integer pos = groupPositionHashMap.get(pack);
        if (pos == null) {
            return -1;
        }
        return pos;
    }

    public void addGroups(StructIGStickerGroup group) {
        this.groups.add(group);
        notifyDataChanged();
    }


    @NonNull
    @Override
    public StickerGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StickerGridView stickerGridView = new StickerGridView(parent.getContext());
        stickerGridView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 4, 0, 0));
        stickerGridView.setListener(this);
        return new StickerGroupViewHolder(stickerGridView);
    }

    public List<StructIGStickerGroup> getGroups() {
        return groups;
    }

    public void notifyDataChanged() {
        groupPositionHashMap.clear();

        totalItems = 0;

        for (int a = -1; a < groups.size(); a++) {
            if (a == -1) {
                groupPositionHashMap.put("recent", totalItems);
            } else {
                groupPositionHashMap.put(groups.get(a), totalItems);
            }
            totalItems++;
        }

        super.notifyDataSetChanged();
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
