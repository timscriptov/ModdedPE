-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute

-dontoptimize
-dontshrink

#-obfuscationdictionary proguard-dictionary.txt
#-packageobfuscationdictionary proguard-dictionary.txt
#-classobfuscationdictionary proguard-dictionary.txt

#-keep class org.apache.commons.logging.impl.Log4JLogger { *; }
#-keep class com.appboy.ui.contentcards.view.ContentCardViewHolder { *; }
#-keep class org.slf4j.LoggerFactory { *; }
#
-keep class com.microsoft.xal.androidjava.Storage { *; }
-keep class com.microsoft.xal.androidjava.DeviceInfo { *; }
-keep class com.microsoft.xal.androidjava.PresenceManager { *; }
-keep class com.microsoft.xal.browser.BrowserLaunchActivity { *; }
-keep class com.microsoft.xal.crypto.EccPubKey { *; }
-keep class com.microsoft.xal.crypto.Ecdsa { *; }
-keep class com.microsoft.xal.crypto.SecureRandom { *; }
-keep class com.microsoft.xal.crypto.ShaHasher { *; }
-keep class com.microsoft.xal.logging.XalLogger { *; }
-keep class com.microsoft.xal.logging.LogEntry { *; }

-keep class com.microsoft.xbox.idp.util.HttpCall { *; }
-keep class com.microsoft.xbox.idp.util.HttpHeaders { *; }
-keep class com.microsoft.xbox.idp.interop.Interop { *; }
-keep class com.microsoft.xbox.idp.interop.XboxLiveAppConfig { *; }

-keep class com.microsoft.xbox.telemetry.helpers.UTCTelemetry { *; }

-keep class com.microsoft.xboxtcui.Interop { *; }

-keep class com.microsoft.xboxlive.LocalStorage { *; }

-keep class com.xbox.httpclient.HttpClientRequestBody { *; }
-keep class com.xbox.httpclient.HttpClientResponse { *; }
-keep class com.xbox.httpclient.HttpClientWebSocket { *; }
-keep class com.xbox.httpclient.HttpClientRequest { *; }

-keep class org.fmod.AudioDevice { *; }
-keep class org.fmod.MediaCodec { *; }
-keep class org.fmod.FMOD { *; }

-keep class com.mojang.minecraftpe.CrashManager { *; }
-keep class com.mojang.minecraftpe.BatteryMonitor { *; }
-keep class com.mojang.minecraftpe.HardwareInformation { *; }
-keep class com.mojang.minecraftpe.MainActivity { *; }
-keep class com.mojang.minecraftpe.ThermalMonitor { *; }
-keep class com.mojang.minecraftpe.store.NativeStoreListener { *; }
-keep class com.mojang.minecraftpe.store.Product { *; }
-keep class com.mojang.minecraftpe.store.Purchase { *; }
-keep class com.mojang.minecraftpe.store.StoreFactory { *; }
-keep class com.mojang.minecraftpe.store.Store { *; }
-keep class com.mojang.minecraftpe.store.StoreListener { *; }
-keep class com.mojang.minecraftpe.store.ExtraLicenseResponseData { *; }
-keep class com.mojang.minecraftpe.packagesource.PackageSourceListener { *; }
-keep class com.mojang.minecraftpe.packagesource.PackageSourceFactory { *; }
-keep class com.mojang.minecraftpe.packagesource.PackageSource { *; }
-keep class com.mojang.minecraftpe.packagesource.NativePackageSourceListener { *; }
-keep class com.mojang.minecraftpe.Webview.MinecraftChromeClient { *; }
-keep class com.mojang.minecraftpe.Webview.MinecraftWebview { *; }
-keep class com.mojang.minecraftpe.Webview.MinecraftWebViewClient { *; }
-keep class com.mojang.minecraftpe.Webview.WebviewHostInterface { *; }
-keep class com.mojang.minecraftpe.input.JellyBeanDeviceManager { *; }
-keep class com.mojang.minecraftpe.WorldRecovery { *; }
-keep class com.mojang.minecraftpe.NotificationListenerService { *; }
-keep class com.mojang.minecraftpe.MinecraftActivityLifecycleCallbackListener { *; }
-keep class com.mojang.minecraftpe.FilePickerManager { *; }

-keep class org.spongycastle.**

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn javax.naming.InvalidNameException
-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult
-dontwarn javax.naming.ldap.LdapName
-dontwarn javax.naming.ldap.Rdn
-dontwarn org.ietf.jgss.GSSContext
-dontwarn org.ietf.jgss.GSSCredential
-dontwarn org.ietf.jgss.GSSException
-dontwarn org.ietf.jgss.GSSManager
-dontwarn org.ietf.jgss.GSSName
-dontwarn org.ietf.jgss.Oid
-dontwarn javax.naming.Binding
-dontwarn javax.servlet.ServletContextEvent
-dontwarn javax.servlet.ServletContextListener
-dontwarn org.apache.avalon.framework.logger.Logger
-dontwarn org.apache.log.Hierarchy
-dontwarn org.apache.log.Logger
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority