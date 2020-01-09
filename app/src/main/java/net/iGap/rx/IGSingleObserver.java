package net.iGap.rx;


import net.iGap.eventbus.EventManager;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class IGSingleObserver<T> implements SingleObserver<T> {

    private CompositeDisposable compositeDisposable;

    public IGSingleObserver(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void onSubscribe(Disposable d) {
        compositeDisposable.add(d);
    }

    @Override
    public void onError(Throwable e) {
//        EventBus.getDefault().post(new AaException(ExceptionMessageFactory.getMessage(e)));
        EventManager.getInstance().postEvent(EventManager.IG_ERROR, e);
    }
}
