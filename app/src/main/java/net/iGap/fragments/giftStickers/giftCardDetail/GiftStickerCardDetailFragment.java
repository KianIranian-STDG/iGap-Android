package net.iGap.fragments.giftStickers.giftCardDetail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentGiftStickerCardDetailBinding;

import static android.content.Context.CLIPBOARD_SERVICE;

public class GiftStickerCardDetailFragment extends Fragment {

    private GiftStickerCardDetailViewModel viewModel;
    private FragmentGiftStickerCardDetailBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                String cardId = null;
                if (getArguments() != null) {
                    cardId = getArguments().getString("cardId", null);
                }
                return (T) new GiftStickerCardDetailViewModel(cardId);
            }
        }).get(GiftStickerCardDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_sticker_card_detail, container, false);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getCopyValue().observe(getViewLifecycleOwner(), value -> {
            if (getActivity() != null) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Gift Sticker", value);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
