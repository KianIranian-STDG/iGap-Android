package net.iGap.rx;

import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ObserverViewModel implements EventListener {
    public CompositeDisposable backgroundDisposable;
    public CompositeDisposable mainThreadDisposable;

    public ObserverViewModel() {
        backgroundDisposable = new CompositeDisposable();
        mainThreadDisposable = new CompositeDisposable();
        EventManager.getInstance().addEventListener(EventManager.IG_ERROR, this);
    }

    public void onResponseError(Throwable throwable) {
        throwable.printStackTrace();
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

    public void onDestroyView() {
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        if (mainThreadDisposable != null && !mainThreadDisposable.isDisposed()) {
            mainThreadDisposable.dispose();
            mainThreadDisposable = null;
        }
    }

    public void unsubscribe() {
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        if (backgroundDisposable != null && !backgroundDisposable.isDisposed()) {
            backgroundDisposable.dispose();
            backgroundDisposable = null;
        }
    }
}
