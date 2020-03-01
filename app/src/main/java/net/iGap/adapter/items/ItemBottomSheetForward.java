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
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hanks.library.AnimateCheckBox;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.module.accountManager.AccountManager;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.CircleImageView;
import net.iGap.module.structs.StructBottomSheetForward;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class ItemBottomSheetForward extends AbstractItem<ItemBottomSheetForward, ItemBottomSheetForward.ViewHolder> {


    public StructBottomSheetForward structBottomSheetForward;
    private AvatarHandler avatarHandler;

    public ItemBottomSheetForward(StructBottomSheetForward structBottomSheetForward, AvatarHandler avatarHandler) {
        this.structBottomSheetForward = structBottomSheetForward;
        this.avatarHandler = avatarHandler;
    }

    @Override
    public void bindView(final ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);

        if (structBottomSheetForward.isContactList()) {
            setAvatarContact(viewHolder, structBottomSheetForward.getId());
        } else {
            setAvatar(structBottomSheetForward, viewHolder.imgSrc);
        }

        if (!structBottomSheetForward.isChecked()) {
            viewHolder.checkBoxSelect.setChecked(false);
            viewHolder.imgSrc.setBorderColor(ContextCompat.getColor(viewHolder.imgSrc.getContext(), R.color.transparent));
        } else {
            viewHolder.checkBoxSelect.setChecked(true);
            viewHolder.imgSrc.setBorderColor(new Theme().getAccentColor(viewHolder.imgSrc.getContext()));
        }

        viewHolder.txtName.setText(EmojiManager.getInstance().replaceEmoji(structBottomSheetForward.getDisplayName(), viewHolder.txtName.getPaint().getFontMetricsInt()));

        viewHolder.checkBoxSelect.setChecked(structBottomSheetForward.isChecked());

        viewHolder.checkBoxSelect.setUnCheckColor(ContextCompat.getColor(viewHolder.checkBoxSelect.getContext(), R.color.transparent));

        viewHolder.checkBoxSelect.setOnClickListener(v -> OnClick(viewHolder));

        viewHolder.itemView.setOnClickListener(v -> OnClick(viewHolder));

    }

    private void OnClick(final ViewHolder viewHolder) {
        if (structBottomSheetForward.isChecked()) {
            structBottomSheetForward.setChecked(false);
            viewHolder.checkBoxSelect.setChecked(false);
            viewHolder.imgSrc.setBorderColor(ContextCompat.getColor(viewHolder.imgSrc.getContext(), R.color.transparent));
        } else {
            structBottomSheetForward.setChecked(true);
            viewHolder.checkBoxSelect.setChecked(true);
            viewHolder.imgSrc.setBorderColor(new Theme().getAccentColor(viewHolder.imgSrc.getContext()));
        }
        FragmentChat.onForwardBottomSheet.path(structBottomSheetForward);
    }

    private void setAvatar(final StructBottomSheetForward mInfo, CircleImageView imageView) {
        long idForGetAvatar;
        AvatarHandler.AvatarType avatarType;
        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
            idForGetAvatar = mInfo.getPeer_id();
            avatarType = AvatarHandler.AvatarType.USER;
        } else {
            idForGetAvatar = mInfo.getId();
            avatarType = AvatarHandler.AvatarType.ROOM;
        }

        if (AccountManager.getInstance().getCurrentUser().getId() == mInfo.getPeer_id()) {
            avatarHandler.removeImageViewFromHandler(imageView);
            imageView.setImageResource(R.drawable.ic_cloud_space_blue);
        } else {
            avatarHandler.getAvatar(new ParamWithAvatarType(imageView, idForGetAvatar).avatarSize(R.dimen.dp52).avatarType(avatarType));
        }
    }

    private void setAvatarContact(final ViewHolder holder, final long userId) {
        avatarHandler.getAvatar(new ParamWithAvatarType(holder.imgSrc, userId).avatarType(AvatarHandler.AvatarType.USER));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.root_forward_bottom_sheet;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.adapter_forward_bottom_sheet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected AnimateCheckBox checkBoxSelect;
        private TextView txtName;
        private CircleImageView imgSrc;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtTitle_forward_bottomSheet);
            imgSrc = view.findViewById(R.id.imageView_forward_bottomSheet);
            checkBoxSelect = view.findViewById(R.id.checkBox_forward_bottomSheet);
        }
    }

}


//}