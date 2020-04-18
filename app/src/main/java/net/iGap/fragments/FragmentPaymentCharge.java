package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentPaymentChargeBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentPaymentChargeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FragmentPaymentCharge extends BaseAPIViewFrag<FragmentPaymentChargeViewModel> {

    private FragmentPaymentChargeBinding binding;

    public static FragmentPaymentCharge newInstance() {
        return new FragmentPaymentCharge();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentPaymentChargeViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_charge, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fpcToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
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

        binding.fpcSpinnerOperator.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(getResources().getTextArray(R.array.phone_operator))));
        binding.fpcSpinnerOperator.setSelection(0);

        viewModel.getOnOpereatorChange().observe(getViewLifecycleOwner(), listResId -> {
            if (getContext() != null) {
                if (listResId != null) {
                    binding.fpcSpinnerChargeType.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(getResources().getTextArray(listResId))));
                }
                binding.fpcSpinnerChargeType.setSelection(0);
            }
        });

        viewModel.getOnPriceChange().observe(getViewLifecycleOwner(), listResId -> {
            if (getContext() != null) {
                if (listResId != null) {
                    binding.fpcSpinnerPrice.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(getResources().getTextArray(listResId))));
                }
                binding.fpcSpinnerPrice.setSelection(0);
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (isGoBack != null && isGoBack) {
                goBack();
            }
        });

        viewModel.getGoToPaymentPage().observe(getViewLifecycleOwner(), token -> {
            if (getActivity() != null && token != null) {
                new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_charge), token, result -> {
                    if (result.isSuccess()) {
                        goBack();
                    }
                });
            }
        });

        viewModel.getHideKeyWord().observe(getViewLifecycleOwner(), isHideKeyword -> {
            if (isHideKeyword != null && isHideKeyword) {
                hideKeyboard();
            }
        });

        viewModel.getShowError().observe(getViewLifecycleOwner(), errorMessageRes -> {
            if (errorMessageRes != null) {
                hideKeyboard();
                HelperError.showSnackMessage(getString(errorMessageRes), false);
            }
        });

        viewModel.getShowMciPaymentError().observe(getViewLifecycleOwner(), errorModel -> {
            if (errorModel != null) {
                hideKeyboard();
                HelperError.showSnackMessage(errorModel.getMessage(), false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.onMplResult = null;
    }

    private void goBack() {
        G.handler.post(() -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
}