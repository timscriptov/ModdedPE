/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.pesdk.nmod

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*
import java.util.*

abstract class NMod protected constructor(val packageName: String, protected var mContext: Context) {
    var info: NMod.NModInfo? = null
        protected set
    var loadException: LoadFailedException? = null
        private set
    private val mWarnings = ArrayList<NModWarning>()
    var icon: Bitmap? = null
        private set
    var bannerImage: Bitmap? = null
        private set

    abstract val assets: AssetManager?

    abstract val packageResourcePath: String

    abstract val nModType: Int

    abstract val isSupportedABI: Boolean

    val name: String?
        get() {
            if (isBugPack)
                return packageName
            return if (info == null || info!!.name == null) packageName else info!!.name
        }

    val bannerTitle: String?
        get() = if (info != null && info!!.banner_title != null) name + " : " + info!!.banner_title else name

    val isValidBanner: Boolean
        get() = bannerImage != null

    val description: String?
        get() = if (info != null && info!!.description != null) info!!.description else mContext.resources.getString(android.R.string.unknownName)

    val author: String?
        get() = if (info != null && info!!.author != null) info!!.author else mContext.resources.getString(android.R.string.unknownName)

    val versionName: String?
        get() = if (info != null && info!!.version_name != null) info!!.version_name else mContext.resources.getString(android.R.string.unknownName)

    val isBugPack: Boolean
        get() = loadException != null

    val warnings: ArrayList<NModWarning>
        get() {
            val newArray = ArrayList<NModWarning>()
            newArray.addAll(mWarnings)
            return newArray
        }

    val changeLog: String?
        get() = info!!.change_log

    val minecraftVersionName: String?
        get() = if (info != null && info!!.minecraft_version_name != null) info!!.minecraft_version_name else mContext.resources.getString(android.R.string.unknownName)

    @Throws(IOException::class)
    abstract fun copyNModFiles(): NModPreloadBean

    protected abstract fun createIcon(): Bitmap?

    protected abstract fun createInfoInputStream(): InputStream?

    override fun equals(obj: Any?): Boolean {
        return obj is NMod && packageName == obj.packageName
    }

    @Throws(LoadFailedException::class)
    fun createBannerImage(): Bitmap? {
        var ret: Bitmap? = null
        try {
            if (info == null || info!!.banner_image_path == null)
                return null
            val `is` = assets?.open(info!!.banner_image_path!!)
            ret = BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
            throw LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e)
        }

        if (ret == null)
            throw LoadFailedException(LoadFailedException.TYPE_DECODE_FAILED, RuntimeException("Cannot decode banner image file."))

        if (ret.width != 1024 || ret.height != 500)
            throw LoadFailedException(LoadFailedException.TYPE_INVALID_SIZE, RuntimeException("Invalid image size for banner: width must be 1024,height must be 500."))
        return ret
    }

    fun setBugPack(e: LoadFailedException) {
        loadException = e
    }

    fun copyIconToData(): File? {
        val icon = this.icon ?: return null
        NModFilePathManager(mContext).nModIconDir.mkdirs()
        val file = NModFilePathManager(mContext).getNModIconPath(this)
        try {
            val baos = ByteArrayOutputStream()
            icon.compress(Bitmap.CompressFormat.PNG, 100, baos)
            file.createNewFile()
            val outfile = FileOutputStream(file)
            outfile.write(baos.toByteArray())
            outfile.close()
            return file
        } catch (ioe: IOException) {
            return null
        }

    }

    fun addWarning(warning: NModWarning) {
        mWarnings.add(warning)
    }

    protected fun checkWarnings() {

    }

    internal fun preload() {
        this.loadException = null

        this.icon = createIcon()

        try {
            val input = createInfoInputStream()
            var tmp = ""
            input!!.bufferedReader().use {
                tmp = it.readText()
            }
            val gson = Gson()
            info = gson.fromJson(tmp, NModInfo::class.java)
        } catch (e: JsonSyntaxException) {
            info = null
            setBugPack(LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, e))
            return
        } catch (ioe: IOException) {
            info = null
            setBugPack(LoadFailedException(LoadFailedException.TYPE_IO_FAILED, ioe))
            return
        }

        if (info == null) {
            setBugPack(LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, JsonSyntaxException("NMod Info returns null.Please check if there is json syntax mistakes in nmod_manifest.json")))
            return
        }

        try {
            this.bannerImage = createBannerImage()
        } catch (nmodle: LoadFailedException) {
            info = null
            setBugPack(nmodle)
        }

    }

    class NModPreloadBean {
        var native_libs: Array<NModLibInfo>? = null
        var assets_path: String? = null
    }

    class NModTextEditBean {
        var path: String? = null
        var mode = MODE_REPLACE

        companion object {
            val MODE_REPLACE = "replace"
            val MODE_APPEND = "append"
            val MODE_PREPEND = "prepend"
        }
    }

    class NModJsonEditBean {
        var path: String? = null
        var mode = MODE_REPLACE

        companion object {
            val MODE_REPLACE = "replace"
            val MODE_MERGE = "merge"
        }
    }

    class NModLibInfo {
        var use_api = false
        var name: String? = null
    }

    class NModInfo {
        var native_libs_info: Array<NModLibInfo>? = null
        var text_edit: Array<NModTextEditBean>? = null
        var json_edit: Array<NModJsonEditBean>? = null
        var version_code = -1
        var name: String? = null
        var package_name: String? = null
        var description: String? = null
        var author: String? = null
        var version_name: String? = null
        var banner_title: String? = null
        var banner_image_path: String? = null
        var change_log: String? = null
        var minecraft_version_name: String? = null
    }

    companion object {
        val MANIFEST_NAME = "nmod_manifest.json"
        val NMOD_TYPE_ZIPPED = 1
        val NMOD_TYPE_PACKAGED = 2
    }
}
