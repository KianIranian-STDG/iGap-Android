package net.iGap.module.dialog.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.module.accountManager.AccountHelper;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.BaseBottomSheet;

import org.paygear.RaadApp;

public class AccountsDialog extends BaseBottomSheet {

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
                    new AccountHelper().changeAccount(id);
                    ((ActivityMain) getActivity()).updateUiForChangeAccount();
                    RaadApp.onCreate(getContext());
                }
                dismiss();
            } else {
                if (getActivity() != null) {
                    new AccountHelper().addAccount();
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
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

}
