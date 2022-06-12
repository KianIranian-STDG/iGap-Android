package net.iGap.module.dialog.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountHelper;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.module.enums.CallState;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.viewmodel.controllers.CallManager;

import static net.iGap.G.context;

public class AccountsDialog extends BaseBottomSheet implements EventManager.EventDelegate {

    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;
    private boolean isAssigned;
    private MaterialDialog uploadingStateDialog;
    private FragmentBottomSheetDialogBinding binding;
    private long userId;
    private Button dialogPositiveButton;
    private Button dialogNegativeButton;
    public static ProgressBar progressBar;

    public AccountsDialog setData(AvatarHandler avatarHandler, AccountDialogListener listener) {
        this.mListener = listener;
        this.mAvatarHandler = avatarHandler;
        return this;
    }

    public MaterialDialog getUploadingStateDialog() {
        return uploadingStateDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.CALL_STATE_CHANGED, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.ON_UPLOAD_SERVICE_STOPPED, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.CALL_STATE_CHANGED, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.ON_UPLOAD_SERVICE_STOPPED, this);
        this.binding = binding;
        binding.bottomSheetList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.bottomSheetList.setAdapter(new AccountsDialogAdapter(mAvatarHandler, (isAssigned, id) -> {
            if (CallManager.getInstance().isUserInCall() || CallManager.getInstance().isCallAlive() || CallManager.getInstance().isRinging()) {
                this.isAssigned = isAssigned;
                this.userId = id;
                ShowMsgDialog(getContext());
            } else if (context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE).getBoolean(SHP_SETTING.KEY_IS_UPLOAD_SERVICE_RUN, false)) {
                showProfileAlertDialog(requireActivity(), getString(R.string.you_are_in_uploading_state));
            } else {
                checkForAssigning(isAssigned, id);
            }
        }, getContext()));
        binding.title.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.mainContainer.setBackgroundColor(Theme.getColor(Theme.key_popup_background));
        binding.lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_line)));
        progressBar = new ProgressBar(requireContext());
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
//                        progressBar.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                }).onNegative((dialog, which) -> dialog.dismiss()).show();
    }

    private void showProfileAlertDialog(Context context, String contentText) {
        uploadingStateDialog = new MaterialDialog.Builder(context)
                .content(contentText)
                .positiveText(context.getResources().getString(R.string.ok))
                .contentGravity(GravityEnum.CENTER)
                .onPositive((dialog, which) -> {
                    //This event is related to upload service that prevents crash when upload service is active and user change account.
                    //Cause dependency injection did not implemented in project, multi account can not be implement in service upload.
                    // TODO: 16/03/22 Add Di in project and refactor this section to implementing multi account in service upload
                    dialog.dismiss();
                }).show();
    }

    public void checkForAssigning(boolean assign, long id) {

        if (assign) {

            if (getActivity() instanceof ActivityMain && AccountManager.getInstance().getCurrentUser().getId() != id) {
                new AccountHelper().changeAccount(id);
                ((ActivityMain) getActivity()).updateUiForChangeAccount();
            }
            dismiss();
        } else {
            if (getActivity() != null) {
                HelperTracker.sendTracker(HelperTracker.TRACKER_ADD_NEW_ACCOUNT);
                new AccountHelper().addAccount();
                // WebSocketClient.GGFconnectNewAccount();
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
    public void receivedEvent(int id, int account, Object... args) {

        if (id == EventManager.CALL_STATE_CHANGED) {
            G.runOnUiThread(() -> {
                binding.accountDialogProgressbar.setVisibility(View.GONE);
                if (CallManager.getInstance().getCurrentSate() == CallState.LEAVE_CALL || CallManager.getInstance().getCurrentSate() == CallState.REJECT) {
                    checkForAssigning(isAssigned, userId);
                }
            });

        }

        if (id == EventManager.ON_UPLOAD_SERVICE_STOPPED) {
            G.runOnUiThread(() -> {
                binding.accountDialogProgressbar.setVisibility(View.GONE);
                checkForAssigning(isAssigned, userId);
            });
        }
    }
}
