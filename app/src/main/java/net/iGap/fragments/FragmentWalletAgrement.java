package net.iGap.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.databinding.FragmentWalletAgrementBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentWalletAgreementViewModel;

import org.jetbrains.annotations.NotNull;
import org.paygear.WalletActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWalletAgrement extends BaseFragment {

    private FragmentWalletAgrementBinding binding;
    private FragmentWalletAgreementViewModel viewModel;
    private final static String PHONE = "PATH";

    public static FragmentWalletAgrement newInstance(String phone, boolean isScan) {
        Bundle args = new Bundle();
        args.putString(PHONE, phone);
        args.putBoolean("isScan", isScan);
        FragmentWalletAgrement fragmentWalletAgrement = new FragmentWalletAgrement();
        fragmentWalletAgrement.setArguments(args);
        return fragmentWalletAgrement;
    }

    public static FragmentWalletAgrement newInstance(String phone) {
        return newInstance(phone, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                String mPhone = null;
                boolean isScan = false;
                if (getArguments() != null) {
                    mPhone = getArguments().getString(PHONE);
                    isScan = getArguments().getBoolean("isScan", false);
                }
                return (T) new FragmentWalletAgreementViewModel(mPhone, isScan);
            }
        }).get(FragmentWalletAgreementViewModel.class);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet_agrement, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.wallet_agrement))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        binding.fwaLayoutToolbar.addView(toolbar.getView());

        viewModel.getShowDialogAcceptTerms().observe(getViewLifecycleOwner(), isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity()).title(R.string.accept_the_terms).
                        content(R.string.are_you_sure)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> viewModel.acceptTerms()).show();
            }
        });

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), messageRes -> {
            if (messageRes != null) {
                HelperError.showSnackMessage(getString(messageRes), false);
            }
        });

        viewModel.getGoToWalletPage().observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
                intent.putExtra("Language", "fa");
                intent.putExtra("Mobile", "0" + data.getPhone());
                intent.putExtra("PrimaryColor", new Theme().getPrimaryColor(getContext()));
                intent.putExtra("DarkPrimaryColor",new Theme().getPrimaryColor(getContext()));
                intent.putExtra("AccentColor",new Theme().getPrimaryColor(getContext()));
                intent.putExtra("IS_DARK_THEME", G.themeColor == Theme.DARK);
                intent.putExtra(WalletActivity.PROGRESSBAR, new Theme().getAccentColor(getContext()));
                intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                intent.putExtra(WalletActivity.BACKGROUND, new Theme().getRootColor(getContext()));
                intent.putExtra(WalletActivity.BACKGROUND_2, new Theme().getRootColor(getContext()));
                intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                intent.putExtra("isScan", data.isScan());
                getActivity().startActivity(intent);
                getActivity().onBackPressed();
            }
        });
    }
}