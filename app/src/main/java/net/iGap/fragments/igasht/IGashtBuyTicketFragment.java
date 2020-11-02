package net.iGap.fragments.igasht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;

import net.iGap.R;
import net.iGap.adapter.igahst.OrderedTicketListAdapter;
import net.iGap.databinding.FragmentIgashtBuyTicketBinding;
import net.iGap.viewmodel.igasht.IGashtBuyTicketViewModel;

import java.util.ArrayList;

public class IGashtBuyTicketFragment extends IGashtBaseView<IGashtBuyTicketViewModel> {

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
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addedPlaceList.addItemDecoration(new DividerItemDecoration(binding.addedPlaceList.getContext(), DividerItemDecoration.VERTICAL));
        binding.addedPlaceList.setNestedScrollingEnabled(false);
        binding.addedPlaceList.setHasFixedSize(true);
        binding.addedPlaceList.setAdapter(new OrderedTicketListAdapter(new ArrayList<>(), totalPrice -> viewModel.setTotalPrice(totalPrice)));

        viewModel.getServiceList().observe(getViewLifecycleOwner(), ticketList -> {
            if (binding.addedPlaceList.getAdapter() instanceof OrderedTicketListAdapter && ticketList != null) {
                ((OrderedTicketListAdapter) binding.addedPlaceList.getAdapter()).addNewItem(ticketList);
            }
        });

        viewModel.getRegisterVoucher().observe(getViewLifecycleOwner(), registerVoucher -> {
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

        viewModel.getShowPriceNull().observe(getViewLifecycleOwner(), showToast -> {
            Toast.makeText(getContext(), R.string.igasht_add_ticket_error, Toast.LENGTH_SHORT).show();

        });
    }
}
