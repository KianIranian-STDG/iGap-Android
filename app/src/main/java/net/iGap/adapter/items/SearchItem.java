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

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.SearchFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import static net.iGap.fragments.SearchFragment.SearchType.contact;
import static net.iGap.fragments.SearchFragment.SearchType.header;

public class SearchItem extends AbstractItem<SearchItem, SearchItem.ViewHolder> {
    public SearchFragment.StructSearch item;
    private AvatarHandler avatarHandler;

    public SearchItem(AvatarHandler avatarHandler) {
        this.avatarHandler = avatarHandler;
    }

    public SearchItem setContact(SearchFragment.StructSearch item) {
        this.item = item;

        return this;
    }

    @Override
    public int getType() {
        return R.id.sfsl_imv_contact_avatar;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout;
    }

    @SuppressLint("NewApi")
    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setAvatar(holder);
        if (item.isVerified) {
            holder.txtIconVerified.setText(R.string.icon_blue_badge);
            holder.txtIconVerified.setTextColor(holder.itemView.getContext().getColor(R.color.verify));
            holder.txtIconVerified.setVisibility(View.VISIBLE);

        } else {
            holder.txtIconVerified.setVisibility(View.GONE);
        }
        holder.name.setText(item.name);
        if (item.comment.isEmpty()) {
            holder.lastSeen.setText("@"+item.userName);
        } else {
            holder.lastSeen.setText(item.comment);
        }

        //holder.txtTime.setText(TimeUtils.toLocal(item.time, G.CHAT_MESSAGE_TIME));
        holder.txtTime.setText(HelperCalander.getTimeForMainRoom(item.time));

        if (HelperCalander.isPersianUnicode) {
            //holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
//            holder.lastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastSeen.getText().toString()));
            holder.txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtTime.getText().toString()));
        }

        holder.txtIcon.setVisibility(View.GONE);

        if (item.roomType == ProtoGlobal.Room.Type.CHAT) {
            //holder.txtIcon.setVisibility(View.VISIBLE);
            //holder.txtIcon.setText(G.context.getString(R.string.md_user_shape));
        } else if (item.roomType == ProtoGlobal.Room.Type.GROUP) {
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.icon_contacts));
        } else if (item.roomType == ProtoGlobal.Room.Type.CHANNEL) {
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.icon_channel));
        }

        switch (item.type) {
            case contact:
            case message:
                holder.participantsCount.setVisibility(View.GONE);
                break;
            case room:
            case GROUP:
            case CHANNEL:
                if (item.participantsCount>0){
                    holder.participantsCount.setText(
                            HelperCalander.isLanguagePersian||HelperCalander.isLanguageArabic? HelperCalander.convertToUnicodeFarsiNumber(G.context.getString(R.string.member) + " " + item.participantsCount):G.context.getString(R.string.member) + " " + item.participantsCount);
                    holder.participantsCount.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    private void setAvatar(final ViewHolder holder) {
        AvatarHandler.AvatarType avatarType;
        if (item.type == contact) {
            avatarType = AvatarHandler.AvatarType.USER;
        } else {
            if (item.roomType == ProtoGlobal.Room.Type.CHAT) {
                avatarType = AvatarHandler.AvatarType.USER;
            } else {
                avatarType = AvatarHandler.AvatarType.ROOM;
            }
        }
        avatarHandler.getAvatar(new ParamWithAvatarType(holder.avatar, item.idDetectAvatar).avatarType(avatarType));
    }

    @Override
    public ViewHolder getViewHolder(View v) {

        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView avatar;
        protected CustomTextViewMedium name;
        protected TextView txtIcon;
        protected TextView txtIconVerified;
        protected TextView lastSeen;
        protected TextView txtTime;
        protected TextView participantsCount;
        protected View line;
        protected LinearLayout mainContainer;

        public ViewHolder(View view) {
            super(view);

            mainContainer = view.findViewById(R.id.mainContainer);
            mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            avatar = view.findViewById(R.id.sfsl_imv_contact_avatar);
            name = view.findViewById(R.id.sfsl_txt_contact_name);
            name.setTextColor(Theme.getColor(Theme.key_default_text));
            lastSeen = view.findViewById(R.id.sfsl_txt_contact_lastseen);
            lastSeen.setTextColor(Theme.getColor(Theme.key_default_text));
            txtIcon = view.findViewById(R.id.sfsl_txt_icon);
            txtIcon.setTextColor(Theme.getColor(Theme.key_icon));
            txtIconVerified = view.findViewById(R.id.sfsl_txt_verified_icon);
            txtTime = view.findViewById(R.id.sfsl_txt_time);
            txtTime.setTextColor(Theme.getColor(Theme.key_default_text));
            participantsCount = view.findViewById(R.id.sfsl_txt_ParticipantsCount);
            line = view.findViewById(R.id.line);
            participantsCount.setTextColor(Theme.getColor(Theme.key_dark_gray));
//            txtIcon.setTextColor(Theme.getColor(Theme.key_default_text));
            line.setBackgroundColor(Theme.getColor(Theme.key_line));
        }
    }
}


