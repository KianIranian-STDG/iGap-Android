package net.iGap.libs.emojiKeyboard.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.emojiKeyboard.emoji.view.EmojiImageView;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {
    private List<String> strings = new ArrayList<>();
    private Listener listener;

    public void setStrings(List<String> strings) {
        this.strings = strings;
        notifyDataSetChanged();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EmojiImageView imageView = new EmojiImageView(parent.getContext());
        imageView.setLayoutParams(LayoutCreator.createFrame(35, 35, Gravity.CENTER, 1.5f, 1.5f, 1.5f, 1.5f));
        return new EmojiViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        EmojiManager.EmojiDrawable emojiDrawable = EmojiManager.getInstance().getEmojiBigDrawable(strings.get(position));
        holder.imageView.setImageDrawable(emojiDrawable);

        holder.imageView.setOnClickListener(v -> listener.onClick(strings.get(position)));
        holder.imageView.setOnLongClickListener(v -> listener.onLongClick(strings.get(position)));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    class EmojiViewHolder extends RecyclerView.ViewHolder {
        private EmojiImageView imageView;

        EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (EmojiImageView) itemView;
        }
    }

    public interface Listener {
        void onClick(String emojiCode);

        boolean onLongClick(String emojiCode);
    }
}
