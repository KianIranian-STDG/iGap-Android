/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.List;

import io.realm.Realm;

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItem extends AbstractItem<ContactItem, ContactItem.ViewHolder> {
    public StructContactInfo mContact;
    private AvatarHandler avatarHandler;

    public ContactItem(AvatarHandler avatarHandler) {
        this.avatarHandler = avatarHandler;
    }

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

        try (Realm realm = Realm.getDefaultInstance()) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, mContact.peerId);
            if (realmRegisteredInfo != null) {

                if (realmRegisteredInfo.getStatus() != null) {
                    if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                        holder.subtitle.setText(LastSeenTimeUtil.computeTime(mContact.peerId, realmRegisteredInfo.getLastSeen(), false));
                    } else {
                        holder.subtitle.setText(realmRegisteredInfo.getStatus());
                    }
                }

                if (HelperCalander.isPersianUnicode) {
                    holder.subtitle.setText(holder.subtitle.getText().toString());
                }
            }
        }

        setAvatar(holder);
    }

    private void setAvatar(final ViewHolder holder) {
        avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, mContact.peerId).avatarType(AvatarHandler.AvatarType.USER));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
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
            topLine = view.findViewById(R.id.topLine);
        }
    }
}
