package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;

import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperAvatar;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

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
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mContact.peerId).findFirst();
        if (realmRegisteredInfo != null) {

            if (realmRegisteredInfo.getStatus() != null) {
                if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    String timeUser = TimeUtils.toLocal(realmRegisteredInfo.getLastSeen() * DateUtils.SECOND_IN_MILLIS, G.ROOM_LAST_MESSAGE_TIME);
                    holder.subtitle.setText(G.context.getResources().getString(R.string.last_seen_at) + " " + timeUser);
                } else {
                    holder.subtitle.setText(realmRegisteredInfo.getStatus());
                }
            }


        }
        realm.close();

        setAvatar(holder);
    }

    private void setAvatar(final ViewHolder holder) {

        HelperAvatar.getAvatar(mContact.peerId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });

       /* String avatarPath = null;
        if (mContact.avatar != null && mContact.avatar.isValid() && mContact.avatar.getFile() != null && mContact.avatar.getFile().isValid()) {
            avatarPath = mContact.avatar.getFile().getLocalThumbnailPath();
        }

        //Set Avatar For Chat,Group,Channel
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.image.setImageBitmap(myBitmap);
            } else {
                if (mContact.avatar != null && mContact.avatar.getFile() != null) {
                    onRequestDownloadThumbnail(mContact.avatar.getFile().getToken(), false);
                }
                holder.image.setImageBitmap(
                        com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                                (int) holder.image.getContext().getResources().getDimension(R.dimen.dp60),
                                mContact.initials, mContact.color));
            }
        } else {
            if (mContact.avatar != null && mContact.avatar.isValid() && mContact.avatar.getFile() != null && mContact.avatar.getFile().isValid()) {
                onRequestDownloadThumbnail(mContact.avatar.getFile().getToken(), false);
            }
            holder.image.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                    (int) holder.image.getContext().getResources().getDimension(R.dimen.dp60),
                    mContact.initials, mContact.color));
        }*/
    }

  /*  public void onRequestDownloadThumbnail(String token, boolean done) {
        final String fileName = "thumb_" + token + "_" + mContact.avatar.getFile().getName();
        if (done) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, mContact.avatar.getFile().getId()).findFirst().setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                }
            });
            realm.close();

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity =
                mContact.avatar.getFile().getToken() + '*' + selector.toString() + '*' + mContact.avatar.getFile().getSmallThumbnail()
                        .getSize() + '*' + fileName + '*' + 0;

        new RequestFileDownload().download(token, 0, (int) mContact.avatar.getFile().getSmallThumbnail().getSize(),
                selector, identity);
    }*/

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

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            topLine = (View) view.findViewById(R.id.topLine);
        }
    }
}
