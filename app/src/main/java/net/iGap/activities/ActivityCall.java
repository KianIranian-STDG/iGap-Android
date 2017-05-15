package net.iGap.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.wang.avi.AVLoadingIndicatorView;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.module.MaterialDesignTextView;

/**
 * Created by android3 on 5/8/2017.
 */

public class ActivityCall extends ActivityEnhanced {

    public static final String UserIdStr = "USERID";

    long userID;

    TextView txtName;
    TextView txtStatus;
    AVLoadingIndicatorView avLoadingIndicatorView;
    ImageView imvPicture;
    LinearLayout layoutAnswer;
    LinearLayout layoutCaller;
    LinearLayout layoutOption;
    Button btnAnswerInputCall;
    Button btnEndInputCall;
    MaterialDesignTextView btnClose;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnCall;
    MaterialDesignTextView btnMic;
    MaterialDesignTextView btnChat;
    MaterialDesignTextView btnSpeaker;

    //************************************************************************

    @Override public void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
            LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        userID = getIntent().getExtras().getLong(UserIdStr);

        initComponent();


        Toast.makeText(ActivityCall.this, userID + "", Toast.LENGTH_SHORT).show();
    }



    //***************************************************************************************

    private void initComponent() {

        txtName = (TextView) findViewById(R.id.fcr_txt_name);
        txtStatus = (TextView) findViewById(R.id.fcr_txt_status);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.fcr_txt_avi);
        imvPicture = (ImageView) findViewById(R.id.fcr_imv_igap_icon);

        layoutAnswer = (LinearLayout) findViewById(R.id.fcr_layout_answer);
        layoutCaller = (LinearLayout) findViewById(R.id.fcr_layout_caller);
        layoutOption = (LinearLayout) findViewById(R.id.fcr_layout_option);

        btnAnswerInputCall = (Button) findViewById(R.id.fcr_btn_answer_input_call);
        btnEndInputCall = (Button) findViewById(R.id.fcr_btn_end_input_call);

        btnClose = (MaterialDesignTextView) findViewById(R.id.fcr_btn_close);
        btnEndCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_end);
        btnCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_call);

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);

        btnSpeaker = (MaterialDesignTextView) findViewById(R.id.fcr_btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnSpeaker.getText().toString().equals(G.context.getResources().getString(R.string.md_muted))) {
                    btnSpeaker.setText(R.string.md_unMuted);
                } else {
                    btnSpeaker.setText(R.string.md_muted);
                }
            }
        });

        btnMic = (MaterialDesignTextView) findViewById(R.id.fcr_btn_mic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnMic.getText().toString().equals(G.context.getResources().getString(R.string.md_mic))) {
                    btnMic.setText(R.string.md_mic_off);
                } else {
                    btnMic.setText(R.string.md_mic);
                }
            }
        });

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                HelperPublicMethod.goToChatRoom(userID, null, null);
            }
        });
    }


}
