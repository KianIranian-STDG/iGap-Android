package net.iGap.fragments.giftStickers.enterNationalCode;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentEnterNationalCodeBinding;
import net.iGap.fragments.chatMoneyTransfer.ParentChatMoneyTransferFragment;
import net.iGap.fragments.giftStickers.GiftStickerHomeFragment;
import net.iGap.helper.HelperError;

public class EnterNationalCodeFragment extends Fragment {
    private EnterNationalCodeViewModel viewModel;
    private boolean fromChat = false;

    private EnterNationalCodeFragment() {
    }

    public static EnterNationalCodeFragment getInstance(boolean fromChat) {
        EnterNationalCodeFragment fragment = new EnterNationalCodeFragment();
        fragment.fromChat = fromChat;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(EnterNationalCodeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentEnterNationalCodeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_national_code, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.checkBox.setTypeface(ResourcesCompat.getFont(container.getContext(), R.font.main_font));
        binding.loadingView.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeedUpdate -> {
            if (getActivity() instanceof ActivityMain && isNeedUpdate != null && isNeedUpdate) {
                ((ActivityMain) getActivity()).checkGoogleUpdate();
            }
        });

        viewModel.getGoNextStep().observe(getViewLifecycleOwner(), isGo -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && isGo != null && isGo) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadStickerPackagePage();
            } else if (getParentFragment() instanceof GiftStickerHomeFragment && isGo != null && isGo) {
                ((GiftStickerHomeFragment) getParentFragment()).loadStickerPackagePage();
            }
        });

        viewModel.getRequestError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });

        viewModel.getShowErrorMessageRequestFailed().observe(getViewLifecycleOwner(), errorMessageRes -> {
            if (getContext() != null && errorMessageRes != null) {
                Toast.makeText(getContext(), getString(errorMessageRes), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
