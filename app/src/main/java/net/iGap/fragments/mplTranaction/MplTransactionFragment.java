package net.iGap.fragments.mplTranaction;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;

public class MplTransactionFragment extends BaseFragment {

    private static final String TAG = "abbasiMpl";
    private View rootView;
    private MplTransactionViewModel viewModel;
    private MplTransActionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView typeAllTv;
    private TextView typeTopUpTv;
    private TextView typeCardToCardTv;
    private TextView typeSalesTv;
    private TextView typeBillTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mpl_trancaction, container, false);
        viewModel = new MplTransactionViewModel();
        adapter = new MplTransActionAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();

        viewModel.getMplTransactionLiveData().observe(getViewLifecycleOwner(), mplTransaction -> {
            if (mplTransaction != null) {
                adapter.setTransAction(mplTransaction);
            }
        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null)
                swipeRefreshLayout.setRefreshing(progress);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getAllMplTransactionList());

        adapter.setCallBack(token -> {
            if (token != null)
                new HelperFragment(getFragmentManager(), MplTransactionInfoFragment.getInstance(token)).setReplace(false).load();
        });

        typeAllTv.setOnClickListener(v -> {
            setEnableButton(typeAllTv, typeBillTv, typeCardToCardTv, typeSalesTv, typeTopUpTv);
            viewModel.getAllMplTransactionList();
        });

        typeBillTv.setOnClickListener(v -> {
            setEnableButton(typeBillTv, typeAllTv, typeCardToCardTv, typeSalesTv, typeTopUpTv);
            viewModel.getBillMplTransactionList();
        });

        typeCardToCardTv.setOnClickListener(v -> {
            setEnableButton(typeCardToCardTv, typeAllTv, typeBillTv, typeSalesTv, typeTopUpTv);
            viewModel.getCardToCardMplTransactionList();
        });

        typeSalesTv.setOnClickListener(v -> {
            setEnableButton(typeSalesTv, typeAllTv, typeBillTv, typeCardToCardTv, typeTopUpTv);
            viewModel.getSaleMplTransactionList();
        });

        typeTopUpTv.setOnClickListener(v -> {
            setEnableButton(typeTopUpTv, typeAllTv, typeBillTv, typeCardToCardTv, typeSalesTv);
            viewModel.getTopUpMplTransactionList();
        });

    }

    private void setUpViews() {
        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_mplTransaction_toolBar);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_mplTransaction);
        swipeRefreshLayout = rootView.findViewById(R.id.sl_itemMplTransAction);
        typeAllTv = rootView.findViewById(R.id.tv_mplTransaction_all);
        typeTopUpTv = rootView.findViewById(R.id.tv_mplTransaction_topup);
        typeCardToCardTv = rootView.findViewById(R.id.tv_mplTransaction_cardToCard);
        typeSalesTv = rootView.findViewById(R.id.tv_mplTransaction_sales);
        typeBillTv = rootView.findViewById(R.id.tv_mplTransaction_bill);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        setEnableButton(typeAllTv, typeBillTv, typeCardToCardTv, typeSalesTv, typeTopUpTv);
        viewModel.getAllMplTransactionList();

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext());
        toolBarContainer.addView(toolbar.getView());
    }


    private void setEnableButton(TextView enable, TextView disable, TextView disable2, TextView disable3, TextView disable4) {

        //use revert for dark theme : disable drawable is light and enable drawable is dark
        if (G.isDarkTheme) {
            enable.setBackground(getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable.setBackground(getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable2.setBackground(getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable3.setBackground(getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable4.setBackground(getResources().getDrawable(R.drawable.round_button_enabled_bg));

            enable.setTextColor(getResources().getColor(R.color.black));
            disable.setTextColor(getResources().getColor(R.color.white));
            disable2.setTextColor(getResources().getColor(R.color.white));
            disable3.setTextColor(getResources().getColor(R.color.white));
            disable4.setTextColor(getResources().getColor(R.color.white));
        } else {
            enable.setBackground(getResources().getDrawable(R.drawable.round_button_selected_bg));
            disable.setBackground(getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable2.setBackground(getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable3.setBackground(getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable4.setBackground(getResources().getDrawable(R.drawable.round_button_disabled_bg));

            enable.setTextColor(getResources().getColor(R.color.white));
            disable.setTextColor(getResources().getColor(R.color.black));
            disable2.setTextColor(getResources().getColor(R.color.black));
            disable3.setTextColor(getResources().getColor(R.color.black));
            disable4.setTextColor(getResources().getColor(R.color.black));
        }
    }

}
