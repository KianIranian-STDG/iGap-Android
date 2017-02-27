package com.iGap.adapter.items;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.proto.ProtoClientSearchUsername;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.github.meness.emoji.EmojiTextView;
import java.util.List;

public class SearchItamIGap extends AbstractItem<SearchItamIGap, SearchItamIGap.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item;
    private Typeface typeFaceIcon;

    public SearchItamIGap setItem(ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item) {
        this.item = item;
        return this;
    }

    public ProtoClientSearchUsername.ClientSearchUsernameResponse.Result getItem() {
        return item;
    }

    @Override public int getType() {
        return R.id.sfsl_imv_contact_avatar;
    }

    @Override public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout;
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        //result.getType();
        //result.getExactMatch();
        //result.getUser();
        //result.getRoom();

        holder.txtIcon.setVisibility(View.GONE);

        if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {

            Log.e("ddddddddd", item.getUser() + "");

            HelperAvatar.getAvatar(item.getUser().getId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override public void onAvatarGet(final String avatarPath, long roomId) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Log.e("ddddddddd", avatarPath + "            aaaaaaaaaaaaaaaa");
                            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), holder.avatar);
                        }
                    });
                }

                @Override public void onShowInitials(final String initials, final String color) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Log.e("ddddddddd", "            aaaaaaaaaaaaaaaa");
                            holder.avatar.setImageBitmap(
                                com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            holder.name.setText(item.getUser().getDisplayName());
            holder.lastSeen.setText(item.getUser().getUsername());
        } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {

            Log.e("ddddddddd", item.getRoom() + "");

            HelperAvatar.getAvatar(item.getRoom().getId(), HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
                @Override public void onAvatarGet(final String avatarPath, long roomId) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {

                            Log.e("ddddddddd", avatarPath + "          bbbbbbbbbbbbbbbb");
                            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), holder.avatar);
                        }
                    });
                }

                @Override public void onShowInitials(final String initials, final String color) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {

                            Log.e("ddddddddd", "          bbbbbbbbbbbbbbbb");
                            holder.avatar.setImageBitmap(
                                com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            holder.name.setText(item.getRoom().getTitle());
            holder.lastSeen.setText(item.getRoom().getType().toString());

            if (item.getRoom().getType() == ProtoGlobal.Room.Type.GROUP) {
                typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/MaterialIcons-Regular.ttf");
                holder.txtIcon.setTypeface(typeFaceIcon);
                holder.txtIcon.setVisibility(View.VISIBLE);
                holder.txtIcon.setText(G.context.getString(R.string.md_users_social_symbol));
            } else if (item.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) {
                typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap_font.ttf");
                holder.txtIcon.setTypeface(typeFaceIcon);
                holder.txtIcon.setVisibility(View.VISIBLE);
                holder.txtIcon.setText(G.context.getString(R.string.md_channel_icon));
            }
        }

        holder.txtTime.setText("");

        if (HelperCalander.isLanguagePersian) {
            holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
            holder.lastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastSeen.getText().toString()));
            holder.txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtTime.getText().toString()));
        }
    }

    @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
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
            txtTime = (TextView) view.findViewById(R.id.sfsl_txt_time);
        }
    }
}


