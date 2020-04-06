package net.iGap.observers.rx;

import android.util.Log;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ObserverViewModel extends BaseAPIViewModel implements EventListener {
    public CompositeDisposable backgroundDisposable;
    public CompositeDisposable mainThreadDisposable;

    public ObserverViewModel() {
        backgroundDisposable = new CompositeDisposable();
        mainThreadDisposable = new CompositeDisposable();
        EventManager.getInstance().addEventListener(EventManager.IG_ERROR, this);
    }

    public void onResponseError(Throwable throwable) {
//        Log.i("abbasiResponse", "onResponseError START ---------------------------------");
//        throwable.printStackTrace();
//        Log.i("abbasiResponse", "onResponseError END ---------------------------------");
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.IG_ERROR)
            try {
                onResponseError((Throwable) message[0]);
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public abstract void subscribe();

    public void onFragmentViewCreated() {
    }

    public void onDestroyView() {
        Log.i(getClass().getName(), "onDestroyView: ");
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        if (mainThreadDisposable != null && !mainThreadDisposable.isDisposed()) {
            mainThreadDisposable.dispose();
            mainThreadDisposable = null;
        }
    }

    public void onDestroy() {
        Log.i(getClass().getName(), "onDestroy: ");
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        if (backgroundDisposable != null && !backgroundDisposable.isDisposed()) {
            backgroundDisposable.dispose();
            backgroundDisposable = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onDestroy();
    }
}
