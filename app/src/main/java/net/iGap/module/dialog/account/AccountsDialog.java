package net.iGap.module.dialog.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.accountManager.AccountHelper;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.module.enums.CallState;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.viewmodel.controllers.CallManager;

import org.paygear.RaadApp;

public class AccountsDialog extends BaseBottomSheet implements EventListener {

    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;
    private boolean isAssigned;
    private FragmentBottomSheetDialogBinding binding;
    private long userId;
    private Button dialogPositiveButton;
    private Button dialogNegativeButton;

    public AccountsDialog setData(AvatarHandler avatarHandler, AccountDialogListener listener) {
        this.mListener = listener;
        this.mAvatarHandler = avatarHandler;
        return this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().removeEventListener(EventManager.CALL_STATE_CHANGED, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);
        EventManager.getInstance().addEventListener(EventManager.CALL_STATE_CHANGED, this);
        this.binding = binding;
        binding.bottomSheetList.setAdapter(new AccountsDialogAdapter(mAvatarHandler, (isAssigned, id) -> {
            if (CallManager.getInstance().isUserInCall() || CallManager.getInstance().isCallAlive() || CallManager.getInstance().isRinging()) {
                this.isAssigned = isAssigned;
                this.userId = id;
                ShowMsgDialog(getContext());
            } else {
                checkForAssigning(isAssigned, id);
            }
        }));

        return binding.getRoot();
    }

    public void ShowMsgDialog(Context context) {
        new MaterialDialog.Builder(context)
                .content(context.getResources().getString(R.string.account_dialog_message))
                .positiveText(context.getResources().getString(R.string.yes))
                .negativeText(context.getResources().getString(R.string.no))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CallManager.getInstance().endCall();
                        binding.accountDialogProgressbar.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                }).onNegative((dialog, which) -> dialog.dismiss()).show();
    }

    public void checkForAssigning(boolean assign, long id) {
        if (assign) {

            if (getActivity() instanceof ActivityMain && AccountManager.getInstance().getCurrentUser().getId() != id) {
                new AccountHelper().changeAccount(id);
                ((ActivityMain) getActivity()).updateUiForChangeAccount();
                RaadApp.onCreate(getContext());
            }
            dismiss();
        } else {
            if (getActivity() != null) {
                HelperTracker.sendTracker(HelperTracker.TRACKER_ADD_NEW_ACCOUNT);
                new AccountHelper().addAccount();
                RaadApp.onCreate(getContext());
                // WebSocketClient.connectNewAccount();
                Intent intent = new Intent(getActivity(), ActivityRegistration.class);
                intent.putExtra("add account", true);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        }
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.CALL_STATE_CHANGED) {
            G.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.accountDialogProgressbar.setVisibility(View.GONE);
                    if (CallManager.getInstance().getCurrentSate() == CallState.LEAVE_CALL || CallManager.getInstance().getCurrentSate() == CallState.REJECT) {
                        checkForAssigning(isAssigned, userId);
                    }
                }
            });

        }
    }
}
