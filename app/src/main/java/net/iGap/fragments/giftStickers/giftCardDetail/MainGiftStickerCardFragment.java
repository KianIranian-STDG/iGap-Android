package net.iGap.fragments.giftStickers.giftCardDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.R;
import net.iGap.dialog.BaseBottomSheet;
import net.iGap.fragments.emoji.struct.StructIGSticker;

public class MainGiftStickerCardFragment extends BaseBottomSheet {
    private StructIGSticker structIGSticker;
    private int mode = 0;
    private boolean canForward;

    public static MainGiftStickerCardFragment getInstance(StructIGSticker structIGSticker, boolean canForward, int mode) {
        MainGiftStickerCardFragment mainGiftStickerCardFragment = new MainGiftStickerCardFragment();
        mainGiftStickerCardFragment.structIGSticker = structIGSticker;
        mainGiftStickerCardFragment.mode = mode;
        mainGiftStickerCardFragment.canForward = canForward;
        return mainGiftStickerCardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            loadEnterNationalCodeForActivatePage(canForward);
        } else if (mode == 1) {
            loadGiftStickerCardDetailFragment();
        }
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    public void loadEnterNationalCodeForActivatePage(boolean canSendToOther) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof EnterNationalCodeForActivateGiftStickerFragment)) {
            fragment = EnterNationalCodeForActivateGiftStickerFragment.getInstance(canSendToOther);
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
}
