-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute
-repackageclasses

-ignorewarnings
-dontwarn
-dontnote

-dontobfuscate

#-obfuscationdictionary proguard-dictionary.txt
#-packageobfuscationdictionary proguard-dictionary.txt
#-classobfuscationdictionary proguard-dictionary.txt

-keep class com.mcal.** { *; }

-keep class org.slf4j.LoggerFactory

-keep class com.xbox.httpclient.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class org.spongycastle.** { *; }
-keep class com.mojang.** { *; }
-keep class org.fmod.** { *; }
-keep class com.microsoft.** { *; }
-keep class com.microsoft.xal.androidjava.DeviceInfo { *; }
-keep class com.microsoft.xal.browser.WebView { *; }
-keep class com.microsoft.xal.crypto.EccPubKey { *; }
-keep class com.microsoft.xal.crypto.Ecdsa { *; }
-keep class com.microsoft.xal.crypto.SecureRandom { *; }
-keep class com.microsoft.xal.crypto.ShaHasher { *; }
-keep class com.google.** { *; }
-keep class com.googleplay.** { *; }
-keep class com.facebook.** { *; }
-keep class com.appboy.** { *; }
-keep class com.appsflyer.** { *; }
-keep class com.amazon.** { *; }
-keep class com.android.** { *; }
-keep class bo.** { *; }
-keep class bolts.** { *; }
-keep class org.apache.** { *; }
-keep class com.simpleframework.** { *; }
-keep class android.net.** { *; }
