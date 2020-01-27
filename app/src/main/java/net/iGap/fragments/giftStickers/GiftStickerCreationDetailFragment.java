package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.dialog.BaseBottomSheet;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;

public class GiftStickerCreationDetailFragment extends BaseBottomSheet {
    private StructIGGiftSticker structIGGiftSticker;

    private GiftStickerCreationDetailFragment() {
    }

    public static GiftStickerCreationDetailFragment getInstance(StructIGGiftSticker giftSticker) {
        GiftStickerCreationDetailFragment detailFragment = new GiftStickerCreationDetailFragment();
        detailFragment.structIGGiftSticker = giftSticker;
        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gift_sticker_creation_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_active).setOnClickListener(v -> {
//            new HelperFragment(getFragmentManager()).loadActiveGiftStickerCard(structIGGiftSticker.getStructIGSticker(), 0);
            dismiss();
        });

        TextView textView = view.findViewById(R.id.tv_creatorPhoneNumber);
        textView.setText(structIGGiftSticker.getPhoneNumber());

        TextView textView1 = view.findViewById(R.id.tv_creatorNationalCode);
        textView1.setText(structIGGiftSticker.getNationalCode());

        TextView rrnTv = view.findViewById(R.id.tv_rrn);
        rrnTv.setText(structIGGiftSticker.getRrn());

    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
