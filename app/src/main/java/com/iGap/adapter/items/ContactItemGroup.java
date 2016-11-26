package com.iGap.adapter.items;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;

import com.hanks.library.AnimateCheckBox;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItemGroup extends AbstractItem<ContactItemGroup, ContactItemGroup.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mContact;

    public ContactItemGroup setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item_group;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.checkBoxSelect.setChecked(true);

        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        if (mContact.isSelected) {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        } else {
            holder.checkBoxSelect.setVisibility(View.INVISIBLE);
        }

        holder.title.setText(mContact.displayName);


        if (mContact.status != null) {
            if (mContact.status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String timeUser = TimeUtils.toLocal(mContact.lastSeen * DateUtils.SECOND_IN_MILLIS, G.ROOM_LAST_MESSAGE_TIME);
                holder.subtitle.setText(G.context.getResources().getString(R.string.last_seen_at) + " " + timeUser);
            } else {
                holder.subtitle.setText(mContact.status);
            }
        }

        holder.image.setImageBitmap(setAvatar(holder.image.getLayoutParams().width));
    }

    private Bitmap setAvatar(int size) {

        String path = null;
        Bitmap bitmap = null;

        if (mContact.avatar != null && mContact.avatar.getFile() != null) {
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
        bitmap = HelperImageBackColor.drawAlphabetOnPicture(size, name, HelperImageBackColor.getColor(name));

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
        protected AnimateCheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            topLine = (View) view.findViewById(R.id.topLine);
            checkBoxSelect = (AnimateCheckBox) view.findViewById(R.id.cig_checkBox_select_user);
        }
    }
}
