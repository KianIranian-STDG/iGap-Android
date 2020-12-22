package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.databinding.FragmentMyGiftStickerRevievedBinding;
import net.iGap.fragments.emoji.struct.StructIGGiftSticker;
import net.iGap.fragments.giftStickers.giftCardDetail.MainGiftStickerCardFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.module.EndlessRecyclerViewScrollListener;

import java.util.List;

public class MyGiftStickerReceivedFragment extends Fragment {

    private FragmentMyGiftStickerRevievedBinding binding;
    private MyGiftStickerReceivedViewModel viewModel;
    private static final String TAG = "MyGiftStickerReceiveTag";
    private RecyclerView rvGifts;

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
        rvGifts = view.findViewById(R.id.giftStickerList);
        if (getParentFragment() instanceof GiftStickerMainFragment) {
            ((GiftStickerMainFragment) getParentFragment()).setToolbarTitle(R.string.my_recived_gift_sticker);
        }

        rvGifts.setAdapter(new MyStickerListAdapter(1));

        if (rvGifts.getAdapter() instanceof MyStickerListAdapter) {
            ((MyStickerListAdapter) rvGifts.getAdapter()).setDelegate((giftSticker, progressDelegate) -> new HelperFragment(getFragmentManager()).loadActiveGiftStickerCard(giftSticker.getStructIGSticker(), null, MainGiftStickerCardFragment.SHOW_CARD_INFO));
        }


        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });

        viewModel.getLoadMoreProgressLiveData().observe(getViewLifecycleOwner(), visibility -> binding.loadingView.setVisibility(visibility));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvGifts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int iPage, int totalItemsCount, RecyclerView view) {
                viewModel.onPageEnded();
            }
        });


        viewModel.getLoadStickerList().observe(getViewLifecycleOwner(), giftStickerList -> ((MyStickerListAdapter) rvGifts.getAdapter()).setItems(giftStickerList));


        rvGifts.setLayoutManager(layoutManager);
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
