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

import android.net.Uri;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.scrollbar.FastScrollerBarBaseAdapter;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItemGroup extends AbstractItem<ContactItemGroup, ContactItemGroup.ViewHolder> implements FastScrollerBarBaseAdapter {
    public static OnClickAdapter OnClickAdapter;
    public StructContactInfo mContact;
    private AvatarHandler avatarHandler;

    public ContactItemGroup(AvatarHandler avatarHandler) {
        this.avatarHandler = avatarHandler;
    }

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
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        //holder.checkBoxSelect.setChecked(true);
        holder.topLine.setVisibility(View.GONE);

        if (!G.isAppRtl) {
            holder.subtitle.setGravity(Gravity.LEFT);
            holder.title.setGravity(Gravity.LEFT);
        } else {
            holder.subtitle.setGravity(Gravity.RIGHT);
            holder.title.setGravity(Gravity.RIGHT);
        }

        if (mContact.isSelected) {
            holder.checkBoxSelect.setChecked(true);
        } else {
            holder.checkBoxSelect.setChecked(false);
        }

        holder.title.setText(mContact.displayName);

        if (mContact.status != null) {
            if (mContact.status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String timeUser = HelperCalander.getClocktime(mContact.lastSeen * DateUtils.SECOND_IN_MILLIS, false);

                holder.subtitle.setText(G.fragmentActivity.getResources().getString(R.string.last_seen_at) + " " + timeUser);
            } else {
                holder.subtitle.setText(mContact.status);
            }

            if (HelperCalander.isPersianUnicode) {
                holder.subtitle.setText(holder.subtitle.getText().toString());
            }
        }
        avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, mContact.peerId).avatarType(AvatarHandler.AvatarType.USER));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public String getBubbleText(int position) {
        if (mContact == null)
            return "-";
        else {
            return mContact.displayName.toLowerCase().substring(0, 1).toUpperCase();
        }
    }

    public interface OnClickAdapter {
        void onclick(long peerId, Uri uri, String displayName, boolean checked);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;
        protected CheckBox checkBoxSelect;
        protected LinearLayout mainContainer;

        public ViewHolder(View view) {
            super(view);

            mainContainer = (LinearLayout) view.findViewById(R.id.mainContainer);
            mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            title.setTextColor(Theme.getColor(Theme.key_default_text));
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            subtitle.setTextColor(Theme.getColor(Theme.key_subtitle_text));
            topLine = view.findViewById(R.id.topLine);
            topLine.setBackgroundColor(Theme.getColor(Theme.key_line));
            checkBoxSelect = view.findViewById(R.id.cig_checkBox_select_user);

        }

    }
}
