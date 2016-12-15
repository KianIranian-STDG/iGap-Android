package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.iGap.R;
import com.iGap.helper.HelperRadius;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

public class ImageItem extends AbstractMessage<ImageItem, ImageItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public ImageItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutImage;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_image;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);

        //holder.image.setCornerRadius(G.IMAGE_CORNER);
        holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));

        holder.itemView.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelected()) {
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                        return;
                    }
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                        messageClickListener.onFailedMessageClick(v, mMessage, holder.getAdapterPosition());
                    } else {
                        messageClickListener.onOpenClick(v, mMessage, holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);

            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
            itemView.setVisibility(View.INVISIBLE);
        }
    }
}
