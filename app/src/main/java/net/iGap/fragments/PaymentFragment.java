package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.adapter.PaymentPlansAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.databinding.FragmentUniversalPaymentBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperScreenShot;
import net.iGap.model.payment.Payment;
import net.iGap.model.payment.PaymentFeature;
import net.iGap.observers.interfaces.PaymentCallBack;
import net.iGap.viewmodel.PaymentViewModel;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class PaymentFragment extends BaseAPIViewFrag {

    private static String TOKEN = "Payment_Token";
    private static String TYPE = "Payment_Type";
    private static String IS_SHOW_VALUE_ADDED = "VALUE_ADDED";

    private FragmentUniversalPaymentBinding binding;
    private PaymentCallBack callBack;
    private PaymentViewModel paymentViewModel;
    private PaymentPlansAdapter adapter;
    private boolean isShowValueAdded = false;

    public static PaymentFragment getInstance(String type, String token, PaymentCallBack paymentCallBack) {
        PaymentFragment fragment = new PaymentFragment();
        fragment.setCallBack(paymentCallBack);
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, token);
        bundle.putString(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PaymentFragment getInstance(String type, boolean isShowValueAdded, String token, PaymentCallBack paymentCallBack) {
        PaymentFragment fragment = new PaymentFragment();
        fragment.setCallBack(paymentCallBack);
        Bundle bundle = new Bundle();
        bundle.putString(TOKEN, token);
        bundle.putString(TYPE, type);
        bundle.putBoolean(IS_SHOW_VALUE_ADDED, isShowValueAdded);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setCallBack(PaymentCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentViewModel = ViewModelProviders.of(
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
        viewModel = paymentViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_universal_payment, container, false);
        binding.setViewModel(paymentViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            isShowValueAdded = getArguments().getBoolean(IS_SHOW_VALUE_ADDED, false);
        }

        paymentViewModel.getGoBack().observe(getViewLifecycleOwner(), paymentResult -> {
            if (getActivity() != null && paymentResult != null) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                if (callBack != null) {
                    callBack.onPaymentFinished(paymentResult);
                }
            }
        });

        paymentViewModel.getGoToWebPage().observe(getViewLifecycleOwner(), webLink -> {
            if (getActivity() != null && webLink != null) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webLink));
                    Bundle bundle = new Bundle();
                    bundle.putString("Authorization", TokenContainer.getInstance().getToken());
                    browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle);
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.add_new_account, Toast.LENGTH_LONG).show();
                }
                /*CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                Bundle bundle = new Bundle();
                bundle.putString("Authorization", G.getApiToken());
                intent.intent.putExtra(Browser.EXTRA_HEADERS, bundle);
                CustomTabsActivityHelper.openCustomTab(getActivity(), intent, Uri.parse(webLink), (activity, uri) -> {

                });*/
            }
        });

        binding.paymentFeatureRC.setHasFixedSize(true);
        paymentViewModel.getDiscountOption().observe(getViewLifecycleOwner(), new Observer<List<PaymentFeature>>() {
            @Override
            public void onChanged(List<PaymentFeature> paymentFeatures) {
                if (paymentFeatures != null) {
                    adapter = new PaymentPlansAdapter(paymentFeatures, new PaymentPlansAdapter.PlanListListener() {
                        @Override
                        public void onPlanClicked(int position, boolean state) {
                            if (state)
                                paymentViewModel.setDiscountPlanPosition(position);
                            else
                                paymentViewModel.setDiscountPlanPosition(-1);
                        }
                    });
                    binding.paymentFeatureRC.setAdapter(adapter);
                }
            }
        });

        binding.screenshotButton.setOnClickListener(v -> {
            loadImage loadImage = new loadImage();
            loadImage.execute();
        });

        paymentViewModel.getPrice().observe(getViewLifecycleOwner(), price -> {
            if (getContext() != null && price != null) {
                DecimalFormat df = new DecimalFormat(",###");
                binding.priceTitle.setText(getString(R.string.wallet_amount) +
                        (isShowValueAdded ? (" + " + getString(R.string.value_added)) : "") + ": " +
                        (HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(price)) : price) +
                        getString(R.string.rial));
            }
        });
    }

    public void setPaymentResult(Payment paymentModel) {
        paymentViewModel.setPaymentResult(paymentModel);
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

    private class loadImage extends AsyncTask<String, String, Boolean> {

        String filename;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            Date date = new Date();
            filename = "receipt" + date.getTime() + ".jpeg";
            return HelperScreenShot.takeScreenshotAndSaveIi(binding.v, filename);

        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                Snackbar snackbar = Snackbar.make(binding.v, getResources().getString(R.string.picture_save_to_galary), Snackbar.LENGTH_LONG);
                snackbar.setAction(getResources().getString(R.string.navigation_drawer_open), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Payment_Receipt/" + filename);
                        Log.d("amini", "onClick: " + file.getAbsolutePath());
                        /*Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "image/*");*/
                        Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                                        FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file) :
                                        Uri.fromFile(file), "image/*")
                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath())));
                    }
                });
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(binding.v, getResources().getString(R.string.str_frag_sync_error), Snackbar.LENGTH_LONG);
                snackbar.setAction(getResources().getString(R.string.ok), v -> snackbar.dismiss());
                snackbar.show();
            }

            super.onPostExecute(s);
        }
    }
}
