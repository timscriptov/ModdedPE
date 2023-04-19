package com.mcal.core

import android.content.Context
import android.content.res.AssetManager
import com.mcal.core.data.StorageHelper.behaviorPackFile
import com.mcal.core.data.StorageHelper.mainPackFile
import com.mcal.core.data.StorageHelper.resourcePackFile
import java.io.IOException
import java.lang.reflect.InvocationTargetException

class AssetInstaller(private val context: Context) {
    fun install() {
        try {
            val patchAssetPath = ArrayList<String>()
            patchAssetPath.add(context.packageResourcePath)
            patchAssetPath.add(resourcePackFile(context).path)
            patchAssetPath.add(mainPackFile(context).path)
            patchAssetPath.add(behaviorPackFile(context).path)
            for (packageResourcePath in patchAssetPath) {
                val addAssetPath =
                    AssetManager::class.java.getMethod("addAssetPath", String::class.java)
                addAssetPath.isAccessible = true
                addAssetPath.invoke(context.assets, packageResourcePath)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }
}