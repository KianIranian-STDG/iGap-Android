package com.iGap.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.helper.HelperMimeType;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by android3 on 9/4/2016.
 */
public class AdapterShearedMedia extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isSelectedMode = false;    // for determine user select some file
    private int numberOfSelected = 0;

    ArrayList<RealmRoomMessage> list;
    ArrayList<option> options;
    Context context;
    private String mediaType;
    private OnComplete complete;
    private MusicPlayer musicPlayer;
    private long roomId = 0;

    private class option {
        public boolean isSelected = false;
        public boolean isDownloading = false;
    }

    public AdapterShearedMedia(Context context, ArrayList<RealmRoomMessage> list, String mediaType, OnComplete complete, LinearLayout mediaLayout, long roomId) {
        this.context = context;
        this.list = list;
        this.mediaType = mediaType;
        this.complete = complete;
        this.roomId = roomId;

        musicPlayer = new MusicPlayer(mediaLayout);

        options = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            options.add(new option());
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        RecyclerView.ViewHolder viewHolder = null;
        boolean isHeader = false;

        if (list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.TEXT.toString()))
            isHeader = true;


        if (mediaType.equals(context.getString(R.string.shared_media))) {// picture and video

            if (isHeader) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new MyHoldersTime(view, position);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new MyHoldersImage(view, position);
            }

        } else if (mediaType.equals(context.getString(R.string.shared_files))) {// file
            if (isHeader) {
                viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new MyHoldersFile(view, position);
            }
        } else if (mediaType.equals(context.getString(R.string.shared_links))) {
            if (isHeader) {
                viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
            } else {

            }
        } else if (mediaType.equals(context.getString(R.string.shared_music))) {
            if (isHeader) {
                viewHolder = new MyHoldersTime(setLayoutHeaderTime(parent), position);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new MyHoldersMusic(view, position);
            }
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


    class LoadImageToImageView extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;


        public LoadImageToImageView(ImageView imageView, String path) {
            imv = imageView;
            this.path = path;
        }


        @Override
        protected Bitmap doInBackground(Object... params) {

            Bitmap bitmap = null;
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inSampleSize = 8;

            File file = new File(path);

            if (file.exists())
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            Log.e("ddd", "LoadImageToImageView");
            if (result != null && imv != null) {
                imv.setImageBitmap(result);
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (!list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.TEXT.toString())) {

            // set blue back ground for selected file
            FrameLayout layout = (FrameLayout) holder.itemView.findViewById(R.id.smsl_fl_contain_main);

            if (options.get(position).isSelected) {
                layout.setForeground(new ColorDrawable(Color.parseColor("#99AADFF7")));
            } else {
                layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
            }


            if (mediaType.equals(context.getString(R.string.shared_media))) {

                String path = "";

                if (list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.VIDEO.toString()) ||
                        list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.VIDEO_TEXT.toString())) {
                    path = list.get(position).getAttachment().getLocalThumbnailPath();
                    if (path == null) path = "";

                    if (path.length() < 1) {
                        ((MyHoldersImage) holder).imvPicFile.setImageResource(R.mipmap.j_video);
                    } else {
                        new HelperMimeType().loadVideoThumbnail(((MyHoldersImage) holder).imvPicFile, path);
                        //  new LoadImageToImageView(((MyHoldersImage) holder).imvPicFile, path).execute();
                    }

                } else {
                    path = list.get(position).getAttachment().getLocalFilePath();
                    if (path == null) path = "";

                    if (path.length() < 1)
                        path = list.get(position).getAttachment().getLocalThumbnailPath();

                    if (path == null) path = "";

                    if (path.length() > 0)
                        new LoadImageToImageView(((MyHoldersImage) holder).imvPicFile, path).execute();
                }

            } else if (mediaType.equals(context.getString(R.string.shared_files))) {
                MyHoldersFile m = (MyHoldersFile) holder;
                if (options.get(position).isDownloading) {
                    m.btnFileState.setText(context.getString(R.string.fa_pause));
                } else {
                    m.btnFileState.setText(context.getString(R.string.fa_arrow_down));
                }


            }
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


    public class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelectedMode) {
                        setSelectedItem(getPosition());
                    } else {
                        doSomething(getPosition());
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

        public MyHoldersImage(View itemView, int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);


            if (list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.VIDEO.toString())) {

                itemView.findViewById(R.id.smsl_ll_video).setVisibility(View.VISIBLE);

                TextView txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);
                txtVideoIcon.setTypeface(G.fontawesome);

                TextView txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);
                txtVideoTime.setText(list.get(position).getAttachment().getSize() + " ");
            }

        }

    }

    public class MyHoldersFile extends MyHolder {

        public ImageView imvPicFile;
        public Button btnFileState;

        public MyHoldersFile(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
            btnFileState = (Button) itemView.findViewById(R.id.smslf_btn_file_state);
            btnFileState.setTypeface(G.fontawesome);
            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);

            if (list.get(position).getAttachment() != null) {
                imvPicFile.setImageBitmap(HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(list.get(position).getAttachment().getLocalFilePath())));
                txtFileName.setText(list.get(position).getAttachment().getName());
                txtFileInfo.setText(Utils.humanReadableByteCount(list.get(position).getAttachment().getSize(), true));
            }
        }

    }


    public class MyHoldersMusic extends MyHolder {

        public ImageView imvPicFile;
        public Button btnFileState;

        public MyHoldersMusic(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
            imvPicFile.setImageResource(R.mipmap.j_audio);

            btnFileState = (Button) itemView.findViewById(R.id.smslf_btn_file_state);
            btnFileState.setTypeface(G.fontawesome);

            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);

            if (list.get(position).getAttachment() != null) {
                txtFileName.setText(list.get(position).getAttachment().getName());
                txtFileInfo.setText(Utils.humanReadableByteCount(list.get(position).getAttachment().getSize(), true));
            }


        }

    }


    private void startDownload(int position) {

        options.get(position).isDownloading = true;
        notifyItemChanged(position);

    }

    private void stopDownload(int position) {
        options.get(position).isDownloading = false;
        notifyItemChanged(position);
    }


    public class MyHoldersTime extends RecyclerView.ViewHolder {

        public TextView txtTime;

        public MyHoldersTime(View itemView, int position) {
            super(itemView);

            txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
            txtTime.setText(list.get(position).getMessage());

        }
    }


    //****************************************************************************************************************


    private void doSomething(int position) {


        if (false) {
            // if need dowmload get file from server
            downloadFile(position);

        } else {

            if (list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.IMAGE.toString()) ||
                    list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.IMAGE_TEXT.toString())) {
                selectImage(position);
            } else if (list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.FILE.toString()) ||
                    list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.FILE_TEXT.toString()) ||
                    list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.VIDEO.toString()) ||
                    list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.VIDEO_TEXT.toString())) {

                Intent intent = HelperMimeType.appropriateProgram(list.get(position).getAttachment().getLocalFilePath());
                if (intent != null)
                    context.startActivity(intent);
            } else if (list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.VOICE.toString()) ||
                    list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) ||
                    list.get(position).getMessageType().equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString())) {

                MusicPlayer.startPlayer(list.get(position).getAttachment().getLocalFilePath(), list.get(position).getAttachment().getName(), roomId, true);
            }

        }

    }

    private void downloadFile(int position) {

        if (options.get(position).isDownloading)
            stopDownload(position);
        else
            startDownload(position);

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
                    if (numberOfSelected < 1)
                        break;
                }
            }

        }
        complete.complete(false, "1", "0");//

        return result;
    }


    public void selectImage(int position) {
        String path = list.get(position).getAttachment().getLocalFilePath();
        showImage(path);
    }

    public static int getCountOfSheareddMedia(long roomId) {

        int counter = 0;

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAll();

        for (RealmChatHistory chatHistory : chatHistories) {
            String type = chatHistory.getRoomMessage().getMessageType();
            if (type.equals(ProtoGlobal.RoomMessageType.VOICE.toString())
                    || type.equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || type.equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.VIDEO.toString()) || type.equals(ProtoGlobal.RoomMessageType.VIDEO_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.FILE.toString()) || type.equals(ProtoGlobal.RoomMessageType.FILE_TEXT.toString()) ||
                    type.equals(ProtoGlobal.RoomMessageType.IMAGE.toString()) || type.equals(ProtoGlobal.RoomMessageType.IMAGE_TEXT.toString())) {

                counter++;
            }
        }

        realm.close();

        return counter;
    }

    private void showImage(String filePath) {

        ArrayList<StructMessageInfo> listPic = new ArrayList<>();
        int selectedPicture = 0;

        for (RealmRoomMessage mMessage : list) {
            if (mMessage.getMessageType().equals(ProtoGlobal.RoomMessageType.IMAGE.toString()) || mMessage.getMessageType().equals(ProtoGlobal.RoomMessageType.IMAGE_TEXT.toString())) {
                listPic.add(StructMessageInfo.convert(mMessage));
            }
        }


        for (int i = 0; i < listPic.size(); i++) { // determin selected image in list image
            if (listPic.get(i).attachment.getLocalFilePath().equals(filePath)) {
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

}
