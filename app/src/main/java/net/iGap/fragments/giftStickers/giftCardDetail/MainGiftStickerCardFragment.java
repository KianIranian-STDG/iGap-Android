package net.iGap.fragments.giftStickers.giftCardDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.R;
import net.iGap.dialog.BaseBottomSheet;
import net.iGap.fragments.emoji.struct.StructIGSticker;

public class MainGiftStickerCardFragment extends BaseBottomSheet {
    private StructIGSticker structIGSticker;
    private MainStickerCardViewModel viewModel;
    private int mode = 0;

    public static MainGiftStickerCardFragment getInstance(StructIGSticker structIGSticker, int mode) {
        MainGiftStickerCardFragment mainGiftStickerCardFragment = new MainGiftStickerCardFragment();
        mainGiftStickerCardFragment.structIGSticker = structIGSticker;
        mainGiftStickerCardFragment.mode = mode;
        return mainGiftStickerCardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new MainStickerCardViewModel(structIGSticker);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent_chat_money_transfer, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mode == 0) {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            viewModel.subscribe();
            viewModel.getGoNextLiveData().observe(getViewLifecycleOwner(), goNext -> {
                if (goNext != null && !goNext) {
                    loadEnterNationalCodeForActivatePage();
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "این کارت هدیه قبلا استفاده شده است!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        } else if (mode == 1) {
            loadGiftStickerCardDetailFragment();
        }
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    public void loadEnterNationalCodeForActivatePage() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof EnterNationalCodeForActivateGiftStickerFragment)) {
            fragment = new EnterNationalCodeForActivateGiftStickerFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadGiftStickerCardDetailFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof GiftStickerCardDetailFragment)) {
            fragment = GiftStickerCardDetailFragment.getInstance(structIGSticker, mode);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
