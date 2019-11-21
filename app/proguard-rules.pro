-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
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

-keep class androidx.arch.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class com.mojang.** { *; }
-keep class org.fmod.** { *; }
-keep class Microsoft.** { *; }
-keep class net.hockeyapp.** { *; }
-keep class MS.** { *; }
-keep class com.microsoft.** { *; }
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
