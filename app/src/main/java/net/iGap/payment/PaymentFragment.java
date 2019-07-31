package net.iGap.payment;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import net.iGap.R;
import net.iGap.api.apiService.ApiStatic;
import net.iGap.databinding.FragmentUniversalPaymentBinding;

public class PaymentFragment extends Fragment {

    private static String TOKEN = "Payment_Token";
    private static String TYPE = "Payment_Type";

    private PaymentViewModel viewModel;
    private FragmentUniversalPaymentBinding binding;

    public static PaymentFragment getInstance(String type, String token) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, token);
        bundle.putString(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(
                this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        if (getArguments() != null) {
                            return (T) new PaymentViewModel(getArguments().getString(TOKEN), getArguments().getString(TYPE));
                        } else {
                            return (T) new PaymentViewModel(null, null);
                        }
                    }
                }
        ).get(PaymentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_universal_payment, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getActivity() != null && isGoBack != null && isGoBack) {
                //todo: set callback for cancel payment
                getActivity().onBackPressed();
            }
        });

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (getContext() != null && errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getGoToWebPage().observe(getViewLifecycleOwner(), webLink -> {
            if (getActivity() != null && webLink != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webLink));
                Bundle bundle = new Bundle();
                bundle.putString("Authorization", ApiStatic.USER_TOKEN);
                browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle);
                startActivity(browserIntent);
            }
        });

        viewModel.getNeedUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeed -> {
            if (getActivity() != null && isNeed != null && isNeed) {
                try {
                    if (getActivity() != null) {
                        ProviderInstaller.installIfNeeded(getActivity().getApplicationContext());
                    }
                } catch (GooglePlayServicesRepairableException e) {
                    // Prompt the user to install/update/enable Google Play services.
                    GoogleApiAvailability.getInstance().showErrorNotification(getActivity(), e.getConnectionStatusCode());
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Indicates a non-recoverable error: let the user know.
                    showDialogNeedGooglePlay();

                }
            }
        });
    }

    public void setPaymentResult(Payment paymentModel) {
        viewModel.setPaymentResult(paymentModel);
    }

    //ToDo: create base view for fragment with request
    private void showDialogNeedGooglePlay() {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.attention).titleColor(Color.parseColor("#1DE9B6"))
                    .titleGravity(GravityEnum.CENTER)
                    .buttonsGravity(GravityEnum.CENTER)
                    .content("برای استفاده از این بخش نیاز به گوگل سرویس است.").contentGravity(GravityEnum.CENTER)
                    .positiveText(R.string.ok).onPositive((dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
}
