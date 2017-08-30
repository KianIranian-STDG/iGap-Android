package net.iGap.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentChat;

public class HelperFragment {

    private Fragment fragment;
    private boolean addToBackStack = true;
    private boolean animated = true;
    private boolean replace = true;
    private boolean stateLoss;
    private boolean hasCustomAnimation;
    private String tag;
    private int resourceContainer = 0;
    private int enter;
    private int exit;
    private int popEnter;
    private int popExit;

    private static String chatName = FragmentChat.class.getName();

    public HelperFragment() {
    }

    public HelperFragment(Fragment fragment) {
        this.fragment = fragment;
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

    public void load() {
        if (fragment == null) {
            return;
        }
        if (G.fragmentManager == null) {
            HelperLog.setErrorLog("helper fragment loadFragment -> " + fragment.getClass().getName());
            return;
        }

        if (tag == null) {
            tag = fragment.getClass().getName();
        }

        if (resourceContainer == 0) {
            resourceContainer = getResContainer(tag);
        }

        FragmentTransaction fragmentTransaction = G.fragmentManager.beginTransaction();

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }

        if (animated) {
            if (hasCustomAnimation) {
                fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
            }
        }

        if (replace) {
            fragmentTransaction.replace(resourceContainer, fragment, tag);
        } else {
            fragmentTransaction.add(resourceContainer, fragment, tag);
        }

        if (stateLoss) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }
    }

    public void remove() {
        if (fragment == null) {
            return;
        }
        G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        G.fragmentActivity.getSupportFragmentManager().popBackStack();
        ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    }

    public void removeAll() {
        for (Fragment f : G.fragmentActivity.getSupportFragmentManager().getFragments()) {
            if (f != null) {
                G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(f).commit();
            }
        }
        G.fragmentActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    }

    public void popBackStack() {
        G.fragmentActivity.getSupportFragmentManager().popBackStack();
    }

    public static Fragment isFragmentVisible(String fragmentTag) {
        FragmentChat fragment = (FragmentChat) G.fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null && fragment.isVisible()) {
            return fragment;
        }
        return null;
    }

    public static Fragment isFragmentVisible(ArrayList<String> fragmentTags) {
        for (String fragmentTag : fragmentTags) {
            FragmentChat fragment = (FragmentChat) G.fragmentActivity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (fragment != null && fragment.isVisible()) {
                return fragment;
            }
        }
        return null;
    }

    private boolean isChatFragment(String fragmentClassName) {

        if (fragmentClassName.equals(chatName)) {
            return true;
        } else {
            return false;
        }
    }

    private int getResContainer(String fragmentClassName) {

        if (fragmentClassName == null || fragmentClassName.length() == 0) {
            return 0;
        }

        int resId = 0;

        if (G.twoPaneMode) {

            if (isChatFragment(fragmentClassName)) {

                resId = R.id.am_frame_chat_container;
                ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.show);

            } else {

                resId = R.id.am_frame_fragment_container;

                if (ActivityMain.frameFragmentBack != null) {
                    ActivityMain.frameFragmentBack.setVisibility(View.VISIBLE);
                }
            }
        } else {
            resId = R.id.frame_main;
        }

        return resId;
    }

    //public static void removeAllFragment() {
    //
    //    for (Fragment f : G.fragmentManager.getFragments()) {
    //        if (f != null) {
    //            G.fragmentManager.beginTransaction().remove(f).commit();
    //        }
    //    }
    //
    //    G.fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    //
    //    ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    //}
    //
    //public static void removeFreagment(Fragment fragment) {
    //
    //    if (G.fragmentManager == null || fragment == null) {
    //        return;
    //    }
    //
    //    G.fragmentManager.beginTransaction().remove(fragment).commit();
    //    G.fragmentManager.popBackStack();
    //
    //    ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    //}

    //*****************************************************************************************************************

    //public static void changeFragmentResourceContainer(FragmentManager fm) {
    //
    //    Fragment fragmentChat = fm.findFragmentByTag(chatName);
    //
    //    if (fragmentChat != null) {
    //
    //        ArrayList<StructFrag> list = new ArrayList<>();
    //
    //        for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
    //
    //            Fragment f = fm.getFragments().get(i);
    //
    //            if (f == null) {
    //                continue;
    //            }
    //
    //            StructFrag st = new StructFrag();
    //            st.fragment = f;
    //            st.tag = f.getTag();
    //            list.add(st);
    //
    //            fm.beginTransaction().remove(f).commit();
    //            fm.executePendingTransactions();
    //
    //            if (f == fragmentChat) {
    //                break;
    //            }
    //        }
    //
    //        fm.popBackStackImmediate(chatName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    //
    //        for (int j = list.size() - 1; j >= 0; j--) {
    //
    //            StructFrag st = list.get(j);
    //
    //            fm.beginTransaction().addToBackStack(st.tag).replace(getResContainer(st.fragment.getClass().getName()), st.fragment, st.tag).commit();
    //        }
    //    }
    //}
}
