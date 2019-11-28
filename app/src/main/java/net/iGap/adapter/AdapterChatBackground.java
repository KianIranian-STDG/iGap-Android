/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.StructWallpaper;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.viewmodel.ChatBackgroundViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class AdapterChatBackground extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SOLID_COLOR = 0;
    public static final int WALLPAPER_IMAGE = 1;

    private List<StructWallpaper> mList;
    private List<String> solidColorList;
    private int type;
    private ChatBackgroundViewModel.OnImageWallpaperListClick onImageClick;


    public AdapterChatBackground(ChatBackgroundViewModel.OnImageWallpaperListClick onImageClick) {
        this.type = WALLPAPER_IMAGE;
        this.onImageClick = onImageClick;
    }

    public void wallpaperList(List<StructWallpaper> mList) {
        this.mList = mList;
        this.type = WALLPAPER_IMAGE;
        notifyDataSetChanged();
    }

    public void setSolidColor(List<String> solidColorList) {
        this.solidColorList = solidColorList;
        this.type = SOLID_COLOR;
        notifyDataSetChanged();
    }

    public void setType(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public int getItemCount() {
        if (type == WALLPAPER_IMAGE) {
            return mList != null ? mList.size() + 1 : 0;
        } else {
            return solidColorList != null ? solidColorList.size() : 0;
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == WALLPAPER_IMAGE) {
            return new ViewHolderImage(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_choose, parent, false));
        } else {
            return new ViewHolderSolid(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_image, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderImage) {
            if (position == 0) {
                ((ViewHolderImage) holder).messageProgress.setVisibility(View.GONE);
                ((ViewHolderImage) holder).imageView.setImageResource(R.drawable.add_chat_background_setting);
            } else {
                ((ViewHolderImage) holder).messageProgress.setVisibility(View.VISIBLE);
                ((ViewHolderImage) holder).imageView.setImageDrawable(null);
                StructWallpaper wallpaper = mList.get(position - 1);

                if (wallpaper.getWallpaperType() == FragmentChatBackground.WallpaperType.proto) {
                    RealmAttachment pf = wallpaper.getProtoWallpaper().getFile();

                    final String path = G.DIR_CHAT_BACKGROUND + "/" + "thumb_" + pf.getCacheId() + "_" + pf.getName();
                    if (!new File(path).exists()) {
                        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, path, 4, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String mPath, int progress) {
                                if (progress == 100) {
                                    G.handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (((ViewHolderImage) holder).imageView != null) {
                                                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), ((ViewHolderImage) holder).imageView);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void OnError(String token) {
                            }
                        });
                    } else {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(path), ((ViewHolderImage) holder).imageView);
                    }
                } else {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(wallpaper.getPath()), ((ViewHolderImage) holder).imageView);

                }

                String bigImagePath;
                if (wallpaper.getWallpaperType() == FragmentChatBackground.WallpaperType.proto) {
                    RealmAttachment pf = wallpaper.getProtoWallpaper().getFile();
                    bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
                } else {
                    bigImagePath = wallpaper.getPath();
                }

                if (new File(bigImagePath).exists()) {
                    ((ViewHolderImage) holder).messageProgress.setVisibility(View.GONE);
                } else {
                    ((ViewHolderImage) holder).messageProgress.setVisibility(View.VISIBLE);
                    startDownload(position - 1, ((ViewHolderImage) holder).messageProgress);
                }
            }
            holder.itemView.setOnClickListener(v -> {
                if (position == 0) {
                    onImageClick.onAddImageClick();
                } else {
                    onImageClick.onClick(type, holder.getAdapterPosition() - 1);
                }
            });

        } else if (holder instanceof ViewHolderSolid) {
            ((ViewHolderSolid) holder).cardView.setCardBackgroundColor(Color.parseColor(solidColorList.get(position)));
            holder.itemView.setOnClickListener(v -> onImageClick.onClick(type, holder.getAdapterPosition()));

        }
    }

    private void startDownload(final int position, final MessageProgress messageProgress) {
        messageProgress.withDrawable(R.drawable.ic_cancel, true);

        RealmAttachment pf = mList.get(position).getProtoWallpaper().getFile();

        String path = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();


        messageProgress.withOnProgress(new OnProgress() {
            @Override
            public void onProgressFinished() {

                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(0);
                        messageProgress.setVisibility(View.GONE);
                        notifyItemChanged(position);
                    }
                });


            }
        });

        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, path, 2, new HelperDownloadFile.UpdateListener() {
            @Override
            public void OnProgress(String mPath, final int progress) {
                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(progress);
                    }
                });
            }

            @Override
            public void OnError(String token) {
                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(0);
                        messageProgress.withDrawable(R.drawable.ic_download, true);
                    }
                });
            }
        });
    }

    private class ViewHolderImage extends RecyclerView.ViewHolder {

        private AppCompatImageView imageView;
        private MessageProgress messageProgress;

        ViewHolderImage(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgBackground);
            messageProgress = itemView.findViewById(R.id.progress);
            AppUtils.setProgresColor(messageProgress.progressBar);
            messageProgress.withDrawable(R.drawable.ic_download, true);
        }
    }

    private class ViewHolderSolid extends RecyclerView.ViewHolder {

        private CardView cardView;

        ViewHolderSolid(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item);
        }
    }
}
