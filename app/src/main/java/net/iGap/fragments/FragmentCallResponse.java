package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by android3 on 4/18/2017.
 */

public class FragmentCallResponse extends Fragment {

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

    public static FragmentCallResponse newInstance(long userID) {

        FragmentCallResponse fragment = new FragmentCallResponse();
        Bundle bundle = new Bundle();
        bundle.putLong(UserIdStr, userID);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_call_response, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userID = getArguments().getLong(UserIdStr);

        initComponent(view);

        Toast.makeText(getContext(), userID + "", Toast.LENGTH_SHORT).show();
    }

    private void initComponent(View view) {

        txtName = (TextView) view.findViewById(R.id.fcr_txt_name);
        txtStatus = (TextView) view.findViewById(R.id.fcr_txt_status);
        avLoadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.fcr_txt_avi);
        imvPicture = (ImageView) view.findViewById(R.id.fcr_imv_igap_icon);

        layoutAnswer = (LinearLayout) view.findViewById(R.id.fcr_layout_answer);
        layoutCaller = (LinearLayout) view.findViewById(R.id.fcr_layout_caller);
        layoutOption = (LinearLayout) view.findViewById(R.id.fcr_layout_option);

        btnAnswerInputCall = (Button) view.findViewById(R.id.fcr_btn_answer_input_call);
        btnEndInputCall = (Button) view.findViewById(R.id.fcr_btn_end_input_call);

        btnClose = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_close);
        btnEndCall = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_end);
        btnCall = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_call);

        btnChat = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_chat);

        btnSpeaker = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnSpeaker.getText().toString().equals(G.context.getResources().getString(R.string.md_muted))) {
                    btnSpeaker.setText(R.string.md_unMuted);
                } else {
                    btnSpeaker.setText(R.string.md_muted);
                }
            }
        });

        btnMic = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_mic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnMic.getText().toString().equals(G.context.getResources().getString(R.string.md_mic))) {
                    btnMic.setText(R.string.md_mic_off);
                } else {
                    btnMic.setText(R.string.md_mic);
                }
            }
        });

        btnChat = (MaterialDesignTextView) view.findViewById(R.id.fcr_btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                HelperPublicMethod.goToChatRoom(userID, null, null);
            }
        });
    }
}
