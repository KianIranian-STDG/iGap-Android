package net.iGap.rx;

import android.util.Log;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;

import io.reactivex.disposables.CompositeDisposable;

public class ObserverViewModel extends BaseAPIViewModel implements EventListener {
    public CompositeDisposable compositeDisposable;

    public ObserverViewModel() {
        compositeDisposable = new CompositeDisposable();
        EventManager.getInstance().addEventListener(EventManager.IG_ERROR, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        Log.e(getClass().getName(), "receivedMessage: " + ((Throwable) message[0]).getMessage());
    }
}
