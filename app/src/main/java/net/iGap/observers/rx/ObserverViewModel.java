package net.iGap.observers.rx;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ObserverViewModel extends BaseAPIViewModel implements EventManager.EventDelegate {
    public CompositeDisposable backgroundDisposable;
    public CompositeDisposable mainThreadDisposable;

    public ObserverViewModel() {
        backgroundDisposable = new CompositeDisposable();
        mainThreadDisposable = new CompositeDisposable();
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.IG_ERROR, this);
    }

    public void onResponseError(Throwable throwable) {

    }

    public abstract void subscribe();

    public void onFragmentViewCreated() {
    }

    public void onDestroyView() {
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.IG_ERROR, this);
        if (mainThreadDisposable != null && !mainThreadDisposable.isDisposed()) {
            mainThreadDisposable.dispose();
            mainThreadDisposable = null;
        }
    }

    public void onDestroy() {
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.IG_ERROR, this);
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

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.IG_ERROR)
            try {
                onResponseError((Throwable) args[0]);
            } catch (ClassCastException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
