package com.iGap.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.helper.HelperMimeType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructSharedMedia;
import com.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

/**
 * Created by android3 on 9/4/2016.
 */
public class AdapterShearedMedia extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isSelectedMode = false;    // for determine user select some file
    private int numberOfSelected = 0;

    ArrayList<StructSharedMedia> list;
    Context context;
    private String mediaType;
    private OnComplete complete;

    public AdapterShearedMedia(Context context, ArrayList<StructSharedMedia> list, String mediaType, OnComplete complete) {
        this.context = context;
        this.list = list;
        this.mediaType = mediaType;
        this.complete = complete;


        Log.e("ddd", mediaType);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        RecyclerView.ViewHolder viewHolder = null;
        boolean isHeader = false;

        if (list.get(position).messgeType == ProtoGlobal.RoomMessageType.TEXT)
            isHeader = true;


        if (mediaType.equals(context.getString(R.string.shared_media))) {// picture and video

            if (isHeader) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                viewHolder = new MyHoldersTime(view, position);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_image, null);
                viewHolder = new MyHoldersImage(view, position);
            }

        } else if (mediaType.equals(context.getString(R.string.shared_files))) {
            if (isHeader) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                view.setBackgroundColor(Color.parseColor("#cccccc"));
                viewHolder = new MyHoldersTime(view, position);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_file, null);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                viewHolder = new MyHoldersFile(view, position);
            }
        } else if (mediaType.equals(context.getString(R.string.shared_links))) {
            if (isHeader) {

            } else {

            }
        } else if (mediaType.equals(context.getString(R.string.shared_music))) {
            if (isHeader) {

            } else {

            }
        }


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (list.get(position).messgeType != ProtoGlobal.RoomMessageType.TEXT) {// set blue back ground for selected file
            FrameLayout layout = (FrameLayout) holder.itemView.findViewById(R.id.smsl_fl_contain_main);

            if (list.get(position).isSelected) {
                layout.setForeground(new ColorDrawable(Color.parseColor("#99AADFF7")));
            } else {
                layout.setForeground(new ColorDrawable(Color.TRANSPARENT));
            }
        }


        if (mediaType.equals(context.getString(R.string.shared_files)) && list.get(position).messgeType == ProtoGlobal.RoomMessageType.FILE) {
            MyHoldersFile m = (MyHoldersFile) holder;
            if (list.get(position).isDownloading) {
                m.btnFileState.setText(context.getString(R.string.fa_pause));
            } else {
                m.btnFileState.setText(context.getString(R.string.fa_arrow_down));
            }

            //// TODO: 9/7/2016 nejati     get picture thumbnile if file image of video or music    use HelperMimeType

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
            imvPicFile.setImageResource(Integer.parseInt(list.get(position).filePath));

            if (list.get(position).messgeType == ProtoGlobal.RoomMessageType.VIDEO) {

                itemView.findViewById(R.id.smsl_ll_video).setVisibility(View.VISIBLE);

                TextView txtVideoIcon = (TextView) itemView.findViewById(R.id.smsl_txt_video_icon);
                txtVideoIcon.setTypeface(G.fontawesome);

                TextView txtVideoTime = (TextView) itemView.findViewById(R.id.smsl_txt_video_time);
                txtVideoTime.setText(list.get(position).fileInfo);
            }

        }

    }

    public class MyHoldersFile extends MyHolder {

        public ImageView imvPicFile;
        public Button btnFileState;

        public MyHoldersFile(View itemView, final int position) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smslf_imv_icon_file);
            imvPicFile.setImageBitmap(HelperMimeType.getMimePic(context, HelperMimeType.getMimeResource(list.get(position).filePath)));

            btnFileState = (Button) itemView.findViewById(R.id.smslf_btn_file_state);
            btnFileState.setTypeface(G.fontawesome);

            TextView txtFileName = (TextView) itemView.findViewById(R.id.smslf_txt_file_name);
            txtFileName.setText(list.get(position).fileName);

            TextView txtFileInfo = (TextView) itemView.findViewById(R.id.smslf_txt_file_info);
            txtFileInfo.setText(list.get(position).fileInfo);

        }

    }

    private void startDownload(int position) {

        list.get(position).isDownloading = true;
        notifyItemChanged(position);

    }

    private void stopDownload(int position) {
        list.get(position).isDownloading = false;
        notifyItemChanged(position);
    }


    public class MyHoldersTime extends RecyclerView.ViewHolder {

        public TextView txtTime;

        public MyHoldersTime(View itemView, int position) {
            super(itemView);

            txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
            txtTime.setText(list.get(position).fileTime);

        }
    }


    //****************************************************************************************************************


    private void doSomething(int position) {

        if (list.get(position).messgeType == ProtoGlobal.RoomMessageType.IMAGE) {
            selectImage(position);
        } else if (list.get(position).messgeType == ProtoGlobal.RoomMessageType.FILE) {
            if (list.get(position).isDownloading)
                stopDownload(position);
            else
                startDownload(position);
        }

    }


    private void setSelectedItem(int position) {

        if (list.get(position).isSelected == false) {
            list.get(position).isSelected = true;
            numberOfSelected++;
        } else {
            list.get(position).isSelected = false;
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
                if (list.get(i).isSelected) {
                    list.get(i).isSelected = false;
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

        String path = list.get(position).filePath;

        showImage(path);


//        if(path.length()>0){
//            File file=new File(path);
//            if(file.exists()){
//                showImage(path);
//            }else{
//                downloadFile();
//            }
//        }
//        else{
//            downloadFile();
//        }


    }


    private void downloadFile() {

        // TODO: 9/6/2016   write download class

    }

    private void showImage(String filePath) {

        ArrayList<StructSharedMedia> listPic = new ArrayList<>();

        int selectedImage = -1;

        for (int i = 0; i < list.size(); i++) {// get list image from list files
            if (list.get(i).messgeType == ProtoGlobal.RoomMessageType.IMAGE)
                listPic.add(list.get(i));
        }


        for (int i = 0; i < listPic.size(); i++) { // determin selected image in list image
            if (listPic.get(i).filePath.equals(filePath)) {
                selectedImage = i;
                break;
            }
        }

        Fragment fragment = FragmentShowImage.newInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable("listPic", listPic);
        bundle.putInt("SelectedImage", selectedImage);
        fragment.setArguments(bundle);

        ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.asm_ll_parent, fragment).commit();

    }

}
