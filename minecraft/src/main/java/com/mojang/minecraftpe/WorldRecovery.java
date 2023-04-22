package com.mojang.minecraftpe;

import static com.mcal.core.utils.FileHelper.writeToFile;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.mcal.core.utils.FileHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class WorldRecovery {
    private ContentResolver mContentResolver;
    private Context mContext;
    private int mTotalFilesToCopy = 0;
    private long mTotalBytesRequired = 0;

    private static native void nativeComplete();

    private static native void nativeError(String error, long bytesRequired, long bytesAvailable);

    private static native void nativeUpdate(String status, int filesTotal, int filesCompleted, long bytesTotal, long bytesCompleted);

    public WorldRecovery(Context context, ContentResolver contentResolver) {
        this.mContext = null;
        this.mContentResolver = null;
        this.mContext = context;
        this.mContentResolver = contentResolver;
    }

    public String migrateFolderContents(String srcURIString, String destFolderString) {
        final DocumentFile fromTreeUri = DocumentFile.fromTreeUri(this.mContext, Uri.parse(srcURIString));
        if (fromTreeUri == null) {
            return "Could not resolve URI to a DocumentFile tree: " + srcURIString;
        } else if (!fromTreeUri.isDirectory()) {
            return "Root file of URI is not a directory: " + srcURIString;
        } else {
            final File file = new File(destFolderString);
            if (!file.isDirectory()) {
                return "Destination folder does not exist: " + destFolderString;
            }
            String[] list = file.list();
            Objects.requireNonNull(list);
            if (list.length != 0) {
                return "Destination folder is not empty: " + destFolderString;
            }
            new Thread(() -> doMigrateFolderContents(fromTreeUri, file)).start();
            return "";
        }
    }

    public void doMigrateFolderContents(DocumentFile root, @NonNull File destFolder) {
        ArrayList<DocumentFile> arrayList = new ArrayList<>();
        mTotalFilesToCopy = 0;
        long j = 0;
        mTotalBytesRequired = 0L;
        generateCopyFilesRecursively(arrayList, root);
        long availableBytes = new StatFs(destFolder.getAbsolutePath()).getAvailableBytes();
        long j2 = mTotalBytesRequired;
        if (j2 >= availableBytes) {
            nativeError("Insufficient space", j2, availableBytes);
            return;
        }
        String path = root.getUri().getPath();
        String str = destFolder + "_temp";
        File file = new File(str);
        Iterator<DocumentFile> it = arrayList.iterator();
        long j3 = 0;
        int i = 0;
        while (it.hasNext()) {
            DocumentFile next = it.next();
            String str2 = str + next.getUri().getPath().substring(path.length());
            if (next.isDirectory()) {
                File file2 = new File(str2);
                if (!file2.isDirectory()) {
                    Log.i("Minecraft", "Creating directory '" + str2 + "'");
                    if (!file2.mkdirs()) {
                        nativeError("Could not create directory: " + str2, j, j);
                        return;
                    }
                } else {
                    Log.i("Minecraft", "Directory '" + str2 + "' already exists");
                }
            } else {
                Log.i("Minecraft", "Copying '" + next.getUri().getPath() + "' to '" + str2 + "'");
                String sb = "Copying: " + str2;
                i++;
                nativeUpdate(sb, mTotalFilesToCopy, i, mTotalBytesRequired, j3);
                try {
                    writeToFile(new File(str2), mContentResolver.openInputStream(next.getUri()));
                } catch (IOException e) {
                    e.printStackTrace();
                    nativeError(e.getMessage(), 0L, 0L);
                    return;
                }
            }
            j = 0;
        }
        if (destFolder.delete()) {
            if (file.renameTo(destFolder)) {
                nativeComplete();
                return;
            } else if (destFolder.mkdir()) {
                nativeError("Could not replace destination directory: " + destFolder.getAbsolutePath(), 0L, 0L);
                return;
            } else {
                nativeError("Could not recreate destination directory after failed replace: " + destFolder.getAbsolutePath(), 0L, 0L);
                return;
            }
        }
        nativeError("Could not delete empty destination directory: " + destFolder.getAbsolutePath(), 0L, 0L);
    }

    private void generateCopyFilesRecursively(ArrayList<DocumentFile> result, @NonNull DocumentFile root) {
        for (DocumentFile documentFile : root.listFiles()) {
            result.add(documentFile);
            if (documentFile.isDirectory()) {
                generateCopyFilesRecursively(result, documentFile);
            } else {
                this.mTotalBytesRequired += documentFile.length();
                this.mTotalFilesToCopy++;
            }
        }
    }
}
