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

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.proto.ProtoClientSearchUsername;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class SearchItamIGap extends AbstractItem<SearchItamIGap, SearchItamIGap.ViewHolder> {
    ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item;
    private AvatarHandler avatarHandler;

    public SearchItamIGap(AvatarHandler avatarHandler) {
        this.avatarHandler = avatarHandler;
    }

    public ProtoClientSearchUsername.ClientSearchUsernameResponse.Result getItem() {
        return item;
    }

    public SearchItamIGap setItem(ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item) {
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

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.txtIcon.setVisibility(View.GONE);

        if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {
            avatarHandler.getAvatar(new ParamWithAvatarType(holder.avatar, item.getUser().getId()).avatarType(AvatarHandler.AvatarType.USER));

            holder.name.setText(item.getUser().getDisplayName());
            holder.lastSeen.setText(item.getUser().getUsername());
        } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {
            avatarHandler.getAvatar(new ParamWithAvatarType(holder.avatar, item.getRoom().getId()).avatarType(AvatarHandler.AvatarType.ROOM));

            holder.name.setText(item.getRoom().getTitle());

            if (item.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) {
                holder.lastSeen.setText(item.getRoom().getChannelRoomExtra().getPublicExtra().getUsername());
            } else if (item.getRoom().getType() == ProtoGlobal.Room.Type.GROUP) {
                holder.lastSeen.setText(item.getRoom().getGroupRoomExtra().getPublicExtra().getUsername());
            }

            Typeface typeFaceIcon = ResourcesCompat.getFont(holder.txtIcon.getContext(), R.font.font_icon);
            if (item.getRoom().getType() == ProtoGlobal.Room.Type.GROUP) {
                holder.txtIcon.setTypeface(typeFaceIcon);
                holder.txtIcon.setVisibility(View.VISIBLE);
                holder.txtIcon.setText(G.context.getString(R.string.group_icon));
            } else if (item.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) {
                holder.txtIcon.setTypeface(typeFaceIcon);
                holder.txtIcon.setVisibility(View.VISIBLE);
                holder.txtIcon.setText(G.context.getString(R.string.channel_main_icon));
            }
        }

        holder.txtTime.setText("");

        if (HelperCalander.isPersianUnicode) {
            holder.name.setText(holder.name.getText().toString());
            holder.lastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastSeen.getText().toString()));
            holder.txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtTime.getText().toString()));
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView avatar;
        protected CustomTextViewMedium name;
        protected TextView txtIcon;
        protected TextView lastSeen;
        protected TextView txtTime;

        public ViewHolder(View view) {
            super(view);

            avatar = view.findViewById(R.id.sfsl_imv_contact_avatar);
            name = view.findViewById(R.id.sfsl_txt_contact_name);
            lastSeen = view.findViewById(R.id.sfsl_txt_contact_lastseen);
            txtIcon = view.findViewById(R.id.sfsl_txt_icon);
            txtTime = view.findViewById(R.id.sfsl_txt_time);
        }
    }
}


