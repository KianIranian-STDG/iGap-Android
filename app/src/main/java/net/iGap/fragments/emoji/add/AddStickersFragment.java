package net.iGap.fragments.emoji.add;


import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGStickerCategory;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.MyAppBarLayout;
import net.iGap.observers.rx.ObserverFragment;
import net.iGap.viewmodel.AddStickerViewModel;

public class AddStickersFragment extends ObserverFragment<AddStickerViewModel> {

    private AddStickerFragmentAdapter adapter;
    private String category;
    private String type;
    private String title;
    private CustomTextViewMedium customTextViewMedium;
    private RippleView buttonBack;
    private MyAppBarLayout addStickerToolbar;

    private ProgressBar progressBar;

    public static AddStickersFragment newInstance(String category, String type, String title) {
        AddStickersFragment addStickersFragment = new AddStickersFragment();
        addStickersFragment.category = category;
        addStickersFragment.type = type;
        addStickersFragment.title = title;
        return addStickersFragment;
    }

    @Override
    public AddStickerViewModel getObserverViewModel() {
        return ViewModelProviders.of(this).get(AddStickerViewModel.class);
    }

    @Override
    public void setupViews() {
        progressBar = rootView.findViewById(R.id.progress_stricker);
        addStickerToolbar = rootView.findViewById(R.id.toolbar2);
        addStickerToolbar.setBackgroundColor(Theme.getColor(Theme.key_toolbar_background));
        customTextViewMedium = rootView.findViewById(R.id.title);
        buttonBack = rootView.findViewById(R.id.chl_ripple_back_Button);

        if (title == null) addStickerToolbar.setVisibility(View.GONE);
        else customTextViewMedium.setText(title);

        RecyclerView recyclerView = rootView.findViewById(R.id.rcvSettingPage);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int iPage, int totalItemsCount, RecyclerView view) {
                viewModel.onPageEnded();
            }
        };
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStackFragment();
            }
        });

        recyclerView.addOnScrollListener(scrollListener);

        adapter.setListener(new AddStickerFragmentAdapter.AddStickerAdapterListener() {
            @Override
            public void onButtonClick(StructIGStickerGroup stickerGroup, AddStickerFragmentAdapter.ProgressStatus progressStatus) {
                viewModel.onItemButtonClicked(stickerGroup, sticker -> {
                    if (sticker.getGroupId().equals(stickerGroup.getGroupId())) {
                        progressStatus.setVisibility(sticker.isInUserList());
                    }
                });
            }

            @Override
            public void onCellClick(StructIGStickerGroup stickerGroup) {
                viewModel.onItemCellClicked(stickerGroup);
            }

            @Override
            public void onButtonStatusChange(AddStickerFragmentAdapter.ButtonsStatus buttonsStatus) {
                viewModel.getButtonStatusChangedLiveData().observe(getViewLifecycleOwner(), buttonsStatus::changed);
            }
        });


        viewModel.getOpenStickerDetailLiveData().observe(getViewLifecycleOwner(), sticker -> openFragmentAddStickerToFavorite(sticker.getGroupId()));

        viewModel.getLoadMoreProgressLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));

        viewModel.getStickerGroupLiveData().observe(getViewLifecycleOwner(), stickerGroup -> adapter.addData(stickerGroup));
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_add_stickers;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setCategory(category);
        viewModel.setType(type);
        adapter = new AddStickerFragmentAdapter();
    }

    private void openFragmentAddStickerToFavorite(String groupId) {
        StructIGStickerGroup stickerGroup = new StructIGStickerGroup(groupId);

        StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup, true);

        if (getFragmentManager() != null)
            dialogFragment.show(getFragmentManager(), "dialogFragment");
    }
}
