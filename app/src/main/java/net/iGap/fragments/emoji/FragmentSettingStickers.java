package net.iGap.fragments.emoji;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.vanniktech.emoji.sticker.struct.StructGroupSticker;
import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.ErrorHandler;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.fragments.emoji.struct.StructStickerResult;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmStickers;
import net.iGap.request.RequestFileDownload;
import net.iGap.response.MessageHandler;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSettingStickers extends BaseFragment {

    private List<StructGroupSticker> stickerList = new ArrayList<>();
    private ArrayList<StructItemSticker> recentStickerList;
    private TextView txtDelete;

    public FragmentSettingStickers() {
        // Required empty public constructor
    }

    public static FragmentSettingStickers newInstance(List<StructGroupSticker> stickerList, ArrayList<StructItemSticker> recentStickerList) {

        FragmentSettingStickers fragmentDetailStickers = new FragmentSettingStickers();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GROUP_ID", (Serializable) stickerList);
        bundle.putSerializable("RECENT", (Serializable) recentStickerList);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_remove_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stickerList = (List<StructGroupSticker>) getArguments().getSerializable("GROUP_ID");
        recentStickerList = (ArrayList<StructItemSticker>) getArguments().getSerializable("RECENT");

        AppBarLayout appBarLayout = view.findViewById(R.id.appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        txtDelete = view.findViewById(R.id.txtDelete);
        txtDelete.setVisibility(View.GONE);
        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FragmentRemoveRecentSticker.removeStickerList != null && FragmentRemoveRecentSticker.removeStickerList.size() > 0) {
                    new MaterialDialog.Builder(getActivity())
                            .title(getResources().getString(R.string.remove_sticker))
                            .content(getResources().getString(R.string.add_sticker_text))
                            .positiveText(getString(org.paygear.wallet.R.string.yes))
                            .negativeText(getString(org.paygear.wallet.R.string.no))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    FragmentChat.onUpdateSticker.updateRecentlySticker(FragmentRemoveRecentSticker.removeStickerList);
                                    FragmentRemoveRecentSticker.removeStickerList.clear();
                                    popBackStackFragment();
                                }
                            })
                            .show();
                } else {
                    HelperError.showSnackMessage(getResources().getString(R.string.Please_select_an_option), false);
                }
            }
        });

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_sticker_ripple_txtBack);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStackFragment();
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);

        viewPager.setAdapter(new SectionPagerAdapter(getActivity().getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i == 1) {
                    txtDelete.setVisibility(View.VISIBLE);
                } else {
                    txtDelete.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FragmentRemoveSticker.newInstance(stickerList);
                case 1:
                default:
                    return FragmentRemoveRecentSticker.newInstance(recentStickerList);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.Favorite_Sticker);
                case 1:
                default:
                    return getString(R.string.Recently_Sticker);
            }
        }
    }
}
