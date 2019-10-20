package net.iGap.dialog.account;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import net.iGap.model.AccountUser;

import org.paygear.RaadApp;

import java.util.List;

import static net.iGap.request.RequestClientGetRoomList.pendingRequest;
import static org.paygear.utils.Utils.signOutWallet;

public class AccountsDialog extends BottomSheetDialogFragment {

    private List<AccountUser> mAccountsList;
    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler;

    public AccountsDialog setData(AvatarHandler avatarHandler, AccountDialogListener listener) {
        this.mAccountsList = AccountManager.getInstance().getUserAccountList();
        for (int i = 0; i < mAccountsList.size(); i++) {
            Log.wtf(this.getClass().getName(), "account: " + mAccountsList.get(i).toString());
        }
        this.mListener = listener;
        this.mAvatarHandler = avatarHandler;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        AccountsDialogAdapter adapter = new AccountsDialogAdapter();
        adapter.setAvatarHandler(mAvatarHandler);
        adapter.setListener(new AccountDialogListener() {
            @Override
            public void onAccountClick(long id) {
                if (getActivity() instanceof ActivityMain && AccountManager.getInstance().getCurrentUser().getId() != id) {
                    WebSocketClient.disconnectSocket();
                    DbManager.getInstance().closeUiRealm();
                    signOutWallet();
                    AccountManager.getInstance().changeCurrentUserAccount(id);
                    RaadApp.onCreate(getContext());
                    pendingRequest.remove(0);
                    FragmentMain.mOffset = 0;
                    ((ActivityMain) getActivity()).updateUiForChangeAccount();
                    dismiss();
                }
            }

            @Override
            public void onNewAccountClick() {
                if (getActivity() != null) {
                    WebSocketClient.disconnectSocket();
                    DbManager.getInstance().closeUiRealm();
                    pendingRequest.remove(0);
                    FragmentMain.mOffset = 0;
                    signOutWallet();
                    AccountManager.getInstance().changeCurrentUserForAddAccount();
                    DbManager.getInstance().changeRealmConfiguration();
                    startActivity(new Intent(getActivity(), ActivityRegistration.class));
                    getActivity().finish();
                    RaadApp.onCreate(getContext());
                    WebSocketClient.connectNewAccount();
                }
            }
        });
        binding.bottomSheetList.setAdapter(adapter);
        adapter.setAccountsList(mAccountsList);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public int getTheme() {
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }
}
