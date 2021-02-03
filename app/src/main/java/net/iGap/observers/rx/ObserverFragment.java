package net.iGap.observers.rx;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;

public abstract class ObserverFragment<T extends ObserverViewModel> extends BaseAPIViewFrag<T> implements EventManager.NotificationCenterDelegate {
    protected T viewModel;
    public ViewGroup rootView;

    public abstract void setupViews();

    public abstract int getLayoutRes();

    public abstract T getObserverViewModel();

    public View getLayoutView() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getObserverViewModel();
        setViewModel(viewModel);

        if (viewModel == null)
            throw new NullPointerException("You must set observerViewModel with getObserverViewModel() method");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            if (getLayoutRes() != 0) {
                rootView = (ViewGroup) inflater.inflate(getLayoutRes(), container, false);
                setupViews();
            } else if (getLayoutView() != null) {
                rootView = (ViewGroup) getLayoutView();
                setupViews();
            } else
                throw new NullPointerException("You must set View with getLayoutRes() for xml view and getLayoutView() for custom view method");
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.onFragmentViewCreated();
        Log.e(getClass().getName(), "onViewCreated: ");
    }

    public void finish() {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.subscribe();
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.IG_ERROR, this);
        Log.e(getClass().getName(), "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.IG_ERROR, this);
        Log.e(getClass().getName(), "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        Log.e(getClass().getName(), "onDestroy: ");
    }

    public void onResponseError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == EventManager.IG_ERROR) {
            try {
                onResponseError((Throwable) args[0]);
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
