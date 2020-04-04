package net.iGap.observers.rx;


import android.widget.Toast;

import net.iGap.G;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.ExceptionMessageFactory;
import net.iGap.observers.eventbus.EventManager;

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
        if (compositeDisposable != null)
            compositeDisposable.add(d);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
//        EventBus.getDefault().post(new AaException(ExceptionMessageFactory.getMessage(e)));
        ErrorModel errorModel = ExceptionMessageFactory.getMessage(e);
        G.runOnUiThread(() -> Toast.makeText(G.context, errorModel.getMessage(), Toast.LENGTH_SHORT).show());

        EventManager.getInstance().postEvent(EventManager.IG_ERROR, e);
    }
}
