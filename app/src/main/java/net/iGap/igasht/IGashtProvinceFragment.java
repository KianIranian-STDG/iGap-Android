package net.iGap.igasht;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtProvinceBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class IGashtProvinceFragment extends Fragment {

    private FragmentIgashtProvinceBinding binding;
    private IGashtProvinceViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtProvinceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_province, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.score_star_icon, R.string.history_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity()!=null){
                            new HelperFragment(getActivity().getSupportFragmentManager(),new IGashtFavoritePlaceListFragment()).setReplace(false).load(true);
                        }
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity()!=null){
                            new HelperFragment(getActivity().getSupportFragmentManager(),new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }
                }).getView());

        viewModel.getGoToShowLocationListPage().observe(getViewLifecycleOwner(), go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtLocationListFragment()).setReplace(false).load(true);
            }
        });

    }
}
