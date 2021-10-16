package net.iGap.fragments.mplTranaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MplTransactionInfoFragment extends BaseFragment implements ToolbarListener {
    private static final String TAG = "abbasiMpl";

    private MplTransactionInfoViewModel viewModel;
    private MplTransactionInfoAdapter adapter;

    private View rootView;
    private String token;
    private TextView statusTv;
    private TextView dataTv;
    private TextView timeTv;
    private TextView statusIv;
    private CardView statusRootView;
    private CardView rvContainer;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isInValid = true;

    public static MplTransactionInfoFragment getInstance(String token) {
        MplTransactionInfoFragment mplTransactionInfoFragment = new MplTransactionInfoFragment();
        mplTransactionInfoFragment.token = token;
        return mplTransactionInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mpl_trancaction_info, container, false);
        viewModel = new MplTransactionInfoViewModel();
        adapter = new MplTransactionInfoAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        viewModel.getTransactionInfo(token);

        viewModel.getTransactionInfoLiveData().observe(getViewLifecycleOwner(), mplTransaction -> {
            List<MilTransActionStruct> data = new ArrayList<>();

            if (mplTransaction != null) {
                setDate(mplTransaction.getPayTime());

                switch (mplTransaction.getType()) {
                    case CARD_TO_CARD:
                        ProtoGlobal.MplTransaction.CardToCard transactionCardToCard = mplTransaction.getCardtocard();
                        data = new ArrayList<>();

                        setStatus(transactionCardToCard.getStatus());

                        if (transactionCardToCard.getStatus() != 0) {
                            TextView transactionDesc = rootView.findViewById(R.id.tv_mplTransActionInfo_description);
                            transactionDesc.setText(transactionCardToCard.getStatusDescription());
                            TextView desTitle = rootView.findViewById(R.id.tv_mplTransActionInfo_descriptionTitle);
                            desTitle.setVisibility(View.VISIBLE);
                            transactionDesc.setVisibility(View.VISIBLE);
                        }

                        data.add(new MilTransActionStruct(getResources().getString(R.string.bank), transactionCardToCard.getBankName()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.card_owner), transactionCardToCard.getCardOwnerName()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.destination_bank), transactionCardToCard.getDestBankName()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.destination_card), transactionCardToCard.getDestCardNumber()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.source_card), transactionCardToCard.getSourceCardNumber()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.amount), String.valueOf(transactionCardToCard.getAmount())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_order_id), String.valueOf(transactionCardToCard.getOrderId())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_rrn), String.valueOf(transactionCardToCard.getRRN())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_trace), String.valueOf(transactionCardToCard.getTraceNo())));

                        break;
                    case TOPUP:
                        ProtoGlobal.MplTransaction.Topup transactionTopUp = mplTransaction.getTopup();
                        data = new ArrayList<>();

                        setStatus(transactionTopUp.getStatus());


                        if (transactionTopUp.getStatus() != 0) {
                            TextView transactionDesc = rootView.findViewById(R.id.tv_mplTransActionInfo_description);
                            transactionDesc.setText(transactionTopUp.getStatusDescription());
                            TextView desTitle = rootView.findViewById(R.id.tv_mplTransActionInfo_descriptionTitle);
                            desTitle.setVisibility(View.VISIBLE);
                            transactionDesc.setVisibility(View.VISIBLE);
                        }

                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_merchent_name), transactionTopUp.getMerchantName()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.card_number), transactionTopUp.getCardNumber()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_terminal_no), String.valueOf(transactionTopUp.getTerminalNo())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_trace), String.valueOf(transactionTopUp.getTraceNo())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.amount), String.valueOf(transactionTopUp.getAmount())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mobile_number), String.valueOf(transactionTopUp.getChargeMobileNumber())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_order_id), String.valueOf(transactionTopUp.getOrderId())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_rrn), String.valueOf(transactionTopUp.getRRN())));


                        break;
                    case SALES:
                        ProtoGlobal.MplTransaction.Sales transactionSales = mplTransaction.getSales();
                        data = new ArrayList<>();

                        setStatus(transactionSales.getStatus());

                        if (transactionSales.getStatus() != 0) {
                            TextView transactionDesc = rootView.findViewById(R.id.tv_mplTransActionInfo_description);
                            transactionDesc.setText(transactionSales.getStatusDescription());
                            TextView desTitle = rootView.findViewById(R.id.tv_mplTransActionInfo_descriptionTitle);
                            desTitle.setVisibility(View.VISIBLE);
                            transactionDesc.setVisibility(View.VISIBLE);
                        }

                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_merchent_name), transactionSales.getMerchantName()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.card_number), transactionSales.getCardNumber()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_terminal_no), String.valueOf(transactionSales.getTerminalNo())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_trace), String.valueOf(transactionSales.getTraceNo())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.amount), String.valueOf(transactionSales.getAmount())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_order_id), String.valueOf(transactionSales.getOrderId())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_rrn), String.valueOf(transactionSales.getRRN())));

                        break;
                    case BILL:
                        ProtoGlobal.MplTransaction.Bill transactionBill = mplTransaction.getBill();
                        data = new ArrayList<>();

                        setStatus(transactionBill.getStatus());

                        if (transactionBill.getStatus() != 0) {
                            TextView transactionDesc = rootView.findViewById(R.id.tv_mplTransActionInfo_description);
                            transactionDesc.setText(transactionBill.getStatusDescription());
                            TextView desTitle = rootView.findViewById(R.id.tv_mplTransActionInfo_descriptionTitle);
                            desTitle.setVisibility(View.VISIBLE);
                            transactionDesc.setVisibility(View.VISIBLE);
                        }

                        data.add(new MilTransActionStruct(getResources().getString(R.string.billing_id), transactionBill.getBillId()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.bill_type), transactionBill.getBillType()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.card_number), transactionBill.getCardNumber()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_merchent_name), transactionBill.getMerchantName()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.pay_id), transactionBill.getPayId()));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.amount), String.valueOf(transactionBill.getAmount())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_order_id), String.valueOf(transactionBill.getOrderId())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_rrn), String.valueOf(transactionBill.getRRN())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.terminal_no), String.valueOf(transactionBill.getTerminalNo())));
                        data.add(new MilTransActionStruct(getResources().getString(R.string.mpl_transaction_trace), String.valueOf(transactionBill.getTraceNo())));

                        break;
                }
            }

            adapter.setData(data);

            statusRootView.setVisibility(View.VISIBLE);
            rvContainer.setVisibility(View.VISIBLE);

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(300);
            rvContainer.setAnimation(fadeIn);
            statusRootView.setAnimation(fadeIn);

        });

        viewModel.getErrorTransActionInfoLiveData().observe(getViewLifecycleOwner(), isInvalid -> {
            if (isInvalid != null) {
                if (isInvalid) {
                    emptyView.setVisibility(View.VISIBLE);
                    isInValid = false;
                } else {
                    emptyView.setVisibility(View.GONE);
                    isInValid = true;
                }
            }
        });

        viewModel.getProgressLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null)
                swipeRefreshLayout.setRefreshing(progress);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.getTransactionInfo(token));


    }

    private void setupViews() {
        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_mplTransactionInfo_toolBar);
        swipeRefreshLayout = rootView.findViewById(R.id.sl_mplTransactionInfo);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_mplTransActionInfo);
        rvContainer = rootView.findViewById(R.id.cl_mplTransactionInfo_rv);
        statusTv = rootView.findViewById(R.id.tv_mplTransactionInfo_stats);
        dataTv = rootView.findViewById(R.id.tv_mplTransactionInfo_data);
        timeTv = rootView.findViewById(R.id.tv_mplTransactionInfo_time);
        statusIv = rootView.findViewById(R.id.fv_mplTransactionInfo_status);
        statusRootView = rootView.findViewById(R.id.cv_mplTransactionInfo);
        emptyView = rootView.findViewById(R.id.tv_mplTransactionInfo_emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(this)
                .setLogoShown(true)
                .setRightIcons(R.string.icon_download)
                .setDefaultTitle(getResources().getString(R.string.payment_history))
                .setLeftIcon(R.string.icon_back);

        toolBarContainer.addView(toolbar.getView());
    }

    private void setStatus(int status) {
        if (status == 0)
            successfulStatus();
        else
            unsuccessfulStatus();
    }

    private void successfulStatus() {
        statusTv.setTextColor(getResources().getColor(R.color.green));
        statusIv.setTextColor(getResources().getColor(R.color.green));
        statusIv.setText(getResources().getString(R.string.icon_sent));
        statusTv.setText(getResources().getString(R.string.successful_payment));
    }

    private void unsuccessfulStatus() {
        statusTv.setTextColor(getResources().getColor(R.color.red));
        statusIv.setTextColor(getResources().getColor(R.color.red));
        statusIv.setText(getResources().getString(R.string.icon_error));
        statusTv.setText(getResources().getString(R.string.unsuccessful_payment));
    }

    private void setDate(long date) {
        date = date * 1000L;
        dataTv.setText(HelperCalander.getTimeForMainRoom(date) + "\n" + HelperCalander.getPersianCalander(date));
        timeTv.setText(HelperCalander.getClocktime(date, G.isAppRtl));
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void onRightIconClickListener(View view) {
        if (isInValid && getContext() != null)
            new MaterialDialog.Builder(getContext())
                    .content(getResources().getString(R.string.mpl_do_you_want_save_receipt))
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> saveReceipt())
                    .show();

    }


    private void saveReceipt() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
        }

        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "iGpap Transaction Log");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public class MilTransActionStruct {
        private String title;
        private String data;

        public MilTransActionStruct(String title, String data) {
            this.title = title;
            this.data = data;
        }

        public String getTitle() {
            return title;
        }

        public String getData() {
            return data;
        }
    }
}
