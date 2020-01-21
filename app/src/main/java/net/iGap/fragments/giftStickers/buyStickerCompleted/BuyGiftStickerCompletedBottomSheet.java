package net.iGap.fragments.giftStickers.buyStickerCompleted;

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

public class BuyGiftStickerCompletedBottomSheet extends BaseBottomSheet {

    private Delegate delegate;
    private StructIGSticker structIGSticker;

    private BuyGiftStickerCompletedBottomSheet() {
    }

    public static BuyGiftStickerCompletedBottomSheet getInstance(StructIGSticker structIGSticker) {
        BuyGiftStickerCompletedBottomSheet bottomSheet = new BuyGiftStickerCompletedBottomSheet();
        bottomSheet.structIGSticker = structIGSticker;
        return bottomSheet;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent_chat_money_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof BuyGiftStickerCompletedFragment)) {
            fragment = BuyGiftStickerCompletedFragment.getInstance(structIGSticker, delegate);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }

    public interface Delegate {
        void onNegativeButton(StructIGSticker structIGSticker);

        void onPositiveButton(StructIGSticker structIGSticker);
    }
}
