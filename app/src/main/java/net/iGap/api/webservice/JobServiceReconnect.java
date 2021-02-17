package net.iGap.api.webservice;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import net.iGap.WebSocketClient;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;

public class JobServiceReconnect extends JobService {
    EventManager.EventManagerDelegate eventListener;

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        Log.d("bagi", "JobServiceReconnectStart");
        if (WebSocketClient.getInstance().isAutoConnect()) {
            addListener(job);
            WebSocketClient.getInstance().connect(true);
            return true;
        }

        return false;
    }

    private void addListener(JobParameters job) {
        eventListener = (id, account, args) -> {

            if (id == EventManager.SOCKET_CONNECT_DENY || id == EventManager.SOCKET_CONNECT_ERROR || id == EventManager.SOCKET_CONNECT_OK) {
                Log.d("bagi", "JobServiceReconnectStartFinish");
                removeListener();
                jobFinished(job, true);
            }
        };

        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.SOCKET_CONNECT_DENY, eventListener);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.SOCKET_CONNECT_ERROR, eventListener);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.SOCKET_CONNECT_OK, eventListener);
    }

    private void removeListener() {
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.SOCKET_CONNECT_DENY, eventListener);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.SOCKET_CONNECT_ERROR, eventListener);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.SOCKET_CONNECT_OK, eventListener);
        eventListener = null;

    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        Log.d("bagi", "JobServiceReconnectStop");
        if (eventListener != null) {
            removeListener();
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            removeListener();
        }
    }

    public static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Bundle extras = new Bundle();
        Job job = dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(JobServiceReconnect.class)
                .setTag("JobReconnect")
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(0, 60 * 60))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setExtras(extras)
                .build();

        dispatcher.mustSchedule(job);
    }

    public static void cancelJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel("JobReconnect");
    }

    public static void cancelAllJobs(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancelAll();
    }
}
