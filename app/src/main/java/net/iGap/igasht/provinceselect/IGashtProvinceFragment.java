package net.iGap.igasht.provinceselect;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtProvinceBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.igasht.historylocation.IGashtHistoryPlaceListFragment;
import net.iGap.igasht.locationlist.IGashtLocationListFragment;
import net.iGap.interfaces.ToolbarListener;

public class IGashtProvinceFragment extends IGashtBaseView {
    private FragmentIgashtProvinceBinding binding;
    private IGashtProvinceViewModel iGashtProvinceViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iGashtProvinceViewModel = ViewModelProviders.of(this).get(IGashtProvinceViewModel.class);
        viewModel = iGashtProvinceViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_province, container, false);
        binding.setViewModel(iGashtProvinceViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(/*R.string.score_star_icon,*/R.string.history_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }

                  /*  @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }*/
                }).getView());

        binding.provinceSearchText.setOnItemClickListener((parent, view1, position, id) -> iGashtProvinceViewModel.setSelectedLocation(position));

        iGashtProvinceViewModel.getGoToShowLocationListPage().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null) {
                if (isGo) {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtLocationListFragment()).setReplace(false).load(true);
                } else {
                    Toast.makeText(getContext(), R.string.select_province, Toast.LENGTH_SHORT).show();
                }
            }
        });

        iGashtProvinceViewModel.getProvinceListResult().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.provinceSearchText.setAdapter(new ProvinceSuggestionListAdapter(getContext(), data));
            }
        });

        iGashtProvinceViewModel.getClearEditText().observe(getViewLifecycleOwner(), isClear -> {
            if (isClear != null && isClear) {
                binding.provinceSearchText.requestFocus();
                binding.provinceSearchText.setText("");
            }
        });
    }

}
