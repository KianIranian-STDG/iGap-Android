package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.kuknos.RefundHistoryAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.kuknos.Model.Parsian.KuknosRefundHistory;
import net.iGap.kuknos.viewmodel.KuknosRefundHistoryVM;

public class KuknosRefundHistoryFrag extends BaseAPIViewFrag<KuknosRefundHistoryVM> {

    private RecyclerView refundHistoryRecycler;
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
        refundHistoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        refundHistoryRecycler.setHasFixedSize(true);

        onRefundHistory();
        return view;
    }


    private void onRefundHistory() {
        viewModel.getRefundHistory().observe(getViewLifecycleOwner(), new Observer<KuknosRefundHistory>() {
            @Override
            public void onChanged(KuknosRefundHistory history) {
                RefundHistoryAdapter adapter = new RefundHistoryAdapter(history, getActivity());
                refundHistoryRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
