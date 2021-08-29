package net.iGap.fragments.igasht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.igahst.PlaceHistoryAdapter;
import net.iGap.databinding.FragmentIgashtHistoryPlaceBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.igasht.IGashtHistoryPlaceViewModel;

public class IGashtHistoryPlaceListFragment extends IGashtBaseView<IGashtHistoryPlaceViewModel> {

    private FragmentIgashtHistoryPlaceBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtHistoryPlaceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_history_place, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.order_history))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        binding.favoriteList.setAdapter(new PlaceHistoryAdapter(position -> viewModel.onClickHistoryItem(position)));
        binding.favoriteList.addItemDecoration(new DividerItemDecoration(binding.favoriteList.getContext(), DividerItemDecoration.VERTICAL));
        binding.favoriteList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.getLayoutManager() != null) {
                viewModel.loadMoreItems(recyclerView.getLayoutManager().getItemCount(), ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition());
                }
            }
        });

        viewModel.getHistoryList().observe(getViewLifecycleOwner(), data -> {
            if (data != null && binding.favoriteList.getAdapter() instanceof PlaceHistoryAdapter) {
                ((PlaceHistoryAdapter) binding.favoriteList.getAdapter()).setItems(data);
            }
        });

        viewModel.getGoToTicketDetail().observe(getViewLifecycleOwner(), voucherNumber -> {
            if (getActivity() != null && voucherNumber != null) {
                Fragment fragment = new FragmentIgashtBarcodeScan();
                Bundle bundle = new Bundle();
                bundle.putString("voucher_number", voucherNumber);
                fragment.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(true);
            }
        });
    }
}
