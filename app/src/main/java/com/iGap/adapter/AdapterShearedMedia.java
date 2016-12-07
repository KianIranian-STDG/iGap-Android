package com.iGap.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityShearedMedia;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.helper.HelperMimeType;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.proto.ProtoClientSearchRoomHistory;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmShearedMedia;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.meness.github.messageprogress.MessageProgress;
import io.realm.Realm;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by android3 on 9/4/2016.
 */
public class AdapterShearedMedia extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ActivityShearedMedia.StructSharedMedia> list;
    ArrayList<option> options;
    Context context;
    private boolean isSelectedMode = false;    // for determine user select some file
    private int numberOfSelected = 0;

    private OnComplete complete;
    private MusicPlayer musicPlayer;
    private long roomId = 0;

    ActivityShearedMedia.SharedMediaType mediaType;

    public AdapterShearedMedia(Context context, ArrayList<ActivityShearedMedia.StructSharedMedia> list, ActivityShearedMedia.SharedMediaType mediaType, OnComplete complete, MusicPlayer musicPlayer, long roomId) {
        this.context = context;
        this.list = list;
        this.mediaType = mediaType;
        this.complete = complete;
        this.roomId = roomId;
        this.musicPlayer = musicPlayer;

        options = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            options.add(new option());
        }
    }

    public static long getCountOfSheareddMedia(long roomId) {

        long counter = 0;

        Realm realm = Realm.getDefaultInstance();

        ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter = ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.URL;

        counter = realm.where(RealmShearedMedia.class).equalTo("roomId", roomId).
                notEqualTo("filter", ActivityShearedMedia.SerializationUtils.serialize(filter)).count();


        realm.close();

        return counter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        RecyclerView.ViewHolder viewHolder = null;
        boolean isHeader = false;

        if (list.get(position).isItemTime) {
            isHeader = true;
        }


        switch (mediaType) {

            case image:
                if (isHeader) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                    viewHolder = new MyHoldersTime(view, position);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                    viewHolder = new MyHoldersImage(view, position);
                }
                break;
            case video:
                if (isHeader) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                    viewHolder = new MyHoldersTime(view, position);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                    viewHolder = new MyHoldersVideo(view, position);
                }
                break;
            case audio:
            case voice:
                if (isHeader) {
                    viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    view.setLayoutParams(lp);
                    viewHolder = new MyHoldersMusic(view, position);
                }
                break;

            case gif:
                if (isHeader) {
                    viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_gif, null);
                    viewHolder = new MyHolderGif(view, position);
                }
                break;
            case file:
                if (isHeader) {
                    viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    view.setLayoutParams(lp);
                    viewHolder = new MyHoldersFile(view, position);
                }
                break;
            case link:
                if (isHeader) {
                    viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
                } else {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_media_sub_layout_link, null);
                    viewHolder = new MyHolderLink(view, position);
                }
                break;
        }


        return viewHolder;
    }

    private View setLayoutHeaderTime(View parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setBackgroundColor(Color.parseColor("#cccccc"));
        return view;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (!list.get(position).isItemTime) {

            setBackgroundColor(holder, position);


            switch (mediaType) {

                case image:
                    onBindImage(holder, position);
                    break;
                case video:
                    onBindVideo(holder, position);
                    break;
                case audio:

                    break;
                case voice:

                    break;
                case gif:

                    break;
                case file:
                    onBindFile(holder, position);
                    break;
                case link:

                    break;
            }


        }
    }

    private void setBackgroundColor(RecyclerView.ViewHolder holder, int position) {

        // set blue back ground for selected file
        FrameLayout layout = (FrameLayout) holder.itemView.findViewById(R.id.smsl_fl_contain_main);

        if (options.get(position).isSelected) {
            layout.setForeground(new ColorDrawable(Color.parseColor("#99AADFF7")));
        } else {
            layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    private void onBindImage(RecyclerView.ViewHolder holder, int position) {

        MyHoldersImage mi = (MyHoldersImage) holder;


        File file = new File(mi.filePath);
        if (file.exists()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mi.filePath), mi.imvPicFile);
        } else {
            file = new File(mi.tempFilePath);
            if (file.exists()) {
                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mi.tempFilePath), mi.imvPicFile);
            }
        }

    }

    private void onBindVideo(RecyclerView.ViewHolder holder, int position) {

        MyHoldersVideo mv = (MyHoldersVideo) holder;
        File file = new File(mv.tempFilePath);

        if (file.exists()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mv.tempFilePath), ((MyHoldersVideo) holder).imvPicFile);

        } else {
            ((MyHoldersVideo) holder).imvPicFile.setImageResource(R.mipmap.j_video);
        }

    }

    private void onBindFile(RecyclerView.ViewHolder holder, int position) {

        MyHoldersFile mf = (MyHoldersFile) holder;
        File file = new File(mf.tempFilePath);

        if (file.exists()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mf.tempFilePath), mf.imvPicFile);
        } else {
            Bitmap bitmap = HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(list.get(position).item.getAttachment().getName()));
            if (bitmap != null)
                mf.imvPicFile.setImageBitmap(bitmap);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void startDownload(final int position) {
        options.get(position).isDownloadCancel = false;
        options.get(position).isDownloading = true;

        new DownLoad().getFile(list.get(position).item, position);

    }

    private void stopDownload(int position) {
        options.get(position).isDownloading = false;
        options.get(position).isDownloadCancel = true;
    }

    private void downloadFile(int position) {

        if (options.get(position).isDownloading) {
            stopDownload(position);
        } else {
            startDownload(position);
        }
    }

    private void setSelectedItem(int position) {

        if (options.get(position).isSelected == false) {
            options.get(position).isSelected = true;
            numberOfSelected++;
        } else {
            options.get(position).isSelected = false;
            numberOfSelected--;

            if (numberOfSelected < 1) {
                isSelectedMode = false;
            }
        }
        notifyItemChanged(position);

        if (complete != null) {
            complete.complete(isSelectedMode, "1", numberOfSelected + "");
        }
    }

    public boolean resetSelected() {

        boolean result = isSelectedMode;

        if (isSelectedMode == true) {
            isSelectedMode = false;

            for (int i = 0; i < list.size(); i++) {
                if (options.get(i).isSelected) {
                    options.get(i).isSelected = false;
                    notifyItemChanged(i);
                    numberOfSelected--;
                    if (numberOfSelected < 1) break;
                }
            }
        }
        complete.complete(false, "1", "0");//

        return result;
    }

    //****************************************************************************************************************

    private void openSelectedFile(int position, RecyclerView.ViewHolder holder) {

        if (options.get(position).needDownload) {

            // first need to download file

        } else {

            switch (mediaType) {

                case image:
                    showImage(position, holder);
                    break;
                case video:
                    playVideo(position, holder);
                    break;
                case file:
                    openFile(position, holder);
                    break;
                case audio:
                case voice:
                    playAudio(position, holder);
                    break;
                case gif:
                    playAndPusGif(position, holder);
                    break;
                case link:

                    break;
            }


        }
    }

    private void playAndPusGif(int position, RecyclerView.ViewHolder holder) {

        MyHolderGif holderGif = (MyHolderGif) holder;

        GifDrawable gifDrawable = holderGif.gifDrawable;
        if (gifDrawable != null) {
            if (gifDrawable.isPlaying()) {
                gifDrawable.pause();
                holderGif.messageProgress.setVisibility(View.VISIBLE);
            } else {
                gifDrawable.start();
                holderGif.messageProgress.setVisibility(View.GONE);
            }
        }

    }

    private void playVideo(int position, RecyclerView.ViewHolder holder) {

        MyHoldersVideo myHoldersVideo = (MyHoldersVideo) holder;

        Intent intent = HelperMimeType.appropriateProgram(myHoldersVideo.filePath);
        if (intent != null) context.startActivity(intent);

    }

    private void openFile(int position, RecyclerView.ViewHolder holder) {

        MyHoldersFile myHoldersFile = (MyHoldersFile) holder;

        Intent intent = HelperMimeType.appropriateProgram(myHoldersFile.filePath);
        if (intent != null) context.startActivity(intent);

    }

    private void showImage(int position, RecyclerView.ViewHolder holder) {

        String filePath = list.get(position).item.getAttachment().getToken();

        ArrayList<ProtoGlobal.RoomMessage> listPic = new ArrayList<>();
        int selectedPicture = 1;


        for (ActivityShearedMedia.StructSharedMedia sharedMedia : list) {

            if (sharedMedia.item != null) {
                listPic.add(sharedMedia.item);
            }
        }

        for (int i = 0; i < listPic.size(); i++) { // determin selected image in list image
            if (listPic.get(i).getAttachment().getToken().equals(filePath)) {
                selectedPicture = i;
                break;
            }
        }


        Fragment fragment = FragmentShowImage.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("listPic", listPic);
        bundle.putInt("SelectedImage", selectedPicture);
        fragment.setArguments(bundle);


        ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.asm_ll_parent, fragment, "Show_Image_fragment_shared_media").commit();
    }

    private void playAudio(int position, RecyclerView.ViewHolder holder) {

        MyHoldersMusic holdersMusic = (MyHoldersMusic) holder;

        MusicPlayer.startPlayer(holdersMusic.filePath, list.get(position).item.getAttachment().getName(), roomId, true);

    }

    //****************************************************************************************************************

    private class option {
        public boolean isSelected = false;
        public boolean isDownloading = false;
        public boolean needDownload = false;
        public boolean isDownloadCancel = false;
        public int progress = 0;
        public long downloadOffset = 0;
        public MessageProgress messageProgress;
    }

    public static String getThumpnailPath(String token, String fileName) {

        String result = "";

        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

        if (attachment != null) {
            if (attachment.getLocalThumbnailPath() != null)
                result = attachment.getLocalThumbnailPath();
        }

        if (result.length() < 1)
            result = G.DIR_TEMP + "/" + "thumb_" + token + "_" + AppUtils.suitableThumbFileName(fileName);

        return result;
    }

    public static String getFilePath(String token, String fileName, ProtoGlobal.RoomMessageType messageType) {

        String result = "";

        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

        if (attachment != null) {
            if (attachment.getLocalFilePath() != null)
                result = attachment.getLocalFilePath();
        }

        if (result.length() < 1)
            result = AndroidUtils.suitableAppFilePath(messageType) + "/" + token + "_" + fileName;

        return result;
    }



    public class MyHolder extends RecyclerView.ViewHolder {

        public MessageProgress messageProgress;


        public MyHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelectedMode) {
                        setSelectedItem(getPosition());
                    } else {
                        openSelectedFile(getPosition(), (MyHolder) MyHolder.this);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    isSelectedMode = true;
                    setSelectedItem(getPosition());
                    return true;
                }
            });


            messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
            messageProgress.withDrawable(R.drawable.ic_download, true);


            messageProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    downloadFile(getPosition());

                    if (options.get(getPosition()).isDownloading) {
                        messageProgress.withDrawable(R.drawable.ic_cancel, true);
                    } else {
                        messageProgress.withDrawable(R.drawable.ic_download, true);
                    }

                }
            });



        }
    }

    public class MyHoldersImage extends MyHolder {

        public ImageView imvPicFile;
        public String tempFilePath;
        public String filePath;


        public MyHoldersImage(View itemView, final int position) {
            super(itemView);

            tempFilePath = getThumpnailPath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName());
            filePath = getFilePath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName(), list.get(position).item.getMessageType());

            imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);
            options.get(position).messageProgress = messageProgress;

            File file = new File(filePath);
            if (file.exists()) {
                options.get(position).needDownload = false;
                messageProgress.setVisibility(View.GONE);
            } else {
                options.get(position).needDownload = true;
                messageProgress.setVisibility(View.VISIBLE);
            }


        }
    }

    public class MyHoldersVideo extends MyHolder {

        public ImageView imvPicFile;
        public String tempFilePath;
        public String filePath;

        public MyHoldersVideo(View itemView, int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);
            options.get(position).messageProgress = messageProgress;

            itemView.findViewById(R.id.smsl_ll_video).setVisibility(View.VISIBLE);

            TextView txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);
            txtVideoIcon.setTypeface(G.fontawesome);

            TextView txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);
            txtVideoTime.setText(AppUtils.humanReadableDuration(list.get(position).item.getAttachment().getDuration()));

            TextView txtVideoSize = (TextView) itemView.findViewById(R.id.smsl_txt_video_size);
            txtVideoSize.setText("(" + AndroidUtils.humanReadableByteCount(list.get(position).item.getAttachment().getSize(), true) + ")");

            tempFilePath = getThumpnailPath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName());
            filePath = getFilePath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName(), list.get(position).item.getMessageType());

            File file = new File(filePath);
            if (file.exists()) {
                options.get(position).needDownload = false;
                messageProgress.setVisibility(View.GONE);
            } else {
                options.get(position).needDownload = true;
                messageProgress.setVisibility(View.VISIBLE);
            }

        }
    }

    public class MyHoldersFile extends MyHolder {

        public ImageView imvPicFile;
        public String tempFilePath;
        public String filePath;

        public MyHoldersFile(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
            tempFilePath = getThumpnailPath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName());
            filePath = getFilePath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName(), list.get(position).item.getMessageType());
            options.get(position).messageProgress = messageProgress;

            File file = new File(filePath);
            if (file.exists()) {
                options.get(position).needDownload = false;
                messageProgress.setVisibility(View.GONE);
            } else {
                options.get(position).needDownload = true;
                messageProgress.setVisibility(View.VISIBLE);
            }

            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
            TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
            txtFileSize.setVisibility(View.INVISIBLE);

            txtFileName.setText(list.get(position).item.getAttachment().getName());
            txtFileInfo.setText(AndroidUtils.humanReadableByteCount(list.get(position).item.getAttachment().getSize(), true));

        }
    }

    public class MyHoldersMusic extends MyHolder {

        public ImageView imvPicFile;
        public String tempFilePath;
        public String filePath;

        public MyHoldersMusic(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
            imvPicFile.setImageResource(R.drawable.green_music_note);

            options.get(position).messageProgress = messageProgress;

            tempFilePath = getThumpnailPath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName());
            filePath = getFilePath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName(), list.get(position).item.getMessageType());

            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
            txtFileName.setText(list.get(position).item.getAttachment().getName());

            TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
            txtFileSize.setText("(" + AndroidUtils.humanReadableByteCount(list.get(position).item.getAttachment().getSize(), true) + ")");

            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
            File file = new File(filePath);


            if (file.exists()) {
                options.get(position).needDownload = false;
                messageProgress.setVisibility(View.GONE);
                MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
                Uri uri = (Uri) Uri.fromFile(file);
                mediaMetadataRetriever.setDataSource(G.context, uri);
                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                if (artist == null)
                    artist = G.context.getString(R.string.unknown_artist);

                txtFileInfo.setText(artist);


                try {
                    mediaMetadataRetriever.setDataSource(G.context, uri);
                    byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                    if (data != null) {
                        Bitmap mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                        imvPicFile.setImageBitmap(mediaThumpnail);
                    } else {
                        file = new File(tempFilePath);
                        if (file.exists()) {
                            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                options.get(position).needDownload = true;
                messageProgress.setVisibility(View.VISIBLE);
                file = new File(tempFilePath);
                if (file.exists()) {
                    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(tempFilePath), imvPicFile);
                }

            }

        }
    }

    public class MyHoldersTime extends RecyclerView.ViewHolder {

        public TextView txtTime;

        public MyHoldersTime(View itemView, int position) {
            super(itemView);

            txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
            txtTime.setText(list.get(position).time);
        }
    }

    public class MyHolderLink extends MyHolder {

        public TextView txtLink;

        public MyHolderLink(View itemView, int position) {
            super(itemView);

            txtLink = (TextView) itemView.findViewById(R.id.smsll_txt_shared_link);

            txtLink.setText(list.get(position).item.getMessage());

            messageProgress.setVisibility(View.GONE);

            options.get(position).messageProgress = messageProgress;
        }
    }

    public class MyHolderGif extends MyHolder {

        GifImageView gifView;
        GifDrawable gifDrawable;

        public String tempFilePath;
        public String filePath;

        public MyHolderGif(final View itemView, int position) {
            super(itemView);

            gifView = (GifImageView) itemView.findViewById(R.id.smslg_gif_view);

            tempFilePath = getThumpnailPath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName());
            filePath = getFilePath(list.get(position).item.getAttachment().getToken(), list.get(position).item.getAttachment().getName(), list.get(position).item.getMessageType());

            options.get(position).messageProgress = messageProgress;

            File file = new File(filePath);
            if (file.exists()) {
                gifView.setImageURI(Uri.fromFile(file));
                options.get(position).needDownload = false;

                messageProgress.withDrawable(R.drawable.ic_play, true);
                messageProgress.setVisibility(View.GONE);
                messageProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (gifDrawable != null) {
                            gifDrawable.start();
                            messageProgress.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                options.get(position).needDownload = true;
                messageProgress.setVisibility(View.VISIBLE);
            }


            gifDrawable = (GifDrawable) gifView.getDrawable();


        }
    }


    class DownLoad {

        OnFileDownloadResponse onFileDownloadResponse;


        public DownLoad() {

            onFileDownloadResponse = new OnFileDownloadResponse() {
                @Override
                public void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
                    int position = getItemPosition(token);

                    if (position >= 0) {
                        options.get(position).progress = progress;
                        options.get(position).downloadOffset = offset;

                        updateView(position);


                        if (progress < 100) {
                            if (!options.get(position).isDownloadCancel) {
                                getFile(list.get(position).item, position);
                            }
                        } else {
                            String localFilePath = getFilePath(list.get(position).item.getAttachment().getToken(),
                                    list.get(position).item.getAttachment().getName(), list.get(position).item.getMessageType());

                            String tmpPath = G.DIR_TEMP + "/" + list.get(position).item.getAttachment().getToken() + "_" +
                                    list.get(position).item.getAttachment().getName();

                            try {
                                AndroidUtils.cutFromTemp(tmpPath, localFilePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }


                    Log.e("tttt", position + "  " + offset + "   " + progress);

                }

                @Override
                public void onAvatarDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType) {

                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    onError(majorCode, minorCode);
                }

                @Override
                public void onBadDownload(String token) {

                }
            };


            G.onFileDownloadResponse = onFileDownloadResponse;

        }


        private void getFile(ProtoGlobal.RoomMessage item, final int position) {

            if (options.get(position).progress == 100) {
                updateView(position);
                return;
            }


            ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

            final String tmpPath = item.getAttachment().getToken() + "_" + item.getAttachment().getName();

            String identity = item.getAttachment().getToken()
                    + '*'
                    + selector.toString()
                    + '*'
                    + item.getAttachment().getSize()
                    + '*'
                    + tmpPath
                    + '*'
                    + options.get(position).downloadOffset;


            new RequestFileDownload().download(item.getAttachment().getToken(), options.get(position).downloadOffset,
                    (int) item.getAttachment().getSize(), selector, identity);
        }

        private int getItemPosition(String token) {

            int position = -1;

            for (int i = 0; i < list.size(); i++) {

                try {
                    if (list.get(i).item.getAttachment().getToken().equals(token)) {
                        position = i;
                        break;
                    }
                } catch (NullPointerException e) {

                }

            }

            return position;
        }


        private void updateView(final int position) {

            final MessageProgress messageProgress = options.get(position).messageProgress;

            if (messageProgress != null) {

                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {

                        if (options.get(position).progress < 100) {
                            messageProgress.withProgress(options.get(position).progress);
                        } else {
                            messageProgress.setVisibility(View.GONE);
                            options.get(position).needDownload = false;
                            options.get(position).needDownload = false;
                        }

                    }
                });

            }

        }

        private void onError(int majorCode, int minorCode) {
            if (majorCode == 713 && minorCode == 1) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                context.getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            } else if (majorCode == 713 && minorCode == 2) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack =
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                        context.getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            } else if (majorCode == 713 && minorCode == 3) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack =
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                        context.getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            } else if (majorCode == 713 && minorCode == 4) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack =
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                        context.getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            } else if (majorCode == 713 && minorCode == 5) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack =
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                        context.getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            } else if (majorCode == 714) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack =
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                        context.getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            } else if (majorCode == 715) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack =
                                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                                        context.getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                        snack.setAction(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            }
        }

    }


    public interface updateProgress {
        void OnProgress(int progress);
    }


}
