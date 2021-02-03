package net.iGap.observers.eventbus;

import android.util.SparseArray;

import androidx.annotation.UiThread;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.helper.FileLog;

import java.util.ArrayList;


/**
 * Created by keyvan on 3/14/16.
 */
public class EventManager {

    /**
     * READ ME :
     * this class used for notifying part of application to another part that not related to each other
     * for using this class you need to use instance method.
     * if your event number is exists just use it (I will tell in continue how to use), else declare your event number
     * event number MUST NOT equal with other event number, for this purpose use '++' to increment last number.
     * <p/>
     * HOW TO USE:
     * in onCreate() or onStart() ( It's related to your usage ) use following function :
     * {@link EventManager#getInstance()#addEventListener(int, EventListener)}
     * int is your event num and EventListener can be `this` so your activity must be implement or new instance of EventListener
     * {@link EventListener}.
     * then in onDestroy() or onPause() use {@link EventManager#getInstance()#removeEventListener(int, EventListener)}
     * to optimize eventManager and prevent from crash.
     * PS: if you remove your listener with above code your component not listen on event anymore.
     * <p/>
     * after getting event onReceived will be called in eventManager thread,
     * if you want heavy process in {@link EventListener#receivedMessage(int, Object...)} use your thread
     */

    private static int eventId = 1;

    public static final int stopAllHeavyOperations = eventId++;
    public static final int startAllHeavyOperations = eventId++;

    public static final int networkStateChange = eventId++;
    public static final int didSetNewTheme = eventId++;

    public static final int ON_ACCESS_TOKEN_RECIVE = eventId++;
    public static final int ON_INIT_PAY = eventId++;
    public static final int ON_INIT_PAY_ERROR = eventId++;
    public static final int ON_PAYMENT_RESULT_RECIEVED = eventId++;

    public static final int ON_UPLOAD_PROGRESS = eventId++;
    public static final int ON_UPLOAD_COMPRESS = eventId++;

    public static final int CALL_STATE_CHANGED = eventId++;

    public static final int SOCKET_CONNECT_DENY = eventId++;
    public static final int SOCKET_CONNECT_OK = eventId++;
    public static final int SOCKET_CONNECT_ERROR = eventId++;
    public static final int SOCKET_DISCONNECT = eventId++;

    public static final int STICKER_DOWNLOAD = eventId++;
    public static final int EMOJI_LOADED = eventId++;
    public static final int IG_ERROR = eventId++;
    public static final int STICKER_CHANGED = eventId++;

    public static final int ROOM_LIST_CHANGED = eventId++;
    public static final int CHAT_BACKGROUND_CHANGED = eventId++;
    public static final int ON_MESSAGE_DELETE = eventId++;
    public static final int AVATAR_UPDATE = eventId++;

    public static final int FILE_UPLOAD_PROGRESS = eventId++;
    public static final int FILE_UPLOAD_FAILED = eventId++;
    public static final int FILE_UPLOAD_SUCCESS = eventId++;
    public static final int APP_CONFIG_CHANGED = eventId++;
    public static final int PRIVACY_CHANGED = eventId++;

    public static final int ON_EDIT_MESSAGE = eventId++;
    public static final int ON_PINNED_MESSAGE = eventId++;
    public static final int CHAT_CLEAR_MESSAGE = eventId++;
    public static final int ON_UPLOAD_COMPLETED = eventId++;

    public static final int CHAT_UPDATE_STATUS = eventId++;
    public static final int ON_FILE_DOWNLOAD_COMPLETED = eventId++;

    private SparseArray<ArrayList<NotificationCenterDelegate>> observers = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> removeAfterBroadcast = new SparseArray<>();
    private SparseArray<ArrayList<NotificationCenterDelegate>> addAfterBroadcast = new SparseArray<>();
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);

    private int broadcasting = 0;
    private boolean animationInProgress;
    private int[] allowedNotifications;

    private int currentAccount;

    private int currentHeavyOperationFlags;
    private static volatile EventManager[] Instance = new EventManager[3];
    private static volatile EventManager globalInstance;

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, int account, Object... args);
    }

    private class DelayedPost {

        private DelayedPost(int id, Object[] args) {
            this.id = id;
            this.args = args;
        }

        private int id;
        private Object[] args;
    }

    public EventManager(int account) {
        currentAccount = account;
    }

    @UiThread
    public static EventManager getInstance(int num) {
        EventManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (EventManager.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new EventManager(num);
                }
            }
        }
        return localInstance;
    }

    @UiThread
    public static EventManager getGlobalInstance() {
        EventManager localInstance = globalInstance;
        if (localInstance == null) {
            synchronized (EventManager.class) {
                localInstance = globalInstance;
                if (localInstance == null) {
                    globalInstance = localInstance = new EventManager(-1);
                }
            }
        }
        return localInstance;
    }

    public void setAllowedNotificationsDutingAnimation(int[] notifications) {
        allowedNotifications = notifications;
    }

    public void setAnimationInProgress(boolean value) {
        if (value) {
            G.runOnUiThread(() -> EventManager.getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512));
        } else {
            G.runOnUiThread(() -> EventManager.getGlobalInstance().postNotificationName(startAllHeavyOperations, 512));
        }
        animationInProgress = value;
        if (!animationInProgress && !delayedPosts.isEmpty()) {
            for (int a = 0; a < delayedPosts.size(); a++) {
                DelayedPost delayedPost = delayedPosts.get(a);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            delayedPosts.clear();
        }
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = id == startAllHeavyOperations || id == stopAllHeavyOperations;
        if (!allowDuringAnimation && allowedNotifications != null) {
            for (int a = 0; a < allowedNotifications.length; a++) {
                if (allowedNotifications[a] == id) {
                    allowDuringAnimation = true;
                    break;
                }
            }
        }
        if (id == startAllHeavyOperations) {
            Integer flags = (Integer) args[0];
            currentHeavyOperationFlags &= ~flags;
        } else if (id == stopAllHeavyOperations) {
            Integer flags = (Integer) args[0];
            currentHeavyOperationFlags |= flags;
        }
        postNotificationNameInternal(id, allowDuringAnimation, args);
    }

    public boolean isAnimationInProgress() {
        return animationInProgress;
    }

    public int getCurrentHeavyOperationFlags() {
        return currentHeavyOperationFlags;
    }

    @UiThread
    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != G.handler.getLooper().getThread()) {
                throw new RuntimeException("postNotificationName allowed only from MAIN thread");
            }
        }
        if (!allowDuringAnimation && animationInProgress) {
            DelayedPost delayedPost = new DelayedPost(id, args);
            delayedPosts.add(delayedPost);
            if (Config.FILE_LOG_ENABLE) {
                FileLog.e("delay post notification " + id + " with args count = " + args.length);
            }
            return;
        }
        broadcasting++;
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            for (int a = 0; a < objects.size(); a++) {
                NotificationCenterDelegate obj = objects.get(a);
                obj.didReceivedNotification(id, currentAccount, args);
            }
        }
        broadcasting--;
        if (broadcasting == 0) {
            if (removeAfterBroadcast.size() != 0) {
                for (int a = 0; a < removeAfterBroadcast.size(); a++) {
                    int key = removeAfterBroadcast.keyAt(a);
                    ArrayList<NotificationCenterDelegate> arrayList = removeAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        removeObserver(key, arrayList.get(b));
                    }
                }
                removeAfterBroadcast.clear();
            }
            if (addAfterBroadcast.size() != 0) {
                for (int a = 0; a < addAfterBroadcast.size(); a++) {
                    int key = addAfterBroadcast.keyAt(a);
                    ArrayList<NotificationCenterDelegate> arrayList = addAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        addObserver(key, arrayList.get(b));
                    }
                }
                addAfterBroadcast.clear();
            }
        }
    }

    public void addObserver(int id, NotificationCenterDelegate observer) {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != G.handler.getLooper().getThread()) {
                throw new RuntimeException("addObserver allowed only from MAIN thread");
            }
        }
        if (broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (objects.contains(observer)) {
            return;
        }
        objects.add(observer);
    }

    public void removeObserver(int id, NotificationCenterDelegate observer) {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != G.handler.getLooper().getThread()) {
                throw new RuntimeException("removeObserver allowed only from MAIN thread");
            }
        }
        if (broadcasting != 0) {
            ArrayList<NotificationCenterDelegate> arrayList = removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<NotificationCenterDelegate> objects = observers.get(id);
        if (objects != null) {
            objects.remove(observer);
        }
    }

    public boolean hasObservers(int id) {
        return observers.indexOfKey(id) >= 0;
    }

}