package net.iGap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.G;
import net.iGap.R;
import net.iGap.story.StoryPagerFragment;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.fragments.news.NewsMainFrag;
import net.iGap.fragments.populaChannel.PopularChannelHomeFragment;
import net.iGap.fragments.populaChannel.PopularMoreChannelFragment;
import net.iGap.helper.DeepLinkHelper;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bottomNavigation.BottomNavigation;
import net.iGap.libs.bottomNavigation.Event.OnItemChangeListener;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AppConfig;
import net.iGap.module.dialog.account.AccountsDialog;
import net.iGap.observers.interfaces.OnUnreadChange;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigationFragment extends BaseFragment implements OnUnreadChange {

    private static final int CONTACT_FRAGMENT = 0;
    private static final int CALL_FRAGMENT = 1;
    public static final int CHAT_FRAGMENT = 2;
    public static final int DISCOVERY_FRAGMENT = 3;
    public static final int PROFILE_FRAGMENT = 4;
    private static final int POPULAR_CHANNEL_FRAGMENT = 5;
    private static final int NEWS_FRAGMENT = 6;

    public static final int START_TAB = AppConfig.defaultTab;
    public static boolean isShowedAdd = false;
    public static final String DEEP_LINK_DISCOVERY = "discovery";
    public static final String DEEP_LINK_CONTACT = "contact";
    public static final String DEEP_LINK_CHAT = "chat";
    public static final String DEEP_LINK_CALL = "call";
    public static final String DEEP_LINK_PROFILE = "profile";
    public static final String DEEP_LINK_POPULAR = "favoritechannel";
    public static final String DEEP_LINK_NEWS = "news";

    //Todo: create viewModel for this it was test class and become main class :D
    private BottomNavigation bottomNavigation;
    private String crawlerMap;
    private DiscoveryFragment.CrawlerStruct crawlerStruct;

    private int currentTab = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setTheme();
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
        bottomNavigation.setDefaultItem(currentTab == -1 ? START_TAB : currentTab);
        bottomNavigation.setOnItemChangeListener(new OnItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int i) {
                if (i == 3) {
                    isShowedAdd = false;
                }
                loadFragment(i);
            }

            @Override
            public void onSelectAgain(int i) {
                Fragment page = getChildFragmentManager().findFragmentById(R.id.viewpager);
                if (page instanceof BaseMainFragments) {
                    ((BaseMainFragments) page).scrollToTopOfList();
                }
            }
        });

        bottomNavigation.setProfileOnLongClickListener(v -> {
            openAccountsDialog();
            return false;
        });
    }

    public void setCrawlerMap(String crawlerMap) {
        this.crawlerMap = crawlerMap;
    }

    private void openAccountsDialog() {
        if (getActivity() == null) return;
        new AccountsDialog()
                .setData(avatarHandler, (isAssigned, id) -> {
                })
                .show(getActivity().getSupportFragmentManager(), "account");

    }

    private void loadFragment(int position) {
        currentTab = position;
        hideKeyboard();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        switch (position) {
            case CONTACT_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(RegisteredContactsFragment.class.getName());
                if (fragment == null) {
                    fragment = RegisteredContactsFragment.newInstance(false, false, RegisteredContactsFragment.CONTACTS);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            case CALL_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(StoryPagerFragment.class.getName());
                if (fragment == null) {
                    fragment = new StoryPagerFragment();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            case CHAT_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(FragmentMain.class.getName());
                if (fragment == null) {
                    fragment = FragmentMain.newInstance(FragmentMain.MainType.all);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            case DISCOVERY_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(DiscoveryFragment.class.getName());
                if (fragment == null) {
                    fragment = DiscoveryFragment.newInstance(0);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }

                if (crawlerStruct != null && !crawlerStruct.isWorkDone()) {
                    ((DiscoveryFragment) fragment).setNeedToCrawl(true);
                }
                fragmentTransaction.replace(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
            case PROFILE_FRAGMENT:
                fragment = fragmentManager.findFragmentByTag(FragmentUserProfile.class.getName());
                if (fragment == null) {
                    fragment = new FragmentUserProfile();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                fragmentTransaction.replace(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        G.onUnreadChange = null;
    }

    @Override
    public void onChange(int unreadTotal) {
        bottomNavigation.setOnBottomNavigationBadge(unreadTotal);
    }

    public void goToUserProfile() {
        bottomNavigation.setCurrentItem(PROFILE_FRAGMENT);
    }


    public boolean isFirstTabItem() {
        if (bottomNavigation.getSelectedItemPosition() == CHAT_FRAGMENT) {
            return true;
        } else {
            bottomNavigation.setCurrentItem(CHAT_FRAGMENT);
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

    public void checkHasSharedData(boolean enable) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentMain.class.getName());

        if (fragment instanceof FragmentMain) {
            if (enable) {
                ((FragmentMain) fragment).checkHasSharedData(true);
            } else {
                ((FragmentMain) fragment).revertToolbarFromForwardMode();
            }
        }
    }

    public void autoLinkCrawler(String uri, DiscoveryFragment.CrawlerStruct.OnDeepValidLink onDeepLinkValid) {
        if (uri.equals("")) {
            onDeepLinkValid.linkInvalid(uri);
            return;
        }

        String[] address = uri.toLowerCase().trim().split("/");

        if (address.length == 0) {
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


                if (address.length > 1) {
                    int lastIndexOfDiscovery = discoveryUri.length - 1;

                    if (HelperString.isInteger(discoveryUri[lastIndexOfDiscovery])) {
                        onDeepLinkValid.linkValid(discoveryUri[lastIndexOfDiscovery]);
                        DeepLinkHelper.HandleDiscoveryDeepLink(this, discoveryUri[lastIndexOfDiscovery]);
                    } else
                        onDeepLinkValid.linkInvalid(discoveryUri[lastIndexOfDiscovery]);

                } else {
                    onDeepLinkValid.linkValid(discoveryUri[0]);
                    setCrawlerMap(DISCOVERY_FRAGMENT, discoveryUri);
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
            case DEEP_LINK_NEWS:
                onDeepLinkValid.linkValid(address[0]);
                setCrawlerMap(NEWS_FRAGMENT, address);
                break;
            default:
                onDeepLinkValid.linkInvalid(address[0]);
                break;
        }
    }

    private void setCrawlerMap(int position, String[] uri) {

        try {
            if (uri != null && uri.length > 0) {
                if (!uri[0].equals(DEEP_LINK_DISCOVERY) && position == DISCOVERY_FRAGMENT) {
                    List<Integer> pages = new ArrayList<>();
                    for (String s : uri) {
                        pages.add(Integer.valueOf(s));
                    }
                    this.crawlerStruct = new DiscoveryFragment.CrawlerStruct(0, pages);
                }
            }
        } catch (Exception e) {
            HelperError.showSnackMessage(getResources().getString(R.string.link_not_valid), false);
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
                            Bundle bundle = new Bundle();
                            bundle.putString("id", uri[1]);
                            popularMoreChannelFragment.setArguments(bundle);
                            new HelperFragment(getFragmentManager(), popularMoreChannelFragment).setReplace(false).load();
                        } else {
                            new HelperFragment(getFragmentManager(), new PopularChannelHomeFragment()).setReplace(false).load();
                        }
                    break;
                case NEWS_FRAGMENT:
                    if (uri != null) {
                        NewsMainFrag frag = new NewsMainFrag();
                        switch (uri.length) {
                            case 2:
                                frag.setSpecificGroupID(uri[1]);
                                break;
                            case 3:
                                frag.setSpecificNewsID(uri[2]);
                                break;
                            default:
                                break;
                        }
                        new HelperFragment(getFragmentManager(), frag).setReplace(false).load(false);
                    }
                    break;
                case PROFILE_FRAGMENT:
                    bottomNavigation.setCurrentItem(PROFILE_FRAGMENT);
                    break;
            }
        }
    }

    protected void hideKeyboard() {
        if (getActivity() != null) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public DiscoveryFragment.CrawlerStruct getCrawlerStruct() {
        return crawlerStruct;
    }

    public void updateContacts() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(RegisteredContactsFragment.class.getName());

        if (fragment instanceof RegisteredContactsFragment) {
            ((RegisteredContactsFragment) fragment).loadContacts();
        }
    }

    private void setTheme() {
        if (getContext() != null) {
            getContext().getTheme().applyStyle(new Theme().getTheme(getContext()), true);
        }
    }
}
