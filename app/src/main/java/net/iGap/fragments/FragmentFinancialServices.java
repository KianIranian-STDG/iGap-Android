package net.iGap.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnCallLogClear;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.PreCachingLayoutManager;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmCallLogFields;
import net.iGap.request.RequestSignalingClearLog;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestSignalingGetLog;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentFinancialServices extends BaseFragment {

    public static final String OPEN_IN_FRAGMENT_MAIN = "OPEN_IN_FRAGMENT_MAIN";
    boolean openInMain = false;

    public static FragmentFinancialServices newInstance(boolean openInFragmentMai) {
        FragmentFinancialServices fragmentCall = new FragmentFinancialServices();
        Bundle bundle = new Bundle();
        fragmentCall.setArguments(bundle);
        return fragmentCall;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_financial_services, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        openInMain = getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color


        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });


        TextView txtTop = view.findViewById(R.id.top);
        txtTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://taps.io/get-top";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        TextView txtPagear = view.findViewById(R.id.paygear);
        txtPagear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://taps.io/get-top";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        if (openInMain) {
            view.findViewById(R.id.fc_layot_title).setVisibility(View.GONE);

        }

    }

}
