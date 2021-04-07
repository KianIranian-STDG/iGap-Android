package net.iGap.libs.emojiKeyboard.adapter;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import net.iGap.G;
import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.libs.emojiKeyboard.struct.StructStickerCategory;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;

import java.util.ArrayList;
import java.util.List;

public class StickerCategoryAdapter extends RecyclerView.Adapter {
    private Listener listener;
    private List<StructStickerCategory> categories = new ArrayList<>();

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setCategories(List<StructStickerCategory> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == StructStickerCategory.DRAWABLE) {
            AppCompatImageView view = new AppCompatImageView(parent.getContext());
            view.setLayoutParams(LayoutCreator.createFrame(42, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 1, 0, 1, 0));
            holder = new DrawableViewHolder(view);
        } else if (viewType == StructIGSticker.ANIMATED_STICKER) {
            AnimatedStickerCell stickerCell = new AnimatedStickerCell(parent.getContext());
            stickerCell.setLayoutParams(LayoutCreator.createFrame(42, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 1, 0, 1, 0));
            holder = new AnimatedViewHolder(stickerCell);
        } else {
            AppCompatImageView normalSticker = new AppCompatImageView(parent.getContext());
            normalSticker.setLayoutParams(LayoutCreator.createFrame(42, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 1, 0, 1, 0));
            holder = new NormalViewHolder(normalSticker);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DrawableViewHolder) {
            ((DrawableViewHolder) holder).bindView(categories.get(position));
        } else if (holder instanceof AnimatedViewHolder) {
            ((AnimatedViewHolder) holder).bindView(categories.get(position));
        } else if (holder instanceof NormalViewHolder) {
            ((NormalViewHolder) holder).bindView(categories.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (categories.get(position).getType() == StructStickerCategory.DRAWABLE) {
            return StructStickerCategory.DRAWABLE;
        } else {
            return categories.get(position).getStructIGStickerGroup().getAvatarType();
        }

    }

    public class AnimatedViewHolder extends RecyclerView.ViewHolder {
        private AnimatedStickerCell stickerCell;

        AnimatedViewHolder(View itemView) {
            super(itemView);
            stickerCell = (AnimatedStickerCell) itemView;

            stickerCell.setRepeatCount(LottieDrawable.INFINITE);
            stickerCell.setRepeatMode(LottieDrawable.REVERSE);

            stickerCell.setFailureListener(result -> Log.e(getClass().getName(), "AnimatedViewHolder: ", result));
        }


        public void bindView(StructStickerCategory category) {
            StructIGStickerGroup group = category.getStructIGStickerGroup();
            stickerCell.setTag(group.getAvatarToken());
            if (group.getAvatarPath() != null && !group.getAvatarPath().equals("")) {
                if (group.hasFileOnLocal()) {
                    stickerCell.playAnimation(group.getAvatarPath());
                } else {
                    IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(group.getGroupId(), group.getAvatarToken(), group.getAvatarSize(), group.getAvatarPath()));
                }
            }

            stickerCell.setOnClickListener(v -> {
                if (listener != null)
                    listener.onStickerClick(category);
            });
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView normalStickerCell;

        NormalViewHolder(View itemView) {
            super(itemView);
            normalStickerCell = (AppCompatImageView) itemView;
            int padding = LayoutCreator.dp(4);
            normalStickerCell.setPadding(padding, padding, padding, padding);
            normalStickerCell.setScaleType(ImageView.ScaleType.CENTER);
        }

        public void bindView(StructStickerCategory category) {
            StructIGStickerGroup group = category.getStructIGStickerGroup();
            if (group.getAvatarPath() != null && !group.getAvatarPath().equals("")) {
                if (group.hasFileOnLocal()) {
                    Glide.with(itemView.getContext())
                            .load(group.getAvatarPath())
                            .transition(DrawableTransitionOptions.withCrossFade(200))
                            .into(normalStickerCell);
                } else {
                    EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.STICKER_DOWNLOAD, (id, account, args) -> {
                        String filePath = (String) args[0];
                        String token = (String) args[1];

                        if (token.equals(group.getAvatarToken())) {
                            G.handler.post(() -> Glide.with(itemView.getContext())
                                    .load(filePath)
                                    .transition(DrawableTransitionOptions.withCrossFade(200))
                                    .into(normalStickerCell));
                        }
                    });

                    IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(group.getGroupId(), group.getAvatarToken(), group.getAvatarSize(), group.getAvatarPath()));
                }
            }

            normalStickerCell.setOnClickListener(v -> {
                if (listener != null)
                    listener.onStickerClick(category);
            });
        }
    }

    public class DrawableViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView drawableIv;

        DrawableViewHolder(View itemView) {
            super(itemView);
            drawableIv = (AppCompatImageView) itemView;
        }

        public void bindView(StructStickerCategory category) {

            drawableIv.setImageResource(category.getResId());

            drawableIv.setScaleType(ImageView.ScaleType.CENTER);

            drawableIv.setOnClickListener(v -> {
                if (listener != null)
                    listener.onStickerClick(category);
            });
        }
    }

    public interface Listener {
        void onStickerClick(StructStickerCategory category);
    }
}
