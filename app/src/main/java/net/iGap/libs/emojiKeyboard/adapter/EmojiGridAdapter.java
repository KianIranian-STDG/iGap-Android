package net.iGap.libs.emojiKeyboard.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.libs.emojiKeyboard.View.EmojiGridView;
import net.iGap.libs.emojiKeyboard.struct.StructIGEmojiGroup;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

public class EmojiGridAdapter extends RecyclerView.Adapter<EmojiGridAdapter.EmojiGridViewHolder> implements EmojiAdapter.Listener {
    private List<StructIGEmojiGroup> structIGEmojiGroups = new ArrayList<>();
    private EmojiAdapter.Listener listener;

    public void setListener(EmojiAdapter.Listener listener) {
        this.listener = listener;
    }

    public void setStructIGEmojiGroups(List<StructIGEmojiGroup> structIGEmojiGroups) {
        this.structIGEmojiGroups = structIGEmojiGroups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmojiGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EmojiGridView emojiGridView = new EmojiGridView(parent.getContext());
        emojiGridView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        emojiGridView.setListener(this);
        return new EmojiGridViewHolder(emojiGridView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiGridViewHolder holder, int position) {
        holder.emojiGridView.setEmojiGroup(structIGEmojiGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return structIGEmojiGroups.size();
    }

    @Override
    public void onClick(String emojiCode) {
        listener.onClick(emojiCode);
    }

    @Override
    public boolean onLongClick(String emojiCode) {
        return listener.onLongClick(emojiCode);
    }

    public class EmojiGridViewHolder extends RecyclerView.ViewHolder {
        private EmojiGridView emojiGridView;

        public EmojiGridViewHolder(@NonNull View itemView) {
            super(itemView);
            emojiGridView = (EmojiGridView) itemView;
        }
    }


}
