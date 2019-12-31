package net.iGap.helper.rx;

import io.reactivex.disposables.CompositeDisposable;

class AaObserver {
    CompositeDisposable compositeDisposable;

    AaObserver(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

}
