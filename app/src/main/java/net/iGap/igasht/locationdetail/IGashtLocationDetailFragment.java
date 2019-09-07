package net.iGap.igasht.locationdetail;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationDetailBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.igasht.historylocation.IGashtHistoryPlaceListFragment;
import net.iGap.igasht.locationdetail.buyticket.IGashtBuyTicketFragment;
import net.iGap.igasht.locationdetail.subdetail.IGashtLocationSubDetailFragment;
import net.iGap.interfaces.ToolbarListener;

public class IGashtLocationDetailFragment extends IGashtBaseView {

    private FragmentIgashtLocationDetailBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location_detail, container, false);
        binding.setViewModel((IGashtLocationDetailViewModel) viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.history_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }
                }).getView());

        ((IGashtLocationDetailViewModel) viewModel).getLoadBuyTicketView().observe(getViewLifecycleOwner(), loadBuyTicketView -> {
            if (loadBuyTicketView != null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                if (loadBuyTicketView) {
                    fragment = fragmentManager.findFragmentByTag(IGashtBuyTicketFragment.class.getName());
                    if (fragment == null) {
                        fragment = new IGashtBuyTicketFragment();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                } else {
                    fragment = fragmentManager.findFragmentByTag(IGashtLocationSubDetailFragment.class.getName());
                    if (fragment == null) {
                        fragment = new IGashtLocationSubDetailFragment();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                }
                fragmentTransaction.replace(R.id.detailFrame, fragment, fragment.getClass().getName()).commit();
            }
        });

        ((IGashtLocationDetailViewModel) viewModel).getGoHistoryPage().observe(getViewLifecycleOwner(), voucherNumber -> {
            if (voucherNumber != null) {
                goToHistoryListPage();
            }
        });

        ((IGashtLocationDetailViewModel) viewModel).getGoPayment().observe(getViewLifecycleOwner(), orderToken -> {
            if (getActivity() != null && orderToken != null) {
                new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.igasht_title), orderToken, result -> {
                    if (result.isSuccess()) {
                        goToHistoryListPage();
                    }
                });
            }
        });

        ((IGashtLocationDetailViewModel) viewModel).getPaymentError().observe(getViewLifecycleOwner(), isError -> {
            if (getContext() != null && isError != null && isError) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerVouchers() {
        ((IGashtLocationDetailViewModel) viewModel).registerOrder();
    }

    private void goToHistoryListPage() {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
        }
    }
}
