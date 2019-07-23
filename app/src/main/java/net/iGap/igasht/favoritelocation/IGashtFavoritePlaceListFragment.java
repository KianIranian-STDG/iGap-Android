package net.iGap.igasht.favoritelocation;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtFavoritePlaceBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class IGashtFavoritePlaceListFragment extends Fragment {

    private FragmentIgashtFavoritePlaceBinding binding;
    private IGashtFavoritePlaceViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtFavoritePlaceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_favorite_place, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.igasht_favorites_title))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        binding.favoriteList.setAdapter(new FavoriteListAdapter(position -> viewModel.onClickFavoriteItem(position)));
        binding.favoriteList.addItemDecoration(new DividerItemDecoration(binding.favoriteList.getContext(), DividerItemDecoration.VERTICAL));

        viewModel.getFavoriteList().observe(getViewLifecycleOwner(), data -> {
            if (data != null && binding.favoriteList.getAdapter() instanceof FavoriteListAdapter) {
                ((FavoriteListAdapter) binding.favoriteList.getAdapter()).setItems(data);
            }
        });
    }
}
