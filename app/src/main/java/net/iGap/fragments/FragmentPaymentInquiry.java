package net.iGap.fragments;


import android.arch.lifecycle.ViewModel;
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
import net.iGap.databinding.FragmentPaymentInquiryBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.IBackHandler;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentPaymentInquiryViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaymentInquiry extends BaseFragment {

    private FragmentPaymentInquiryBinding fragmentPaymentInquiryBinding;
    private FragmentPaymentInquiryViewModel viewModel;


    public static FragmentPaymentInquiry newInstance(FragmentPaymentInquiryViewModel.OperatorType type, String phone) {
        Bundle args = new Bundle();
        args.putSerializable("type", type);

        if (phone != null && phone.length() > 0) {
            args.putString("phone", phone);
        }

        FragmentPaymentInquiry fragmentPaymentInquiry = new FragmentPaymentInquiry();
        fragmentPaymentInquiry.setArguments(args);

        return fragmentPaymentInquiry;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentPaymentInquiryViewModel((FragmentPaymentInquiryViewModel.OperatorType) getArguments().getSerializable("type"));
            }
        }).get(FragmentPaymentInquiryViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPaymentInquiryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_inquiry, container, false);
        fragmentPaymentInquiryBinding.setViewModel(viewModel);
        fragmentPaymentInquiryBinding.setLifecycleOwner(this);
        return attachToSwipeBack(fragmentPaymentInquiryBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String phone = getArguments().getString("phone");
        if (phone != null && phone.length() > 0) {
            fragmentPaymentInquiryBinding.fpiEdtMci.setText(phone);
            fragmentPaymentInquiryViewModel.onInquiryClick(null);
        }

        fragmentPaymentInquiryBinding.fpiLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(viewModel.observeTitleToolbar.getValue())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

    }


}