package com.iGap.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.hanks.library.AnimateCheckBox;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.module.StructBottomSheet;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

public class AdapterBottomSheet extends AbstractItem<AdapterBottomSheet, AdapterBottomSheet.ViewHolder> {

    public StructBottomSheet mList;
    public boolean isChecked = false;

    public StructBottomSheet getItem() {
        return mList;
    }

    public AdapterBottomSheet(StructBottomSheet item) {
        this.mList = item;
    }

    public void setItem(StructBottomSheet item) {
        this.mList = item;
    }

    //The unique ID for this type of mList
    @Override public int getType() {
        return R.id.rcv_root_bottom_sheet;
    }

    //The layout to be used for this type of mList
    @Override public int getLayoutRes() {
        return R.layout.adapter_bottom_sheet;
    }

    //The logic to bind your data to the view

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        //DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.icon) // resource or drawable
        //    .showImageForEmptyUri(R.mipmap.icon) // resource or drawable
        //    .showImageOnFail(R.mipmap.icon) // resource or drawable
        //    .resetViewBeforeLoading(false)  // default
        //    .delayBeforeLoading(1000).cacheInMemory(false) // default
        //    .cacheOnDisk(false) // default
        //    .considerExifParams(false) // default
        //    .imageScaleType(ImageScaleType.IN_SAMPLE_INT) // default
        //    .bitmapConfig(Bitmap.Config.ARGB_8888) // default
        //    .handler(new Handler()) // default
        //    .build();
        //
        //ImageSize targetSize = new ImageSize(100, 130); // result Bitmap will be fit to this size
        //imageLoader.loadImage("file://" + mList.getPath(), targetSize, options, new SimpleImageLoadingListener() {
        //    @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        //        holder.imgSrc.setImageBitmap(loadedImage);
        //    }
        //});
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        imageLoader.displayImage("file://" + mList.getPath(), holder.imgSrc);

        if (mList.isSelected) {
            holder.checkBoxSelect.setChecked(false);
        } else {
            holder.checkBoxSelect.setChecked(true);
        }

        holder.checkBoxSelect.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (holder.checkBoxSelect.isChecked()) {
                    holder.checkBoxSelect.setChecked(false);
                    ActivityChat.onPathAdapterBottomSheet.path(mList.getPath(), false);
                } else {
                    holder.checkBoxSelect.setChecked(true);
                    ActivityChat.onPathAdapterBottomSheet.path(mList.getPath(), true);
                }
            }
        });

        holder.cr.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (holder.checkBoxSelect.isChecked()) {
                    holder.checkBoxSelect.setChecked(false);
                    ActivityChat.onPathAdapterBottomSheet.path(mList.getPath(), false);
                } else {
                    holder.checkBoxSelect.setChecked(true);
                    ActivityChat.onPathAdapterBottomSheet.path(mList.getPath(), true);
                }
            }
        });
    }

    //The viewHolder used for this mList. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public static ImageLoader imageLoader;
        private CardView cr;
        private ImageView imgSrc;
        protected AnimateCheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);
            imageLoader = ImageLoader.getInstance();
            cr = (CardView) view.findViewById(R.id.card_view);
            imgSrc = (ImageView) view.findViewById(R.id.img_gallery);
            checkBoxSelect = (AnimateCheckBox) view.findViewById(R.id.cig_checkBox_select_user);
        }
    }

    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your mList.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     */
    @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }
}