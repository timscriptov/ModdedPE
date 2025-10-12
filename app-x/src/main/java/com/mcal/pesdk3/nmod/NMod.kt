/*
 * Copyright (C) 2018-2025 Тимашков Иван
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
package com.mcal.pesdk3.nmod

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mcal.pesdk3.data.LoadFailedException
import com.mcal.pesdk3.data.NModInfo
import com.mcal.pesdk3.data.NModPreloadBean
import com.mcal.pesdk3.data.NModWarning
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.*

abstract class NMod {
    companion object {
        const val MANIFEST_NAME = "nmod_manifest.json"
        const val NMOD_TYPE_ZIPPED = 1
        const val NMOD_TYPE_PACKAGED = 2

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    private val mWarnings = ArrayList<NModWarning>()
    private val mPackageName: String
    protected var mContext: Context
    protected var mInfo: NModInfo? = null
    private var mBugException: LoadFailedException? = null
    private var mIcon: Bitmap? = null
    private var mBannerImage: Bitmap? = null

    protected constructor(packageName: String, context: Context) {
        mContext = context
        mPackageName = packageName
    }

    @Throws(IOException::class)
    abstract fun copyNModFiles(): NModPreloadBean

    abstract fun getAssets(): AssetManager

    abstract fun getPackageResourcePath(): String

    abstract fun getNModType(): Int

    abstract fun isSupportedABI(): Boolean

    protected abstract fun createIcon(): Bitmap?

    protected abstract fun createInfoInputStream(): InputStream?

    fun preload() {
        mBugException = null
        mIcon = createIcon()

        try {
            createInfoInputStream()?.let { input ->
                val buffer = ByteArray(1024)
                val tmp = StringBuilder()
                var byteRead: Int
                while (input.read(buffer).also { byteRead = it } > 0) {
                    tmp.append(String(buffer, 0, byteRead))
                }
                mInfo = json.decodeFromString(tmp.toString())
            }
        } catch (e: SerializationException) {
            mInfo = null
            setBugPack(LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, e))
            return
        } catch (e: IllegalArgumentException) {
            mInfo = null
            setBugPack(LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, e))
            return
        } catch (ioe: IOException) {
            mInfo = null
            setBugPack(LoadFailedException(LoadFailedException.TYPE_IO_FAILED, ioe))
            return
        }

        if (mInfo == null) {
            setBugPack(
                LoadFailedException(
                    LoadFailedException.TYPE_JSON_SYNTAX,
                    SerializationException("NMod Info returns null. Please check if there is json syntax mistakes in nmod_manifest.json")
                )
            )
            return
        }

        try {
            mBannerImage = createBannerImage()
        } catch (nmodle: LoadFailedException) {
            mInfo = null
            setBugPack(nmodle)
        }
    }

    fun getPackageName(): String {
        return mPackageName
    }

    fun getName(): String {
        if (isBugPack()) {
            return getPackageName()
        }
        if (mInfo == null || mInfo!!.name == null) {
            return getPackageName()
        }
        return mInfo!!.name!!
    }

    @Throws(LoadFailedException::class)
    fun createBannerImage(): Bitmap? {
        val ret: Bitmap
        try {
            mInfo?.bannerImagePath?.let { path ->
                val inputStream = getAssets().open(path)
                ret = BitmapFactory.decodeStream(inputStream) ?: return null
            } ?: run {
                return null
            }
        } catch (e: IOException) {
            throw LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e)
        }

        if (ret.width != 1024 || ret.height != 500) {
            throw LoadFailedException(
                LoadFailedException.TYPE_INVALID_SIZE,
                RuntimeException("Invalid image size for banner: width must be 1024, height must be 500.")
            )
        }
        return ret
    }

    fun getBannerImage(): Bitmap? {
        return mBannerImage
    }

    fun getBannerTitle(): String {
        mInfo?.bannerTitle?.let { title ->
            return getName() + " : " + title
        }
        return getName()
    }

    fun isValidBanner(): Boolean {
        return mBannerImage != null
    }

    fun getIcon(): Bitmap? {
        return mIcon
    }

    fun getDescription(): String {
        return mInfo?.description ?: "Unknown"
    }

    fun getAuthor(): String {
        return mInfo?.author ?: "Unknown"
    }

    fun getVersionName(): String {
        return mInfo?.versionName ?: "Unknown"
    }

    fun getVersionCode(): Int {
        return mInfo?.versionCode?.takeIf { it > 1 } ?: -1
    }

    fun isBugPack(): Boolean {
        return mBugException != null
    }

    fun setBugPack(e: LoadFailedException?) {
        mBugException = e
    }

    fun copyIconToData(): File? {
        val icon = getIcon() ?: return null
        NModFilePathManager(mContext).getNModIconDir().mkdirs()
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

    fun copyBannerToData(): File? {
        val icon = getIcon() ?: return null
        NModFilePathManager(mContext).getNModIconDir().mkdirs()
        val file = NModFilePathManager(mContext).getNModBannerIconPath(this)
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

    fun getLoadException(): LoadFailedException? {
        return mBugException
    }

    fun addWarning(warning: NModWarning) {
        mWarnings.add(warning)
    }

    fun getWarnings(): ArrayList<NModWarning> {
        return ArrayList(mWarnings)
    }

    protected open fun checkWarnings() {

    }

    fun getChangeLog(): String? {
        return mInfo?.changeLog
    }

    fun getInfo(): NModInfo? {
        return mInfo
    }

    override fun equals(other: Any?): Boolean {
        return other is NMod && getPackageName() == other.getPackageName()
    }

    override fun hashCode(): Int {
        var result = mWarnings.hashCode()
        result = 31 * result + mPackageName.hashCode()
        result = 31 * result + mContext.hashCode()
        result = 31 * result + (mInfo?.hashCode() ?: 0)
        result = 31 * result + (mBugException?.hashCode() ?: 0)
        result = 31 * result + (mIcon?.hashCode() ?: 0)
        result = 31 * result + (mBannerImage?.hashCode() ?: 0)
        return result
    }
}
