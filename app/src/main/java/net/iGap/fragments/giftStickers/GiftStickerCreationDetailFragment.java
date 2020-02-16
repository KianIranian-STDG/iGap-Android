package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.R;
import net.iGap.dialog.BaseBottomSheet;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.fragments.giftStickers.giftCardDetail.MainGiftStickerCardFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.mobileBank.repository.util.JalaliCalendar;
import net.iGap.module.AndroidUtils;

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
            new HelperFragment(getFragmentManager()).loadActiveGiftStickerCard(structIGGiftSticker.getStructIGSticker(), clickListener, structIGGiftSticker.isForward() ? MainGiftStickerCardFragment.ACTIVE_CARD_WHIT_OUT_FORWARD : MainGiftStickerCardFragment.ACTIVE_CARD_WHIT_FORWARD);
            dismiss();
        });

        TextView rrnTv = view.findViewById(R.id.tv_rrn);
        rrnTv.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(structIGGiftSticker.getRrn()) : structIGGiftSticker.getRrn());

        TextView dateTv = view.findViewById(R.id.tv_date);
        dateTv.setText(JalaliCalendar.getPersianDate(AndroidUtils.compatibleUnicode(structIGGiftSticker.getCreatedAt().replace("T", " "))));

        rrnTv.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
        dateTv.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);

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
