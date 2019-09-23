package net.iGap.news.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.NewsGrouptabFragBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.view.adapter.TabAdapter;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.news.repository.model.NewsApiArg;

import java.util.Objects;

public class NewsGroupPagerFrag extends BaseFragment {

    private NewsGrouptabFragBinding binding;
    private TabLayout tabLayout;


    public static NewsGroupPagerFrag newInstance() {
        return new NewsGroupPagerFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.news_grouptab_frag, container, false);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Bundle arg = getArguments();
        String groupID = arg.getString("GroupID");

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setDefaultTitle(arg.getString("GroupTitle"))
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        Picasso.get()
//                .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                .load(arg.getString("GroupPic"))
                .placeholder(R.mipmap.news_temp_banner)
                .into(binding.groupImage);

        ViewPager viewPager = binding.secondaryLayout.viewPager;
        tabLayout = binding.secondaryLayout.pagerTabLayout;
        TabAdapter adapter = new TabAdapter(getFragmentManager());

        NewsListFrag frag = new NewsListFrag();
        frag.setApiArg(new NewsApiArg(0, 50, Integer.parseInt(groupID), NewsApiArg.NewsType.GROUP_NEWS));
        adapter.addFragment(frag, getResources().getString(R.string.news_latest));

        NewsListFrag frag2 = new NewsListFrag();
        frag2.setApiArg(new NewsApiArg(0, 50, Integer.parseInt(groupID), NewsApiArg.NewsType.FEATURED_GROUP));
        adapter.addFragment(frag2, getResources().getString(R.string.news_MHits));

        NewsListFrag frag3 = new NewsListFrag();
        frag3.setApiArg(new NewsApiArg(0, 50, Integer.parseInt(groupID), NewsApiArg.NewsType.ERGENT_GROUP));
        adapter.addFragment(frag3, getResources().getString(R.string.news_ergent));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        updateFontTabLayout();
    }

    private void updateFontTabLayout() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (tabLayout.getTabAt(i) == null) {
                continue;
            }

            TextView tv = new TextView(getContext());
            tv.setText(Objects.requireNonNull(tabLayout.getTabAt(i)).getText());
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(G.typeface_IRANSansMobile);

            if (G.isDarkTheme) {
                tv.setTextColor(G.context.getResources().getColor(R.color.white));
            } else {
                tv.setTextColor(G.context.getResources().getColor(R.color.black));
            }
            Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(tv);
        }
    }

}
