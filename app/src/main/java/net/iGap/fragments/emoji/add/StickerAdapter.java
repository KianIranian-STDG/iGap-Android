package net.iGap.fragments.emoji.add;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.helper.LayoutCreator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends RecyclerView.Adapter {

//    private List<StructItemSticker> mData = new ArrayList<>();

//    private List<String> paths = new ArrayList<>();


    private List<StructIGSticker> igStickers = new ArrayList<>();
    private AddStickerDialogListener listener;

    public void setListener(AddStickerDialogListener listener) {
        this.listener = listener;
    }

    public StickerAdapter(AddStickerDialogListener stickerDialogListener) {
        this.listener = stickerDialogListener;

//        paths.add("/storage/emulated/0/iGap/iGap Images/791d9a011707a22caf2564be154d1803586ca9fa.json");
//        paths.add("/storage/emulated/0/iGap/iGap Images/59150779dd5d73bfd16f802f9d90be16e06b5028.json");
//        paths.add("/storage/emulated/0/iGap/iGap Images/a711d859f130dc337ea18662d15b2d183d972637.json");
//        paths.add("/storage/emulated/0/iGap/iGap Images/9c8b2a95e824e1d99ac99861c0e1e609ca7f949f.json");
//        paths.add("/storage/emulated/0/iGap/iGap Images/75201abbe3d90f4e59da4a3cbef0b5590e745750.json");
//        paths.add("/storage/emulated/0/iGap/iGap Images/5348019ae8dd57851ee3dd71739172ae5d89f062.json");
//        paths.add("/storage/emulated/0/Android/data/net.iGap/cache/.sticker/9D1OKFOfwzb1vDiwUukkMsEtal7ObXR914RsaCdh.json");
//        paths.add("/storage/emulated/0/Android/data/net.iGap/cache/.sticker/FsQljROpARpWEbRtQ4ZdOhdGNwF0jLet8PZYSPoO.json");
//        paths.add("/storage/emulated/0/Android/data/net.iGap/cache/.sticker/ChaqHK1zqfWwV7QFIdTnOXIchFAL4zYoUdH55y1N.json");
//        paths.add("/storage/emulated/0/Android/data/net.iGap/cache/.sticker/94sbyb9eRBtmUUXGwTX60shtnngAQuNm7EkDxS67.json");
//        paths.add("/storage/emulated/0/Android/data/net.iGap/cache/.sticker/GVWf4pfTXbcM1xSzJelCuj556EY67L1igQpQ3yoQ.json");
//        paths.add("/storage/emulated/0/Android/data/net.iGap/cache/.sticker/z5hWy2YVLDT32WcC5sr9q7vslXghQY4oBble5i0x.json");


    }


    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == StructIGSticker.ANIMATED_STICKER) {
            AnimatedStickerCell stickerCell = new AnimatedStickerCell(parent.getContext());
            stickerCell.setLayoutParams(LayoutCreator.createFrame(100, 100));
            viewHolder = new AnimatedViewHolder(stickerCell);
        } else if (viewType == StructIGSticker.NORMAL_STICKER) {
            View normalSticker = new ImageView(parent.getContext());
            normalSticker.setLayoutParams(LayoutCreator.createFrame(100, 100));
            viewHolder = new NormalViewHolder(normalSticker);
        } else {
            ProgressBar progressBar = new ProgressBar(parent.getContext());
            progressBar.setLayoutParams(LayoutCreator.createFrame(100, 100));
            viewHolder = new ProgressViewHolder(progressBar);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        String path = igStickers.get(position).getPath();

        if (viewType == StructIGSticker.ANIMATED_STICKER) {
            ((AnimatedViewHolder) holder).bindView(igStickers.get(position));

//            if (!new File(path).exists()) {
//                HelperDownloadSticker.stickerDownload(item.getToken(), item.getName(), item.getAvatarSize(), ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {
//                    @Override
//                    public void OnProgress(String path, String token, int progress, int stickerType) {
//                        G.handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                animatedViewHolder.stickerCell.playAnimation(path);
//                                Log.i("abbasiSticker", "run: " + progress + path);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void OnError(String token) {
//
//                    }
//                });
//            } else {
//            }

        } else if (viewType == StructIGSticker.NORMAL_STICKER) {
            ((NormalViewHolder) holder).bindView(igStickers.get(position));

//            if (!new File(path).exists()) {
//                HelperDownloadSticker.stickerDownload(item.getToken(), item.getName(), item.getAvatarSize(), ProtoFileDownload.FileDownload.Selector.FILE, RequestFileDownload.TypeDownload.STICKER, new HelperDownloadSticker.UpdateStickerListener() {
//                    @Override
//                    public void OnProgress(String path, String token, int progress, int stickerType) {
//                        G.handler.post(() -> Glide.with(holder.itemView.getContext())
//                                .load(path)
//                                .into(normalViewHolder.imgSticker));
//                    }
//
//                    @Override
//                    public void OnError(String token) {
//
//                    }
//                });
//            } else {
//                Glide.with(holder.itemView.getContext())
//                        .load(path)
//                        .into(normalViewHolder.imgSticker);
//            }
        } else {

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

    public void updateIgStickers(List<StructIGSticker> igStickers) {
        this.igStickers = igStickers;
        notifyDataSetChanged();
    }


    //    public void updateAdapter(List<StructItemSticker> data) {
////        this.mData = data;
//        notifyDataSetChanged();
//    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSticker;

        NormalViewHolder(View itemView) {
            super(itemView);
            imgSticker = (ImageView) itemView;
        }


        public void bindView(StructIGSticker structIGSticker) {
            if (structIGSticker.getPath() != null && !structIGSticker.getPath().equals(""))
                Glide.with(itemView.getContext()).load(structIGSticker.getPath()).into(imgSticker);
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView;
        }

        public void bindView(int visibility) {
            progressBar.setVisibility(visibility);
        }
    }

    public class AnimatedViewHolder extends RecyclerView.ViewHolder {
        private AnimatedStickerCell stickerCell;

        AnimatedViewHolder(View itemView) {
            super(itemView);
            stickerCell = (AnimatedStickerCell) itemView;
        }

        public void bindView(StructIGSticker structIGSticker) {
            if (structIGSticker.getPath() != null && !structIGSticker.getPath().equals(""))
                stickerCell.playAnimation(structIGSticker.getPath());
        }
    }

    public interface AddStickerDialogListener {
        void onStickerClick();

        void onStickerLongClick();

        void onStickerDownLoaded();
    }
}

