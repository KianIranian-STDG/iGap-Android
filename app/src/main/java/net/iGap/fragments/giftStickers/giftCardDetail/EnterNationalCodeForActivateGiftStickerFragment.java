package net.iGap.fragments.giftStickers.giftCardDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentEnterNationalCodeForActivateGiftStickerBinding;
import net.iGap.helper.HelperError;

public class EnterNationalCodeForActivateGiftStickerFragment extends Fragment {
    private boolean canForward;

    private EnterNationalCodeForActivateGiftStickerFragment() {
    }

    public static EnterNationalCodeForActivateGiftStickerFragment getInstance(boolean canForward) {
        EnterNationalCodeForActivateGiftStickerFragment fragment = new EnterNationalCodeForActivateGiftStickerFragment();
        fragment.canForward = canForward;
        return fragment;
    }

    private EnterNationalCodeForActivateGiftStickerViewModel viewModel;
    private FragmentEnterNationalCodeForActivateGiftStickerBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(EnterNationalCodeForActivateGiftStickerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_enter_national_code_for_activate_gift_sticker, container, false);
        binding.setViewModel(viewModel);
        binding.forward.setVisibility(canForward ? View.GONE : View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessageRes -> {
            if (errorMessageRes != null) {
                HelperError.showSnackMessage(getString(errorMessageRes), false);
            }
        });

        viewModel.getGoToNextStep().observe(getViewLifecycleOwner(), goNext -> {
            if (goNext != null && goNext && getParentFragment() instanceof MainGiftStickerCardFragment) {
                ((MainGiftStickerCardFragment) getParentFragment()).loadGiftStickerCardDetailFragment();
            }
        });
    }
}
