package net.iGap.observers.eventbus;

/**
 * Created by keyvan on 7/14/17.
 */

public interface ErrorHandler {
    void onFailure(Throwable e);
}