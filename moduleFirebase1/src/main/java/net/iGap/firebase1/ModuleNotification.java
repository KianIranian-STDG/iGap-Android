package net.iGap.firebase1;

import android.net.Uri;

public class ModuleNotification {
    private String body;
    private String[] bodyLocalizationArgs;
    private String bodyLocalizationKey;
    private String channelId;
    private String clickAction;
    private String color;
    private boolean defaultLightSettings;
    private boolean defaultSound;
    private boolean defaultVibrateSettings;
    private long eventTime;
    private String icon;
    private Uri imageUrl;
    private int[] lightSettings;
    private Uri link;
    private boolean localOnly;
    private int notificationCount;
    private int notificationPriority;
    private String sound;
    private boolean sticky;
    private String tag;
    private String ticker;
    private String title;
    private String[] titleLocalizationArgs;
    private String titleLocalizationKey;
    private long[] vibrateTiming;
    private int visibility;

    public ModuleNotification(String body, String[] bodyLocalizationArgs, String bodyLocalizationKey, String channelId, String clickAction, String color, boolean defaultLightSettings, boolean defaultSound, boolean defaultVibrateSettings, long eventTime, String icon, Uri imageUrl, int[] lightSettings, Uri link, boolean localOnly, int notificationCount, int notificationPriority, String sound, boolean sticky, String tag, String ticker, String title, String[] titleLocalizationArgs, String titleLocalizationKey, long[] vibrateTiming, int visibility) {
        this.body = body;
        this.bodyLocalizationArgs = bodyLocalizationArgs;
        this.bodyLocalizationKey = bodyLocalizationKey;
        this.channelId = channelId;
        this.clickAction = clickAction;
        this.color = color;
        this.defaultLightSettings = defaultLightSettings;
        this.defaultSound = defaultSound;
        this.defaultVibrateSettings = defaultVibrateSettings;
        this.eventTime = eventTime;
        this.icon = icon;
        this.imageUrl = imageUrl;
        this.lightSettings = lightSettings;
        this.link = link;
        this.localOnly = localOnly;
        this.notificationCount = notificationCount;
        this.notificationPriority = notificationPriority;
        this.sound = sound;
        this.sticky = sticky;
        this.tag = tag;
        this.ticker = ticker;
        this.title = title;
        this.titleLocalizationArgs = titleLocalizationArgs;
        this.titleLocalizationKey = titleLocalizationKey;
        this.vibrateTiming = vibrateTiming;
        this.visibility = visibility;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String[] getBodyLocalizationArgs() {
        return bodyLocalizationArgs;
    }

    public void setBodyLocalizationArgs(String[] bodyLocalizationArgs) {
        this.bodyLocalizationArgs = bodyLocalizationArgs;
    }

    public String getBodyLocalizationKey() {
        return bodyLocalizationKey;
    }

    public void setBodyLocalizationKey(String bodyLocalizationKey) {
        this.bodyLocalizationKey = bodyLocalizationKey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDefaultLightSettings() {
        return defaultLightSettings;
    }

    public void setDefaultLightSettings(boolean defaultLightSettings) {
        this.defaultLightSettings = defaultLightSettings;
    }

    public boolean isDefaultSound() {
        return defaultSound;
    }

    public void setDefaultSound(boolean defaultSound) {
        this.defaultSound = defaultSound;
    }

    public boolean isDefaultVibrateSettings() {
        return defaultVibrateSettings;
    }

    public void setDefaultVibrateSettings(boolean defaultVibrateSettings) {
        this.defaultVibrateSettings = defaultVibrateSettings;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int[] getLightSettings() {
        return lightSettings;
    }

    public void setLightSettings(int[] lightSettings) {
        this.lightSettings = lightSettings;
    }

    public Uri getLink() {
        return link;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        this.localOnly = localOnly;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public int getNotificationPriority() {
        return notificationPriority;
    }

    public void setNotificationPriority(int notificationPriority) {
        this.notificationPriority = notificationPriority;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getTitleLocalizationArgs() {
        return titleLocalizationArgs;
    }

    public void setTitleLocalizationArgs(String[] titleLocalizationArgs) {
        this.titleLocalizationArgs = titleLocalizationArgs;
    }

    public String getTitleLocalizationKey() {
        return titleLocalizationKey;
    }

    public void setTitleLocalizationKey(String titleLocalizationKey) {
        this.titleLocalizationKey = titleLocalizationKey;
    }

    public long[] getVibrateTiming() {
        return vibrateTiming;
    }

    public void setVibrateTiming(long[] vibrateTiming) {
        this.vibrateTiming = vibrateTiming;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
