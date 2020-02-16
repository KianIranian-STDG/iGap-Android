package net.iGap.fragments.giftStickers.giftCardDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentEnterNationalCodeForActivateGiftStickerBinding;

public class EnterNationalCodeForActivateGiftStickerFragment extends Fragment {
    private boolean canForward;
    private View.OnClickListener sendOtherListener;

    private EnterNationalCodeForActivateGiftStickerFragment() {
    }

    public static EnterNationalCodeForActivateGiftStickerFragment getInstance(View.OnClickListener sendOtherListener, boolean canForward) {
        EnterNationalCodeForActivateGiftStickerFragment fragment = new EnterNationalCodeForActivateGiftStickerFragment();
        fragment.canForward = canForward;
        fragment.sendOtherListener = sendOtherListener;
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
        binding.forward.setVisibility(canForward ? View.VISIBLE : View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessageRes -> {
            if (errorMessageRes != null) {
                Toast.makeText(getContext(), getString(errorMessageRes), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getGoToNextStep().observe(getViewLifecycleOwner(), goNext -> {
            if (goNext != null && goNext && getParentFragment() instanceof MainGiftStickerCardFragment) {
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title(R.string.app_name)
                        .content(R.string.active_gift_card_note)
                        .positiveText(R.string.ok)
                        .onPositive((dialog, which) -> {
                            ((MainGiftStickerCardFragment) getParentFragment()).loadGiftStickerCardDetailFragment(((MainGiftStickerCardFragment) getParentFragment()).getCurrentMode());
                            dialog.dismiss();
                        })
                        .onNegative((dialog, which) -> dialog.dismiss())
                        .negativeText(R.string.cancel)
                        .show();

            }
        });

        binding.forward.setOnClickListener(v -> {
            if (getParentFragment() instanceof MainGiftStickerCardFragment) {
                ((MainGiftStickerCardFragment) getParentFragment()).dismiss();
            }
            sendOtherListener.onClick(v);
        });
    }
}
