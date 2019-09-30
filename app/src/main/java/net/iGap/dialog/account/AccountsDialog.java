package net.iGap.dialog.account;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.model.AccountUser;

import java.util.List;

public class AccountsDialog extends BottomSheetDialogFragment {

    private List<AccountUser> mAccountsList;
    private AccountDialogListener mListener;
    private AvatarHandler mAvatarHandler ;

    public AccountsDialog setData(List<AccountUser> accounts , AvatarHandler avatarHandler , AccountDialogListener listener) {
        this.mAccountsList = accounts;
        this.mListener = listener;
        this.mAvatarHandler = avatarHandler ;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        AccountsDialogAdapter adapter = new AccountsDialogAdapter();
        adapter.setAvatarHandler(mAvatarHandler , getContext());
        adapter.setListener(mListener);
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
