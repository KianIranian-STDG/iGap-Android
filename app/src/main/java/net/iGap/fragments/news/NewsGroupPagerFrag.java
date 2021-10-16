package net.iGap.fragments.news;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.NewsGrouptabFragBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.news.NewsApiArg;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;

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
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setDefaultTitle(arg.getString("GroupTitle"))
                .setRoundBackground(false) // cause of imageView below toolbar use flat toolbar is better than round
                .setLogoShown(true);

        binding.Toolbar.addView(mHelperToolbar.getView());

        if (!arg.getString("GroupPic").equals(""))
            Glide.with(G.context)
                    .load(arg.getString("GroupPic"))
                    .placeholder(R.mipmap.news_temp_banner)
                    .into(binding.groupImage);

        ViewPager viewPager = binding.secondaryLayout.viewPager;
        tabLayout = binding.secondaryLayout.pagerTabLayout;
     //   TabAdapter adapter = new TabAdapter(getFragmentManager());

        NewsListFrag frag = new NewsListFrag();
        frag.setApiArg(new NewsApiArg(1, 10, Integer.parseInt(groupID), NewsApiArg.NewsType.GROUP_NEWS));
        frag.setHandler(news -> {
            Glide.with(G.context).load(news.getImage()).into(binding.groupImage);
            binding.groupTitle.setText(news.getTitle());
            binding.headerNews.setOnClickListener(v -> {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(NewsDetailFrag.class.getName());
                if (fragment == null) {
                    fragment = NewsDetailFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                Bundle args = new Bundle();
                args.putString("NewsID", news.getId());
                fragment.setArguments(args);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            });
        });
       // adapter.addFragment(frag, getResources().getString(R.string.news_latest));

        NewsListFrag frag2 = new NewsListFrag();
        frag2.setApiArg(new NewsApiArg(1, 10, Integer.parseInt(groupID), NewsApiArg.NewsType.MOST_HITS));
     //   adapter.addFragment(frag2, getResources().getString(R.string.news_MHits));

        NewsListFrag frag3 = new NewsListFrag();
        frag3.setApiArg(new NewsApiArg(1, 10, Integer.parseInt(groupID), NewsApiArg.NewsType.CONTROVERSIAL_NEWS));
        //adapter.addFragment(frag3, getResources().getString(R.string.news_ergent));

     //   viewPager.setAdapter(adapter);
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
            tv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));

            if (G.themeColor == Theme.DARK) {
                tv.setTextColor(G.context.getResources().getColor(R.color.white));
            } else {
                tv.setTextColor(G.context.getResources().getColor(R.color.black));
            }
            Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(tv);
        }
    }

}
