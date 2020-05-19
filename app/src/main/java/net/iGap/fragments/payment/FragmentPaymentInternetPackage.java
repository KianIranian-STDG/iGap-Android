package net.iGap.fragments.payment;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.adapter.MySpinnerAdapter;
import net.iGap.adapter.payment.internetpackage.AdapterInternetPackage;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

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

        String operator = MTN;
        String simType = SIM_TYPE_CREDIT;
        String phoneNumber = null;
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
                    if (getActivity() != null && result.isSuccess()) {
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
                paymentInternetPackageViewModel.setPackageType(item);
                adapterOthers.unSelectCurrent();
            });
        }
        if (adapterOthers == null) {
            adapterOthers = new AdapterInternetPackage();
            rvOtherPackages.setLayoutManager(new LinearLayoutManager(getContext()));
            rvOtherPackages.setAdapter(adapterOthers);
            adapterOthers.setSelectedListener(item -> {
                paymentInternetPackageViewModel.setPackageType(item);
                adapterProposal.unSelectCurrent();
            });
        }
    }
}
