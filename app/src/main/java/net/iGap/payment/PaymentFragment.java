package net.iGap.payment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.api.apiService.ApiStatic;
import net.iGap.databinding.FragmentUniversalPaymentBinding;

public class PaymentFragment extends Fragment {

    private static String TOKEN = "Payment_Token";

    private PaymentViewModel viewModel;
    private FragmentUniversalPaymentBinding binding;

    public static PaymentFragment getInstance(String token) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, token);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(
                this,
                new PaymentViewModelFactory(getArguments() != null ? getArguments().getString(TOKEN) : null)
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


                Intent intent = new Intent("net.iGap.payment");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                Bundle b = new Bundle();
                b.putString("message", "data of message");
                b.putString("status","data of status");
                intent.putExtras(b);

                Log.wtf(this.getClass().getName(), intent.toUri(Intent.URI_INTENT_SCHEME));

                /*startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webLink)));*/
            }
        });
    }
}
