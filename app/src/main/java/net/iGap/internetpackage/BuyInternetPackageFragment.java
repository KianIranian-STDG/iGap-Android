package net.iGap.internetpackage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.MySpinnerAdapter;
import net.iGap.databinding.FragmentBuyInternetPackageBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
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
                .setDefaultTitle(getString(R.string.buy_internet_package_title))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessageResId -> {
            if (errorMessageResId != null) {
                HelperError.showSnackMessage(getString(errorMessageResId), false);
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage.getMessage(), false);
            }
        });

        viewModel.getTypeList().observe(getViewLifecycleOwner(), typeList -> {
            hideKeyboard();
            if (typeList != null) {
                binding.filterType.setAdapter(new MySpinnerAdapter(typeList));
            } else {
                binding.filterType.setSelection(0);
            }
        });

        viewModel.getInternetPackageFiltered().observe(getViewLifecycleOwner(), packageList -> {
            if (packageList != null) {
                binding.packageList.setAdapter(new InternetPackageListAdapter(packageList));
            } else {
                binding.packageList.setSelection(0);
            }
        });

        viewModel.getNeedUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeed -> {
            if (getActivity() instanceof ActivityMain && isNeed != null && isNeed) {
                ((ActivityMain) getActivity()).checkGoogleUpdate();
            }
        });

        viewModel.getGoToPaymentPage().observe(getViewLifecycleOwner(), token -> {
            if (getActivity() != null && token != null) {
                new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_internet_package_title), token, result -> {
                    Log.wtf(this.getClass().getName(), "on back pressed : " + result.isSuccess());
                    if (getActivity() != null && result.isSuccess()) {
                        getActivity().onBackPressed();
                    }
                });
            }
        });

        viewModel.getClearTypeChecked().observe(getViewLifecycleOwner(), isCleared -> {
            if (isCleared != null && isCleared) {
                binding.typeGroup.clearCheck();
            }
        });
    }
}
