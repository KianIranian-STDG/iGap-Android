package net.iGap.helper.rx;


import android.widget.Toast;

import net.iGap.G;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class AaSingleObserver<T> extends AaObserver implements SingleObserver<T> {


    public AaSingleObserver(CompositeDisposable compositeDisposable) {
        super(compositeDisposable);
    }

    @Override
    public void onSubscribe(Disposable d) {
        compositeDisposable.add(d);
    }

    @Override
    public void onError(Throwable e) {
//        EventBus.getDefault().post(new AaException(ExceptionMessageFactory.getMessage(e)));

        // TODO: 12/31/19  just for test
        Toast.makeText(G.context, e.getMessage(), Toast.LENGTH_SHORT).show();
    }


}
