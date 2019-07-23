package net.iGap.igasht.locationdetail.buyticket;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtBuyTicketBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.helper.HelperLog;

import java.util.ArrayList;

public class IGhashtBuyTicketFragment extends Fragment {

    private static String LocationId = "LocationId";

    private FragmentIgashtBuyTicketBinding binding;
    private IGashtBuyTicketViewModel viewModel;

    public static IGhashtBuyTicketFragment getInstance(int locationId) {
        IGhashtBuyTicketFragment fragment = new IGhashtBuyTicketFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LocationId, locationId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtBuyTicketViewModel.class);
        //todo: create factory provider and remove init function;
        if (getArguments() != null) {
            viewModel.setLocationId(getArguments().getInt(LocationId));
        } else {
            HelperLog.setErrorLog(new Exception(this.getClass().getName() + ": selected location id not found"));
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_buy_ticket, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addedPlaceList.setAdapter(new OrderedTicketListAdapter(new ArrayList<>(), position -> {
            //call when remove item click
            viewModel.removeOrderedTicket(position);
            ((OrderedTicketListAdapter)binding.addedPlaceList.getAdapter()).removeItem(position);
        }));

        viewModel.getShowDialogSelectService().observe(getViewLifecycleOwner(), serviceList -> {
            if (getFragmentManager() != null && serviceList != null) {
                new BottomSheetFragment().setData(serviceList, -1, position -> viewModel.selectedService(position)).setTitle(getString(R.string.igasht_place_of_visit_title))
                        .show(getFragmentManager(), "selectService");
            }
        });

        viewModel.getShowDialogSelectTicketType().observe(getViewLifecycleOwner(), amountType -> {
            if (getFragmentManager() != null && amountType != null) {
                new SelectTicketTypeBottomSheetFragment().setData(amountType, new AddBottomSheetListener<IGashtServiceAmount>() {
                    @Override
                    public void setTicketCount(IGashtServiceAmount data) {
                        viewModel.selectedTicketType(data);
                    }

                    @Override
                    public void onBackPressed() {
                        viewModel.onAddPlaceClick();
                    }
                }).show(getFragmentManager(), "selectTicketType");
            }
        });

        viewModel.getShowDialogEnterCount().observe(getViewLifecycleOwner(), isShow -> {
            if (getFragmentManager() != null && isShow != null) {
                new IGashtEnterTicketCountBottomSheetFragment().setCallBack(new AddBottomSheetListener<Integer>() {
                    @Override
                    public void setTicketCount(Integer ticketCount) {
                        viewModel.setTicketCount(ticketCount);
                    }

                    @Override
                    public void onBackPressed() {
                        Log.wtf(this.getClass().getName(), "onBackPressed");
                        viewModel.selectedService();
                    }
                }).show(getFragmentManager(), "setTicketCount");
            }
        });

        viewModel.getAddToTicketList().observe(getViewLifecycleOwner(),data->{
            if (binding.addedPlaceList.getAdapter() instanceof OrderedTicketListAdapter && data != null) {
                ((OrderedTicketListAdapter) binding.addedPlaceList.getAdapter()).addNewItem(data);
            }
        });
    }
}
