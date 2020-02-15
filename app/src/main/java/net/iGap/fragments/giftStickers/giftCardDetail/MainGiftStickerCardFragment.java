package net.iGap.fragments.giftStickers.giftCardDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.BaseBottomSheet;
import net.iGap.fragments.emoji.struct.StructIGSticker;

public class MainGiftStickerCardFragment extends BaseBottomSheet {
    private StructIGSticker structIGSticker;
    private int mode = 0;
    private boolean canForward;
    private View.OnClickListener sendOtherListener;

    public static MainGiftStickerCardFragment getInstance(StructIGSticker structIGSticker, boolean canForward, View.OnClickListener sendOtherListener, int mode) {
        MainGiftStickerCardFragment mainGiftStickerCardFragment = new MainGiftStickerCardFragment();
        mainGiftStickerCardFragment.structIGSticker = structIGSticker;
        mainGiftStickerCardFragment.mode = mode;
        mainGiftStickerCardFragment.canForward = canForward;
        mainGiftStickerCardFragment.sendOtherListener = sendOtherListener;
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
        } else if (G.getNationalCode() == null) {
            loadEnterNationalCodeForActivatePage(true);
        } else if (mode == 1 && G.getNationalCode() != null) {
            loadGiftStickerCardDetailFragment();
        }
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    private void loadEnterNationalCodeForActivatePage(boolean canForward) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof EnterNationalCodeForActivateGiftStickerFragment)) {
            fragment = EnterNationalCodeForActivateGiftStickerFragment.getInstance(structIGSticker, sendOtherListener, canForward);
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
