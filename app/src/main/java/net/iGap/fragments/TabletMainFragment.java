package net.iGap.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;

public class TabletMainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int count = getChildFragmentManager().getBackStackEntryCount();
        Fragment fragment;
        if (count < 1) {
            if (G.isLandscape) {
                fragment = new TabletEmptyChatFragment();
            } else {
                fragment = FragmentMain.newInstance(FragmentMain.MainType.all);
            }
            getChildFragmentManager().beginTransaction().addToBackStack(fragment.getClass().getName()).add(R.id.childMainContainer, fragment, fragment.getClass().getName()).commit();
        }/* else {
            fragment = (Fragment) getChildFragmentManager().getBackStackEntryAt(count -1);
        }*/
        /*getChildFragmentManager().beginTransaction().add(R.id.childMainContainer,fragment,fragment.getClass().getName()).commit();*/

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.wtf(this.getClass().getName(), "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        handleFirstFragment();
    }

    public void loadChatFragment(FragmentChat fragmentChat) {
        getChildFragmentManager().beginTransaction().addToBackStack(fragmentChat.getClass().getName()).add(R.id.childMainContainer, fragmentChat, fragmentChat.getClass().getName()).commit();
    }

    public void handleFirstFragment() {
        int count = getChildFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            Fragment fragment = getChildFragmentManager().findFragmentById(R.id.childMainContainer);
            if (G.isLandscape) {
                if (fragment.getClass().getName().equals(FragmentMain.class.getName())) {
                    getChildFragmentManager().popBackStack();
                    Fragment f = new TabletEmptyChatFragment();
                    getChildFragmentManager().beginTransaction().addToBackStack(f.getClass().getName()).add(R.id.childMainContainer, f, f.getClass().getName()).commit();
                }
            } else {
                if (fragment.getClass().getName().equals(TabletEmptyChatFragment.class.getName())) {
                    getChildFragmentManager().popBackStack();
                    Fragment f = FragmentMain.newInstance(FragmentMain.MainType.all);
                    getChildFragmentManager().beginTransaction().addToBackStack(f.getClass().getName()).add(R.id.childMainContainer, f, f.getClass().getName()).commit();
                }
            }
        }
    }
}
