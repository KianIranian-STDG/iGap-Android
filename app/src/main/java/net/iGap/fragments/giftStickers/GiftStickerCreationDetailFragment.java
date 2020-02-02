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
import net.iGap.helper.HelperFragment;

public class GiftStickerCreationDetailFragment extends BaseBottomSheet {
    private StructIGGiftSticker structIGGiftSticker;
    private View.OnClickListener clickListener;
    private boolean canForward;

    private GiftStickerCreationDetailFragment() {
    }

    public static GiftStickerCreationDetailFragment getInstance(StructIGGiftSticker giftSticker, View.OnClickListener onClickListener) {
        GiftStickerCreationDetailFragment detailFragment = new GiftStickerCreationDetailFragment();
        detailFragment.structIGGiftSticker = giftSticker;
        detailFragment.canForward = giftSticker.isValid();
        detailFragment.clickListener = onClickListener;
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

        View activeBtn = view.findViewById(R.id.btn_active);

        activeBtn.setVisibility(canForward ? View.VISIBLE : View.GONE);

        activeBtn.setOnClickListener(v -> {
            new HelperFragment(getParentFragmentManager()).loadActiveGiftStickerCard(structIGGiftSticker.getStructIGSticker(), structIGGiftSticker.isForward(), clickListener, 0);
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
