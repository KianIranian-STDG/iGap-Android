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

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.module.dialog.BaseBottomSheet;

public class MainGiftStickerCardFragment extends BaseBottomSheet {
    public static final int ACTIVE_BY_ME = 0;
    public static final int ACTIVE_CARD_WHIT_FORWARD = 1;
    public static final int ACTIVE_CARD_WHIT_OUT_FORWARD = 2;
    public static final int SHOW_CARD_INFO = 3;

    private StructIGSticker structIGSticker;
    private int mode = -1;

    private View.OnClickListener sendOtherListener;

    public static MainGiftStickerCardFragment getInstance(StructIGSticker structIGSticker, View.OnClickListener sendOtherListener, int mode) {
        MainGiftStickerCardFragment mainGiftStickerCardFragment = new MainGiftStickerCardFragment();
        mainGiftStickerCardFragment.structIGSticker = structIGSticker;
        mainGiftStickerCardFragment.mode = mode;
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

        if (mode == ACTIVE_BY_ME || mode == SHOW_CARD_INFO) {
            if (G.getNationalCode() == null) {
                loadEnterNationalCodeForActivatePage(false);
            } else {
                loadGiftStickerCardDetailFragment(ACTIVE_BY_ME);
            }
        } else if (mode == ACTIVE_CARD_WHIT_FORWARD) {
            loadEnterNationalCodeForActivatePage(true);
        } else if (mode == ACTIVE_CARD_WHIT_OUT_FORWARD) {
            if (G.getNationalCode() == null) {
                loadEnterNationalCodeForActivatePage(false);
            } else {
                loadGiftStickerCardDetailFragment(SHOW_CARD_INFO);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public int getCurrentMode() {
        return mode;
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    public void closeFragment(boolean hasError) {
        if (hasError)
            Toast.makeText(getContext(), R.string.normal_error, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void loadEnterNationalCodeForActivatePage(boolean canForward) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof EnterNationalCodeForActivateGiftStickerFragment)) {
            fragment = EnterNationalCodeForActivateGiftStickerFragment.getInstance(sendOtherListener, canForward);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadGiftStickerCardDetailFragment(int mode) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof GiftStickerCardDetailFragment)) {
            fragment = GiftStickerCardDetailFragment.getInstance(structIGSticker, mode);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }
}
