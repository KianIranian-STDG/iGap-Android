/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.CircleImageView;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LocationItem extends AbstractMessage<LocationItem, LocationItem.ViewHolder> {

    private FragmentActivity activity;

    public LocationItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener, FragmentActivity activity) {
        super(mAdapter, true, type, messageClickListener);
        this.activity = activity;
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLocation;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.getChatBloke().setBackgroundResource(0);
        holder.mapPosition.setImageResource(R.drawable.map);
//        RealmRoomMessageLocation item = null;
//
//        if (messageObject.forwardedMessage != null) {
//            if (messageObject.forwardedMessage.location != null) {
//                item = mMessage.getForwardMessage().getLocation();
//            }
//        } else {
//            if (mMessage.getLocation() != null) {
//                item = mMessage.getLocation();
//            }
//        }
//
//        if (item != null) {
//            String path = AppUtils.getLocationPath(item.getLocationLat(), item.getLocationLong());
//
//            if (new File(path).exists()) {
//                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), holder.mapPosition);
//            } else {
//                RealmRoomMessageLocation finalItem1 = item;
//                FragmentMap.loadImageFromPosition(item.getLocationLat(), item.getLocationLong(), bitmap -> {
//                    if (bitmap == null) {
//                        holder.mapPosition.setImageResource(R.drawable.map);
//                    } else {
//                        holder.mapPosition.setImageBitmap(bitmap);
//                        AppUtils.saveMapToFile(bitmap, finalItem1.getLocationLat(), finalItem1.getLocationLong());
//                    }
//                });
//            }
//
//            final RealmRoomMessageLocation finalItem = item;
//            holder.mapPosition.setOnLongClickListener(getLongClickPerform(holder));
//
//            holder.mapPosition.setOnClickListener(v -> {
//                if (FragmentChat.isInSelectionMode) {
//                    holder.itemView.performLongClick();
//                    return;
//                }
//
//                try {
//                    HelperPermission.getLocationPermission(activity, new OnGetPermission() {
//                        @Override
//                        public void Allow() {
//                            G.handler.post(() -> {
//                                FragmentMap fragment = FragmentMap.getInctance(finalItem.getLocationLat(), finalItem.getLocationLong(), FragmentMap.Mode.seePosition,
//                                        RealmRoom.detectType(messageObject.roomId).getNumber(), messageObject.roomId, messageObject.userId + "");
//                                new HelperFragment(activity.getSupportFragmentManager(), fragment).setReplace(false).load();
//                            });
//                        }
//
//                        @Override
//                        public void deny() {
//
//                        }
//                    });
//                } catch (IOException | IllegalStateException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder implements IThumbNailItem {

        CircleImageView mapPosition;

        public ViewHolder(View view) {
            super(view);

            mapPosition = new CircleImageView(getContext());
            mapPosition.setId(R.id.thumbnail);
            mapPosition.setBorderWidth(dpToPx(1));
            getContentBloke().addView(mapPosition, LayoutCreator.createFrame(dpToPx(60), dpToPx(60)));
        }

        @Override
        public ImageView getThumbNailImageView() {
            return mapPosition;
        }
    }
}
