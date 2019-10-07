package net.iGap.fragments.cPay;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.iGap.R;
import net.iGap.adapter.cPay.AdapterPlaqueList;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentCpayBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentSeePayViewModel;

import java.util.ArrayList;
import java.util.List;


public class FragmentCPay extends BaseAPIViewFrag implements ToolbarListener {

    private FragmentSeePayViewModel seePayViewModel;
    private FragmentCpayBinding binding;
    private AdapterPlaqueList adapter;
    private List<String> plaqueList = new ArrayList<>();

    public FragmentCPay() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seePayViewModel = ViewModelProviders.of(this).get(FragmentSeePayViewModel.class);
        viewModel = seePayViewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cpay, container, false);
        binding.setViewModel(seePayViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initCallBacks();

    }

    private void initCallBacks() {

        seePayViewModel.getOnAddClickListener().observe(getViewLifecycleOwner(), isOpen -> {
            openEditOrAddFragment(null);
        });

        seePayViewModel.getOnInquiryClickListener().observe(getViewLifecycleOwner(), isOpen -> {
            if (getActivity() == null) return;

            if (adapter.getSelectedPlaqueList().size() == 0) {
                Toast.makeText(getContext(), getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentCPayInquiry.getInstance(adapter.getSelectedPlaqueList().get(0)))
                    .setReplace(false)
                    .load();
        });

        seePayViewModel.getOnChargeClickListener().observe(getViewLifecycleOwner(), isOpen -> {
            if (getActivity() == null) return;

            if (adapter.getSelectedPlaqueList().size() == 0) {
                Toast.makeText(getContext(), getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentCPayCharge.getInstance(adapter.getSelectedPlaqueList().get(0)))
                    .setReplace(false)
                    .load();
        });

        seePayViewModel.getPlaqueChangeListener().observe(getViewLifecycleOwner(), isUpdate -> {
            if (isUpdate != null && isUpdate) seePayViewModel.getPlaqueListByApi();
        });

        seePayViewModel.getLoaderListener().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading == null) return;
            handleView(isLoading ? PageState.LOADING : PageState.ERROR);
        });

        seePayViewModel.getPlaquesReceiverListener().observe(getViewLifecycleOwner(), userPlaques -> {
            if (userPlaques == null) {
                handleView(PageState.NO_CAR);
            } else {
                plaqueList = userPlaques.getData();
                updateRecyclerView();
                handleView(PageState.HAS_CAR);
            }
        });

        seePayViewModel.getMessageToUser().observe(getViewLifecycleOwner(), resID -> {
            if (resID == null) return;
            Toast.makeText(getActivity(), getString(resID), Toast.LENGTH_LONG).show();
        });

        seePayViewModel.getMessageToUserText().observe(getViewLifecycleOwner(), s -> {
            if (s == null) return;
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        });

    }

    private void handleView(PageState state) {
        switch (state) {
            case NO_CAR:
                binding.retry.setVisibility(View.GONE);
                binding.loader.setVisibility(View.GONE);
                binding.stateHasCar.setVisibility(View.GONE);
                binding.stateNoCar.setVisibility(View.VISIBLE);
                break;

            case HAS_CAR:
                binding.retry.setVisibility(View.GONE);
                binding.loader.setVisibility(View.GONE);
                binding.stateHasCar.setVisibility(View.VISIBLE);
                binding.stateNoCar.setVisibility(View.GONE);
                break;

            case LOADING:
                binding.retry.setVisibility(View.GONE);
                binding.loader.setVisibility(View.VISIBLE);
                binding.stateHasCar.setVisibility(View.GONE);
                binding.stateNoCar.setVisibility(View.GONE);
                break;

            case ERROR:
                binding.retry.setVisibility(View.VISIBLE);
                binding.loader.setVisibility(View.GONE);
                binding.stateHasCar.setVisibility(View.GONE);
                binding.stateNoCar.setVisibility(View.GONE);
                break;
        }

    }

    private void updateRecyclerView() {

        if (adapter == null) {
            adapter = new AdapterPlaqueList(getActivity());
            adapter.setPlaqueList(plaqueList);
            binding.rvPlaques.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.rvPlaques.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        adapter.onEditClickListener.observe(getViewLifecycleOwner(), plaque -> {
            if (plaque != null) {
                openEditOrAddFragment(plaque);
            }
        });
    }

    private void openEditOrAddFragment(String plaque) {
        if (getActivity() == null) return;

        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentCPayEdit.getInstance(plaque))
                .setReplace(false)
                .load();
    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.c_pay_title))
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.history_icon)
                .setListener(this);

        binding.fspToolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onRightIconClickListener(View view) {

        if (getActivity() == null) return;

        new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentCPayHistory())
                .setReplace(false)
                .load();

    }

    enum PageState {
        LOADING, HAS_CAR, NO_CAR, ERROR
    }
}
