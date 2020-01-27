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
import net.iGap.databinding.FragmentMyGiftStickerRevievedBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;

public class MyGiftStickerReceivedFragment extends Fragment {

    private FragmentMyGiftStickerRevievedBinding binding;
    private MyGiftStickerReceivedViewModel viewModel;

    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MyGiftStickerReceivedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_gift_sticker_revieved, container, false);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.subscribe();

        if (getParentFragment() instanceof GiftStickerMainFragment) {
            ((GiftStickerMainFragment) getParentFragment()).setToolbarTitle(R.string.my_recived_gift_sticker);
        }

        binding.giftStickerList.setAdapter(new MyStickerListAdapter());

        if (binding.giftStickerList.getAdapter() instanceof MyStickerListAdapter) {
            ((MyStickerListAdapter) binding.giftStickerList.getAdapter()).setDelegate(structIGSticker -> new HelperFragment(getFragmentManager()).loadActiveGiftStickerCard(structIGSticker.getStructIGSticker(), false, null, 1));
        }

        viewModel.getLoadStickerList().observe(getViewLifecycleOwner(), giftStickerList -> {
            if (binding.giftStickerList.getAdapter() instanceof MyStickerListAdapter && giftStickerList != null) {
                ((MyStickerListAdapter) binding.giftStickerList.getAdapter()).setItems(giftStickerList);
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
