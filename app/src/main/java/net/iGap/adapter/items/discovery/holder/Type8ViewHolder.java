package net.iGap.adapter.items.discovery.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.helper.FileLog;

import ir.tapsell.plus.AdHolder;
import ir.tapsell.plus.AdRequestCallback;
import ir.tapsell.plus.AdShowListener;
import ir.tapsell.plus.TapsellPlus;
import ir.tapsell.sdk.nativeads.views.RatioImageView;

public class Type8ViewHolder extends BaseViewHolder {

    private CardView adContainer;
    private AdHolder adHolder;
    private Activity activity;
    private String addId;
    private int addModel;
    private View tapCellWrapper;


    public Type8ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        this.activity = activity;
    }

    @SuppressLint("ClickableViewAccessibility")
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
                adContainer = itemView.findViewById(R.id.root_ad_layout_banner);
                adHolder = TapsellPlus.createAdHolder(activity, adContainer, R.layout.item_discovery_9);
                break;
        }

        tapCellWrapper = itemView.findViewById(R.id.tapCellWrapper);
        tapCellWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(0));
                return false;
            }
        });

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
