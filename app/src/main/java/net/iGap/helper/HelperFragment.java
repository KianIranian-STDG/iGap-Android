package net.iGap.helper;

import android.content.res.Configuration;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.EditChannelFragment;
import net.iGap.fragments.EditGroupFragment;
import net.iGap.fragments.FragmentChannelProfile;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentGallery;
import net.iGap.fragments.FragmentGroupProfile;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.FragmentShowImage;
import net.iGap.fragments.FragmentShowMember;
import net.iGap.fragments.PaymentFragment;
import net.iGap.fragments.ShowCustomList;
import net.iGap.fragments.TabletEmptyChatFragment;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.giftStickers.giftCardDetail.MainGiftStickerCardFragment;
import net.iGap.observers.interfaces.PaymentCallBack;

/**
 * this is helper class for open new fragment
 */

public class HelperFragment {

    private static String chatName = FragmentChat.class.getName();
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private boolean addToBackStack = true;
    private boolean animated = true;
    private boolean replace = true;
    private boolean stateLoss;
    private boolean hasCustomAnimation;
    private boolean immediateRemove;
    private String tag;
    private int resourceContainer = 0;
    private int enter;
    private int exit;
    private int popEnter;
    private int popExit;

    public HelperFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public HelperFragment(FragmentManager fragmentManager, Fragment fragment) {
        this.fragmentManager = fragmentManager;
        this.fragment = fragment;
    }

    public boolean isFragmentVisible(String fragmentTag) {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        return fragment != null && fragment.isVisible();
    }

    public HelperFragment setFragment(Fragment fragment) {
        this.fragment = fragment;
        return this;
    }

    public HelperFragment setAddToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
        return this;
    }

    public HelperFragment setAnimated(boolean animated) {
        this.animated = animated;
        return this;
    }

    public HelperFragment setReplace(boolean replace) {
        this.replace = replace;
        return this;
    }

    public HelperFragment setStateLoss(boolean stateLoss) {
        this.stateLoss = stateLoss;
        return this;
    }

    public HelperFragment setImmediateRemove(boolean immediateRemove) {
        this.immediateRemove = immediateRemove;
        return this;
    }

    public HelperFragment setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public HelperFragment setResourceContainer(int resourceContainer) {
        this.resourceContainer = resourceContainer;
        return this;
    }

    public HelperFragment setAnimation(int enter, int exit, int popEnter, int popExit) {
        hasCustomAnimation = true;
        this.enter = enter;
        this.exit = exit;
        this.popEnter = popEnter;
        this.popExit = popExit;
        return this;
    }

    public void load(boolean checkStack) {
        if (fragment == null) {
            return;
        }

        try {
            if (checkStack) {
                if (fragment.getClass().getName().equalsIgnoreCase(FragmentChat.class.getName())) {
                    if (SystemClock.elapsedRealtime() - G.mLastClickTime > 1000) {
                        G.mLastClickTime = SystemClock.elapsedRealtime();
                    } else {
                        Log.wtf(this.getClass().getName(), "return1");
                        return;
                    }
                } else if (fragmentManager.getBackStackEntryCount() > 1) {
                    if ((fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName().equalsIgnoreCase(fragment.getClass().getName()))) {
                        Log.wtf(this.getClass().getName(), "return2");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fragmentManager == null) {
            HelperLog.getInstance().setErrorLog(new Exception("helper fragment loadFragment -> " + fragment.getClass().getName()));
            return;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (tag == null) {
            tag = fragment.getClass().getName();
        }

        if (getAnimation(tag)) {
            if (hasCustomAnimation) {
                fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
            }
        }

        if (resourceContainer == 0) {
            resourceContainer = getResContainer(tag);
        }

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }

        if (replace) {
            fragmentTransaction.replace(resourceContainer, fragment, tag);
        } else {
            fragmentTransaction.add(resourceContainer, fragment, tag);
        }

        try {
            if (stateLoss) {
                fragmentTransaction.commitAllowingStateLoss();
            } else {
                fragmentTransaction.commit();
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        load(true);
    }

    public void remove() {
        try {
            if (fragment == null) {
                return;
            }
            fragmentManager.beginTransaction().remove(fragment).commit();

            if (immediateRemove) {
                fragmentManager.popBackStackImmediate();
            } else {
                fragmentManager.popBackStack();
            }

            if (G.iTowPanModDesinLayout != null) {
                G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void removeAll(boolean keepMain) {
        /*if (G.fragmentActivity != null && !G.fragmentActivity.isFinishing()) {*/
        if (G.twoPaneMode) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.mainFrame);
            if (fragment instanceof FragmentChat) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
            fragmentManager.popBackStack(TabletEmptyChatFragment.class.getName(), 0);
        } else {
            fragmentManager.popBackStack(BottomNavigationFragment.class.getName(), 0);
        }
        /*for (int i = fragmentManager.getBackStackEntryCount() - 1; i > -1; i--) {

        }*/
        /*for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null) {

                if (keepMain) {
                    if (fragment.getClass().getName().equals(FragmentMain.class.getName())) {
                        continue;
                    }
                    if (fragment.getClass().getName().equals(DiscoveryFragment.class.getName())) {
                        if (fragment.getArguments().getInt("page") == 0) {
                            continue;
                        }
                    }
                    if (fragment instanceof FragmentCall) {
                        if (fragment.getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN)) {
                            continue;
                        }
                    }

                    fragmentManager.beginTransaction().remove(fragment).commit();
                } else {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }
            }
        }
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);*/
        /*}*/

        /*if (G.iTowPanModDesinLayout != null) {
            G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
        }*/
    }

    public void popBackStack() {
        fragmentManager.popBackStack();
    }

    public void popBackStack(int count) {
        for (int i = 0; i < count; i++) {
            fragmentManager.popBackStackImmediate();
        }
    }

    private boolean getAnimation(String tag) {

        for (String immovableClass : G.generalImmovableClasses) {
            if (tag.equals(immovableClass)) {
                return false;
            }
        }

        if (G.twoPaneMode) {
            if ((G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) && (tag.equals(FragmentChat.class.getName()))) {
                return true;
            }

            return G.iTowPanModDesinLayout != null && G.iTowPanModDesinLayout.getBackChatVisibility();
        } else {
            return true;
        }

    }

    private boolean isChatFragment(String fragmentClassName) {
        return fragmentClassName.equals(chatName);
    }

    private int getResContainer(String fragmentClassName) {

        if (fragmentClassName == null || fragmentClassName.length() == 0) {
            return 0;
        }

        if (G.twoPaneMode) {
            if (fragmentClassName.equals(FragmentShowImage.class.getName()) || fragmentClassName.equals(FragmentShowAvatars.class.getName())) {
                /*if (G.isLandscape) {*/
                if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.setBackChatVisibility(true);
                }
                return R.id.fullScreenFrame;
                /*} else {
                    return R.id.mainFrame;
                }*/
            } else if (isChatFragment(fragmentClassName)) {
                /*if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.show);
                }*/
                return R.id.mainFrame;
            } else if (fragmentClassName.equals(BottomNavigationFragment.class.getName())) {
                return R.id.roomListFrame;
            } else if (fragmentClassName.equals(TabletEmptyChatFragment.class.getName())) {
                return R.id.mainFrame;
            } else {
                if (isRelatedToChat(fragmentClassName)) {
                    if (G.iTowPanModDesinLayout != null) {
                        G.iTowPanModDesinLayout.setBackChatVisibility(true);
                    }
                    return R.id.detailFrame;
                } else {
                    return R.id.roomListFrame;
                }
            }
        } else {
            return R.id.mainFrame;
        }
    }

    public void loadPayment(String title, String token, PaymentCallBack callBack) {
        fragment = PaymentFragment.getInstance(title, token, callBack);
        setAnimation(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        replace = false;
        load();
    }

    public void loadPayment(String title, boolean valueAdded, String token, PaymentCallBack callBack) {
        fragment = PaymentFragment.getInstance(title, valueAdded, token, callBack);
        setAnimation(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        replace = false;
        load();
    }

    public void loadActiveGiftStickerCard(StructIGSticker structIGSticker, View.OnClickListener sendOtherListener, int mode) {
        MainGiftStickerCardFragment fragment = MainGiftStickerCardFragment.getInstance(structIGSticker, sendOtherListener, mode);
        fragment.show(fragmentManager, "gift sticker card");
    }

    private boolean isRelatedToChat(String fragmentName) {
        return fragmentName.equals(FragmentChannelProfile.class.getName()) || fragmentName.equals(EditChannelFragment.class.getName()) ||
                fragmentName.equals(FragmentGroupProfile.class.getName()) || fragmentName.equals(EditGroupFragment.class.getName()) ||
                fragmentName.equals(FragmentContactsProfile.class.getName()) || fragmentName.equals(ShowCustomList.class.getName()) ||
                fragmentName.equals(FragmentShearedMedia.class.getName()) || fragmentName.equals(FragmentEditImage.class.getName()) ||
                fragmentName.equals(FragmentShowMember.class.getName()) || fragmentName.equals(FragmentGallery.class.getName());
    }
}
