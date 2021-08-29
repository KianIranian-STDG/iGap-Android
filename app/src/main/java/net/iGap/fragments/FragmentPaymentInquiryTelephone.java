package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentPaymentInquiryTelephoneBinding;
import net.iGap.fragments.inquiryBill.PaymentInquiryTelephoneViewModel;
import net.iGap.fragments.inquiryBill.ShowBillInquiryFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaymentInquiryTelephone extends BaseFragment {

    private PaymentInquiryTelephoneViewModel viewModel;
    private FragmentPaymentInquiryTelephoneBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(PaymentInquiryTelephoneViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_inquiry_telephone, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.bills_inquiry_telecom))
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        viewModel.getHideKeyword().observe(getViewLifecycleOwner(), isHide -> {
            if (isHide != null && isHide) {
                hideKeyboard();
            }
        });

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), messageResId -> {
            if (messageResId != null) {
                HelperError.showSnackMessage(getString(messageResId), false);
            }
        });

        viewModel.getGoToShowInquiryBillPage().observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), ShowBillInquiryFragment.getInstance(data, R.string.bills_inquiry_telecom)).setReplace(false).load(true);
            }
        });

    }


}