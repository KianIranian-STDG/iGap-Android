package com.iGap.adapter.items;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;

public class ContatItemGroupProfile
        extends AbstractItem<ContatItemGroupProfile, ContatItemGroupProfile.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mContact;

    public ContatItemGroupProfile setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item_group_profile;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        holder.title.setText(mContact.displayName);

        holder.subtitle.setText(R.string.last_seen_recently);

        holder.image.setImageBitmap(setAvatar(holder.image.getLayoutParams().width));
    }

    private Bitmap setAvatar(int size) {

        String path = null;
        Bitmap bitmap = null;

        if (mContact.avatar.getFile() != null) {
            if (mContact.avatar.getFile().getLocalFilePath() != null) {
                path = mContact.avatar.getFile().getLocalFilePath();
            } else {
                path = mContact.avatar.getFile().getLocalThumbnailPath();
            }
        }

        File imgFile = null;

        if (path != null) {
            imgFile = new File(path);
        }

        if (imgFile != null) {
            if (imgFile.exists()) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }

        String name = HelperImageBackColor.getFirstAlphabetName(mContact.displayName);
        bitmap = HelperImageBackColor.drawAlphabetOnPicture(size, name,
                HelperImageBackColor.getColor(name));

        return bitmap;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;
        protected TextView txtNomberOfSharedMedia;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.cigp_imv_contact_avatar);
            title = (CustomTextViewMedium) view.findViewById(R.id.cigp_txt_contact_name);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.cigp_txt_contact_lastseen);
            topLine = view.findViewById(R.id.cigp_view_topLine);
            txtNomberOfSharedMedia =
                    (TextView) view.findViewById(R.id.cigp_txt_nomber_of_shared_media);
        }
    }
}
