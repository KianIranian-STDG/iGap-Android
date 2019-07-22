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

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationDetailBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.favoritelocation.IGashtFavoritePlaceListFragment;
import net.iGap.igasht.historylocation.IGashtHistoryPlaceListFragment;
import net.iGap.igasht.locationdetail.buyticket.IGhashtBuyTicketFragment;
import net.iGap.igasht.locationdetail.subdetail.IGashtLocationSubDetailFragment;
import net.iGap.igasht.locationlist.IGashtLocationItem;
import net.iGap.interfaces.ToolbarListener;

public class IGashtLocationDetailFragment extends Fragment {

    private FragmentIgashtLocationDetailBinding binding;
    private IGashtLocationDetailViewModel viewModel;

    private static String LOCATION_ITEM = "locationItem";

    public static IGashtLocationDetailFragment getInstance(IGashtLocationItem locationItem) {
        IGashtLocationDetailFragment fragment = new IGashtLocationDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LOCATION_ITEM, locationItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationDetailViewModel.class);
        //todo: create factory provider and remove init function;
        if (getArguments() != null) {
            viewModel.setLocationItem(getArguments().getParcelable(LOCATION_ITEM));
        } else {
            HelperLog.setErrorLog(new Exception(this.getClass().getName() + ": selected location data not found"));
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
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

        viewModel.getLoadBuyTicketView().observe(getViewLifecycleOwner(), locationId -> {
            if (locationId!=null){
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(IGhashtBuyTicketFragment.class.getName());
                if (fragment == null) {
                    fragment = IGhashtBuyTicketFragment.getInstance(locationId);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.detailFrame, fragment, fragment.getClass().getName()).commit();
            }
        });

        viewModel.getLoadDetailSubView().observe(getViewLifecycleOwner(), locationDetail -> {
            if (locationDetail != null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(IGashtLocationSubDetailFragment.class.getName());
                if (fragment == null) {
                    fragment = IGashtLocationSubDetailFragment.getInstance(locationDetail);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.detailFrame, fragment, fragment.getClass().getName()).commit();
            }
        });
    }
}
