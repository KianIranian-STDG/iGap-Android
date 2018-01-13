/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.hanks.library.AnimateCheckBox;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.HashMap;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;

import static net.iGap.G.context;

public class AdapterBottomSheetForward extends AbstractItem<AdapterBottomSheetForward, AdapterBottomSheetForward.ViewHolder> {

    public RealmRoom mList;
    public boolean isChecked = false;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();

    public RealmRoom getItem() {
        return mList;
    }

    public AdapterBottomSheetForward(RealmRoom item) {
        this.mList = item;
    }

    public void setItem(RealmRoom item) {
        this.mList = item;
    }

    //The unique ID for this type of mList
    @Override
    public int getType() {
        return R.id.root_forward_bottom_sheet;
    }

    //The layout to be used for this type of mList
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_forward_bottom_sheet;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        //G.imageLoader.displayImage("file://" + mList.getPath(), holder.imgSrc);


        setAvatar(mList, holder.imgSrc);
        holder.txtName.setText(mList.getTitle());

        //if (mList.isSelected) {
        //    holder.checkBoxSelect.setChecked(false);
        //} else {
        //    holder.checkBoxSelect.setChecked(true);
        //}
        holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));

        holder.checkBoxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.checkBoxSelect.isChecked()) {
                    holder.checkBoxSelect.setChecked(false);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                    //FragmentChat.onPathAdapterBottomSheet.path(mList.getPath(), false);
                    FragmentChat.onForwardBottomSheet.path(mList.getId(), false);
                    //mList.setSelected(true);
                } else {
                    holder.checkBoxSelect.setChecked(true);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                    //FragmentChat.onPathAdapterBottomSheet.path(mList.getPath(), true);
                    FragmentChat.onForwardBottomSheet.path(mList.getId(), true);
                    //mList.setSelected(false);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxSelect.isChecked()) {
                    holder.checkBoxSelect.setChecked(false);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                    FragmentChat.onForwardBottomSheet.path(mList.getId(), false);
                    //mList.setSelected(false);
                } else {
                    holder.checkBoxSelect.setChecked(true);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                    FragmentChat.onForwardBottomSheet.path(mList.getId(), true);
                    //mList.setSelected(true);
                }
            }
        });
    }

    //The viewHolder used for this mList. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private EmojiTextViewE txtName;
        private CircleImageView imgSrc;
        protected AnimateCheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);

            txtName = (EmojiTextViewE) view.findViewById(R.id.txtTitle_forward_bottomSheet);
            imgSrc = (CircleImageView) view.findViewById(R.id.imageView_forward_bottomSheet);
            checkBoxSelect = (AnimateCheckBox) view.findViewById(R.id.checkBox_forward_bottomSheet);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    private void setAvatar(final RealmRoom mInfo, CircleImageView imageView) {
        long idForGetAvatar;
        HelperAvatar.AvatarType avatarType;
        if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
            idForGetAvatar = mInfo.getChatRoom().getPeerId();
            avatarType = HelperAvatar.AvatarType.USER;
        } else {
            idForGetAvatar = mInfo.getId();
            avatarType = HelperAvatar.AvatarType.ROOM;
        }

        hashMapAvatar.put(idForGetAvatar, imageView);

        HelperAvatar.getAvatar(idForGetAvatar, avatarType, false, new OnAvatarGet() {
            @Override
            public void onAvatarGet(String avatarPath, long idForGetAvatar) {
                if (hashMapAvatar.get(idForGetAvatar) != null) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(idForGetAvatar));
                }
            }

            @Override
            public void onShowInitials(String initials, String color) {
                long idForGetAvatar;
                if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
                    idForGetAvatar = mInfo.getChatRoom().getPeerId();
                } else {
                    idForGetAvatar = mInfo.getId();
                }
                if (hashMapAvatar.get(idForGetAvatar) != null) {
                    hashMapAvatar.get(idForGetAvatar).setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp52), initials, color));
                }
            }
        });
    }

}