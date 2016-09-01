package com.iGap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private int replayCommentNumber = -1;

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

        public MyViewHolder(final View itemView, final int position) {
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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "comment click  " + position);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Log.e("ddd", "long comment click  " + position);


                    if (replayCommentNumber == -1) {
                        visibleLayoutReplay(position, itemView);

                    } else if (replayCommentNumber == position) {
                        goneLayoutReplay(position, itemView);
                    } else {
                        goneLayoutReplay(replayCommentNumber, itemView);
                        visibleLayoutReplay(position, itemView);
                    }

                    return true;
                }
            });



            if (list.get(position).replayMessageList != null) {

                int count = list.get(position).replayMessageList.size();
                if (count > 0) {

                    LinearLayout layout = (LinearLayout) itemView.findViewById(R.id.csl_ll_add_replay);

                    for (int i = 0; i < count && i < 3; i++) {

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

                        v.setOnLongClickListener(null);


                        layout.addView(v);

                    }

                    if (count > 2) {
                        View vMore = LayoutInflater.from(context).inflate(R.layout.comment_sub_layout_more, null, false);

                        vMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("ddd", " btn more clicked");
                            }
                        });

                        layout.addView(vMore);
                    }


                }
            }
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (list.get(position).isChange) {
            updateSomething(position, holder.itemView);
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


    //************************************************************************************************

    private void updateSomething(int position, View holder) {

        Log.e("ddd", "update something " + position + "   " + replayCommentNumber);

        for (int i = 0; i < list.get(position).allChanges.size(); i++) {

            int whatUpdate = Integer.parseInt(list.get(position).allChanges.get(i));

            switch (whatUpdate) {

                case 1: // gone layout replay
                    holder.findViewById(R.id.csl_ll_replay_comment).setVisibility(View.GONE);
                    holder.findViewById(R.id.csl_img_comment_sender_picture).setVisibility(View.VISIBLE);
                    holder.findViewById(R.id.csl_ll_comment).setBackgroundColor(Color.WHITE);
                    break;
            }


        }

        list.get(position).isChange = false;
        list.get(position).allChanges.clear();

    }


    private void visibleLayoutReplay(int position, View itemView) {

        replayCommentNumber = position;
        itemView.findViewById(R.id.csl_img_comment_sender_picture).setVisibility(View.GONE);
        itemView.findViewById(R.id.csl_ll_replay_comment).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.csl_ll_comment).setBackgroundColor(Color.parseColor("#999999"));


    }

    private void goneLayoutReplay(int position, View itemView) {

        list.get(position).isChange = true;
        list.get(position).allChanges.add("1");
        replayCommentNumber = -1;


        notifyItemChanged(position);
    }




}
