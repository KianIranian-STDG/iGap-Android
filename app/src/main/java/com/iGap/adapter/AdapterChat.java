package com.iGap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.CircleImageView;
import com.iGap.module.GifMovieView;
import com.iGap.module.MyType;
import com.iGap.module.StructChatInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/5/2016.
 */
public class AdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<StructChatInfo> list;
    private Context context;
    private MyType.ChatType chatType;

    private boolean isSelectedMode = false;
    private boolean isSetBackGroundLayout = false;
    private int numberOfSelected = 0;

    public AdapterChat(Context context, MyType.ChatType chatType, ArrayList<StructChatInfo> list) {
        this.list = list;
        this.context = context;
        this.chatType = chatType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        View main = null;

        // inflate layout send or recive
        if (list.get(viewType).sendType == MyType.SendType.timeLayout) {
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_date_time, null);
            viewHolder = new holdrNoAction(main);
            return viewHolder;
        } else if (list.get(viewType).sendType.getValue() == MyType.sendLayot || chatType == MyType.ChatType.channel) {
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_send, null);
        } else {
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_recive, null);
        }


        // add layout forward message to layout
        if (list.get(viewType).forwardMessageFrom.length() > 0) {
            main.findViewById(R.id.cslr_ll_forward).setVisibility(View.VISIBLE);
            TextView txtForwardMessage = (TextView) main.findViewById(R.id.cslr_txt_forward_from);
            txtForwardMessage.setText("Forward From " + list.get(viewType).forwardMessageFrom);

        }

        // set layout mergin for group chat or message or channel
        if (chatType == MyType.ChatType.singleChat) {

            if (list.get(viewType).sendType == MyType.SendType.recvive) {
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

        } else if (chatType == MyType.ChatType.groupChat) {

        } else if (chatType == MyType.ChatType.channel) {
            main.findViewById(R.id.cslr_view_distance).setVisibility(View.GONE);
            if (list.get(viewType).sendType == MyType.SendType.recvive)
                main.findViewById(R.id.cslr_txt_tic).setVisibility(View.GONE);
        }


        LinearLayout frameLayout = (LinearLayout) main.findViewById(R.id.cslr_ll_content_main);

        if (list.get(viewType).sendType == MyType.SendType.send)
            ((TextView) main.findViewById(R.id.cslr_txt_tic)).setTypeface(G.fontawesome);


        //add layout replay to layout
        if (list.get(viewType).replayFrom.length() > 0)
            addLayoutReplay(inflater, frameLayout, viewType);


        // add item layout
        switch (list.get(viewType).messageType.getValue()) {

            case MyType.message:
                viewHolder = new viewHolderMessage(main);
                break;
            case MyType.image:
                v = inflater.inflate(R.layout.chat_sub_layout_image, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderImage(main);
                break;
            case MyType.files:
                v = inflater.inflate(R.layout.chat_sub_layout_file, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderFile(main);
                break;
            case MyType.audio:
                v = inflater.inflate(R.layout.chat_sub_layout_audio, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderAudio(main);
                break;
            case MyType.gif:
                v = inflater.inflate(R.layout.chat_sub_layout_gif, parent, false);
                frameLayout.addView(v);
                viewHolder = new viewHolderGif(main);
                break;
            case MyType.sticker:
                v = inflater.inflate(R.layout.chat_sub_layout_sticker, parent, false);
                frameLayout.addView(v);
                ImageView imvSticker = (ImageView) v.findViewById(R.id.cslst_imv_sticker);
                imvSticker.setImageResource(Integer.parseInt(list.get(viewType).filePath));
                viewHolder = new myHolder(main);
                break;
        }

        //add layout text to view
        if (list.get(viewType).messag.length() > 0) {
            v = inflater.inflate(R.layout.chat_sub_layout_message, parent, false);
            ((TextView) v.findViewById(R.id.cslm_txt_message)).setText(list.get(viewType).messag);
            if (list.get(viewType).sendType == MyType.SendType.recvive && chatType == MyType.ChatType.groupChat && list.get(viewType).messageType == MyType.MessageType.message)
                v.setPadding((int) context.getResources().getDimension(R.dimen.dp24), 0, 0, 0);
            frameLayout.addView(v);
        }


        //set background layout time
        if (((list.get(viewType).messageType == MyType.MessageType.image || list.get(viewType).messageType == MyType.MessageType.gif)
                && list.get(viewType).messag == "" && chatType != MyType.ChatType.channel) || list.get(viewType).messageType == MyType.MessageType.sticker) {

            LinearLayout layoutTime = (LinearLayout) main.findViewById(R.id.cslr_ll_time);
            layoutTime.setBackgroundResource(R.drawable.recangle_gray_tranceparent);

            FrameLayout.LayoutParams timeParaams = (FrameLayout.LayoutParams) layoutTime.getLayoutParams();
            timeParaams.setMargins(0, 0, 6, 6);
            layoutTime.setLayoutParams(timeParaams);

            ((TextView) main.findViewById(R.id.cslr_txt_time)).setTextColor(Color.WHITE);
        }

        setBackGroundLayout(viewType, (FrameLayout) main.findViewById(R.id.cslr_ll_frame));


        //add layout link
        if (chatType == MyType.ChatType.channel && list.get(viewType).messageType != MyType.MessageType.sticker)
            addLayoutLink(inflater, frameLayout, viewType);

        main.findViewById(R.id.cslr_ll_time).bringToFront();

        return viewHolder;
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

    /**
     * set background color for evry item
     *
     * @param position
     * @param layout
     */
    private void setBackGroundLayout(int position, FrameLayout layout) {

        Drawable drawable = null;

        if (list.get(position).isSelected) {
            drawable = context.getResources().getDrawable(R.drawable.rectangle_round_yellow);
        } else if (list.get(position).messageType == MyType.MessageType.sticker) {
            drawable = new ColorDrawable(Color.TRANSPARENT);
            ;
        } else if (list.get(position).sendType == MyType.SendType.send) {
            drawable = context.getResources().getDrawable(R.drawable.rectangle_round_white);
        } else if (list.get(position).sendType == MyType.SendType.recvive) {
            drawable = context.getResources().getDrawable(R.drawable.rectangle_round_gray);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackground(drawable);
        } else {
            layout.setBackgroundDrawable(drawable);
        }

    }

    /**
     * add layout link to item in channel list
     */
    private void addLayoutLink(LayoutInflater inflater, LinearLayout layout, final int position) {

        View v = inflater.inflate(R.layout.chat_sub_layout_link, null, false);

        TextView txtEye = (TextView) v.findViewById(R.id.csll_txt_eye);
        txtEye.setTypeface(G.fontawesome);

        TextView txtSeen = (TextView) v.findViewById(R.id.csll_txt_seen_count);
        txtSeen.setText(list.get(position).seen);

        if (list.get(position).channelLink.length() > 0) {
            TextView txtLink = (TextView) v.findViewById(R.id.csll_txt_link);
            txtLink.setText(list.get(position).channelLink);
            txtLink.setVisibility(View.VISIBLE);

            txtLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "link clicked   " + position);
                }
            });
        }


        layout.addView(v);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (isSetBackGroundLayout)
            setBackGroundLayout(position, (FrameLayout) holder.itemView.findViewById(R.id.cslr_ll_frame));

        // set sender image in group chat
        if (chatType == MyType.ChatType.groupChat) {
            if (list.get(position).sendType == MyType.SendType.recvive) {
                if (list.get(position).senderAvatar.length() > 0) {
                    CircleImageView imvSenderAvatar = (CircleImageView) holder.itemView.findViewById(R.id.cslr_imv_sender_picture);
                    imvSenderAvatar.setImageResource(Integer.parseInt(list.get(position).senderAvatar));
                }
            }
        }


        switch (list.get(holder.getItemViewType()).messageType.getValue()) {

            case MyType.message:
                configureViewHolderMessage((viewHolderMessage) holder, position);
                break;
            case MyType.image:
                configureViewHolderImage((viewHolderImage) holder, position);
                break;
            case MyType.files:
                configureViewHolderFile((viewHolderFile) holder, position);
                break;
            case MyType.audio:
                configureViewHolderAudio((viewHolderAudio) holder, position);
                break;
            case MyType.gif:
                configureViewHolderGif((viewHolderGif) holder, position);
                break;

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


    //********************************************************************************************************

    private void configureViewHolderFile(viewHolderFile holder, int position) {


    }

    private void configureViewHolderAudio(viewHolderAudio holder, int position) {


    }

    private void configureViewHolderMessage(viewHolderMessage holder, int position) {


    }

    private void configureViewHolderImage(final viewHolderImage holder, final int position) {
        holder.imvPicture.setImageResource(Integer.parseInt(list.get(position).filePath));
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
                Log.e("ddd", "gif click");

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

                if (list.get(position).isSelected == false) {
                    list.get(position).isSelected = true;
                    numberOfSelected++;
                    notifyItemChanged(position);
                }

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


    //********************************************************************************************************


    private void setIsSelectedItem(int position) {


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

    }


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
                        Log.e("ddd", " item   " + position + "   clicked");
                    }

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    int position = myHolder.super.getPosition();
                    isSelectedMode = true;

                    if (list.get(position).isSelected == false) {

                        list.get(position).isSelected = true;
                        numberOfSelected++;
                        notifyItemChanged(position);
                    }


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


    class viewHolderMessage extends myHolder {

        public TextView txtMessage;

        public viewHolderMessage(View itemView) {
            super(itemView);

            txtMessage = (TextView) itemView.findViewById(R.id.cslm_txt_message);

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


    //****************************************************************************************************************

    /**
     * reset all background color item in list
     *
     * @return
     */
    public boolean resetSelected() {

        boolean result = isSelectedMode;

        if (isSelectedMode == true) {
            isSelectedMode = false;
            isSetBackGroundLayout = true;

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


        return result;
    }

}
