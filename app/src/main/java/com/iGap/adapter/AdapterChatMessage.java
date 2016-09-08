/*
public class AdapterChatMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // add layout forward message to layout
        if (list.get(viewType).forwardMessageFrom.length() > 0 && chatType != ProtoGlobal.Room.Type.CHANNEL) {
            main.findViewById(R.id.cslr_ll_forward).setVisibility(View.VISIBLE);
            TextView txtForwardMessage = (TextView) main.findViewById(R.id.cslr_txt_forward_from);
            txtForwardMessage.setText("Forward From " + list.get(viewType).forwardMessageFrom);
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

    /*/
/***********************************************************************************************************

    private void configureLayoutChannel(RecyclerView.ViewHolder holder, final int position) {
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
