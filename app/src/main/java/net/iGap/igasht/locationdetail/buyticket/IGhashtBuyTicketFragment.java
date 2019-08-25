package net.iGap.igasht.locationdetail.buyticket;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtBuyTicketBinding;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.igasht.IGashtBaseView;
import net.iGap.igasht.locationdetail.IGashtLocationDetailFragment;

import java.util.ArrayList;

public class IGhashtBuyTicketFragment extends IGashtBaseView {

    private FragmentIgashtBuyTicketBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtBuyTicketViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_buy_ticket, container, false);
        binding.setViewModel((IGashtBuyTicketViewModel) viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addedPlaceList.addItemDecoration(new DividerItemDecoration(binding.addedPlaceList.getContext(), DividerItemDecoration.VERTICAL));
        binding.addedPlaceList.setAdapter(new OrderedTicketListAdapter(new ArrayList<>(), totalPrice -> ((IGashtBuyTicketViewModel) viewModel).setTotalPrice(totalPrice)));

        ((IGashtBuyTicketViewModel) viewModel).getServiceList().observe(getViewLifecycleOwner(), ticketList -> {
            if (binding.addedPlaceList.getAdapter() instanceof OrderedTicketListAdapter && ticketList != null) {
                ((OrderedTicketListAdapter) binding.addedPlaceList.getAdapter()).addNewItem(ticketList);
            }
        });

        /*((IGashtBuyTicketViewModel) viewModel).getShowDialogSelectService().observe(getViewLifecycleOwner(), serviceList -> {
            if (getFragmentManager() != null && serviceList != null) {
                new BottomSheetFragment().setData(serviceList, -1, position -> ((IGashtBuyTicketViewModel) viewModel).selectedService(position)).setTitle(getString(R.string.igasht_place_of_visit_title))
                        .show(getFragmentManager(), "selectService");
            }
        });*/

        /*((IGashtBuyTicketViewModel) viewModel).getShowDialogSelectTicketType().observe(getViewLifecycleOwner(), amountType -> {
            if (getFragmentManager() != null && amountType != null) {
                new SelectTicketTypeBottomSheetFragment().setData(amountType, new TicketListCountChangeListener<IGashtServiceAmount>() {
                    @Override
                    public void setTicketCount(IGashtServiceAmount data) {
                        ((IGashtBuyTicketViewModel) viewModel).selectedTicketType(data);
                    }

                    @Override
                    public void onBackPressed() {
                        ((IGashtBuyTicketViewModel) viewModel).onAddPlaceClick();
                    }
                }).show(getFragmentManager(), "selectTicketType");
            }
        });*/

        /*((IGashtBuyTicketViewModel) viewModel).getShowDialogEnterCount().observe(getViewLifecycleOwner(), isShow -> {
            if (getFragmentManager() != null && isShow != null) {
                new IGashtEnterTicketCountBottomSheetFragment().setCallBack(new TicketListCountChangeListener<Integer>() {
                    @Override
                    public void setTicketCount(Integer ticketCount) {
                        ((IGashtBuyTicketViewModel) viewModel).setTicketCount(ticketCount);
                    }

                    @Override
                    public void onBackPressed() {
                        Log.wtf(this.getClass().getName(), "onBackPressed");
                        ((IGashtBuyTicketViewModel) viewModel).selectedService();
                    }
                }).show(getFragmentManager(), "setTicketCount");
            }
        });*/

        /*((IGashtBuyTicketViewModel) viewModel).getAddToTicketList().observe(getViewLifecycleOwner(), data -> {

        });*/

        ((IGashtBuyTicketViewModel) viewModel).getRegisterVoucher().observe(getViewLifecycleOwner(), registerVoucher -> {
            if (registerVoucher != null) {
                if (registerVoucher) {
                    if (getParentFragment() instanceof IGashtLocationDetailFragment) {
                        ((IGashtLocationDetailFragment) getParentFragment()).registerVouchers();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.igasht_add_ticket_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
