package com.iGap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.activitys.ActivityShowImage;
import com.iGap.module.StructSharedMedia;
import com.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

/**
 * Created by android3 on 9/4/2016.
 */
public class AdapterShearedMedia extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int Type_Header = 1;
    public static final int Type_Item = 2;

    ArrayList<StructSharedMedia> list;
    Context context;

    public AdapterShearedMedia(Context context, ArrayList<StructSharedMedia> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == Type_Header) {//layout time
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout_time, null);
            viewHolder = new MyHoldersTime(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_media_sub_layout, null);
            viewHolder = new MyHolders(view);
        }


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == Type_Header) {
            ((MyHoldersTime) holder).txtTime.setText(list.get(position).fileTime);
        } else {
            ((MyHolders) holder).imvPicFile.setImageResource(Integer.parseInt(list.get(position).filePath));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (list.get(position).messgeType == ProtoGlobal.RoomMessageType.TEXT) {
            return Type_Header;
        } else {
            return Type_Item;
        }
    }

    public class MyHolders extends RecyclerView.ViewHolder {

        public ImageView imvPicFile;

        public MyHolders(View itemView) {
            super(itemView);

            imvPicFile = (ImageView) itemView.findViewById(R.id.smsl_imv_file_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "item click  " + getPosition());

                    if (list.get(getPosition()).messgeType == ProtoGlobal.RoomMessageType.IMAGE) {
                        selectImage(getPosition());
                    }



                }
            });
        }

    }

    public class MyHoldersTime extends RecyclerView.ViewHolder {

        public TextView txtTime;

        public MyHoldersTime(View itemView) {
            super(itemView);

            txtTime = (TextView) itemView.findViewById(R.id.smslt_txt_time);
        }
    }


    //****************************************************************************************************************


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


        Intent intent = new Intent(context, ActivityShowImage.class); // run activity show image k
        intent.putExtra("listPic", listPic);
        intent.putExtra("SelectedImage", selectedImage);
        context.startActivity(intent);
    }

}
