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
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

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

    public static int getCountOfSheareddMedia(long roomId) {

        int counter = 0;

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmRoomMessage> realmRoomMessages =
                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                        .findAll();

        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            ProtoGlobal.RoomMessageType type = realmRoomMessage.getMessageType();
            if (type.equals(ProtoGlobal.RoomMessageType.VOICE) || type.equals(
                    ProtoGlobal.RoomMessageType.AUDIO.toString()) || type.equals(
                    ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.VIDEO) || type.equals(
                    ProtoGlobal.RoomMessageType.VIDEO_TEXT) ||
                    type.equals(ProtoGlobal.RoomMessageType.FILE.toString()) || type.equals(
                    ProtoGlobal.RoomMessageType.FILE_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.IMAGE) || type.equals(
                    ProtoGlobal.RoomMessageType.IMAGE_TEXT)) {

                counter++;
            }
        }

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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

        String path = "";

        path = ((MyHoldersImage) holder).attachment.getLocalFilePath();
        if (path == null) path = "";

        if (path.length() < 1) {
            path = ((MyHoldersImage) holder).attachment.getLocalThumbnailPath();
        }

        if (path == null) path = "";

        if (path.length() > 0) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(path), ((MyHoldersImage) holder).imvPicFile);
        }

    }


    private void onBindVideo(RecyclerView.ViewHolder holder, int position) {

        String path = "";

        path = ((MyHoldersVideo) holder).attachment.getLocalThumbnailPath();
        if (path == null) path = "";

        if (path.length() < 1) {
            ((MyHoldersVideo) holder).imvPicFile.setImageResource(R.mipmap.j_video);
        } else {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(path), ((MyHoldersVideo) holder).imvPicFile);

            //  new LoadImageToImageView(((MyHoldersVideo) holder).imvPicFile, path).execute();
        }

    }


    private void onBindFile(RecyclerView.ViewHolder holder, int position) {

        MyHoldersFile m = (MyHoldersFile) holder;

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void startDownload(int position) {

        options.get(position).isDownloading = true;
        notifyItemChanged(position);
    }

    private void stopDownload(int position) {
        options.get(position).isDownloading = false;
        notifyItemChanged(position);
    }

    private void openSelectedFile(int position) {

        if (false) {
            // if need dowmload get file from server
            downloadFile(position);
        } else {


            switch (mediaType) {

                case image:
                    showImage(position);
                    break;
                case video:
                case file:
                    openFileOrVideo(position);
                    break;
                case audio:
                case voice:
                    playAudio(position);
                    break;
                case gif:

                    break;
                case link:

                    break;
            }


        }
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

    private void openFileOrVideo(int position) {
        RealmAttachment attachment = RealmAttachment.build(list.get(position).item.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT);

        Intent intent = HelperMimeType.appropriateProgram(attachment.getLocalFilePath());
        if (intent != null) context.startActivity(intent);

    }

    private void showImage(int position) {

        RealmAttachment attachment = RealmAttachment.build(list.get(position).item.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT);
        String filePath = attachment.getLocalFilePath();


        ArrayList<StructMessageInfo> listPic = new ArrayList<>();
        int selectedPicture = 1;


        // TODO: 11/29/2016  nejati     obtimize image view for path parameter

        for (ActivityShearedMedia.StructSharedMedia sharedMedia : list) {

            if (sharedMedia.item != null) {
                if (sharedMedia.item.getMessageType().equals(ProtoGlobal.RoomMessageType.IMAGE)
                        || sharedMedia.item.getMessageType().equals(ProtoGlobal.RoomMessageType.IMAGE_TEXT)) {
                    listPic.add(StructMessageInfo.convert(putOrUpdate(sharedMedia.item)));
                }
            }
        }

        for (int i = 0; i < listPic.size(); i++) { // determin selected image in list image
            if (listPic.get(i).attachment != null && listPic.get(i).attachment.getLocalFilePath() != null && filePath != null
                    && listPic.get(i).attachment.getLocalFilePath().equals(filePath)) {
                selectedPicture = i;
                break;
            }
        }


        Fragment fragment = FragmentShowImage.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("listPic", listPic);
        bundle.putInt("SelectedImage", selectedPicture);
        fragment.setArguments(bundle);


        ((Activity) context).getFragmentManager()
                .beginTransaction()
                .replace(R.id.asm_ll_parent, fragment, "Show_Image_fragment_shared_media")
                .commit();
    }

    private void playAudio(int position) {
        RealmAttachment attachment = RealmAttachment.build(list.get(position).item.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT);

        MusicPlayer.startPlayer(attachment.getLocalFilePath(), attachment.getName(), roomId, true);
    }

    //****************************************************************************************************************

    private class option {
        public boolean isSelected = false;
        public boolean isDownloading = false;
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelectedMode) {
                        setSelectedItem(getPosition());
                    } else {
                        openSelectedFile(getPosition());
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
        }
    }

    public class MyHoldersImage extends MyHolder {

        public ImageView imvPicFile;

        RealmAttachment attachment;

        public MyHoldersImage(View itemView, int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);

            attachment = RealmAttachment.build(list.get(position).item.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT);
        }
    }

    public class MyHoldersVideo extends MyHolder {

        public ImageView imvPicFile;
        RealmAttachment attachment;

        public MyHoldersVideo(View itemView, int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);


            itemView.findViewById(R.id.smsl_ll_video).setVisibility(View.VISIBLE);

            TextView txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);
            txtVideoIcon.setTypeface(G.fontawesome);

            TextView txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);
            txtVideoTime.setText(AppUtils.humanReadableDuration(list.get(position).item.getAttachment().getDuration()));

            TextView txtVideoSize = (TextView) itemView.findViewById(R.id.smsl_txt_video_size);
            txtVideoSize.setText("(" + AndroidUtils.humanReadableByteCount(list.get(position).item.getAttachment().getSize(), true) + ")");

            attachment = RealmAttachment.build(list.get(position).item.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT);


        }
    }

    public class MyHoldersFile extends MyHolder {

        public ImageView imvPicFile;


        public MyHoldersFile(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);

            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);


//
//
//            if (list.get(position).getAttachment() != null) {
//                imvPicFile.setImageBitmap(HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource
//                        (list.get(position).getAttachment().getLocalFilePath())));
//                txtFileName.setText(list.get(position).getAttachment().getName());
//                txtFileInfo.setText(AndroidUtils.humanReadableByteCount(
//                        list.get(position).getAttachment().getSize(), true));
//            }
        }
    }

    public class MyHoldersMusic extends MyHolder {

        public ImageView imvPicFile;
        public RealmAttachment attachment;

        public MyHoldersMusic(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
            imvPicFile.setImageResource(R.drawable.green_music_note);

            attachment = RealmAttachment.build(list.get(position).item.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT);


            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);

            if (attachment.getName() != null) {
                txtFileName.setText(attachment.getName());
            } else if (attachment.getLocalFilePath() != null) {
                txtFileName.setText(attachment.getLocalFilePath().substring(attachment.getLocalFilePath().lastIndexOf("/") + 1));
            }


            TextView txtFileSize = (TextView) itemView.findViewById(R.id.smslf_txt_file_size);
            txtFileSize.setText("(" + AndroidUtils.humanReadableByteCount(attachment.getSize(), true) + ")");

            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);

            MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
            Uri uri = (Uri) Uri.fromFile(new File(attachment.getLocalFilePath()));
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
                }
            } catch (Exception e) {
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
        }
    }


    private RealmRoomMessage isMessageExist(Long messageId) {

        RealmRoomMessage realmRoomMessages = null;


        Realm realm = Realm.getDefaultInstance();

        realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();

        return realmRoomMessages;

    }


    private boolean isFileExist(RealmRoomMessage realmRoomMessage) {
        boolean result = false;


        if (realmRoomMessage == null)
            return false;


        if (realmRoomMessage.getAttachment() != null) {

            if (realmRoomMessage.getAttachment().isFileExistsOnLocal())
                return true;


        }


        return result;
    }


    public static RealmRoomMessage putOrUpdate(ProtoGlobal.RoomMessage input) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, input.getMessageId()).findFirst();

        realm.close();


        if (message != null) {

            return message;

        } else {
            message = new RealmRoomMessage();

            message.setMessage(input.getMessage());
            message.setStatus(input.getStatus().toString());
            message.setUserId(input.getAuthor().getUser().getUserId());
            message.setDeleted(input.getDeleted());
            message.setEdited(input.getEdited());
            if (input.hasAttachment()) {
                message.setAttachment(RealmAttachment.build(input.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT));
            }
//        if (input.hasForwardFrom()) {
//            message.setForwardMessage(RealmRoomMessage.putOrUpdate(input.getForwardFrom(), getRoomId(input.getForwardFrom().getAuthor())));
//        }
//        if (input.hasLocation()) {
//            message.setLocation(RealmRoomMessageLocation.build(input.getLocation()));
//        }
//        if (input.hasLog()) {
//            message.setLog(RealmRoomMessageLog.build(input.getLog()));
//        }
//        if (input.hasReplyTo()) {
//            message.setReplyTo(RealmRoomMessage.putOrUpdate(input.getReplyTo(), getRoomId(input.getReplyTo().getAuthor())));
//        }
//        if (input.hasContact()) {
//            message.setRoomMessageContact(RealmRoomMessageContact.build(input.getContact()));
//        }
            message.setMessageType(input.getMessageType());
            message.setMessageVersion(input.getMessageVersion());
            message.setStatusVersion(input.getStatusVersion());
            message.setUpdateTime(input.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);
            message.setCreateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);

        }


        return message;
    }


}
