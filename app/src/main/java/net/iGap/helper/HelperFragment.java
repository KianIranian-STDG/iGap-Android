package net.iGap.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentChat;

public class HelperFragment {

    public static String chatName = FragmentChat.class.getName();

    static class StructFrag {
        String tag = null;
        Fragment fragment;
    }

    //*****************************************************************************************************************

    public static void loadFragment(Fragment fragment) {

        loadFragment(fragment, false);
    }

    public static void loadFragment(Fragment fragment, boolean stateLoose) {

        if (fragment == null) {
            return;
        }

        if (G.fragmentManager == null) {

            HelperLog.setErrorLog("helper fragment    loadFragment     " + fragment.getClass().getName());
            return;
        }


        String tag = fragment.getClass().getName();

        int resId = getResContainer(tag);

        FragmentTransaction ft = G.fragmentManager.beginTransaction();

        ft.addToBackStack(tag).add(resId, fragment, tag).
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);

        if (stateLoose) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
    }

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

    public static int getResContainer(String fragmentClassName) {

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
            resId = R.id.am_frame_main_container;
        }

        return resId;
    }

    public static void removeAllFragment() {

        for (Fragment f : G.fragmentManager.getFragments()) {
            if (f != null) {
                G.fragmentManager.beginTransaction().remove(f).commit();
            }
        }

        G.fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    }

    private static boolean isChatFragment(String fragmentClassName) {

        if (fragmentClassName.equals(chatName)) {
            return true;
        } else {
            return false;
        }
    }

    public static void removeFreagment(Fragment fragment) {

        if (G.fragmentManager == null || fragment == null) {
            return;
        }

        G.fragmentManager.beginTransaction().remove(fragment).commit();
        G.fragmentManager.popBackStack();

        ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    }
}
