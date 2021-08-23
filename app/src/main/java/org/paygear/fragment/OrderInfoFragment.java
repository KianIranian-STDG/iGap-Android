package org.paygear.fragment;


import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import org.paygear.RaadApp;
import org.paygear.WalletActivity;
import org.paygear.model.Order;
import org.paygear.model.PaymentEntryListItem;
import org.paygear.model.PaymentResult;
import org.paygear.web.Web;
import org.paygear.widget.OrderView;

import java.util.ArrayList;
import java.util.Calendar;

import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.KeyValue;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.widget.ProgressLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderInfoFragment extends Fragment {

    private OrderView mOrderView;
    private RecyclerView mList;
    private ProgressLayout progress;

    private String mOrderId;
    private Order mOrder;
    private Button showReceiptButton;

    private PaymentEntryListAdapter adapter;

    public OrderInfoFragment() {
    }

    public static OrderInfoFragment newInstance(String orderId) {
        OrderInfoFragment fragment = new OrderInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("OrderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderId = getArguments().getString("OrderId");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_info, container, false);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.icon_back)
                .setDefaultTitle(getString(R.string.order_info))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }
                });

        LinearLayout lytToolbar = view.findViewById(R.id.toolbarLayout);
        lytToolbar.addView(toolbar.getView());

        RelativeLayout root= view.findViewById(R.id.rootView);
        root.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        mOrderView = view.findViewById(R.id.order_view);
        showReceiptButton = view.findViewById(R.id.show_receipt_button);
        showReceiptButton.setTypeface(Typefaces.get(getContext(), Typefaces.IRAN_MEDIUM));

        mList = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOrder();
            }
        });


        if (mOrder != null)
            updateInfo();
        else
            loadOrder();
        return view;
    }

    private void loadOrder() {
        progress.setStatus(0);
        Web.getInstance().getWebService().getSingleOrder(RaadApp.selectedMerchant == null ? Auth.getCurrentAuth().getId() : RaadApp.selectedMerchant.get_id(), mOrderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                Boolean success = Web.checkResponse(OrderInfoFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mOrder = response.body();
                    updateInfo();
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                if (Web.checkFailureResponse(OrderInfoFragment.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }

    private void updateInfo() {
        progress.setStatus(1);

        mOrderView.setOrder(mOrder);

        showReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReceipt(mOrder);
            }
        });

        if (adapter == null) {
            setAdapter();
        } else {
            mList.setAdapter(adapter);
        }
    }

    private void showReceipt(Order mOrder) {
        PaymentResult paymentResult = new PaymentResult();
        paymentResult.amount = mOrder.amount;
        paymentResult.invoiceNumber = mOrder.invoiceNumber;
        ArrayList<KeyValue> keyValues = new ArrayList<>();

        KeyValue keyValue1 = new KeyValue();
        keyValue1.key = getString(R.string.price_rial);
        keyValue1.value = RaadCommonUtils.formatPrice(mOrder.getTotalPrice(), true);
        keyValues.add(keyValue1);

        KeyValue keyValue2 = new KeyValue();
        keyValue2.key = getString(R.string.reciever_name);
        keyValue2.value = mOrder.receiver.name;
        keyValues.add(keyValue2);

        KeyValue keyValue3 = new KeyValue();
        keyValue3.key = getString(R.string.trace_no);
        keyValue3.value = String.valueOf(mOrder.traceNumber);
        keyValues.add(keyValue3);

        KeyValue keyValue4 = new KeyValue();
        keyValue4.key = getString(R.string.reference_code);
        keyValue4.value = String.valueOf(mOrder.invoiceNumber);
        keyValues.add(keyValue4);

        KeyValue keyValue5 = new KeyValue();
        Calendar calendar = Calendar.getInstance();
       // calendar.setTimeInMillis(mOrder.paidMicroTime + TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings());
        calendar.setTimeInMillis(mOrder.paidMicroTime);
        keyValue5.key = getString(R.string.date_time);
        keyValue5.value = RaadCommonUtils.getLocaleFullDateTime(calendar);
        keyValues.add(keyValue5);

//                                            KeyValue keyValue6=new KeyValue();
//                                            keyValue6.key=getString(R.string.payable_price);
//                                            keyValue6.value=String.valueOf(lastOrderDetail.paidAmount);
//                                            keyValues.add(keyValue6);

        KeyValue keyValue7 = new KeyValue();
        keyValue7.key = getString(R.string.transaction_type);
        keyValue7.value = getString(R.string.purchase);
        keyValues.add(keyValue7);

        paymentResult.result = keyValues.toArray(new KeyValue[0]);


        paymentResult.traceNumber = mOrder.traceNumber;

        final PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
        dialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        },"");
        if (getActivity() != null)
            dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
    }

    private void setAdapter() {
        ArrayList<PaymentEntryListItem> items = new ArrayList<>();
        if (!TextUtils.isEmpty(mOrder.cardNumber))
            items.add(new PaymentEntryListItem(getString(R.string.source_card), mOrder.cardNumber, null, true));
        if (!TextUtils.isEmpty(mOrder.targetCardNumber))
            items.add(new PaymentEntryListItem(getString(R.string.destination_card), mOrder.targetCardNumber, null, true));
        if (!TextUtils.isEmpty(mOrder.targetShebaNumber))
            items.add(new PaymentEntryListItem(getString(R.string.destination_sheba_number), mOrder.targetShebaNumber, null, true));
        if (mOrder.sender != null && mOrder.sender.id.equals(Auth.getCurrentAuth().getId()) && mOrder.sender.balance != null) {
            items.add(new PaymentEntryListItem(getString(R.string.remaining_balance), RaadCommonUtils.formatPrice(mOrder.sender.balance, true), null, true));
        }

        if (mOrder.receiver != null && mOrder.receiver.id.equals(Auth.getCurrentAuth().getId()) && mOrder.receiver.balance != null) {
            items.add(new PaymentEntryListItem(getString(R.string.remaining_balance), RaadCommonUtils.formatPrice(mOrder.receiver.balance, true), null, true));
        }
        if (mOrder.traceNumber > 0)
            items.add(new PaymentEntryListItem(getString(R.string.trace_no), String.valueOf(mOrder.traceNumber), null, true));
        if (mOrder.invoiceNumber > 0)
            items.add(new PaymentEntryListItem(getString(R.string.reference_code), String.valueOf(mOrder.invoiceNumber), null, true));


        if (mOrder.orderType == Order.ORDER_TYPE_CASH_OUT) {
            Calendar calendar = Calendar.getInstance();

            if (mOrder.createdMicroTime > 0) {
              //  calendar.setTimeInMillis(mOrder.createdMicroTime + TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings());
                calendar.setTimeInMillis(mOrder.createdMicroTime);
                items.add(new PaymentEntryListItem(getString(R.string.request_time), RaadCommonUtils.getLocaleFullDateTime(calendar), null, true));
            }
            if (mOrder.paidMicroTime > 0) {
             //   calendar.setTimeInMillis(mOrder.paidMicroTime + TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings());
                calendar.setTimeInMillis(mOrder.paidMicroTime );
                items.add(new PaymentEntryListItem(getString(R.string.settlement_time), RaadCommonUtils.getLocaleFullDateTime(calendar), null, true));
            }

            items.add(new PaymentEntryListItem(null, getString(R.string.requested_amount_rial), RaadCommonUtils.formatPrice(mOrder.amount, false), false));
            //items.add(new PaymentEntryListItem(null, getString(R.string.settlement_wage), RaadCommonUtils.formatPrice(mOrder.paidAmount - mOrder.amount, false), false));
            items.add(new PaymentEntryListItem(null, getString(R.string.deposits), RaadCommonUtils.formatPrice(mOrder.paidAmount, false), false));
        } else {
            if (mOrder.isPay() && mOrder.receiver.type != Account.TYPE_USER) {
                /*if (mOrder.pod != null)
                    items.add(new PaymentEntryListItem(getString(R.string.delivery_place), mOrder.pod.toString(getContext()), null, true));
                items.add(new PaymentEntryListItem(getString(R.string.coupon), getString(mOrder.hasCoupon ? R.string.has : R.string.not_has), null, true));
                if (mOrder.cart != null)
                    items.add(new PaymentEntryListItem(getString(R.string.cart), getString(R.string.see_details), null, true));
*/
                items.add(new PaymentEntryListItem(null, getString(R.string.total_price), RaadCommonUtils.formatPrice(mOrder.getTotalPrice(), false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.discount), RaadCommonUtils.formatPrice(mOrder.discountPrice, false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.service_price), RaadCommonUtils.formatPrice(mOrder.additionalFee, false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.tax), RaadCommonUtils.formatPrice(mOrder.tax, false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.delivery_price), RaadCommonUtils.formatPrice(mOrder.deliveryPrice, false), false));
            }

            items.add(new PaymentEntryListItem(null, getString(R.string.final_price_rial), RaadCommonUtils.formatPrice(mOrder.amount, false), false));

        }
        adapter = new PaymentEntryListAdapter(getContext(), items, new PaymentEntryListAdapter.OnPaymentEntryItemClickListener() {
            @Override
            public void onItemClick(PaymentEntryListItem item) {
                /*if (item.isSelectable && item.title1 != null && item.title1.equals(getString(R.string.cart))) {
                    if (getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).replaceFragment(
                                CartInfoFragment.newInstance(mOrder.cart), "CartInfoFragment", true);
                    }
                }*/
            }
        });

        mList.setAdapter(adapter);
    }

}
