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
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.fragments.populaChannel.PopularChannelHomeFragment;
import net.iGap.fragments.populaChannel.PopularMoreChannelFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnUnreadChange;
import net.iGap.libs.bottomNavigation.BottomNavigation;
import net.iGap.libs.bottomNavigation.Event.OnBottomNavigationBadge;
import net.iGap.realm.RealmRoom;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigationFragment extends Fragment implements OnUnreadChange {

    private static final int CONTACT_FRAGMENT = 0;
    private static final int CALL_FRAGMENT = 1;
    private static final int CHAT_FRAGMENT = 2;
    private static final int DISCOVERY_FRAGMENT = 3;
    private static final int PROFILE_FRAGMENT = 4;
    private static final int POPULAR_CHANNEL_FRAGMENT = 5;

    public static final String DEEP_LINK_DISCOVERY = "discovery";
    public static final String DEEP_LINK_CONTACT = "contact";
    public static final String DEEP_LINK_CHAT = "chat";
    public static final String DEEP_LINK_CALL = "call";
    public static final String DEEP_LINK_PROFILE = "profile";
    public static final String DEEP_LINK_POPULAR = "favoritechannel";

    //Todo: create viewModel for this it was test class and become main class :D
    private BottomNavigation bottomNavigation;
    private String crawlerMap;
    private DiscoveryFragment.CrawlerStruct crawlerStruct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        G.onUnreadChange = this;
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (crawlerMap != null) {
            autoLinkCrawler(crawlerMap, new DiscoveryFragment.CrawlerStruct.OnDeepValidLink() {
                @Override
                public void linkValid(String link) {

                }

                @Override
                public void linkInvalid(String link) {
                    if (getContext() != null)
                        HelperError.showSnackMessage(link + " " + getContext().getResources().getString(R.string.link_not_valid), false);
                }
            });
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigation = view.findViewById(R.id.bn_main_bottomNavigation);
        bottomNavigation.setDefaultItem(2);
        bottomNavigation.setOnItemChangeListener(this::loadFragment);
        bottomNavigation.setCurrentItem(2);
    }

    public void setCrawlerMap(String crawlerMap) {
        this.crawlerMap = crawlerMap;
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
                break;
            case 3:
                fragment = fragmentManager.findFragmentByTag(DiscoveryFragment.class.getName());
                if (fragment == null) {
                    fragment = DiscoveryFragment.newInstance(0);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }

                if (crawlerStruct != null) {
                    ((DiscoveryFragment) fragment).setNeedToCrawl(true);
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

    public void checkPassCodeIconVisibility() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentMain.class.getName());

        if (fragment instanceof FragmentMain) {
            ((FragmentMain) fragment).checkPassCodeIconVisibility();
        }
    }

    public void setForwardMessage(boolean enable) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentMain.class.getName());

        if (fragment instanceof FragmentMain) {
            ((FragmentMain) fragment).setForwardMessage(enable);
        }
    }


    public void autoLinkCrawler(String uri , DiscoveryFragment.CrawlerStruct.OnDeepValidLink onDeepLinkValid) {
        if (uri.equals("")){
            onDeepLinkValid.linkInvalid(uri);
            return;
        }

        String[] address = uri.toLowerCase().trim().split("/");

        if (address.length == 0){
            onDeepLinkValid.linkInvalid(uri);
            return;
        }

        switch (address[0]) {
            case DEEP_LINK_DISCOVERY:
                String[] discoveryUri;
                if (address.length > 1) {
                    discoveryUri = uri.toLowerCase().trim().replace("discovery/", "").split("/");
                } else
                    discoveryUri = address;

                for (int i = 0; i < discoveryUri.length; i++) {
                    if (HelperString.isInteger(discoveryUri[i])) {
                        if (i == 0)
                            onDeepLinkValid.linkValid(address[i]);
                        setCrawlerMap(DISCOVERY_FRAGMENT, discoveryUri);
                    } else {
                        if (discoveryUri[0].equals(DEEP_LINK_DISCOVERY)){
                            onDeepLinkValid.linkValid(address[i]);
                            setCrawlerMap(DISCOVERY_FRAGMENT, discoveryUri);
                        }else {
                            onDeepLinkValid.linkInvalid(discoveryUri[i]);
                            return;
                        }
                    }

                }

                break;
            case DEEP_LINK_CHAT:
                String chatUri = uri.toLowerCase().trim().replace("chat/", "").replace("chat", "").trim();
                if (chatUri.length() > 1) {
                    HelperUrl.checkUsernameAndGoToRoom(getActivity(), chatUri, HelperUrl.ChatEntry.chat);
                }
                onDeepLinkValid.linkValid(address[0]);
                setCrawlerMap(CHAT_FRAGMENT, null);
                break;
            case DEEP_LINK_PROFILE:
                onDeepLinkValid.linkValid(address[0]);
                setCrawlerMap(PROFILE_FRAGMENT, null);
                break;
            case DEEP_LINK_CALL:
                onDeepLinkValid.linkValid(address[0]);
                setCrawlerMap(CALL_FRAGMENT, null);
                break;
            case DEEP_LINK_CONTACT:
                onDeepLinkValid.linkValid(address[0]);
                setCrawlerMap(CONTACT_FRAGMENT, null);
                break;
            case DEEP_LINK_POPULAR:
                onDeepLinkValid.linkValid(address[0]);
                setCrawlerMap(POPULAR_CHANNEL_FRAGMENT, address);
                break;
            default:
                onDeepLinkValid.linkInvalid(address[0]);
                break;
        }
    }


    private void setCrawlerMap(int position, String[] uri) {

        if (uri != null && uri.length > 0) {
            if (!uri[0].equals(DEEP_LINK_DISCOVERY) && position == DISCOVERY_FRAGMENT) {
                List<Integer> pages = new ArrayList<>();
                for (String s : uri) {
                    pages.add(Integer.valueOf(s));
                }
                this.crawlerStruct = new DiscoveryFragment.CrawlerStruct(0, pages);
            }
        }

        if (position == bottomNavigation.getCurrentTab()) {
            if (bottomNavigation.getSelectedItemPosition() == DISCOVERY_FRAGMENT) {
                if (getActivity() != null && getActivity() instanceof ActivityMain)
                    ((ActivityMain) getActivity()).removeAllFragmentFromMain();

                if (getActivity() != null) {
                    DiscoveryFragment discoveryFragment = (DiscoveryFragment) getChildFragmentManager().findFragmentByTag(DiscoveryFragment.class.getName());
                    if (discoveryFragment != null) {
                        discoveryFragment.setNeedToCrawl(true);
                        discoveryFragment.discoveryCrawler(getActivity());
                    }
                }
            }
        } else {
            switch (position) {
                case CONTACT_FRAGMENT:
                    bottomNavigation.setCurrentItem(CONTACT_FRAGMENT);
                    break;
                case CALL_FRAGMENT:
                    bottomNavigation.setCurrentItem(CALL_FRAGMENT);
                    break;
                case CHAT_FRAGMENT:
                    bottomNavigation.setCurrentItem(CHAT_FRAGMENT);
                    break;
                case DISCOVERY_FRAGMENT:

                    DiscoveryFragment discoveryFragment = (DiscoveryFragment) getChildFragmentManager().findFragmentByTag(DiscoveryFragment.class.getName());
                    if (discoveryFragment != null)
                        discoveryFragment.setNeedToReload(true);

                    bottomNavigation.setCurrentItem(DISCOVERY_FRAGMENT);
                    break;
                case POPULAR_CHANNEL_FRAGMENT:
                    if (uri != null)
                        if (uri.length > 1) {
                            PopularMoreChannelFragment popularMoreChannelFragment = new PopularMoreChannelFragment();
                            popularMoreChannelFragment.setId(uri[1]);
                            new HelperFragment(getFragmentManager(), popularMoreChannelFragment).setReplace(false).load();
                        } else {
                            new HelperFragment(getFragmentManager(), new PopularChannelHomeFragment()).setReplace(false).load();
                        }
                    break;
                case PROFILE_FRAGMENT:
                    bottomNavigation.setCurrentItem(PROFILE_FRAGMENT);
                    break;
            }
        }
    }

    public DiscoveryFragment.CrawlerStruct getCrawlerStruct() {
        return crawlerStruct;
    }
}
