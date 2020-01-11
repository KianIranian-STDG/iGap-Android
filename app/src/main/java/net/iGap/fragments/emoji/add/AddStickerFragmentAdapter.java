package net.iGap.fragments.emoji.add;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.adapter.items.cells.AddAnimatedStickerCell;
import net.iGap.adapter.items.cells.AddNormalStickerCell;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AddStickerFragmentAdapter extends RecyclerView.Adapter {
    private List<StructIGStickerGroup> stickerGroups = new ArrayList<>();
    private AddStickerAdapterListener listener;

    public void setListener(AddStickerAdapterListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == StructIGSticker.ANIMATED_STICKER) {
            AddAnimatedStickerCell view = new AddAnimatedStickerCell(parent.getContext());
            holder = new MotionViewHolder(view);
        } else {
            AddNormalStickerCell view = new AddNormalStickerCell(parent.getContext());
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        StructIGStickerGroup item = stickerGroups.get(position);

        item.setAvatarPath(HelperDownloadSticker.downloadStickerPath(item.getAvatarToken(), item.getAvatarName()));
//
//        DbManager.getInstance().doRealmTask(realm -> {
//            RealmStickers realmStickers = RealmStickers.checkStickerExist(item.getGroupId(), realm);
//            if (realmStickers == null) {
//                item.setIsFavorite(false);
//            } else {
//                item.setIsFavorite(true);
//            }
//        });

        if (holder instanceof MotionViewHolder) {
            MotionViewHolder motionViewHolder = (MotionViewHolder) holder;
            motionViewHolder.bindView(item);
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bindView(item);
        }
    }

    @Override
    public int getItemCount() {
        return stickerGroups.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (stickerGroups.get(position).getAvatarName().endsWith(".json"))
            return StructIGSticker.ANIMATED_STICKER;
        else
            return StructIGSticker.NORMAL_STICKER;
    }

    void addData(List<StructIGStickerGroup> data) {
        this.stickerGroups.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AddNormalStickerCell stickerCell;

        ViewHolder(View itemView) {
            super(itemView);
            stickerCell = (AddNormalStickerCell) itemView;
        }

        private void bindView(StructIGStickerGroup sticker) {

            stickerCell.getButton().setMode(sticker.isFavorite() ? 0 : 1);
            stickerCell.getGroupNameTv().setText(sticker.getName());

            String size = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(sticker.getStickers().size())) : String.valueOf(sticker.getStickers().size());
            stickerCell.getGroupStickerCountTv().setText(size);

            stickerCell.setOnClickListener(v -> listener.onCellClick(sticker));

            stickerCell.getButton().setOnClickListener(v -> {
                stickerCell.getButton().changeProgressTo(View.VISIBLE);
                listener.onButtonClick(sticker, isFavorite -> {
                    stickerCell.getButton().changeProgressTo(View.GONE);
                    stickerCell.getButton().setMode(isFavorite ? 0 : 1);
                });
            });

            if (new File(sticker.getAvatarPath()).exists()) {
                Glide.with(itemView.getContext()).load(sticker.getAvatarPath()).into(stickerCell.getGroupAvatarIv());
            } else {
                stickerCell.setEventListener((id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(sticker.getAvatarToken()))
                            Glide.with(itemView.getContext()).load(filePath).into(stickerCell.getGroupAvatarIv());
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(sticker.getGroupId(), sticker.getAvatarToken(), sticker.getAvatarSize(), sticker.getAvatarPath()));
            }
        }
    }

    public class MotionViewHolder extends RecyclerView.ViewHolder {
        private AddAnimatedStickerCell stickerCell;

        MotionViewHolder(View itemView) {
            super(itemView);
            stickerCell = (AddAnimatedStickerCell) itemView;
        }

        private void bindView(StructIGStickerGroup sticker) {
            stickerCell.getButton().setMode(sticker.isFavorite() ? 0 : 1);
            stickerCell.getGroupNameTv().setText(sticker.getName());

            String size = HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(sticker.getStickers().size())) : String.valueOf(sticker.getStickers().size());
            stickerCell.getGroupStickerCountTv().setText(size);

            stickerCell.setOnClickListener(v -> listener.onCellClick(sticker));

            stickerCell.getButton().setOnClickListener(v -> {
                stickerCell.getButton().changeProgressTo(View.VISIBLE);
                listener.onButtonClick(sticker, isFavorite -> {
                    stickerCell.getButton().changeProgressTo(View.GONE);
                    stickerCell.getButton().setMode(isFavorite ? 0 : 1);
                });
            });

            if (new File(sticker.getAvatarPath()).exists()) {
                try {
                    stickerCell.getGroupAvatarIv().setAnimation(new FileInputStream(sticker.getAvatarPath()), sticker.getAvatarToken());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                stickerCell.setEventListener((id, message) -> {
                    String filePath = (String) message[0];
                    String token = (String) message[1];

                    G.handler.post(() -> {
                        if (token.equals(sticker.getAvatarToken())) {
                            try {
                                stickerCell.getGroupAvatarIv().setAnimation(new FileInputStream(filePath), token);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });

                IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(sticker.getGroupId(), sticker.getAvatarToken(), sticker.getAvatarSize(), sticker.getAvatarPath()));
            }
        }
    }

    public interface AddStickerAdapterListener {
        void onButtonClick(StructIGStickerGroup stickerGroup, ProgressStatus progressStatus);

        void onCellClick(StructIGStickerGroup stickerGroup);
    }

    public interface ProgressStatus {
        void setVisibility(boolean isFavorite);
    }
}
