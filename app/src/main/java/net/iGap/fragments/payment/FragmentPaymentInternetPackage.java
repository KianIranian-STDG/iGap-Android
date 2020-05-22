package net.iGap.fragments.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MySpinnerAdapter;
import net.iGap.adapter.payment.internetpackage.InternetPackageAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.paymentPackage.InternetPackage;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.PaymentInternetPackageViewModel;

import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MTN;

public class FragmentPaymentInternetPackage extends BaseFragment {

    private static final String PARAM_OPERATOR = "PARAM_OPERATOR";
    private static final String PARAM_SIM_TYPE = "PARAM_SIM_TYPE";
    private static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
    private static final String PARAM_PACKAGE_TYPE = "PARAM_PACKAGE_TYPE";

    private PaymentInternetPackageViewModel viewModel;
    private InternetPackageAdapter proposalAdapter;
    private InternetPackageAdapter othersAdapter;
    private RecyclerView suggestedRecyclerView;
    private RecyclerView otherPackagesRecyclerView;
    private AppCompatTextView suggestedTextView;
    private AppCompatTextView othersTextView;
    private Spinner spinnerTime;
    private Spinner spinnerTraffic;
    private int timeFilterPosition = -1;
    private int trafficFilterPosition = -1;
    private LinearLayout toolbar;
    private MaterialButton payBtn;
    private View loadingView;
    private NestedScrollView scrollView;
    private String operator;
    private String simType;
    private String phoneNumber;
    private int packageType = -1;
    private boolean inScroll;
    private InternetPackage currentInternetPackage;

    public static FragmentPaymentInternetPackage newInstance(String phoneNumber, String operator, String simType, int packageType) {

        Bundle args = new Bundle();
        args.putString(PARAM_PHONE_NUMBER, phoneNumber);
        args.putString(PARAM_OPERATOR, operator);
        args.putString(PARAM_SIM_TYPE, simType);
        args.putInt(PARAM_PACKAGE_TYPE, packageType);

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
            packageType = getArguments().getInt(PARAM_PACKAGE_TYPE);
        }

        viewModel = ViewModelProviders.of(this).get(PaymentInternetPackageViewModel.class);
        viewModel.setOperator(operator);
        viewModel.setSimType(simType);
        viewModel.setPhoneNumber(phoneNumber);
        viewModel.getData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_internet_packages, container, false);
        suggestedRecyclerView = view.findViewById(R.id.rv_proposalPackage);
        otherPackagesRecyclerView = view.findViewById(R.id.rv_otherPackage);
        suggestedTextView = view.findViewById(R.id.suggested_packages_textView);
        othersTextView = view.findViewById(R.id.other_packages_textView);
        spinnerTime = view.findViewById(R.id.spinner_time);
        spinnerTraffic = view.findViewById(R.id.spinner_traffic);
        toolbar = view.findViewById(R.id.toolbar);
        payBtn = view.findViewById(R.id.btn_pay);
        loadingView = view.findViewById(R.id.loadingView);
        scrollView = view.findViewById(R.id.scrollView);

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

        viewModel.getTimeFilterListObservable().observe(getViewLifecycleOwner(), timeFilterList -> {
            hideKeyboard();
            if (timeFilterList != null) {
                spinnerTime.setAdapter(new MySpinnerAdapter(timeFilterList));
            } else {
                spinnerTime.setSelection(0);
            }
        });

        viewModel.getTrafficFilterListObservable().observe(getViewLifecycleOwner(), trafficFilter -> {
            hideKeyboard();
            if (trafficFilter != null) {
                spinnerTraffic.setAdapter(new MySpinnerAdapter(trafficFilter));
            } else {
                spinnerTraffic.setSelection(0);
            }
        });

        viewModel.getPackageListSuggestedObservable().observe(getViewLifecycleOwner(), internetPackages -> {
            if (internetPackages == null || internetPackages.size() == 0) {
                suggestedRecyclerView.setVisibility(View.GONE);
                suggestedTextView.setVisibility(View.GONE);
                return;
            }
            suggestedRecyclerView.setVisibility(View.VISIBLE);
            suggestedTextView.setVisibility(View.VISIBLE);

            proposalAdapter.setData(internetPackages, packageType);
        });


        viewModel.getPackageListOthersObservable().observe(getViewLifecycleOwner(), internetPackages -> {
            if (internetPackages == null || internetPackages.size() == 0) {
                otherPackagesRecyclerView.setVisibility(View.GONE);
                othersTextView.setVisibility(View.GONE);
                return;
            }
            otherPackagesRecyclerView.setVisibility(View.VISIBLE);
            othersTextView.setVisibility(View.VISIBLE);

            othersAdapter.setData(internetPackages, packageType);

            dataDidLoad();
        });

        viewModel.getGoToPaymentPage().observe(getViewLifecycleOwner(), token -> {
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

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorRes -> {
            showError(getResources().getString(errorRes));
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        viewModel.getLoadingVisibility().observe(getViewLifecycleOwner(), showLoading -> {
            if (showLoading == null)
                return;

            if (showLoading) {
                loadingView.setVisibility(View.VISIBLE);
                payBtn.setEnabled(false);
            } else {
                loadingView.setVisibility(View.GONE);
                payBtn.setEnabled(true);
            }
        });

        viewModel.getIsDataLoaded().observe(getViewLifecycleOwner(), isDataLoaded -> {
            if (isDataLoaded != null && isDataLoaded) {
                loadingView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                payBtn.setVisibility(View.VISIBLE);
            }
        });

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeFilterPosition = position - 1;
                viewModel.updateInternetPackager(timeFilterPosition, trafficFilterPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTraffic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trafficFilterPosition = position - 1;
                viewModel.updateInternetPackager(timeFilterPosition, trafficFilterPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payBtn.setOnClickListener(v -> viewModel.requestPayment());
    }

    private void dataDidLoad() {
        if (packageType != -1 && proposalAdapter.getCurrentlySelectedPosition() != -1) {
            viewModel.setPackageType(currentInternetPackage = proposalAdapter.getData().get(proposalAdapter.getCurrentlySelectedPosition()));
            scrollToSuggestion(true);
        } else if (packageType != -1 && othersAdapter.getCurrentlySelectedPosition() != -1) {
            viewModel.setPackageType(currentInternetPackage = othersAdapter.getData().get(othersAdapter.getCurrentlySelectedPosition()));
            scrollToSuggestion(false);
        } else if (packageType != -1) {
            showError(getResources().getString(R.string.no_package_selected));
        }
    }

    public void showError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void setupRecyclerViews() {
        if (proposalAdapter == null) {
            proposalAdapter = new InternetPackageAdapter();
            suggestedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            suggestedRecyclerView.setAdapter(proposalAdapter);
            proposalAdapter.setSelectedListener(item -> {
                currentInternetPackage = item;
                viewModel.setPackageType(item);
                othersAdapter.unSelectCurrent();
            });
        }
        if (othersAdapter == null) {
            othersAdapter = new InternetPackageAdapter();
            otherPackagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            otherPackagesRecyclerView.setAdapter(othersAdapter);
            othersAdapter.setSelectedListener(item -> {
                currentInternetPackage = item;
                viewModel.setPackageType(item);
                proposalAdapter.unSelectCurrent();
            });
        }
    }

    private void scrollToSuggestion(boolean inSuggestion) {
        G.runOnUiThread(() -> {
            if (inScroll)
                return;

            try {
                inScroll = true;
                if (inSuggestion) {
                    RecyclerListView.ViewHolder viewHolder = suggestedRecyclerView.findViewHolderForAdapterPosition(proposalAdapter.getCurrentlySelectedPosition());
                    if (viewHolder != null) {
                        scrollView.scrollTo((int) viewHolder.itemView.getX(), (int) viewHolder.itemView.getY() + 300);
                    }
                } else {
                    RecyclerListView.ViewHolder viewHolder = otherPackagesRecyclerView.findViewHolderForAdapterPosition(othersAdapter.getCurrentlySelectedPosition());
                    if (viewHolder != null) {
                        scrollView.scrollTo((int) viewHolder.itemView.getX(), (int) (viewHolder.itemView.getY() + suggestedRecyclerView.getBottom() + 600));
                    }
                }
            } catch (Exception e) {
                showError(getResources().getString(R.string.no_package_selected));
                e.printStackTrace();
            }

        }, 500);
    }

    private void savePayment() {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(getResources().getString(R.string.save_purchase))
                .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                .positiveText(R.string.ok)
                .onNegative((dialog1, which) -> dialog1.dismiss()).show();

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
            viewModel.savePayment();
            dialog.dismiss();
        });
    }
}
