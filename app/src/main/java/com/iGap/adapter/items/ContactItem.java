package com.iGap.adapter.items;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.request.RequestFileDownload;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItem extends AbstractItem<ContactItem, ContactItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mContact;

    public ContactItem setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item;
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

        setAvatar(holder);

        // TODO set image
        // TODO set subtitle
    }

    private void setAvatar(ViewHolder holder) {

        String avatarPath = null;
        if (mContact.avatar != null) {
            avatarPath = mContact.avatar.getFile().getLocalThumbnailPath();
        }

        //Set Avatar For Chat,Group,Channel
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.image.setImageBitmap(myBitmap);
            } else {
                if (mContact.avatar.getFile() != null) {
                    onRequestDownloadAvatar();
                }
                holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), mContact.initials, mContact.color));
            }
        } else {
            if (mContact.avatar.getFile() != null) {
                onRequestDownloadAvatar();
            }
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), mContact.initials, mContact.color));
        }

    }

    public void onRequestDownloadAvatar() {

        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        RealmAttachment file = mContact.avatar.getFile();

        final String filepath = G.DIR_IMAGE_USER + "/" + file.getToken() + "_" + System.nanoTime() + "_" + selector.toString();

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, mContact.peerId).findFirst();
                realmContacts.getAvatar().getFile().setLocalThumbnailPath(filepath);
            }
        });
        realm.close();

        // I don't use offset in getting thumbnail
        String identity = file.getToken() + '*' + selector.toString() + '*' + file.getSmallThumbnail().getSize() + '*' + filepath + '*' + file.getSmallThumbnail().getSize() + '*' + "true" + '*' + mContact.peerId;

        if (!file.getToken().isEmpty()) {
            new RequestFileDownload().download(file.getToken(), 0, (int) file.getSmallThumbnail().getSize(), selector, identity);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            topLine = (View) view.findViewById(R.id.topLine);
        }
    }
}
