# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\android\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


########## My Tricks
-keep class net.iGap.model.popularChannel.**
-keep class net.iGap.payment.**
-keep class net.iGap.proto.** , com.neovisionaries.ws.client.** { *; }
-keep class net.iGap.network.LookUpClass { *; }
-keep public class * extends net.iGap.response.MessageHandler
-keepclasseswithmembers class * {
   public <init>(int, java.lang.Object, java.lang.String);
}

-keep class * extends net.iGap.response.MessageHandler {
    *;
}
###

#Warning:cat.ereza.customactivityoncrash.config.CaocConfig$Builder: can't find referenced class cat.ereza.customactivityoncrash.config.CaocConfig$BackgroundMode
###

###Ghamari Date
-keep class com.github.msarhan.ummalqura.** { *; }
-dontwarn com.github.msarhan.ummalqura.**
#

###Chips
-dontwarn com.beloo.widget.chipslayoutmanager.Orientation
#

###CustomCrash
-keep class cat.ereza.customactivityoncrash.** { *; }
-dontwarn cat.ereza.customactivityoncrash.**
#

###Crashlytics
-keep class com.crashlytics.** { *; }
-keepattributes SourceFile,LineNumberTable
-dontwarn com.crashlytics.**
###

###For Compress Module
-dontwarn com.googlecode.mp4parser.**
###

###Trim
-keep class com.coremedia.** { *; }
-keep class com.mp4parser.** { *; }
-keep class com.googlecode.** { *; }
###

-keepattributes Signature,InnerClasses
-keepnames class com.squareup.** {
    *;
}

###Call
-keep class org.codehaus.** { *; }
-dontwarn org.codehaus.**
-keep class org.whispersystems.** { *; }
-dontwarn org.whispersystems.**
-keep class org.webrtc.** { *; }
-dontwarn org.webrtc.**
-keep class org.chromium.** { *; }
-dontwarn org.chromium.**
###

###protobuf
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
###

#
-keep class org.jboss.** { *; }
-keep enum org.jboss.** { *; }
-keep class sun.nio.sctp.AbstractNotificationHandler { *; }

###fastadapter
#after update to version 3.0.3 following dontwarn added to proguard
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-dontwarn com.mikepenz.fastadapter_extensions.**

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
###

###osmdroid
-dontwarn org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck
###

###Parceler library
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.Parceler$$Parcels
-keepclassmembers class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
###

-keep class org.apache.http.**
-keep class android.net.http.**
-dontwarn com.google.android.gms.**

# Realm library
-keepnames public class * extends io.realm.RealmObject
-keep class io.realm.** { *; }
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

-dontwarn javax.servlet.**
-dontwarn org.joda.time.**
-dontwarn org.w3c.dom.**

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontwarn android.support.**
-verbose

-dontwarn java.nio.file.*
-dontwarn com.squareup.javapoet.*

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepclassmembers class android.support.v4.widget.ViewDragHelper {
    private <fields>;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

### Wallet

-keepclassmembers class * {
    native <methods>;
}

-dontwarn okhttp3.**
-dontwarn com.android.volley.toolbox.**

-keepattributes Annotation,SourceFile,LineNumberTable

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.squareup.okhttp.**
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
### -keepattributes Signature // used this line before
-keepattributes Exceptions
-keepattributes EnclosingMethod

-dontwarn com.alexvasilkov.gestures**

-ignorewarnings

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keepclassmembers enum * { *; }
-keep class **.R$* { *; }

#Metrix
-keep class ir.metrix.sdk.** { *; }

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>


#Gson
# Gson specific classes
-keep class com.google.gson.stream.** { *; }
-keep class sun.misc.Unsafe { *; }
-keepclassmembers,allowobfuscation class * {
                                    @com.google.gson.annotations.SerializedName <fields>;
                                  }


#gms
-keep class com.google.android.gms.** { *; }

-dontwarn android.content.pm.PackageInfo
-keep public class com.android.installreferrer.** { *; }

# prevent proguard from destroying bouncy castle for kuknos
-dontwarn org.bouncycastle.**
-keep class org.bouncycastle.**
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


-keep class com.google.obf.** { *; }
-keep interface com.google.obf.** { *; }

-keep class com.google.ads.interactivemedia.** { *; }
-keep interface com.google.ads.interactivemedia.** { *; }


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------


##---------------Begin: proguard configuration for Retrofit  ----------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions
##---------------End: proguard configuration for Retrofit  ----------


##---------------Begin: proguard configuration for okhttp3  ----------
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
##---------------End: proguard configuration for okhttp3  ----------


##---------------Begin: proguard configuration for okio  ----------
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
##---------------End: proguard configuration for okio  ----------



##---------------Begin: proguard configuration for admob  ----------
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# The following rules are used to strip any non essential Google Play Services classes and method.

# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

# For old ads classes
-keep public class com.google.ads.**{
   public *;
}

# For mediation
-keepattributes *Annotation*

# Other required classes for Google Play Services
# Read more at http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}
##---------------End: proguard configuration for admob  ----------


##---------------Begin: proguard configuration for chartboost  ----------
-keep class com.chartboost.** { *; }
##---------------End: proguard configuration for chartboost  ----------


##---------------Begin: proguard configuration for tapsell  ----------
-keepclassmembers enum * { *; }
-keep class **.R$* { *; }
-keep interface ir.tapsell.sdk.NoProguard
-keep interface ir.tapsell.sdk.NoNameProguard
-keep class * implements ir.tapsell.sdk.NoProguard { *; }
-keep interface * extends ir.tapsell.sdk.NoProguard { *; }
-keep enum * implements ir.tapsell.sdk.NoProguard { *; }
-keepnames class * implements ir.tapsell.sdk.NoNameProguard { *; }
-keepnames class * extends android.app.Activity
-keep class ir.tapsell.plus.model.** { *; }
-keep class ir.tapsell.sdk.models.** { *; }

-keep class ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader$Builder {*;}
-keep class ir.tapsell.sdk.nativeads.TapsellNativeBannerAdLoader$Builder {*;}

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keep interface ir.tapsell.plus.NoProguard
-keep interface * extends ir.tapsell.plus.NoProguard { *; }
-keep class * implements ir.tapsell.plus.NoProguard { *; }

##---------------End: proguard configuration for tapsell  ----------

##---------------Begin: proguard configuration for AppLovin  ----------

-dontwarn com.applovin.**
-keep class com.applovin.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }

##---------------End: proguard configuration for AppLovin  ----------

-keep public class com.bumptech.glide.**

# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# TapsellPlus needs their formal names
-keepnames public class com.google.android.gms.ads.MobileAds
-keepnames public class com.unity3d.services.monetization.IUnityMonetizationListener
-keepnames public class com.adcolony.sdk.AdColony
-keepnames public class com.google.android.gms.ads.identifier.AdvertisingIdClient
-keepnames public class com.chartboost.sdk.Chartboost
-keepnames public class com.applovin.sdk.AppLovinSdkSettings