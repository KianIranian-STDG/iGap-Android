package net.iGap.rx;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ObserverViewModel extends BaseAPIViewModel implements EventListener {
    public CompositeDisposable compositeDisposable;

    public ObserverViewModel() {
        compositeDisposable = new CompositeDisposable();
        EventManager.getInstance().addEventListener(EventManager.IG_ERROR, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unsubscribe();
        EventManager.getInstance().removeEventListener(EventManager.IG_ERROR, this);
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
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

    public void unsubscribe() {

    }
}
