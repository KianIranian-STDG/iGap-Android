package net.iGap.adapter.items.discovery.holder;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.BottomNavigationFragment;

import ir.tapsell.plus.AdHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;

public class Type9ViewHolder extends BaseViewHolder {

    private FrameLayout adContainer;
    private AdHolder adHolder;
    private Activity activity;

    public Type9ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        this.activity = activity;
        adContainer = itemView.findViewById(R.id.adContainer);
        adHolder = TapsellPlus.createAdHolder(activity, adContainer, R.layout.custom_native_banner);
    }


    @Override
    public void bindView(DiscoveryItem item) {
        if (!BottomNavigationFragment.isShowedAdd) {
            TapsellPlus.requestNativeBanner(activity, "5f7b3016b32aee00019b7900", new AdRequestCallback() {
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
                "5f7b3016b32aee00019b7900",
                new AdShowListener() {
                    @Override
                    public void onOpened() {
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
                    }
                });
    }
}
