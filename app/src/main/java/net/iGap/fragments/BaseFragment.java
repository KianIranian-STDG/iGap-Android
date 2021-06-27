/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.controllers.MessageController;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.controllers.RoomController;
import net.iGap.controllers.UserController;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.libs.swipeback.SwipeBackFragment;
import net.iGap.libs.swipeback.SwipeBackLayout;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.IDownloader;
import net.iGap.module.upload.IUpload;
import net.iGap.module.upload.Uploader;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventManager;

public class BaseFragment extends SwipeBackFragment {

    public boolean isNeedResume = false;
    protected Fragment currentFragment;
    public int currentAccount = AccountManager.selectedAccount;
    public AvatarHandler avatarHandler;
    protected Context context;
    protected View fragmentView;
    protected Toolbar toolbar;
    protected Dialog currentDialog;
    protected int fragmentUniqueId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

        G.fragmentActivity = (FragmentActivity) context;
        currentFragment = this;
//        hideKeyboard();
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        avatarHandler = new AvatarHandler();
        fragmentUniqueId = RequestManager.getLastClassUniqueId();
        onFragmentCreate();
        super.onCreate(savedInstanceState);

        getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_LEFT);

        getSwipeBackLayout().setOnTouchListener((view, motionEvent) -> {
            if (ActivityMain.disableSwipe) {
                getSwipeBackLayout().setEnableGesture(false);
            } else {
                getSwipeBackLayout().setEnableGesture(true);
            }
            return false;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("create", "onViewCreated: " );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("create", "onResume: " );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("create", "onCreateView: " );
        View toolBar = createToolBar(context);
        View rootView = createView(context);

        toolbar = (Toolbar) toolBar;

        if (toolbar != null) {
            fragmentView = rootView;

            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.setBackgroundColor(Theme.getInstance().getRootColor(context));
            frameLayout.addView(toolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 0, 0, 0, 0));
            frameLayout.addView(fragmentView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, 60, 0, 0));

            rootView = frameLayout;
        }

        return rootView;
    }

    public boolean onFragmentCreate() {//for init data this scope
        return true;
    }

    public View createView(Context context) {
        return null;
    }

    public View createToolBar(Context context) {
        return null;
    }

    public void finish() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    public void replaceFragment(Fragment fragment, int frameLayoutId) {
        if (getActivity() != null)
            new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(fragment).setResourceContainer(frameLayoutId).load();
    }

    public void replaceFragment(Fragment fragment) {
        if (getActivity() != null)
            new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(fragment).load();
    }

    public void addFragment(Fragment fragment, int frameLayoutId) {
        if (getActivity() != null)
            new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(fragment).setResourceContainer(frameLayoutId).setReplace(false).load();
    }

    public void addFragment(Fragment fragment) {
        if (getActivity() != null)
            new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(fragment).setReplace(false).load();
    }

    public View getFragmentView() {
        return fragmentView;
    }

    public Dialog showDialog(Dialog dialog, final Dialog.OnDismissListener onDismissListener) {
        if (dialog == null || fragmentView == null) {
            return null;
        }

        try {
            if (currentDialog != null) {
                currentDialog.dismiss();
                currentDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }

        try {
            currentDialog = dialog;
            currentDialog.setCanceledOnTouchOutside(true);
            currentDialog.setOnDismissListener(dialog1 -> {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(dialog1);
                }
                onDialogDismiss((Dialog) dialog1);
                if (dialog1 == currentDialog) {
                    currentDialog = null;
                }
            });
            currentDialog.show();
            return currentDialog;
        } catch (Exception e) {
            FileLog.e(e);
        }

        return null;
    }

    protected void onDialogDismiss(Dialog dialog) {

    }

    public void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        hideKeyboard();
        if (getActivity() != null) {
            //Todo : check logic and fixed this
            if (getActivity() instanceof ActivityMain) {
                for (int i = getActivity().getSupportFragmentManager().getFragments().size() - 1; i >= 0; i--) {
                    Fragment f = getActivity().getSupportFragmentManager().getFragments().get(i);
                    if (f instanceof BaseFragment && f != currentFragment) {
                        if (((BaseFragment) f).isNeedResume) {
                            f.onResume();
                        }
                        break;
                    }
                }
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

    public void popBackStackFragment() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getActivity() != null) {
                        Log.wtf(this.getClass().getName(), "popBackStackFragment");
                        getActivity().onBackPressed();

                        if (G.iTowPanModDesinLayout != null) {
                            G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
                        }
                    }
                } catch (Exception empty) {
                    empty.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPause() {
        try {
            if (currentDialog != null && currentDialog.isShowing()) {
                currentDialog.dismiss();
                currentDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }

        super.onPause();
    }

    public void dismissCurrentDialog() {
        if (currentDialog == null) {
            return;
        }

        try {
            currentDialog.dismiss();
            currentDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getRequestManager().cancelRequestByUniqueId(fragmentUniqueId);
    }

    public void removeFromBaseFragment() {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), currentFragment).remove();
        }
    }

    public void removeFromBaseFragment(Fragment fragment) {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).remove();
        }
    }

    public void openKeyBoard() {

        try {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            //nothing
        }
    }

    public boolean onBackPressed() {
        return false;
    }


    public EventManager getEventManager() {
        return EventManager.getInstance(currentAccount);
    }

    public MessageController getMessageController() {
        return MessageController.getInstance(currentAccount);
    }

    public RoomController getRoomController() {
        return RoomController.getInstance(currentAccount);
    }

    public UserController getUserController() {
        return UserController.getInstance(currentAccount);
    }

    public MessageDataStorage getMessageDataStorage() {
        return MessageDataStorage.getInstance(currentAccount);
    }

    public RequestManager getRequestManager() {
        return RequestManager.getInstance(currentAccount);
    }

    public IDownloader getDownloader() {
        return Downloader.getInstance(currentAccount);
    }

    public IUpload getIUploader() {
        return Uploader.getInstance();
    }

    public ChatSendMessageUtil getSendMessageUtil() {
        return ChatSendMessageUtil.getInstance(currentAccount);
    }
}
