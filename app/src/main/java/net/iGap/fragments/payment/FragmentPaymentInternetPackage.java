package net.iGap.fragments.payment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import net.iGap.R;
import net.iGap.adapter.MySpinnerAdapter;
import net.iGap.adapter.payment.internetpackage.AdapterInternetPackage;
import net.iGap.api.ChargeApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.paymentPackage.InternetPackage;
import net.iGap.observers.interfaces.ToolbarListener;

import org.jetbrains.annotations.NotNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.fragments.payment.FragmentPaymentInternet.SIM_TYPE_CREDIT;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MTN;

public class FragmentPaymentInternetPackage extends BaseFragment {

    private static final String PARAM_OPERATOR = "PARAM_OPERATOR";
    private static final String PARAM_SIM_TYPE = "PARAM_SIM_TYPE";
    private static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";

    private PaymentInternetPackageViewModel paymentInternetPackageViewModel;
    private AdapterInternetPackage adapterProposal;
    private AdapterInternetPackage adapterOthers;
    private RecyclerView rvSuggested;
    private RecyclerView rvOtherPackages;
    private AppCompatTextView suggestedTextView;
    private AppCompatTextView othersTextView;
    private Spinner spinnerTime;
    private Spinner spinnerTraffic;
    private int timeFilterPosition = -1;
    private int trafficFilterPosition = -1;
    private LinearLayout toolbar;
    private MaterialButton btnPay;
    private ProgressBar loadingView;
    private NestedScrollView scrollView;
    private ChargeApi chargeApi;
    private String operator;
    private String simType;
    private String phoneNumber;
    private InternetPackage currentInternetPackage;

    public static FragmentPaymentInternetPackage newInstance(String phoneNumber, String operator, String simType) {

        Bundle args = new Bundle();
        args.putString(PARAM_PHONE_NUMBER, phoneNumber);
        args.putString(PARAM_OPERATOR, operator);
        args.putString(PARAM_SIM_TYPE, simType);

        FragmentPaymentInternetPackage fragment = new FragmentPaymentInternetPackage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            operator = getArguments().getString(PARAM_OPERATOR, MTN);
            simType = getArguments().getString(PARAM_SIM_TYPE);
            phoneNumber = getArguments().getString(PARAM_PHONE_NUMBER);
        }

        paymentInternetPackageViewModel = ViewModelProviders.of(this).get(PaymentInternetPackageViewModel.class);
        paymentInternetPackageViewModel.setOperator(operator);
        paymentInternetPackageViewModel.setSimType(simType);
        paymentInternetPackageViewModel.setPhoneNumber(phoneNumber);
        paymentInternetPackageViewModel.getData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_internet_packages, container, false);
        rvSuggested = view.findViewById(R.id.rv_proposalPackage);
        rvOtherPackages = view.findViewById(R.id.rv_otherPackage);
        suggestedTextView = view.findViewById(R.id.suggested_packages_textView);
        othersTextView = view.findViewById(R.id.other_packages_textView);
        spinnerTime = view.findViewById(R.id.spinner_time);
        spinnerTraffic = view.findViewById(R.id.spinner_traffic);
        toolbar = view.findViewById(R.id.toolbar);
        btnPay = view.findViewById(R.id.btn_pay);
        loadingView = view.findViewById(R.id.loadingView);
        scrollView = view.findViewById(R.id.scrollView);

        chargeApi = new RetrofitFactory().getChargeRetrofit();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerViews();

        toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(getString(R.string.buy_internet_package_title))
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        paymentInternetPackageViewModel.getTimeFilterListObservable().observe(getViewLifecycleOwner(), timeFilterList -> {
            hideKeyboard();
            if (timeFilterList != null) {
                spinnerTime.setAdapter(new MySpinnerAdapter(timeFilterList));
            } else {
                spinnerTime.setSelection(0);
            }
        });

        paymentInternetPackageViewModel.getTrafficFilterListObservable().observe(getViewLifecycleOwner(), trafficFilter -> {
            hideKeyboard();
            if (trafficFilter != null) {
                spinnerTraffic.setAdapter(new MySpinnerAdapter(trafficFilter));
            } else {
                spinnerTraffic.setSelection(0);
            }
        });

        paymentInternetPackageViewModel.getPackageListSuggestedObservable().observe(getViewLifecycleOwner(), internetPackages -> {
            if (internetPackages == null || internetPackages.size() == 0) {
                rvSuggested.setVisibility(View.GONE);
                suggestedTextView.setVisibility(View.GONE);
                return;
            }
            rvSuggested.setVisibility(View.VISIBLE);
            suggestedTextView.setVisibility(View.VISIBLE);

            adapterProposal.setData(internetPackages);
        });

        paymentInternetPackageViewModel.getPackageListOthersObservable().observe(getViewLifecycleOwner(), internetPackages -> {
            if (internetPackages == null || internetPackages.size() == 0) {
                rvOtherPackages.setVisibility(View.GONE);
                othersTextView.setVisibility(View.GONE);
                return;
            }
            rvOtherPackages.setVisibility(View.VISIBLE);
            othersTextView.setVisibility(View.VISIBLE);

            adapterOthers.setData(internetPackages);
        });

        paymentInternetPackageViewModel.getGoToPaymentPage().observe(getViewLifecycleOwner(), token -> {
            if (getActivity() != null && token != null) {

                new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_internet_package_title), true, token, result -> {
                    if (result.isSuccess()) {
                        if (currentInternetPackage != null) {
                            savePayment();
                        }
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }
                });
            }
        });

        paymentInternetPackageViewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorRes -> {
            showError(getResources().getString(errorRes));
        });

        paymentInternetPackageViewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        paymentInternetPackageViewModel.getLoadingVisibility().observe(getViewLifecycleOwner(), showLoading -> {
            if (showLoading == null)
                return;

            if (showLoading) {
                loadingView.setVisibility(View.VISIBLE);
                btnPay.setEnabled(false);
            } else {
                loadingView.setVisibility(View.GONE);
                btnPay.setEnabled(true);
            }
        });

        paymentInternetPackageViewModel.getIsDataLoaded().observe(getViewLifecycleOwner(), isDataLoaded -> {
            if (isDataLoaded != null && isDataLoaded) {
                loadingView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                btnPay.setVisibility(View.VISIBLE);
            }
        });

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeFilterPosition = position - 1;
                paymentInternetPackageViewModel.updateInternetPackager(timeFilterPosition, trafficFilterPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTraffic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trafficFilterPosition = position - 1;
                paymentInternetPackageViewModel.updateInternetPackager(timeFilterPosition, trafficFilterPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnPay.setOnClickListener(v -> paymentInternetPackageViewModel.requestPayment());
    }

    public void showError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void setupRecyclerViews() {
        if (adapterProposal == null) {
            adapterProposal = new AdapterInternetPackage();
            rvSuggested.setLayoutManager(new LinearLayoutManager(getContext()));
            rvSuggested.setAdapter(adapterProposal);
            adapterProposal.setSelectedListener(item -> {
                currentInternetPackage = item;
                paymentInternetPackageViewModel.setPackageType(item);
                adapterOthers.unSelectCurrent();
            });
        }
        if (adapterOthers == null) {
            adapterOthers = new AdapterInternetPackage();
            rvOtherPackages.setLayoutManager(new LinearLayoutManager(getContext()));
            rvOtherPackages.setAdapter(adapterOthers);
            adapterOthers.setSelectedListener(item -> {
                currentInternetPackage = item;
                paymentInternetPackageViewModel.setPackageType(item);
                adapterProposal.unSelectCurrent();
            });
        }
    }

    public void savePayment() {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.save_purchase))
                .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                .positiveText(R.string.ok)
                .onNegative((dialog1, which) -> dialog1.dismiss()).show();
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("phone_number", phoneNumber);
            jsonObject.addProperty("package_type", String.valueOf(currentInternetPackage.getType()));
            jsonObject.addProperty("charge_type", simType);
            chargeApi.setFavoriteInternetPackage(operator, jsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    loadingView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loadingView.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getResources().getString(R.string.server_do_not_response), false);
                }
            });
            dialog.dismiss();
        });
    }
}
