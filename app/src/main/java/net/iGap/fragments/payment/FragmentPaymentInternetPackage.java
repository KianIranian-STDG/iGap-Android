package net.iGap.fragments.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.payment.internetpackage.AdapterProposalPackage;
import net.iGap.adapter.payment.internetpackage.AdapterRecentlyPackage;
import net.iGap.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentPaymentInternetPackage extends BaseFragment {

    private AppCompatSpinner spinnerTime;
    private AppCompatSpinner spinnerSize;
    private RecyclerView rvRecently;
    private RecyclerView rvProposal;
    private AdapterProposalPackage adapterProposal;
    private AdapterRecentlyPackage adapterRecently;

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
        spinnerTime = view.findViewById(R.id.spinner_time);
        spinnerSize = view.findViewById(R.id.spinner_size);
        rvRecently = view.findViewById(R.id.rv_recentlyPackage);
        rvProposal = view.findViewById(R.id.rv_proposalPackage);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapterRecently = new AdapterRecentlyPackage();
        rvRecently.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvRecently.setAdapter(adapterRecently);

        adapterProposal = new AdapterProposalPackage();
        rvProposal.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvProposal.setAdapter(adapterProposal);

        setSpinnerData();
    }

    private void setSpinnerData() {
        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> timeList = new ArrayList<>();
        timeList.add("همه");
        timeList.add("روزانه");
        ArrayAdapter<String> timeDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, timeList);
        timeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeDataAdapter);

        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> sizeList = new ArrayList<>();
        sizeList.add("همه");
        sizeList.add("یک گیگ");
        ArrayAdapter<String> sizeDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sizeList);
        sizeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(sizeDataAdapter);

    }

}
