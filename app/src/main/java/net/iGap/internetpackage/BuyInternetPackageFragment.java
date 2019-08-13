package net.iGap.internetpackage;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.MySpinnerAdapter;
import net.iGap.databinding.FragmentBuyInternetPackageBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class BuyInternetPackageFragment extends BaseFragment {

    private BuyInternetPackageViewModel viewModel;
    private FragmentBuyInternetPackageBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(BuyInternetPackageViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_internet_package, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.buy_charge))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessageResId -> {
            if (errorMessageResId != null){
                HelperError.showSnackMessage(getString(errorMessageResId), false);
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null){
                HelperError.showSnackMessage(errorMessage.getMessage(), false);
            }
        });

        viewModel.getTypeList().observe(getViewLifecycleOwner(), typeList -> {
            if (getContext() != null) {
                if (typeList != null) {
                    binding.filterType.setAdapter(new MySpinnerAdapter(typeList));
                }
                binding.filterType.setSelection(0);
            }
        });

        viewModel.getNeedUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeed -> {
            if (getActivity() instanceof ActivityMain && isNeed != null && isNeed){
                ((ActivityMain) getActivity()).checkGoogleUpdate();
            }
        });
    }
}
