package com.mcal.moddedpe.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

open class BaseActivity : AppCompatActivity() {

    private var preferences: SharedPreferences? = null

    var installedResourcePack: Boolean
        get() = preferences?.getBoolean(
            "resource_pack_installed", false
        ) ?: false
        set(flag) {
            preferences?.edit()?.putBoolean(
                "resource_pack_installed", flag
            )?.apply()
        }
    var installedBehaviorPack: Boolean
        get() = preferences?.getBoolean(
            "behavior_pack_installed", false
        ) ?: false
        set(flag) {
            preferences?.edit()?.putBoolean(
                "behavior_pack_installed", flag
            )?.apply()
        }
    var installedMainPack: Boolean
        get() = preferences?.getBoolean(
            "main_pack_installed", false
        ) ?: false
        set(flag) {
            preferences?.edit()?.putBoolean(
                "main_pack_installed", flag
            )?.apply()
        }
    var installedNative: Boolean
        get() = preferences?.getBoolean(
            "native_installed", false
        ) ?: false
        set(flag) {
            preferences?.edit()?.putBoolean(
                "native_installed", flag
            )?.apply()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
    }
}
