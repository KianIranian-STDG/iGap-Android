package net.iGap.adapter.items.discovery.holder;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.helper.FileLog;

import ir.tapsell.plus.AdHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;

public class Type8ViewHolder extends BaseViewHolder {

    private FrameLayout adContainer;
    private AdHolder adHolder;
    private Activity activity;
    private String addId;
    private int addModel;

    public Type8ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        this.activity = activity;
    }

    @Override
    public void bindView(DiscoveryItem item) {

        addId = item.discoveryFields.get(0).value;
        addModel = item.model.getNumber();

        switch (addModel) {
            case 8:
                adContainer = itemView.findViewById(R.id.root_ad_layout);
                adHolder = TapsellPlus.createAdHolder(activity, adContainer, R.layout.item_discovery_8);
                break;
            case 9:
            case 10:
                adContainer = itemView.findViewById(R.id.root_ad_layout_banner);
                adHolder = TapsellPlus.createAdHolder(activity, adContainer, R.layout.item_discovery_9);
                break;


        }

        if (!BottomNavigationFragment.isShowedAdd) {
            TapsellPlus.requestNativeBanner(activity, addId, new AdRequestCallback() {
                @Override
                public void response() {
                    showAdd();
                }

                @Override
                public void error(@NonNull String message) {
                }
            });
        }
    }

    private void showAdd() {
        TapsellPlus.showAd(
                activity,
                adHolder,
                addId,
                new AdShowListener() {
                    @Override
                    public void onOpened() {
                        adContainer.setVisibility(View.VISIBLE);
                        BottomNavigationFragment.isShowedAdd = true;
                    }

                    @Override
                    public void onClosed() {
                    }

                    @Override
                    public void onRewarded() {
                    }

                    @Override
                    public void onError(String s) {
                        FileLog.e(s);
                    }
                });
    }
}
