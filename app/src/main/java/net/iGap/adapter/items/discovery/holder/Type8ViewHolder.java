package net.iGap.adapter.items.discovery.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.helper.FileLog;

import ir.tapsell.plus.AdHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.plus.model.TapsellPlusAdModel;
import ir.tapsell.plus.model.TapsellPlusErrorModel;

public class Type8ViewHolder extends BaseViewHolder {

    private ViewGroup adContainer;
    private AdHolder adHolder;
    private final Activity activity;

    public Type8ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        this.activity = activity;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void bindView(DiscoveryItem item) {
        String addId = item.discoveryFields.get(0).value;
        int addModel = item.model.getNumber();

        adContainer = itemView.findViewById(R.id.adContainer);

        if (adContainer != null && activity != null) {
            switch (addModel) {
                case 8:
                    adHolder = TapsellPlus.createAdHolder(activity, adContainer, R.layout.item_discovery_8);
                    break;
                case 9:
                    adHolder = TapsellPlus.createAdHolder(activity, adContainer, R.layout.item_discovery_9);
                    break;
            }
        }

        View tapCellWrapper = itemView.findViewById(R.id.tapCellWrapper);
        tapCellWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(0));
                return false;
            }
        });

        if (!BottomNavigationFragment.isShowedAdd) {
            if (activity != null && addId != null) {
                TapsellPlus.requestNativeAd(
                        activity,
                        addId,
                        new AdRequestCallback() {
                            @Override
                            public void response(TapsellPlusAdModel tapsellPlusAdModel) {
                                super.response(tapsellPlusAdModel);
                                showAdd(tapsellPlusAdModel.getResponseId());
                            }

                            @Override
                            public void error(@NonNull String message) {
                            }
                        });
            }
        }
    }

    private void showAdd(String nativeAdResponseId) {
        if (activity != null && adHolder != null && nativeAdResponseId != null) {
            TapsellPlus.showNativeAd(activity, nativeAdResponseId, adHolder,
                    new AdShowListener() {
                        @Override
                        public void onOpened(TapsellPlusAdModel tapsellPlusAdModel) {
                            adContainer.setVisibility(View.VISIBLE);
                            BottomNavigationFragment.isShowedAdd = true;
                        }

                        @Override
                        public void onError(TapsellPlusErrorModel tapsellPlusErrorModel) {
                            FileLog.e(tapsellPlusErrorModel.getErrorMessage());
                        }
                    });
        }
    }

}
