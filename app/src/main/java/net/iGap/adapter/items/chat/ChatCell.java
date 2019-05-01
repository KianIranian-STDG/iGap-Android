package net.iGap.adapter.items.chat;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iGap.R;

public class ChatCell extends RelativeLayout {

    public static final int CHAT = 0;
    public static final int CHANNEL = 1;
    public static final int GROUP = 2;
    public static final int BOT = 3;

    public static final boolean MUTE = true;
    public static final boolean UNMUTE = false;

    public static final boolean VERIFIED = true;
    public static final boolean UNVERIFIED = false;

    public static final int FAILED = 0;
    public static final int SENDING = 1;
    public static final int SENT = 2;
    public static final int DELIVERED = 3;
    public static final int SEEN = 4;
    public static final int LISTENED = 5;

    private String userName;
    private String message;
    private String lastMessage;
    private String data;
    private int messageStatus = SENDING;
    private int chatType = CHAT;
    private boolean isVerified = UNVERIFIED;
    private boolean notificationType = UNMUTE;
    private boolean isRtl = false;
    private int backgroundColor = 0;


    public ChatCell(Context context) {
        super(context);
        init();
    }

    private void init() {
        int dimen4 = dpToPx(4);
        int dimen8 = dpToPx(8);
        int dimen16 = dpToPx(16);


        ImageView avatarImageIv = new ImageView(getContext());
        TextView userNameTv = new TextView(getContext());
        TextView lastMessageTv = new TextView(getContext());
        TextView dataTv = new TextView(getContext());
        TextView messageStatusTv = new TextView(getContext());
        TextView chatTypeTv = new TextView(getContext());
        TextView messageDetailTv = new TextView(getContext());
        TextView verifiedTv = new TextView(getContext());

        textViewSetup(userNameTv);
        textViewSetup(lastMessageTv);


        if (chatType == CHANNEL)
            chatTypeTv.setText("Ch");
        else if (chatType == GROUP)
            chatTypeTv.setText("Gr");
        else if (chatType == BOT)
            chatTypeTv.setText("Bot");
        else
            chatTypeTv.setText("");

        lastMessageTv.setText(lastMessage);

        if (!userName.isEmpty())
            userNameTv.setText(userName);

        if (isVerified)
            verifiedTv.setText("V");
        else
            verifiedTv.setText("");

        if (isVerified)
            verifiedTv.setText("V");
        else
            verifiedTv.setText("");
        verifiedTv.setTextColor(getResources().getColor(R.color.verify_color));


        RelativeLayout.LayoutParams avatarParams = new RelativeLayout.LayoutParams(dpToPx(55), dpToPx(55));
        avatarParams.addRule(isRtl ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        avatarParams.addRule(RelativeLayout.CENTER_VERTICAL);
        avatarParams.setMargins(isRtl ? dimen16 : dimen8, dimen8, isRtl ? dimen8 : dimen16, dimen8);
        avatarImageIv.setBackgroundColor(getResources().getColor(R.color.black_register));
        avatarImageIv.setId(R.id.iv_chatCell_avatar);
        avatarImageIv.setLayoutParams(avatarParams);

        LinearLayout rootLinear = new LinearLayout(getContext());
        rootLinear.setOrientation(LinearLayout.VERTICAL);
        rootLinear.setWeightSum(2);
        RelativeLayout.LayoutParams rootLinearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLinearParams.addRule(isRtl ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, avatarImageIv.getId());
        rootLinear.setLayoutParams(rootLinearParams);

        RelativeLayout topFrame = new RelativeLayout(getContext());
        LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        topFrame.setLayoutParams(topParams);
        topFrame.setPadding(isRtl ? dimen16 : dimen8, 0, isRtl ? dimen8 : dimen16, 0);
        rootLinear.addView(topFrame);

        RelativeLayout bottomFrame = new RelativeLayout(getContext());
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        bottomFrame.setLayoutParams(bottomParams);
        bottomFrame.setPadding(isRtl ? dimen16 : dimen8, 0, isRtl ? dimen8 : dimen16, 0);

        rootLinear.addView(bottomFrame);


        RelativeLayout.LayoutParams chatTypeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        chatTypeParams.addRule(isRtl ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        chatTypeParams.setMargins(0, 0, isRtl ? 0 : dimen4, 0);
        chatTypeParams.addRule(CENTER_VERTICAL);
        chatTypeTv.setId(R.id.tv_chatCell_chatType);
        chatTypeTv.setLayoutParams(chatTypeParams);
        topFrame.addView(chatTypeTv);

        RelativeLayout.LayoutParams userNameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userNameParams.addRule(isRtl ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, chatTypeTv.getId());
        userNameParams.setMargins(0, 0, isRtl ? chatTypeTv.getText().equals("") ? 0 : dimen4 : 0, 0);
        userNameParams.addRule(CENTER_VERTICAL);
        userNameTv.setId(R.id.tv_chatCell_userName);
        userNameTv.setLayoutParams(userNameParams);
        topFrame.addView(userNameTv);

        RelativeLayout.LayoutParams verifyParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        verifyParams.addRule(isRtl ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.tv_chatCell_userName);
        verifyParams.setMargins(isRtl ? 0 : dimen8, 0, isRtl ? dimen8 : 0, 0);
        verifiedTv.setLayoutParams(verifyParams);
        verifyParams.addRule(RelativeLayout.CENTER_VERTICAL);
        topFrame.addView(verifiedTv);

        RelativeLayout.LayoutParams dataParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dataParams.addRule(CENTER_VERTICAL);
        dataParams.addRule(isRtl ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
        dataTv.setLayoutParams(dataParams);
        topFrame.addView(dataTv);


        RelativeLayout.LayoutParams lastMessageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lastMessageParams.addRule(isRtl ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        lastMessageParams.addRule(CENTER_VERTICAL);
        lastMessageTv.setLayoutParams(lastMessageParams);
        bottomFrame.addView(lastMessageTv);


        RelativeLayout.LayoutParams messageStatusParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageStatusParams.addRule(isRtl ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT);
        messageStatusParams.addRule(CENTER_VERTICAL);
        messageStatusTv.setLayoutParams(messageStatusParams);
        bottomFrame.addView(messageStatusTv);

        addView(avatarImageIv);
        addView(rootLinear);

    }

    private TextView textViewSetup(TextView textView) {
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setHorizontallyScrolling(false);

        return textView;
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isNotificationType() {
        return notificationType;
    }

    public void setNotificationType(boolean notificationType) {
        this.notificationType = notificationType;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setRtl(boolean isRtls) {
        isRtl = isRtls;
    }
}
