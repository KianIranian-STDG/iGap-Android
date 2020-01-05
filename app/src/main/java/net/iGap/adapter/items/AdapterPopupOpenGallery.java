package net.iGap.adapter.items;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterPopupOpenGallery extends AbstractItem<AdapterPopupOpenGallery, AdapterPopupOpenGallery.ViewHolder> {

    public OpenGalleryPopupListener listener;

    public AdapterPopupOpenGallery(OpenGalleryPopupListener listener) {
        this.listener = listener;
    }

    @Override
    public int getType() {
        return R.id.rootGallery;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.adpter_more_gallery;
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

            View root = view.findViewById(R.id.card_open_gallery);
            root.setOnClickListener(v -> listener.openGallery());
        }
    }

    public interface OpenGalleryPopupListener {
        void openGallery();
    }
}
