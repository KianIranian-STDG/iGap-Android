package net.iGap.observers.rx;


import net.iGap.Config;
import net.iGap.G;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.ExceptionMessageFactory;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperError;
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

        if (Config.FILE_LOG_ENABLE) {
            FileLog.e(e);
        }

        e.printStackTrace();
//        EventBus.getDefault().post(new AaException(ExceptionMessageFactory.getMessage(e)));
        ErrorModel errorModel = ExceptionMessageFactory.getMessage(e);
        G.runOnUiThread(() -> HelperError.showSnackMessage(errorModel.getMessage(), false));

        EventManager.getInstance().postEvent(EventManager.IG_ERROR, e);
    }
}
