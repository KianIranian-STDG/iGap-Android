package net.iGap.fragments.giftStickers;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperError;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.observers.rx.ObserverFragment;

public class GiftStickerPurchasedByMeFragment extends ObserverFragment<MyGiftStickerBuyViewModel> {

    public static final int NEW = 0;
    public static final int ACTIVE = 1;
    public static final int FORWARD = 2;

    private int mode;

    private RecyclerView recyclerView;
    private ProgressBar loadingProgressView;
    private ProgressBar loadMoreProgressView;
    private TextView emptyTv;
    private TextView retryTv;

    private GiftStickerPurchasedByMeFragment() {
    }

    public static GiftStickerPurchasedByMeFragment getInstance(int mode) {
        GiftStickerPurchasedByMeFragment fragment = new GiftStickerPurchasedByMeFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setMode(mode);
    }

    @Override
    public void setupViews() {
        recyclerView = rootView.findViewById(R.id.rv_giftStickerPurchasedByMe);
        loadingProgressView = rootView.findViewById(R.id.pb_giftStickerPurchasedByMe_loading);
        loadMoreProgressView = rootView.findViewById(R.id.pb_giftStickerPurchasedByMe_loadMore);
        emptyTv = rootView.findViewById(R.id.tv_giftStickerPurchasedByMe_emptyView);
        retryTv = rootView.findViewById(R.id.ic_giftStickerPurchasedByMe_retryIcon);

        if (getParentFragment() instanceof GiftStickerMainFragment) {
            ((GiftStickerMainFragment) getParentFragment()).setToolbarTitle(R.string.my_gift_sticker);
        }

        recyclerView.setAdapter(new MyStickerListAdapter(0));

        viewModel.getLoadStickerList().observe(getViewLifecycleOwner(), stickerList -> {
            if (recyclerView.getAdapter() instanceof MyStickerListAdapter && stickerList != null) {
                ((MyStickerListAdapter) recyclerView.getAdapter()).setItems(stickerList);
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });

        viewModel.getGoNext().observe(getViewLifecycleOwner(), giftSticker -> {
            if (giftSticker != null) {
                GiftStickerCreationDetailFragment detailFragment = GiftStickerCreationDetailFragment.getInstance(giftSticker, v -> {
                    if (getActivity() instanceof ActivityMain) {

                        FragmentChat.structIGSticker = giftSticker.getStructIGSticker();

                        ((ActivityMain) getActivity()).setForwardMessage(true);
                        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                    }
                });
                detailFragment.show(getParentFragmentManager(), null);
            }
        });

        if (recyclerView.getAdapter() instanceof MyStickerListAdapter) {
            ((MyStickerListAdapter) recyclerView.getAdapter()).setDelegate((giftSticker, progressDelegate) -> viewModel.onItemClicked(giftSticker, progressDelegate));
        }

        retryTv.setOnClickListener(v -> viewModel.onRetryButtonClick());

        viewModel.getShowEmptyListMessage().observe(getViewLifecycleOwner(), visibility -> emptyTv.setVisibility(visibility));
        viewModel.getShowLoading().observe(getViewLifecycleOwner(), visibility -> loadingProgressView.setVisibility(visibility));
        viewModel.getShowRetryView().observe(getViewLifecycleOwner(), visibility -> retryTv.setVisibility(visibility));

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int iPage, int totalItemsCount, RecyclerView view) {
                viewModel.onPageEnded();
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_gift_sticker_purchased_by_me;
    }

    @Override
    public MyGiftStickerBuyViewModel getObserverViewModel() {
        return new MyGiftStickerBuyViewModel();
    }

    public int getMode() {
        return mode;
    }
}
