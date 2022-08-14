-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute
-repackageclasses

-ignorewarnings
-dontwarn
-dontnote

-dontobfuscate
-dontshrink # TODO: Need Update Rules for 1.19

#-obfuscationdictionary proguard-dictionary.txt
#-packageobfuscationdictionary proguard-dictionary.txt
#-classobfuscationdictionary proguard-dictionary.txt

-keep class org.slf4j.LoggerFactory

-keep class com.microsoft.xal.androidjava.DeviceInfo { *; }
-keep class com.microsoft.xal.androidjava.PresenceManager { *; }
-keep class com.microsoft.xal.browser.BrowserLaunchActivity { *; }
-keep class com.microsoft.xal.crypto.EccPubKey { *; }
-keep class com.microsoft.xal.crypto.Ecdsa { *; }
-keep class com.microsoft.xal.crypto.SecureRandom { *; }
-keep class com.microsoft.xal.crypto.ShaHasher { *; }

-keep class com.microsoft.xbox.idp.interop.Interop { *; }
-keep class com.microsoft.xbox.idp.util.HttpCall { *; }
-keep class com.microsoft.xbox.idp.interop.XboxLiveAppConfig { *; }
-keep class com.microsoft.xboxtcui.Interop { *; }
-keep class com.microsoft.xboxlive.LocalStorage { *; }

-keep class com.xbox.httpclient.HttpClientRequest { *; }
-keep class com.xbox.httpclient.HttpClientResponse { *; }

-keep class org.fmod.AudioDevice { *; }
-keep class org.fmod.MediaCodec { *; }
-keep class org.fmod.FMOD { *; }

-keep class com.mojang.minecraftpe.ActiveDirectorySignIn { *; }
-keep class com.mojang.minecraftpe.BatteryMonitor { *; }
-keep class com.mojang.minecraftpe.HardwareInformation { *; }
-keep class com.mojang.minecraftpe.MainActivity { *; }
-keep class com.mojang.minecraftpe.ThermalMonitor { *; }
-keep class com.mojang.minecraftpe.store.NativeStoreListener { *; }
-keep class com.mojang.minecraftpe.store.Product { *; }
-keep class com.mojang.minecraftpe.store.Purchase { *; }
-keep class com.mojang.minecraftpe.store.StoreFactory { *; }
-keep class com.mojang.minecraftpe.store.Store { *; }
-keep class com.mojang.minecraftpe.store.ExtraLicenseResponseData { *; }
-keep class com.mojang.minecraftpe.packagesource.PackageSourceFactory { *; }
-keep class com.mojang.minecraftpe.packagesource.PackageSource { *; }
-keep class com.mojang.minecraftpe.packagesource.NativePackageSourceListener { *; }
-keep class com.mojang.minecraftpe.Webview.MinecraftChromeClient { *; }
-keep class com.mojang.minecraftpe.Webview.MinecraftWebview { *; }
-keep class com.mojang.minecraftpe.Webview.MinecraftWebViewClient { *; }
-keep class com.mojang.minecraftpe.Webview.WebviewHostInterface { *; }

-keep class com.mcal.pesdk.nativeapi.NativeUtils { *; }

-keep class org.spongycastle.**