package net.iGap.fragments.emoji.remove;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.adapter.items.cells.AddAnimatedStickerCell;
import net.iGap.adapter.items.cells.AddNormalStickerCell;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class RemoveStickerAdapter extends RecyclerView.Adapter {
    private List<StructIGStickerGroup> stickerGroups = new ArrayList<>();
    private RemoveStickerDialogListener listener;

    void updateAdapter(List<StructIGStickerGroup> data) {
        this.stickerGroups = data;
        notifyDataSetChanged();
    }

    public void setListener(RemoveStickerDialogListener listener) {
        this.listener = listener;
    }

    StructIGStickerGroup getStickerGroup(int pos) {
        return stickerGroups.get(pos);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        if (viewType == StructIGSticker.NORMAL_STICKER) {
            AddNormalStickerCell view = new AddNormalStickerCell(parent.getContext());
            holder = new NormalStickerViewHolder(view);
        } else {
            AddAnimatedStickerCell view = new AddAnimatedStickerCell(parent.getContext());
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

    void removeItem(int removedItemPosition) {
        stickerGroups.remove(removedItemPosition);
        notifyItemRemoved(removedItemPosition);
    }

    private class NormalStickerViewHolder extends RecyclerView.ViewHolder {
        private AddNormalStickerCell stickerCell;

        NormalStickerViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerCell = (AddNormalStickerCell) itemView;
        }

        private void bindStickers(StructIGStickerGroup stickerGroup) {
            stickerCell.getGroupNameTv().setText(stickerGroup.getName());
            String size = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize())) : String.valueOf(stickerGroup.getStickersSize());
            stickerCell.getGroupStickerCountTv().setText(size);

            File file = new File(stickerGroup.getAvatarPath());

            stickerCell.getButton().setMode(0);
            stickerCell.getButton().changeProgressTo(View.GONE);

            if (file.exists() && file.canRead()) {
                Glide.with(itemView.getContext()).load(stickerGroup.getAvatarPath()).into(stickerCell.getGroupAvatarIv());
            } else {

                stickerCell.setEventListener((id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(stickerGroup.getAvatarToken())) {
                            Glide.with(itemView.getContext()).load(filePath).into(stickerCell.getGroupAvatarIv());
                        }
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(stickerGroup.getGroupId(), stickerGroup.getAvatarToken(), stickerGroup.getAvatarSize(), stickerGroup.getAvatarPath()));
            }

            stickerCell.getButton().setOnClickListener(v -> listener.onRemoveStickerClick(stickerGroup, getAdapterPosition(), visibility -> {
                if (visibility)
                    stickerCell.getButton().changeProgressTo(View.VISIBLE);
            }));

            stickerCell.setOnClickListener(v -> listener.onStickerClick(stickerGroup));

        }
    }


    private class MotionStickerViewHolder extends RecyclerView.ViewHolder {
        private AddAnimatedStickerCell stickerCell;

        MotionStickerViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerCell = (AddAnimatedStickerCell) itemView;
        }

        private void bindStickers(StructIGStickerGroup stickerGroup) {
            stickerCell.getGroupNameTv().setText(stickerGroup.getName());
            String size = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize())) : String.valueOf(stickerGroup.getStickersSize());
            stickerCell.getGroupStickerCountTv().setText(size);

            stickerCell.getButton().setMode(0);
            stickerCell.getButton().changeProgressTo(View.GONE);

            File file = new File(stickerGroup.getAvatarPath());

            if (file.exists() && file.canRead()) {
                try {
                    stickerCell.getGroupAvatarIv().setAnimation(new FileInputStream(stickerGroup.getAvatarPath()), stickerGroup.getAvatarToken());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                stickerCell.setEventListener((id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(stickerGroup.getAvatarToken())) {
                            try {
                                stickerCell.getGroupAvatarIv().setAnimation(new FileInputStream(filePath), token);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(stickerGroup.getGroupId(), stickerGroup.getAvatarToken(), stickerGroup.getAvatarSize(), stickerGroup.getAvatarPath()));
            }

            stickerCell.getButton().setOnClickListener(v -> listener.onRemoveStickerClick(stickerGroup, getAdapterPosition(), visibility -> {
                if (visibility)
                    stickerCell.getButton().changeProgressTo(View.VISIBLE);
            }));

            stickerCell.setOnClickListener(v -> listener.onStickerClick(stickerGroup));

        }
    }

    public interface RemoveStickerDialogListener {
        void onStickerClick(StructIGStickerGroup stickerGroup);

        void onRemoveStickerClick(StructIGStickerGroup stickerGroup, int pos, ProgressStatus progressStatus);
    }

    public interface ProgressStatus {
        void setVisibility(boolean visibility);
    }
}
