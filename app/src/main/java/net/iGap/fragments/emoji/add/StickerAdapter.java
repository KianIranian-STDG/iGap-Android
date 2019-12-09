package net.iGap.fragments.emoji.add;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.helper.LayoutCreator;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter {
    private List<StructIGSticker> igStickers = new ArrayList<>();
    private AddStickerDialogListener listener;
    private String TAG = "abbasiStickerAdapter";
    private List<String> paths = new ArrayList<>();

    public void setListener(AddStickerDialogListener listener) {
        this.listener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == StructIGSticker.ANIMATED_STICKER) {
//            LottieAnimationView stickerCell = new LottieAnimationView(parent.getContext());
//            stickerCell.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 75, Gravity.CENTER, 1, 0, 1, 2));
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            viewHolder = new AnimatedViewHolder(view);
        } else {
            View normalSticker = new ImageView(parent.getContext());
            normalSticker.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 75, Gravity.CENTER, 1, 0, 1, 2));
            viewHolder = new NormalViewHolder(normalSticker);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        if (viewType == StructIGSticker.ANIMATED_STICKER) {
            ((AnimatedViewHolder) holder).bindView(igStickers.get(position));
        } else if (viewType == StructIGSticker.NORMAL_STICKER) {
            ((NormalViewHolder) holder).bindView(igStickers.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return igStickers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return igStickers.get(position).getType();
    }

    public void setIgStickers(List<StructIGSticker> igStickers) {
        this.igStickers = igStickers;
        notifyDataSetChanged();
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView normalStickerCell;

        NormalViewHolder(View itemView) {
            super(itemView);
            normalStickerCell = (ImageView) itemView;

        }

        /**
         * @param structIGSticker file exist on local load with path but haven't file on local add to download queue and add event listener for load again after download
         */
        public void bindView(StructIGSticker structIGSticker) {
            if (structIGSticker.getPath() != null && !structIGSticker.getPath().equals("")) {
                if (structIGSticker.hasFileOnLocal()) {
                    Glide.with(itemView.getContext()).load(structIGSticker.getPath()).into(normalStickerCell);
                    Log.i(TAG, "LOAD NORMAL STICKER WITH PATH -> " + structIGSticker.getPath());
                } else {
                    EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                        String filePath = (String) message[0];
                        String token = (String) message[1];

                        if (token.equals(structIGSticker.getToken())) {
                            G.handler.post(() -> Glide.with(itemView.getContext()).load(filePath).into(normalStickerCell));
                            Log.i(TAG, "DOWNLOAD STICKER SUCCESSFULLY  token -> " + token + " path -> " + filePath);
                        }
                    });

                    HelperDownloadSticker.downloadSticker(structIGSticker);
                }
            }

            normalStickerCell.setOnClickListener(v -> {
                if (listener != null)
                    listener.onStickerClick(structIGSticker);
            });
        }
    }

    public class AnimatedViewHolder extends RecyclerView.ViewHolder {
        private LottieAnimationView stickerCell;

        AnimatedViewHolder(View itemView) {
            super(itemView);
            stickerCell = itemView.findViewById(R.id.lottie);
//            stickerCell.setRepeatCount(LottieDrawable.INFINITE);
//            stickerCell.setRepeatMode(LottieDrawable.REVERSE);

            stickerCell.setFailureListener(result -> Log.e(getClass().getName(), "AnimatedViewHolder: ", result));
        }


        /**
         * @param structIGSticker file exist on local load with path but haven't file on local add to download queue and add event listener for load again after download
         */
        public void bindView(StructIGSticker structIGSticker) {
            if (structIGSticker.getPath() != null && !structIGSticker.getPath().equals("")) {
                if (structIGSticker.hasFileOnLocal()) {
                    try {
                        stickerCell.setAnimation(new FileInputStream(structIGSticker.getPath()), null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "LOAD LOTTIE STICKER WITH PATH -> " + structIGSticker.getPath());
                } else {
                    EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                        String filePath = (String) message[0];
                        String token = (String) message[1];

                        if (token.equals(structIGSticker.getToken())) {
                            G.handler.post(() -> {
                                try {
                                    stickerCell.setAnimation(new FileInputStream(structIGSticker.getPath()), null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            });
                            Log.i(TAG, "DOWNLOAD STICKER SUCCESSFULLY  token -> " + token + " path -> " + filePath);
                        }
                    });
                    HelperDownloadSticker.downloadSticker(structIGSticker);
                }
            }

            stickerCell.setOnClickListener(v -> {
                if (listener != null)
                    listener.onStickerClick(structIGSticker);
            });
        }
    }

    public interface AddStickerDialogListener {
        void onStickerClick(StructIGSticker structIGSticker);
    }
}

