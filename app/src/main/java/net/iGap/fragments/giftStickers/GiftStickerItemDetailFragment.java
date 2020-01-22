package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentGiftStickerItemDetailBinding;
import net.iGap.fragments.chatMoneyTransfer.ParentChatMoneyTransferFragment;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.HelperCalander;

import java.text.DecimalFormat;

public class GiftStickerItemDetailFragment extends Fragment {
    private StructIGSticker sticker;

    private GiftStickerItemDetailFragment() {
    }

    public static GiftStickerItemDetailFragment getInstance(StructIGSticker structIGSticker) {
        GiftStickerItemDetailFragment detailFragment = new GiftStickerItemDetailFragment();
        detailFragment.sticker = structIGSticker;
        return detailFragment;
    }

    private GiftStickerItemDetailViewModel viewModel;
    private FragmentGiftStickerItemDetailBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(GiftStickerItemDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_sticker_item_detail, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.setStickerItem(sticker);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.stickerView.loadSticker(sticker);

        DecimalFormat df = new DecimalFormat("#,###");

        String text = "شما استیکر " + (HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Double.valueOf(sticker.getGiftAmount()))) : df.format(Double.valueOf(sticker.getGiftAmount()))) + " " + getResources().getString(R.string.rial) + "ی را انتخاب کردید!";
        binding.stickerPrice.setText(text);

        viewModel.getGetPaymentLiveData().observe(getViewLifecycleOwner(), token -> {
            if (token != null) {
                if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                    ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
                    ((ParentChatMoneyTransferFragment) getParentFragment()).delegate.onGiftStickerGetStartPayment(sticker, token);
                } else if (getParentFragment() instanceof GiftStickerMainFragment) {
                    ((GiftStickerMainFragment) getParentFragment()).loadPaymentFragment(sticker, token);
                }
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment && isGoBack != null && isGoBack) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });
    }
}
