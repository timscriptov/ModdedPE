package com.mcal.moddedpe.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mcal.moddedpe.LoadingActivity
import com.mcal.moddedpe.R
import com.mcal.moddedpe.data.DownloadItem
import com.mcal.moddedpe.data.ResourceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.buffer
import okio.sink
import java.util.concurrent.TimeUnit

class DownloaderAdapter(
    private val activity: LoadingActivity,
    private val tools: MutableList<DownloadItem>?,
) :
    RecyclerView.Adapter<DownloaderAdapter.AppListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppListViewHolder {
        return AppListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_resource_download, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        tools?.get(position)?.let { item ->
            holder.progressTitleView.text = item.title
            download(holder, item)
        }
    }

    override fun getItemCount(): Int {
        return tools?.size ?: 0
    }

    class AppListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressTitleView: TextView = itemView.findViewById(R.id.progress_title)
        val progressBarView: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val progressLengthView: TextView = itemView.findViewById(R.id.progress_length)
    }

    private fun download(holder: AppListViewHolder, item: DownloadItem) =
        CoroutineScope(Dispatchers.Main).launch {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url(item.url)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.let { body ->
                        val contentLength = body.contentLength().toInt()
                        body.source().use { sourceBytes ->
                            val sink = item.file.sink().buffer()
                            var totalRead = 0L
                            var lastRead: Long
                            while (sourceBytes.read(sink.buffer, 8L * 1024)
                                    .also { lastRead = it } != -1L
                            ) {
                                totalRead += lastRead
                                sink.emitCompleteSegments()
                                withContext(Dispatchers.Main) {
                                    updateProgress(holder, contentLength, totalRead)
                                }
                            }
                            sink.writeAll(sourceBytes)
                            sink.close()
                            finishDownloading(holder, item)
                        }
                    }
                } else {
                    val resourcePackFile = item.file
                    if (resourcePackFile.exists()) {
                        resourcePackFile.delete()
                    }
                    withContext(Dispatchers.Main) {
                        showDialog()
                    }
                }
            }
        }

    private fun finishDownloading(holder: AppListViewHolder, item: DownloadItem) {
        when (item.type) {
            ResourceType.RESOURCE -> {
                activity.installedResourcePack = true
            }

            ResourceType.BEHAVIOR -> {
                activity.installedBehaviorPack = true
            }

            ResourceType.MAIN -> {
                activity.installedMainPack = true
            }

            ResourceType.LIBS -> {
                activity.installedNative = true
            }

            ResourceType.VANILLA -> {
                activity.installedVanillaResourcePack = true
            }
        }
        holder.progressLengthView.text = activity.getString(R.string.done)
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(
        holder: AppListViewHolder,
        contentLength: Int,
        totalRead: Long,
    ) {
        holder.progressBarView.visibility = View.VISIBLE
        if (contentLength != -1) {
            holder.progressBarView.progress = (totalRead * 100 / contentLength).toInt()
        } else {
            holder.progressBarView.isIndeterminate = true
        }
        val total = totalRead / (1024 * 1024)
        val length = contentLength / (1024 * 1024)
        holder.progressLengthView.text = "$total / $length MB"
    }

    private fun showDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            MaterialAlertDialogBuilder(activity).apply {
                setMessage(R.string.download_resource_pack_failed)
                setPositiveButton(R.string.download_repeat) { dialog, _ ->
                    dialog.dismiss()
                    activity.recreate()
                }
                setNegativeButton(R.string.exit) { dialog, _ ->
                    dialog.dismiss()
                    activity.finish()
                }
            }.show()
        }
    }
}
