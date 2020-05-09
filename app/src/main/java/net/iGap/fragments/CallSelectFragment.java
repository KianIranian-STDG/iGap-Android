package net.iGap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.helper.HelperError;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.viewmodel.controllers.CallManager;

public class CallSelectFragment extends BaseBottomSheet {

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
                DbManager.getInstance().doRealmTask(realm -> {
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
                            // TODO: 5/9/2020 needs to review this function
                            CallManager.getInstance().leaveCall();
                        } catch (Exception e) {
                        }

                    }
                });
            }
        } else {

            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_call_action, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voiceCall = view.findViewById(R.id.ll_callAction_voiceCall);
        videoCall = view.findViewById(R.id.ll_callAction_videoCall);
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
        return R.style.BaseBottomSheetDialog;
    }
}
