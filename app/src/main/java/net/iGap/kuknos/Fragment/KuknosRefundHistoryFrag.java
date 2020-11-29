package net.iGap.kuknos.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.kuknos.Model.Parsian.KuknosRefundHistory;
import net.iGap.kuknos.viewmodel.KuknosRefundHistoryVM;

import java.text.DecimalFormat;

public class KuknosRefundHistoryFrag extends BaseAPIViewFrag<KuknosRefundHistoryVM> {

    private RecyclerView refundHistoryRecycler;
    private SwipeRefreshLayout refundHistoryRefresh;
    private static final String TAG = "KuknosRefundHistoryFrag";


    public static KuknosRefundHistoryFrag newInstance() {
        KuknosRefundHistoryFrag fragment = new KuknosRefundHistoryFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosRefundHistoryVM.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kuknos_refund_history, container, false);
        viewModel.getUserRefundsFromServer();
        refundHistoryRecycler = view.findViewById(R.id.refundHistoryRecycler);
        refundHistoryRefresh = view.findViewById(R.id.refundHistoryRefresh);

        refundHistoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        refundHistoryRecycler.setHasFixedSize(true);

        refundHistoryRefresh.setRefreshing(true);
        refundHistoryRefresh.setOnRefreshListener(() -> viewModel.getUserRefundsFromServer());

        onRefundHistory();
        onProgressChanged();
        onError();

        return view;
    }


    private void onRefundHistory() {
        viewModel.getRefundHistory().observe(getViewLifecycleOwner(), history -> {
            RefundHistoryAdapter adapter = new RefundHistoryAdapter(history, getActivity());
            refundHistoryRecycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });
    }

    private void onProgressChanged() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> refundHistoryRefresh.setRefreshing(aBoolean));
    }

    private void onError() {
        viewModel.getErrorState().observe(getViewLifecycleOwner(), s -> {
            HelperError.showSnackMessage(s, false);
        });
    }

    public class RefundHistoryAdapter extends RecyclerView.Adapter<RefundHistoryAdapter.RefundViewHolder> {

        private KuknosRefundHistory refundHistory;
        private Context context;

        public RefundHistoryAdapter(KuknosRefundHistory refundHistory, Context context) {
            this.refundHistory = refundHistory;
            this.context = context;
        }

        @NonNull
        @Override
        public RefundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_kuknos_refund_history_cell, parent, false);
            return new RefundViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RefundViewHolder holder, int position) {
            holder.initViews(refundHistory.getRefunds().get(position));
        }

        @Override
        public int getItemCount() {
            return refundHistory.getRefunds().size();
        }

        public class RefundViewHolder extends RecyclerView.ViewHolder {

            private TextView refundAssetCode, refundAmount, refundFee, refundCount;
            private KuknosRefundHistory.Refund refund;

            public RefundViewHolder(@NonNull View view) {
                super(view);

                refundAssetCode = view.findViewById(R.id.kuknos_refund_assetCode);
                refundAmount = view.findViewById(R.id.kuknos_refund_amount);
                refundFee = view.findViewById(R.id.kuknos_refund_fee);
                refundCount = view.findViewById(R.id.kuknos_refund_count);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = getChildFragmentManager().findFragmentByTag(KuknosReceiptFrag.class.getName());
                        if (fragment == null) {
                            fragment = KuknosReceiptFrag.newInstance(refund.getRefNumber());
                            getChildFragmentManager().beginTransaction().addToBackStack(fragment.getClass().getName());
                        }
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                    }
                });
            }

            public void initViews(KuknosRefundHistory.Refund refund) {
                this.refund = refund;
                refundAssetCode.setText(refund.getAssetCode());
                DecimalFormat df = new DecimalFormat("###,###,###");

                refundAmount.setText(
                        HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Long.valueOf(refund.getAmount()))) + " ﷼"
                                : df.format(Long.valueOf(refund.getAmount())) + " ﷼"
                );

                refundFee.setText(
                        HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(refund.getFee())))
                                : df.format(Float.valueOf(refund.getFee()))
                );

                refundCount.setText(
                        HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(Float.valueOf(refund.getAssetCount())))
                                : df.format(Float.valueOf(refund.getAssetCount()))
                );
            }
        }
    }


}
