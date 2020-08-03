package net.iGap.fragments.mplTranaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;

public class MplTransactionFragment extends BaseFragment implements ToolbarListener {

    private static final String TAG = "abbasiMpl";
    private MplTransactionViewModel viewModel;
    private MplTransactionAdapter adapter;

    private View rootView;
    private TextView typeAllTv;
    private TextView typeTopUpTv;
    private TextView typeCardToCardTv;
    private TextView typeSalesTv;
    private TextView typeBillTv;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private int page = 1;
    private int start;
    private int end;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mpl_trancaction, container, false);
        viewModel = new MplTransactionViewModel();
        adapter = new MplTransactionAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews();

        viewModel.getMplTransactionLiveData().observe(getViewLifecycleOwner(), mplTransaction -> {
            if (mplTransaction != null) {
                if (mplTransaction.size() > 0) {
                    adapter.setTransAction(mplTransaction);
                    start = adapter.getItemCount();
                    end = start + MplTransactionViewModel.PAGINATION_LIMIT;
                    page++;
                }
            }

        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null)
                swipeRefreshLayout.setRefreshing(progress);
        });

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                viewModel.getMorePageOffset(start, end);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getFirstPageMplTransactionList(viewModel.getType());
        });

        adapter.setCallBack(token -> {
            if (token != null)
                new HelperFragment(getFragmentManager(), MplTransactionInfoFragment.getInstance(token)).setReplace(false).load();
        });

        typeAllTv.setOnClickListener(v -> {
            setEnableButton(typeAllTv, typeBillTv, typeCardToCardTv, typeSalesTv, typeTopUpTv);
            viewModel.getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type.NONE);
        });

        typeBillTv.setOnClickListener(v -> {
            setEnableButton(typeBillTv, typeAllTv, typeCardToCardTv, typeSalesTv, typeTopUpTv);
            viewModel.getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type.BILL);
        });

        typeCardToCardTv.setOnClickListener(v -> {
            setEnableButton(typeCardToCardTv, typeAllTv, typeBillTv, typeSalesTv, typeTopUpTv);
            viewModel.getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type.CARD_TO_CARD);
        });

        typeSalesTv.setOnClickListener(v -> {
            setEnableButton(typeSalesTv, typeAllTv, typeBillTv, typeCardToCardTv, typeTopUpTv);
            viewModel.getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type.SALES);
        });

        typeTopUpTv.setOnClickListener(v -> {
            setEnableButton(typeTopUpTv, typeAllTv, typeBillTv, typeCardToCardTv, typeSalesTv);
            viewModel.getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type.TOPUP);
        });

    }

    private void setUpViews() {
        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_mplTransaction_toolBar);
        recyclerView = rootView.findViewById(R.id.rv_mplTransaction);
        swipeRefreshLayout = rootView.findViewById(R.id.sl_itemMplTransAction);
        typeAllTv = rootView.findViewById(R.id.tv_mplTransaction_all);
        typeTopUpTv = rootView.findViewById(R.id.tv_mplTransaction_topup);
        typeCardToCardTv = rootView.findViewById(R.id.tv_mplTransaction_cardToCard);
        typeSalesTv = rootView.findViewById(R.id.tv_mplTransaction_sales);
        typeBillTv = rootView.findViewById(R.id.tv_mplTransaction_bill);
        emptyView = rootView.findViewById(R.id.tv_mplTransaction_emptyView);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setEnableButton(typeAllTv, typeBillTv, typeCardToCardTv, typeSalesTv, typeTopUpTv);
        viewModel.getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type.NONE);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle(getResources().getString(R.string.payment_history))
                .setLeftIcon(R.string.back_icon);

        toolBarContainer.addView(toolbar.getView());
    }


    private void setEnableButton(TextView enable, TextView disable, TextView disable2, TextView disable3, TextView disable4) {
        enable.setSelected(true);
        disable.setSelected(false);
        disable2.setSelected(false);
        disable3.setSelected(false);
        disable4.setSelected(false);
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }
}
