package com.iGap.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityComment;
import com.iGap.interface_package.OnMessageClick;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomRoundCornerImageView;
import com.iGap.module.GifMovieView;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructMessageInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/5/2016.
 */
public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StructMessageInfo> list;
    private Context context;
    private MyType.ChatType chatType;
    private OnComplete complete;
    private OnMessageClick mOnMessageClick;

    private boolean isSelectedMode = false;
    private int numberOfSelected = 0;

    public AdapterChat(Context context, MyType.ChatType chatType, ArrayList<StructMessageInfo> list, OnComplete complete, OnMessageClick onMessageClick) {
        this.list = list;
        this.context = context;
        this.chatType = chatType;
        this.complete = complete;
        mOnMessageClick = onMessageClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        View main = null;

        // inflate layout send or recive
        if (list.get(viewType).sendType == MyType.SendType.timeLayout) {// inflate time layout
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_date_time, null);
            viewHolder = new holdrNoAction(main);
            return viewHolder;
        } else if (chatType == MyType.ChatType.channel) {//inflate layout channel
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_channel, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            main.setLayoutParams(lp);
        } else if (list.get(viewType).sendType == MyType.SendType.send) {// inflate layout send
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_send, null);
        } else {//inflate layout recive
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_recive, null);
        }


        if (list.get(viewType).messag.length() > 0) {
            TextView txtMessage = (TextView) main.findViewById(R.id.cslr_txt_message);
            txtMessage.setVisibility(View.VISIBLE);
            txtMessage.setText(list.get(viewType).messag);
        }



        // add layout forward message to layout
        if (list.get(viewType).forwardMessageFrom.length() > 0 && chatType != MyType.ChatType.channel) {
            main.findViewById(R.id.cslr_ll_forward).setVisibility(View.VISIBLE);
            TextView txtForwardMessage = (TextView) main.findViewById(R.id.cslr_txt_forward_from);
            txtForwardMessage.setText("Forward From " + list.get(viewType).forwardMessageFrom);
        }


        // set layout mergin for singlechat
        if (chatType == MyType.ChatType.singleChat) {

            if (list.get(viewType).sendType == MyType.SendType.recvive) {// gone avatar sernder message and set layout mergin
                main.findViewById(R.id.cslr_imv_sender_picture).setVisibility(View.GONE);

                FrameLayout frameLayout = (FrameLayout) main.findViewById(R.id.cslr_ll_frame);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                frameLayout.setLayoutParams(params);

                LinearLayout linearLayout = (LinearLayout) main.findViewById(R.id.cslr_ll_forward);
                LinearLayout.LayoutParams paramsa = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
                paramsa.setMargins(0, 0, 0, 0);
                linearLayout.setLayoutParams(paramsa);
            }
        }


        LinearLayout frameLayout = (LinearLayout) main.findViewById(R.id.cslr_ll_content_main);// layout for add other layot in it

        if (chatType != MyType.ChatType.channel)
            if (list.get(viewType).sendType == MyType.SendType.send)
                ((TextView) main.findViewById(R.id.cslr_txt_tic)).setTypeface(G.fontawesome);


        //add layout replay to layout
        if (list.get(viewType).replayFrom.length() > 0)
            addLayoutReplay(inflater, frameLayout, viewType);


        // add item layout
        switch (list.get(viewType).messageType.getValue()) {
            case MyType.message:
                viewHolder = new myHolder(main);
                break;
            case MyType.image:
                v = inflater.inflate(R.layout.chat_sub_layout_image, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderImage(main);
                configureViewHolderImage((viewHolderImage) viewHolder, viewType);
                break;
            case MyType.video:
                v = inflater.inflate(R.layout.chat_sub_layout_video, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderVideo(main);
                configureViewHolderVideo((viewHolderVideo) viewHolder, viewType);
                break;
            case MyType.files:
                v = inflater.inflate(R.layout.chat_sub_layout_file, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderFile(main);
                configureViewHolderFile((viewHolderFile) viewHolder, viewType);
                break;
            case MyType.audio:
                v = inflater.inflate(R.layout.chat_sub_layout_audio, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderAudio(main);
                configureViewHolderAudio((viewHolderAudio) viewHolder, viewType);
                break;
            case MyType.gif:
                v = inflater.inflate(R.layout.chat_sub_layout_gif, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderGif(main);
                configureViewHolderGif((viewHolderGif) viewHolder, viewType);
                break;
            case MyType.sticker:
                v = inflater.inflate(R.layout.chat_sub_layout_sticker, parent, false);
                frameLayout.addView(v);
                ImageView imvSticker = (ImageView) v.findViewById(R.id.cslst_imv_sticker);
                imvSticker.setImageResource(Integer.parseInt(list.get(viewType).filePath));
                if (chatType == MyType.ChatType.channel)
                    main.findViewById(R.id.cslch_ll_parent).setBackgroundColor(Color.TRANSPARENT);
                else
                    main.findViewById(R.id.cslr_ll_frame).setBackgroundColor(Color.TRANSPARENT);
                viewHolder = new myHolder(main);
                break;
        }


        //set background layout time in single chat or group chat
        if (chatType != MyType.ChatType.channel) {
            if (((list.get(viewType).messageType == MyType.MessageType.image || list.get(viewType).messageType == MyType.MessageType.gif)
                    && list.get(viewType).messag == "") || list.get(viewType).messageType == MyType.MessageType.sticker) {

                LinearLayout layoutTime = (LinearLayout) main.findViewById(R.id.cslr_ll_time);
                layoutTime.setBackgroundResource(R.drawable.recangle_gray_tranceparent);

                FrameLayout.LayoutParams timeParaams = (FrameLayout.LayoutParams) layoutTime.getLayoutParams();
                timeParaams.setMargins(0, 0, 6, 6);
                layoutTime.setLayoutParams(timeParaams);

                ((TextView) main.findViewById(R.id.cslr_txt_time)).setTextColor(Color.WHITE);
            }
        }


        if (chatType == MyType.ChatType.channel && list.get(viewType).sendType != MyType.SendType.timeLayout)
            configureLayoutChannel(viewHolder, viewType);


        // set sender image in group chat
        if (chatType == MyType.ChatType.groupChat) {
            if (list.get(viewType).sendType == MyType.SendType.recvive) {
                if (list.get(viewType).senderAvatar.length() > 0) {
                    CircleImageView imvSenderAvatar = (CircleImageView) viewHolder.itemView.findViewById(R.id.cslr_imv_sender_picture);
                    imvSenderAvatar.setImageResource(Integer.parseInt(list.get(viewType).senderAvatar));
                }
            }
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (list.get(position).isChange) {
            UpdateSomething(position, holder);
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

    //***********************************************************************************************************

    class holdrNoAction extends RecyclerView.ViewHolder {

        public holdrNoAction(View itemView) {
            super(itemView);
        }
    }

    class myHolder extends RecyclerView.ViewHolder {

        public myHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = myHolder.super.getPosition();

                    if (isSelectedMode) {
                        setIsSelectedItem(position);
                    } else {
                        // when message item clicked
                        if (mOnMessageClick != null) {
                            mOnMessageClick.onMessageClick(view, list.get(getAdapterPosition()));
                        }
                    }

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int position = myHolder.super.getPosition();
                    isSelectedMode = true;

                    setIsSelectedItem(position);

                    Log.e("ddd", " item   " + numberOfSelected + "   numbrer of selected   onlong");

                    return true;
                }
            });
        }
    }

    class viewHolderFile extends myHolder {

        public ImageView imvImageFile;
        public ImageView imvStateFile;
        public TextView txtFileName;
        public TextView txtFileMimeType;
        public TextView txtFileSize;

        public viewHolderFile(View itemView) {
            super(itemView);

            imvImageFile = (ImageView) itemView.findViewById(R.id.cslf_imv_image_file);
            imvStateFile = (ImageView) itemView.findViewById(R.id.cslf_imv_state_file);
            txtFileName = (TextView) itemView.findViewById(R.id.cslf_txt_file_name);
            txtFileMimeType = (TextView) itemView.findViewById(R.id.cslf_txt_file_mime_type);
            txtFileSize = (TextView) itemView.findViewById(R.id.cslf_txt_file_size);
        }
    }

    class viewHolderVideo extends myHolder {

        public ImageView imvImageVideo;
        public ImageButton imbPlayVideo;
        public TextView txtVideoName;
        public TextView txtVideoMimeType;
        public TextView txtVideoInfo;

        public viewHolderVideo(View itemView) {
            super(itemView);

            imvImageVideo = (ImageView) itemView.findViewById(R.id.cslv_imv_vido_image);
            imbPlayVideo = (ImageButton) itemView.findViewById(R.id.cslv_btn_play_video);
            txtVideoName = (TextView) itemView.findViewById(R.id.cslv_txt_video_name);
            txtVideoMimeType = (TextView) itemView.findViewById(R.id.cslv_txt_video_mime_type);
            txtVideoInfo = (TextView) itemView.findViewById(R.id.cslv_txt_vido_info);
        }
    }

    class viewHolderAudio extends myHolder {

        public ImageView imvStateAudio;
        public TextView txtAudioName;
        public TextView txtAudioMimeType;
        public TextView txtAudioSize;
        public Button btnAudioMenu;

        public viewHolderAudio(View itemView) {
            super(itemView);

            imvStateAudio = (ImageView) itemView.findViewById(R.id.csla_imv_state_audio);
            txtAudioName = (TextView) itemView.findViewById(R.id.csla_txt_audio_name);
            txtAudioMimeType = (TextView) itemView.findViewById(R.id.csla_txt_audio_mime_type);
            txtAudioSize = (TextView) itemView.findViewById(R.id.csla_txt_audio_size);

            btnAudioMenu = (Button) itemView.findViewById(R.id.csla_btn_audio_menu);
            btnAudioMenu.setTypeface(G.fontawesome);
            btnAudioMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "click audio menu");
                }
            });
        }
    }

    class viewHolderImage extends myHolder {

        public ImageView imvPicture;

        public viewHolderImage(View itemView) {
            super(itemView);
            imvPicture = (ImageView) itemView.findViewById(R.id.shli_imv_image);
        }
    }

    class viewHolderGif extends myHolder {

        public GifMovieView gifview;
        public ImageButton btnPlay;

        public viewHolderGif(View itemView) {
            super(itemView);

            gifview = (GifMovieView) itemView.findViewById(R.id.cslg_gifview);
            btnPlay = (ImageButton) itemView.findViewById(R.id.cslg_btn_play);
        }
    }

    //***********************************************************************************************************

    private void configureLayoutChannel(RecyclerView.ViewHolder holder, final int position) {

        TextView txtTimeDate = (TextView) holder.itemView.findViewById(R.id.cslch_txt_time_date);
        TextView txtTimeClock = (TextView) holder.itemView.findViewById(R.id.cslch_txt_time_clock);
        TextView txtImageLike = (TextView) holder.itemView.findViewById(R.id.cslch_txt_image_like);
        TextView txtlike = (TextView) holder.itemView.findViewById(R.id.cslch_txt_like);
        TextView txtImageUnlike = (TextView) holder.itemView.findViewById(R.id.cslch_txt_image_unlike);
        TextView txtLike = (TextView) holder.itemView.findViewById(R.id.cslch_txt_like);
        TextView txtImageComment = (TextView) holder.itemView.findViewById(R.id.cslch_txt_image_comment);
        TextView txtComment = (TextView) holder.itemView.findViewById(R.id.cslch_txt_comment);
        TextView txtImageSeen = (TextView) holder.itemView.findViewById(R.id.cslch_txt_image_seen);
        TextView txtSeen = (TextView) holder.itemView.findViewById(R.id.cslch_txt_seen);

        txtImageLike.setTypeface(G.fontawesome);
        txtImageUnlike.setTypeface(G.fontawesome);
        txtImageComment.setTypeface(G.fontawesome);
        txtImageSeen.setTypeface(G.fontawesome);

        Button btnMenu = (Button) holder.itemView.findViewById(R.id.cslch_btn_item_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "item menu click " + position);
            }
        });

        LinearLayout layout_like = (LinearLayout) holder.itemView.findViewById(R.id.cslch_ll_like);
        layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "like click " + position);
            }
        });

        LinearLayout layout_Unlike = (LinearLayout) holder.itemView.findViewById(R.id.cslch_ll_unlike);
        layout_Unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "Unclick " + position);
            }
        });

        LinearLayout layout_Comment = (LinearLayout) holder.itemView.findViewById(R.id.cslch_ll_comment);
        layout_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "omment click " + position);


                Intent intent = new Intent(context, ActivityComment.class);
                intent.putExtra("MessageID", list.get(position).messageID);

                context.startActivity(intent);

            }
        });


        if (list.get(position).messageType == MyType.MessageType.image)
            holder.itemView.findViewById(R.id.cslr_ll_content_main).setPadding(0, 0, 0, 0);


        if (list.get(position).messageType == MyType.MessageType.audio) {// gone btn elipse_v audio
            holder.itemView.findViewById(R.id.csla_btn_audio_menu).setVisibility(View.GONE);
        } else if (list.get(position).messageType == MyType.MessageType.sticker) {// inivsible layout infor and menu in sticker
            holder.itemView.findViewById(R.id.cslch_ll_info).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.cslch_btn_item_menu).setVisibility(View.INVISIBLE);
        }


    }

    private void configureViewHolderFile(viewHolderFile holder, int position) {


    }

    private void configureViewHolderVideo(final viewHolderVideo holder, int position) {

        holder.itemView.findViewById(R.id.cslr_ll_content_main).setPadding(0, 0, 0, 0);

        holder.txtVideoName.setText(list.get(position).fileName);
        holder.txtVideoMimeType.setText(list.get(position).fileMime);
        holder.txtVideoInfo.setText(list.get(position).fileInfo);

        holder.imvImageVideo.setImageResource(Integer.parseInt(list.get(position).filePic));


        MyType.FileState fs = list.get(position).fileState;

        if (fs == MyType.FileState.notDownload || fs == MyType.FileState.downloading)// enabel or disable btn play video
            holder.imbPlayVideo.setVisibility(View.GONE);
        else
            holder.imbPlayVideo.setVisibility(View.VISIBLE);

        holder.imbPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " play video clicked");
            }
        });


        if (chatType == MyType.ChatType.channel) {  // for round the corner of image
            ViewTreeObserver vto = holder.imvImageVideo.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    holder.imvImageVideo.getViewTreeObserver().removeOnPreDrawListener(this);

                    int imageWith = holder.imvImageVideo.getWidth();
                    int imageHeight = holder.imvImageVideo.getHeight();

                    int layoutWith = ((View) holder.imvImageVideo.getParent().getParent()).getWidth();
                    int layoutHeight = ((View) holder.imvImageVideo.getParent().getParent()).getHeight();

                    if (imageWith == layoutWith) {
                        int radiousTop = 0;
                        int radiousBottom = 0;
                        CustomRoundCornerImageView view = (CustomRoundCornerImageView) holder.imvImageVideo;
                        radiousTop = (int) context.getResources().getDimension(R.dimen.dp4);
                        if (imageHeight == layoutHeight)
                            radiousBottom = radiousTop;

                        if (radiousTop > 0 || radiousBottom > 0)
                            view.setCornerRadiiDP(radiousTop, 0, radiousBottom, 0);

                    }

                    return true;
                }
            });
        }


    }

    private void configureViewHolderAudio(viewHolderAudio holder, int position) {


    }

    private void configureViewHolderImage(final viewHolderImage holder, final int position) {
        holder.imvPicture.setImageResource(Integer.parseInt(list.get(position).filePath));

        if (chatType == MyType.ChatType.channel) {
            ViewTreeObserver vto = holder.imvPicture.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    holder.imvPicture.getViewTreeObserver().removeOnPreDrawListener(this);

                    int imageWith = holder.imvPicture.getWidth();
                    int imageHeight = holder.imvPicture.getHeight();

                    int layoutWith = ((View) holder.imvPicture.getParent().getParent().getParent()).getWidth();
                    int layoutHeight = ((View) holder.imvPicture.getParent().getParent().getParent()).getHeight();

                    if (imageWith == layoutWith) {
                        int radiousTop = 0;
                        int radiousBottom = 0;
                        CustomRoundCornerImageView view = (CustomRoundCornerImageView) holder.imvPicture;
                        radiousTop = (int) context.getResources().getDimension(R.dimen.dp4);
                        if (imageHeight == layoutHeight)
                            radiousBottom = radiousTop;

                        if (radiousTop > 0 || radiousBottom > 0)
                            view.setCornerRadiiDP(radiousTop, 0, radiousBottom, 0);

                    }

                    return true;
                }
            });
        }

    }

    private void configureViewHolderGif(final viewHolderGif holder, final int position) {

        holder.gifview.setMovieResource(R.drawable.anim2);
        holder.gifview.setPaused(true);

        if (holder.gifview.isPaused())
            holder.btnPlay.setVisibility(View.VISIBLE);
        else holder.btnPlay.setVisibility(View.GONE);


        holder.gifview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isSelectedMode) {
                    setIsSelectedItem(position);
                } else {
                    holder.gifview.setPaused(true);
                    holder.gifview.requestLayout();
                    holder.btnPlay.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.gifview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                isSelectedMode = true;
                setIsSelectedItem(position);

                return true;
            }
        });

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("ddd", "btn gif click");

                holder.gifview.setPaused(false);
                holder.btnPlay.setVisibility(View.GONE);


            }
        });


    }

    /**
     * add layout replay to layout
     */
    private void addLayoutReplay(LayoutInflater inflater, LinearLayout layout, final int position) {
        View v = inflater.inflate(R.layout.chat_sub_layout_replay, null, false);
        TextView txtReplayFrom = (TextView) v.findViewById(R.id.chslr_txt_replay_from);
        TextView txtReplayMessage = (TextView) v.findViewById(R.id.chslr_txt_replay_message);
        ImageView imv;

        txtReplayFrom.setText(list.get(position).replayFrom);
        txtReplayMessage.setText(list.get(position).replayMessage);

        if (list.get(position).replayPicturePath.length() > 0) {
            imv = (ImageView) v.findViewById(R.id.chslr_imv_replay_pic);
            imv.setVisibility(View.VISIBLE);

            //ToDo  set image from file path
        }

        layout.addView(v);

    }

    //********************************************************************************************************
    private void setIsSelectedItem(int position) {

        list.get(position).isChange = true;
        list.get(position).allChanges.add("1");

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

    /**
     * reset all background color item in list
     *
     * @return
     */
    public boolean resetSelected() {

        boolean result = isSelectedMode;

        if (isSelectedMode == true) {
            isSelectedMode = false;

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected) {
                    list.get(i).isSelected = false;
                    list.get(i).isChange = true;
                    list.get(i).allChanges.add("1");
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

    /**
     * set background color for evry item
     *
     * @param position
     */
    private void setBackGroundLayout(int position, RecyclerView.ViewHolder holder) {

        int color;

        if (list.get(position).isSelected) {
            color = Color.parseColor("#99AADFF7");
        } else {
            color = Color.TRANSPARENT;
        }

        Drawable drawable = new ColorDrawable(color);
        ((FrameLayout) holder.itemView.findViewById(R.id.cslr_ll_frame)).setForeground(drawable);
        holder.itemView.setBackgroundColor(color);
    }


    private void UpdateSomething(int position, RecyclerView.ViewHolder holder) {


        for (int i = 0; i < list.get(position).allChanges.size(); i++) {

            int whatUpdate = Integer.parseInt(list.get(position).allChanges.get(i));

            switch (whatUpdate) {

                case 1: // update background layout
                    if (list.get(position).sendType != MyType.SendType.timeLayout)
                        setBackGroundLayout(position, holder);
                    break;
            }

        }


        list.get(position).isChange = false;
        list.get(position).allChanges.clear();


    }


}
