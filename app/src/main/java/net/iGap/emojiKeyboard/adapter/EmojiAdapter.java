package net.iGap.emojiKeyboard.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {
    List<String> imagesUri = new ArrayList<>();

    public void setImages(List<String> imagesUri) {
        this.imagesUri = imagesUri;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AppCompatImageView imageView = new AppCompatImageView(parent.getContext());
        imageView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
        return new EmojiViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return imagesUri.size();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView imageView;

        public EmojiViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (AppCompatImageView) itemView;
        }

    }
}
