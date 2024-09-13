package com.mcal.moddedpe

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

open class PmsHookApplication : Application(), InvocationHandler {
    private val GET_SIGNATURES = 0x00000040

    private var mBase: Any? = null
    private var mSign = arrayOf<ByteArray>()
    private var mPackageName: String = ""

    override fun attachBaseContext(base: Context) {
        hook(base)
        super.attachBaseContext(base)
    }

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
        val safeArgs = args ?: arrayOf()

        if (method.name == "getPackageInfo") {
            val pkgName = safeArgs[0] as String
            val flag = (safeArgs[1] as Number).toInt()
            if (flag and GET_SIGNATURES != 0 && mPackageName == pkgName) {
                val info = method.invoke(mBase, *safeArgs) as PackageInfo
                info.signatures = Array(mSign.size) { i -> Signature(mSign[i]) }
                return info
            }
        }
        return method.invoke(mBase, *safeArgs)
    }

    private fun hook(context: Context) {
        try {
            val data = "AQAAA3AwggNsMIICVKADAgECAgROPuATMA0GCSqGSIb3DQEBBQUAMHgxCzAJBgNVBAYTAlNFMRkw\n" +
                    "FwYDVQQIDBBTdG9ja2hvbG1zIEzigJ5uMRIwEAYDVQQHEwlTdG9ja2hvbG0xEjAQBgNVBAoTCU1v\n" +
                    "amFuZyBBQjESMBAGA1UECxMJTW9qYW5nIEFCMRIwEAYDVQQDEwlNb2phbmcgQUIwHhcNMTEwODA3\n" +
                    "MTg1NzIzWhcNMzgxMjIzMTg1NzIzWjB4MQswCQYDVQQGEwJTRTEZMBcGA1UECAwQU3RvY2tob2xt\n" +
                    "cyBM4oCebjESMBAGA1UEBxMJU3RvY2tob2xtMRIwEAYDVQQKEwlNb2phbmcgQUIxEjAQBgNVBAsT\n" +
                    "CU1vamFuZyBBQjESMBAGA1UEAxMJTW9qYW5nIEFCMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" +
                    "CgKCAQEAhD5zm6F0AVIE/N3rl8MQ1v/HAbQI59Oy/EME8RQp99hORYmnWPx2HufFQ4K4sZQYrqGq\n" +
                    "5pxFXvxGqpWnKLUvCtxQbTFbs3KHaoMuDYtLSjfy1XtAPB6O4nshh1OsNoTEy3rlU7TNsMFHK2Nu\n" +
                    "ZZuZKqIWvEEIcif8A83n+i20yHcU4D7ZN1/610Uu92ccT0Ls3K4SH8oDb7LKMd6O/ahllbngdNx0\n" +
                    "bKv0wLQfj4vyKhzX7HACDKNuigEOeuoOFa2o33YbxqHCbnSp+kXxdgwlynR0uz/udFgm5BgCtufp\n" +
                    "iRrPJShz7JtYIy0rk3CzWy7s4oGZZlxQVtPJx7zwDnZg9wIDAQABMA0GCSqGSIb3DQEBBQUAA4IB\n" +
                    "AQAbeYC/oCRcgawXwI3hV9Cr0EcIPDOX4t9+9f320t2U1LZEJuf+o3O7hwehRx4jk0FOUPTjPlld\n" +
                    "TSZ0Ysm2KW6FFSQw14O98ksP64m0xpvRuBF6hjCXBk+OdqUM547WkdRRqNiyh81vwXgmleUyNuPg\n" +
                    "UyRcylMLhBmWgO0AG2PD2RJg010ZIvJQ8h3mk9VEukO+hWOIbvR62b6wcPRcmsRxe+AGF6dzQajz\n" +
                    "MBtT+F1Nbep4sThFmvMipQWTuUx30ElO0ejRPmtumztSDPgXi9TEbbv6XQwe8hJJWg1+u7XEF/Jw\n" +
                    "Sfn2NW4NLAV5a5jWr9N/qJl/2sanTavjo4fddiXG\n"
            val inputStream = DataInputStream(ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT)))
            mSign = Array(inputStream.read() and 0xFF) {
                ByteArray(inputStream.readInt()).also { inputStream.readFully(it) }
            }

            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
            val currentActivityThread = currentActivityThreadMethod.invoke(null)

            val sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager").apply {
                isAccessible = true
            }

            val iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager")
            mBase = sPackageManagerField.get(currentActivityThread)
            mPackageName = context.packageName

            val proxy = Proxy.newProxyInstance(
                iPackageManagerInterface.classLoader,
                arrayOf(iPackageManagerInterface),
                this
            )

            sPackageManagerField.set(currentActivityThread, proxy)

            context.packageManager.javaClass.getDeclaredField("mPM").apply {
                isAccessible = true
                set(this, proxy)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}