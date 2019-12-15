package net.iGap.fragments.emoji.remove;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.hanks.library.AnimateCheckBox;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static net.iGap.fragments.emoji.remove.RemoveRecentStickerFragment.removeStickerList;

public class RemoveRecentStickerAdapter extends RecyclerView.Adapter {
    private List<StructItemSticker> mData;

    RemoveRecentStickerAdapter(List<StructItemSticker> data) {
        this.mData = data;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == StructIGSticker.ANIMATED_STICKER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_detail_motion_stickers, parent, false);
            holder = new MotionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_remove_recently_stickers, parent, false);
            holder = new RemoveRecentStickerAdapter.ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StructItemSticker item = mData.get(position);

        if (holder instanceof MotionViewHolder) {
            MotionViewHolder motionViewHolder = (MotionViewHolder) holder;

            String path = HelperDownloadSticker.downloadStickerPath(item.getToken(), item.getUri());
            if (!new File(path).exists()) {

                EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                    String filePath = (String) message[0];
                    String fileToken = (String) message[1];

                    G.handler.post(() -> {
                        if (item.getToken().equals(fileToken)) {
                            try {
                                motionViewHolder.animationView.setAnimation(new FileInputStream(filePath), fileToken);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(item.getId(), item.getToken(), item.getAvatarSize(), path));

            } else {
                try {
                    motionViewHolder.animationView.setAnimation(new FileInputStream(path), item.getToken());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            RemoveRecentStickerAdapter.ViewHolder viewHolder = (ViewHolder) holder;

            String path = HelperDownloadSticker.downloadStickerPath(item.getToken(), item.getUri());

            if (!new File(path).exists()) {
                EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                    String filePath = (String) message[0];
                    String fileToken = (String) message[1];

                    G.handler.post(() -> {
                        if (item.getToken().equals(fileToken)) {
                            Glide.with(holder.itemView.getContext())
                                    .load(filePath)
                                    .into(viewHolder.imgSticker);
                        }
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(item.getId(), item.getToken(), item.getAvatarSize(), path));
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(path)
                        .into(viewHolder.imgSticker);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getUri().endsWith(".json"))
            return StructIGSticker.ANIMATED_STICKER;
        else
            return StructIGSticker.NORMAL_STICKER;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSticker;
        AnimateCheckBox animateCheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            imgSticker = itemView.findViewById(R.id.imgSticker);
            animateCheckBox = itemView.findViewById(R.id.cig_checkBox_select_user);

            itemView.setOnClickListener(v -> {
                if (animateCheckBox.isChecked()) {
                    animateCheckBox.setChecked(false);
                    removeStickerList.remove(mData.get(getAdapterPosition()).getId());
                } else {
                    animateCheckBox.setChecked(true);
                    removeStickerList.add(mData.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    public class MotionViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView animationView;
        AnimateCheckBox animateCheckBox;

        MotionViewHolder(View itemView) {
            super(itemView);
            animationView = itemView.findViewById(R.id.imgSticker);
            animateCheckBox = itemView.findViewById(R.id.cig_checkBox_select_user);

            animationView.setFailureListener(result -> Log.e(getClass().getName(), "MotionViewHolder: ", result));

            itemView.setOnClickListener(v -> {
                if (animateCheckBox.isChecked()) {
                    animateCheckBox.setChecked(false);
                    removeStickerList.remove(mData.get(getAdapterPosition()).getId());

                } else {
                    animateCheckBox.setChecked(true);
                    removeStickerList.add(mData.get(getAdapterPosition()).getId());

                }
            });
        }
    }
}