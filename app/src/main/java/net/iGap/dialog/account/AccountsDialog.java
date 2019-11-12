package net.iGap.dialog.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.fragments.FragmentMain;
import net.iGap.helper.avatar.AvatarHandler;

import org.paygear.RaadApp;

import static net.iGap.request.RequestClientGetRoomList.pendingRequest;
import static org.paygear.utils.Utils.signOutWallet;

public class AccountsDialog extends BottomSheetDialogFragment {

    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;

    public AccountsDialog setData(AvatarHandler avatarHandler, AccountDialogListener listener) {
        this.mListener = listener;
        this.mAvatarHandler = avatarHandler;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        binding.bottomSheetList.setAdapter(new AccountsDialogAdapter(mAvatarHandler, (isAssigned, id) -> {
            if (isAssigned) {
                if (getActivity() instanceof ActivityMain && AccountManager.getInstance().getCurrentUser().getId() != id) {
                    WebSocketClient.getInstance().disconnectSocket();
                    G.handler.removeCallbacksAndMessages(null);
                    DbManager.getInstance().closeUiRealm();
                    signOutWallet();
                    AccountManager.getInstance().changeCurrentUserAccount(id);
                    RaadApp.onCreate(getContext());
                    pendingRequest.remove(0);
                    FragmentMain.mOffset = 0;
                    dismiss();
                    ((ActivityMain) getActivity()).updateUiForChangeAccount();
                }
            } else {
                if (getActivity() != null) {
                    WebSocketClient.getInstance().disconnectSocket();
                    G.handler.removeCallbacksAndMessages(null);
                    DbManager.getInstance().closeUiRealm();
                    pendingRequest.remove(0);
                    FragmentMain.mOffset = 0;
                    signOutWallet();
                    AccountManager.getInstance().changeCurrentUserForAddAccount();
                    DbManager.getInstance().changeRealmConfiguration();
                    RaadApp.onCreate(getContext());
                   // WebSocketClient.connectNewAccount();
                    Intent intent = new Intent(getActivity(), ActivityRegistration.class);
                    intent.putExtra("add account", true);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        }));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }
}
