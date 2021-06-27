package net.iGap.fragments.inquiryBill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentPaymentInquiryMobileBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

public class FragmentPaymentInquiryMobile extends BaseFragment {

    private FragmentPaymentInquiryMobileBinding binding;
    private PaymentInquiryMobileViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(PaymentInquiryMobileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_inquiry_mobile, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.bills_inquiry_mci))
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessageId -> {
            if (errorMessageId != null) {
                HelperError.showSnackMessage(getString(errorMessageId), false);
            }
        });

        viewModel.getGoToShowInquiryBillPage().observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), ShowBillInquiryFragment.getInstance(data, R.string.bills_inquiry_mci)).setReplace(false).load(true);
            }
        });
    }
}
