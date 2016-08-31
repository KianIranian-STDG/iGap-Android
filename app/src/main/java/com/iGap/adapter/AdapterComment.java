package com.iGap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.StructCommentInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/31/2016.
 */
public class AdapterComment extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StructCommentInfo> list;
    private Context context;

    public AdapterComment(Context context, ArrayList<StructCommentInfo> list) {
        this.list = list;
        this.context = context;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_sub_layout, parent, false);
        return new MyViewHolder(itemView, viewType);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imvSenderPicture;
        public TextView txtMessage;
        public TextView txtDate;
        public TextView txtClock;

        public MyViewHolder(View itemView, int position) {
            super(itemView);

            imvSenderPicture = (CircleImageView) itemView.findViewById(R.id.csl_img_comment_sender_picture);
            txtMessage = (TextView) itemView.findViewById(R.id.csl_txt_message);
            txtDate = (TextView) itemView.findViewById(R.id.csl_txt_date);
            txtClock = (TextView) itemView.findViewById(R.id.csl_txt_clock);


            if (list.get(position).senderPicturePath.length() > 0) {
                imvSenderPicture.setImageResource(Integer.parseInt(list.get(position).senderPicturePath));
            } else {
                imvSenderPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp60), list.get(position).senderName, ""));
            }


            txtMessage.setText(list.get(position).message);

            txtDate.setText(list.get(position).date);
            txtClock.setText(list.get(position).time);


            if (list.get(position).replayMessageList != null) {

                int count = list.get(position).replayMessageList.size();
                if (count > 0) {

                    LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.csl_ll_replay_comment);

                    for (int i = 0; i < count; i++) {

                        View v = LayoutInflater.from(context).inflate(R.layout.comment_sub_layout, null, false);

                        StructCommentInfo infoReplay = list.get(position).replayMessageList.get(i);


                        CircleImageView imvSenderPictureReplay = (CircleImageView) v.findViewById(R.id.csl_img_comment_sender_picture);
                        TextView txtMessageReplay = (TextView) v.findViewById(R.id.csl_txt_message);
                        TextView txtDateReplay = (TextView) v.findViewById(R.id.csl_txt_date);
                        TextView txtClockReplay = (TextView) v.findViewById(R.id.csl_txt_clock);


                        if (infoReplay.senderPicturePath.length() > 0) {
                            imvSenderPictureReplay.setImageResource(Integer.parseInt(infoReplay.senderPicturePath));
                        } else {
                            imvSenderPictureReplay.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp60), infoReplay.senderName, ""));
                        }


                        txtMessageReplay.setText(infoReplay.message);

                        txtDateReplay.setText(infoReplay.date);
                        txtClockReplay.setText(infoReplay.time);


                        layout.addView(v);

                    }


                }
            }
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
