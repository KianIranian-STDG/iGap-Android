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

public class MainGiftStickerCardFragment extends BaseBottomSheet {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent_chat_money_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadEnterNationalCodeForActivatePage();
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

    public void loadGiftStickerCardDetailFragment(String cardId) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("cardId", cardId);
        if (!(fragment instanceof GiftStickerCardDetailFragment)) {
            fragment = new GiftStickerCardDetailFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }
}
