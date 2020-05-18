package net.iGap.fragments.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.payment.internetpackage.AdapterProposalPackage;
import net.iGap.adapter.payment.internetpackage.AdapterRecentlyPackage;
import net.iGap.fragments.BaseFragment;

public class FragmentPaymentInternetPackage extends BaseFragment {

    private AdapterProposalPackage adapterProposal;
    private AdapterRecentlyPackage adapterRecently;
    private RecyclerView rvSuggested;
    private RecyclerView rvRecently;
    private Spinner spinnerTime;
    private Spinner spinnerVolume;


    public static FragmentPaymentInternetPackage newInstance() {

        Bundle args = new Bundle();

        FragmentPaymentInternetPackage fragment = new FragmentPaymentInternetPackage();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_internet_packages, container, false);
        rvSuggested = view.findViewById(R.id.rv_proposalPackage);
        rvRecently = view.findViewById(R.id.rv_recentlyPackage);
        spinnerTime = view.findViewById(R.id.spinner_time);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapterRecently = new AdapterRecentlyPackage();
        rvRecently.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvRecently.setAdapter(adapterRecently);

        adapterProposal = new AdapterProposalPackage();
        rvSuggested.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvSuggested.setAdapter(adapterProposal);
    }
}
