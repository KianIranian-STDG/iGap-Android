package net.iGap.igasht.locationdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationDetailBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.favoritelocation.IGashtFavoritePlaceListFragment;
import net.iGap.igasht.historylocation.IGashtHistoryPlaceListFragment;
import net.iGap.igasht.locationdetail.buyticket.IGhashtBuyTicketFragment;
import net.iGap.igasht.locationdetail.subdetail.IGashtLocationSubDetailFragment;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.payment.PaymentFragment;

public class IGashtLocationDetailFragment extends Fragment {

    private FragmentIgashtLocationDetailBinding binding;
    private IGashtLocationDetailViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location_detail, container, false);
        binding.setViewModel(viewModel);
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
                .setRightIcons(R.string.score_star_icon, R.string.history_icon)
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
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtFavoritePlaceListFragment()).setReplace(false).load(true);
                        }
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }
                }).getView());

        viewModel.getLoadBuyTicketView().observe(getViewLifecycleOwner(), loadBuyTicketView -> {
            if (loadBuyTicketView != null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                if (loadBuyTicketView) {
                    fragment = fragmentManager.findFragmentByTag(IGhashtBuyTicketFragment.class.getName());
                    if (fragment == null) {
                        fragment = new IGhashtBuyTicketFragment();
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

        viewModel.getRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (getContext() != null && errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getActivity() != null && isGoBack != null && isGoBack) {
                Toast.makeText(getActivity(), R.string.successful_payment, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        });

        viewModel.getGoPayment().observe(getViewLifecycleOwner(), orderToken -> {
            if (getActivity() != null && orderToken != null) {
                new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(PaymentFragment.getInstance(orderToken)).setReplace(false).load(true);
            }
        });

        viewModel.getPaymentError().observe(getViewLifecycleOwner(), isError -> {
            if (getContext() != null && isError != null && isError) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerVouchers() {
        viewModel.registerOrder();
    }
}
