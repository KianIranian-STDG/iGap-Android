package net.iGap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.iGap.R;
import net.iGap.activities.CallActivity;
import net.iGap.helper.PermissionHelper;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.FontIconTextView;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.module.webrtc.CallService;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.viewmodel.controllers.CallManager;

public class CallSelectFragment extends BaseBottomSheet {

    private View voiceCall;
    private View videoCall;
    private long userId;
    private Intent intent;
    private Activity activity;


    public static CallSelectFragment getInstance(long userId, boolean isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type callType) {
        CallSelectFragment transferAction = new CallSelectFragment();
        transferAction.userId = userId;
        return transferAction;
    }

    public static void call(long userID, boolean isIncomingCall, ProtoSignalingOffer.SignalingOffer.Type callTYpe) {

    }

    private void startCall(Activity activity, long userId, ProtoSignalingOffer.SignalingOffer.Type callType) {
        if (CallManager.getInstance().isUserInSimCall() && activity != null) {
            Toast.makeText(activity, "You are in Calling", Toast.LENGTH_LONG).show();
            return;
        }
        if (activity == null || userId <= 0) {
            return;
        }
        this.activity = activity;

        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            if (CallManager.getInstance().getCallPeerId() == userId) {
                Intent activityIntent = new Intent(getActivity(), CallActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(activityIntent);
            } else if (!CallManager.getInstance().isCallAlive()) {
                intent = new Intent(activity, CallService.class);
                intent.putExtra(CallService.USER_ID, userId);
                intent.putExtra(CallService.IS_INCOMING, false);
                intent.putExtra(CallService.CALL_TYPE, callType.toString());

                if (!checkPermissions(callType == ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING))
                    return;

                try {
                    activity.startService(intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getContext(), "NOT ALLOWED", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    private boolean checkPermissions(boolean isVideoCall) {
        PermissionHelper permissionHelper = new PermissionHelper(getActivity(), CallSelectFragment.this);
        if (isVideoCall)
            return permissionHelper.grantCameraAndVoicePermission();
        else
            return permissionHelper.grantVoicePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean tmp = true;
        for (int grantResult : grantResults) {
            tmp = tmp && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (tmp) {
            try {
                activity.startService(intent);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        dismiss();
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
        FontIconTextView voiceCallIcon = view.findViewById(R.id.voiceCallIcon);
        FontIconTextView videoCallIcon = view.findViewById(R.id.videoCallIcon);
        voiceCallIcon.setTextColor(Theme.getColor(Theme.key_icon));
        videoCallIcon.setTextColor(Theme.getColor(Theme.key_icon));

        View lineViewTop = view.findViewById(R.id.lineViewTop);
        lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_icon)));
    }

    @Override
    public void onStart() {
        super.onStart();
        voiceCall.setOnClickListener(v -> startCall(getActivity(), userId, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING));
        videoCall.setOnClickListener(v -> startCall(getActivity(), userId, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING));
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }
}
