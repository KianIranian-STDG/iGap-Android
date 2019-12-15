package net.iGap.fragments.emoji.remove;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.emoji.IGDownloadFile;
import net.iGap.fragments.emoji.IGDownloadFileStruct;
import net.iGap.fragments.emoji.add.StructIGSticker;
import net.iGap.fragments.emoji.add.StructIGStickerGroup;
import net.iGap.helper.HelperCalander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class RemoveStickerAdapter extends RecyclerView.Adapter {
    private List<StructIGStickerGroup> stickerGroups;
    private RemoveStickerDialogListener listener;

    public void updateAdapter(List<StructIGStickerGroup> data) {
        this.stickerGroups = data;
        notifyDataSetChanged();
    }

    public void setListener(RemoveStickerDialogListener listener) {
        this.listener = listener;
    }

    public StructIGStickerGroup getStickerGroup(int pos) {
        return stickerGroups.get(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        if (viewType == StructIGSticker.NORMAL_STICKER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remove_normal_sticker, parent, false);
            holder = new NormalStickerViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remove_motion_sticker, parent, false);
            holder = new MotionStickerViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalStickerViewHolder) {
            NormalStickerViewHolder normalStickerViewHolder = (NormalStickerViewHolder) holder;
            normalStickerViewHolder.bindStickers(stickerGroups.get(position));
        } else if (holder instanceof MotionStickerViewHolder) {
            MotionStickerViewHolder motionStickerViewHolder = (MotionStickerViewHolder) holder;
            motionStickerViewHolder.bindStickers(stickerGroups.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (stickerGroups.get(position).getAvatarType() == StructIGSticker.ANIMATED_STICKER)
            return StructIGSticker.ANIMATED_STICKER;
        else
            return StructIGSticker.NORMAL_STICKER;
    }

    @Override
    public int getItemCount() {
        return stickerGroups.size();
    }

    public void removeItem(int removedItemPosition) {
        stickerGroups.remove(removedItemPosition);
        notifyItemRemoved(removedItemPosition);
    }

    private class NormalStickerViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv;
        private TextView stickerNameTv;
        private TextView stickerCountTv;

        public NormalStickerViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.iv_itemRemoveSticker_stickerAvatar);
            stickerNameTv = itemView.findViewById(R.id.tv_itemRemoveSticker_stickerName);
            stickerCountTv = itemView.findViewById(R.id.tv_itemRemoveSticker_stickerCount);
        }

        private void bindStickers(StructIGStickerGroup stickerGroup) {
            stickerNameTv.setText(stickerGroup.getName());
            stickerCountTv.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize())));

            File file = new File(stickerGroup.getAvatarPath());

            if (file.exists() && file.canRead()) {
                Glide.with(itemView.getContext()).load(stickerGroup.getAvatarPath()).into(avatarIv);
            } else {
                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(stickerGroup.getGroupId(),
                        stickerGroup.getAvatarToken(), stickerGroup.getAvatarSize(), stickerGroup.getAvatarPath()));

                EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(stickerGroup.getAvatarToken())) {
                            Glide.with(itemView.getContext()).load(filePath).into(avatarIv);
                        }
                    });

                });
            }

            itemView.findViewById(R.id.btn_itemRemoveSticker).setOnClickListener(v -> {
                if (listener != null)
                    listener.onRemoveStickerClick(stickerGroup, getAdapterPosition());
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onStickerClick(stickerGroup);
            });
        }
    }


    private class MotionStickerViewHolder extends RecyclerView.ViewHolder {
        private LottieAnimationView avatarIv;
        private TextView stickerNameTv;
        private TextView stickerCountTv;

        public MotionStickerViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.lv_itemRemoveSticker_stickerAvatar);
            stickerNameTv = itemView.findViewById(R.id.tv_itemRemoveSticker_stickerName);
            stickerCountTv = itemView.findViewById(R.id.tv_itemRemoveSticker_stickerCount);
        }

        private void bindStickers(StructIGStickerGroup stickerGroup) {
            stickerNameTv.setText(stickerGroup.getName());
            stickerCountTv.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize())));

            avatarIv.setFailureListener(result -> Log.e(getClass().getName(), "bindStickers: ", result));

            File file = new File(stickerGroup.getAvatarPath());

            if (file.exists() && file.canRead()) {
                try {
                    avatarIv.setAnimation(new FileInputStream(stickerGroup.getAvatarPath()), stickerGroup.getAvatarToken());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(stickerGroup.getGroupId(),
                        stickerGroup.getAvatarToken(), stickerGroup.getAvatarSize(), stickerGroup.getAvatarPath()));

                EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(stickerGroup.getAvatarToken())) {
                            try {
                                avatarIv.setAnimation(new FileInputStream(filePath), stickerGroup.getAvatarToken());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                });
            }

            itemView.findViewById(R.id.btn_itemRemoveSticker).setOnClickListener(v -> {
                if (listener != null)
                    listener.onRemoveStickerClick(stickerGroup, getAdapterPosition());
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onStickerClick(stickerGroup);
            });
        }
    }

    public interface RemoveStickerDialogListener {
        void onStickerClick(StructIGStickerGroup stickerGroup);

        void onRemoveStickerClick(StructIGStickerGroup stickerGroup, int pos);
    }
}
