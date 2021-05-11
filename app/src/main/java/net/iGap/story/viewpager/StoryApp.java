package net.iGap.story.viewpager;

import android.app.Application;

import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

public class StoryApp extends Application {
    public static SimpleCache simpleCache = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024);
        DatabaseProvider databaseProvider = new ExoDatabaseProvider(this);

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, databaseProvider);
        }
    }
}
