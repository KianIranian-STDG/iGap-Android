package net.iGap.fragments.cPay;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.adapter.seePay.AdapterPlaqueList;
import net.iGap.databinding.FragmentSeePayBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentSeePayViewModel;

import java.util.ArrayList;


public class FragmentCPay extends BaseFragment implements ToolbarListener {

    private FragmentSeePayViewModel viewModel ;
    private FragmentSeePayBinding binding ;
    private AdapterPlaqueList adapter ;
    private ArrayList<String> plaqueList = new ArrayList<>();

    public FragmentCPay() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentSeePayViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater ,R.layout.fragment_see_pay, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initRecyclerView();

    }

    private void initRecyclerView() {

        plaqueList.add("110214566");
        plaqueList.add("184426588");

        adapter = new AdapterPlaqueList(getActivity());
        adapter.setPlaqueList(plaqueList);
        binding.rvPlaques.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPlaques.setAdapter(adapter);

        adapter.onEditClickListener.observe(getViewLifecycleOwner() , plaque -> {
            if (plaque != null){
                new HelperFragment(getActivity().getSupportFragmentManager() , FragmentCPayEdit.getInstance(plaque))
                        .setReplace(false)
                        .load();
            }
        });
    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.see_pay_title))
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

    }
}
