/*
public class AdapterChatMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (chatType == ProtoGlobal.Room.Type.CHANNEL) {//inflate layout channel
            main = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sub_layout_channel, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            main.setLayoutParams(lp);
        }

        // add layout forward message to layout
        if (list.get(viewType).forwardMessageFrom.length() > 0 && chatType != ProtoGlobal.Room.Type.CHANNEL) {
            main.findViewById(R.id.cslr_ll_forward).setVisibility(View.VISIBLE);
            TextView txtForwardMessage = (TextView) main.findViewById(R.id.cslr_txt_forward_from);
            txtForwardMessage.setText("Forward From " + list.get(viewType).forwardMessageFrom);
        }


        // set layout mergin for singlechat
        if (chatType == ProtoGlobal.Room.Type.CHAT) {

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

        //add layout replay to layout
        if (list.get(viewType).replayFrom.length() > 0)
            addLayoutReplay(inflater, frameLayout, viewType);


        //set background layout time in single chat or group chat
        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
            if (((list.get(viewType).messageType == ProtoGlobal.RoomMessageType.IMAGE)
                    && list.get(viewType).messageText == "")) {

                LinearLayout layoutTime = (LinearLayout) main.findViewById(R.id.cslr_ll_time);
                layoutTime.setBackgroundResource(R.drawable.recangle_gray_tranceparent);

                FrameLayout.LayoutParams timeParaams = (FrameLayout.LayoutParams) layoutTime.getLayoutParams();
                timeParaams.setMargins(0, 0, 6, 6);
                layoutTime.setLayoutParams(timeParaams);

                ((TextView) main.findViewById(R.id.cslr_txt_time)).setTextColor(Color.WHITE);
            }
        }


        // set sender image in group chat
        if (chatType == ProtoGlobal.Room.Type.GROUP) {
            if (list.get(viewType).sendType == MyType.SendType.recvive) {
                if (list.get(viewType).senderAvatar.length() > 0) {
                    CircleImageView imvSenderAvatar = (CircleImageView) viewHolder.itemView.findViewById(R.id.cslr_imv_sender_picture);
                    imvSenderAvatar.setImageResource(Integer.parseInt(list.get(viewType).senderAvatar));
                }
            }
        }


        return viewHolder;
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

    /*/
/***********************************************************************************************************

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


        if (list.get(position).messageType == ProtoGlobal.RoomMessageType.IMAGE)


 if (list.get(position).messageType == ProtoGlobal.RoomMessageType.VOICE) {// gone btn elipse_v audio
 holder.itemView.findViewById(R.id.csla_btn_audio_menu).setVisibility(View.GONE);
            }


    }

 */
/**
     * add layout replay to layout
 *//*

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

}*/
