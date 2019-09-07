package net.iGap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.helper.HelperError;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.webrtc.WebRTC;

import io.realm.Realm;

public class CallSelectFragment extends BottomSheetDialogFragment {
    private static final String TAG = "aabolfazl";

    private View rootView;
    private View voiceCall;
    private View videoCall;
    private long userId;
    private boolean isIncomingCall;
    private ProtoSignalingOffer.SignalingOffer.Type callType;


    public static CallSelectFragment getInstance(long userId, boolean isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type callType) {
        CallSelectFragment transferAction = new CallSelectFragment();
        transferAction.userId = userId;
        transferAction.isIncomingCall = isIncomingCall;
        transferAction.callType = callType;
        return transferAction;
    }

    public static void call(long userID, boolean isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type callTYpe) {

        if (G.userLogin) {

            if (!G.isInCall) {
                try (Realm realm = Realm.getDefaultInstance()) {
                    RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();

                    if (realmCallConfig == null) {
                        new RequestSignalingGetConfiguration().signalingGetConfiguration();
                        HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                    } else if (!G.isCalling) {
                        if (G.currentActivity != null) {
                            Intent intent = new Intent(G.currentActivity, ActivityCall.class);
                            intent.putExtra(ActivityCall.USER_ID_STR, userID);
                            intent.putExtra(ActivityCall.INCOMING_CALL_STR, isIncomingCall);
                            intent.putExtra(ActivityCall.CALL_TYPE, callTYpe);
                            ActivityCall.isGoingfromApp = true;
                            G.currentActivity.startActivity(intent);
                        } else {
                            Intent intent = new Intent(G.context, ActivityCall.class);
                            intent.putExtra(ActivityCall.USER_ID_STR, userID);
                            intent.putExtra(ActivityCall.INCOMING_CALL_STR, isIncomingCall);
                            intent.putExtra(ActivityCall.CALL_TYPE, callTYpe);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ActivityCall.isGoingfromApp = true;
                            G.context.startActivity(intent);
                        }


                    } else {
                        try {
                            WebRTC.getInstance().leaveCall();
                        } catch (Exception e) {
                        }

                    }
                }
            }
        } else {

            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_select_call_action, container);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        voiceCall = rootView.findViewById(R.id.ll_callAction_voiceCall);
        videoCall = rootView.findViewById(R.id.ll_callAction_videoCall);
        TextView voiceCallIv = rootView.findViewById(R.id.iv_callAction_voiceCall);
        TextView videoCallIv = rootView.findViewById(R.id.iv_callAction_videoCall);
        TextView voiceCallTv = rootView.findViewById(R.id.tv_callAction_voiceCall);
        TextView videoCallTv = rootView.findViewById(R.id.tv_callAction_videoCall);

        Utils.darkModeHandler(voiceCallIv);
        Utils.darkModeHandler(voiceCallTv);
        Utils.darkModeHandler(videoCallTv);
        Utils.darkModeHandler(videoCallIv);

        if (G.isDarkTheme)
            rootView.findViewById(R.id.lineViewTop).setBackgroundResource(R.drawable.bottom_sheet_dialog_line_dark);
    }

    @Override
    public void onStart() {
        super.onStart();
        voiceCall.setOnClickListener(v -> {
            call(userId, isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
            dismiss();
        });


        videoCall.setOnClickListener(v -> {
            call(userId, isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
            dismiss();
        });


    }

    @Override
    public int getTheme() {
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }
}
