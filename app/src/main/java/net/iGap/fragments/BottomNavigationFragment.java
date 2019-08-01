package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.interfaces.OnUnreadChange;
import net.iGap.libs.bottomNavigation.BottomNavigation;
import net.iGap.libs.bottomNavigation.Event.OnBottomNavigationBadge;
import net.iGap.realm.RealmRoom;

public class BottomNavigationFragment extends Fragment implements OnUnreadChange {

    //Todo: create viewModel for this it was test class and become main class :D
    private BottomNavigation bottomNavigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        G.onUnreadChange = this;
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigation = view.findViewById(R.id.bn_main_bottomNavigation);
        bottomNavigation.setDefaultItem(2);
        bottomNavigation.setOnItemChangeListener(this::loadFragment);
        bottomNavigation.setCurrentItem(2);
    }

    private void loadFragment(int position) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        Fragment current = fragmentManager.findFragmentById(R.id.viewpager);
        switch (position) {
            case 0:
                fragment = fragmentManager.findFragmentByTag(RegisteredContactsFragment.class.getName());
                if (fragment == null) {
                    fragment = RegisteredContactsFragment.newInstance(false, false, RegisteredContactsFragment.CONTACTS);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                if (!(current instanceof FragmentMain)) {
                    fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.viewpager));
                }
                fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            case 1:
                fragment = fragmentManager.findFragmentByTag(FragmentCall.class.getName());
                if (fragment == null) {
                    fragment = FragmentCall.newInstance(true);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                if (!(fragmentManager.findFragmentById(R.id.viewpager) instanceof FragmentMain)) {
                    fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.viewpager));
                }
                fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            case 2:
                if (G.twoPaneMode) {
                    fragment = fragmentManager.findFragmentByTag(TabletMainFragment.class.getName());
                    if (fragment == null) {
                        fragment = new TabletMainFragment();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    } /*else {
                        Log.wtf(this.getClass().getName(),"loadFragment in else position2");
                        if (!(fragmentManager.findFragmentById(R.id.viewpager) instanceof TabletMainFragment)) {
                            fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.viewpager));
                        }
                        fragmentTransaction.show(fragment).commit();
                    }*/
                    fragmentTransaction.replace(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                } else {
                    fragment = fragmentManager.findFragmentByTag(FragmentMain.class.getName());
                    if (fragment == null) {
                        fragment = FragmentMain.newInstance(FragmentMain.MainType.all);
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                        fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                    } else {
                        if (!(fragmentManager.findFragmentById(R.id.viewpager) instanceof FragmentMain)) {
                            fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.viewpager));
                        }
                        fragmentTransaction.show(fragment).commit();
                    }
                }
                break;
            case 3:
                fragment = fragmentManager.findFragmentByTag(DiscoveryFragment.class.getName());
                if (fragment == null) {
                    fragment = DiscoveryFragment.newInstance(0);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                if (!(fragmentManager.findFragmentById(R.id.viewpager) instanceof FragmentMain)) {
                    fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.viewpager));
                }
                fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            default:
                fragment = fragmentManager.findFragmentByTag(FragmentUserProfile.class.getName());
                if (fragment == null) {
                    fragment = new FragmentUserProfile();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                if (!(fragmentManager.findFragmentById(R.id.viewpager) instanceof FragmentMain)) {
                    fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.viewpager));
                }
                fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        G.onUnreadChange = null;
    }

    @Override
    public void onChange() {

        int unReadCount = RealmRoom.getAllUnreadCount();

        bottomNavigation.setOnBottomNavigationBadge(new OnBottomNavigationBadge() {
            @Override
            public int callCount() {
                return 0;
            }

            @Override
            public int messageCount() {
                return unReadCount;
            }
        });

    }

    public void goToUserProfile() {
        bottomNavigation.setCurrentItem(4);
    }

    public void setChatPage(FragmentChat fragmentChat) {
        if (bottomNavigation.getSelectedItemPosition() != 2) {
            bottomNavigation.setCurrentItem(2);
        }
        Fragment page = getChildFragmentManager().findFragmentById(R.id.viewpager);
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        if (page instanceof TabletMainFragment) {
            Log.wtf(this.getClass().getName(), "if");
            ((TabletMainFragment) page).loadChatFragment(fragmentChat);
        } else {
            Log.wtf(this.getClass().getName(), "else");
        }
    }

    public Fragment getViewPagerCurrentFragment() {
        return getChildFragmentManager().findFragmentById(R.id.viewpager);
    }

    public boolean isFirstTabItem() {
        if (bottomNavigation.getSelectedItemPosition() == 2) {
            return true;
        } else {
            bottomNavigation.setCurrentItem(2);
            return false;
        }
    }

    public boolean isAllowToBackPressed() {
        Fragment page = getChildFragmentManager().findFragmentById(R.id.viewpager);
        // based on the current position you can then cast the page to the correct
        // class and call the method:
        if (page instanceof BaseMainFragments) {
            return ((BaseMainFragments) page).isAllowToBackPressed();
        } else {
            return true;
        }

    }

    public void checkPassCodeIconVisibility(){
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentMain.class.getName());

        if (fragment instanceof FragmentMain){
            ((FragmentMain) fragment).checkPassCodeIconVisibility();
        }
    }
}
