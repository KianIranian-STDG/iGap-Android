package com.iGap.adapter.items;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.SearchFragment;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.EmojiTextView;
import com.iGap.module.TimeUtils;
import com.iGap.realm.enums.RoomType;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;


public class SearchItem extends AbstractItem<SearchItem, SearchItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public SearchFragment.StructSearch item;

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

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setAvatar(holder);

        holder.name.setText(item.name);
        holder.lastSeen.setText(item.comment);

        holder.txtTime.setText(TimeUtils.toLocal(item.time, G.CHAT_MESSAGE_TIME));


        holder.txtIcon.setVisibility(View.GONE);

        if (item.roomType == RoomType.CHAT) {
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.md_user_shape));

        } else if (item.roomType == RoomType.GROUP) {
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.md_users_social_symbol));
        } else if (item.roomType == RoomType.CHANNEL) {
            holder.txtIcon.setVisibility(View.VISIBLE);
            holder.txtIcon.setText(G.context.getString(R.string.fa_bullhorn));
        }

    }

    private void setAvatar(ViewHolder holder) {
        if (item.avatar != null && item.avatar.getFile() != null) {

            String filepath = item.avatar.getFile().getLocalFilePath();
            if (filepath != null && new File(filepath).exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                holder.avatar.setImageBitmap(bitmap);
            } else {
                String filepathThumbnail = item.avatar.getFile().getLocalThumbnailPath();
                if (filepathThumbnail != null && new File(filepathThumbnail).exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filepathThumbnail);
                    holder.avatar.setImageBitmap(bitmap);
                } else {
                    holder.avatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(holder.avatar.getLayoutParams().width, item.initials, item.color));
                }
            }

        } else {
            holder.avatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(holder.avatar.getLayoutParams().width, item.initials, item.color));
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
        protected CircleImageView avatar;
        protected CustomTextViewMedium name;
        protected TextView txtIcon;
        protected EmojiTextView lastSeen;
        protected TextView txtTime;

        public ViewHolder(View view) {
            super(view);

            avatar = (CircleImageView) view.findViewById(R.id.sfsl_imv_contact_avatar);
            name = (CustomTextViewMedium) view.findViewById(R.id.sfsl_txt_contact_name);
            lastSeen = (EmojiTextView) view.findViewById(R.id.sfsl_txt_contact_lastseen);
            txtIcon = (TextView) view.findViewById(R.id.sfsl_txt_icon);
            txtIcon.setTypeface(G.flaticon);
            txtTime = (TextView) view.findViewById(R.id.sfsl_txt_time);
        }
    }
}


